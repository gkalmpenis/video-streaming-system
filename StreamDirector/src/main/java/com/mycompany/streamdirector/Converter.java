/*
 * This class is responsible for the video conversion to the required formats
 * @author Giannis Gkalmpenis
 */
package com.mycompany.streamdirector;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Converter {
    public static String[] bitrateKB = {"200k", "500k", "1000k", "3000k"};
    public static String[] maxrateKB = {"400k", "1000k", "2000k", "6000k"};
    public static String[] bitrateMB = {"0.2Mbps", "0.5Mbps", "1.0Mbps", "3.0Mbps"};
    public static String[] format = {"avi", "mp4", "mkv"};
    String ffmpegLocation = null;
    String absoluteInputFilePath = null;
    String filename = null;
    String bareFilename = null;
    String inputDir = null;
    String outputDir = null;
    
    public int convert() {
        this.setFilename(absoluteInputFilePath);
        this.bareFilename = getBareFilename(filename);
        this.setInOutDirectories();

        try {
            //Create output directory if it doesn't exist
            File directory = new File(outputDir);
            if (! directory.exists()) { directory.mkdir(); }

            for (String outFormat : format) {
                //Videos will be converted one after the other
                
                String command = "";
                for (int i=0; i<bitrateKB.length; i++) {
                    command = buildCommand(bareFilename, maxrateKB[i], bitrateKB[i], bitrateMB[i], outputDir, outFormat);
                    System.out.println("\n\n\nExecuting command: " + command);
                    Process p = Runtime.getRuntime().exec(command);
                    InputStream in = p.getErrorStream();
                    int c;
                    while((c = in.read()) != -1) {
                        //Process log in the console
                        System.out.print((char)c);
                    }
                    in.close();
                }
            }
            //this.deleteFiles(); //This was asked for the assignment, we will not use it now
            return 0; //Conversion success
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }
    }
    
    private String buildCommand(String bareFilename, String outMaxrateKB, String outBitrateKB, String outBitrateMB, String outPath, String outFormat) {
        return ffmpegLocation + " -i " + absoluteInputFilePath + " -b:v " 
                    + outBitrateKB + " -maxrate " + outMaxrateKB 
                    + " -bufsize " + outBitrateKB + " -y " + outPath
                    + bareFilename + "-" + outBitrateMB + "." + outFormat;
    }
    
    private void deleteFiles() {
        /* Deleting the files from "raw_videos" folder
         * after checking that we are really there so as not to
         * accidentally delete other user files
         */
        String[] pathParts = inputDir.split("\\\\");
        String folder = pathParts[pathParts.length-1];
        
        if ( folder.equals("raw_videos") ) {
            File raw_videos = new File(inputDir);
            for (File file : raw_videos.listFiles()) {
                file.delete();
            }
        }
    }

    public void setFFmpegLocation(String location) {
        this.ffmpegLocation = location;
    }
    
    public void setAbsoluteInputFilePath(String absolutePath) {
        this.absoluteInputFilePath = absolutePath;
    }
    
    private void setFilename(String absolutePath) {
        String[] parts = absolutePath.split("\\\\");
        this.filename = parts[parts.length-1];
    }
    
    private String getBareFilename(String fname) {
        String bareName = "";
        String[] filenameParts = fname.split("\\.");
        for (String f : filenameParts) {
            if ( f.equals(filenameParts[filenameParts.length-2]))
            {
                //filenameParts.length-2 is the last filename part (after that follows the extension)
                bareName = bareName + f;
                break; //End the iteration
            }
            //We splitted by "\\." so we now add it
            bareName = bareName + f + ".";
        }
        return bareName;
    }
    
    private void setInOutDirectories() {
        String outPath = "";
        String inPath = "";
        String[] parts = absoluteInputFilePath.split("\\\\");
        for (String f : parts) {
            if ( f.equals(parts[parts.length-2]))
            {
                //parts.length-2 should be "raw_videos"
                inPath = inPath + f;
                outPath = outPath + "\\videos\\";
                break;
            }
            //We splitted by "\\\\" so we now add it
            inPath = inPath + f + "\\";
            outPath = outPath + f + "\\";
        }
        this.inputDir = inPath;
        this.outputDir = outPath;
    }
}