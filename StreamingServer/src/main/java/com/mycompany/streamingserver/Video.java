/**
 *
 * @author Giannis Gkalmpenis
 */
package com.mycompany.streamingserver;

public final class Video {
    public String fullPath;
    public String bareName;
    public String bitrate;
    public String format;
    public String nameWithAttributes;

    //Constructors
    public Video(String path) { 
        this.fullPath = path;
        this.bareName = getBareName(path);
        this.bitrate = getBitrate(path);
        this.format = getFormat(path);
        this.nameWithAttributes = bareName + "-" + bitrate + "." + format;
    }
    public Video() { /*If no arguments are given do nothing*/ }
    
    
    public String getBareName (String fullPath) {
        String[] pathParts = fullPath.split("\\\\");
        String filename = pathParts[pathParts.length-1];
        String[] filenameParts = filename.split("-");
        if (filenameParts.length == 2) {
            return filenameParts[0];
        }
        return null;
    }
    public String getBitrate (String fullPath) {
        String[] pathParts = fullPath.split("\\\\");
        String filename = pathParts[pathParts.length-1];
        String[] filenameParts = filename.split("-");
        if (filenameParts.length == 2) {
            String[] partsAfterDash = filenameParts[1].split("\\.");
            return partsAfterDash[0] + "." + partsAfterDash[1];
        }
        return null;
    }
    public String getFormat (String fullPath) {
        String[] pathParts = fullPath.split("\\\\");
        String filename = pathParts[pathParts.length-1];
        String[] filenameParts = filename.split("\\.");
        return filenameParts[filenameParts.length-1]; //Last part will always be file extension
    }
}
