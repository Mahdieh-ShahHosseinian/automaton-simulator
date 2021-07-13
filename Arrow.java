import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;

public class Arrow {

    private final int radius = 30;

    private Color transColour = Color.HOTPINK;
    private Circle loop;
    private Text loopLabel;
    private QuadCurve arrowLine;
    private Text arrowLabel;
    private Path arrowHead;
    private Line startLine;

    // QuadCurve between two different state
    public Arrow(double startX, double startY, double endX, double endY, String label, boolean existsSameTrans) {

        double slope = (endX == startX) ? 1 : Math.abs((endY - startY) / (endX - startX));
        double deltaX = (endX == startX) ? 0 : Math.sqrt(Math.pow(radius, 2) / (Math.pow(slope, 2) + 1));
        double deltaY = (endX == startX) ? radius : slope * deltaX;

        double startLineX = (startX > endX) ? startX - deltaX : startX + deltaX;
        double endLineX = (endX > startX) ? endX - deltaX : endX + deltaX;
        double startLineY = (startY > endY) ? startY - deltaY : startY + deltaY;
        double endLineY = (endY > startY) ? endY - deltaY : endY + deltaY;

        arrowLine = new QuadCurve(startLineX, startLineY, 0.0f, 0.0f, endLineX, endLineY);

        int curveControl = 100;

        if (existsSameTrans) {
            transColour = Color.ROYALBLUE;
            curveControl = -100;
        }

        arrowLine.controlXProperty().bind((arrowLine.startXProperty().add(arrowLine.endXProperty()).divide(2)));
        arrowLine.controlYProperty().bind((arrowLine.startYProperty().add(arrowLine.endYProperty()).divide(2)).add(curveControl));

        arrowLine.setStroke(Color.BLACK);
        arrowLine.setStrokeWidth(1);
        arrowLine.setFill(null);

        createArrowHead(slope, startLineX, startLineY, endLineX, endLineY);

        arrowLabel = new Text(label + "");
        arrowLabel.setLayoutX((arrowLine.getStartX() + arrowLine.getEndX() + arrowLine.getControlX()) / 3);
        arrowLabel.setLayoutY((arrowLine.getStartY() + arrowLine.getEndY() + arrowLine.getControlY()) / 3);

        arrowLine.setStroke(transColour);
        arrowLabel.setFill(transColour);
    }

    //Circle for loop
    public Arrow(double startX, double startY, String label) {

        loop = new Circle(radius - 5);
        loop.setCenterX(startX - 17);
        loop.setCenterY(startY - 17);
        loop.setStroke(Color.BLACK);
        loop.setStrokeWidth(1);
        loop.setFill(null);
        loop.setStroke(Color.FIREBRICK);

        loopLabel = new Text(label + "");
        loopLabel.setFill(Color.FIREBRICK);
        loopLabel.setLayoutX(loop.getCenterX() - 10);
        loopLabel.setLayoutY(loop.getCenterY() - 8);
    }

    //Line for start state
    public Arrow(double startX, double startY, double endX, double endY) {

        startX -= 2 * radius;
        endX -= radius;

        startLine = new Line();
        startLine.setStartY(startY);
        startLine.setEndY(startY);
        startLine.setStartX(startX);
        startLine.setEndX(endX);

        startLine.setStroke(transColour);
        createArrowHead(0, startX, startY, endX, endY);
    }

    private void createArrowHead(double slope, double startLineX, double startLineY, double endLineX, double endLineY) {
        arrowHead = new Path();
        double arrowHeadSize = 12.0;
        startLineY -= slope;

        //ArrowHead
        double angle = Math.atan2((endLineY - startLineY), (endLineX - startLineX)) - Math.PI / 2.0;
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        //point1
        double x1 = (-1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * arrowHeadSize + endLineX;
        double y1 = (-1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * arrowHeadSize + endLineY;
        //point2
        double x2 = (1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * arrowHeadSize + endLineX;
        double y2 = (1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * arrowHeadSize + endLineY;

        arrowHead.getElements().add(new MoveTo(x1, y1));
        arrowHead.getElements().add(new LineTo(endLineX, endLineY));
        arrowHead.getElements().add(new LineTo(x2, y2));

        arrowHead.setStroke(transColour);
    }

    public Circle getLoop() {
        return loop;
    }

    public Text getLoopLabel() {
        return loopLabel;
    }

    public void setLoopLabel(String labelStr) {
        loopLabel.setText(labelStr);
    }

    public Path getArrowHead() {
        return arrowHead;
    }

    public QuadCurve getArrowLine() {
        return arrowLine;
    }

    public Text getArrowLabel() {
        return arrowLabel;
    }

    public Line getStartLine() {
        return startLine;
    }

    public void setArrowLabel(String labelStr) {
        arrowLabel.setText(labelStr);
    }
}

