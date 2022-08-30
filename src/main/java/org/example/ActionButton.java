package org.example;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class ActionButton extends SelectableButton {

    private Image image;
    protected GraphicsContext gc = getGraphicsContext2D();

    public ActionButton(Image image){
        super(205, 124);
        this.image = image;
        drawSelectableButton();
    }

    @Override
    public void drawSelectableButton() {
        gc.drawImage(image, 0, 0, getWidth(), getHeight());
    }

}
