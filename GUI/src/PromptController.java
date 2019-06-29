import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;


public class PromptController implements Initializable {

    public static Stage promptStage;
    @FXML
    private TextField ipPrompt;
    @FXML
    private TextField portPrompt;


    @FXML
    void cancelPrompt() {
        promptStage.close();
    }

    @FXML
    void addPrompt() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
