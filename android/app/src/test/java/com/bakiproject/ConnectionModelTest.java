package com.bakiproject;

import android.media.MediaPlayer;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;


import java.io.IOException;

class ConnectionModelTest {

    @Test
    synchronized void startServer() throws IOException, InterruptedException {
        MediaPlayer mp = mock(MediaPlayer.class);
        MediaPlayer mp2 = mock(MediaPlayer.class);

        ConnectionModel model = new ConnectionModel(null, mp);

        model.startServer("asd", "fg");

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


    @Test
    synchronized void listServers() throws IOException, InterruptedException {
        MediaPlayer mp = mock(MediaPlayer.class);
        MediaPlayer mp2 = mock(MediaPlayer.class);

        ConnectionModel model = new ConnectionModel(null, mp);

        Thread.sleep(3000);

        System.out.println(model.broadcastClient.getAvailableServers());


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
