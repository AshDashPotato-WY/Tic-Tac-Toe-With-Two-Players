package com.example.cis296proj4;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ResourceBundle;


public class TicTacToeController implements Initializable {
    @FXML
    private AnchorPane rootPane;

    @FXML
    private GridPane gridPane;

    @FXML
    private Button logoutBtn;

    @FXML
    private Label textLabel;

    private String[][] gridCells = new String[3][3];

    private String symbol = "X";

    private boolean yourTurn = true;   // check whose turn it is: self-true, opponent-false

    private Node firstNode; // line drawing
    private Node lastNode; // line drawing

    @FXML
    public void gridMouseClicked(MouseEvent event) {
        Node source = (Node)event.getTarget();
        // get row index and column index from the grid pane
        Integer colIndex = gridPane.getColumnIndex(source);
        Integer rowIndex = gridPane.getRowIndex(source);

        // check if index is null
        if (colIndex == null || rowIndex == null) {
            return;
        }

        System.out.printf("Mouse clicked cell [row, col] : [%d, %d]%n", rowIndex, colIndex);

        // Find the label at the clicked position and set its text
        setSybmbol(rowIndex, colIndex);

        // check game status and decide win/loss/tie
        Line line = new Line();
        line.setStroke(Color.rgb(60, 60, 60, 0.5));
        line.setStrokeWidth(10);
        if (status(symbol) && yourTurn) {
            line.setStartX(firstNode.getBoundsInParent().getCenterX() + gridPane.getLayoutX());
            line.setEndX(lastNode.getBoundsInParent().getCenterX()+ gridPane.getLayoutX());
            line.setStartY(firstNode.getBoundsInParent().getCenterY() + gridPane.getLayoutY());
            line.setEndY(lastNode.getBoundsInParent().getCenterY() + gridPane.getLayoutY());
            rootPane.getChildren().add(line);
            Alert alert = new Alert(Alert.AlertType.NONE, "You WON!", ButtonType.CLOSE);
            alert.show();
        }
        else if (isTie() && yourTurn) {
            Alert alert = new Alert(Alert.AlertType.NONE, "It's A TIE!", ButtonType.CLOSE);
            alert.show();
        }
        // TODO: handle case for loser


        // Toggle symbol for next turn
        symbol = symbol.equals("X") ? "O" : "X";
        yourTurn = !yourTurn;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // label grid pane cells
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Label cellLabel = new Label();
                cellLabel.setFont(new Font("Arial", 86));
                cellLabel.setStyle("-fx-content-display: CENTER; -fx-alignment: CENTER; -fx-pref-height: 80.0; -fx-pref-width: 89.0; -fx-text-alignment: CENTER");
                gridPane.add(cellLabel, j, i); // label each cell in grid pane : j-colIndex, i-rowIndex
            }
        }
    }

    // set symbol in the particular cell
    public void setSybmbol(int row, int col) {
        for (Node node : gridPane.getChildren()) {
            Integer gridCol = GridPane.getColumnIndex(node);
            Integer gridRow = GridPane.getRowIndex(node);
            // Check for null indices
            if (gridCol == null || gridRow == null) {
                continue;
            }

            if (gridCol.equals(col) && gridRow.equals(row)) {
                if (node instanceof Label) {
                    Label clickedLabel = (Label) node;
                    if (clickedLabel.getText() == null || clickedLabel.getText().isEmpty()) {
                        clickedLabel.setText(symbol);
                        // add to the gridCells array
                        gridCells[row][col] = symbol;
                        // check turns and set color
                        if (yourTurn) {
                            clickedLabel.setTextFill(Color.web("#003049"));
                        }
                        else {
                            clickedLabel.setTextFill(Color.web("#d62828"));
                        }
                        break;
                    }
                }
            }
        }
    }

    // check winner
    public boolean status(String symbol) {
        // check 3 cells in the same row
        for (int i = 0; i < 3; i++) {
            // Check for null indices
            if (gridCells[i][0] == null || gridCells[i][1] == null || gridCells[i][2] == null) {
                continue;
            }
            if (gridCells[i][0].equals(symbol) && gridCells[i][1].equals(symbol) && gridCells[i][2].equals(symbol)) {
                for (Node node: gridPane.getChildren()) {
                    Integer gridCol = GridPane.getColumnIndex(node);
                    Integer gridRow = GridPane.getRowIndex(node);
                    if (gridCol == null || gridRow == null) {
                        continue;
                    }
                    if (gridCol.equals(0) && gridRow.equals(i)) {
                        firstNode = node;
                    }
                    else if (gridCol.equals(2) && gridRow.equals(i)) {
                        lastNode = node;
                    }
                }
                return true;
            }
        }
        // check 3 cells in the same column
        for (int j = 0; j < 3; j++) {
            if (gridCells[0][j] == null || gridCells[1][j] == null || gridCells[2][j] == null) {
                continue;
            }
            if (gridCells[0][j].equals(symbol) && gridCells[1][j].equals(symbol) && gridCells[2][j].equals(symbol)) {
                for (Node node: gridPane.getChildren()) {
                    Integer gridCol = GridPane.getColumnIndex(node);
                    Integer gridRow = GridPane.getRowIndex(node);
                    if (gridCol == null || gridRow == null) {
                        continue;
                    }
                    if (gridCol.equals(j) && gridRow.equals(0)) {
                        firstNode = node;
                    }
                    else if (gridCol.equals(j) && gridRow.equals(2)) {
                        lastNode = node;
                    }
                }
                return true;
            }
        }
        // check diagonal
        if (gridCells[0][0] == null || gridCells[1][1] == null || gridCells[2][2] == null) {
            ;
        }
        else if (gridCells[0][0].equals(symbol) && gridCells[1][1].equals(symbol) && gridCells[2][2].equals(symbol)) {
            for (Node node: gridPane.getChildren()) {
                Integer gridCol = GridPane.getColumnIndex(node);
                Integer gridRow = GridPane.getRowIndex(node);
                if (gridCol == null || gridRow == null) {
                    continue;
                }
                if (gridCol.equals(0) && gridRow.equals(0)) {
                    firstNode = node;
                }
                else if (gridCol.equals(2) && gridRow.equals(2)) {
                    lastNode = node;
                }
            }
            return true;
        }
        if (gridCells[0][0] == null || gridCells[1][1] == null || gridCells[2][2] == null) {
            ;
        }
        else if (gridCells[0][2].equals(symbol) && gridCells[1][1].equals(symbol) && gridCells[2][0].equals(symbol)) {
            for (Node node: gridPane.getChildren()) {
                Integer gridCol = GridPane.getColumnIndex(node);
                Integer gridRow = GridPane.getRowIndex(node);
                if (gridCol == null || gridRow == null) {
                    continue;
                }
                if (gridCol.equals(2) && gridRow.equals(0)) {
                    firstNode = node;
                }
                else if (gridCol.equals(0) && gridRow.equals(2)) {
                    lastNode = node;
                }
            }
            return true;
        }
        // other case
        return false;
    }

    // check if it's a cat game
    public boolean isTie() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (gridCells[i][j] == null) {
                    return false;
                }
            }
        }
        return true;
    }


}