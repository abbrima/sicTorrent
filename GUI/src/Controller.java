import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.awt.*;
import java.io.File;
import java.util.*;

import java.net.URL;

import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.*;

public class Controller implements Initializable {

    void setSidePanelButtonBackground(Button btn,boolean enable){
        if (enable)
             btn.setStyle("-fx-background-color:  #FF9800;");
        else
             btn.setStyle("-fx-background-color:  #EF6C00;");
    }
    void setTorrentControlButtons(TorrentStatus status){
        if (status == TorrentStatus.ACTIVE)
        {
            ResumeTorrentButton.setDisable(true);
            PauseTorrentButton.setDisable(false);
        }
        else{
            ResumeTorrentButton.setDisable(false);
            PauseTorrentButton.setDisable(true);
        }
    }
    public static Stage primaryStage;
    @FXML
    private TableView<Tracker> trackers;
    @FXML
    private TableColumn<Tracker, String> trackersID;
    @FXML
    private TableColumn<Tracker, String> trackersStatus;
    @FXML
    private Button Refresh;
    private static ArrayList<Long> downloadeds = new ArrayList<>();

    @FXML Button BrowseButton;
    @FXML TextField DownloadPath;

    @FXML private Button PauseTorrentButton;
    @FXML private Button ResumeTorrentButton;

    @FXML
    private TableView<DownloadFile> Files;
    @FXML
    private TableColumn<DownloadFile, String> FileName;
    @FXML
    private TableColumn<DownloadFile, String> FileSize;
    @FXML
    private TableColumn<DownloadFile, String> FileDownloaded;
    @FXML
    private TableColumn<DownloadFile, String> FileStatuss;

    @FXML
    private TableView<Torrent> Torrents;
    @FXML
    private TableColumn<Torrent, String> TorrentName;
    @FXML
    private TableColumn<Torrent, String> TorrentSize;
    @FXML
    private TableColumn<Torrent, String> Torrentstatus;
    @FXML
    private TableColumn<Torrent, String> TorrentDownloaded;
    @FXML
    private TableColumn<Torrent, String> TorrentUploaded;

    private ContextMenu TorrentContextMenu;
    private Menu DownloadMode;
    private Menu DownLimit;
    private Menu UpLimit;
    private Menu DeleteMenu;

    private CheckMenuItem _32d, _64d, _128d, _256d, _Unlimitedd;
    private CheckMenuItem _32u, _64u, _128u, _256u, _Unlimitedu;
    private CheckMenuItem downLimitarr[] = new CheckMenuItem[5];
    private CheckMenuItem upLimitarr[] = new CheckMenuItem[5];

    private ContextMenu PeersMenu;
    private MenuItem AddPeerMenuItem;

    private MenuItem DeleteWithData;
    private MenuItem Delete;
    private CheckMenuItem SequentialMode;
    private CheckMenuItem RandomMode;

    @FXML
    private TableView<Piece> Pieces;
    @FXML
    private TableColumn<Piece, String> PieceDownloaded;
    @FXML
    private TableColumn<Piece, String> PieceSize;
    @FXML
    private TableColumn<Piece, String> PieceStatus;
    @FXML
    private TableColumn<Piece, Integer> PieceIndex;

    @FXML
    private TableView<Connection> peers;
    @FXML
    private TableColumn<Connection, String> PeerID;
    @FXML
    private TableColumn<Connection, String> PeerStatus;
    @FXML
    private TableColumn<Connection, String> PeerDebug;

    @FXML
    private Pane paneStatus;

    @FXML
    private Button btnTorrents;
    @FXML
    private Button addTorrentBtn;

    @FXML
    private Button btnSettings;

    @FXML
    private Button btnDelete;

    @FXML
    private Label speedlbl;

    @FXML
    private Button btnAddTorrent;


    @FXML
    private Label labelStatus;


    @FXML
    private Button btnclose;

    @FXML
    private Pane settGrid;

    @FXML
    private GridPane torrnetGrid;

    public static Torrent currentTorrent;
    private static DownloadFile currentFile;
    private ContextMenu FilesMenu;
    private CheckMenuItem Download;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ResumeTorrentButton.setDisable(true);
        PauseTorrentButton.setDisable(true);
       // DownloadPath.setEditable(false);
       // DownloadPath.setText(Parameters.downloadDir);
        if (NetworkController.getTorrents().size() > 0)
        {
            currentTorrent = NetworkController.getTorrents().get(0);
            setTorrentControlButtons(currentTorrent.getStatus());
        }

        {
            TorrentContextMenu = new ContextMenu();
            TorrentContextMenu.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                TorrentContextMenu.hide();
            });
            _32d = new CheckMenuItem("32 KB/s");
            _64d = new CheckMenuItem("64 KB/s");
            _128d = new CheckMenuItem("128 KB/s");
            _Unlimitedd = new CheckMenuItem("Unlimited");
            _256d = new CheckMenuItem("256 KB/s");
            _32u = new CheckMenuItem("32 KB/s");
            _64u = new CheckMenuItem("64 KB/s");
            _128u = new CheckMenuItem("128 KB/s");
            _Unlimitedu = new CheckMenuItem("Unlimited");
            _256u = new CheckMenuItem("256 KB/s");

            downLimitarr[0] = _32d; downLimitarr[1] = _64d; downLimitarr[2] = _128d; downLimitarr[3] = _256d; downLimitarr[4] = _Unlimitedd;
            upLimitarr[0] = _32u;   upLimitarr[1] = _64u;   upLimitarr[2]   = _128u; upLimitarr[3]   = _256u; upLimitarr[4]   = _Unlimitedu;

            DownLimit = new Menu("Downstream Limit");
            DownLimit.getItems().addAll(_32d, _64d, _128d, _256d, _Unlimitedd);
            UpLimit = new Menu("Upstream Limit");
            UpLimit.getItems().addAll(_32u, _64u, _128u, _256u, _Unlimitedu);
            SequentialMode = new CheckMenuItem("Sequential");
            RandomMode = new CheckMenuItem("Random");
            Delete = new MenuItem("Delete Torrent Only");
            DeleteWithData = new MenuItem("Delete With Data");
            DownloadMode = new Menu("Download Mode");
            DownloadMode.getItems().addAll(SequentialMode, RandomMode);
            DeleteMenu = new Menu("Delete");
            DeleteMenu.getItems().addAll(Delete, DeleteWithData);
            TorrentContextMenu.getItems().addAll(DeleteMenu, DownLimit, UpLimit, DownloadMode);
            DeleteWithData.setOnAction(e -> {
                deleteTorrent(true);
            });
            Delete.setOnAction(e -> {
                deleteTorrent(false);
            });
            SequentialMode.setOnAction(e -> {
                setSequential(e);
            });
            RandomMode.setOnAction(e -> {
                setRandom(e);
            });
            _32d.setOnAction(e -> {
                currentTorrent.setDownLimit(32);
            });
            _64d.setOnAction(e -> {
                currentTorrent.setDownLimit(64);
            });
            _128d.setOnAction(e -> {
                currentTorrent.setDownLimit(128);
            });
            _256d.setOnAction(e -> {
                currentTorrent.setDownLimit(256);
            });
            _Unlimitedd.setOnAction(e -> {
                currentTorrent.setDownLimit(-1);
            });

            _32u.setOnAction(e -> {
                currentTorrent.setUpLimit(32);
            });
            _64u.setOnAction(e -> {
                currentTorrent.setUpLimit(64);
            });
            _128u.setOnAction(e -> {
                currentTorrent.setUpLimit(128);
            });
            _256u.setOnAction(e -> {
                currentTorrent.setUpLimit(256);
            });
            _Unlimitedu.setOnAction(e -> {
                currentTorrent.setUpLimit(-1);
            });
            TorrentName.setCellValueFactory(new PropertyValueFactory<>("name"));
            TorrentDownloaded.setCellValueFactory(new PropertyValueFactory<>("DownloadedString"));
            TorrentUploaded.setCellValueFactory(new PropertyValueFactory<>("UploadedString"));
            TorrentSize.setCellValueFactory(new PropertyValueFactory<>("LengthString"));
            Torrentstatus.setCellValueFactory(new PropertyValueFactory<>("progress"));
            Torrents.setRowFactory(e -> {
                TableRow<Torrent> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 1 && event.getButton().equals(MouseButton.PRIMARY) && row.getItem() != null)
                    { currentTorrent = row.getItem();            setTorrentControlButtons(currentTorrent.getStatus());
                    }
                    if (event.getClickCount() == 1 && event.getButton().equals(MouseButton.SECONDARY) && row.getItem() != null) {

                            currentTorrent = row.getItem();
                            setTorrentControlButtons(currentTorrent.getStatus());
                            SequentialMode.setSelected(row.getItem().getLinear());
                            RandomMode.setSelected(!row.getItem().getLinear());
                            for (CheckMenuItem item:downLimitarr)
                                item.setSelected(false);
                            for (CheckMenuItem item:upLimitarr)
                                item.setSelected(false);
                            switch(row.getItem().getBandwidthcontroller().getDown()){
                                case 32:
                                    _32d.setSelected(true);
                                    break;
                                case 64:
                                    _64d.setSelected(true); break;
                                case 128:
                                    _128d.setSelected(true); break;
                                case 256:
                                    _256d.setSelected(true); break;
                                case -1:
                                    _Unlimitedd.setSelected(true); break;
                            }
                            switch(row.getItem().getBandwidthcontroller().getUp()){
                                case 32:
                                    _32u.setSelected(true); break;
                                case 64:
                                    _64u.setSelected(true); break;
                                case 128:
                                    _128u.setSelected(true); break;
                                case 256:
                                    _256u.setSelected(true); break;
                                case -1:
                                    _Unlimitedu.setSelected(true); break;
                            }
                            TorrentContextMenu.show(row, event.getScreenX(), event.getScreenY());
                    }
                });
                return row;
            });
            //TorrentStatus.setCellFactory(ProgressBarTableCell.<Torrent> forTableColumn());
            try {
                if (NetworkController.getTorrents().size() > 0)
                    Torrents.getItems().addAll(NetworkController.getTorrents());
            } catch (Exception ioobe) {
                Torrents.getItems().clear();
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
            PeersMenu = new ContextMenu();
            AddPeerMenuItem = new MenuItem("Add Peer");
            AddPeerMenuItem.setOnAction(e -> addPeer());
            PeersMenu.getItems().add(AddPeerMenuItem);
            PeersMenu.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                PeersMenu.hide();
            });
            peers.setRowFactory(e -> {
                TableRow<Connection> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 1 && event.getButton().equals(MouseButton.SECONDARY))
                        PeersMenu.show(row, event.getScreenX(), event.getScreenY());
                });
                return row;
            });

            FilesMenu = new ContextMenu();
            Download = new CheckMenuItem("Download");
            FilesMenu.getItems().addAll(Download);
            FilesMenu.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                FilesMenu.hide();
            });
            Download.setOnAction(e -> {
                if (currentFile.getStatus()!=FileStatus.DOWNLOADED) {
                    if (Download.isSelected())
                        currentTorrent.doNotDownload(currentFile);
                    else
                        currentTorrent.downloadFile(currentFile);
                }
            });

            setSidePanelButtonBackground(btnTorrents,true);

            FileName.setCellValueFactory(new PropertyValueFactory<DownloadFile, String>("path"));
            FileSize.setCellValueFactory(new PropertyValueFactory<>("LengthString"));
            FileStatuss.setCellValueFactory(new PropertyValueFactory<DownloadFile, String>("status"));
            FileDownloaded.setCellValueFactory(new PropertyValueFactory<>("DownloadedString"));
            Files.setRowFactory(e -> {
                TableRow<DownloadFile> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 1 && event.getButton().equals(MouseButton.SECONDARY)
                            && row.getItem() != null) {
                        currentFile = row.getItem();
                        Download.setSelected(row.getItem().getStatus()!=FileStatus.DONOTDOWNLOAD);
                        FilesMenu.show(row, event.getScreenX(), event.getScreenY());
                    } else if (event.getClickCount() == 2 && event.getButton().equals(MouseButton.PRIMARY) &&
                            row.getItem() != null && row.getItem().getStatus() == FileStatus.DOWNLOADED) {
                        Desktop desktop = Desktop.getDesktop();
                        try {
                            desktop.open(new File(Parameters.downloadDir + row.getItem().getPath()));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
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
        RefreshTimer T = new RefreshTimer();
        T.start();
    }

    public void setRefresh() {
        try {
            if (downloadeds.size() == NetworkController.getTorrents().size()) {
                long data = 0;
                for (int i = 0; i < downloadeds.size(); i++) {
                    data += NetworkController.getTorrents().get(i).getDownloaded() - downloadeds.get(i);
                }
                double speed = (double) data / 1024.0;
                Platform.runLater(() -> speedlbl.setText(String.format("%.2f KB/s", speed)));
            }
            downloadeds.clear();
            for (Torrent t : NetworkController.getTorrents())
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
        } catch (Exception ioobe) {
        }
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
    private void Handleclicks(ActionEvent event) {

        if (event.getSource() == btnTorrents) {
            labelStatus.setText("Torrents");
            setSidePanelButtonBackground(btnTorrents,true);
            setSidePanelButtonBackground(btnSettings,false);
            //paneStatus.setBackground(new Background(new BackgroundFill(Color.rgb(0, 80, 58), CornerRadii.EMPTY, Insets.EMPTY)));
            torrnetGrid.toFront();
        } else if (event.getSource() == btnSettings) {
            labelStatus.setText("Settings");
            setSidePanelButtonBackground(btnTorrents,false);
            setSidePanelButtonBackground(btnSettings,true);
            //paneStatus.setBackground(new Background(new BackgroundFill(Color.rgb(170, 0, 14), CornerRadii.EMPTY, Insets.EMPTY)));
            settGrid.toFront();
        }
    }

    public void deleteTorrent(boolean delete) {

        if (currentTorrent != null) {
            currentTorrent.killThreads();
            try {
                if (delete == true) {
                    currentTorrent.deleteFiles();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            NetworkController.getTorrents().remove(currentTorrent);
            if (NetworkController.getTorrents().size() > 0) {
                currentTorrent = NetworkController.getTorrents().get(0);
                setTorrentControlButtons(currentTorrent.getStatus());
            }
            else
            {currentTorrent = null;
            PauseTorrentButton.setDisable(true);
            ResumeTorrentButton.setDisable(true);
            }
        }
    }

    public void pauseTorrent(ActionEvent e) {
        if (currentTorrent != null)
        {
            currentTorrent.killThreads();
            setTorrentControlButtons(currentTorrent.getStatus());
        }
    }

    public void resumeTorrent(ActionEvent e) {
        if (currentTorrent != null)
        {
            currentTorrent.invokeThreads();
            setTorrentControlButtons(currentTorrent.getStatus());
        }
    }

    public void setSequential(ActionEvent e) {
        if (currentTorrent != null)
            currentTorrent.setLinear(true);
    }

    public void setRandom(ActionEvent e) {
        if (currentTorrent != null)
            currentTorrent.setLinear(false);
    }

    public void addTorrentBtnPress(ActionEvent e) {
        FileChooser chooser = new FileChooser();
        File dir = new File("files/");
        if (dir.exists())
            chooser.setInitialDirectory(dir);
        File fl = chooser.showOpenDialog(primaryStage);

        try {
            byte arr[] = TorrentFileReader.readFile(fl.getAbsolutePath());
            Torrent torrent = new Torrent(bCoder.decode(arr, ParcelType.TORRENT));
            if (NetworkController.checkIfTorrentExists(torrent.getInfoHash()) != null)
                return;
            NetworkController.addTorrent(torrent);
            torrent.invokeThreads();
            currentTorrent = torrent;
            setTorrentControlButtons(currentTorrent.getStatus());
            Torrents.getItems().clear();
            Torrents.getItems().addAll(NetworkController.getTorrents());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    private void addPeer() {

        new Thread(new Task() {
            protected Object call() {
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("NewPeerPrompt.fxml"));
                    Platform.runLater(()->{
                    Stage prompt = new Stage();
                        prompt.initStyle(StageStyle.UNDECORATED);

                        Scene scene = new Scene(root);
                    prompt.setScene(scene);
                    prompt.initModality(Modality.APPLICATION_MODAL);
                        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
                        prompt.show();
                        prompt.setX((primScreenBounds.getWidth() - prompt.getWidth()) / 2);
                        prompt.setY((primScreenBounds.getHeight() - prompt.getHeight()) / 2);
                        PromptController.promptStage = prompt;
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }).start();
    }

    @FXML void BrowseButtonClicked(){
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setInitialDirectory(new File(Parameters.downloadDir));
        try{
            File fl = chooser.showDialog(primaryStage);
            if (fl.exists() && fl.canExecute() && fl.canRead() && fl.canWrite())
            {
                Parameters.downloadDir = fl.getPath();
                DownloadPath.setText(fl.getPath());
            }
        }catch(Exception e){e.printStackTrace();}
    }
}
