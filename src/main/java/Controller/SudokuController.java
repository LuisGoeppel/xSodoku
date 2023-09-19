package Controller;

import SudokuSolver.SudokuSolver;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Sudoku;

public class SudokuController {

    @FXML
    private AnchorPane gamePane;
    @FXML
    private Canvas gameCanvas;
    @FXML
    private Canvas notationCanvas;
    @FXML
    private Button btnSetup;

    private Sudoku sudoku;

    private final Color hoverColor = new Color(0.9, 0.9, 0.9, 0.5);
    private final Color selectColor = new Color(0.5, 0.9, 0.5, 0.5);

    private Rectangle selectionRect;
    private int selectedSquare = -1;
    private boolean setupMode;

    private static final double squareWidth = 55.3;

    public void init(){
        selectionRect = new Rectangle();
        selectionRect.setFill(hoverColor);
        selectionRect.setX(0);
        selectionRect.setY(0);
        selectionRect.setWidth(squareWidth - 2);
        selectionRect.setHeight(squareWidth - 2);
        selectionRect.setVisible(false);
        gamePane.getChildren().add(selectionRect);

        sudoku = new Sudoku();
        setupMode = false;
        drawMatrix(sudoku.getMatrix(), sudoku.getStartNumbers());

        gamePane.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            int xCord = Math.min((int) (mouseEvent.getX() / squareWidth), 8);
            int yCord = Math.min((int) (mouseEvent.getY() / squareWidth), 8);

            selectedSquare = xCord + yCord * 9;
            selectionRect.setX(xCord * squareWidth + (xCord/3) * 6);
            selectionRect.setY(yCord * squareWidth + (yCord/3) * 6);
            selectionRect.setFill(selectColor);
        });

        gamePane.addEventHandler(MouseEvent.MOUSE_MOVED, mouseEvent -> {
            if (selectedSquare == -1) {
                int xCord = (int) (mouseEvent.getX() / squareWidth);
                int yCord = (int) (mouseEvent.getY() / squareWidth);

                selectionRect.setX(xCord * squareWidth + (xCord/3) * 6);
                selectionRect.setY(yCord * squareWidth + (yCord/3) * 6);

                selectionRect.setVisible(xCord < 9 && yCord < 9);
            }
        });

        gamePane.addEventHandler(MouseEvent.MOUSE_ENTERED, mouseEvent -> {
            selectionRect.setVisible(true);
        });

        gamePane.addEventHandler(MouseEvent.MOUSE_EXITED, mouseEvent -> {
            selectionRect.setVisible(false);
        });
    }

    @FXML
    public void setUpBoard() {
        if (setupMode) {
            btnSetup.setText("Custom Setup");
            setupMode = false;
        } else {
            btnSetup.setText("Save");
            sudoku = new Sudoku("---------/---------/---------/---------/---------/---------/---------/---------/---------");
            drawMatrix(sudoku.getMatrix(), sudoku.getStartNumbers());
            setupMode = true;
        }
    }

    @FXML
    public void generateNew() {
        //sudoku = new Sudoku("----9-276/-3------8/--4-8----/----38---/4-3---5--/-1---5-6-/1-----3--/-4--71---/--65---9-"); // Extrem Hard
        //sudoku = new Sudoku("---74----/7436-5---/--1------/------73-/-----8---/--52-4---/----2--93/1--4----2/9-4--3-5-"); // Extrem Hard
        //sudoku = new Sudoku("---------/-39256--8/6---4----/---9-2--7/-2-1---96/--7---51-/-8--2----/5-3---1--/-----7--5"); // Extrem Hard
        //sudoku = new Sudoku("--1--3-67/----8421-/---------/18----6-2/-2---9---/-4-2--7-8/3-------1/---3-8---/--7-9-5--"); // Very Hard
        //sudoku = new Sudoku("--------3/---69--4-/8---751--/463------/--8---2--/-----6--5/59-1--82-/---5----6/---8-9---"); // Very Hard
        //sudoku = new Sudoku("-----6-7-/56-3--48-/--4-1----/------3--/----64--8/--698-14-/2------6-/-4---2---/-15-4-82-"); // Hard
        //sudoku = new Sudoku("5-8---2--/--4----18/2--------/7------24/---34-96-/----768-3/--3-5----/-9---3-4-/----8---1"); // Hard
        //sudoku = new Sudoku("-3--98---/-86-1---9/1--------/----26--5/--85---9-/---7-96-3/3-7-----6/---367-1-/-5---1-3-"); // Normal
        //sudoku = new Sudoku("--3------/7--2---3-/-9-137-2-/----6--7-/3--9-56--/--67-14--/---47-5-1/--4--2---/-3--18-4-"); // Normal
        sudoku = new Sudoku("-4-185---/-316--9--/-----7---/---92-4-6/36-------/-9------5/-2----3--/----5--1-/65-2-4---"); // Easy
        drawMatrix(sudoku.getMatrix(), sudoku.getStartNumbers());
    }

    @FXML
    public void solveSudoku() {
        String solvedSudoku = SudokuSolver.solve(sudoku.getAsString());
        sudoku.updateSudoku(solvedSudoku);
        drawMatrix(sudoku.getMatrix(), sudoku.getStartNumbers());
    }

    public void keyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.ESCAPE) {
            selectedSquare = -1;
            selectionRect.setFill(hoverColor);
        } else if (e.getCode().getCode() <= 57 && e.getCode().getCode() > 48) {
            enterNumber(e.getCode().getCode() - 48);
        } else if (e.getCode() == KeyCode.DELETE) {
            sudoku.remove(selectedSquare);
            clearSmallNumbers(selectedSquare);
            selectedSquare = -1;
            selectionRect.setFill(hoverColor);
            drawMatrix(sudoku.getMatrix(), sudoku.getStartNumbers());
        } else if (e.getCode().getCode() > 96 && e.getCode().getCode() <= 105) {
            if (!setupMode) {
                drawSmallNumber(e.getCode().getCode() - 96, selectedSquare);
            }
        }
    }

    private void enterNumber(int number) {
        if (setupMode) {
            if (sudoku.insertStartNumber(selectedSquare, number)) {
                drawMatrix(sudoku.getMatrix(), sudoku.getStartNumbers());
                clearSmallNumbers(selectedSquare);
            }
        } else {
            if (sudoku.insertNumber(selectedSquare, number)) {
                drawMatrix(sudoku.getMatrix(), sudoku.getStartNumbers());
                clearSmallNumbers(selectedSquare);
            }
        }

        selectedSquare = -1;
        selectionRect.setFill(hoverColor);
    }

    private void drawMatrix(int[] matrix, boolean[] startNumbers) {
        double numberOffset = 15;
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        Font font;

        if (matrix.length != 81) {
            throw new IllegalArgumentException("Matrix has the wrong length!");
        }

        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
        for (int i = 0; i < matrix.length; i++) {
            int xCord = i % 9;
            int yCord = i / 9;
            if (matrix[i] > 0 && matrix[i] < 10) {
                if (startNumbers[i]) {
                    font = Font.font("Arial", FontWeight.BOLD, 44);
                } else {
                    font = Font.font("Arial", 44);
                }
                gc.setFont(font);
                gc.fillText(Integer.toString(matrix[i]), xCord * squareWidth + numberOffset + (xCord/3) * 6,
                        yCord * squareWidth + (yCord/3) * 6 + 42);
            }
        }
    }

    private void drawSmallNumber(int number, int squareNumber) {
        GraphicsContext gc = notationCanvas.getGraphicsContext2D();
        Font font = Font.font("Arial", 15);
        gc.setFill(Color.GREY);
        gc.setFont(font);

        int xCord = squareNumber % 9;
        int yCord = squareNumber / 9;

        int xCordNumber = (int) (xCord * squareWidth + (xCord/3) * 6 + 5 + ((number - 1) % 3) * 17);
        int yCordNumber = (int) (yCord * squareWidth + (yCord/3) * 6 + 15 + ((number - 1) / 3) * 17);

        gc.fillText(Integer.toString(number), xCordNumber, yCordNumber);
    }

    private void clearSmallNumbers(int squareNumber) {
        GraphicsContext gc = notationCanvas.getGraphicsContext2D();

        int xCord = squareNumber % 9;
        int yCord = squareNumber / 9;

        int xTopLeft = (int) (xCord * squareWidth + (xCord/3) * 6);
        int yTopLeft = (int) (yCord * squareWidth + (yCord/3) * 6);

        gc.clearRect(xTopLeft, yTopLeft, squareWidth, squareWidth);
    }
}

// 13-865--9/--8--41-2/---12----/--76--9-3/9154---87/6-3-17-5-/------345/--67-389-/--9-8---6
// 926-1---5/---39----/-7-26-8-1/-97------/3-29516-7/-6-----1-/21-7-6538/-8--2-4-9/---5-916-

// -64----1-/---1-2--4/13---9685/4--------/9-5----42/-8-2-41-7/87----9--/3--7-----/---891---

// -6-9---1-/--34--97-/-2-7-----/------2-7/--2--1--8/-8---5-3-/---------/-3---6-9-/4--1---8-


// -423-761-/-8-4-5-9-/-5-----3-/--8-3-9--/7-------3/--3-5-8--/-7-----4-/-9-2-3-8-/-351-972-
// -741-945-/9-------3/---4-6---/-3-----7-/756-8-921/-4-----8-/---7-5---/8-------6/-258-371-



// --38-951-/-1--54-2-/5-------9/6--21--9-/-916-7--2/---4-5---/-3-7-1--6/-7--2-93-/462-83--5

// -4-185---/-316--9--/-----7---/---92-4-6/36-------/-9------5/-2----3--/----5--1-/65-2-4---

// --3------/7--2---3-/-9-137-2-/----6--7-/3--9-56--/--67-14--/---47-5-1/--4--2---/-3--18-4-