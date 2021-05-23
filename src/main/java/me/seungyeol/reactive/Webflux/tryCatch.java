package me.seungyeol.reactive.Webflux;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Schedulers;

public class tryCatch {

    @Autowired
    WebClient localhost;

    String sendMessage(String s) {
        localhost.get("/temp")
                ..;

        return s;
    }
    Mono<String> sendAnotherMessage() {
        return Mono.just("temp");
    }
    void test2(MyRequest myRequest) {
        MyResponse myResponse = new MyResponse();

        String response = "";
        Mono.just(response)
                .map(myRequest::up)
                .filter(myRequest::isDataOn)
                .switchIfEmpty(MyRequest.down())
                .onErrorMap(RuntimeException.class, e -> new CustomException(e.getMessage()))
                .map(myResponse::toBuilder)
                .doFinally(type -> {
                    if(type == SignalType.ON_COMPLETE) {
                        sendAnotherMessage().subscribeOn(Schedulers.parallel());
                    }
                });


    }
    void test(MyRequest myRequest) {

        String response = "";
        try {
            if(myRequest.getData().equals("on")){
                response = "up";
            } else {
                response = "down";
            }
        } catch (RuntimeException e) {
            response = "";
            throw new CustomException(e.getMessage());
        } finally {
            if(Strings.isNotBlank(response)) {
                sendAnotherMessage();
            }
        }
    }
}
