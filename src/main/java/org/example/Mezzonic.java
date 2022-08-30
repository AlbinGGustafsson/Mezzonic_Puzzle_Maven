package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Random;

public class Mezzonic extends Application {

    private static final int BOARD_SIZE = 5;
    private static final int SOLVE_STEP_DELAY_TIME_MS = 300;
    private GridPane grid;
    private ArrayList<ArrayList<Brick>> puzzleBoard;
    private Stage stage;

    private StringBuilder savedState;

    private boolean isSolving;
    private boolean isEditing;

    @Override
    public void start(Stage stage) throws Exception {

        this.stage = stage;

        BorderPane root = new BorderPane();
        grid = new GridPane();
        root.setCenter(grid);

        FlowPane flowPane = new FlowPane();

        ShuffleButton shuffleButton = new ShuffleButton();
        shuffleButton.setOnMouseClicked(new ShuffleClickHandler());

        SolveButton solveButton = new SolveButton();
        solveButton.setOnMouseClicked(new SolveClickHandler());

        EditButton editButton = new EditButton();
        editButton.setOnMouseClicked(new EditClickHandler());

        flowPane.getChildren().addAll(shuffleButton, solveButton, editButton);
        root.setBottom(flowPane);

        puzzleBoard = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            puzzleBoard.add(createRowOfBricks(i));
        }

        Scene scene = new Scene(root);
        scene.setCursor(new ImageCursor(new Image("file:images/wowmus.png")));

        stage.setScene(scene);
        stage.setTitle("Your First Mezzonic Protolock");
        stage.setResizable(false);
        stage.show();

        rollBricks();

    }

    private ArrayList<Brick> createRowOfBricks(int rowNr) {
        ArrayList<Brick> row = new ArrayList<>();

        for (int i = 0; i < BOARD_SIZE; i++) {

            Brick brick = new Brick(i, rowNr);
            row.add(brick);
            grid.add(brick, i, rowNr);
            brick.setOnMouseClicked(new ClickHandler());
        }

        return row;
    }

    private void rollBricks() {

        //ta bort ettor från board
        resetSolutionOnBoard();

        //clear board
        for (ArrayList<Brick> row : puzzleBoard) {
            for (Brick brick : row) {
                brick.setOff();
            }
        }

        //shuffle board
        for (ArrayList<Brick> row : puzzleBoard) {
            for (Brick brick : row) {
                if (new Random().nextInt(2) == 1) {
                    playClick(brick, false);
                }
            }
        }

        //save board state to variable
        saveState();

    }

    public void playClick(Brick sourceBrick, boolean trackPlay) {

        sourceBrick.toggleBrick();

        try {
            puzzleBoard.get(sourceBrick.getY() + 1).get(sourceBrick.getX()).toggleBrick();
        } catch (IndexOutOfBoundsException e) {
        }

        try {
            puzzleBoard.get(sourceBrick.getY() - 1).get(sourceBrick.getX()).toggleBrick();
        } catch (IndexOutOfBoundsException e) {
        }

        try {
            puzzleBoard.get(sourceBrick.getY()).get(sourceBrick.getX() + 1).toggleBrick();
        } catch (IndexOutOfBoundsException e) {
        }

        try {
            puzzleBoard.get(sourceBrick.getY()).get(sourceBrick.getX() - 1).toggleBrick();
        } catch (IndexOutOfBoundsException e) {
        }

        if (trackPlay) {
            //System.out.println("X: " + sourceBrick.getX() + ", Y: " + sourceBrick.getY());
            sourceBrick.addClick(1);
        }

        //kollar om puzzle är löst, om den är löst kör kod som ska köras när den är löst.
        if (isPuzzleSolved()) {

            //måste delegeras tillbaka för att under solvern så körs playclick på en annan Thread.....
            Platform.runLater(new Runnable() {
                @Override
                public void run() {

                    //sparar som variabel för att metoden resettar click counter.
                    StringBuilder solution = getSolution();

                    Alert solved;
                    if (isSolving) {
                        solved = new Alert(Alert.AlertType.INFORMATION, "The puzzle is solved! \n" + "Solution:" + solution + "\nFor Board:" + savedState);
                    } else {
                        solved = new Alert(Alert.AlertType.INFORMATION, "You solved the puzzle!");
                    }
                    solved.showAndWait();
                    isSolving = false;

                    updateBoardFromSavedState();
                    drawSolutionOnBoard(solution);
                    //rollBricks();
                }
            });

        }
    }

    //returnar en StringBuilder som passar utskriften på popupen med mellanrum mellan. Om en bricka får en 1 så ska den klickas i lösningen.
    //kommer också resetta click counter för varje brick. (clock countern används för att spara lösningen) kanske ska flytta den härifrån
    private StringBuilder getSolution() {

        StringBuilder stringBuilder = new StringBuilder();

        for (var row : puzzleBoard) {
            stringBuilder.append("\n");
            for (var brick : row) {
                stringBuilder.append(brick.getClicks() > 1 ? 0 : brick.getClicks()).append(" ");
                brick.resetClicks();
            }
        }

        return stringBuilder;
    }


    //sparar boardens state till en Stringbuilder som passar utskriften på popupen med mellanrum mellan.
    private void saveState() {
        savedState = new StringBuilder();
        for (ArrayList<Brick> row : puzzleBoard) {
            savedState.append("\n");
            for (Brick brick : row) {
                savedState.append(brick.isLit() ? "#" : "o").append(" ");
            }
        }
    }

    //gör om savedstate till en string utan mellanrum eller radbyten. (enklare att jobba me den då)
    //ändrar boarden till denna savedstate
    private void updateBoardFromSavedState() {

        String savedStateString = savedState.toString().replace(" ", "").replace("\n", "");
        System.out.println(savedStateString);

        int index = 0;
        for (var row : puzzleBoard) {
            for (var brick : row) {
                if (savedStateString.charAt(index) == '#') {
                    brick.setLit();
                } else {
                    brick.setOff();
                }
                index++;
            }
        }
    }


    //gör om en StringBuilder solutiob  till en string utan mellanrum eller radbyten. (enklare att jobba me den då)
    //ritar ut lösningen på boarden genom att den ritar ettor på alla som ska klickas.
    private void drawSolutionOnBoard(StringBuilder solution) {

        String solutionString = solution.toString().replace(" ", "").replace("\n", "");
        System.out.println(solutionString);

        int index = 0;
        for (var row : puzzleBoard) {
            for (var brick : row) {
                if (solutionString.charAt(index) == '1') {
                    brick.drawOne();
                }
                index++;
            }
        }
    }

    //tar bort alla ettor från alla brickor.
    private void resetSolutionOnBoard() {

        for (ArrayList<Brick> row : puzzleBoard) {
            for (Brick brick : row) {
                brick.removeOne();
            }
        }

    }

    //När man klickar på en bricka
    class ClickHandler implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent mouseEvent) {

            Brick sourceBrick = (Brick) mouseEvent.getSource();

            if (sourceBrick.hasOne()) {
                sourceBrick.removeOne();
            }

            if (isEditing) {
                sourceBrick.toggleBrick();
            } else {
                playClick(sourceBrick, false);
            }

            saveState();
            sourceBrick.paintSelection();
        }
    }

    class ShuffleClickHandler implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent mouseEvent) {
            if (isSolving) {
                Alert solving = new Alert(Alert.AlertType.INFORMATION, "Cant shuffle when solving is in progress!");
                solving.showAndWait();
            } else {
                rollBricks();
            }
        }
    }

    class SolveClickHandler implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent mouseEvent) {

            resetSolutionOnBoard();

            //När man klickar på solve ska man alltid hamna i play mode
            isEditing = false;
            stage.setTitle("Your First Mezzonic Protolock");

            if (!isPuzzleSolved()) {
                Solver solver = new Solver();
                Thread solverThread = new Thread(solver);
                solverThread.start();
            } else {
                Alert alreadySolved = new Alert(Alert.AlertType.INFORMATION, "Puzzle is already solved");
                alreadySolved.showAndWait();
            }

        }
    }

    class EditClickHandler implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent mouseEvent) {
            resetSolutionOnBoard();

            isEditing = !isEditing;

            stage.setTitle("Your First Mezzonic Protolock " + (isEditing ? "(Edit Mode)" : ""));

            System.out.println("Edit mode:" + isEditing);
        }
    }

    private boolean isPuzzleSolved() {

        for (ArrayList<Brick> row : puzzleBoard) {
            for (Brick brick : row) {
                if (brick.isLit()) {
                    return false;
                }
            }
        }
        //isSolving = false;
        return true;
    }

    class Solver implements Runnable {

        @Override
        public void run() {

            isSolving = true;

            sleep(SOLVE_STEP_DELAY_TIME_MS);

            chaseLights();

            ArrayList<Brick> topRow = puzzleBoard.get(0);
            ArrayList<Brick> bottomRow = puzzleBoard.get(4);

            if (!bottomRow.get(0).isLit() && !bottomRow.get(1).isLit() && bottomRow.get(2).isLit() && bottomRow.get(3).isLit() && bottomRow.get(4).isLit()) {
                topRow.get(3).paintSelection();
                sleep(SOLVE_STEP_DELAY_TIME_MS);
                playClick(topRow.get(3), true);
                chaseLights();
            } else if (!bottomRow.get(0).isLit() && bottomRow.get(1).isLit() && !bottomRow.get(2).isLit() && bottomRow.get(3).isLit() && !bottomRow.get(4).isLit()) {
                topRow.get(1).paintSelection();
                sleep(SOLVE_STEP_DELAY_TIME_MS);
                playClick(topRow.get(1), true);
                topRow.get(4).paintSelection();
                sleep(SOLVE_STEP_DELAY_TIME_MS);
                playClick(topRow.get(4), true);
                chaseLights();
            } else if (!bottomRow.get(0).isLit() && bottomRow.get(1).isLit() && bottomRow.get(2).isLit() && !bottomRow.get(3).isLit() && bottomRow.get(4).isLit()) {
                topRow.get(0).paintSelection();
                sleep(SOLVE_STEP_DELAY_TIME_MS);
                playClick(topRow.get(0), true);
                chaseLights();
            } else if (bottomRow.get(0).isLit() && !bottomRow.get(1).isLit() && !bottomRow.get(2).isLit() && !bottomRow.get(3).isLit() && bottomRow.get(4).isLit()) {
                topRow.get(3).paintSelection();
                sleep(SOLVE_STEP_DELAY_TIME_MS);
                playClick(topRow.get(3), true);
                topRow.get(4).paintSelection();
                sleep(SOLVE_STEP_DELAY_TIME_MS);
                playClick(topRow.get(4), true);
                chaseLights();
            } else if (bottomRow.get(0).isLit() && !bottomRow.get(1).isLit() && bottomRow.get(2).isLit() && bottomRow.get(3).isLit() && !bottomRow.get(4).isLit()) {
                topRow.get(4).paintSelection();
                sleep(SOLVE_STEP_DELAY_TIME_MS);
                playClick(topRow.get(4), true);
                chaseLights();
            } else if (bottomRow.get(0).isLit() && bottomRow.get(1).isLit() && !bottomRow.get(2).isLit() && bottomRow.get(3).isLit() && bottomRow.get(4).isLit()) {
                topRow.get(2).paintSelection();
                sleep(SOLVE_STEP_DELAY_TIME_MS);
                playClick(topRow.get(2), true);
                chaseLights();
            } else if (bottomRow.get(0).isLit() && bottomRow.get(1).isLit() && bottomRow.get(2).isLit() && !bottomRow.get(3).isLit() && !bottomRow.get(4).isLit()) {
                topRow.get(1).paintSelection();
                sleep(SOLVE_STEP_DELAY_TIME_MS);
                playClick(topRow.get(1), true);
                chaseLights();
            }

            if (!isPuzzleSolved()) {
                Platform.runLater(() -> new Alert(Alert.AlertType.INFORMATION, "Board not solvable").showAndWait());
                isSolving = false;
            }

        }

    }

    private void chaseLights() {

        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 5; x++) {
                if (puzzleBoard.get(y).get(x).isLit()) {
                    puzzleBoard.get(y + 1).get(x).paintSelection();
                    sleep(SOLVE_STEP_DELAY_TIME_MS);
                    playClick(puzzleBoard.get(y + 1).get(x), true);
                }
            }
        }

    }

    private void sleep(int ms) {

        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
