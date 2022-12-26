package com.bakiproject.streams;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;


/**
 * A Subject that reemits the last emitted object when a new object subscribes.
 */
public class StatefulSubject<T> extends Subject<T> implements StatefulObservable<T> {
    T state;

    public StatefulSubject(T startState) {
        super();
        this.state = startState;
    }

    @Override
    public synchronized void accept(T t) {
        state = t;
        super.accept(state);
    }

    @Override
    public synchronized void subscribe(Consumer<T> c) {
        super.subscribe(c);
        c.accept(state);
    }

    public synchronized T getState() {
        return state;
    }
}
