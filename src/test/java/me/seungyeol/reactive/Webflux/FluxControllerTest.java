package me.seungyeol.reactive.Webflux;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@SpringBootTest
class FluxControllerTest {
    @Test
    void expectFooBarComplete() {
        Flux<String> flux = Flux.just("foo","bar");

        StepVerifier.create(flux)
                .expectNext("foo")
                .expectNext("bar")
                .verifyComplete(); // expectComplete -> then -> verify
    }

    @Test
    void expectFooBarError() {
        Flux<String> flux = Flux.just("foo","bar");

        StepVerifier.create(flux)
                .expectNext("foo")
                .expectNext("bar")
                .verifyError(RuntimeException.class);
    }


    //  Use StepVerifier to check that the flux parameter emits a User with "swhite"username
    // and another one with "jpinkman" then completes successfully.
    @Test
    void expectSkylerJesseComplete() {
        Flux<User> flux = Flux.just(new User("swhite"), new User("jpinkman"));
        StepVerifier.create(flux)
                .assertNext(u -> Assertions.assertThat(u.getName()).isEqualTo("swhite"))
                .assertNext(u -> Assertions.assertThat(u.getName()).isEqualTo("jpinkman"))
                .verifyComplete();
    }


    // Expect 10 elements then complete and notice how long the test takes.
    @Test
    void expect10Elements() {
        Flux<Long> flux = Flux.interval(Duration.ofMillis(1000)).take(10);

        StepVerifier.create(flux)
                .expectNextCount(10)
                .verifyComplete();
    }

    // Expect 3600 elements at intervals of 1 second, and verify quicker than 3600s
    // by manipulating virtual time thanks to StepVerifier#withVirtualTime, notice how long the test takes
    @Test
    void expect3600Elements() {
        StepVerifier.withVirtualTime(() -> Flux.interval(Duration.ofSeconds(1)).take(3600))
                .thenAwait(Duration.ofHours(1))
                .expectNextCount(3600)
                .verifyComplete();
    }

    @Test

    void Test() {
        Mono<Integer> integerMono = Mono.<Integer>just(1)
                .subscribeOn(Schedulers.single());

        integerMono.subscribe(item -> Assertions.assertThat(item).isEqualTo(2));


    }

    @Test
    @DisplayName("parallel 이 없고 빠르지 않다. latency에 있어서 장점이 없는 테스트")
    void test() {
        Flux.just("a","b","c","d","e","f","g","h","i")
                .window(3)
                .flatMap(l -> l.map(this::toUpperCase))
                .doOnNext(System.out::println)
                .blockLast();
    }
    @Test
    @DisplayName("parallel 이 있다.. latency에 있어서 장점이 있는 테스트 + Sequencial 을 유지하는 코드")
    void test2() throws InterruptedException {
        //CountDownLatch countDownLatch = new CountDownLatch(1);
        Flux.just("a","b","c","d","e","f","g","h","i")
                .window(3)
                .flatMapSequential(l -> l.map(this::toUpperCase).subscribeOn(Schedulers.parallel()))
                .doOnNext(System.out::println)
                .blockLast();

        // conCatMap은 순서를 보장하는데 의미가 없다. Delay를 그대로 가져가기 때문
        // parallel 하면서 sequencial 한 방법은 flatMapSequencial 이다.

        //countDownLatch.await(5, TimeUnit.SECONDS);

    }

    private List<String> toUpperCase(String s) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return List.of(s.toUpperCase(), Thread.currentThread().getName());
    }

    private void fail() {
        throw new AssertionError("workshop not implemented");
    }


    public class User {
        String name;

        public User(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }


}