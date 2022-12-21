package com.bakiproject.react;

import com.facebook.react.bridge.Callback;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;


public class ReactObservable<T> implements Consumer<T> {
<<<<<<< HEAD
    final T state;
=======
    T state;
>>>>>>> 55c260576c237008b1a945d80c04b3c318c04d34
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
        //state = t;
        sendUpdates();
    }

    private void sendUpdates() {
        callbacks.forEach(this::sendUpdate);
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
