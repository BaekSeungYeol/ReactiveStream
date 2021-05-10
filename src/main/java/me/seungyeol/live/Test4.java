package me.seungyeol.live;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.UnaryOperator;

@Slf4j
public class Test4 {
    public static void main(String[] args) {
        Publisher<Integer> iterPub = sub -> {
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
            };

        Publisher<Integer> pubOnPub = new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                ExecutorService es = Executors.newSingleThreadExecutor(new CustomizableThreadFactory() {
                    @Override
                    public String getThreadNamePrefix() { return "pubOn-";}
                });

                iterPub.subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        sub.onSubscribe(s);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        es.execute(() -> sub.onNext(integer));
                    }

                    @Override
                    public void onError(Throwable t) {
                        es.execute(() -> sub.onError(t));
                    }

                    @Override
                    public void onComplete() {
                        es.execute(() -> sub.onComplete());
                    }
                });
            }
        };

        Publisher<Integer> mapPub = new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> sub) {

                UnaryOperator<Integer> o = i -> i * 10;

                pubOnPub.subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        log.info("onSubscribe");
                        sub.onSubscribe(s);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Integer apply = o.apply(integer);
                        sub.onNext(apply);
                         log.info("onNext: {}", apply);
                    }

                    @Override
                    public void onError(Throwable t) {
                        sub.onError(t);
                        log.info("onError");
                    }

                    @Override
                    public void onComplete() {
                        sub.onComplete();
                        log.info("onComplete");
                    }
                });
            }
        };

        Publisher<Integer> subOnPub = new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                ExecutorService es = Executors.newSingleThreadExecutor(new CustomizableThreadFactory() {
                    @Override
                    public String getThreadNamePrefix() { return "subOn-";}
                });
                es.execute(() -> mapPub.subscribe(sub));
            }
        };


        Publisher<Integer> mapPub2 = new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> sub) {

                UnaryOperator<Integer> o = i -> -i;

                subOnPub.subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                      //  log.info("onSubscribe");
                        sub.onSubscribe(s);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Integer apply = o.apply(integer);
                        sub.onNext(apply);
                     //   log.info("onNext: {}", apply);
                    }

                    @Override
                    public void onError(Throwable t) {
                        sub.onError(t);
                      //  log.info("onError");
                    }

                    @Override
                    public void onComplete() {
                        sub.onComplete();
                       // log.info("onComplete");
                    }
                });
            }
        };


        Subscriber<Integer> sub = new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
//                log.info("onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
//                log.info("onNext : {}", integer);
            }

            @Override
            public void onError(Throwable t) {
//                log.info("onError");
            }

            @Override
            public void onComplete() {
//                log.info("onComplete");
            }
        };


        // 타고 타고 위로 올라가서 onSubscribe 부터 떠올리면 쉽다.

        mapPub2.subscribe(sub);
    }
}
