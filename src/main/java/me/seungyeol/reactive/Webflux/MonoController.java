package me.seungyeol.reactive.Webflux;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class MonoController {

    @GetMapping("/hello")
    Mono<String> hello() {

//        log.info("pos1");
//        Mono m = Mono.just("Hello webFlux").doOnNext(c -> log.info(c)).log();
//        log.info("pos2");
//        return m;


        //Mono m = Mono.just(generateHello()).doOnNext(c -> log.info(c)).log(); // Same


        // 이미 준비가 아닌 모노때 준비
//        log.info("pos1");
//        String msg = generateHello();
//        Mono m = Mono.just(msg).doOnNext(c-> log.info(c)).log();
//        Mono m = Mono.fromSupplier(() -> generateHello()).doOnNext(c->log.info(c)).log();
//        m.subscribe();
//        log.info("pos2");
//        return m;

        log.info("pos1");
        String msg = generateHello();
        Mono<String> m = Mono.just(msg).doOnNext(c-> log.info(c)).log();
        String msg2 = m.block(); // Spring boot 2에서 막았다.
        log.info("pos2: " + msg2);
        return m;
    }

    private String generateHello() {
        log.info("method generateHello()");
        return "Hello Mono";
    }


}
