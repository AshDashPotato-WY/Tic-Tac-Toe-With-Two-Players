package com.example.cis296proj4;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;


public class TicTacToeController {
    @FXML
    private GridPane gridPane;

    @FXML
    private Button logoutBtn;

    @FXML
    private Label textLabel;

    @FXML
    public void gridMouseClicked(MouseEvent event) {
        Node source = (Node)event.getTarget();
        // get row index and column index from the grid pane
        Integer colIndex = gridPane.getColumnIndex(source);
        Integer rowIndex = gridPane.getRowIndex(source);
        System.out.printf("Mouse clicked cell [row, col] : [%d, %d]%n", rowIndex, colIndex);
    }

}