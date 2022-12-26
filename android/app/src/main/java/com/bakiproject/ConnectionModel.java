package com.bakiproject;

import android.media.MediaPlayer;

import androidx.annotation.NonNull;

import com.bakiproject.broadcast.BroadcastClient;
import com.bakiproject.broadcast.BroadcastServer;
import com.bakiproject.communication.CommunicationClient;
import com.bakiproject.communication.CommunicationServer;

import java.io.IOException;
import java.net.InetAddress;
import java.time.Clock;
import java.util.Collections;
import java.util.Set;

import com.bakiproject.streams.StatefulSubject;
import com.bakiproject.streams.Subject;
import com.bakiproject.react.WritableWrapper;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;

public class ConnectionModel extends ReactContextBaseJavaModule {

    enum State {
        READY, CONNECTED, SERVING
    }

    BroadcastClient broadcastClient;

    CommunicationClient communicationClient = null;

    BroadcastServer broadcastServer = null;
    CommunicationServer communicationServer = null;

    final MediaPlayer mp;

    StatefulSubject<Set<Server>> serverListObservable = new StatefulSubject<>(Collections.emptySet());
    StatefulSubject<State> stateObservable = new StatefulSubject<>(State.READY);
    StatefulSubject<Set<UserInfo>> userListObservable = new StatefulSubject<>(Collections.emptySet());

    public ConnectionModel(ReactApplicationContext context, MediaPlayer mp) {
        this.mp = mp;
        try {
            broadcastClient = new BroadcastClient(serverListObservable);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * @Override
     * public void initialize() {
     * super.initialize();
     *
     * serverListObservable.subscribe(list -> this
     * .getReactApplicationContext()
     * .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
     * .emit("serverListChange", list));
     * stateObservable.subscribe(state -> this
     * .getReactApplicationContext()
     * .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
     * .emit("stateChange", state));
     * userListObservable.subscribe(list -> this
     * .getReactApplicationContext()
     * .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
     * .emit("userListChange", list));
     * }
     */

    @NonNull
    @Override
    public String getName() {
        return "ConnectionModel";
    }

    private void startMusicAtTime(long time) {
        /*new Thread(() -> {
            while (Clock.systemUTC().millis() < time) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });*/
        mp.start();
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
                    .getStartMusicEventsStream()
                    .subscribe(this::startMusicAtTime);

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

            communicationServer
                    .getStartMusicEventsStream()
                    .subscribe(this::startMusicAtTime);

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

        communicationServer.doStartMusicSequence();
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void stopMusic() {
        mp.stop();


    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public String getState() {
        return WritableWrapper.wrap(stateObservable.getState()).getObj();
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public WritableArray getUserList() {
        return WritableWrapper.wrap(userListObservable.getState()).getObj();
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public WritableArray getServerList() {
        return WritableWrapper.wrap(serverListObservable.getState()).getObj();
    }
}
