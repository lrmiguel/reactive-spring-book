package com.github.lrmiguel.reactivespringbook.project_reactor;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
public class HandleTest {

    @Test
    public void handle() {

        StepVerifier
                .create(this.handle(5, 4))
                .expectNext(0, 1, 2, 3)
                .expectError(IllegalArgumentException.class)
                .verify();

        StepVerifier
                .create(this.handle(3, 3))
                .expectNext(0, 1, 2)
                .verifyComplete();
    }

    Flux<Integer> handle(int max, int numberToError) {
        return Flux
                // The Publisher<T> publishes max elements which are then passed to the handle
                // method where we can veto its emission, emit an error, or anything else we'd like to do
                .range(0, max)
                .handle((value, sink) -> {
                    var upTo = Stream
                            .iterate(0, i -> i < numberToError, i -> i + 1)
                            .collect(Collectors.toList());
                    if (upTo.contains(value)) {
                        sink.next(value);
                        return;
                    }
                    if (value == numberToError) {
                        sink.error(new IllegalArgumentException("No 4 for you"));
                        return;
                    }
                    sink.complete();
                });
    }
}
