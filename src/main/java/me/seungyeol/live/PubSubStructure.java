package me.seungyeol.live;

import org.reactivestreams.Publisher;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;

public class PubSubStructure {
    public static void main(String[] args) throws InterruptedException {
        // Publisher = Observable
        // Subscriber  <- Observer
        //
        Iterable<Integer> iter = Arrays.asList(1,2,3,4,5);
        ExecutorService es = Executors.newSingleThreadExecutor();

        Flow.Publisher p = new Flow.Publisher() {
            @Override
            public void subscribe(Flow.Subscriber subscriber) {
                subscriber.onSubscribe(new Flow.Subscription() {
                    Iterator<Integer> it = iter.iterator();
                    @Override
                    public void request(long n) {
                        int i = 0;
                        es.execute(() -> {
                            try {
                                while (true) {
                                    if (it.hasNext()) {
                                        subscriber.onNext(it.next());
                                    } else {
                                        subscriber.onComplete();
                                        break;
                                    }
                                }
                            } catch (RuntimeException e) {
                                subscriber.onError(e);
                            }
                        });

                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };

        Flow.Subscriber<Integer> s = new Flow.Subscriber<Integer>() {
            Flow.Subscription subscription;
            @Override
            public void onSubscribe(Flow.Subscription subscription) {

                this.subscription = subscription;
                this.subscription.request(1);
            }

            @Override
            public void onNext(Integer item) {
                System.out.println(item);
                this.subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        };

        p.subscribe(s);
        es.awaitTermination(10,TimeUnit.HOURS);
        es.shutdown();

    }
}
