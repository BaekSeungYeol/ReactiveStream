package me.seungyeol.live;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class Test {
    public static void main(String[] args) {

        Iterable<Integer> iter = List.of(1,2,3,4,5);

        Publisher iterPub = new Publisher() {
            @Override
            public void subscribe(Subscriber sub) {

                Iterator<Integer> iterator = iter.iterator();
                sub.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        while(n-- > 0) {
                            if(iterator.hasNext()) {
                                sub.onNext(iterator.next());
                            }
                            else {
                                sub.onComplete();
                                break;
                            }
                        }
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };
        Subscriber<Integer> subscriber = new Subscriber<>() {
            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("onSubscribe");
                s.request(Long.MAX_VALUE);
            }
            @Override
            public void onNext(Integer o) {
                System.out.println("onNext: " + o);

            }

            @Override
            public void onError(Throwable t) {
                System.out.println("onError:" + t);

            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        };


        Publisher<Integer> mapPub = mapping(iterPub,(Function<Integer,Integer>)a -> a*10);
        mapPub.subscribe(subscriber);
    }

    private static Publisher<Integer> mapping(Publisher<Integer> iterPub, Function<Integer, Integer> integerIntegerFunction) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                iterPub.subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        sub.onSubscribe(s);
                    }

                    @Override
                    public void onNext(Integer o) {
                        sub.onNext(integerIntegerFunction.apply(o));
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {
                        sub.onComplete();
                    }
                });
            }
        };
    }
}
