package com.example.cis296proj4;

import java.io.BufferedReader;
import java.io.IOException;

public class ClientHandler implements Runnable {
    private String clientName;
    private BufferedReader in;

    public ClientHandler(String clientName, BufferedReader in) {
        this.clientName = clientName;
        this.in = in;
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
