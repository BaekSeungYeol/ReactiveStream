package me.seungyeol.reactive.Webflux;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
@RestController
@Slf4j
public class FluxController {

    @GetMapping("/event/{id}")
    Mono<Event> event(@PathVariable long id) {
        return Mono.just(new Event(id,"event + id"));
    }

    @GetMapping("/event2/{id}")
    Mono<List<Event>> event2(@PathVariable long id) {
        return Mono.just(List.of(
                new Event(1L,"event1"),
                new Event(2L,"event2")
        ));
    }

//    @GetMapping(value="/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    Flux<Event> events() {
//        return Flux.just(
//                new Event(1L,"event1"),
//                new Event(2L,"event2")
//        );
//    }

    @GetMapping(value="/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Event> events() {
        //List<Event> list = Arrays.asList(new Event(1L, "events1"), new Event(2L, "events2"));
        //return Flux.fromIterable(list);

//        Stream<Event> s = Stream.generate(() -> new Event(System.currentTimeMillis(), "value"));
//        return Flux
//                .fromStream(s)
//                .delayElements(Duration.ofSeconds(1))
//                .take(10);


//                Flux
//                .<Event, Long>generate(() -> 1L, (id, sink) -> {
//                    sink.next(new Event(id, "value" + id));
//                    return id + 1;
//                });
//                .delayElements(Duration.ofSeconds(1))
//                .take(10);

//        Flux<Event> f = Flux.<Event, Long>generate(() -> 1L, (id, sink) -> {
//            sink.next(new Event(id, "value" + id));
//            return id + 1;
//        });
//        Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));
//        return Flux.zip(f, interval).map(tu -> tu.getT1()).take(10);


//        Flux<String> es = Flux.generate(sink -> sink.next("Value"));
//        Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));
//        return Flux.zip(es,interval).map(tu -> new Event(tu.getT2(),tu.getT1())).take(10);

        List<Integer> a = List.of(1,2,3,4,5);
        a.stream().flatMap(i -> Arrays.stream(new Integer[]{i,i,i})).collect(Collectors.toList());

        return null;
    }



    @Data @AllArgsConstructor
    public static class Event {
        long id;
        String value;
    }

}