package com.example.cis296proj4;

import java.io.BufferedReader;
import java.io.IOException;

public class ClientHandler implements Runnable {
    private String clientName;
    private BufferedReader in;

    private char clientSymbol;

    public ClientHandler(String clientName, BufferedReader in, char clientSymbol) {
        this.clientName = clientName;
        this.in = in;
        this.clientSymbol = clientSymbol;
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                GameServer.broadcast(clientName, message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Handle client disconnect
            System.out.println(clientName + " has left.");
            GameServer.removeClient(clientName);
        }
    }

}
