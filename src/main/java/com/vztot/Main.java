package com.vztot;

import com.vztot.client.Client;
import com.vztot.model.User;
import com.vztot.server.Server;
import java.util.Scanner;

public class Main {
    public static String ENCODING = System.getProperty("console.encoding", "utf-8");
    private static Thread serverThread;
    private static Thread clientThread;
    private static String userName;
    private static String ipAddress;
    private static Integer port;

    public static void main(String[] args) {

        System.setProperty("console.encoding", "utf-8");

        System.out.println("Welcome to jv-console-chat app!");
        System.out.print("Enter username: ");
        userName = new Scanner(System.in, ENCODING).nextLine();

        System.out.print("Enter server address (enter \"localhost\" "
                + "if server is running on this machine): ");
        ipAddress = new Scanner(System.in, ENCODING).nextLine();

        System.out.print("Enter server port: ");
        port = Integer.valueOf(new Scanner(System.in, ENCODING).nextLine());

        if (ipAddress.equalsIgnoreCase("localhost")) {
            System.out.print("Use this app as server: ");
            String answer = new Scanner(System.in, ENCODING).nextLine();
            serverThread = new Thread(() -> {
                Server server = new Server(port);
            });
            if (answer.equalsIgnoreCase("yes")
                    || answer.equalsIgnoreCase("y")) {
                serverThread.start();
            }
        }

        clientThread = new Thread(() -> {
            Client client = new Client(new User(userName), ipAddress, port);
        });
        clientThread.start();
    }
}
