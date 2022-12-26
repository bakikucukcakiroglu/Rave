package com.bakiproject.communication;

import static org.junit.jupiter.api.Assertions.*;

import com.bakiproject.UserInfo;
import com.bakiproject.streams.Observable;
import com.bakiproject.streams.StatefulObservable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Set;

class CommunicationTest {

    CommunicationServer server = new CommunicationServer("test", "admin");

    StatefulObservable<Set<UserInfo>> serverUserInfoUpdates = server.getUserInfoUpdatesStream();

    @BeforeEach
    void setUp() {
    }

    @Test
    void simpleConnectionTest() throws IOException, InterruptedException {
        assertEquals(1, serverUserInfoUpdates.getState().size());
        CommunicationClient c1 = new CommunicationClient(InetAddress.getByName("localhost"), 8000, "c1");

        Thread.sleep(1000);
        assertEquals(2, serverUserInfoUpdates.getState().size());
        assertEquals(2, c1.getUserInfoUpdatesStream().getState().size());

        CommunicationClient c2 = new CommunicationClient(InetAddress.getByName("localhost"), 8000, "c2");

        Thread.sleep(2000);
        assertEquals(3, serverUserInfoUpdates.getState().size());
        assertEquals(3, c1.getUserInfoUpdatesStream().getState().size());
        assertEquals(3, c2.getUserInfoUpdatesStream().getState().size());

        c2.close();
        Thread.sleep(4000);
        assertEquals(2, serverUserInfoUpdates.getState().size());
        assertEquals(2, c1.getUserInfoUpdatesStream().getState().size());

    }

    @Test
    void doStartMusicSequence() {

    }
}