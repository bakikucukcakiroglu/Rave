package com.bakiproject.communication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.bakiproject.ConnectionModel;
import com.bakiproject.UserInfo;
import com.bakiproject.streams.StatefulObservable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.verification.VerificationMode;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Set;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
class CommunicationTest {

    CommunicationServer server;

    StatefulObservable<Set<UserInfo>> serverUserInfoUpdates;

    @BeforeEach
    void setUp() {
        server = new CommunicationServer("test", "admin");
        serverUserInfoUpdates = server.getUserInfoUpdatesStream();
    }

    //@Test
    void connectDisconnectTest() throws IOException, InterruptedException {
        assertEquals(1, serverUserInfoUpdates.getState().size());

        Thread.sleep(500);
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
        Thread.sleep(10000);
        assertEquals(2, serverUserInfoUpdates.getState().size());
        assertEquals(2, c1.getUserInfoUpdatesStream().getState().size());

    }

    //@Test
    void doStartMusicSequence() throws InterruptedException, IOException {
        Thread.sleep(1000);
        CommunicationClient c1 = new CommunicationClient(InetAddress.getByName("localhost"), 8000, "c1");

        Thread.sleep(1000);
        CommunicationClient c2 = new CommunicationClient(InetAddress.getByName("localhost"), 8000, "c2");

        Thread.sleep(1000);
        Consumer<ConnectionModel.MusicPair> cS = mock(Consumer.class);
        Consumer<ConnectionModel.MusicPair> cc1 = mock(Consumer.class);
        Consumer<ConnectionModel.MusicPair> cc2 = mock(Consumer.class);

        server.getControlMusicEventsStream().subscribe(cS);
        c1.getControlMusicEventsStream().subscribe(cc1);
        c2.getControlMusicEventsStream().subscribe(cc2);

        server.doControlMusicSequence(ConnectionModel.MusicState.PLAYING);

        Thread.sleep(5000);

        verify(cS).accept(any());
        verify(cc1).accept(any());
        verify(cc2).accept(any());
    }
}