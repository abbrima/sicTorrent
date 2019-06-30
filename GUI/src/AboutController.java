import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AboutController implements Initializable {
    public static Stage promptStage;
    @FXML
    void cancelPrompt() {
        promptStage.close();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
