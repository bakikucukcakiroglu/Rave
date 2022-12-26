package com.bakiproject.streams;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Semaphore;

/**
 * A non-bounded verson of the BoundedBuffer example in “Concurrent and Distributed Computing in Java” by Vijay K. Garg
 */
public class ConcurrentQueue<T> {
    Queue<T> buffer = new ArrayDeque<>();
    Semaphore mutex = new Semaphore(1);
    Semaphore isEmpty = new Semaphore(0);

    public void add(T value) throws InterruptedException {

        mutex.acquire(); // ensures mutual exclusion
        buffer.add(value); // update the buffer
        mutex.release();
        isEmpty.release();  // notify any waiting consumer
    }

    public T fetch() throws InterruptedException {
        T value;

        isEmpty.acquire();
        mutex.acquire(); // ensures mutual exclusion

        value = buffer.poll(); //read from buffer
        mutex.release();
        return value;
    }
}
