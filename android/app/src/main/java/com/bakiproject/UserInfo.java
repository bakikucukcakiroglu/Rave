package com.bakiproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bakiproject.react.ReactSerialisable;
import com.bakiproject.react.WritableWrapper;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Objects;

public final class UserInfo implements ReactSerialisable {
    private final String username;
    private final byte[] profilePic;
    @Nullable
    private final InetAddress address;

    public UserInfo(String username, byte[] profilePic, @Nullable InetAddress address) {
        this.username = username;
        this.profilePic = profilePic;
        this.address = address;
    }

    public UserInfo(UserInfo info, @Nullable InetAddress address) {
        this.username = info.username;
        this.profilePic = info.profilePic;
        this.address = address;
    }

    public String username() {
        return username;
    }

    public byte[] profilePic() {
        return profilePic;
    }

    public InetAddress address() {
        return address;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        UserInfo that = (UserInfo) obj;
        return Objects.equals(this.username, that.username) &&
                Arrays.equals(this.profilePic, that.profilePic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, Arrays.hashCode(profilePic));
    }

    @NonNull
    @Override
    public String toString() {
        return "UserInfo[" +
                "username=" + username + ", " +
                "profilePic=" + Arrays.toString(profilePic) + ']';
    }

    @Override
    public WritableWrapper toReact() {
        WritableMap map = Arguments.createMap();
        map.putString("username", username);
        map.putString("address", address == null ? "" : address.getHostAddress());
        return WritableWrapper.wrap(map);
    }

}
