package com.bakiproject.streams;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

class StatefulSubjectTest {

    @Test
    void test() {
        StatefulSubject<String> subject = new StatefulSubject<>("1");
        assertEquals("1", subject.getState());

        AtomicReference<String> received = new AtomicReference<>();
        subject.subscribe(received::set);

        assertEquals("1", subject.getState());
        assertEquals("1", received.get());

        subject.accept("asd");
        assertEquals("asd", received.get());
        assertEquals("asd", subject.getState());

        subject.accept("fg");
        assertEquals("fg", received.get());
        assertEquals("fg", subject.getState());
    }
}