public class State {

    private boolean isInitial = false;
    private boolean isFinal = false;
    private String name;
    private final int positionX;
    private final int positionY;

    public State(String n, int x, int y) {
        name = n;
        positionX = x;
        positionY = y;
    }

    public String getName() {
        return name;
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setInitial() {
        isInitial = true;
    }

    public void setFinal() {
        isFinal = true;
    }

    public boolean isInitial() {
        return isInitial;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setName(String name) {
        this.name = name;
    }
}
