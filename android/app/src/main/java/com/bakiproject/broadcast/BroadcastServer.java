package com.bakiproject.broadcast;

import android.app.job.JobScheduler;

import com.bakiproject.Server;

import java.io.IOException;
import java.net.*;
import java.util.Timer;
import java.util.TimerTask;

public class BroadcastServer {
    private boolean isOpen = true;
    private final MulticastSocket socket;
    private final InetAddress group;
    private Server announcement;
    Timer timer;

    public BroadcastServer(String name) throws IOException {
        group = InetAddress.getByName(BroadcastProtocol.BROADCAST_SERVER);
        socket = new MulticastSocket(BroadcastProtocol.BROADCAST_PORT);
        socket.joinGroup(group);
        announcement = new Server(null, name, 8000, 1);
        Thread broadcastListener = new Thread(this::runListener);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                doBroadcast();
            }
        }, 0, 500);

        broadcastListener.start();
    }

    public void setCurrentMembers(int currentMembers) {
        announcement = new Server(null, announcement.name(), 8000, currentMembers);
    }

    public void runListener() {
        while (isOpen) {
            try {
                byte[] buf = new byte[512];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String message = new String(packet.getData(), packet.getOffset(), packet.getLength()).trim().toUpperCase();
                if (BroadcastProtocol.REQUEST_HEADER.equals(message)) {
                    System.out.println("Received announce request.");
                    doBroadcast();
                }
            } catch (SocketException ignored) {
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void doBroadcast() {
        System.out.println("Doing announcement.");
        byte[] buf = announcement.toMessage().getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, group, BroadcastProtocol.BROADCAST_PORT);
        try {
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        timer.cancel();
        isOpen = false;
        socket.close();
    }

}
