package com.bakiproject.communication;

import com.bakiproject.UserInfo;

import java.net.*;
import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CommunicationServer {
    private ServerSocket server;

    Set<ServerConnection> connections = new HashSet<>();

    private boolean isOpen = true;

    private final Consumer<Set<UserInfo>> onClientConnected;

    public CommunicationServer(Consumer<Set<UserInfo>> onClientConnected) {
        this.onClientConnected = onClientConnected;
        // starts server and waits for a connection
        try {
            server = new ServerSocket(8000);
        } catch (IOException i) {
            System.err.println(i);
        }
        new Thread(() -> {
            while (isOpen) {
                try {
                    Socket socket = server.accept();
                    System.out.print("New connection from ");
                    System.out.println(socket.getInetAddress());
                    ServerConnection connection = new ServerConnection(socket);
                    new Thread(connection).start();
                } catch (IOException e) {
                    close();
                    System.err.println(e);
                }
            }
        }).start();
    }

    public void close() {
        isOpen = false;
        try {
            server.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void refreshTimings() throws IOException {
        for (ServerConnection connection : connections) {
            connection.sendRefreshTimingsMessage();
        }
    }

    private class ServerConnection extends Connection {

        UserInfo userInfo = null;
        long timeDifference;

        public ServerConnection(Socket socket) throws IOException {
            super(socket, true);
        }

        @Override
        void messageReceived(Message rawMsg) {
            if (rawMsg instanceof Message.GetTimeResponse) {
                Message.GetTimeResponse msg = (Message.GetTimeResponse) rawMsg;
                long ctm = System.currentTimeMillis();
                timeDifference = (ctm + msg.millisTimeRequestSent()) / 2 - msg.millisTimeResponseSent();
                System.out.printf("Time difference: %d%n", timeDifference);
            } else if (rawMsg instanceof Message.UserIntroMessage) {
                userInfo = new UserInfo(((Message.UserIntroMessage) rawMsg).info(), address);
                connections.add(this);

                Message.UsersListUpdateMessage usersMsg = new Message.UsersListUpdateMessage(connections
                        .stream()
                        .map(ServerConnection::getUserInfo)
                        .collect(Collectors.toSet()));

                for (ServerConnection connection : connections) {
                    try {
                        connection.sendMessage(usersMsg);
                    } catch (IOException e) {
                        System.out.println(e);
                    }
                }

                onClientConnected.accept(connections
                        .stream()
                        .map(ServerConnection::getUserInfo)
                        .collect(Collectors.toSet()));
            }
        }

        void sendRefreshTimingsMessage() throws IOException {
            sendMessage(new Message.GetTimeMessage());
        }

        @Override
        void onStopped() {
            connections.remove(this);
        }

        UserInfo getUserInfo(){
            return userInfo;
        }
    }
}

