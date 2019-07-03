import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class PopupController implements Initializable {

    public static String msg;
    public static Stage stage;
    @FXML private Label message;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        message.setText(msg);
        message.setTextAlignment(TextAlignment.CENTER);
    }
    @FXML public void close(){
        stage.close();
    }
}
