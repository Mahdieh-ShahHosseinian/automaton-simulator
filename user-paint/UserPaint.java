import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class UserPaint {

    private final int xRatio = 8, yRatio = 6, radius = 30;

    @FXML
    private BorderPane borderPane;

    @FXML
    private Pane paper;

    @FXML
    private Button plusCircle;

    @FXML
    private Button plusTransition;

    @FXML
    private HBox transHBox;

    @FXML
    private TextField getTransLabel;

    private DrawState drawState = DrawState.NON;

    private static int S = 0;
    private static int TR = 0;
    private final ArrayList<State> states = new ArrayList<>();
    private final ArrayList<Transition> transitions = new ArrayList<>();
    private final Set<Character> letters = new HashSet<>();
    private final ArrayList<State> finalStates = new ArrayList<>();
    private final ArrayList<Circle> circles = new ArrayList<>();
    private final ArrayList<TextField> textFields = new ArrayList<>();

    public void plusCircle() {

        plusCircle.setDisable(true);

        drawState = DrawState.STATE;

        AtomicBoolean initial = new AtomicBoolean(true);
        AtomicBoolean circleClicked = new AtomicBoolean(false);

        borderPane.addEventHandler(KeyEvent.KEY_PRESSED, event -> {

            if (event.getCode() == KeyCode.ESCAPE) {

                plusTransition.setDisable(false);
                plusTransition.requestFocus();
                drawState = DrawState.NON;

                paper.getChildren().removeAll(textFields);
                for (Circle circle : circles) {

                    Label stateName = new Label(states.get(circles.indexOf(circle)).getName());
                    stateName.setLayoutX(circle.getCenterX() - 7.5);
                    stateName.setLayoutY(circle.getCenterY() - 7.5);
                    paper.getChildren().add(stateName);
                }
            }
        });

        paper.setOnMouseClicked(mouseClickedEvent -> {

            paper.requestFocus();

            if (circleClicked.get()) {
                circleClicked.set(false);
            } else if (drawState == DrawState.STATE
                    && mouseClickedEvent.getX() > 35
                    && mouseClickedEvent.getX() < 1360
                    && mouseClickedEvent.getY() > 45
                    && mouseClickedEvent.getY() < 658) {

                Circle circle = new Circle(0, 0, radius, Color.WHITE);
                circles.add(circle);

                circle.setStroke(Color.BLACK);
                circle.setCenterX(mouseClickedEvent.getX());
                circle.setCenterY(mouseClickedEvent.getY());
                paper.getChildren().add(circle);

                State state = new State("", (int) circle.getCenterX(), (int) circle.getCenterY());
                if (initial.get()) {

                    state.setInitial();
                    Arrow arrow = new Arrow(state.getPositionX(), state.getPositionY(), state.getPositionX(), state.getPositionY());
                    paper.getChildren().addAll(arrow.getStartLine(), arrow.getArrowHead());

                    initial.set(false);
                }
                states.add(state);

                TextField text = new TextField();
                textFields.add(text);
                {
                    text.setPrefSize(25, 25);
                    text.setStyle("-fx-font: 12 arial; -fx-padding: 0.11117");
                    text.setLayoutX(circle.getCenterX() - 12);
                    text.setLayoutY(circle.getCenterY() - 12);
                    text.setPromptText("S" + S++);
                    state.setName(text.getPromptText());
                    text.lengthProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue.intValue() > oldValue.intValue()) {
                            if (text.getText().length() >= 2) {

                                text.setText(text.getText().substring(0, 2));
                                paper.requestFocus();
                            }
                        }
                        state.setName(text.getText());
                    });
                }
                paper.getChildren().add(text);

                circle.setOnMouseClicked(mouseEvent -> { // final state

                    circleClicked.set(true);

                    if (mouseEvent.getClickCount() == 2 && drawState == DrawState.STATE) {
                        state.setFinal();
                        finalStates.add(state);
                        Circle inner = new Circle(circle.getCenterX(), circle.getCenterY(), radius - 3, Color.WHITE);
                        circles.set(circles.indexOf(circle), inner);
                        inner.setStroke(Color.BLACK);
                        paper.getChildren().add(inner);
                        paper.getChildren().remove(text);
                        paper.getChildren().add(text);

                        inner.setOnMouseClicked(mouseEvent1 -> {
                            circleClicked.set(true);
                        });
                    }
                });
            }
        });
    }

    public void plusTrans() {

        plusTransition.setDisable(true);
        transHBox.setDisable(false);
        drawState = DrawState.TRANSITION;

        AtomicBoolean sourceIsChosen = new AtomicBoolean(false);
        AtomicBoolean destinationIsChosen = new AtomicBoolean(false);

        borderPane.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                plusTransition.setDisable(true);
                transHBox.setDisable(true);
                drawState = DrawState.NON;
            }
        });

        if (drawState == DrawState.TRANSITION) {

            AtomicReference<Double> sourceX = new AtomicReference<>();
            AtomicReference<Double> sourceY = new AtomicReference<>();
            AtomicInteger sourceStateIndex = new AtomicInteger();

            for (Circle circle : circles) {
                circle.setOnMouseClicked(mouseClicked -> {
                    if (!sourceIsChosen.get()) {

                        sourceX.set(circle.getCenterX());
                        sourceY.set(circle.getCenterY());
                        sourceStateIndex.set(circles.indexOf(circle));

                        destinationIsChosen.set(false);
                        sourceIsChosen.set(true);
                    } else if (!destinationIsChosen.get()) {

                        getTransLabel.clear();

                        boolean sameTrans = false;
                        for (Transition transition : transitions) {

                            if (transition.getSource().equals(states.get(sourceStateIndex.get())) &&
                                    transition.getDestination().equals(states.get(circles.indexOf(circle)))) {
                                sameTrans = true;
                                break;
                            }
                        }
                        if (!sameTrans) {

                            // loop
                            if (states.get(circles.indexOf(circle)).equals(states.get(sourceStateIndex.get()))) {

                                Arrow arrow = new Arrow(states.get(sourceStateIndex.get()).getPositionX()
                                        , states.get(sourceStateIndex.get()).getPositionY(), "");

                                paper.getChildren().remove(circle);
                                Label stateName = new Label(states.get(circles.indexOf(circle)).getName());
                                stateName.setLayoutX(circle.getCenterX() - 7.5);
                                stateName.setLayoutY(circle.getCenterY() - 7.5);

                                Transition transition = new Transition("TR" + TR++, states.get(sourceStateIndex.get()),
                                        states.get(circles.indexOf(circle)), "");
                                transitions.add(transition);

                                getTransLabel.setOnKeyPressed(inputMethodEvent -> {

                                    String input = inputMethodEvent.getText().toLowerCase();
                                    letters.add(input.charAt(0));
                                    if (!arrow.getLoopLabel().getText().contains(input)) {
                                        arrow.setLoopLabel(arrow.getLoopLabel().getText() +
                                                (arrow.getLoopLabel().getText().equals("") ? "" : ",") + input);
                                        transition.setLabel(arrow.getLoopLabel().getText());
                                        paper.getChildren().remove(arrow.getLoopLabel());
                                        paper.getChildren().add(arrow.getLoopLabel());
                                    }
                                    paper.requestFocus();
                                });
                                paper.getChildren().addAll(arrow.getLoop(), arrow.getLoopLabel(), circle, stateName);

                                // between 2 states
                            } else {

                                boolean isItTimeToReverseTheCurveOfTheTransition = false;
                                for (Transition transition : transitions) {

                                    if (transition.getSource().equals(states.get(circles.indexOf(circle)))
                                            && transition.getDestination().equals(states.get(sourceStateIndex.get()))) {
                                        isItTimeToReverseTheCurveOfTheTransition = true;
                                        break;
                                    }
                                }

                                Arrow arrow = new Arrow(sourceX.get(), sourceY.get(),
                                        states.get(circles.indexOf(circle)).getPositionX(),
                                        states.get(circles.indexOf(circle)).getPositionY(),
                                        "", isItTimeToReverseTheCurveOfTheTransition);

                                Transition transition = new Transition("TR" + TR++, states.get(sourceStateIndex.get()),
                                        states.get(circles.indexOf(circle)), "");
                                transitions.add(transition);

                                getTransLabel.setOnKeyPressed(inputMethodEvent -> {

                                    String input = inputMethodEvent.getText().toLowerCase();
                                    letters.add(input.charAt(0));
                                    if (!arrow.getArrowLabel().getText().contains(input)) {
                                        arrow.setArrowLabel(arrow.getArrowLabel().getText() +
                                                (arrow.getArrowLabel().getText().equals("") ? "" : ",") + input);
                                        transition.setLabel(arrow.getArrowLabel().getText());
                                        paper.getChildren().remove(arrow.getArrowLabel());
                                        paper.getChildren().add(arrow.getArrowLabel());
                                    }
                                    paper.requestFocus();
                                });
                                paper.getChildren().addAll(arrow.getArrowLine(), arrow.getArrowHead(), arrow.getArrowLabel());
                            }
                        }
                        destinationIsChosen.set(true);
                        sourceIsChosen.set(false);
                    }
                });
            }
        }
    }

    public void save() throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        //root elements
        Document doc = docBuilder.newDocument();

        Element root = doc.createElement("Automata");
        root.setAttribute("Type", "FA");
        doc.appendChild(root);

        Element numberOfAlphabets = doc.createElement("Alphabets");
        numberOfAlphabets.setAttribute("numberOfAlphabets", String.valueOf(letters.size()));
        root.appendChild(numberOfAlphabets);

        for (Character ch : letters) {
            Element alphabet = doc.createElement("alphabet");
            alphabet.setAttribute("letter", String.valueOf(ch));

            numberOfAlphabets.appendChild(alphabet);
        }

        Element numberOfStates = doc.createElement("States");
        numberOfStates.setAttribute("numberOfStates", String.valueOf(states.size()));
        root.appendChild(numberOfStates);

        State initial = null;
        for (State state : states) {
            if (state.isInitial()) initial = state;

            Element stateName = doc.createElement("state");
            stateName.setAttribute("name", state.getName());
            stateName.setAttribute("positionX", String.valueOf(state.getPositionX() / xRatio));
            stateName.setAttribute("positionY", String.valueOf(state.getPositionY() / yRatio));
            numberOfStates.appendChild(stateName);
        }

        Element initialState = doc.createElement("initialState");
        assert initial != null;
        initialState.setAttribute("name", initial.getName());
        numberOfStates.appendChild(initialState);

        Element numberOfFinalStates = doc.createElement("FinalStates");
        numberOfFinalStates.setAttribute("numberOfFinalStates", String.valueOf(finalStates.size()));
        numberOfStates.appendChild(numberOfFinalStates);

        for (State state : finalStates) {
            Element finalState = doc.createElement("finalState");
            finalState.setAttribute("name", state.getName());
            numberOfFinalStates.appendChild(finalState);
        }

        Element numberOfTrans = doc.createElement("Transitions");
        numberOfTrans.setAttribute("numberOfTrans", String.valueOf(transitions.size()));
        root.appendChild(numberOfTrans);

        for (Transition t : transitions) {
            Element transitionName = doc.createElement("transition");
            transitionName.setAttribute("name", String.valueOf(t.getName()));
            transitionName.setAttribute("source", String.valueOf(t.getSource().getName()));
            transitionName.setAttribute("destination", String.valueOf(t.getDestination().getName()));
            transitionName.setAttribute("label", String.valueOf(t.getLabel()));
            numberOfTrans.appendChild(transitionName);
        }

        // write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(System.getProperty("user.home") + "\\Documents\\save.xml"));

        transformer.transform(source, result);
    }
}

enum DrawState {
    NON, TRANSITION, STATE
}
