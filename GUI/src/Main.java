
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class Main extends Application {
    public static Stage loadStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        loadStage = new Stage();
        loadStage.initStyle(StageStyle.UNDECORATED);
        StackPane stackpane = new StackPane();
        Image img = new Image("icon.png");
        ImageView imgview = new ImageView(img);
        stackpane.getChildren().addAll(imgview);
        stackpane.setAlignment(Pos.CENTER);
        Scene scene = new Scene(stackpane, img.getWidth(), img.getHeight());
        loadStage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        loadStage.initModality(Modality.APPLICATION_MODAL);
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        loadStage.setScene(scene);
        loadStage.show();
        loadStage.setX((primScreenBounds.getWidth() - loadStage.getWidth()) / 2);
        loadStage.setY((primScreenBounds.getHeight() - loadStage.getHeight()) / 2);


        new Thread(new Task() {
            @Override
            protected Object call() throws Exception {
                NetworkController.startServer();

                File fl = new File("torrents.list");
                if (fl.exists()) {
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fl));
                    try {
                        ArrayList<Torrent> torrents = (ArrayList<Torrent>) ois.readObject();
                        NetworkController.addTorrents(torrents);
                        NetworkController.invokeTorrents();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Thread t = new Thread(() -> {
                    while (true) {
                        try {
                            Thread.sleep(30000);
                        } catch (InterruptedException ie) {
                            return;
                        }
                        try {
                            File fil = new File("torrents.list");
                            fil.createNewFile();
                            ObjectOutputStream obs = new ObjectOutputStream(new FileOutputStream(fil));
                            obs.writeObject(NetworkController.getTorrents());
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                    }
                });

                Platform.runLater(() -> {
                    try{
                    Parent root = FXMLLoader.load(getClass().getResource("scene.fxml"));
                    Controller.primaryStage = primaryStage;
                    loadStage.hide();
                    primaryStage.setTitle("SicTorrent");
                    primaryStage.setScene(new Scene(root, 1280, 720));
                    primaryStage.setOnCloseRequest(event -> {
                        t.interrupt();
                        NetworkController.killServer();
                        NetworkController.killTorrents();
                        //save torrent objects
                        try {
                            File fll = new File("torrents.list");
                            fll.createNewFile();
                            ObjectOutputStream obs = new ObjectOutputStream(new FileOutputStream(fll));
                            obs.writeObject(NetworkController.getTorrents());
                        }catch (IOException ioe){}
                    });
                    primaryStage.setOnHiding(event -> {
                            t.interrupt();
                            NetworkController.killServer();
                            NetworkController.killTorrents();
                            //save torrent objects
                        try {
                            File fll = new File("torrents.list");
                            fll.createNewFile();
                            ObjectOutputStream obs = new ObjectOutputStream(new FileOutputStream(fll));
                            obs.writeObject(NetworkController.getTorrents());
                        }catch (IOException ioe){}
                    });
                    loadStage.close();
                    primaryStage.show();
                    primaryStage.setMaximized(true);
                   }catch(Exception e){}
                });
                return null;
            }
        }).start();
    }


    public static void main(String[] args) throws Exception {
        File fl = new File("Err.txt");
        fl.createNewFile();
       // System.setErr(new PrintStream(fl));
        launch(args);
    }
}
