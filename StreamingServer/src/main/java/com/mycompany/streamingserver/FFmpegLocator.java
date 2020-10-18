/*
 * This class is responsible for the discovery of ffmpeg in the user's system
 * @author Giannis Gkalmpenis
 */
package com.mycompany.streamingserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import javafx.application.Platform;

public class FFmpegLocator extends Thread {
    private int retValue = -1;
    private String ffmpegLocation = null;
    public FXMLController controller;
    
    public void setController(FXMLController controller) {
        this.controller = controller;
    }
    
    public void locateFFmpeg() {
        thread.setDaemon(true);
        thread.start();
    }
    
    public int getRetValue() {
        /* -1 means IOException occured (so the variable remained with its default value)
            0 means the location was not found
            1 means the location was found
        */
        return this.retValue;
    }
    
    public String getFFmpegLocation() {
        return this.ffmpegLocation;
    }
    
    public Thread thread = new Thread() {
        @Override
        public void run() {
            this.findFFmpegLocation();
        }
        
        public void findFFmpegLocation() {
            //Searches for the existance of FFmpeg and if found, sets the path in "ffmpegLocation" variable and the value "1" in "retValue".
            try {
                LocalDateTime initial = LocalDateTime.now(); //To stop the search after 30 seconds
                Process p = Runtime.getRuntime().exec("cmd.exe /c where /r %SystemDrive%\\ ffmpeg.exe"); //Search command
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

                long current, prev, difference;
                current = prev = difference = -1;

                while(true) {
                    current = ChronoUnit.SECONDS.between(initial, LocalDateTime.now());
                    difference = current - prev;

                    if (difference != 0) {
                        //Update the GUI after 1 second has passed since the previous update, otherwise it is constantly updated and eventually stuck
                        String message = "Searching for " + current + "/30 seconds";
                        Platform.runLater(() -> {controller.updateFFmpegLabel(message);});
                    }
                    prev = current; //After the control has passed we can now update the "prev" variable

                    if (current >= 30)
                        break; //Stop searching after 30 seconds

                    if (br.ready()) {
                        //We use br.ready() so as not to halt the execution
                        //(or else everything stops until br.readLine() has a line to read) 
                        String line = br.readLine();
                        if (isTheProperFFmpegInstallation(line)) {
                            FFmpegLocator.this.ffmpegLocation = line;
                            FFmpegLocator.this.retValue = 1;
                            System.out.println("Found FFmpeg location: " + line);
                            Platform.runLater(() -> {controller.updateFFmpegLabel(line);});
                            controller.doThisWhenFFmpegIsFound();
                            p.destroy();
                            break;
                        }
                    }
                }
                if (FFmpegLocator.this.retValue != 1) {
                    FFmpegLocator.this.retValue = 0; //After 30 seconds and no success, the wanted path is considered not found
                    Platform.runLater(() -> {controller.updateFFmpegLabel("Could not find the location automatically");});
                    p.destroy();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        private boolean isTheProperFFmpegInstallation(String path) {
            //The wanted path is of type "Drive:\...\ffmpeg\bin\ffmpeg.exe

            String[] pathParts = path.split("\\\\");
            int length = pathParts.length;

            if (length >= 4) {
                if ( pathParts[length-1].equals("ffmpeg.exe") &&
                        pathParts[length-2].equals("bin") &&
                        pathParts[length-3].equals("ffmpeg") )
                    return true;
            }
            return false;
        }
    }; //End of Thread
} //End of FFmpegLocator
