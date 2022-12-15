package com.bakiproject;

import java.util.Arrays;
import java.util.Objects;

public final class UserInfo {
    private final String username;
    private final byte[] profilePic;

    public UserInfo(String username, byte[] profilePic) {
        this.username = username;
        this.profilePic = profilePic;
    }

    public String username() {
        return username;
    }

    public byte[] profilePic() {
        return profilePic;
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

    @Override
    public String toString() {
        return "UserInfo[" +
                "username=" + username + ", " +
                "profilePic=" + Arrays.toString(profilePic) + ']';
    }

}
