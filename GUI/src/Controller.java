
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import java.util.Timer;
import java.util.TimerTask;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.print.attribute.SetOfIntegerSyntax;
import javax.sound.midi.Track;


public class Controller implements Initializable {
    @FXML private TableView<Tracker> trackers;
    @FXML private TableColumn<Tracker,String> trackersID;
    @FXML private TableColumn<Tracker,String> trackersStatus;
    @FXML private Button Refresh;
    @FXML ObservableList<Tracker> tracker;


    @FXML private TableView<DownloadFile> Files;
    @FXML private TableColumn<DownloadFile,String> FileName;
    @FXML private TableColumn<DownloadFile,Long> FileSize;
    @FXML private TableColumn<DownloadFile,String> FileStatus;
    @FXML ObservableList<DownloadFile> FileTable;

    @FXML private TableView<Piece> Pieces;
    @FXML private TableColumn<Piece, Integer> PieceDownloaded;
    @FXML private TableColumn<Piece,Integer> PieceSize;
    @FXML private TableColumn<Piece,String> PieceStatus;
    @FXML private TableColumn<Piece,Integer> PieceIndex;
    @FXML ObservableList<Piece> PieceTable;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        trackersID.setCellValueFactory(new PropertyValueFactory<Tracker, String>("uri"));
        trackersStatus.setCellValueFactory(new PropertyValueFactory<Tracker, String>("Status"));
       try{ trackers.setItems(getTrackers());}catch(IndexOutOfBoundsException ioobe){}


        FileName.setCellValueFactory(new PropertyValueFactory<DownloadFile, String>("path"));
        FileSize.setCellValueFactory(new PropertyValueFactory<DownloadFile, Long>("length"));
        FileStatus.setCellValueFactory(new PropertyValueFactory<DownloadFile, String>("status"));
        try{Files.setItems(getFileInfo());}catch(IndexOutOfBoundsException ioobe){}

        PieceDownloaded.setCellValueFactory(new PropertyValueFactory<Piece,Integer>("downloaded"));
        PieceSize.setCellValueFactory(new PropertyValueFactory<Piece, Integer>("length"));
        PieceStatus.setCellValueFactory(new PropertyValueFactory<Piece, String>("status"));
        PieceIndex.setCellValueFactory(new PropertyValueFactory<Piece, Integer>("index"));
        try{Pieces.setItems(getPieceInfo());}catch(IndexOutOfBoundsException ioobe){}

        RefreshTimer T= new RefreshTimer();
        T.start();

    }

    public void setRefresh() {

            trackers.refresh();
            Pieces.refresh();;
            Files.refresh();

    }

    public ObservableList<Tracker>  getTrackers() throws IndexOutOfBoundsException
    {
        tracker = FXCollections.observableArrayList();
        tracker.addAll(NetworkController.getTorrents().get(0).getTrackers());
        return  tracker;


    }


    public ObservableList<DownloadFile>  getFileInfo() throws IndexOutOfBoundsException
    {
        FileTable = FXCollections.observableArrayList();
        FileTable.addAll(NetworkController.getTorrents().get(0).getFiles());
        return  FileTable;

    }
    public ObservableList<Piece>  getPieceInfo() throws IndexOutOfBoundsException
    {
        PieceTable = FXCollections.observableArrayList();
        PieceTable.addAll(NetworkController.getTorrents().get(0).getPieces());
        return  PieceTable;

    }
   public class RefreshTimer {
        private Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                setRefresh();

            }
        };


        public void start() {
            timer.scheduleAtFixedRate(task, 0, 2000);
        }
    }


}
