package me.seungyeol.live;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;

public class Test3 {
    public static void main(String[] args) throws InterruptedException {

        Flux<Integer> just = Flux.fromIterable(List.of(1,2,3,4,5));
//        Flux.just(1,2,3,4,5);
//        just.map(Test3::anotherTask).subscribeOn(Schedulers.parallel()).log().subscribe();
        // onNext에서는 기본적으로 같은 스레드들로 처리 된다.
//        just.map(Test3::anotherTask).log();


//        just.flatMap(i -> {
//            return Flux.just(i).map(Test3::anotherTask).subscribeOn(Schedulers.parallel()).log();
//        }).subscribe();


        just.flatMapSequential(i -> {
            return Flux.just(i).map(Test3::anotherTask).subscribeOn(Schedulers.parallel()).log();
        }).subscribe(s -> System.out.println(s));

        System.out.println("Exit");
        Thread.sleep(50000L);
    }

    public static int anotherTask(int i) {

        try {
            if(i == 5)  Thread.sleep(1000L);
            if(i == 4) Thread.sleep(1500L);
            if(i == 3) Thread.sleep(2000L);
            if(i == 2) Thread.sleep(2500L);
            if(i == 1) Thread.sleep(3000L);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }
}
