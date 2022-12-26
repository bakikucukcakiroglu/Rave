package com.bakiproject.communication;

import android.util.Pair;

import com.bakiproject.ManyToOneBarrier;
import com.bakiproject.UserInfo;
import com.bakiproject.streams.Observable;
import com.bakiproject.streams.StatefulObservable;
import com.bakiproject.streams.StatefulSubject;
import com.bakiproject.streams.StreamThread;
import com.bakiproject.streams.Subject;

import java.net.*;
import java.io.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CommunicationServer {
    private ServerSocket server;

    HashSet<ServerConnection> connections = new HashSet<>();

    private boolean isOpen = true;

    private final String username;

    /**
     * Ortak thread'de broadcastleri yapmamızı sağlayan bir stream yarattım. Bu sayede her şey tek
     * thread'den çalışıyor, concurrency ile uğraşmıyoruz.
     */
    Subject<Function<ServerConnection, Message>> broadcastRequestsStream = new Subject<>();

    /**
     * User listemiz değiştiğinde streame yeni liste akıyor. Buraya yapılan tüm accept'ler
     * messages thread'den geliyor
     */
    private final StatefulSubject<Set<UserInfo>> userInfoUpdatesStream
            = new StatefulSubject<>(Collections.emptySet());

    /**
     * Ana sınıfa müziği başlat eventi yollamak istediğimizde buraya event atıyoruz.
     */
    private final Subject<Long> startMusicEventsStream = new Subject<>();

    /**
     * Bu stream her bizim connectionlarımızdan biri düştüğünde veya clientlardan biri timingupdate
     * mesajı yolladığında yeni listeyi receive ediyor. Messages thread'de çalışıyor
     */
    private final Subject<Set<ServerConnection>> timingUpdatesStream = new Subject<>();


    public CommunicationServer(String roomName, String username) {
        this.username = username;

        Subject<Pair<ServerConnection, Message>> allMessagesStream = new Subject<>();
        StreamThread messagesThread = new StreamThread();

        try {
            server = new ServerSocket(8000);
        } catch (IOException i) {
            i.printStackTrace();
        }

        broadcastRequestsStream
                .subscribeOnThread(messagesThread)
                .subscribe(msg -> connections.forEach(c -> c.sendMessage(msg.apply(c))));

        /*
         * Ortak thread'de mesajları işleyen bir mesaj işleme streami yarattım. Bu sayede her şey tek
         * thread'den çalışıyor, concurrency ile uğraşmıyoruz.
         */

        allMessagesStream
                .subscribeOnThread(messagesThread)
                .filter(pair -> pair.second instanceof Message.UserIntroMessage)
                .subscribe(pair -> {
                    pair.first.userInfo = new UserInfo(
                            ((Message.UserIntroMessage) pair.second).info(),
                            pair.first.address.getHostAddress());

                    connections.add(pair.first);

                    Set<UserInfo> currUsers = getUsers();
                    userInfoUpdatesStream.accept(currUsers);
                    broadcastRequestsStream.accept(c -> new Message.UsersListUpdateMessage(currUsers));
                });

        allMessagesStream
                .subscribeOnThread(messagesThread)
                .filter(pair -> pair.second instanceof Message.DisconnectMessage)
                .subscribe(pair -> {
                    System.out.println("Removed " + pair.first.userInfo);

                    connections.remove(pair.first);
                    Set<UserInfo> currUsers = getUsers();
                    userInfoUpdatesStream.accept(currUsers);
                    broadcastRequestsStream.accept(c -> new Message.UsersListUpdateMessage(currUsers));

                    timingUpdatesStream.accept(connections
                            .stream()
                            .filter(conn -> conn.timeDifference != Long.MAX_VALUE)
                            .collect(Collectors.toSet()));
                });

        allMessagesStream
                .subscribeOnThread(messagesThread)
                .filter(pair -> pair.second instanceof Message.GetTimeResponse)
                .subscribe(pair -> {
                    Message.GetTimeResponse msg = (Message.GetTimeResponse) pair.second;
                    long ctm = System.currentTimeMillis();
                    pair.first.timeDifference = (ctm + msg.millisTimeRequestSent()) / 2 - msg.millisTimeResponseSent();

                    timingUpdatesStream.accept(connections
                            .stream()
                            .filter(conn -> conn.timeDifference != Long.MAX_VALUE)
                            .collect(Collectors.toSet()));
                });


        //Server listener thread sadece yeni bağlantıları receive ediyor ve her bağlantı için connection objesini yaratıyor
        Thread serverListenerThread = new Thread(() -> {
            while (isOpen) {
                try {
                    Socket socket = server.accept();
                    ServerConnection connection = new ServerConnection(socket);
                    connection
                            .getMessageStream()
                            .map(m -> new Pair<>(connection, m))
                            .subscribe(allMessagesStream);

                    connection.start();
                } catch (IOException e) {
                    close();
                    e.printStackTrace();
                }
            }
        });
        serverListenerThread.start();
    }

    private Set<UserInfo> getUsers() {
        Set<UserInfo> currUsers = new HashSet<>();
        currUsers.add(new UserInfo(username, null, null));
        connections.forEach(sc -> currUsers.add(sc.userInfo));
        return currUsers;
    }

    public void close() {
        isOpen = false;
        try {
            server.close();
            connections.forEach(Connection::close);

            userInfoUpdatesStream.accept(Collections.emptySet());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doStartMusicSequence() {

        broadcastRequestsStream.accept(connection ->
        {
            connection.timeDifference = Long.MAX_VALUE;
            return new Message.GetTimeMessage();
        });


        timingUpdatesStream
                .filter(c -> c.size() == connections.size())
                .once()
                .subscribe(connections -> {
                    long musicTime = System.currentTimeMillis() + 1000;

                    broadcastRequestsStream.accept(connection ->
                            new Message.StartMusicAtTimeMessage(musicTime - connection.timeDifference));

                    startMusicEventsStream.accept(musicTime);
                });
    }

    public StatefulObservable<Set<UserInfo>> getUserInfoUpdatesStream() {
        return userInfoUpdatesStream;
    }

    public Observable<Long> getStartMusicEventsStream() {
        return startMusicEventsStream;
    }

    private static class ServerConnection extends Connection {

        UserInfo userInfo = null;
        long timeDifference = Long.MAX_VALUE;

        public ServerConnection(Socket socket) throws IOException {
            super(socket, true);
        }
    }
}

