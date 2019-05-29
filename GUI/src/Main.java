
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("scene.fxml"));
        Controller.primaryStage=primaryStage;
        primaryStage.setTitle("SicTorrent");
        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.setOnCloseRequest(event->{
              NetworkController.killServer();
              NetworkController.killTorrents();
              //save torrent objects
        });
        primaryStage.show();
    }


    public static void main(String[] args) throws Exception
    {
        NetworkController.startServer();

        File fl = new File("torrents.list");
        if (fl.exists()) {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fl));
            try {
                ArrayList<Torrent> torrents = (ArrayList<Torrent>) ois.readObject();
                NetworkController.addTorrents(torrents);
                NetworkController.invokeTorrents();
            }catch(Exception e){}
        }
        launch(args);
    }
}
