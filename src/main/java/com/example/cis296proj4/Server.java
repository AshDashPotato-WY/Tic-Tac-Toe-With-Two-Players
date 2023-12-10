package com.example.cis296proj4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Server {

    private static final int PORT = 8000;
    private static final Set<GameClientHandler> clients = new HashSet<>();
    private static final String[] playerSymbols = {"X", "O"};
    private static int currentPlayerIndex = 0;
    private static String[][] board = new String[3][3];


    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            while (clients.size() < 2) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");

                // Create a new thread to handle the client
                GameClientHandler clientHandler = new GameClientHandler(clientSocket, playerSymbols[currentPlayerIndex]);
                clients.add(clientHandler);
                new Thread(clientHandler).start();

                // Switch to the next player
                currentPlayerIndex = (currentPlayerIndex + 1) % playerSymbols.length;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //----------------------------------------------------------------------//
    //--------------------------| Handler |---------------------------------//
    //----------------------------------------------------------------------//
    private static class GameClientHandler implements Runnable {
        private final Socket clientSocket;
        private final String playerSymbol;
        private PrintWriter writer;

        public GameClientHandler(Socket clientSocket, String playerSymbol) {
            this.clientSocket = clientSocket;
            this.playerSymbol = playerSymbol;
        }

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer = new PrintWriter(clientSocket.getOutputStream(), true);

                // Notify the client about its assigned mark
                writer.println("You are player " + playerSymbol);

                // Broadcast the assigned mark to all clients
                broadcast(playerSymbol + " has joined the game", this);

                String message;
                while ((message = reader.readLine()) != null) {
                    processMove(message, playerSymbol);
                    System.out.println("Received message from player " + playerSymbol + ": " + message);
                    broadcast(playerSymbol + " : " + message, this);
                    // update game status
                    String gameStatus = checkGameStatus();
                    if (!gameStatus.equals("")) {
                        broadcast(gameStatus, null);
                        // resetBoard();
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Remove client when it disconnects
                clients.remove(this);
                broadcast(playerSymbol + " has left the game", this);
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private String checkGameStatus() {
            // Check for a win or a tie
            if (isWinner(playerSymbol)) {
                return playerSymbol + " wins";
            } else if (isTie()) {
                return "It's a tie";
            }
            return "";
        }

        private boolean isTie() {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == null) {
                        return false;
                    }
                }
            }
            return true;
        }

        private boolean isWinner(String symbol) {
            // Check rows, columns, and diagonals for a win
            for (int i = 0; i < 3; i++) {
                if (symbol.equals(board[i][0]) && symbol.equals(board[i][1]) && symbol.equals(board[i][2])) {
                    return true;
                }
                if (symbol.equals(board[0][i]) && symbol.equals(board[1][i]) && symbol.equals(board[2][i])) {
                    return true;
                }
            }
            if (symbol.equals(board[0][0]) && symbol.equals(board[1][1]) && symbol.equals(board[2][2])) {
                return true;
            }
            if (symbol.equals(board[0][2]) && symbol.equals(board[1][1]) && symbol.equals(board[2][0])) {
                return true;
            }
            return false;
        }

        private void processMove(String message, String symbol) {
            String[] parts = message.split(" ");
            int row = Integer.parseInt(parts[0]);
            int col = Integer.parseInt(parts[1]);
            board[row][col] = symbol;
        }

        private void broadcast(String message, GameClientHandler sender) {
            // Send the message to all connected clients, excluding the sender
            // In our case, this is just whoever the other client is
            for (GameClientHandler client : clients) {
                if (client != sender) {
                    client.writer.println(message);
                }
            }
        }
    }
}

