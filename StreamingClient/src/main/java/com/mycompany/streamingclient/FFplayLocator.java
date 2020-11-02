/*
 * This class is responsible for the discovery of ffplay in the user's system
 * @author Giannis Gkalmpenis
 */
package com.mycompany.streamingclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import javafx.application.Platform;

public class FFplayLocator extends Thread {
    private int retValue = -1;
    private String ffplayLocation = null;
    public FXMLController controller;
    
    public void setController(FXMLController controller) {
        this.controller = controller;
    }
    
    public void locateFFplay() {
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
    
    public String getFFplayLocation() {
        return this.ffplayLocation;
    }
    
    public Thread thread = new Thread() {
        @Override
        public void run() {
            this.findFFplayLocation();
        }
        
        public void findFFplayLocation() {
            //Searches for the existance of FFplay and if found, sets the path in "ffplayLocation" variable and the value "1" in "retValue".
            try {
                LocalDateTime initial = LocalDateTime.now(); //To stop the search after 30 seconds
                Process p = Runtime.getRuntime().exec("cmd.exe /c where /r %SystemDrive%\\ ffplay.exe"); //Search command
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

                long current, prev, difference;
                current = prev = difference = -1;

                while(true) {
                    current = ChronoUnit.SECONDS.between(initial, LocalDateTime.now());
                    difference = current - prev;

                    if (difference != 0) {
                        //Update the GUI after 1 second has passed since the previous update, otherwise it is constantly updated and eventually stuck
                        String message = "Searching for " + current + "/30 seconds";
                        Platform.runLater(() -> {controller.updateFFplayLabel(message);});
                    }
                    prev = current; //After the control has passed we can now update the "prev" variable

                    if (current >= 30)
                        break; //Stop searching after 30 seconds

                    if (br.ready()) {
                        //We use br.ready() so as not to halt the execution
                        //(or else everything stops until br.readLine() has a line to read) 
                        String line = br.readLine();
                        if (isTheProperFFplayInstallation(line)) {
                            FFplayLocator.this.ffplayLocation = line;
                            FFplayLocator.this.retValue = 1;
                            System.out.println("Found FFplay location: " + line);
                            Platform.runLater(() -> {controller.updateFFplayLabel(line);});
                            controller.doThisWhenFFplayIsFound();
                            p.destroy();
                            break;
                        }
                    }
                }
                if (FFplayLocator.this.retValue != 1) {
                    FFplayLocator.this.retValue = 0; //After 30 seconds and no success, the wanted path is considered not found
                    Platform.runLater(() -> {controller.updateFFplayLabel("Could not find the location automatically");});
                    p.destroy();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        private boolean isTheProperFFplayInstallation(String path) {
            //The wanted path is of type "Drive:\...\ffmpeg\bin\ffplay.exe

            String[] pathParts = path.split("\\\\");
            int length = pathParts.length;

            if (length >= 4) {
                if ( pathParts[length-1].equals("ffplay.exe") &&
                        pathParts[length-2].equals("bin") &&
                        pathParts[length-3].equals("ffmpeg") )
                    return true;
            }
            return false;
        }
    }; //End of Thread
} //End of FFplayLocator
