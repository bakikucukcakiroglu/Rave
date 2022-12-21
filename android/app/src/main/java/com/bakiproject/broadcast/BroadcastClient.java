package com.bakiproject.broadcast;

import com.bakiproject.Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.time.Clock;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class BroadcastClient {

    private final MulticastSocket socket;
    final Consumer<Set<Server>> onServerListChanged;
    InetAddress group;
    ListenerThread listenerThread;

    HashSet<Server> availableServers = new HashSet<>();

    public BroadcastClient(Consumer<Set<Server>> onServerListChanged) throws IOException {
        this.onServerListChanged = onServerListChanged;
        socket = new MulticastSocket(BroadcastProtocol.BROADCAST_PORT);
        group = InetAddress.getByName(BroadcastProtocol.BROADCAST_SERVER);
        socket.joinGroup(group);
        // don't wait for request...just send a quote
        listenerThread = new ListenerThread();
        listenerThread.start();
    }

    public Set<Server> getAvailableServers() {
        return availableServers;
    }

    private class ListenerThread extends Thread {
        boolean isRunning = true;

        @Override
        public void run() {
            while (isRunning) {
                try {
                    byte[] buf = BroadcastProtocol.REQUEST_HEADER.getBytes();
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, group, BroadcastProtocol.BROADCAST_PORT);
                    socket.send(packet);


                    byte[] buf2 = new byte[512];
                    DatagramPacket packet2 = new DatagramPacket(buf2, buf2.length);

                    long time_millis = Clock.systemUTC().millis();

                    HashSet<Server> announcements = new HashSet<>();

                    while (Clock.systemUTC().millis() - time_millis < 5000) {
                        socket.receive(packet2);
                        //System.out.println(packet2.getAddress());
                        String message = new String(packet2.getData(), packet2.getOffset(), packet2.getLength()).trim();
                        Optional<Server> announcement = Server.fromMessage(packet2.getAddress(), message);
                        announcement.ifPresent(announcements::add);
                    }

                    availableServers = announcements;
                    onServerListChanged.accept(availableServers);
                    Thread.sleep(5000);
                } catch (IOException | InterruptedException e) {
                    isRunning = false;
                }
            }
        }
    }
}
