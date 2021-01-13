package com.github.lrmiguel.reactivespringbook;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SimpleFluxFactoriesTest {

    @Test
    public void simple() {

        // Creates a new Flux whose values are in a (finite) range
        Publisher<Integer> rangeOfIntegers = Flux.range(0, 10);
        StepVerifier.create(rangeOfIntegers).expectNextCount(10).verifyComplete();

        // Creates a new Flux whose values are the literal strings A, B, and C
        Flux<String> letters = Flux.just("A", "B", "C");
        StepVerifier.create(letters).expectNext("A", "B", "C").verifyComplete();

        // Creates a new Mono whose single value is a java.util.Date
        var now = System.currentTimeMillis();
        Mono<Date> greetingMono = Mono.just(new Date(now));
        StepVerifier.create(greetingMono).expectNext(new Date(now)).verifyComplete();

        // Creates an empty Mono
        Mono<Object> empty = Mono.empty();
        StepVerifier.create(empty).verifyComplete();

        // Creates a Flux whose elements come from a Java Array
        Flux<Integer> fromArray = Flux.fromArray(new Integer[] {1, 2, 3});
        StepVerifier.create(fromArray).expectNext(1, 2, 3).verifyComplete();

        // Creates a Flux whose elements come from a Java Iterable, which describes among other things all
        // java.util.Collection subclasses like List, Set, etc
        Flux<Integer> fromIterable = Flux.fromIterable(Arrays.asList(1, 2, 3));
        StepVerifier.create(fromIterable).expectNext(1, 2, 3).verifyComplete();

        // Creates a new Flux from a Java 8 Stream
        AtomicInteger integer = new AtomicInteger();
        Supplier<Integer> supplier = integer::incrementAndGet;
        Flux<Integer> integerFlux = Flux.fromStream(Stream.generate(supplier));
        StepVerifier.create(integerFlux.take(3)).expectNext(1).expectNext(2).expectNext(3).verifyComplete();
    }
}
