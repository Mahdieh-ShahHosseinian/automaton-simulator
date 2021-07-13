import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class SystemPaint {

    private final int xRatio = 8, yRatio = 6, radius = 30;

    @FXML
    public Button importButton;

    @FXML
    public Button drawButton;

    @FXML
    private TextArea showXML;

    @FXML
    private Pane border;

    private Automata automata;
    private File file;
    private File previousFile = null;

    public void fileChooser() throws FileNotFoundException { // Import Button

        drawButton.setDisable(true);
        showXML.clear();
        border.getChildren().clear();

        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML Files", "*.xml"));

        file = fileChooser.showOpenDialog(importButton.getScene().getWindow());

        if (file != null) { // for textArea
            drawButton.setDisable(false);
            Scanner s = new Scanner(file).useDelimiter("\\\\r?\\\\n");
            while (s.hasNext()) showXML.appendText(s.next());
            s.close();
        }
    }

    public void draw() {

        drawButton.setDisable(true);
        if (file != null && file != previousFile) {
            ReadXMLFile readXMLFile = new ReadXMLFile(file.getAbsolutePath());
            automata = readXMLFile.getAutomata();

            StringBuilder warningNames = new StringBuilder();
            for (State state : automata.getStates()) {
                if (state.getPositionX() * xRatio < 35 || state.getPositionX() * xRatio > 1360 ||
                        state.getPositionY() * yRatio < 45 || state.getPositionY() * yRatio > 658) {
                    warningNames.append(state.getName()).append(",");
                }
            }
            if (!warningNames.toString().equals("")) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Input Warning");
                alert.setHeaderText("OUT OF RANGE");
                alert.setContentText("The position of state " + warningNames + " is out of range!");
                alert.showAndWait();
            }
            drawTrans();
            drawStates();
            previousFile = file;
        }
    }

    private void drawStates() {

        for (State state : automata.getStates()) {

            Circle circle = new Circle(state.getPositionX() * xRatio, state.getPositionY() * yRatio, radius, Color.WHITE);
            circle.setStroke(Color.BLACK);
            border.getChildren().add(circle);

            if (state.isInitial()) {

                Arrow arrow = new Arrow(state.getPositionX() * xRatio, state.getPositionY() * yRatio,
                        state.getPositionX() * xRatio, state.getPositionY() * yRatio);
                border.getChildren().addAll(arrow.getStartLine(), arrow.getArrowHead());

            } else if (state.isFinal()) {
                Circle inner = new Circle(state.getPositionX() * xRatio, state.getPositionY() * yRatio, radius - 3, Color.WHITE);
                inner.setStroke(Color.BLACK);
                border.getChildren().add(inner);
            }

            Text name = new Text(state.getName());
            name.setLayoutX(state.getPositionX() * xRatio - 6);
            name.setLayoutY(state.getPositionY() * yRatio + 4);
            border.getChildren().add(name);
        }
    }

    private void drawTrans() {

        for (Transition transition : automata.getTransitions()) {

            if (transition.hasUsed()) continue;

            for (int i = automata.getTransitions().indexOf(transition) + 1; i < automata.getTransitions().size(); i++) {

                if (automata.getTransitions().get(i).getSource().equals(transition.getSource())
                        && automata.getTransitions().get(i).getDestination().equals(transition.getDestination())) {

                    transition.setLabel(transition.getLabel() + "," + automata.getTransitions().get(i).getLabel());
                    automata.getTransitions().get(i).setHasUsed(true);
                }
            }

            if (transition.getSource().equals(transition.getDestination())) {

                Arrow arrow = new Arrow(transition.getSource().getPositionX() * xRatio,
                        transition.getSource().getPositionY() * yRatio, transition.getLabel());

                border.getChildren().addAll(arrow.getLoop(), arrow.getLoopLabel());

            } else {

                boolean existSameTrans = false;
                for (int i = 0; i < automata.getTransitions().indexOf(transition); i++) {

                    if (automata.getTransitions().get(i).getSource().equals(transition.getDestination())
                            && automata.getTransitions().get(i).getDestination().equals(transition.getSource()))
                        existSameTrans = true;
                }

                Arrow arrow = new Arrow(transition.getSource().getPositionX() * xRatio,
                        transition.getSource().getPositionY() * yRatio,
                        transition.getDestination().getPositionX() * xRatio,
                        transition.getDestination().getPositionY() * yRatio,
                        transition.getLabel(), existSameTrans);

                border.getChildren().addAll(arrow.getArrowLine(), arrow.getArrowLabel(), arrow.getArrowHead());
            }
        }
    }
}