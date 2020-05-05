package com.vztot.client;

import com.google.gson.Gson;
import com.vztot.Main;
import com.vztot.model.Message;
import com.vztot.model.Request;
import com.vztot.model.RequestType;
import com.vztot.model.Storage;
import com.vztot.model.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class Client {
    public static final int TICK_RATE = 1000;
    private User user;
    private String ipAddress;
    private int port;
    private Storage<Message> storage;
    private ClientDaemon daemon;

    public Client(User user, String ipAddress, int port) {
        this.user = user;
        this.ipAddress = ipAddress;
        this.port = port;
        storage = new Storage<>();
        daemon = new ClientDaemon(TICK_RATE);
        System.setProperty("console.encoding","utf-8");
        init();
    }

    public static void main(String[] args) {
        Client client = new Client(new User("vztot"), "176.37.115.67", 7777);
    }

    private void init() {
        Scanner systemScanner = new Scanner(System.in, Main.ENCODING);
        System.out.println("Info: Initialization complete. "
                + "For exit enter \"/exit\". Feel free to start chat");

        while (true) {
            if (systemScanner.hasNextLine()) {
                String inputLine = systemScanner.nextLine();
                if (!inputLine.equals("")) {
                    if (inputLine.equals("/exit")) {
                        System.exit(0);
                    }

                    try (Socket socket = new Socket()) {
                        socket.connect(new InetSocketAddress(ipAddress, port), 3000);
                        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true,
                                Charset.forName("UTF-8"));
                        Scanner inputScanner = new Scanner(socket.getInputStream(), Main.ENCODING);

                        Message message = new Message(user, System.currentTimeMillis(), inputLine);
                        Request request = new Request(RequestType.MESSAGE, message);
                        String json = new Gson().toJson(request);
                        printWriter.println(json);

                        if (inputScanner.hasNextLine()) {
                            String answer = inputScanner.nextLine();
                            if (!answer.equals("true")) {
                                System.out.println("err: Server didn't received your message");
                            }
                        }
                    } catch (IOException e) {
                        System.out.println("err: Cant connect to server");
                    }
                }
            }
        }
    }

    private class ClientDaemon {
        private int tickRate;

        public ClientDaemon(int tickRate) {
            this.tickRate = tickRate;
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    System.setProperty("console.encoding","utf-8");
                    try (Socket socket = new Socket()) {
                        socket.connect(new InetSocketAddress(ipAddress, port), 3000);
                        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true,
                                Charset.forName("UTF-8"));
                        Scanner inputScanner = new Scanner(socket.getInputStream(), Main.ENCODING);

                        Request request = new Request(RequestType.DAEMON, storage.hash());
                        String json = new Gson().toJson(request);
                        printWriter.println(json);

                        if (inputScanner.hasNextLine()) {
                            String answer = inputScanner.nextLine();
                            if (!answer.equals("true")) {
                                if (answer.length() > 0) {
                                    int sizeBefore = storage.size();
                                    Request serverResponse =
                                            new Gson().fromJson(answer, Request.class);
                                    switch (serverResponse.getRequestType()) {
                                        case STORAGE: {
                                            storage.update(serverResponse.getStorage());
                                            break;
                                        }
                                        default: {
                                            break;
                                        }
                                    }
                                    int sizeAfter = storage.size();
                                    if (sizeBefore < sizeAfter) {
                                        List<Message> list =
                                                storage.get().subList(sizeBefore, sizeAfter);
                                        list.stream()
                                                .filter(message -> !message.getUser().equals(user))
                                                .forEach(message -> System.out.println(message));
                                    }
                                } else {
                                    System.out.println("err: Wrong server answer");
                                }
                            }
                        }
                    } catch (IOException e) {
                        System.out.println("err: Daemon can't connect to server");
                    }
                }
            }, tickRate, tickRate);
        }
    }
}
