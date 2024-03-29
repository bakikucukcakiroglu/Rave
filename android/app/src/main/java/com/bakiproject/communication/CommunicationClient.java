package com.bakiproject.communication;

import com.bakiproject.ConnectionModel;
import com.bakiproject.UserInfo;
import com.bakiproject.streams.Observable;
import com.bakiproject.streams.Single;
import com.bakiproject.streams.SingleSubject;
import com.bakiproject.streams.StatefulObservable;
import com.bakiproject.streams.StatefulSubject;
import com.bakiproject.streams.Subject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collections;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class CommunicationClient {
    Connection connection;

    /**
     * User listemiz değiştiğinde streame yeni liste akıyor. Buraya yapılan tüm accept'ler
     * messages thread'den geliyor
     */
    private final StatefulSubject<Set<UserInfo>> userInfoUpdatesStream
            = new StatefulSubject<>(Collections.emptySet());

    /**
     * Bağlantı kopunca buraya bir event düşüyor. Haliyle bir defa yaşanabilir bu.
     */
    private final SingleSubject<Void> connectionLostEventStream
            = new SingleSubject<>();


    /**
     * Ana sınıfa müziği başlat eventi yollamak istediğimizde buraya event atıyoruz.
     */
    private final Subject<ConnectionModel.MusicPair> controlMusicEventsStream = new Subject<>();


    public CommunicationClient(InetAddress address,
                               int port,
                               String username) throws IOException {

        Socket socket = new Socket(address, port);
        connection = new Connection(socket, false);

        Observable<Message> messagesStream = connection.getMessageStream();

        messagesStream
                .filter(msg -> msg instanceof Message.GetTimeMessage)
                .subscribe(msg -> {
                    Message m = new Message.GetTimeResponse(((Message.GetTimeMessage) msg).millisTimeSent(), System.currentTimeMillis());
                    connection.sendMessage(m);
                });

        messagesStream
                .filter(msg -> msg instanceof Message.UsersListUpdateMessage)
                .map(msg -> ((Message.UsersListUpdateMessage) msg).users())
                .subscribe(userInfoUpdatesStream);

        messagesStream
                .filter(msg -> msg instanceof Message.ControlMusicAtTimeMessage)
                .map(msg -> ((Message.ControlMusicAtTimeMessage) msg).millisTimeStart())
                .subscribe(controlMusicEventsStream);


        messagesStream
                .filter(msg -> msg instanceof Message.DisconnectMessage)
                .subscribe(a -> connectionLostEventStream.accept(null));

        connection.start();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                connection.sendMessage(new Message.UserIntroMessage(username, null));
            }
        }, 500);
    }

    public void close() {
        connection.close();
    }

    public StatefulObservable<Set<UserInfo>> getUserInfoUpdatesStream() {
        return userInfoUpdatesStream;
    }

    public Single<Void> getConnectionLostEventStream() {
        return connectionLostEventStream;
    }

    public Observable<ConnectionModel.MusicPair> getControlMusicEventsStream() {
        return controlMusicEventsStream;
    }
}
