/**
 * @author Giannis Gkalmpenis
 */
package com.mycompany.streamingserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Server extends Thread {
    public String ffmpegLocation;
    private String folderLocation;
    private ArrayList<Video> videos;
    private static ServerSocket server;
    private static final int port = 5000;
    private static Map<Integer, String> bitrates = Collections.unmodifiableMap(
                        new HashMap<Integer, String>() {{
                            put(200, "0.2Mbps");
                            put(500, "0.5Mbps");
                            put(1000, "1.0Mbps");
                            put(3000, "3.0Mbps");
                        }});    
    
    public void setFFmpegLocation (String location) {
        this.ffmpegLocation = location;
    }
    public void setFolderLocation (String location) {
        this.folderLocation = location;
        
        //Now we can assign objects in the "videos" variable
        this.videos = new ArrayList<Video>();
        this.videos = getAllVideos(this.folderLocation);
    }
    private ArrayList<Video> getAllVideos(String folderLocation) {
        ArrayList<Video> videos = new ArrayList<Video>();
        File folder = new File(folderLocation);
        File[] files = folder.listFiles();
        for (File f : files) {
            Video v = new Video(f.getAbsolutePath());
            videos.add(v);
        }
        return videos;
    }    
    private ArrayList<Video> getSpecificVideos(String bitrate, String format) {
        //Getting a list with all the videos that have the specific bitrate and format
        
        ArrayList<Video> specificVideos = new ArrayList<Video>();
        for (Video v : videos) {
            if (v.bitrate.equals(bitrate) && v.format.equals(format)) {
                specificVideos.add(v);
            }
        }
        return specificVideos;
    }
    private ArrayList<String> getAppropriateVideos(int bandwidth, String format) {
        /* In this method we get videos (only the names)
        *  with the requested format and bitrates less
        *  or equal than the requested.
        */
        ArrayList<String> appropriateVideos = new ArrayList<String>();
        ArrayList<Video> v = new ArrayList<Video>();

        for (int key : bitrates.keySet()) {
            //Iterate through the keys of "bitrates"
            if (key <= bandwidth) {
                v = getSpecificVideos(bitrates.get(key), format);
                for (int i=0; i<v.size(); i++) {
                    appropriateVideos.add(v.get(i).nameWithAttributes);
                }
            }
        }    
       return appropriateVideos;
    }
    private String getFullPathByName(String nameWAttributes) {
        for (Video v : videos) {
            if (v.nameWithAttributes.equals(nameWAttributes)) {
                return v.fullPath;
            }
        }
        return "";
    }
    private String buildCommand(String videoPath, String format, String protocol, String IP, int port) {
        String cmd = "";
        if ( !format.equals("avi")) {
            //Change muxer
            format = "matroska";
        }
        switch (protocol) {
            case "udp":
                cmd = this.ffmpegLocation + " -re -i " +
                        videoPath + " -f " + format + " " +
                        protocol + "://" + IP + ":" + port;
                break;
            case "tcp":
                cmd = this.ffmpegLocation + " -i " + videoPath +
                        " -f " + format + " " + protocol +
                        "://" + IP + ":" + port + "?listen";
                break;
            default:
                break;
        }
        return cmd;
    }
     
    
   public void startServer() {        
        /* We start a new thread otherwise the whole application
        * (including the GUI) will stuck until a client is connected
        */
        thread.setDaemon(true);
        thread.start();
    }
   
   
   Thread thread = new Thread() {
        @Override
        public void run() {
            //Code to be executed when we call the startServer() method
            try {
                /* ---- Initial flow of the application ----
                 * 1. Get bandwidth and format from client
                 * 2. Send the appropriate videos to client
                 * 3. Get desired video and protocol from client
                 * 4. Notify client that server is ready to stream and begin streaming
                 */
                
                
                /* We use Server.this because we want to manipulate the variables
                 * of the current Server class that the Thread object is inside
                 */
                Server.this.server = new ServerSocket(Server.this.port);
                while (true) { //Keep the server open for clients
                    System.out.println("Listening for client requests...");
                    Socket socket = server.accept();
                    System.out.println("Connected with client");
                    int bandwidth = 0;
                    String format = "";
                    String videoName = "";
                    String desiredProtocol = "";
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    
                    
                    String clientOrder = (String) in.readObject();
                    while ( !clientOrder.equals("Finish") ) {
                        switch (clientOrder) {
                            case "rcvBandwidthAndFormat" :
                                bandwidth = (Integer) in.readObject();
                                format = (String) in.readObject();
                                System.out.println("Received bandwidth: " + bandwidth);
                                System.out.println("Received format: " + format);
                                break;
                            case "sendAppropriateTitles" :
                                ArrayList<String> videosToSend = Server.this.getAppropriateVideos(bandwidth, format);
                                int objsToSend = videosToSend.size();
                                System.out.println("Sending " + objsToSend + " video titles to client");
                                out.writeObject(objsToSend);
                                for (int i=0; i<objsToSend; i++) {
                                    System.out.println("Title " + i + ": " + videosToSend.get(i));
                                    out.writeObject(videosToSend.get(i));
                                }
                                break;
                            case "rcvVideoAndProtocol" :
                                videoName = (String) in.readObject();
                                desiredProtocol = (String) in.readObject();
                                System.out.println("Received video name: " + videoName);
                                System.out.println("Received protocol: " + desiredProtocol);
                                break;
                            case "startStream" :
                                String ready = "Start of streaming";
                                out.writeObject(ready);
                                
                                //ffmpeg will try to post output to these endpoints
                                String anIP = "127.0.0.1";
                                int aPort = 1234;

                                //Building command to execute
                                String vidToStream = Server.this.getFullPathByName(videoName);
                                String command = Server.this.buildCommand(vidToStream, format, desiredProtocol, anIP, aPort);
                                
                                //Executing the command
                                System.out.println("---- Starting to stream ----");
                                Process p = Runtime.getRuntime().exec(command);
                                
                                //Show process log in the console
                                InputStream inS = p.getErrorStream();
                                int c;
                                while((c = inS.read()) != -1) {
                                    System.out.print((char)c);
                                }
                                inS.close();
                                break;
                            default:
                                break;
                        }
                        try {
                            //Read next client order
                            clientOrder = (String) in.readObject();
                        } catch (SocketException e) {
                            //We get here if the client stops the application
                            System.out.println("Client disconnected");
                            clientOrder = "Finish";
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    };
}
