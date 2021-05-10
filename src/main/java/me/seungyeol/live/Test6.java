package me.seungyeol.live;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class Test6 {
    public static void main(String[] args) {
        Flux.range(0,5)
                .map(i -> i)
                .subscribeOn(Schedulers.newSingle("sub1"))
                .map(i -> i)
                .subscribeOn(Schedulers.newSingle("sub2"))
                .map(i -> i)
                .log()
                .subscribe();
    }
}
