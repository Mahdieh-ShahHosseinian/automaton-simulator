import java.util.ArrayList;

public class Automata {

    private String type;
    private int numberOfAlphabets;
    private final ArrayList<Character> letters = new ArrayList<>(numberOfAlphabets);
    private int numberOfStates;
    private final ArrayList<State> states = new ArrayList<>(numberOfStates);
    private State initialState;
    private int numberOfFinalStates;
    private final ArrayList<State> finalStates = new ArrayList<>(numberOfFinalStates);
    private int numberOfTrans;
    private final ArrayList<Transition> transitions = new ArrayList<>(numberOfTrans);

    private String initialStateStr;
    private final ArrayList<String> finalStateStr = new ArrayList<>();
    private final ArrayList<String> transName = new ArrayList<>();
    private final ArrayList<String> transSource = new ArrayList<>();
    private final ArrayList<String> transDestination = new ArrayList<>();
    private final ArrayList<String> transLabel = new ArrayList<>();

    private State findState(String str) {
        for (State s : states) if (s.getName().equals(str)) return s;
        return null;
    }

    public void addToStates(String n, int x, int y) {
        states.add(new State(n, x, y));
    }

    public void addToTrans(String name, String source, String destination, String label) {

        transName.add(name);
        transSource.add(source);
        transDestination.add(destination);
        transLabel.add(label);
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setNumberOfAlphabets(int numberOfAlphabets) {
        this.numberOfAlphabets = numberOfAlphabets;
    }

    public void addToLetters(Character letter) {
        letters.add(letter);
    }

    public void setNumberOfStates(int numberOfStates) {
        this.numberOfStates = numberOfStates;
    }

    public void setInitialState(String str) {
        initialStateStr = str;
    }

    public void addToFinalStates(String str) {
        finalStateStr.add(str);
    }

    public void setNumberOfTrans(int numberOfTrans) {
        this.numberOfTrans = numberOfTrans;
    }

    public ArrayList<State> getStates() {
        return states;
    }

    public void setNumberOfFinalStates(int numberOfFinalStates) {
        this.numberOfFinalStates = numberOfFinalStates;
    }

    public ArrayList<Transition> getTransitions() {
        return transitions;
    }

    public void makeThingsRight() {

        initialState = findState(initialStateStr);
        assert initialState != null;
        initialState.setInitial();

        for (String str : finalStateStr) {

            State finalState = findState(str);
            assert finalState != null;
            finalState.setFinal();
            finalStates.add(finalState);
        }

        for (int i = 0; i < transName.size(); i++) {

            transitions.add(new Transition(transName.get(i), findState(transSource.get(i)),
                    findState(transDestination.get(i)), transLabel.get(i)));
        }
    }
}
