package com.bakiproject.react;

import com.facebook.react.bridge.Callback;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ReactObservable<T> implements Consumer<T> {
    T state;
    List<Callback> callbacks = new ArrayList<>();
    final Function<T, WritableWrapper> serialise;

    public ReactObservable(Function<T, WritableWrapper> serialise) {
        this(serialise, null);
    }

    public ReactObservable(Function<T, WritableWrapper> serialise, T state) {
        this.serialise = serialise;
        this.state = state;
    }

    @Override
    public void accept(T t) {
        // state = t;
        sendUpdates();
    }

    private void sendUpdates() {
        List<Callback> privCallbacks = callbacks;
        callbacks = new ArrayList<>();
        privCallbacks.forEach(this::sendUpdate);
    }

    public void subscribe(Callback c) {
        callbacks.add(c);
        sendUpdate(c);
    }

    private void sendUpdate(Callback callback) {
        callback.invoke(serialise.apply(state).getObj());
    }

    public T getState() {
        return state;
    }
}
