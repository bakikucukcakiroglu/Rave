package com.bakiproject;

import android.media.MediaPlayer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

import com.bakiproject.react.WritableWrapper;

class ConnectionModelTest {

    //@Test
    synchronized void startServer() throws InterruptedException {
        MediaPlayer mp = mock(MediaPlayer.class);
        MediaPlayer mp2 = mock(MediaPlayer.class);

        ConnectionModel model = new ConnectionModel(null, mp);

        model.startServer("asd", "fg");

        ConnectionModel cl1 = new ConnectionModel(null, mp2);
        Thread.sleep(5000);

        cl1.connectToServer("127.0.0.1", "fgfhsa");
        Thread.sleep(5000);

        cl1.disconnectFromServer();
        Thread.sleep(5000);


/*
        model.startMusic();

        verify(mp, never()).start();
        verify(mp2, never()).start();

        Thread.sleep(2000);

        verify(mp).start();
        verify(mp2).start();

        System.in.read();*/
    }


    //@Test
    synchronized void listServers() throws InterruptedException {
        MediaPlayer mp = mock(MediaPlayer.class);
        MediaPlayer mp2 = mock(MediaPlayer.class);

        ConnectionModel model = new ConnectionModel(null, mp);

        Thread.sleep(3000);

        System.out.println(model.broadcastClient.getServerListUpdatesStream().getState());

        /*model.connectToServer(
                model.broadcastClient
                        .getAvailableServers()
                        .iterator()
                        .next()
                        .addr(),
                "asd");

/*        model.startServer("asd", "fg");

        ConnectionModel cl1 = new ConnectionModel(null, mp2);
        cl1.connectToServer("127.0.0.1", "fgfhsa");

        wait();
/*
        model.startMusic();

        verify(mp, never()).start();
        verify(mp2, never()).start();

        Thread.sleep(2000);

        verify(mp).start();
        verify(mp2).start();

        System.in.read();*/
    }

}
