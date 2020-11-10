<p align="center">
  A system consisted of 3 applications capable of video conversion, streaming and bidirectional communication (server-client model).
</p>
<br>

## Technologies
* **Java 8**
* **JavaFX** framework
* **FFmpeg**
<br>

## Installation
1. Download and install **ffmpeg** (https://ffmpeg.org/download.html).
2. Run the _.jar_ files of the applications (download them from <a href="https://github.com/gkalmpenis/video-streaming-system/releases"><i>Releases</i></a>). 


3. That's it!
 
To log messages generated by the applications, run the _.jar_ files from a console (`java -jar <application-name>.jar`).

<br>

## Visuals
* **StreamDirector** application - converts a video to 3 formats (_avi_, _mp4_, _mkv_) and 4 bitrates (_0.2Mbps_, _0.5Mbps_, _1Mbps_, _3Mbps_) (a total of 12 outputs).
<details>
  <summary>Click to see it in action</summary>
  <p align="center">
    <img src="./img/sd.gif"/>
   <br>
   <sub><i>(GIF - Duration: 19 seconds)</i></sub>
  </p>
</details>
<br>

* **StreamingServer** application - accesses the videos to be streamed and implements the server logic.
<details>
  <summary>Click to see it in action</summary>
  <p align="center">
    <img src="./img/ss.gif"/>
    <br>
    <sub><i>(GIF - Duration: 8 seconds)</i></sub>
  </p>
</details>
<br>

* **StreamingClient** application - communicates with the server to request a subset of the available videos and to then request the streaming of a specific video.
<details>
  <summary>Click to see it in action</summary>
  <p align="center">
    <img align="center" src="./img/sc1.gif" width="590"/> <br>
    <b>1. Initial flow</b> 
    <br>
    <sub><i>(GIF - Duration: 15 seconds)</i></sub>
  </p>
  <br>
  
  <p align="center">
    <img src="./img/sc2.gif" width="590"/> <br>
    <b>2. Changing connection speed, video format and streaming protocol</b>
    <br>
    <sub><i>(GIF - Duration: 18 seconds)</i></sub>
  </p>
</details>
<br>

## Features
* _New:_ Locate the path to _ffmpeg_'s directory on the user's computer automatically, without the user having to declare it in any system variable.
* Server-Client communication is performed using _sockets_ and _threads_. The server can accept and will respond to multiple different requests from a client. Supports **TCP** and **UDP** communication protocols.
* The system can extract information from the title of a video which is of type _videoName_-_bitrate_._format_ (eg The.Invisible.Guest-3.0Mbps.mp4).
* The client receices a list with videos that have a bitrate less or equal with his/her connection speed.
* Built with **MVC Architecture Pattern**.
<br>

## Notes
University project for the course of _Multimedia and Multimedia Communications_. It is built to work on windows machines.
