
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Thread t = new Thread(()->{
           try{Thread.sleep(60000);}catch(InterruptedException ie){return;}
            try {
                File fl = new File("torrents.list");
                fl.createNewFile();
                ObjectOutputStream obs = new ObjectOutputStream(new FileOutputStream(fl));
                obs.writeObject(NetworkController.getTorrents());
            }catch (IOException ioe){ioe.printStackTrace();}
        });
        Parent root = FXMLLoader.load(getClass().getResource("scene.fxml"));
        Controller.primaryStage=primaryStage;
        primaryStage.setTitle("SicTorrent");
        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.setOnCloseRequest(event->{
            t.interrupt();
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
            }catch(Exception e){e.printStackTrace();}
        }
        launch(args);
    }
}
