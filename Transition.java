public class Transition {

    private String name;
    private State source;
    private State destination;
    private String label;
    private boolean hasUsed = false;

    public Transition(String name, State source, State destination, String label) {
        this.name = name;
        this.source = source;
        this.destination = destination;
        this.label = label;
    }

    public State getSource() {
        return source;
    }

    public State getDestination() {
        return destination;
    }

    public String getLabel() {
        return label;
    }

    public String getName() {
        return name;
    }

    public boolean hasUsed() {
        return hasUsed;
    }

    public void setHasUsed(boolean hasUsed) {
        this.hasUsed = hasUsed;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
