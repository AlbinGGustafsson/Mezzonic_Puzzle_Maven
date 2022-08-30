package org.example;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public abstract class SelectableButton extends Canvas {

    private Mezzonic mez = new Mezzonic();
    private GraphicsContext gc;
    private boolean brickIsLit = true;
    protected Color highlightColor = Color.GOLDENROD;

    double width;
    double height;

    public SelectableButton(double w, double h) {
        super(w, h);
        this.width = w;
        this.height = h;

        //installerar selection handlers
        setOnMouseEntered(new MouseEnterHandler());
        setOnMouseExited(new MouseExitHandler());

        gc = getGraphicsContext2D();

    }

    public void drawSelectableButton() {}

    //Detta har med selection att göra
    class MouseEnterHandler implements EventHandler<MouseEvent>{

        @Override
        public void handle(MouseEvent mouseEvent) {
            paintSelection();
        }
    }

    class MouseExitHandler implements EventHandler<MouseEvent>{

        @Override
        public void handle(MouseEvent mouseEvent) {

            gc.clearRect(0,0,getWidth(),getHeight());
            drawSelectableButton();

        }
    }

    public void paintSelection(){
        gc.setStroke(highlightColor);
        gc.setLineWidth(4);
        gc.strokeRect(0,0,width-6,height-6);
    }

}
