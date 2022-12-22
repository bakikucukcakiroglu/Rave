package com.bakiproject.communication;

import com.bakiproject.UserInfo;

import java.net.*;
import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommunicationServer {
    private ServerSocket server;

    Set<ServerConnection> connections = new HashSet<>();

    private boolean isOpen = true;

    private final String username;
    private final Consumer<Set<UserInfo>> onClientConnected;
    private LongConsumer startMusicAtTime;

    public CommunicationServer(String roomName, String username, Consumer<Set<UserInfo>> onClientConnected, LongConsumer startMusicAtTime) {
        this.username = username;
        this.onClientConnected = onClientConnected;
        this.startMusicAtTime = startMusicAtTime;
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
                userInfo = new UserInfo(((Message.UserIntroMessage) rawMsg).info(), address.getHostAddress());
                connections.add(this);

                sendUserListMessage();

                onClientConnected.accept(connections
                        .stream()
                        .map(ServerConnection::getUserInfo)
                        .collect(Collectors.toSet()));
            }
        }

        void sendRefreshTimingsMessage() throws IOException {
            sendMessage(new Message.GetTimeMessage());
        }

        private void sendUserListMessage() {
            Message.UsersListUpdateMessage usersMsg = new Message.UsersListUpdateMessage(
                    Stream.concat(
                                    Stream.of(new UserInfo(username, null, null)),
                                    connections.stream().map(ServerConnection::getUserInfo))
                            .collect(Collectors.toSet()));

            for (ServerConnection connection : connections) {
                try {
                    connection.sendMessage(usersMsg);
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        }

        @Override
        void onStopped() {
            connections.remove(this);
            sendUserListMessage();
        }

        UserInfo getUserInfo() {
            return userInfo;
        }
    }
}

