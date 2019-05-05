
import animatefx.animation.Jello;
import animatefx.animation.RollIn;
import animatefx.animation.Tada;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.print.attribute.SetOfIntegerSyntax;
import javax.sound.midi.Track;


public class Controller implements Initializable {

    public static Stage primaryStage;
    @FXML private TableView<Tracker> trackers;
    @FXML private TableColumn<Tracker,String> trackersID;
    @FXML private TableColumn<Tracker,String> trackersStatus;
    @FXML private Button Refresh;



    @FXML private TableView<DownloadFile> Files;
    @FXML private TableColumn<DownloadFile,String> FileName;
    @FXML private TableColumn<DownloadFile,Long> FileSize;
    @FXML private TableColumn<DownloadFile,Long> FileDownloaded;
    @FXML private TableColumn<DownloadFile,String> FileStatus;

    @FXML private TableView<Piece> Pieces;
    @FXML private TableColumn<Piece, Integer> PieceDownloaded;
    @FXML private TableColumn<Piece,Integer> PieceSize;
    @FXML private TableColumn<Piece,String> PieceStatus;
    @FXML private TableColumn<Piece,Integer> PieceIndex;

    @FXML private TableView<Connection> peers;
    @FXML private TableColumn<Connection,String> PeerID;
    @FXML private TableColumn<Connection,String> PeerStatus;
    @FXML private TableColumn<Connection,String> PeerDebug;

    @FXML
    private Pane paneStatus;

    @FXML
    private Button btnTorrents;
    @FXML private Button addTorrentBtn;

    @FXML
    private Button btnSettings;

    @FXML
    private Button btnAddTorrent;

    @FXML
    private Button btnTasks;

    @FXML
    private Label labelStatus;


    @FXML
    private Button btnclose;

    @FXML
    private GridPane settGrid;

    @FXML
    private GridPane torrnetGrid;

    @FXML
    private GridPane taskGrid;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        trackersID.setCellValueFactory(new PropertyValueFactory<Tracker, String>("uri"));
        trackersStatus.setCellValueFactory(new PropertyValueFactory<Tracker, String>("Status"));
       try{ trackers.getItems().addAll(NetworkController.getTorrents().get(0).getTrackers());}catch(Exception ioobe){}

        PeerID.setCellValueFactory(new PropertyValueFactory<Connection,String>("ID"));
        PeerStatus.setCellValueFactory(new PropertyValueFactory<Connection,String>("state"));
        PeerDebug.setCellValueFactory(new PropertyValueFactory<Connection,String>("debug"));
        try{ peers.getItems().addAll(NetworkController.getTorrents().get(0).getConnections());}catch(Exception e){}

        FileName.setCellValueFactory(new PropertyValueFactory<DownloadFile, String>("path"));
        FileSize.setCellValueFactory(new PropertyValueFactory<DownloadFile, Long>("length"));
        FileStatus.setCellValueFactory(new PropertyValueFactory<DownloadFile, String>("status"));
        FileDownloaded.setCellValueFactory(new PropertyValueFactory<DownloadFile,Long>("downloaded"));
        try{Files.getItems().addAll(NetworkController.getTorrents().get(0).getFiles());}catch(Exception ioobe){}

        PieceDownloaded.setCellValueFactory(new PropertyValueFactory<Piece,Integer>("downloaded"));
        PieceSize.setCellValueFactory(new PropertyValueFactory<Piece, Integer>("length"));
        PieceStatus.setCellValueFactory(new PropertyValueFactory<Piece, String>("status"));
        PieceIndex.setCellValueFactory(new PropertyValueFactory<Piece, Integer>("index"));
        try{Pieces.getItems().addAll(NetworkController.getTorrents().get(0).getPieces());}catch(Exception ioobe){}

        RefreshTimer T= new RefreshTimer();
        T.start();
    }

    public void setRefresh() {
        try{
            trackers.getItems().clear();
            trackers.getItems().addAll(NetworkController.getTorrents().get(0).getTrackers());

            Pieces.getItems().clear();
            Pieces.getItems().addAll(NetworkController.getTorrents().get(0).getPieces());

            Files.getItems().clear();
            Files.getItems().addAll(NetworkController.getTorrents().get(0).getFiles());

            peers.getItems().clear();
            peers.getItems().addAll(NetworkController.getTorrents().get(0).getConnections());
            }catch(Exception ioobe){}
    }


   public class RefreshTimer {
        private Timer timer = new Timer(true);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                setRefresh();

            }
        };

        public void start() {
            timer.scheduleAtFixedRate(task, 0, 500);
        }
    }


    @FXML
    private void Handleclicks(ActionEvent event){

        if(event.getSource()==btnTorrents){
            labelStatus.setText("Torrents");
            paneStatus.setBackground(new Background(new BackgroundFill(Color.rgb(0, 80, 58), CornerRadii.EMPTY, Insets.EMPTY)));
           new Tada(paneStatus).play();
            torrnetGrid.toFront();
        }

        else if(event.getSource()==btnSettings){
            labelStatus.setText("Settings");
            paneStatus.setBackground(new Background(new BackgroundFill(Color.rgb(170, 0, 14), CornerRadii.EMPTY, Insets.EMPTY)));
            new RollIn(paneStatus).play();
            settGrid.toFront();
        }
        else if(event.getSource()==btnTasks){
            labelStatus.setText("Monitor");
            paneStatus.setBackground(new Background(new BackgroundFill(Color.rgb(17, 225, 111), CornerRadii.EMPTY, Insets.EMPTY)));
            new Jello(paneStatus).play();
            taskGrid.toFront();
        }
    }

    public void addTorrentBtnPress(ActionEvent e){
        FileChooser chooser = new FileChooser();
        File dir = new File("files/");
        chooser.setInitialDirectory(dir);
        File fl = chooser.showOpenDialog(primaryStage);
        try {
            byte arr[] = TorrentFileReader.readFile(fl.getAbsolutePath());
            Torrent torrent = new Torrent(bCoder.decode(arr, ParcelType.TORRENT));
            NetworkController.addTorrent(torrent);
            torrent.invokeThreads();
        }catch(Exception ex){ex.printStackTrace();}
    }

    public void HandleClose(MouseEvent mouseEvent) {
        if(mouseEvent.getSource()==btnclose){
            System.exit(0);
        }
    }
}
