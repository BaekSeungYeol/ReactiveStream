package me.seungyeol.reactive.Webflux;

import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.Exceptions;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Test {
    public static void main(String[] args) throws InterruptedException {

//        Flux<String> slow = Flux.just("1", "2").delayElements(Duration.ofSeconds(1));
//        Flux<String> fast = Flux.just("1", "2", "3", "4", "5").delayElements(Duration.ofSeconds(2));;
//
//        Flux<String> stringFlux = Flux.firstWithSignal(slow, fast).log();
//        stringFlux.subscribe(s -> System.out.println(s));


//        Sinks.Many<Integer> all = Sinks.many().replay().limit(2);
//
//        all.emitNext(1, FAIL_FAST);
//
//        //thread2, later
//        all.emitNext(2, FAIL_FAST);
//
//        //thread3, concurrently with thread 2
//        Sinks.EmitResult result = all.tryEmitNext(3);
//
//        Mono.just(1).concatWith(Mono.just(2)).subscribe(s -> System.out.println(s));
//
//        Flux.range(1,3).then(Mono.just(3)).subscribe(s -> System.out.println(s));


//        Sinks.Many<Integer> all2 = Sinks.many().multicast().directAllOrNothing();
//        all2.emitNext(3,FAIL_FAST);
//        all2.asFlux().takeWhile(i -> i<10)
//                .log().subscribe();
//        Thread.sleep(10000);

//        Flux.just(1,2,3,4,5);


//        Flux.range(1,4)
//                .subscribe(i -> System.out.println(i),
//                        (e) -> System.err.println(e.getMessage())
//                ,() -> System.out.println("Done"),
//                        sub -> sub.request(2));

//        ExecutorService exec = Executors.newSingleThreadExecutor();
//        Disposable.Swap composit = Disposables.swap();
//
//        Disposable disposable = composit.get();
//        composit.dispose();

//
//        SimpleSubscriber simpleSubscriber = new SimpleSubscriber();
//        Flux.range(1,3)
//                .subscribe(simpleSubscriber);

//        Flux.generate(() -> 0,
//                (state, sink) -> {
//                    sink.next("3 * " + state + " = " + (3 * state));
//                    if (state == 10) sink.complete();
//                    return state + 1;
//                })
//                .subscribe(s -> System.out.println(s));

//        Flux.generate(() -> new AtomicLong(0),
//                (state, sink) -> {
//                    long i = state.incrementAndGet();
//                    sink.next("3 * " + i + " = " + (3 * i));
//                    if (i == 10L) sink.complete();
//                    return state;
//                },(state) -> System.out.println(state))
//                .subscribe(s -> System.out.println(s));

//        Flux.just(1, 2, 0)
//                .map(i -> "100 / " + i + " = " + (100 / i)) //this triggers an error with 0
//                .onErrorReturn("Divided by zero :(")
//                .log().subscribe();
//
//        Thread.sleep(10000L);

        AtomicBoolean isDisposed = new AtomicBoolean();
        Disposable disposableInstance = new Disposable() {
            @Override
            public void dispose() {
                isDisposed.set(true);
            }

            @Override
            public String toString() {
                return "DISPOSABLE";
            }
        };

        Flux.using(() -> disposableInstance,
                disposable -> Flux.just(disposable.toString()),
                Disposable::dispose);

        Flux.interval(Duration.ofMillis(250))
                .map(input -> {
                    if (input < 3) return "tick " + input;
                    throw new RuntimeException("boom");
                })
                .retry(1)
                .elapsed()
                .subscribe(System.out::println, System.err::println);

        Flux<String> flux = Flux
                .<String>error(new IllegalArgumentException())
                .doOnError(System.out::println)
                .retryWhen(Retry.from(companion -> companion.take(3)));

        AtomicInteger errorCount = new AtomicInteger();
        Flux<String> flux2 =
                Flux.<String>error(new IllegalArgumentException())
                        .doOnError(e -> errorCount.incrementAndGet())
                        .retryWhen(Retry.from(companion -> // (1)
                                companion.map(rs -> { // (2)
                                    if (rs.totalRetries() < 3) return rs.totalRetries(); // (3)
                                    else throw Exceptions.propagate(rs.failure()); // (4)
                                })
                        ));

    }
    public static class SimpleSubscriber<T> extends BaseSubscriber<T> {
        @Override
        protected void hookOnSubscribe(Subscription subscription) {
            System.out.println("subscribed");
            request(1);
        }

        @Override
        protected void hookOnNext(T value) {
            System.out.println(value);
            request(1);
        }

        @Override
        protected void hookOnComplete() {
            System.out.println("onComplete");
        }
    }

}
