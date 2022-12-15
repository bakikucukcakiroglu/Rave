package com.bakiproject;

import com.bakiproject.broadcast.BroadcastClient;
import com.bakiproject.broadcast.BroadcastServer;
import com.bakiproject.communication.CommunicationClient;
import com.bakiproject.communication.CommunicationServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableArray;

import java.util.Map;
import java.util.HashMap;
import android.util.Log;



public class ConnectionModel extends ReactContextBaseJavaModule {


    enum State {READY, CONNECTED, SERVING, FAILED}

    BroadcastClient broadcastClient;

    CommunicationClient communicationClient = null;

    BroadcastServer broadcastServer = null;
    CommunicationServer communicationServer = null;

    State state = State.READY;



    public ConnectionModel(ReactApplicationContext context) {
        try {
            broadcastClient = new BroadcastClient();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public String getName() {
        return "ConnectionModel";
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public int thirtyOne() {
        return 31;
    }

    public boolean startServer(String name, Consumer<Set<UserInfo>> onUserInfoChanged) {
        if (state != State.READY)
            return false;

        try {
            broadcastServer = new BroadcastServer(name);
            communicationServer = new CommunicationServer(clients -> {
                broadcastServer.setCurrentMembers(clients.size());
                onUserInfoChanged.accept(clients);
            });
            state = State.SERVING;
            return true;
        } catch (IOException e) {
            System.out.println(e);
            return false;
        }
    }

    public void stopServer() {
        broadcastServer.close();
        communicationServer.close();
        state = State.READY;
    }

    public boolean connectToServer(Server server) {
        if (state != State.READY)
            return false;
        try {
            communicationClient = new CommunicationClient(server.addr(), 8000);
            return true;
        } catch (IOException e) {
            System.out.println(e);
            return false;
        }
    }

    public void disconnectFromServer() {
        communicationClient.close();
    }

    public State getState() {
        return state;
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public WritableArray getAvailableServers() {

        // Create a new ReadableArray object
        WritableArray array = Arguments.createArray();

        broadcastClient.getAvailableServers().forEach(( server) -> {


            array.pushMap(server.getServerAsString());
            System.out.println("çalıştı");
        });
        System.out.println("çalıştı2");


        return array;
    }


}
