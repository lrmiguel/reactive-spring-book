package com.github.lrmiguel.reactivespringbook.project_reactor;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Log4j2
public class ControlFlowTimeoutTest {

    @Test
    public void timeout() {
        Flux<Integer> ids = Flux.just(1, 2, 3).delayElements(Duration.ofSeconds(1))
                .timeout(Duration.ofMillis(500)).onErrorResume(this::given);
        StepVerifier.create(ids).expectNext(0).verifyComplete();
    }

    private Flux<Integer> given(Throwable t) {
        Assertions
                .assertTrue(t instanceof TimeoutException,
                        "this exception should be a " + TimeoutException.class.getName());
        return Flux.just(0);
    }
}
