package me.seungyeol.live;


import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Reactive Streams -
 */
public class PubSub {
    public static void main(String[] args) {
        Publisher<Integer> pub = new Publisher<Integer>() {
            Iterable<Integer> iter = Stream.iterate(1, a -> a +1).limit(10).collect(Collectors.toList());

            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                sub.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {

                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };

        Subscriber<Integer> sub = new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {

            }

            @Override
            public void onNext(Integer integer) {

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        };

        pub.subscribe(sub);
    }
}
