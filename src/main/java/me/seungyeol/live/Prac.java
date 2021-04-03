package me.seungyeol.live;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class Prac {

    public static void main(String[] args) {

        Publisher<Integer> pub = iterPub(Stream.iterate(1, i -> i + 1).limit(10).collect(Collectors.toList()));
        Publisher<Integer> newpub = mapPub(pub, (Function<Integer,Integer>)s -> s *10);
        Subscriber<Integer> sub = logSub();

        newpub.subscribe(sub);


    }

    private static Publisher<Integer> mapPub(Publisher<Integer> pub, Function<Integer, Integer> f) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                pub.subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        sub.onSubscribe(s);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        sub.onNext(f.apply(integer));
                    }

                    @Override
                    public void onError(Throwable t) {
                        sub.onError(t);
                    }

                    @Override
                    public void onComplete() {
                        sub.onComplete();
                    }
                });
            }
        };
    }


    private static Subscriber<Integer> logSub() {
        return new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("start");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                System.out.println("onNext:" + integer);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        };
    }

    private static Publisher<Integer> iterPub(List<Integer> iter) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                sub.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        iter.forEach(t -> sub.onNext(t));
                        sub.onComplete();
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };
    }
}
