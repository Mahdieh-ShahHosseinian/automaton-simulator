import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Options implements Initializable {

    @FXML
    private BorderPane rootPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void loadSystemPaint() throws IOException { // Import Button
        BorderPane pane = FXMLLoader.load(getClass().getResource("systemPaint.fxml"));
        rootPane.getChildren().clear();
        rootPane.getChildren().addAll(pane);
    }

    @FXML
    private void loadUserPaint() throws IOException { // Draw Button
        BorderPane pane = FXMLLoader.load(getClass().getResource("userPaint.fxml"));
        rootPane.getChildren().clear();
        rootPane.getChildren().addAll(pane);
    }
}
