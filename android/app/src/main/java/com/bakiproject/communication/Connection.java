package com.bakiproject.communication;

import com.bakiproject.streams.Observable;
import com.bakiproject.streams.Subject;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

public class Connection extends Thread {
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    public final InetAddress address;
    private volatile boolean isRunning = true;

    private final AtomicLong lastReceivedPing = new AtomicLong(System.currentTimeMillis());

    private final Subject<Message> receivedMessagesStream = new Subject<>();
    private final Socket socket;

    Timer timer;

    public Connection(Socket socket, boolean isServer) throws IOException {
        this.socket = socket;
        if (isServer) {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        } else {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        }
        address = socket.getInetAddress();

        receivedMessagesStream
                .filter(msg -> msg instanceof Message.PingMessage)
                .subscribe(m -> lastReceivedPing.set(System.currentTimeMillis()));


        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendMessage(new Message.PingMessage());
                if (System.currentTimeMillis() - lastReceivedPing.get() > 4000) {
                    System.out.println("Killing");
                    close();
                }
            }
        }, 0, 2000);
    }

    public void close() {
        timer.cancel();
        isRunning = false;
        Connection.this.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                System.out.println("Received message of type " + msg.getClass().getSimpleName());
            } catch (EOFException | SocketException e) {
                close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public Observable<Message> getMessageStream() {
        return receivedMessagesStream.filter(m -> !(m instanceof Message.PingMessage));
    }

}
