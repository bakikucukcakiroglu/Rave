package com.bakiproject;

import com.bakiproject.broadcast.BroadcastProtocol;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.JavaScriptModuleRegistry;
import com.facebook.react.bridge.WritableMap;

import java.net.InetAddress;
import java.util.Objects;
import java.util.Optional;

public final class Server {
    private final InetAddress addr;
    private final String name;
    private final int port;
    private final int currentMembers;

    public Server(InetAddress addr, String name, int port, int currentMembers) {
        this.addr = addr;
        this.name = name;
        this.port = port;
        this.currentMembers = currentMembers;
    }

    public static Optional<Server> fromMessage(InetAddress addr, String annStr) {
        String[] ann = annStr.split("\r\n");
        if (!BroadcastProtocol.ANNOUNCEMENT_HEADER.equals(ann[0])) return Optional.empty();
        return Optional.of(new Server(addr, ann[1], Integer.parseInt(ann[2]), Integer.parseInt(ann[3])));
    }

    public String toMessage() {
        return BroadcastProtocol.ANNOUNCEMENT_HEADER + "\r\n" + name + "\r\n" + port + "\r\n" + currentMembers;
    }

    public InetAddress addr() {
        return addr;
    }

    public String name() {
        return name;
    }

    public int port() {
        return port;
    }

    public int currentMembers() {
        return currentMembers;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Server that = (Server) obj;
        return Objects.equals(this.addr, that.addr) &&
                Objects.equals(this.name, that.name) &&
                this.port == that.port &&
                this.currentMembers == that.currentMembers;
    }

    @Override
    public int hashCode() {
        return Objects.hash(addr, name, port, currentMembers);
    }

    @Override
    public String toString() {
        return "Server[" +
                "addr=" + addr + ", " +
                "name=" + name + ", " +
                "port=" + port + ", " +
                "currentMembers=" + currentMembers + ']';
    }


    public WritableMap getServerAsString() {
        // Create a new JavaScript object to represent the Person
        WritableMap personMap = Arguments.createMap();

        // Add the Person's name and age to the JavaScript object
        personMap.putString("name", this.name);


        // Return the JavaScript object
        return personMap;
    }



}


