package com.bakiproject.streams;

public interface StatefulObservable<T> extends Observable<T> {
    T getState();


}
