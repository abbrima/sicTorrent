
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;


import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;


public class Controller implements Initializable {
    @FXML private TableView<Tracker> trackers;
    @FXML private TableColumn<Tracker,String> trackersID;
    @FXML private TableColumn<Tracker,String> trackersStatus;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        trackersID.setCellValueFactory(new PropertyValueFactory<Tracker, String>("uri"));
        trackersStatus.setCellValueFactory(new PropertyValueFactory<Tracker, String>("Status"));
       try{ trackers.setItems(getTrackers());}catch(IndexOutOfBoundsException ioobe){}
    }
    public ObservableList<Tracker>  getTrackers() throws IndexOutOfBoundsException
    {
        ObservableList<Tracker> trackers = FXCollections.observableArrayList();
        trackers.addAll(NetworkController.getTorrents().get(0).getTrackers());
        return trackers;
    }
}
