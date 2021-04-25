package me.seungyeol.reactive.JavaNonBlockingAfter8;


import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.*;

@Slf4j
public class CFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        CompletableFuture<Integer> f = new CompletableFuture<>();
//
//        f.completeExceptionally(new RuntimeException());
//        System.out.println(f.get());



//        CompletableFuture.runAsync(() -> log.info("runAsync"))
//                .thenRun(() -> log.info("thenRun"))
//                .thenRun(() -> log.info("thenRun"));
//        log.info("exit");



//        CompletableFuture
//                .supplyAsync(() -> {
//                    log.info("runAsync");
//                    return 1;
//                })
//                .thenApply(s -> {
//                    log.info("thenApply: {}", s);
//                    return s+1;
//                })
//                .thenApply(s2-> {
//                    log.info("thenApply : {}" , s2);
//                    return s2+3;
//                })
//                .thenAccept((s3) -> {
//                    log.info("thenAccept {}", s3);
//                });


//        CompletableFuture
//                .supplyAsync(() -> {
//                    log.info("runAsync");
//                    if(1 == 1) throw new RuntimeException();
//                    return 1;
//                })
//                .thenCompose(s -> {
//                    log.info("thenApply: {}", s);
//                    return CompletableFuture.completedFuture(s+1);
//                })
//                .thenApply(s2-> {
//                    log.info("thenApply : {}" , s2);
//                    return s2+3;
//                })
//                .exceptionally(e -> -10)
//                .thenAccept((s3) -> {
//                    log.info("thenAccept {}", s3);
//                });


//        ExecutorService es = Executors.newFixedThreadPool(10);
//        CompletableFuture
//                .supplyAsync(() -> {
//                    log.info("runAsync");
//                    return 1;
//                }, es)
//                .thenCompose(s -> {
//                    log.info("thenApply: {}", s);
//                    return CompletableFuture.completedFuture(s+1);
//                })
//                .thenApplyAsync(s2-> {
//                    log.info("thenApply : {}" , s2);
//                    return s2+3;
//                }, es)
//                .exceptionally(e -> -10)
//                .thenAcceptAsync((s3) -> {
//                    log.info("thenAccept {}", s3);
//                }, es);
//
//
//        log.info("exit");
//
//        ForkJoinPool.commonPool().shutdown();
//        ForkJoinPool.commonPool().awaitTermination(10, TimeUnit.SECONDS);



        /*
          Map.Entry::getKey,
                e -> e.getValue()
                      .values()
                      .stream()
                      .reduce(0d, (a, b) -> a + b)
                )
         */

        // 후보
        // 유저의 선호하는 카 찾기

//        Mono<List<CarInfo>> m1 = Flux.fromIterable(carInfos).distinct().collectList();
//        Mono<LinkedHashMap<String, Integer>> reduce = Flux.fromIterable(carInfos)
//                .reduce((c1, c2) -> new LinkedHashMap<String, Integer>(){{
//                    put(c1.getType(), getOrDefault(c1.getType(),0) + c1.getFailedTime());
//                    put(c2.getType(), getOrDefault(c2.getType(),0) + c2.getFailedTime());
//                }});// Flux<Map<String,Integer>>
//
//        reduce.subscribe(System.out::println);
//

        List<CarInfo> carInfos = test();
        Mono<List<String>> distinctType = Flux.fromIterable(carInfos).map(CarInfo::getType).distinct().collectList();
        Mono<HashMap<String, Long>> favoriteType = Flux.fromIterable(carInfos)
                .groupBy(carInfo -> carInfo)
                .flatMap(groupedFlux -> groupedFlux.count()
                        .map(count -> {
                            HashMap<String, Long> map = new LinkedHashMap<>();
                            map.put(groupedFlux.key().getType(), count);
                            return map;
                        }))
                .reduce((befMap, curMap) -> new HashMap<String, Long>() {{
                    putAll(befMap);
                    putAll(curMap);
                }});
        Flux.zip(distinctType,favoriteType, FavoriteTypeInfo::new);



//                .collectMultimap(CarInfo::getType).log();// Flux<List<CarInfo>>
//        mapMono.subscribe(s -> System.out.println(s));
//        TestType("a1",3);
//        List<TestType> a = new LinkedList<>();

        // a1 3
        // a1 2
        // a2 4
        // a3 3
        // a3 3
        // a2 3

        // a1 5
        // a2 7
        // a3 6


//        HashMap<String, Integer> map = new HashMap<>();
//        for(int i=0; i< carInfos.size(); ++i) {
//            CarInfo car = carInfos.get(i);
//            map.put(car.getType(),map.getOrDefault(car.getType(),0)+car.getFailedTime());
//        }
//        for (Map.Entry<String, Integer> stringIntegerEntry : map.entrySet()) {
//            System.out.println(stringIntegerEntry.getKey() + " " + stringIntegerEntry.getValue());
//        }

        // Map.put(key(), getOrDefault(map.get(key()),0)+1);



    }
    static List<CarInfo> test() {
        List<CarInfo> car = new LinkedList<>();
        List<String> types = List.of("k1","k2","k3","k4");
        for(int i=0; i<100; ++i) car.add(new CarInfo(i,types.get(i%4)));
        return car;
    }

    private static Integer start() throws InterruptedException {
        Thread.sleep(3000);
        return 3;
    }

}
