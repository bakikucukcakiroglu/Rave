package com.bakiproject.streams;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class RecentEventsSubject<T> implements Consumer<T>, StatefulObservable<Collection<T>> {

    Timer timer = new Timer();
    ArrayDeque<T> items = new ArrayDeque<>();
    Subject<Collection<T>> stream = new Subject<>();
    public final long range;

    public RecentEventsSubject(long range) {
        this.range = range;
        stream.accept(Collections.emptyList());
    }

    @Override
    public void subscribe(Consumer<Collection<T>> c) {
        stream.subscribe(c);
    }

    @Override
    public synchronized void accept(T t) {
        items.addLast(t);
        stream.accept(new ArrayList<>(items));

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (RecentEventsSubject.this) {
                    items.removeFirstOccurrence(t);
                    stream.accept(new ArrayList<>(items));
                }
            }
        }, range);
    }

    @Override
    public Collection<T> getState() {
        return new ArrayList<>(items);
    }
}
