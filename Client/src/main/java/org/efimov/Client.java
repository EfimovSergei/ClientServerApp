package org.efimov;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {

    private static BufferedReader inMess;
    private static PrintWriter outMess;
    private static Scanner scannerConsole;
    private static Socket clientSocket = null;


    public static Socket getSettings() {
        String settings = "Client/settings.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(settings))) {
            String ip = reader.readLine();
            int port = Integer.parseInt(reader.readLine());
            return new Socket(ip, port);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void start() {
        try {
            clientSocket = getSettings();
            assert clientSocket != null;

            outMess = new PrintWriter(clientSocket.getOutputStream(), true);
            inMess = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            scannerConsole = new Scanner(System.in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        AtomicBoolean flag = new AtomicBoolean(true);

        new Thread(() -> {
            try {
                while (true) {
                    if (!flag.get()) {
                        inMess.close();
                        clientSocket.close();
                        break;
                    }

                    if (inMess.ready()) {
                        String messFormServer = inMess.readLine();
                        System.out.println(messFormServer);

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();


        new Thread(() -> {
            while (true) {
                if (scannerConsole.hasNext()) {
                    String mess = scannerConsole.nextLine();
                    if (mess.equalsIgnoreCase("exit")) {
                        outMess.println(mess);
                        scannerConsole.close();
                        outMess.close();
                        flag.set(false);
                        break;
                    }
                    outMess.println(mess);
                }
            }
        }).start();
    }
}