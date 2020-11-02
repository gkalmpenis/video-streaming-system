package com.mycompany.streamingclient;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class FXMLController implements Initializable {
    private Client client = new Client();
    private final FFplayLocator ffplayLocator = new FFplayLocator();
    private String ffplayLocation;
    private String userSpeed;
    private String formatChoice;
    
    @FXML private Label ffplayLabel;
    @FXML private Label connectLabel;
    @FXML private HBox speedFormatHBox;
    @FXML private TextField speedTextField;
    @FXML private ComboBox formatComboBox;
    @FXML private VBox videoListVBox;
    @FXML private ListView videosListView;
    @FXML private HBox protocolHBox;
    @FXML private ComboBox protocolComboBox;
    @FXML private VBox requestVBox;
    
    @FXML
    public void updateFFplayLabel(String txt) {
        ffplayLabel.setText(txt);
    }
    
    @FXML 
    public void doThisWhenFFplayIsFound() {
        client.setFFplayLocation(ffplayLocator.getFFplayLocation());
    }
    
    @FXML
    private void handleFFplayFindButton (ActionEvent event) {
        ffplayLocator.setController(this);
        ffplayLocator.locateFFplay(); //This method also prints messages to our GUI
    }
    
    @FXML
    private void handleFFplaySelectButton(ActionEvent event) {
        FileChooser fc = new FileChooser();
        Window appWindow = ffplayLabel.getScene().getWindow(); //Get the required window with the help of ffplayLabel
        File selectedFile = fc.showOpenDialog(appWindow);
        this.ffplayLocation = selectedFile.getAbsolutePath();
        client.setFFplayLocation(ffplayLocation);
        ffplayLabel.setText(ffplayLocation);
    }
    @FXML
    private void handleConnectSelection(ActionEvent event) {
        String msg = client.connectToServer();
        connectLabel.setText(msg);
        if (msg.equals("Successful connection")) {
            speedFormatHBox.setVisible(true);
            videoListVBox.setVisible(true);
        }
    }
    @FXML
    private void showAvailableVideos(ActionEvent event) {
        userSpeed = speedTextField.getText();
        formatChoice = formatComboBox.getValue().toString();
        if (userSpeed!= null && formatChoice!= null) {
            //First, we send speed and format to the Server
            client.sendSpeedAndFormat(userSpeed, formatChoice);
            
            //Second, we receive the appropriate videos
            client.receiveVideos();
            ArrayList<String> videos = new ArrayList<String>();
            videos = client.getVideoList();
            videosListView.setItems(FXCollections.observableArrayList(videos));
            
            //Show last GUI elements
            protocolHBox.setVisible(true);
            requestVBox.setVisible(true);
        }
    }
    @FXML
    private void handleRequestBtn(ActionEvent event) {
        String videoChoice = videosListView.getSelectionModel().getSelectedItem().toString();
        String protocolChoice = protocolComboBox.getValue().toString().toLowerCase();
        if ((videoChoice!=null) && (protocolChoice!=null)) {
            client.sendVideoAndProtocol(videoChoice, protocolChoice);
            client.receiveStream(protocolChoice);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        formatComboBox.setItems(FXCollections.observableArrayList("AVI","MP4","MKV"));
        protocolComboBox.setItems(FXCollections.observableArrayList("UDP","TCP"));
    }    
}
