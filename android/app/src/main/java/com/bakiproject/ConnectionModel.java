package com.bakiproject;

import androidx.annotation.NonNull;

import com.bakiproject.broadcast.BroadcastClient;
import com.bakiproject.broadcast.BroadcastServer;
import com.bakiproject.communication.CommunicationClient;
import com.bakiproject.communication.CommunicationServer;

import java.io.IOException;
import java.util.Set;

import com.bakiproject.react.ReactObservable;
import com.bakiproject.react.WritableWrapper;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;


public class ConnectionModel extends ReactContextBaseJavaModule {


    enum State {READY, CONNECTED, SERVING}

    BroadcastClient broadcastClient;

    CommunicationClient communicationClient = null;

    BroadcastServer broadcastServer = null;
    CommunicationServer communicationServer = null;

    State state = State.READY;

    ReactObservable<Set<Server>> serverListObservable = new ReactObservable<>(WritableWrapper::wrap);
    ReactObservable<State> stateObservable = new ReactObservable<>(WritableWrapper::wrap);
    ReactObservable<Set<UserInfo>> userListObservable = new ReactObservable<>(WritableWrapper::wrap);


    public ConnectionModel(ReactApplicationContext context) {
        try {
            broadcastClient = new BroadcastClient(serverListObservable);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
            communicationServer = new CommunicationServer(roomName, username, clients -> {
                broadcastServer.setCurrentMembers(clients.size());
                userListObservable.accept(clients);
            });
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
    public void connectToServer(Server server, String username) {
        if (stateObservable.getState() != State.READY) {
            return;
        }

        try {
            communicationClient = new CommunicationClient(
                    server.addr(),
                    8000,
                    username,
                    userListObservable);
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
    public void subscribeToServerList(Callback onServersChanged) {
        serverListObservable.subscribe(onServersChanged);
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void subscribeToState(Callback onStateChanged) {
        stateObservable.subscribe(onStateChanged);
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void subscribeToUserList(Callback onUserListChanged) {
        userListObservable.subscribe(onUserListChanged);
    }


}
