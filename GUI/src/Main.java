
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
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.show();
    }


    public static void main(String[] args) throws Exception {
        launch(args);
        NetworkController.startServer();

        byte arr[] = TorrentFileReader.readFile("files/fb.torrent");
        Torrent torrent = new Torrent(bCoder.decode(arr, ParcelType.TORRENT));
        NetworkController.addTorrent(torrent);
    }

}
