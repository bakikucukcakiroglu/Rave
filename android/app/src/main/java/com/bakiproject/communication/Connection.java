package com.bakiproject.communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public abstract class Connection implements Runnable {
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    public final InetAddress address;
    private volatile boolean isRunning = true;

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
        onStopped();
    }

    public void sendMessage(Message message) throws IOException {
        out.writeObject(message);
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                Message msg = (Message) in.readObject();
                messageReceived(msg);
                System.out.println("new message of type "+ msg.getClass().getSimpleName());
            } catch (IOException | ClassNotFoundException e) {
                close();
            }
        }
    }

    abstract void messageReceived(Message message);

    void onStopped(){}
}
