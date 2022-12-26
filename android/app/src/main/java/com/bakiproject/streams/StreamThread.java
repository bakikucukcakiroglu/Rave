package com.bakiproject.streams;

import android.util.Pair;

import java.util.function.Consumer;

public class StreamThread {
    boolean isRunning = true;
    ConcurrentQueue<AcceptPair> applyQueue = new ConcurrentQueue<>();

    Thread localThread = new Thread(this::doAccepts);

    public StreamThread() {
        localThread.start();
    }

    <T> Observable<T> listenInThread(Observable<T> observable) {
        Subject<T> localObservable = new Subject<>();
        observable.subscribe(c -> {
            try {
                applyQueue.add(new AcceptPair(localObservable, c));
            } catch (InterruptedException ignored) {
            }
        });
        return localObservable;
    }

    private void doAccepts() {
        while (isRunning) {
            try {
                applyQueue.fetch().applyObject();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class AcceptPair {
        private final Consumer<Object> subject;
        private final Object object;

        private <T> AcceptPair(Consumer<T> subject, T object) {
            //noinspection unchecked
            this.subject = (Consumer<Object>) subject;
            this.object = object;
        }

        public void applyObject() {
            subject.accept(object);
        }
    }

    public void stop() {
        isRunning = false;
        localThread.interrupt();
    }
}
