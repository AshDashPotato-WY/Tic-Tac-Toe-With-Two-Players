package com.example.cis296proj4;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;


public class TicTacToeController implements Initializable {
    @FXML
    private AnchorPane rootPane;

    @FXML
    private GridPane gridPane;

    @FXML
    private Label textLabel;

    private String[][] gridCells = new String[3][3];

    private String symbol;

    private boolean yourTurn = true;   // check whose turn it is: self-true, opponent-false

    private Node firstNode; // line drawing
    private Node lastNode; // line drawing

    // Client vars
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private static final int PORT = 8000;
    private static final String IP = "localhost";

    @FXML
    public void gridMouseClicked(MouseEvent event) throws IOException {
        Node source = (Node)event.getTarget();
        // get row index and column index from the grid pane
        Integer colIndex = gridPane.getColumnIndex(source);
        Integer rowIndex = gridPane.getRowIndex(source);

        // check if index is null, not your turn or cell already has a symbol
        if (colIndex == null || rowIndex == null || !yourTurn || gridCells[rowIndex][colIndex] != null) {
            return;
        }

        System.out.printf("Mouse clicked cell [row, col] : [%d, %d]%n", rowIndex, colIndex);

        // Find the label at the clicked position and set its text
        setSymbol(rowIndex, colIndex, symbol);

        // send the message to the server
        writer.printf("%d %d\n", rowIndex, colIndex);
        writer.flush();

        // check game status and draw lines if there is a win
        if (checkWinner(symbol)) {
            drawLine();
        }

        yourTurn = false;
        updateTurnLabel();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the socket connection here
        try {
            // Replace "localhost" and 12345 with your server's address and port
            socket = new Socket(IP, PORT);
            writer = new PrintWriter(socket.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // label grid pane cells
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    Label cellLabel = new Label();
                    cellLabel.setFont(new Font("Arial", 86));
                    cellLabel.setStyle("-fx-content-display: CENTER; -fx-alignment: CENTER; -fx-pref-height: 80.0; -fx-pref-width: 89.0; -fx-text-alignment: CENTER");
                    gridPane.add(cellLabel, j, i); // label each cell in grid pane : j-colIndex, i-rowIndex
                }
            }

            // Start a separate thread to listen for messages from the server
            new Thread(this::receiveMessages).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveMessages() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                // Update the UI with the received message
                final String msg = message;
                System.out.println(msg);
                Platform.runLater(() -> {
                    if (msg.startsWith("You are player")) {
                        // Initialize the symbol for the player
                        symbol = msg.split(" ")[3]; // Assuming message format is "You are player X" or "You are player O"
                        updateTurnLabel();
                    } else if (msg.contains("wins") || msg.contains("tie")) {
                        // Handle game end scenario
                        if (msg.contains(symbol)) {
                            showAlert("You WON!");
                        } else if (msg.contains("tie")) {
                            showAlert("It's a TIE");
                        } else {
                            showAlert("You LOST!");
                        }
                    } else if (msg.contains("has joined the game") || msg.contains("has left the game")) {
                        // Handle other players joining or leaving the game
                        // System.out.println(msg); // message printed by server
                    } else {
                        // Handle game move messages
                        String[] parts = msg.split(" ");
                        String receivedSymbol = parts[0];
                        int row = Integer.parseInt(parts[2]);
                        int col = Integer.parseInt(parts[3]);

                        setSymbol(row, col, receivedSymbol);
                        // check game status and draw lines if there is a win
                        if (checkWinner(receivedSymbol)) {
                            drawLine();
                        }
                        if (receivedSymbol.equals(symbol)) {
                            // If the received symbol is the same as the player's symbol, it's now the opponent's turn
                            yourTurn = false;
                        } else {
                            // If the received symbol is different, it's now the player's turn
                            yourTurn = true;
                        }
                        updateTurnLabel();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle disconnection or server errors here
            Platform.runLater(() -> {
                showAlert("Connection to the server has been lost.");
                // You can also close the application or navigate to another screen here
            });
        }
    }

    private void updateTurnLabel() {
        if (yourTurn) {
            textLabel.setText("Your turn: " + symbol);
        }
        else {
            textLabel.setText("Opponent's turn");
        }
    }

    // set symbol in the particular cell
    public void setSymbol(int row, int col, String s) {
        for (Node node : gridPane.getChildren()) {
            Integer gridCol = GridPane.getColumnIndex(node);
            Integer gridRow = GridPane.getRowIndex(node);
            // Check for null indices
            if (gridCol == null || gridRow == null) {
                continue;
            }

            if (gridCol.equals(col) && gridRow.equals(row)) {
                if (node instanceof Label clickedLabel) {
                    clickedLabel.setText(s);
                    clickedLabel.setTextFill(s.equals("X") ? Color.web("#003049") : Color.web("#d62828"));
                    gridCells[row][col] = s;
                    break;
                }
            }
        }
    }

    // check winner
    public boolean checkWinner(String symbol) {
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

    private void drawLine() {
        Line line = new Line();
        line.setStroke(Color.rgb(60, 60, 60, 0.5));
        line.setStrokeWidth(10);
        line.setStartX(firstNode.getBoundsInParent().getCenterX() + gridPane.getLayoutX());
        line.setEndX(lastNode.getBoundsInParent().getCenterX()+ gridPane.getLayoutX());
        line.setStartY(firstNode.getBoundsInParent().getCenterY() + gridPane.getLayoutY());
        line.setEndY(lastNode.getBoundsInParent().getCenterY() + gridPane.getLayoutY());
        rootPane.getChildren().add(line);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message);
        alert.show();
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