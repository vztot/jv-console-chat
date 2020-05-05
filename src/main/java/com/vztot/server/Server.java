package com.vztot.server;

import com.google.gson.Gson;
import com.vztot.Main;
import com.vztot.model.Message;
import com.vztot.model.Request;
import com.vztot.model.RequestType;
import com.vztot.model.Storage;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Scanner;

public class Server {
    private int port;
    private Storage<Message> storage;

    public Server(int port) {
        System.setProperty("console.encoding","utf-8");
        this.port = port;
        storage = new Storage<>();
        System.out.println("Running server...");
        run();
    }

    public static void main(String[] args) {
        Server server = new Server(1717);
    }

    private void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ServerBackEnd(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ServerBackEnd implements Runnable {
        Socket socket;

        public ServerBackEnd(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (Scanner scanner = new Scanner(socket.getInputStream(), Main.ENCODING);
                    PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true,
                                    Charset.forName("UTF-8"))) {
                if (scanner.hasNextLine()) {
                    String json = scanner.nextLine();
                    String out = "true";

                    try {
                        Request request = new Gson().fromJson(json, Request.class);
                        switch (request.getRequestType()) {
                            case MESSAGE: {
                                storage.addMessage(request.getMessage());
                                break;
                            }
                            case DAEMON: {
                                if (request.getStorageHash() != storage.hash()) {
                                    Request serverResponse =
                                            new Request(RequestType.STORAGE, storage);
                                    out = new Gson().toJson(serverResponse);
                                }
                                break;
                            }
                            default: {
                                break;
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Request is not recognized: " + json);
                    }
                    printWriter.print(out);

                    //System.out.println("RCD: " + json);
                    //System.out.println("SND: " + out);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
