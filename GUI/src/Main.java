
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.Socket;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("scene.fxml"));
        primaryStage.setTitle("HelloWorld");


        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.show();
    }


    public static void main(String[] args) throws Exception {
        launch(args);

    }


    public static void Run() throws Exception{
        InetAddress address = InetAddress.getByName("192.168.77.242");
        Socket socket = new Socket(address,6881);
    }
}
