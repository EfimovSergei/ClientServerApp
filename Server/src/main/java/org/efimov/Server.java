package org.efimov;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static List<PrintWriter> clients = new ArrayList<>();

    public static void main(String[] args) throws IOException {


        ServerSocket serverSocket = getSettings();
        System.out.println("Start server..... ");
        while (true) {
            Socket clientSocket = serverSocket.accept();
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            clients.add(out);
            Thread clientThread = new Thread(new ClientHandler(clientSocket, out, clients));
            clientThread.start();
        }
    }


    public static void broadcast(String message, PrintWriter sender, List<PrintWriter> clients) {
        for (PrintWriter client : clients) {
            if (client != sender) {
                client.println(message);
            }
        }
    }

    public static ServerSocket getSettings() {
        String settings = "C:\\Users\\Сергей\\IdeaProjects\\MultiThreading\\ClientServerApp\\Server\\settings.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(settings))) {
            int port = Integer.parseInt(reader.readLine());
            return new ServerSocket(port);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}