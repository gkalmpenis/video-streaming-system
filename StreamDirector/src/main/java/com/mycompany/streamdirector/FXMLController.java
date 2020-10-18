package com.mycompany.streamdirector;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class FXMLController implements Initializable {
    private final Converter converter = new Converter();
    private final FFmpegLocator ffmpegLocator = new FFmpegLocator();
    private String inputFile;
    private String ffmpegLocation;
    
    @FXML private Label ffmpegLabel;
    @FXML private Label selectionLabel;
    @FXML private Label conversionLabel;
    @FXML private VBox fileSelectionVBox;
    @FXML private VBox conversionVBox;
    
    @FXML
    public void updateFFmpegLabel(String txt) {
        ffmpegLabel.setText(txt);
    }
    
    @FXML 
    public void doThisWhenFFmpegIsFound() {
        converter.setFFmpegLocation(ffmpegLocator.getFFmpegLocation());
        fileSelectionVBox.setVisible(true);
    }
    
    @FXML
    private void handleFFmpegFindButton (ActionEvent event) {
        ffmpegLocator.setController(this);
        ffmpegLocator.locateFFmpeg(); //This method also prints messages to our GUI
    }
    
    @FXML
    private void handleFFmpegSelectButton (ActionEvent event) {
        FileChooser fc = new FileChooser();
        Window appWindow = selectionLabel.getScene().getWindow(); //Get the required window with the help of selectionLabel
        File selectedFile = fc.showOpenDialog(appWindow);
        this.ffmpegLocation = selectedFile.getAbsolutePath();
        converter.setFFmpegLocation(ffmpegLocation);
        ffmpegLabel.setText(ffmpegLocation);
        fileSelectionVBox.setVisible(true);
    }
    
    @FXML
    private void handleFileSelectButton (ActionEvent event) {
        FileChooser fc = new FileChooser();
        Window appWindow = selectionLabel.getScene().getWindow(); //Get the required window with the help of selectionLabel
        File selectedFile = fc.showOpenDialog(appWindow);
        this.inputFile = selectedFile.getAbsolutePath();
        converter.setAbsoluteInputFilePath(inputFile);
        selectionLabel.setText(inputFile);
        conversionVBox.setVisible(true);
    }
    
    @FXML
    private void handleConvertButton (ActionEvent event) {
        int code = converter.convert();
        switch (code) {
            case 0:
                conversionLabel.setText("Successful conversion.");
                conversionLabel.setTextFill(Color.GREEN);
                break;
            case 1:
                conversionLabel.setText("Conversion failed.");
                conversionLabel.setTextFill(Color.RED);
                break;
            default:
                break;
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    
}
