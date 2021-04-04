package me.seungyeol.reactive.JavaNonBlockingAfter8;


import lombok.extern.slf4j.Slf4j;

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


        ExecutorService es = Executors.newFixedThreadPool(10);
        CompletableFuture
                .supplyAsync(() -> {
                    log.info("runAsync");
                    return 1;
                }, es)
                .thenCompose(s -> {
                    log.info("thenApply: {}", s);
                    return CompletableFuture.completedFuture(s+1);
                })
                .thenApplyAsync(s2-> {
                    log.info("thenApply : {}" , s2);
                    return s2+3;
                }, es)
                .exceptionally(e -> -10)
                .thenAcceptAsync((s3) -> {
                    log.info("thenAccept {}", s3);
                }, es);


        log.info("exit");

        ForkJoinPool.commonPool().shutdown();
        ForkJoinPool.commonPool().awaitTermination(10, TimeUnit.SECONDS);

    }

    private static Integer start() throws InterruptedException {
        Thread.sleep(3000);
        return 3;
    }

}
