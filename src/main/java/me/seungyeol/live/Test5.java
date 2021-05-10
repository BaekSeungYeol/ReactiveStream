package me.seungyeol.live;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class Test5 {
    public static void main(String[] args) {

        Flux.range(0,5)
                .subscribeOn(Schedulers.newSingle("subOn"))
                .map(i -> i*10)
                .publishOn(Schedulers.newSingle("pubOn"))
                .log()
                .map(i -> -i)
                .subscribe();
    }
}
