package com.bakiproject.communication;

import com.bakiproject.ConnectionModel;
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
    private final StatefulSubject<Set<UserInfo>> userInfoUpdatesStream;

    /**
     * Ana sınıfa müziği başlat eventi yollamak istediğimizde buraya event atıyoruz.
     */
    private final Subject<ConnectionModel.MusicPair> controlMusicEventsStream = new Subject<>();

    /**
     * Bu stream her bizim connectionlarımızdan biri düştüğünde veya clientlardan biri timingupdate
     * mesajı yolladığında yeni listeyi receive ediyor. Messages thread'de çalışıyor
     */
    private final StatefulSubject<Set<ServerConnection>> timingUpdatesStream
            = new StatefulSubject<>(Collections.emptySet());


    public CommunicationServer(String roomName, String username) {
        this.username = username;

        userInfoUpdatesStream = new StatefulSubject<>(
                Collections.singleton(new UserInfo(username)));

        Subject<MessagePair> allMessagesStream = new Subject<>();
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
                .filter(pair -> pair.msg instanceof Message.UserIntroMessage)
                .subscribe(pair -> {
                    System.out.println("Received UserIntroMessage");
                    pair.connection.userInfo = new UserInfo(
                            ((Message.UserIntroMessage) pair.msg).info(),
                            pair.connection.address.getHostAddress());

                    connections.add(pair.connection);

                    Set<UserInfo> currUsers = getUsers();
                    userInfoUpdatesStream.accept(currUsers);
                    broadcastRequestsStream.accept(c -> new Message.UsersListUpdateMessage(currUsers));
                });

        allMessagesStream
                .subscribeOnThread(messagesThread)
                .filter(pair -> pair.msg instanceof Message.DisconnectMessage)
                .subscribe(pair -> {
                    System.out.println("Removed " + pair.connection);

                    connections.remove(pair.connection);
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
                .filter(pair -> pair.msg instanceof Message.GetTimeResponse)
                .subscribe(pair -> {
                    System.out.println("Received GetTimeResponse");
                    Message.GetTimeResponse msg = (Message.GetTimeResponse) pair.msg;
                    long ctm = System.currentTimeMillis();
                    pair.connection.timeDifference = (ctm + msg.millisTimeRequestSent()) / 2 - msg.millisTimeResponseSent();

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
                            .map(m -> new MessagePair(connection, m))
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
        currUsers.add(new UserInfo(username));
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

    public void doControlMusicSequence(ConnectionModel.MusicState state) {

        timingUpdatesStream.subscribe(x -> {
            System.out.println("Timings Update");
            System.out.println(x);
            System.out.println(connections);
        });

        timingUpdatesStream
                .filter(c -> c.size() == connections.size())
                .once()
                .subscribe(connections -> {
                    long musicTime = System.currentTimeMillis() + 1000;

                    broadcastRequestsStream.accept(connection ->
                            new Message.ControlMusicAtTimeMessage(
                                    new ConnectionModel.MusicPair(
                                            state,
                                            musicTime - connection.timeDifference)));

                    controlMusicEventsStream.accept(
                            new ConnectionModel.MusicPair(
                                    state,
                                    musicTime));
                });

        broadcastRequestsStream.accept(connection ->
        {
            connection.timeDifference = Long.MAX_VALUE;
            return new Message.GetTimeMessage();
        });
    }

    public StatefulObservable<Set<UserInfo>> getUserInfoUpdatesStream() {
        return userInfoUpdatesStream;
    }

    public Observable<ConnectionModel.MusicPair> getControlMusicEventsStream() {
        return controlMusicEventsStream;
    }

    private static class ServerConnection extends Connection {

        UserInfo userInfo = null;
        long timeDifference = Long.MAX_VALUE;

        public ServerConnection(Socket socket) throws IOException {
            super(socket, true);
        }
    }

    static class MessagePair {
        public final ServerConnection connection;
        public final Message msg;

        MessagePair(ServerConnection first, Message second) {
            this.connection = first;
            this.msg = second;
        }
    }


}

