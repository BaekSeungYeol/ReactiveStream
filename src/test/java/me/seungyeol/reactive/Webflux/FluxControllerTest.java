package me.seungyeol.reactive.Webflux;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
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