package com.bakiproject.streams;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

class StreamThreadTest {
    @Test
    void singleTest() throws InterruptedException {
        StreamThread thr = new StreamThread();


        Subject<String> subject = new Subject<>();
        AtomicReference<String> received = new AtomicReference<>();
        subject
                .map(a -> Thread.currentThread().getName())
                .subscribe(received::set);

        AtomicReference<String> received2 = new AtomicReference<>();
        subject
                .subscribeOnThread(thr)
                .map(a -> Thread.currentThread().getName())
                .subscribe(received2::set);

        AtomicReference<String> stReceived = new AtomicReference<>();
        subject
                .subscribeOnThread(thr)
                .subscribe(stReceived::set);

        subject.accept("asd");
        Thread.sleep(20);
        assertEquals(Thread.currentThread().getName(), received.get());
        assertEquals(thr.localThread.getName(), received2.get());
        assertEquals("asd", stReceived.get());


        subject.accept("fg");
        Thread.sleep(20);
        assertEquals(Thread.currentThread().getName(), received.get());
        assertEquals(thr.localThread.getName(), received2.get());
        assertEquals("fg", stReceived.get());
    }
}