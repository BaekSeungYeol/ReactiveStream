package me.seungyeol.reactive.Webflux;

import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;

public class PublishEx {

    public static void main(String[] args) throws InterruptedException {

        Flux.interval(Duration.ofMillis(100)).take(10).subscribe(s -> System.out.println(s));


        Thread.sleep(5000);
    }

}
