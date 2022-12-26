package com.bakiproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bakiproject.react.ReactSerialisable;
import com.bakiproject.react.WritableWrapper;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

import java.util.Objects;

public final class UserInfo implements ReactSerialisable {
    private final String username;
    private final String address;

    public UserInfo(String username, byte[] profilePic, @Nullable String address) {
        this.username = username;
        this.address = address;
    }

    public UserInfo(UserInfo info, @Nullable String address) {
        this.username = info.username;
        this.address = address;
    }

    public UserInfo(String username) {
        this(username, null, null);
    }

    public String username() {
        return username;
    }

    public String address() {
        return address;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        UserInfo that = (UserInfo) obj;
        return Objects.equals(this.username, that.username) &&
                Objects.equals(this.address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, Objects.hashCode(address));
    }

    @NonNull
    @Override
    public String toString() {
        return "UserInfo[" +
                "username=" + username + ", " +
                "address=" + address + ']';
    }

    @Override
    public WritableWrapper toReact() {
        WritableMap map = Arguments.createMap();
        map.putString("username", username);
        map.putString("address", address);
        return WritableWrapper.wrap(map);
    }

}
