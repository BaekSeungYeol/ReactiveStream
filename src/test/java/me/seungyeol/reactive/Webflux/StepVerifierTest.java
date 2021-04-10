package me.seungyeol.reactive.Webflux;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

@SpringBootTest
public class StepVerifierTest {
    String key = "message";

    @Test
    @DisplayName("StepVerifier을 이용한 Flux 테스트")
    public void testAppendBoomError() {
        Flux<String> source = Flux.just("thing1", "thing2");

        StepVerifier
                .create(
                        appendBoomError(source))
                .expectNext("thing1")
                .expectNext("thing2")
                .expectErrorMessage("boom")
                .verify(Duration.ofSeconds(10));
    }

    @Test
    @DisplayName("하루가 걸리는 테스트 가상 동작으로 검증하기")
    public void takeLongTime() {

        Duration duration = StepVerifier.withVirtualTime(() -> Mono.delay(Duration.ofDays(1)))
                .expectSubscription()
                .expectNoEvent(Duration.ofDays(1))
                .expectNext(0L)
                .verifyComplete();

        System.out.println("실제 시간 : " + duration);
    }

    @Test
    @DisplayName("컨텍스트 업스트림 테스트")
    public void contextUpStreamTest() {

        Mono<String> r =  Mono.just("Hello")
                .flatMap(s -> Mono.deferContextual(ctx ->
                        Mono.just(s + " " + ctx.get(key))))
                .contextWrite(ctx -> ctx.put(key, "World"));

        StepVerifier.create(r)
                .expectNext("Hello World")
                .verifyComplete();
    }

    @Test
    @DisplayName("컨텍스트 변경 테스트")
    public void changeContextStatus() {

        Mono<String> r = Mono.just("Default")
                .contextWrite(ctx -> ctx.put(key, "World"))
                .flatMap(s -> Mono.deferContextual(ctx ->
                        Mono.just(s + " " + ctx.getOrDefault(key,"Stranger"))));


        StepVerifier.create(r)
                .expectNext("Hello Stranger")
                .verifyComplete();
    }

    @Test
    @DisplayName("컨텍스트 업스트림 변경 테스트")
    public void changeContextStatusUpstream() {

        Mono<String> r = Mono.deferContextual(ctx -> Mono.just("Hello " + ctx.get(key)))
                .contextWrite(ctx -> ctx.put(key, "Reactor"))
                .contextWrite(ctx -> ctx.put(key, "world"));

        StepVerifier.create(r)
                .expectNext("Hello Reactor")
                .verifyComplete();
    }

    @Test
    @DisplayName("컨텍스트 업스트림 변경 테스트 2")
    public void changeContextStatusUpstream2() {

        Mono<String> r = Mono.deferContextual(ctx -> Mono.just("Hello " + ctx.get(key)))
                .contextWrite(ctx -> ctx.put(key, "Reactor"))
                .flatMap(s -> Mono.deferContextual(ctx ->
                        Mono.just(s + ctx.get(key))))
                .contextWrite(ctx -> ctx.put(key, "World"));

        StepVerifier.create(r)
                .expectNext("Hello Reactor World")
                .verifyComplete();
    }


    @Test
    @DisplayName("컨텍스트 업스트림 변경 테스트 3")
    public void changeContextStatusUpstream3() {

        Mono<String> r = Mono.just("Hello")
                .flatMap(s -> Mono.deferContextual(ctx -> Mono.just(s + " " + ctx.get(key))))
                .flatMap(s -> Mono.deferContextual(ctx -> Mono.just(s + " " + ctx.get(key)))
                        .contextWrite(ctx -> ctx.put(key, "Reactor")))
                .contextWrite(ctx -> ctx.put(key, "World"));

        StepVerifier.create(r)
                .expectNext("Hello World Reactor")
                .verifyComplete();
    }
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

//    @Test
//    public void testSplitPathIsUsed() {
//        StepVerifier.create(processOrFallback(Mono.just("just   a   phrase  with    tabs!"),
//                Mono.just("EMPTY_PHRASE")))
//                .expectNext("just", "a", "phrase", "with", "tabs!")
//                .verifyComplete();
//    }
//
//    @Test
//    public void testEmptyPathIsUsed() {
//        StepVerifier.create(processOrFallback(Mono.empty(),
//                Mono.just("EMPTY_PHRASE")))
//                .expectNext("EMPTY_PHRASE")
//                .verifyComplete();
//    }

    private Mono<String> executeCommand(String command) {
        return Mono.just(command + " DONE");
    }

    @Test
    public void testCommandEmptyPathIsUsed() {

    }

//    public Flux<String> processOrFallback(Mono<String> source,
//                                          Publisher<String> fallback) {
//        return source
//                .flatMapMany(phrase ->
//                        Flux.fromArray(phrase.split("\\s+")))
//                .switchIfEmpty(fallback);
//    }



public class User {
    String name;

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}



    public <T> Flux<T> appendBoomError(Flux<T> source) {
        return source.concatWith(Mono.error(new IllegalArgumentException("boom")));
    }

}

