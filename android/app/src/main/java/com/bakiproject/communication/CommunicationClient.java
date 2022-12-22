package com.bakiproject.communication;

import com.bakiproject.Server;
import com.bakiproject.UserInfo;
import com.bakiproject.broadcast.BroadcastClient;

import java.io.IOException;
import java.lang.reflect.Member;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

public class CommunicationClient {
    ClientConnection connection;

    private final String username;
    final Consumer<Set<UserInfo>> onUsersReceived;
    private Runnable onConnectionClosed;
    private LongConsumer startMusicAtTime;

    public CommunicationClient(InetAddress address,
                               int port,
                               String username,
                               Consumer<Set<UserInfo>> onUsersReceived,
                               Runnable onConnectionClosed,
                               LongConsumer startMusicAtTime) throws IOException {
        this.username = username;
        this.onUsersReceived = onUsersReceived;
        this.onConnectionClosed = onConnectionClosed;
        this.startMusicAtTime = startMusicAtTime;
        Socket socket = new Socket(address, port);
        connection = new ClientConnection(socket);
        new Thread(connection).start();
    }

    public void close() {
        connection.close();
        onConnectionClosed.run();
    }

    private class ClientConnection extends Connection {
        public ClientConnection(Socket socket) throws IOException {
            super(socket, false);
            sendMessage(new Message.UserIntroMessage(username, null));
        }

        @Override
        void messageReceived(Message rawMsg) {
            if (rawMsg instanceof Message.GetTimeMessage) {
                try {
                    sendMessage(new Message.GetTimeResponse(((Message.GetTimeMessage) rawMsg).millisTimeSent(), System.currentTimeMillis()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (rawMsg instanceof Message.UsersListUpdateMessage) {
                onUsersReceived.accept(((Message.UsersListUpdateMessage) rawMsg).users());
            } else if (rawMsg instanceof Message.StartMusicAtTimeMessage) {
                startMusicAtTime.accept(((Message.StartMusicAtTimeMessage) rawMsg).millisTimeStart());
            }
        }

        @Override
        void onStopped() {
            onConnectionClosed.run();
        }
    }
}
