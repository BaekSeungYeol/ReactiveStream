package me.seungyeol.reactive.Webflux;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;

import static reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST;

public class Test {
    public static void main(String[] args) throws InterruptedException {

//        Flux<String> slow = Flux.just("1", "2").delayElements(Duration.ofSeconds(1));
//        Flux<String> fast = Flux.just("1", "2", "3", "4", "5").delayElements(Duration.ofSeconds(2));;
//
//        Flux<String> stringFlux = Flux.firstWithSignal(slow, fast).log();
//        stringFlux.subscribe(s -> System.out.println(s));


        Sinks.Many<Integer> all = Sinks.many().replay().limit(2);

        all.emitNext(1, FAIL_FAST);

        //thread2, later
        all.emitNext(2, FAIL_FAST);

        //thread3, concurrently with thread 2
        Sinks.EmitResult result = all.tryEmitNext(3);

        Mono.just(1).concatWith(Mono.just(2)).subscribe(s -> System.out.println(s));

        Flux.range(1,3).then(Mono.just(3)).subscribe(s -> System.out.println(s));


//        Sinks.Many<Integer> all2 = Sinks.many().multicast().directAllOrNothing();
//        all2.emitNext(3,FAIL_FAST);
//        all2.asFlux().takeWhile(i -> i<10)
//                .log().subscribe();
        Thread.sleep(10000);
    }
}
