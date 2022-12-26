package com.bakiproject.communication;

import com.bakiproject.streams.Observable;
import com.bakiproject.streams.SingleSubject;
import com.bakiproject.streams.Subject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Connection extends Thread {
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    public final InetAddress address;
    private volatile boolean isRunning = true;

    private final Subject<Message> receivedMessagesStream = new Subject<>();

    public Connection(Socket socket, boolean isServer) throws IOException {
        if (isServer) {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        } else {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        }
        address = socket.getInetAddress();
    }

    public void close() {
        isRunning = false;
        receivedMessagesStream.accept(new Message.DisconnectMessage());
    }

    public void sendMessage(Message message) {
        try {
            out.writeObject(message);
        } catch (IOException e) {
            close();
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                Message msg = (Message) in.readObject();
                receivedMessagesStream.accept(msg);
                System.out.println("new message of type " + msg.getClass().getSimpleName());
            } catch (IOException | ClassNotFoundException e) {
                System.out.print("Connection closed because of ");
                e.printStackTrace();
                close();
            }
        }
    }

    public Observable<Message> getMessageStream() {
        return receivedMessagesStream.filter(m -> !(m instanceof Message.PingMessage));
    }

}
