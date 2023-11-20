package org.efimov;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class ClientHandler implements Runnable {

    private BufferedReader in;
    private PrintWriter out;
    private List<PrintWriter> clients;

    public ClientHandler(Socket clientSocket, PrintWriter out, List<PrintWriter> clients) throws IOException {
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.out = out;
        this.clients = clients;

    }

    @Override
    public void run() {
        try {
            out.println("Hello, welcome to our chat. What's your name?");
            String clientName = in.readLine();
            out.println("Your name: " + clientName + ".\n"
                    + "All needs your message!");
            try (PrintWriter log = new PrintWriter(new FileWriter("C:\\Users\\Сергей\\IdeaProjects\\MultiThreading\\ClientServerApp\\Server\\File.log", true))) {
                log.println(getCurrentDataTime() + " " + clientName + ": " + "Присоединился к чату");
            }
            System.out.println("Server: Подключение нового клиента - " + clientName);
            Server.broadcast(clientName + " has joined the chat.", out, clients);

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Message from " + clientName + ": " + message);
                Server.broadcast(clientName + ": " + message, out, clients);

                try (PrintWriter log = new PrintWriter(new FileWriter("file.log", true))) {
                    log.println(getCurrentDataTime() + " " + clientName + ": " + message);
                }
            }


            System.out.println("Server: Клиент покинул чат - " + clientName);
            Server.broadcast(clientName + " has left the chat.", out, clients);
            clients.remove(out);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getCurrentDataTime() {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyy HH:mm:ss");
        return dateTime.format(formatter);
    }
}