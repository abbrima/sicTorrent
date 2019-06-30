import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AboutController implements Initializable {
    public static Stage promptStage;
    @FXML
    public Text VersionText;
    @FXML
    void cancelPrompt() {
        promptStage.close();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        VersionText.setText(Info.version.charAt(0) + "." + Info.version.substring(1));
    }
}
