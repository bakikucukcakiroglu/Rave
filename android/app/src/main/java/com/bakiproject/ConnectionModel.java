package com.bakiproject;

import android.media.MediaPlayer;

import androidx.annotation.NonNull;

import com.bakiproject.broadcast.BroadcastClient;
import com.bakiproject.broadcast.BroadcastServer;
import com.bakiproject.communication.CommunicationClient;
import com.bakiproject.communication.CommunicationServer;

import java.io.IOException;
import java.net.InetAddress;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.bakiproject.streams.StatefulObservable;
import com.bakiproject.streams.StatefulSubject;
import com.bakiproject.react.WritableWrapper;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;

public class ConnectionModel extends ReactContextBaseJavaModule {

    enum State {
        READY, CONNECTED, SERVING
    }

    public enum MusicState {
        PAUSED, PLAYING, STOPPED, WAIT
    }

    BroadcastClient broadcastClient;

    CommunicationClient communicationClient = null;

    BroadcastServer broadcastServer = null;
    CommunicationServer communicationServer = null;

    final MediaPlayer mp;

    StatefulObservable<Set<Server>> serverListObservable;
    StatefulSubject<State> stateObservable = new StatefulSubject<>(State.READY);
    StatefulSubject<MusicState> musicStateObservable = new StatefulSubject<>(MusicState.STOPPED);
    StatefulSubject<Set<UserInfo>> userListObservable = new StatefulSubject<>(Collections.emptySet());

    public ConnectionModel(ReactApplicationContext context, MediaPlayer mp) {
        this.mp = mp;

        try {
            broadcastClient = new BroadcastClient();
            serverListObservable = broadcastClient.getServerListUpdatesStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        stateObservable.subscribe(t -> stopMusic());
    }

    @NonNull
    @Override
    public String getName() {
        return "ConnectionModel";
    }

    private void controlMusicAtTime(MusicPair pair) {
        musicStateObservable.accept(MusicState.WAIT);

        if (pair.state == MusicState.PLAYING) {
            mp.start();
            mp.pause();
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                switch (pair.state) {
                    case PAUSED:
                        mp.pause();
                        break;
                    case PLAYING:
                        mp.start();
                        break;
                    case STOPPED:
                        mp.stop();
                        mp.prepareAsync();
                        break;
                }
                musicStateObservable.accept(pair.state);
            }
        }, Date.from(Instant.ofEpochMilli(pair.time)));
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void startServer(String roomName, String username) {
        if (stateObservable.getState() != State.READY) {
            return;
        }
        System.out.println("Starting server");
        try {
            broadcastServer = new BroadcastServer(roomName);
            communicationServer = new CommunicationServer(
                    roomName,
                    username);

            communicationServer
                    .getControlMusicEventsStream()
                    .subscribe(this::controlMusicAtTime);

            communicationServer
                    .getUserInfoUpdatesStream()
                    .subscribe(userListObservable);

            userListObservable
                    .map(Set::size)
                    .subscribe(broadcastServer::setCurrentMembers);

            stateObservable.accept(State.SERVING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void stopServer() {
        if (stateObservable.getState() != State.SERVING) {
            return;
        }

        System.out.println("Stopping server");

        broadcastServer.close();
        communicationServer.close();
        stateObservable.accept(State.READY);
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void connectToServer(String addr, String username) {
        if (stateObservable.getState() != State.READY) {
            return;
        }

        System.out.println("Connecting to server");

        try {
            communicationClient = new CommunicationClient(
                    InetAddress.getByName(addr),
                    8000,
                    username);

            communicationClient
                    .getControlMusicEventsStream()
                    .subscribe(this::controlMusicAtTime);

            communicationClient
                    .getConnectionLostEventStream()
                    .map(a -> State.READY)
                    .subscribe(stateObservable);

            communicationClient
                    .getUserInfoUpdatesStream()
                    .subscribe(userListObservable);

            stateObservable.accept(State.CONNECTED);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void disconnectFromServer() {
        if (stateObservable.getState() != State.CONNECTED) {
            return;
        }

        System.out.println("Disconnecting from server");

        communicationClient.close();
        communicationClient = null;
        stateObservable.accept(State.READY);
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void startMusic() {
        if (stateObservable.getState() != State.SERVING) {
            return;
        }
        musicStateObservable.accept(MusicState.WAIT);

        communicationServer.doControlMusicSequence(MusicState.PLAYING);
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void pauseMusic() {
        if (stateObservable.getState() != State.SERVING) {
            return;
        }
        musicStateObservable.accept(MusicState.WAIT);

        communicationServer.doControlMusicSequence(MusicState.PAUSED);
    }


    @ReactMethod(isBlockingSynchronousMethod = true)
    public void stopMusic() {
        if (stateObservable.getState() != State.SERVING) {
            return;
        }
        musicStateObservable.accept(MusicState.WAIT);

        communicationServer.doControlMusicSequence(MusicState.STOPPED);
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public WritableArray getUserList() {
        return WritableWrapper.wrap(userListObservable.getState()).getObj();
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public WritableArray getServerList() {
        return WritableWrapper.wrap(serverListObservable.getState()).getObj();
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public String getState() {
        return WritableWrapper.wrap(stateObservable.getState()).getObj();
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public String getMusicState() {
        return WritableWrapper.wrap(musicStateObservable.getState()).getObj();
    }

    public static class MusicPair {
        public final MusicState state;
        public final long time;

        public MusicPair(MusicState state, long time) {
            this.state = state;
            this.time = time;
        }
    }
}
