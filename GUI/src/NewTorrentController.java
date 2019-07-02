import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class NewTorrentController implements Initializable {
    public static Stage promptStage;
    public static Torrent torrent;
    public static boolean add=false;
    @FXML public TextField Name;
    @FXML public TextField Infohash;
    @FXML public TextField Size;
    @FXML public TextField FileCount;
    @FXML public CheckBox box;

    @FXML
    void cancelPrompt() {
        add=false;
        promptStage.close();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        add=false;
        Name.setText(torrent.getName());
        Infohash.setText(Funcs.byteArrayToHex(torrent.getInfoHash()).toUpperCase());
        Size.setText(Funcs.lengthToStr(torrent.getLength()));
        FileCount.setText(Integer.toString(torrent.getFiles().size()));
        box.setSelected(torrent.getLinear());
    }
    @FXML void addTorrent(){
        if (Name.getText().trim().length() != 0)
        {
                torrent.setName(Name.getText());
                torrent.setLinear(box.isSelected());
                add=true;
                promptStage.close();
        }
    }
}
