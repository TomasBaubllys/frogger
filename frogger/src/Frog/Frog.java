package Frog;

import Constants.Constants;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Frog {
    private float x;
    private float y;

    private float prevX;
    private float prevY;

    private double angle;
    private float speed;

    private float width;
    private float height;

    private float maxX;
    private float maxY;

    private Image image;

    private Color color;

    private boolean pen; // false = up, true = down

    public Frog(float x, float y, double angle, float speed, float width, float height, float maxX, float maxY) {
        this.x = x;
        this.y = y;
        this.prevX = x;
        this.prevY = y;
        this.angle = -angle;
        this.speed = speed;
        this.width = width;
        this.height = height;
        this.maxX = maxX;
        this.maxY = maxY;
        this.pen = true;
        this.image = new Image("frog.png", Constants.FROG_WIDTH, Constants.FROG_HEIGHT, false, false);
        this.color = Color.LIGHTGREEN;

        System.out.println("Current working directory: " + System.getProperty("user.dir"));
        if(image.isError()){
            System.err.println("Image failed to load");
        }
        else {
            System.out.println("Image of frog loaded");
        }

    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getPrevX() {
        return prevX;
    }

    public float getPrevY() {
        return prevY;
    }

    public void moveMiddle() {
        this.x = Constants.DEFAULT_DRAWBOX_WIDTH / 2;
        this.y = Constants.DEFAULT_DRAWBOX_HEIGHT / 2;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void moveX(float delta) {
        this.prevX = this.x;
        if(this.angle == 0) {
            if (this.x + delta > this.maxX - this.width || x + delta < 0) {
                return;
            }

            this.x += delta;
        }
        else {
            this.move((float) (delta * Math.cos(this.angle)),(float) (-delta * Math.sin(this.angle)));
        }
    }

    public void moveY(float delta) {
        this.prevY = this.y;
        if(angle == 0) {
            if (y + delta > maxY - this.width || y + delta < 0) {
                return;
            }
            y += delta;
        }
        else {
            this.move((float) (delta * Math.sin(this.angle)),(float) (delta * Math.cos(this.angle)));
        }
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = -angle;
    }

    public float getSpeed() {
        return speed;
    }

    public void rotateClockwise(float radians) {
        angle -= radians;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void move(float deltaX, float deltaY) {
        if(x + deltaX > maxX || x + deltaX < 0 || y + deltaY > maxY || y + deltaY < 0) {
            return;
        }
        this.x += deltaX;
        this.y += deltaY;
    }

    public Image getImage() {
        return this.image;
    }

    // support up or down
    public void setPen(String penDir) {
        this.pen = penDir.equals("down");
    }

    public void setPen(Boolean pen) {
        this.pen = pen;
    }

    public boolean getPen() {
        return this.pen;
    }

    public float getAngleDeg() {
        return (float)Math.toDegrees(this.angle);
    }
}
