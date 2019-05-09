
import animatefx.animation.Jello;
import animatefx.animation.RollIn;
import animatefx.animation.Tada;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;

import java.io.File;
import java.util.*;

import java.net.URL;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
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
    private static ArrayList<Long> downloadeds = new ArrayList<>();


    @FXML private TableView<DownloadFile> Files;
    @FXML private TableColumn<DownloadFile,String> FileName;
    @FXML private TableColumn<DownloadFile,Long> FileSize;
    @FXML private TableColumn<DownloadFile,Long> FileDownloaded;
    @FXML private TableColumn<DownloadFile,String> FileStatus;

    @FXML private TableView<Torrent> Torrents;
    @FXML private TableColumn<Torrent,String> TorrentName;
    @FXML private TableColumn<Torrent,Long> TorrentSize;
    @FXML private TableColumn<Torrent,ProgressBar> TorrentStatus;
    @FXML private TableColumn<Torrent,Long> TorrentDownloaded;

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

    @FXML private Button btnDelete;

    @FXML private Label speedlbl;

    @FXML
    private Button btnAddTorrent;


    @FXML
    private Label labelStatus;


    @FXML
    private Button btnclose;

    @FXML
    private GridPane settGrid;

    @FXML
    private GridPane torrnetGrid;

    private static Torrent currentTorrent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (NetworkController.getTorrents().size()>0)
            currentTorrent=NetworkController.getTorrents().get(0);


        if (currentTorrent!=null) {
            TorrentName.setCellValueFactory(new PropertyValueFactory<>("name"));
            TorrentDownloaded.setCellValueFactory(new PropertyValueFactory<>("downloaded"));
            TorrentSize.setCellValueFactory(new PropertyValueFactory<>("length"));
            TorrentStatus.setCellValueFactory(new PropertyValueFactory<>("progress"));
            Torrents.setRowFactory(e-> {
                TableRow<Torrent> row = new TableRow<>();
                row.setOnMouseClicked(event->{
                    if (event.getClickCount()==1 && event.getButton().equals(MouseButton.PRIMARY))
                        currentTorrent = row.getItem();
                });
                return row;
            });
            //TorrentStatus.setCellFactory(ProgressBarTableCell.<Torrent> forTableColumn());
            try {
                Torrents.getItems().addAll(NetworkController.getTorrents());
            } catch (Exception ioobe) {
            }

            trackersID.setCellValueFactory(new PropertyValueFactory<Tracker, String>("uri"));
            trackersStatus.setCellValueFactory(new PropertyValueFactory<Tracker, String>("Status"));
            try {
                trackers.getItems().addAll(currentTorrent.getTrackers());
            } catch (Exception ioobe) {
            }

            PeerID.setCellValueFactory(new PropertyValueFactory<Connection, String>("ID"));
            PeerStatus.setCellValueFactory(new PropertyValueFactory<Connection, String>("state"));
            PeerDebug.setCellValueFactory(new PropertyValueFactory<Connection, String>("debug"));
            try {
                peers.getItems().addAll(currentTorrent.getConnections());
            } catch (Exception e) {
            }

            FileName.setCellValueFactory(new PropertyValueFactory<DownloadFile, String>("path"));
            FileSize.setCellValueFactory(new PropertyValueFactory<DownloadFile, Long>("length"));
            FileStatus.setCellValueFactory(new PropertyValueFactory<DownloadFile, String>("status"));
            FileDownloaded.setCellValueFactory(new PropertyValueFactory<DownloadFile, Long>("downloaded"));
            try {
                Files.getItems().addAll(currentTorrent.getFiles());
            } catch (Exception ioobe) {
            }

            PieceDownloaded.setCellValueFactory(new PropertyValueFactory<Piece, Integer>("downloaded"));
            PieceSize.setCellValueFactory(new PropertyValueFactory<Piece, Integer>("length"));
            PieceStatus.setCellValueFactory(new PropertyValueFactory<Piece, String>("status"));
            PieceIndex.setCellValueFactory(new PropertyValueFactory<Piece, Integer>("index"));
            try {
                Pieces.getItems().addAll(currentTorrent.getPieces());
            } catch (Exception ioobe) {
            }
        }
        RefreshTimer T= new RefreshTimer();
        T.start();
    }

    public void setRefresh() {
        try{
            if (downloadeds.size()==NetworkController.getTorrents().size())
            {
                long data=0;
                for (int i=0;i<downloadeds.size();i++){
                    data+=NetworkController.getTorrents().get(i).getDownloaded()-downloadeds.get(i);
                }
                double speed = (double)data/1024.0;
                Platform.runLater(()->speedlbl.setText(String.format("%.2f KB/s",speed)));
            }
            downloadeds.clear();
            for (Torrent t:NetworkController.getTorrents())
                downloadeds.add(t.getDownloaded());

            Torrents.getItems().clear();
            Torrents.getItems().addAll(NetworkController.getTorrents());

            trackers.getItems().clear();
            trackers.getItems().addAll(currentTorrent.getTrackers());

            Pieces.getItems().clear();
            Pieces.getItems().addAll(currentTorrent.getPieces());

            Files.getItems().clear();
            Files.getItems().addAll(currentTorrent.getFiles());

            peers.getItems().clear();
            peers.getItems().addAll(currentTorrent.getConnections());
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
            timer.scheduleAtFixedRate(task, 0, 1000);
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
    }
    public void deleteTorrentBtnPress(ActionEvent e){
        if (currentTorrent!=null){
            currentTorrent.killThreads();
            NetworkController.getTorrents().remove(currentTorrent);
            if (NetworkController.getTorrents().size()>0)
                currentTorrent = NetworkController.getTorrents().get(0);
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
            currentTorrent = torrent;
        }catch(Exception ex){ex.printStackTrace();}
    }

    public void HandleClose(MouseEvent mouseEvent) {
        if(mouseEvent.getSource()==btnclose){
            System.exit(0);
        }
    }
}
