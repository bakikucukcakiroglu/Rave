package com.bakiproject.streams;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * An Observable is a stream of T objects that can be consumed using subscribe or transformed into other streams.
 *
 * @param <T> type of objects flowing in the stream
 */
public interface Observable<T> {
    void subscribe(Consumer<T> c);

    default <R> Observable<R> map(Function<T, R> function) {
        return (consumer) ->
                this.subscribe(event -> consumer.accept(function.apply(event)));
    }

    default Observable<T> filter(Predicate<T> pred) {
        return (consumer) ->
                this.subscribe(event -> {
                    if (pred.test(event)) consumer.accept(event);
                });
    }

    @SuppressWarnings("unchecked")
    default Observable<T> concat(Observable<? extends T>... others) {
        return (consumer) -> {
            this.subscribe(consumer);
            for (Observable<? extends T> other : others)
                other.subscribe(consumer::accept);
        };
    }

    default Single<T> once() {
        final boolean[] sentAlready = {false};
        return consumer ->
                this.subscribe(event -> {
                    if (!sentAlready[0]) {
                        sentAlready[0] = true;
                        consumer.accept(event);
                    }
                });
    }

    default Observable<T> subscribeOnThread(StreamThread thread) {
        return thread.listenInThread(this);
    }
}
