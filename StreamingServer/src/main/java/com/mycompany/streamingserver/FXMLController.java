package com.mycompany.streamingserver;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class FXMLController implements Initializable {
    private final Server server = new Server();
    private final FFmpegLocator ffmpegLocator = new FFmpegLocator();
    private String ffmpegLocation;
    private String folderLocation;
    
    @FXML private Label ffmpegLabel;
    @FXML private Label folderLabel;
    @FXML public Label serverMsgLabel;
    
    @FXML
    public void updateFFmpegLabel(String txt) {
        ffmpegLabel.setText(txt);
    }
    
    @FXML 
    public void doThisWhenFFmpegIsFound() {
        server.setFFmpegLocation(ffmpegLocator.getFFmpegLocation());
    }
    
    @FXML
    private void handleFFmpegFindButton (ActionEvent event) {
        ffmpegLocator.setController(this);
        ffmpegLocator.locateFFmpeg(); //This method also prints messages to our GUI
    }
    
    @FXML
    private void handleFFmpegSelectButton(ActionEvent event) {
        FileChooser fc = new FileChooser();
        Window appWindow = ffmpegLabel.getScene().getWindow(); //Get the required window with the help of ffmpegLabel
        File selectedFile = fc.showOpenDialog(appWindow);
        this.ffmpegLocation = selectedFile.getAbsolutePath();
        server.setFFmpegLocation(ffmpegLocation);
        ffmpegLabel.setText(ffmpegLocation);
    }
    @FXML
    private void handleFolderSelection(ActionEvent event) {
        DirectoryChooser dc = new DirectoryChooser();
        Window appWindow = folderLabel.getScene().getWindow();
        File selectedFolder = dc.showDialog(appWindow);
        this.folderLocation = selectedFolder.getAbsolutePath();
        server.setFolderLocation(folderLocation);
        folderLabel.setText(folderLocation);
    }
    @FXML
    private void handleServerStart(ActionEvent event) {
        server.startServer();
        serverMsgLabel.setText("Server is up");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    
}
