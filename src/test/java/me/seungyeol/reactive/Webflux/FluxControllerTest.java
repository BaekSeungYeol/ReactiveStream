package me.seungyeol.reactive.Webflux;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringBootTest
@AutoConfigureWebTestClient
class FluxControllerTest {
    @Autowired
    WebTestClient webTestClient;

    WebTestClient client = WebTestClient.bindToController(
            new FluxController(), new MonoController()
    ).build();

    @Test
    void hello() {
        webTestClient.get().uri("/hello/{name}", "Spring")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Hello Spring");
    }



    @Test
    void expectFooBarComplete() {
        Flux<String> flux = Flux.just("foo", "bar");

        StepVerifier.create(flux)
                .expectNext("foo")
                .expectNext("bar")
                .verifyComplete(); // expectComplete -> then -> verify
    }

    @Test
    void expectFooBarError() {
        Flux<String> flux = Flux.just("foo", "bar");

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
        Flux.just("a", "b", "c", "d", "e", "f", "g", "h", "i")
                .window(3)
                .flatMap(l -> l.map(this::toUpperCase).subscribeOn(Schedulers.parallel()))
                .doOnNext(System.out::println)
                .blockLast();
    }

    @Test
    @DisplayName("parallel 이 있다.. latency에 있어서 장점이 있는 테스트 + Sequencial 을 유지하는 코드")
    void test2() throws InterruptedException {
        //CountDownLatch countDownLatch = new CountDownLatch(1);
        Flux.just("a", "b", "c", "d", "e", "f", "g", "h", "i")
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

    @Test
    @DisplayName("flux1는 Delay가 있고 flux2는 Delay가 없는 상황")
    void test3() {
        Flux<Long> flux1 = Flux.interval(Duration.ofMillis(100)).take(10);
        Flux<Long> flux2 = Flux.just(100L,101L,102L);

        flux1.mergeWith(flux2)
                .doOnNext(System.out::println)
                .blockLast();
    }
    @Test
    @DisplayName("순서를 지키는 Concat flux1는 Delay가 있고 flux2는 Delay가 없는 상황")
    void test4() {
        Flux<Long> flux1 = Flux.interval(Duration.ofMillis(100)).take(10);
        Flux<Long> flux2 = Flux.just(100L,101L,102L);

        flux1.concatWith(flux2)
                .doOnNext(System.out::println)
                .blockLast();
    }

    @Test
    @DisplayName("동일한 Mono로 Flux를 만들기")
    void test5() {
        Mono<Integer> mono1 = Mono.just(1);
        Mono<Integer> mono2 = Mono.just(2);

        Flux.concat(mono2,mono1)
                .doOnNext(System.out::println)
                .blockLast();

    }

    @Test
    void reactorMerge() {
        Flux<Long> flux1 = Flux.interval(Duration.ofMillis(100)).take(10);
        Flux<Long> flux2 = Flux.just(100L,101L,102L);

        flux1.mergeWith(flux2)
                .doOnNext(System.out::println)
                .blockLast();
    }


    @Test
    void reactorConcat() {
        Flux<Long> flux1 = Flux.interval(Duration.ofMillis(100)).take(10);
        Flux<Long> flux2 = Flux.just(100L,101L,102L);

        flux1.concatWith(flux2)
                .doOnNext(System.out::println)
                .blockLast();
    }

    @Test
    void reactorMono() {
        Mono<Integer> mono1 = Mono.just(1);
        Mono<Integer> mono2 = Mono.just(2);

        Flux.concat(mono1, mono2)
                .doOnNext(System.out::println)
                .blockLast();
    }

    public static class Player {
        String first,last;

        public Player(String first, String last) {
            this.first = first;
            this.last = last;
        }
    }
    @Test
    void flatMap() {

        Flux<Player> playerFlux = Flux.just("Michael Jordan", "Scottie Pippen", "Steve Kerr")
                .flatMap(n -> Mono.just(n))
                .map(p -> {
                    String[] split = p.split("\\s");
                    return new Player(split[0], split[1]);
                })
                .subscribeOn(Schedulers.parallel());

        List<Player> playerList = Arrays.asList(
                new Player("Michael", "Jordan"),
                new Player("Scottie", "Pippen"),
                new Player("Steve", "Kerr"));

        StepVerifier.create(playerFlux)
                .expectNextMatches(p -> playerList.contains(p))
                .expectNextMatches(p -> playerList.contains(p))
                .expectNextMatches(p -> playerList.contains(p))
                .verifyComplete();
    }

    @Test
    void buffer1() {
        Flux<String> fruitFlux = Flux.just("apple", "orange", "banana", "kiwi", "strawberry");

        Flux<List<String>> buffer = fruitFlux.buffer(2);

        StepVerifier.create(buffer)
                .expectNext(Arrays.asList("apple","orange","banana"))
                .expectNext(Arrays.asList("banana","kiwi"))
                .expectNext(Arrays.asList("strawberry"))
                .verifyComplete();
    }
    @Test
    void buffer2() {
        Flux.just("apple", "orange", "banana", "kiwi", "strawberry")
                .buffer(3)
                .flatMap(l -> {
                    return Flux.fromIterable(l)
                                    .map(String::toUpperCase)
                                    .subscribeOn(Schedulers.parallel())
                                    .log();
                        }
                ).subscribe();

    }

    @Test
    void collectMap() {
        Flux<String> animalFlux = Flux.just("aardvark", "elephant", "koala", "eagle", "kangaroo");

        Mono<Map<Character, String>> mapMono =
                animalFlux.collectMap(a -> a.charAt(0));

        StepVerifier.create(mapMono)
                .expectNextMatches(map -> {
                    return
                            map.size() == 3 &&
                                    map.get('a').equals("aardvark") &&
                                    map.get('e').equals("eagle") &&
                                    map.get('k').equals("kangaroo");
                })
                .verifyComplete();
    }

    @Test
    void all() {
        Flux<String> animalFlux = Flux.just("aardvark", "elephant", "koala", "eagle", "kangaroo");

        Mono<Boolean> hasAMono = animalFlux.all(a -> a.contains("a"));
        StepVerifier.create(hasAMono)
                .expectNext(true)
                .verifyComplete();
    }

}
