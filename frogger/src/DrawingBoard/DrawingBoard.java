package DrawingBoard;

import Constants.Constants;
import Editor.EditorInterface;
import Frog.Frog;
import Header.ConsoleInterface;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

public class DrawingBoard extends StackPane implements DrawingBoardInteface {

    // CONSTANTS
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color FROG_COLOR = Color.LIGHTGREEN;

    private final GraphicsContext backroundGC; // Graphics context for drawing
    private final Canvas backroundCanvas;
    private final GraphicsContext frogGC;
    private final Canvas frogCanvas;

    private final Frog frog;

    private ConsoleInterface consoleInterface;
    private EditorInterface editorListener;

    public DrawingBoard() {

        // Setup canvas and gc
        this.backroundCanvas = new Canvas(Constants.DEFAULT_DRAWBOX_WIDTH, Constants.DEFAULT_DRAWBOX_HEIGHT);
        this.backroundGC = backroundCanvas.getGraphicsContext2D();

        this.frogCanvas = new Canvas(Constants.DEFAULT_DRAWBOX_WIDTH, Constants.DEFAULT_DRAWBOX_HEIGHT);
        this.frogGC = frogCanvas.getGraphicsContext2D();

        this.drawBackground();

        this.frog = new Frog(Constants.DEFAULT_DRAWBOX_WIDTH / 2f, Constants.DEFAULT_DRAWBOX_HEIGHT / 2f, 0, 20, Constants.FROG_WIDTH, Constants.FROG_HEIGHT, Constants.DEFAULT_DRAWBOX_WIDTH, Constants.DEFAULT_DRAWBOX_HEIGHT);

        this.drawFrog();

        this.getChildren().addAll(backroundCanvas, frogCanvas);
    }

    // Move the Frog smoothly with Timeline
    public boolean moveFrog(String command) {
        // later add another command parse maybe?
        if (command.startsWith("rotate")) {
            String[] cmds = command.split(" ");
            double radians = Math.toRadians(Double.parseDouble(cmds[1]));
            smoothRotate(radians);

            return true;
        }

        if (command.equals("reset")) {
            this.frog.moveMiddle();
            this.frog.setAngle(0);
            smoothMove(0, 0);
            this.drawBackground();
            return true;
        }

        float delta = 0;
        String dir = "down";

        String[] cmds = command.split(" ");

        if (cmds[0].startsWith("pen")) {
            dir = cmds[1];
        } else if (cmds[0].startsWith("color")) {
            dir = cmds[1];
        } else {
            try {
                delta = Float.parseFloat(cmds[1]);
            } catch (Exception e) {
                this.commandUnknown(command);
                return false;
            }
        }


        // Define movement deltas
        float deltaX = 0;
        float deltaY = 0;

        switch (cmds[0]) {
            case "forward":
                deltaY = -delta;
                break;
            case "backward":
                deltaY = delta;
                break;
            case "left":
                deltaX = -delta;
                break;
            case "right":
                deltaX = delta;
                break;
            case "speed":
                if (delta > 10000 || delta < 0.1) {
                    this.consoleInterface.setConsoleText("Speed must be between 0.1 and 10000");
                    this.editorListener.stopCode();
                    return false;
                }
                this.frog.setSpeed(delta);
                break;
            case "pen":
                this.frog.setPen(dir);
                break;
            case "color":
                try {
                    this.frog.setColor(Color.web(dir));
                    System.out.println(frog.getColor());
                } catch (Exception e) {
                    this.commandUnknown(command);
                }
                break;
            default:
                this.commandUnknown(cmds[0]);
                return false;
        }

        // Smoothly animate the movement
        smoothMove(deltaX, deltaY);

        return true;
    }

    private void commandUnknown(String command) {
        //System.out.println("Unknown command: " + command);
        this.consoleInterface.setConsoleText("Unknown command + " + command);
        this.editorListener.stopCode();
    }

    // Smooth move logic with Timeline
    private void smoothMove(float deltaX, float deltaY) {
        // Number of steps for the animation (higher value for smoother movement)
        float stepX = deltaX / 20;
        float stepY = deltaY / 20;

        Timeline timeline = new Timeline();

        // Add keyframes for each step
        for (int i = 1; i <= 20; i++) {
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(i * frog.getSpeed()), e -> {
                this.frog.moveX(stepX);
                this.frog.moveY(stepY);

                if (this.frog.getPen()) {
                    this.drawLine(); // uncomment this later probs
                }
                drawFrog();
            }));
        }

        // Start the timeline animation
        timeline.setCycleCount(1);
        timeline.play();

        timeline.setOnFinished(e -> {
            String nextCommand = this.editorListener.getNextCommand(this.frog);
            if (nextCommand == null) {
                return;
            }

            //this.cmdFinnished = true;
            this.moveFrog(nextCommand);
        });
    }

    private void smoothRotate(double radians) {
        // Number of steps for the animation (higher value for smoother movement)
        float stepRadians = (float) radians / 10;

        Timeline timeline = new Timeline();

        // Add keyframes for each step
        for (int i = 1; i <= 10; i++) {
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(this.frog.getSpeed() * i), e -> {
                this.frog.rotateClockwise(stepRadians);

                this.drawFrog();
            }));
        }

        // Start the timeline animation
        timeline.setCycleCount(1);
        timeline.play();

        timeline.setOnFinished(e -> {
            String nextCommand = this.editorListener.getNextCommand(this.frog);
            if (nextCommand == null) {
                return;
            }
            this.moveFrog(nextCommand);

        });
    }

    private void drawFrog() {
        Paint fill = this.frogGC.getFill();
        this.frogGC.clearRect(0, 0, this.getWidth(), this.getHeight());
        this.frogGC.setFill(this.frog.getColor());

        // Save the current transformation state
        this.frogGC.save();

        // Move the origin of rotation to the center of the rectangle
        double centerX = this.frog.getX() + Constants.FROG_WIDTH / 2.0;
        double centerY = this.frog.getY() + Constants.FROG_HEIGHT / 2.0;
        this.frogGC.translate(centerX, centerY);

        // keep it negative since frog rotation is based on the y axis, and it is inverted
        this.frogGC.rotate(Math.toDegrees(-this.frog.getAngle()));

        // Draw the rectangle at the origin (adjust for width/height since we translated)
        Image frogImg = this.frog.getImage();
        if (frogImg != null) {
            this.frogGC.drawImage(frogImg, -Constants.FROG_WIDTH / 2.0, -Constants.FROG_HEIGHT / 2.0);
        } else {
            this.frogGC.fillRect(-Constants.FROG_WIDTH / 2.0, -Constants.FROG_HEIGHT / 2.0, Constants.FROG_WIDTH, Constants.FROG_HEIGHT);
        }
        // Restore the previous transformation state
        this.frogGC.restore();

        this.frogGC.setFill(fill);
    }

    // Paint the backgrounds, doesnt affect the Fill
    private void drawBackground() {
        Paint stroke = this.backroundGC.getFill();
        this.backroundGC.setFill(BACKGROUND_COLOR);
        this.backroundGC.fillRect(0, 0, Constants.DEFAULT_DRAWBOX_WIDTH, Constants.DEFAULT_DRAWBOX_HEIGHT);
        this.backroundGC.setFill(stroke);
        this.drawGrid();
    }

    private void drawLine() {
        double startX = this.frog.getPrevX() + Constants.FROG_WIDTH / 2;
        double startY = this.frog.getPrevY() + Constants.FROG_HEIGHT / 2;
        double endX = this.frog.getX() + Constants.FROG_WIDTH / 2;
        double endY = this.frog.getY() + Constants.FROG_HEIGHT / 2;

        backroundGC.setStroke(this.frog.getColor());
        backroundGC.setLineWidth(2);

        backroundGC.strokeLine(startX, startY, endX, endY);
    }

    private void drawGrid() {
        Paint stroke = this.backroundGC.getStroke();
        this.backroundGC.setStroke(Color.LIGHTGRAY);
        this.backroundGC.setLineWidth(1);

        // draw vertical lines
        for (int i = 0; i < Constants.DEFAULT_DRAWBOX_WIDTH; i += (int) Constants.FROG_WIDTH) {
            backroundGC.strokeLine(i, 0, i, Constants.DEFAULT_DRAWBOX_HEIGHT);
        }

        for (int i = 0; i < Constants.DEFAULT_DRAWBOX_HEIGHT; i += (int) Constants.FROG_HEIGHT) {
            backroundGC.strokeLine(0, i, Constants.DEFAULT_DRAWBOX_WIDTH, i);
        }

        this.backroundGC.setStroke(stroke);
    }

    public void setEditorListener(EditorInterface editorListener) {
        this.editorListener = editorListener;
    }

    public void setConsoleInterface(ConsoleInterface consoleInterface) {
        this.consoleInterface = consoleInterface;
    }

    public Frog getFrog() {
        return this.frog;
    }
}
