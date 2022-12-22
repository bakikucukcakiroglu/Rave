package com.bakiproject;

import android.media.MediaPlayer;

import androidx.annotation.NonNull;

import com.bakiproject.broadcast.BroadcastClient;
import com.bakiproject.broadcast.BroadcastServer;
import com.bakiproject.communication.CommunicationClient;
import com.bakiproject.communication.CommunicationServer;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.Timer;

import com.bakiproject.react.ReactObservable;
import com.bakiproject.react.WritableWrapper;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class ConnectionModel extends ReactContextBaseJavaModule {

    enum State {
        READY, CONNECTED, SERVING
    }

    BroadcastClient broadcastClient;

    CommunicationClient communicationClient = null;

    BroadcastServer broadcastServer = null;
    CommunicationServer communicationServer = null;

    final MediaPlayer mp;

    ReactObservable<Set<Server>> serverListObservable = new ReactObservable<>(WritableWrapper::wrap,
            Collections.emptySet());
    ReactObservable<State> stateObservable = new ReactObservable<>(WritableWrapper::wrap, State.READY);
    ReactObservable<Set<UserInfo>> userListObservable = new ReactObservable<>(WritableWrapper::wrap,
            Collections.emptySet());

    public ConnectionModel(ReactApplicationContext context) {
        try {
            broadcastClient = new BroadcastClient(serverListObservable);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mp = MediaPlayer.create(context, R.raw.piano);
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

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void startServer(String roomName, String username) {
        if (stateObservable.getState() != State.READY) {
            return;
        }

        try {
            broadcastServer = new BroadcastServer(roomName);
            communicationServer = new CommunicationServer(
                    roomName,
                    username,
                    clients -> {
                        broadcastServer.setCurrentMembers(clients.size());
                        userListObservable.accept(clients);
                    },
                    time->{
                        Timer timer = new Timer();
                        //timer.schedule();
                    }
                    );
            stateObservable.accept(State.SERVING);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void stopServer() {
        if (stateObservable.getState() != State.SERVING) {
            return;
        }

        broadcastServer.close();
        communicationServer.close();
        stateObservable.accept(State.READY);
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void connectToServer(String addr, String username) {
        if (stateObservable.getState() != State.READY) {
            return;
        }

        try {
            communicationClient = new CommunicationClient(
                    InetAddress.getByName(addr),
                    8000,
                    username,
                    userListObservable,
                    () -> stateObservable.accept(State.READY));
            stateObservable.accept(State.CONNECTED);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void disconnectFromServer() {
        if (stateObservable.getState() != State.CONNECTED) {
            return;
        }

        communicationClient.close();
        communicationClient = null;
        stateObservable.accept(State.READY);
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
