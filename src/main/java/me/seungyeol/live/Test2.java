package me.seungyeol.live;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

@Slf4j
public class Test2 {
    public static void main(String[] args) {


        Publisher<Integer> publisher = new Publisher<>() {
            @Override
            public void subscribe(Subscriber sub) {
                sub.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        sub.onNext(1);
                        sub.onNext(2);
                        sub.onNext(3);
                        sub.onNext(4);
                        sub.onNext(5);
                        sub.onComplete();
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };

        Publisher<Integer> subOnPub = new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                ExecutorService executors = Executors.newSingleThreadExecutor();

                executors.execute(() -> publisher.subscribe(sub));
            }
        };

        Publisher<Integer> pubOnPub = new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                ExecutorService executorService = Executors.newSingleThreadExecutor();

                publisher.subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        sub.onSubscribe(s);

                    }

                    @Override
                    public void onNext(Integer integer) {
                        executorService.execute(() -> sub.onNext(integer));
                    }

                    @Override
                    public void onError(Throwable t) {
                        executorService.execute(() -> sub.onError(t));
                    }

                    @Override
                    public void onComplete() {
                        executorService.execute(() -> sub.onComplete());
                    }
                });
            }
        };

        Publisher<Integer> mapOnPub = new Publisher<Integer>() {
            Function<Integer,Integer> func = a -> a*10;

            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                subOnPub.subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        sub.onSubscribe(s);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        sub.onNext(func.apply(integer));
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


        Subscriber<Integer> subscriber = new Subscriber<>() {
            @Override
            public void onSubscribe(Subscription s) {
                log.info("onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer o) {
                log.info("onNext: " + o);
            }

            @Override
            public void onError(Throwable t) {
                log.info("onError");
            }

            @Override
            public void onComplete() {
                log.info("onComplete");
            }
        };

        pubOnPub.subscribe(subscriber);

        System.out.println("Exit");
    }
}
