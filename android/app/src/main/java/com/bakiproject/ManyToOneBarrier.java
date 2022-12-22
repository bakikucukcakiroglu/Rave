package com.bakiproject;

public class ManyToOneBarrier {

    int value;

    public ManyToOneBarrier() {
        value = -1;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public synchronized void waitForOthers() throws InterruptedException {
        while (value != 0) {
            wait();
        }
    }

    public synchronized void notifyFinished() {
        value--;
        notify();
    }
}
