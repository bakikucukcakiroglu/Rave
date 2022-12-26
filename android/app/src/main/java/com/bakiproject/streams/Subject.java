package com.bakiproject.streams;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Subject implements both Consumer and Observable, allowing creating events from normal code.
 *
 * @param <T>
 */
public class Subject<T> implements Consumer<T>, Observable<T> {

    List<Consumer<T>> callbacks = new ArrayList<>();

    @Override
    synchronized public void accept(T t) {
        callbacks.forEach(a -> a.accept(t));
    }

    @Override
    synchronized public void subscribe(Consumer<T> c) {
        callbacks.add(c);
    }

}
