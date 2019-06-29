import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.InetAddress;
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
        try {
            InetAddress address;
            address = InetAddress.getByName(ipPrompt.getText());
            int port = Integer.parseInt(portPrompt.getText());
            if (port<0 || port > 65535)
                throw new Exception();
            Controller.currentTorrent.getConnections().add(new Connection(Controller.currentTorrent,address.getHostName(),port));
            promptStage.close();
        }catch(Exception e){System.out.println("FAILED");}
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
