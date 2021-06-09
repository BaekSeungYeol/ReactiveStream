package me.seungyeol.reactive.Webflux;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Slf4j
public class Test {

    public static Flux<String> temp(String s) {
        return Flux.just(s);
    }
    public static String toLowerCase(String s){
        try {
            Thread.sleep(5000L);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }
    public static void main(String[] args) throws InterruptedException {


        for(int i=0; i< 10; ++i) {
            temp("HI" + i)
                    .publishOn(Schedulers.boundedElastic())
                    .map(Test::toLowerCase)
                    .log()
                    .subscribe(s -> System.out.println());
        }

//
//        Flux<Object> log = Flux.generate(synchronousSink -> {
//            synchronousSink.next(LocalDateTime.now().toString());
//        })
//                .log();
//
//        log.limitRequest(10).subscribe();
//        Flux<String> flux = Flux.create((FluxSink<String> sink) -> {
//            sink.onRequest(request -> {
//                for (int i = 1; i <= request; i++) {
//                    sink.next(LocalDateTime.now().toString());
//                }
//            });
//        }).log();

//        flux.limitRequest(50).subscribe(new BaseSubscriber<String>() {
//            private int count = 0 ;
//            private final int requestCount = 30;
//
//            @Override
//            protected void hookOnSubscribe(Subscription subscription) {
//                request(requestCount);
//            }
//
//            @Override
//            protected void hookOnNext(String value) {
//                count++;
//                if(count >= requestCount) {
//                    count = 0;
//                    request(requestCount);
//                }
//            }
//        });

        Thread.sleep(30000L);
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
//
//        AtomicBoolean isDisposed = new AtomicBoolean();
//        Disposable disposableInstance = new Disposable() {
//            @Override
//            public void dispose() {
//                isDisposed.set(true);
//            }
//
//            @Override
//            public String toString() {
//                return "DISPOSABLE";
//            }
//        };
//
//        Flux.using(() -> disposableInstance,
//                disposable -> Flux.just(disposable.toString()),
//                Disposable::dispose);
//
//        Flux.interval(Duration.ofMillis(250))
//                .map(input -> {
//                    if (input < 3) return "tick " + input;
//                    throw new RuntimeException("boom");
//                })
//                .retry(1)
//                .elapsed()
//                .subscribe(System.out::println, System.err::println);
//
//        Flux<String> flux = Flux
//                .<String>error(new IllegalArgumentException())
//                .doOnError(System.out::println)
//                .retryWhen(Retry.from(companion -> companion.take(3)));
//
//
//        AtomicInteger errorCount = new AtomicInteger();
//
//        Flux.error(new IllegalArgumentException())
//                .doOnError(e -> errorCount.incrementAndGet())
//                .retryWhen(Retry.from(companion -> companion.map(rs -> {
//                    if (rs.totalRetries() < 3) return rs.totalRetries();
//                    else return Exceptions.propagate(rs.failure());
//                })));
//
//
//        AtomicInteger eC = new AtomicInteger();
//        AtomicInteger transientHelper = new AtomicInteger();
//        Flux<Integer> transientFlux = Flux.<Integer>generate(sink -> {
//            int i = transientHelper.incrementAndGet();
//
//            if (i == 10) {
//                sink.next(i);
//                sink.complete();
//            } else if (i % 3 == 0) {
//                sink.next(i);
//            } else {
//                sink.error(new IllegalArgumentException("Transient error at : " + i));
//            }
//        })
//                .doOnError(e -> eC.incrementAndGet());
//        transientFlux.retryWhen(Retry.max(2).transientErrors(true))
//                .blockLast();

    }
}


