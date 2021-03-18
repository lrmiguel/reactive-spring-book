package com.github.lrmiguel.reactivespringbook.project_reactor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicBoolean;

public class TransformTest {

    @Test
    public void transform() {
        var finished = new AtomicBoolean();

        // The transform operator gives us a chance to act on a Flux<T>, customizing it. This can be quite
        // useful if you want to avoid extra intermediate variables.
        var letters = Flux.just("A", "B", "C")
                .transform(stringFlux -> stringFlux.doFinally(signal -> finished.set(true)));
        StepVerifier.create(letters).expectNextCount(3).verifyComplete();
        Assertions.assertTrue(finished.get(), "the finished Boolean must be true.");
    }
}
