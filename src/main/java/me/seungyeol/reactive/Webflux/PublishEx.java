package me.seungyeol.reactive.Webflux;

import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class PublishEx {

    public static void main(String[] args) throws InterruptedException {

        final List<String> basket1 = Arrays.asList("kiwi", "orange", "lemon", "orange", "lemon", "kiwi");
        final List<String> basket2 = Arrays.asList("banana", "lemon", "lemon", "kiwi");
        final List<String> basket3 = Arrays.asList("strawberry", "orange", "lemon", "grape", "strawberry");
        final List<List<String>> baskets = Arrays.asList(basket1, basket2, basket3);
        final Flux<List<String>> basketFlux = Flux.fromIterable(baskets);

        List<String> hi = List.of("1","2","3","4","5","1","2","3");
        Mono<List<String>> listMono = Flux.fromIterable(hi).distinct().collectList();
//        Flux<Map<String, Long>> mapFlux = Flux.fromIterable(hi)
//                .groupBy(a -> a)
//                .concatMap(groupedFlux -> groupedFlux.count()
//                        .map(count -> {
//                            final Map<String, Long> fruitCount = new LinkedHashMap<>();
//                            fruitCount.put(groupedFlux.key(), count);
//                            return fruitCount;
//                        }))
//                .reduce((acMap, curMap) -> new LinkedHashMap<String,Long>() { {
//                    putAll(acMap);
//                    putAll(curMap);
//                        }});

//        LinkedHashMap<String,Long> map1 = new LinkedHashMap<>(); map1.put("apple", 2L);
//        LinkedHashMap<String,Long> map2 = new LinkedHashMap<>(); map2.put("pine",5L);
//        LinkedHashMap<String,Long> map = new LinkedHashMap<>() {
//            {
//                putAll(map1);
//                putAll(map2);
//            }
//        };
//        for (Map.Entry<String, Long> stringLongEntry : map.entrySet()) {
//            System.out.println(stringLongEntry.getKey() + " " + stringLongEntry.getValue());
//        }

        //1 2 3 4 5
//        Flux.range(1,5)
//                .reduce((a,b) -> a+b)
//                .log()
//                .subscribe(s-> System.out.println(s));

        basketFlux.concatMap(basket -> {
            // basket -> List<String>                   // Flux<String>
            final Mono<List<String>> distinctFruits = Flux.fromIterable(basket).distinct().collectList();
            final Mono<Map<String, Long>> countFruitsMono = Flux.fromIterable(basket)
                    .groupBy(fruit -> fruit) // 바구니로 부터 넘어온 과일 기준으로 group을 묶는다.
                    .concatMap(groupedFlux -> groupedFlux.count()
                            .map(count -> {
                                final Map<String, Long> fruitCount = new LinkedHashMap<>();
                                fruitCount.put(groupedFlux.key(), count);
                                return fruitCount;
                            }) // 각 과일별로 개수를 Map으로 리턴
                    ) // concatMap으로 순서보장
                    .reduce((accumulatedMap, currentMap) -> new LinkedHashMap<String, Long>() { {
                        putAll(accumulatedMap);
                        putAll(currentMap);
                    }}); // 그동안 누적된 accumulatedMap에 현재 넘어오는 currentMap을 합쳐서 새로운 Map을 만든다. // map끼리 putAll하여 하나의 Map으로 만든다.
            return Flux.zip(distinctFruits, countFruitsMono, FruitInfo::new);
        }).subscribe(System.out::println);

    }
    static class FruitInfo {

        private final List<String> distinctFruits;
        private final Map<String,Long> countFruits;

        public FruitInfo(List<String> distinctFruits, Map<String, Long> countFruits) {
            this.distinctFruits = distinctFruits;
            this.countFruits = countFruits;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FruitInfo fruitInfo = (FruitInfo) o;
            return Objects.equals(distinctFruits, fruitInfo.distinctFruits) &&
                    Objects.equals(countFruits, fruitInfo.countFruits);
        }

        @Override
        public int hashCode() {
            return Objects.hash(distinctFruits, countFruits);
        }

        @Override
        public String toString() {
            return "FruitInfo{" +
                    "distinctFruits=" + distinctFruits +
                    ", countFruits=" + countFruits +
                    '}';
        }
    }

}
