package com.bakiproject.communication;

import com.bakiproject.Server;
import com.bakiproject.UserInfo;
import com.bakiproject.broadcast.BroadcastClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Consumer;

public class CommunicationClient {
    ClientConnection connection;

    private final String username;
    final Consumer<Set<UserInfo>> onUsersReceived;

    public CommunicationClient(InetAddress address, int port, String username, Consumer<Set<UserInfo>> onUsersReceived) throws IOException {
        this.username = username;
        this.onUsersReceived = onUsersReceived;
        Socket socket = new Socket(address, port);
        connection = new ClientConnection(socket);
        connection.run();
    }

    public void close() {
        connection.close();
    }



    public static void main(String[] args) throws IOException, InterruptedException {
        BroadcastClient client = new BroadcastClient(a->{});
        Thread.sleep(7000);

        List<Server> announcements = new ArrayList<>(client.getAvailableServers());
        for (int i = 0; i < announcements.size(); i++) {
            System.out.printf("%d: %s%n", i, announcements.get(i));
        }
        System.out.print("Pick: ");
        int i = new Scanner(System.in).nextInt();
        System.out.println(i);
        InetAddress addr = announcements.get(i).addr();
        new CommunicationClient(addr, 8000, "asd", a->{});
        System.out.print("Done at addr:");
        System.out.println(addr);
        //noinspection ResultOfMethodCallIgnored
        System.in.read();
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
            }
        }
    }
}
