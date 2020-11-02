/**
 *
 * @author Giannis Gkalmpenis
 */
package com.mycompany.streamingclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class Client {
    public String ffplayLocation;
    private final String serverIP = "127.0.0.1";
    private final int serverPort = 5000;
    private Socket socket;
    ArrayList<String> videoList;
    ObjectOutputStream out;
    ObjectInputStream in;
    
    public void setFFplayLocation (String location) {
        this.ffplayLocation = location;
    }
    
    public String connectToServer() {
        try {
            this.socket = new Socket(this.serverIP, this.serverPort);
            System.out.println("Connected to Server");
            
            //Opening input and output streams
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            
            return "Successful connection";
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "Unknown Host";
        } catch (IOException e) {
            e.printStackTrace();
            return "I/O Exception occured";
        }
    }
    
    public void sendSpeedAndFormat(String speed, String format) {
        try {
            String commandToServer = "rcvBandwidthAndFormat";
            out.writeObject(commandToServer);
            int bandwidth = Integer.parseInt(speed); //Server needs the user's speed as an integer
            out.writeObject(bandwidth);
            out.writeObject(format.toLowerCase());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void receiveVideos() {
        try {
            String commandToServer = "sendAppropriateTitles";
            out.writeObject(commandToServer);
            
            int objsToReceive = (Integer) in.readObject();  //Server tells us how many objects we are going to receive
            System.out.println("Objects to receive: " + objsToReceive);
            String videoTitle;
            this.videoList = new ArrayList<String>();
            for (int i=0; i<objsToReceive; i++) {
                videoTitle = (String) in.readObject();
                System.out.println("Received video title: " + videoTitle);
                videoList.add(videoTitle);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public ArrayList<String> getVideoList () {
        return this.videoList;
    }
    
    public void sendVideoAndProtocol(String videoChoice, String protocolChoice) {
        try {
            String commandToServer = "rcvVideoAndProtocol";
            out.writeObject(commandToServer);
            
            out.writeObject(videoChoice);
            out.writeObject(protocolChoice);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void receiveStream (String protocol) {
        try {
            String commandToServer = "startStream";
            out.writeObject(commandToServer);
            
            String serverMsg = (String) in.readObject();
            if (serverMsg.equals("Start of streaming")) {
                //IP and port that ffplay will bind on
                String anIP = this.serverIP;
                int aPort = 1234;
                
                //Building the command to execute
                String command = this.ffplayLocation + " " + protocol 
                                    + "://" + anIP 
                                    + ":" + aPort;
                
                //Executing the command
                System.out.println("---- Receiving stream ----");
                Process p = Runtime.getRuntime().exec(command);
                
                //Show process log in the console
                InputStream inS = p.getErrorStream();
                int c;
                while((c = inS.read()) != -1) {
                    System.out.print((char)c);
                }
                inS.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
