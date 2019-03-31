
import javafx.fxml.FXML;

import javafx.fxml.Initializable;


import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;


public class Controller implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }
    public  void Run() throws Exception{
        System.out.println("Running");
        InetAddress address = InetAddress.getByName("192.168.77.242");
        Socket socket = new Socket(address,6881);

    }
}
