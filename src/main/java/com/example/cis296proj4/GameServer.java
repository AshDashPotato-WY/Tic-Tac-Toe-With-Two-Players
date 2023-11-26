package com.example.cis296proj4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class GameServer {
    private static final int PORT = 5858;
    private static Map<String, PrintWriter> clients = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running and waiting for players...");

            while (clients.size() < 2) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New player connected: " + clientSocket);

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Ask for the client's name
                out.println("Enter your name:");
                String clientName = in.readLine();

                // Store the client's name and PrintWriter in the map
                clients.put(clientName, out);

                System.out.println("Player " + clientName + " has joined.");

                // Start a new thread to handle client messages
                new Thread(new ClientHandler(clientName, in)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //remember that the key for the map is a String (the client name)
    //and that the value for the map is a PrintWriter
    static void broadcast(String sender, String message) {
        for (Map.Entry<String, PrintWriter> client : clients.entrySet()) {
            if(sender != client.getKey()) {
                client.getValue().println(sender + ": " + message);
            }

        }
        System.out.println(sender + ": " + message);
    }

    public static void removeClient(String clientName) {
        clients.remove(clientName);
    }
}