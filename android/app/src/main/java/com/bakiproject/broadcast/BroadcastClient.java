package com.bakiproject.broadcast;

import com.bakiproject.Server;
import com.bakiproject.streams.Observable;
import com.bakiproject.streams.RecentEventsSubject;
import com.bakiproject.streams.StatefulObservable;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class BroadcastClient {

    private final MulticastSocket socket;
    InetAddress group;

    private final RecentEventsSubject<Server> serverListUpdatesStream;

    public BroadcastClient() throws IOException {
        socket = new MulticastSocket(BroadcastProtocol.BROADCAST_PORT);
        group = InetAddress.getByName(BroadcastProtocol.BROADCAST_SERVER);
        socket.joinGroup(group);

        serverListUpdatesStream = new RecentEventsSubject<>(2000);

        new Thread(() -> {
            byte[] buf2 = new byte[512];
            DatagramPacket packet2 = new DatagramPacket(buf2, buf2.length);

            while (true) {
                try {
                    socket.receive(packet2);
                    String message = new String(packet2.getData(), packet2.getOffset(), packet2.getLength()).trim();
                    Server
                            .fromMessage(packet2.getAddress().getHostAddress(), message)
                            .ifPresent(serverListUpdatesStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        serverListUpdatesStream.subscribe(System.out::println);
    }

    public Observable<Set<Server>> getServerListUpdatesStream() {
        return serverListUpdatesStream.map(HashSet::new);
    }
}
