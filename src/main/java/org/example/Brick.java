package org.example;

import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Brick extends SelectableButton {

    private static final Image litImage = new Image("file:images/Lit.png");
    private static final Image offImage = new Image("file:images/Off.png");
    private static final Image oneImage = new Image("file:images/1.png");

    private GraphicsContext gc = getGraphicsContext2D();
    private boolean brickIsLit = false;
    private boolean hasOne;

    private int x;
    private int y;
    private int clicks = 0;

    public Brick(int x, int y) {
        super(123, 123);
        this.x = x;
        this.y = y;

        drawSelectableButton();
        //drawOne();
        //gc.drawImage(litImage, 0, 0, getWidth(), getHeight());
    }

    public void toggleBrick() {

        gc.clearRect(0, 0, getWidth(), getHeight());
        if (brickIsLit) {
            gc.drawImage(offImage, 0, 0, getWidth(), getHeight());
        } else {
            gc.drawImage(litImage, 0, 0, getWidth(), getHeight());
        }
        brickIsLit = !brickIsLit;

        if (hasOne){
            drawOne();
        }

    }

    public void setLit(){
        gc.clearRect(0, 0, getWidth(), getHeight());
        gc.drawImage(litImage, 0, 0, getWidth(), getHeight());
        brickIsLit = true;
    }
    public void setOff(){
        gc.clearRect(0, 0, getWidth(), getHeight());
        gc.drawImage(offImage, 0, 0, getWidth(), getHeight());
        brickIsLit = false;
    }

    public boolean isLit() {
        return brickIsLit;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getClicks() {
        return clicks;
    }

    public void addClick(int i) {
        clicks += i;
    }

    public void resetClicks() {
        clicks = 0;
    }

    public void drawOne(){
        gc.drawImage(oneImage,47 , 25, 30, 70);
        hasOne = true;
    }

    public boolean hasOne(){
        return hasOne;
    }

    public void removeOne(){
        this.hasOne = false;
        drawSelectableButton();
    }

    @Override
    public void drawSelectableButton() {
        if (isLit()) {
            gc.drawImage(litImage, 0, 0, getWidth(), getHeight());
        } else {
            gc.drawImage(offImage, 0, 0, getWidth(), getHeight());
        }

        if (hasOne){
            drawOne();
        }
    }

}
