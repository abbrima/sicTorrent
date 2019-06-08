import animatefx.animation.RollIn;
import animatefx.animation.Tada;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;

import java.awt.*;
import java.io.File;
import java.util.*;

import java.net.URL;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Controller implements Initializable {

    public static Stage primaryStage;
    @FXML private TableView<Tracker> trackers;
    @FXML private TableColumn<Tracker,String> trackersID;
    @FXML private TableColumn<Tracker,String> trackersStatus;
    @FXML private Button Refresh;
    private static ArrayList<Long> downloadeds = new ArrayList<>();


    @FXML private TableView<DownloadFile> Files;
    @FXML private TableColumn<DownloadFile,String> FileName;
    @FXML private TableColumn<DownloadFile,String> FileSize;
    @FXML private TableColumn<DownloadFile,String> FileDownloaded;
    @FXML private TableColumn<DownloadFile,String> FileStatuss;

    @FXML private TableView<Torrent> Torrents;
    @FXML private TableColumn<Torrent,String> TorrentName;
    @FXML private TableColumn<Torrent,String> TorrentSize;
    @FXML private TableColumn<Torrent,String> TorrentStatus;
    @FXML private TableColumn<Torrent,String> TorrentDownloaded;
    @FXML private TableColumn<Torrent,String> TorrentUploaded;

    private ContextMenu TorrentContextMenu;
    private Menu DownloadMode;
    private Menu TorrentPriority;
    private MenuItem TorrentHigh,TorrentNorm,TorrentLow;
    private MenuItem TorrentResume;
    private MenuItem TorrentPause;
    private MenuItem SequentialMode;
    private MenuItem RandomMode;

    @FXML private TableView<Piece> Pieces;
    @FXML private TableColumn<Piece, String> PieceDownloaded;
    @FXML private TableColumn<Piece,String> PieceSize;
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
    private static DownloadFile currentFile;
    private ContextMenu FilesMenu;
    private MenuItem Download,DoNotDownload;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (NetworkController.getTorrents().size()>0)
            currentTorrent=NetworkController.getTorrents().get(0);


        {
            TorrentContextMenu = new ContextMenu();
            TorrentContextMenu.addEventHandler(MouseEvent.MOUSE_CLICKED,e->{TorrentContextMenu.hide();});
            TorrentHigh = new MenuItem("High"); TorrentNorm = new MenuItem("Normal");
            TorrentLow = new MenuItem("Low");
            TorrentPriority = new Menu("Priority"); TorrentPriority.getItems().addAll(TorrentHigh,TorrentNorm,TorrentLow);
            SequentialMode = new MenuItem("Sequential");
            RandomMode = new MenuItem("Random");
            TorrentPause = new MenuItem("Pause");
            TorrentResume = new MenuItem("Resume");
            DownloadMode = new Menu("Download Mode");
            DownloadMode.getItems().addAll(SequentialMode,RandomMode);
            TorrentContextMenu.getItems().addAll(TorrentResume,TorrentPause,TorrentPriority,DownloadMode);
            TorrentPause.setOnAction(e->{pauseTorrent(e);});
            TorrentResume.setOnAction(e->{resumeTorrent(e);});
            SequentialMode.setOnAction(e->{setSequential(e);});
            RandomMode.setOnAction(e->{setRandom(e);});



            TorrentName.setCellValueFactory(new PropertyValueFactory<>("name"));
            TorrentDownloaded.setCellValueFactory(new PropertyValueFactory<>("DownloadedString"));
            TorrentUploaded.setCellValueFactory(new PropertyValueFactory<>("UploadedString"));
            TorrentSize.setCellValueFactory(new PropertyValueFactory<>("LengthString"));
            TorrentStatus.setCellValueFactory(new PropertyValueFactory<>("progress"));
            Torrents.setRowFactory(e-> {
                TableRow<Torrent> row = new TableRow<>();
                row.setOnMouseClicked(event->{
                    if (event.getClickCount()==1 && event.getButton().equals(MouseButton.PRIMARY) && row.getItem()!=null)
                        currentTorrent = row.getItem();
                    if (event.getClickCount()==1 && event.getButton().equals(MouseButton.SECONDARY) && row.getItem()!=null)
                    {
                        currentTorrent = row.getItem();
                        TorrentContextMenu.show(row,event.getScreenX(),event.getScreenY());
                    }
                });
                return row;
            });
            //TorrentStatus.setCellFactory(ProgressBarTableCell.<Torrent> forTableColumn());
            try {
                if (NetworkController.getTorrents().size()>0)
                Torrents.getItems().addAll(NetworkController.getTorrents());
            } catch (Exception ioobe) {Torrents.getItems().clear();
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

            FilesMenu = new ContextMenu();
            Download = new MenuItem("Download");
            DoNotDownload = new MenuItem("Do not download");
            FilesMenu.getItems().addAll(Download,DoNotDownload);
            FilesMenu.addEventHandler(MouseEvent.MOUSE_CLICKED,e->{
                FilesMenu.hide();
            });
            Download.setOnAction(e->{currentTorrent.downloadFile(currentFile);});
            DoNotDownload.setOnAction(e->{currentTorrent.doNotDownload(currentFile);});

            FileName.setCellValueFactory(new PropertyValueFactory<DownloadFile, String>("path"));
            FileSize.setCellValueFactory(new PropertyValueFactory<>("LengthString"));
            FileStatuss.setCellValueFactory(new PropertyValueFactory<DownloadFile, String>("status"));
            FileDownloaded.setCellValueFactory(new PropertyValueFactory<>("DownloadedString"));
            Files.setRowFactory(e-> {
                TableRow<DownloadFile> row = new TableRow<>();
                row.setOnMouseClicked(event->{
                    if (event.getClickCount()==1 && event.getButton().equals(MouseButton.SECONDARY)
                            && row.getItem()!=null)
                    {
                        currentFile = row.getItem();
                        FilesMenu.show(row,event.getScreenX(),event.getScreenY());
                    }
                    else if (event.getClickCount() == 2 && event.getButton().equals(MouseButton.PRIMARY) &&
                            row.getItem()!=null && row.getItem().getStatus() == FileStatus.DOWNLOADED)
                    {
                        Desktop desktop = Desktop.getDesktop();try {
                        desktop.open(new File(Parameters.downloadDir+row.getItem().getPath()));
                    }catch(Exception ex){ex.printStackTrace();}
                    }
                });
                return row;
            });
            try {
                Files.getItems().addAll(currentTorrent.getFiles());
            } catch (Exception ioobe) {
            }

            PieceDownloaded.setCellValueFactory(new PropertyValueFactory<>("DownloadedString"));
            PieceSize.setCellValueFactory(new PropertyValueFactory<>("LengthString"));
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
            trackers.getItems().clear();
            Pieces.getItems().clear();
            Files.getItems().clear();
            peers.getItems().clear();

            trackers.getItems().addAll(currentTorrent.getTrackers());
            Pieces.getItems().addAll(currentTorrent.getPieces());
            Files.getItems().addAll(currentTorrent.getFiles());
            peers.getItems().addAll(currentTorrent.getConnections());
            Torrents.getItems().addAll(NetworkController.getTorrents());
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
            else
                currentTorrent=null;
        }
    }
    public void pauseTorrent(ActionEvent e){
        if (currentTorrent!=null)
            currentTorrent.killThreads();
    }
    public void resumeTorrent(ActionEvent e){
        if (currentTorrent!=null)
            currentTorrent.invokeThreads();
    }
    public void setSequential(ActionEvent e){
        if (currentTorrent!=null)
            currentTorrent.setLinear(true);
    }
    public void setRandom(ActionEvent e){
        if (currentTorrent!=null)
            currentTorrent.setLinear(false);
    }
    public void addTorrentBtnPress(ActionEvent e){
        FileChooser chooser = new FileChooser();
        File dir = new File("files/");
        chooser.setInitialDirectory(dir);
        File fl = chooser.showOpenDialog(primaryStage);
        try {
            byte arr[] = TorrentFileReader.readFile(fl.getAbsolutePath());
            Torrent torrent = new Torrent(bCoder.decode(arr, ParcelType.TORRENT));
            if (NetworkController.checkIfTorrentExists(torrent.getInfoHash())!=null)
                return;
            NetworkController.addTorrent(torrent);
            torrent.invokeThreads();
            currentTorrent = torrent;
            Torrents.getItems().clear();
            Torrents.getItems().addAll(NetworkController.getTorrents());
        }catch(Exception ex){}
    }

    public void HandleClose(MouseEvent mouseEvent) {
        if(mouseEvent.getSource()==btnclose){
            System.exit(0);
        }
    }
}
