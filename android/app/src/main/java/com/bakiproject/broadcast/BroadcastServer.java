package com.bakiproject.broadcast;

import com.bakiproject.Server;

import java.io.IOException;
import java.net.*;

public class BroadcastServer {
    private boolean isOpen = true;
    private final MulticastSocket socket;
    private final InetAddress group;
    private Server announcement;

    public BroadcastServer(String name) throws IOException {
        group = InetAddress.getByName(BroadcastProtocol.BROADCAST_SERVER);
        socket = new MulticastSocket(BroadcastProtocol.BROADCAST_PORT);
        socket.joinGroup(group);
        announcement = new Server(null, name, 8000, 0);
        Thread broadcastListener = new Thread(this::runListener);
        Thread broadcastSender = new Thread(() -> {
            while (isOpen) {
                doBroadcast();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        broadcastListener.start();
        broadcastSender.start();
    }

    public void setCurrentMembers(int currentMembers){
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
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            socket.leaveGroup(group);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            socket.close();
        }
    }

    public void doBroadcast() {
        //System.out.println("Doing announcement.");
        byte[] buf = announcement.toMessage().getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, group, BroadcastProtocol.BROADCAST_PORT);
        try {
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void close() {
        isOpen = false;
    }

    public String packetString(DatagramPacket packet) {
        return "DatagramPacket{" +
                "buf=\"" + new String(packet.getData(), packet.getOffset(), packet.getLength()) +
                "\", offset=" + packet.getOffset() +
                ", length=" + packet.getLength() +
                ", address=" + packet.getAddress() +
                ", port=" + packet.getPort() +
                '}';
    }
}
