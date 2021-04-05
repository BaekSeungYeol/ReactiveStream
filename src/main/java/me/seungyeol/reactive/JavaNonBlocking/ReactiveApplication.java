package me.seungyeol.reactive.JavaNonBlocking;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;


@SpringBootApplication
@Slf4j
@EnableAsync
public class ReactiveApplication {



    static final String URL1 = "http://localhost:8081/service?req={req}";
    static final String URL2 = "http://localhost:8081/service2?req={req}";


    @RestController
    public static class MyController {
        @Autowired MyService myService;

//        @GetMapping("/rest")
//        public DeferredResult<String> rest(int idx) {
//            DeferredResult<String> dr = new DeferredResult<>();

//            Completion
//                    .from(rt.getForEntity(URL1, String.class, "h" + idx))
//                    .andApply(s -> rt.getForEntity(URL2, String.class, s.getBody()))
//                    .andApply(s -> myService.work(s.getBody()))
//                    .andError(e -> dr.setErrorResult(e.toString()))
//                    .andAccept(s -> dr.setResult(s));


//            ListenableFuture<ResponseEntity<String>> f1 = rt.getForEntity(URL1, String.class, "h" + idx);
//            f1.addCallback(s-> {
//                ListenableFuture<ResponseEntity<String>> f2 = rt.getForEntity(URL2, String.class, s.getBody());
//                f2.addCallback(s2-> {
//
//                    ListenableFuture<String> f3 = myService.work(s2.getBody());
//                    f3.addCallback(s3-> {
//                        dr.setResult(s3);
//                    }, e-> {
//                        dr.setErrorResult(e.getMessage());
//                    });
//                }, e-> {
//                    dr.setErrorResult(e.getMessage());
//                });
//
//                dr.setResult(s.getBody() + "/work");
//            }, e-> {
//               dr.setErrorResult(e.getMessage());
//            });

            //CompletableFuture<ResponseEntity<String>> f = toCF(rt.getForEntity(URL1, String.class, "h" + idx));

//            toCF(rt.getForEntity(URL1, String.class, "h" + idx))
//                    .thenCompose(s -> {
//                        if (1 == 1) throw new RuntimeException("ERROR");
//                        return toCF(rt.getForEntity(URL2, String.class, s.getBody()));
//                    })
//                    .thenApplyAsync(s2 -> (myService.work(s2.getBody())))
//                    .thenAccept(s3 -> dr.setResult(s3))
//                    .exceptionally(e -> { dr.setErrorResult(e.getMessage()); return null;});

//            return dr;
//        }

        //Spring 5.0



        WebClient client = WebClient.create();
        @GetMapping("/rest")
        public Mono<String> rest(int idx) {
//            String s = "HI";
//            Mono<String> s2 = Mono.just("HI");

            Mono<String> bef = client.get().uri(URL1,idx).exchange() // Mono<ClientResponse>
                    .flatMap(c -> c.bodyToMono(String.class)) // Mono<String>
                    .flatMap((String res1) -> client.get().uri(URL2,res1).exchange()) // Mono<ClientResponse>
                    .flatMap(c -> c.bodyToMono(String.class)) // Mono<String>
                     .doOnNext(c -> log.info("The2:" + c.toString()))
                    .flatMap(res2 -> Mono.fromCompletionStage(myService.work(res2)))
                      .doOnNext(c -> log.info("The3: " + c.toString()));
            return bef;
        }

        @GetMapping("/rest2")
        public Mono<String> rest2(int mymy) {
            Mono<String> body = client.get().uri(URL1, mymy)
                    .exchangeToMono(c -> c.bodyToMono(String.class))
                    .doOnNext(c -> log.info("The:" + c.toString()))
                    .flatMap((String res1) -> client.get().uri(URL2, res1)
                            .exchangeToMono(c -> c.bodyToMono(String.class)))
                    .doOnNext(c -> log.info("The2:" + c.toString()))
                    .flatMap((res2 -> Mono.fromCompletionStage(myService.work(res2)))) // Mono<String>
                                    // COmpletableFuture<String> -> Mono<String>
                    .doOnNext(c -> log.info("The3: " + c.toString()));

            return body;
        }

        public static void main(String[] args) {
            SpringApplication.run(ReactiveApplication.class, args);
        }













//        <T> CompletableFuture<T> toCF(ListenableFuture<T> lf) {
//            CompletableFuture<T> cf = new CompletableFuture<>();
//            lf.addCallback(s-> cf.complete(s), e->cf.completeExceptionally(e));
//            return cf;
//        }

        public static class AcceptCompletion<S> extends Completion<S,Void> {
            public Consumer<S> con;
            public AcceptCompletion(Consumer<S> con) {
                this.con = con;
            }
            @Override
            public void run(S value) {
                con.accept(value);
            }
        }

        public static class ApplyCompletion<S,T> extends Completion<S,T> {
            Function<S, ListenableFuture<T>> fn;

            public ApplyCompletion(Function<S, ListenableFuture<T>> fn) {
                this.fn = fn;
            }

            @Override
            public void run(S value) {
                ListenableFuture<T> lf = fn.apply(value);
                lf.addCallback(s -> { complete(s); }, e-> { error(e); });
            }
        }
        public static class ErrorCompletion<T> extends Completion<T,T> {
            Consumer<Throwable> econ;

            public ErrorCompletion(Consumer<Throwable> econ) {
                this.econ = econ;
            }
            @Override
            public void run(T value) {
                if(next != null) next.run(value);
            }

            @Override
            public void error(Throwable e) {
                econ.accept(e);
            }
        }

        public static class Completion<S,T> {
             Completion next;


            public Completion() {
            }

            public static <S,T> Completion<S,T> from(ListenableFuture<T> lf) {
                Completion<S,T> c = new Completion();
                lf.addCallback(s -> {
                    c.complete(s);
                }, e-> {
                    c.error(e);
                });
                return c;
            }

            public  void error(Throwable e) {
                if(next != null) next.error(e);
            }

            public  void complete(T s) {
                if(next != null) next.run(s);
            }

            public void run(S value) {

            }
            public <V> Completion<T,V> andApply(Function<T,ListenableFuture<V>> fn) {
                Completion<T,V> c = new ApplyCompletion<>(fn);
                this.next = c;
                return c;
            }
            public void andAccept(Consumer<T> con) {
                Completion<T,Void> c = new AcceptCompletion(con);
                this.next = c;
            }

            public Completion<T,T> andError(Consumer<Throwable> con) {
                Completion<T,T> c = new ErrorCompletion<>(con);
                this.next = c;
                return c;
            }
        }


        @Service
        public static class MyService {
//            @Async
//            public ListenableFuture<String> work(String req) {
//                return new AsyncResult<>(req + "/asyncwork");
//            }

            @Async
            public CompletableFuture<String> work(String req)  {
                return CompletableFuture.completedFuture(req + "/asyncwork");
            }
        }
    }
    @Bean
    ThreadPoolTaskExecutor myExecutor() {
        ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
        te.setCorePoolSize(1);
        te.setMaxPoolSize(10);
        te.initialize();
        return te;
    }


}
