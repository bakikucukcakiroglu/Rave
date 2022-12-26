package com.bakiproject.streams;

/**
 * An observable that emits once and then discards
 */
public class SingleSubject<T> extends Subject<T> implements Single<T> {
    private boolean alreadyEmitted = false;

    @Override
    public synchronized void accept(T t) {
        if (!alreadyEmitted) {
            alreadyEmitted = true;
            super.accept(t);
        }
    }
}
