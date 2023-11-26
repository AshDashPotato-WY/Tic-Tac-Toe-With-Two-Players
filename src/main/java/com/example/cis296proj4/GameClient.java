package com.example.cis296proj4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameClient {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 5858;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to the server.");

            // Receive and print the server's welcome message
            System.out.println(in.readLine());

            // Get the client's name
            String clientName = consoleInput.readLine();
            out.println(clientName);

            new Thread(new ServerListener(in)).start();

            while (true) {
                String userInput = consoleInput.readLine();
                out.println(userInput);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
