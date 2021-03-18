package com.github.lrmiguel.reactivespringbook.project_reactor;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

@Log4j2
public class ControlFlowFirstTest {

    @Test
    public void first() {
        Flux<Integer> slow = Flux.just(1, 2, 3).delayElements(Duration.ofMillis(100));
        Flux<Integer> fast = Flux.just(4, 5, 6, 7).delayElements(Duration.ofMillis(2));
        Flux<Integer> first = Flux.firstWithSignal(slow, fast);

        StepVerifier.create(first).expectNext(4, 5, 6, 7).verifyComplete();
    }
}
