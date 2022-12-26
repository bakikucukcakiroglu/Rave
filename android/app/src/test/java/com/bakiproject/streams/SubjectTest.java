package com.bakiproject.streams;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

class SubjectTest {

    @Test
    void subscribe() {
        Subject<String> subject = new Subject<>();
        AtomicReference<String> received = new AtomicReference<>();
        subject.subscribe(received::set);

        subject.accept("asd");
        assertEquals("asd", received.get());

        subject.accept("fg");
        assertEquals("fg", received.get());
    }

    @Test
    void map() {
        Subject<String> subject = new Subject<>();
        AtomicReference<String> received = new AtomicReference<>();
        subject.subscribe(received::set);

        AtomicReference<String> received2 = new AtomicReference<>();
        subject
                .map(s -> s + "1")
                .subscribe(received2::set);

        subject.accept("asd");
        assertEquals("asd", received.get());
        assertEquals("asd1", received2.get());

        subject.accept("fg");
        assertEquals("fg", received.get());
        assertEquals("fg1", received2.get());
    }

    @Test
    void filter() {
        Subject<String> subject = new Subject<>();
        AtomicReference<String> received = new AtomicReference<>();
        subject.subscribe(received::set);

        AtomicReference<String> received2 = new AtomicReference<>();
        subject
                .filter(s -> s.charAt(0) == 'a')
                .subscribe(received2::set);

        subject.accept("asd");
        assertEquals("asd", received.get());
        assertEquals("asd", received2.get());

        subject.accept("fg");
        assertEquals("fg", received.get());
        assertEquals("asd", received2.get());

        subject.accept("aaa");
        assertEquals("aaa", received.get());
        assertEquals("aaa", received2.get());
    }
}