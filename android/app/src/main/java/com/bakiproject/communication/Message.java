package com.bakiproject.communication;

import com.bakiproject.UserInfo;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

public interface Message extends Serializable {

    final class UserIntroMessage implements Message {
        private static final long serialVersionUID = 0L;
        private final UserInfo info;

        public UserIntroMessage(UserInfo info) {
            this.info = info;
        }

        public UserIntroMessage(String username, byte[] profilePic) {
            this.info = new UserInfo(username, profilePic, null);
        }

        public UserInfo info() {
            return info;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            UserIntroMessage that = (UserIntroMessage) obj;
            return Objects.equals(this.info, that.info);
        }

        @Override
        public int hashCode() {
            return Objects.hash(info);
        }

        @Override
        public String toString() {
            return "UserIntroMessage[" +
                    "info=" + info + ']';
        }

        }

    final class GetTimeMessage implements Message {
        private static final long serialVersionUID = 0L;
        private final long millisTimeSent;

        public GetTimeMessage(long millisTimeSent) {
            this.millisTimeSent = millisTimeSent;
        }

        public GetTimeMessage() {
                this(System.currentTimeMillis());
            }

        public long millisTimeSent() {
            return millisTimeSent;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            GetTimeMessage that = (GetTimeMessage) obj;
            return this.millisTimeSent == that.millisTimeSent;
        }

        @Override
        public int hashCode() {
            return Objects.hash(millisTimeSent);
        }

        @Override
        public String toString() {
            return "GetTimeMessage[" +
                    "millisTimeSent=" + millisTimeSent + ']';
        }

        }

    final class GetTimeResponse implements Message {
        private static final long serialVersionUID = 0L;
        private final long millisTimeRequestSent;
        private final long millisTimeResponseSent;

        public GetTimeResponse(long millisTimeRequestSent, long millisTimeResponseSent) {
            this.millisTimeRequestSent = millisTimeRequestSent;
            this.millisTimeResponseSent = millisTimeResponseSent;
        }

        public long millisTimeRequestSent() {
            return millisTimeRequestSent;
        }

        public long millisTimeResponseSent() {
            return millisTimeResponseSent;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            GetTimeResponse that = (GetTimeResponse) obj;
            return this.millisTimeRequestSent == that.millisTimeRequestSent &&
                    this.millisTimeResponseSent == that.millisTimeResponseSent;
        }

        @Override
        public int hashCode() {
            return Objects.hash(millisTimeRequestSent, millisTimeResponseSent);
        }

        @Override
        public String toString() {
            return "GetTimeResponse[" +
                    "millisTimeRequestSent=" + millisTimeRequestSent + ", " +
                    "millisTimeResponseSent=" + millisTimeResponseSent + ']';
        }

    }

    final class UsersListUpdateMessage implements Message {
        private static final long serialVersionUID = 0L;
        private final Set<UserInfo> users;

        public UsersListUpdateMessage(Set<UserInfo> users) {
            this.users = users;
        }

        public Set<UserInfo> users() {
            return users;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            UsersListUpdateMessage that = (UsersListUpdateMessage) obj;
            return Objects.equals(this.users, that.users);
        }

        @Override
        public int hashCode() {
            return Objects.hash(users);
        }

        @Override
        public String toString() {
            return "UsersListUpdateMessage[" +
                    "users=" + users + ']';
        }

    }
}
