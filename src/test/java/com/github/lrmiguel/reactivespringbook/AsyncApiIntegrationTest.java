package com.github.lrmiguel.reactivespringbook;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.test.StepVerifier;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
public class AsyncApiIntegrationTest {

    private final ExecutorService executorService = Executors.newFixedThreadPool(1);

    @Test
    public void async() {
        // The Flux.create factory passes a reference to a FluxSink<T> in a Consumer<FluxSink<T>>. We will
        // use the FluxSink<T> to emit new elements as they become available. It is important that we
        // stash this reference for later
        Flux<Integer> integers = Flux.create(emitter -> this.launch(emitter, 5));

        // It's important to tear down any resources once the Flux has finished its work
        StepVerifier
                .create(integers.doFinally(signalType -> this.executorService.shutdown()))
                .expectNextCount(5)
                .verifyComplete();
    }

    // The launch method spins up a background thread using the ExecutorService. Setup whatever
    // connections with an external API only after execution inside the callback has begun.
    private void launch(FluxSink<Integer> integerFluxSink, int count) {
        this.executorService.submit(() -> {
            var integer = new AtomicInteger();
            Assertions.assertNotNull(integerFluxSink);
            while (integer.get() < count) {
                double random = Math.random();

                // Each time there's a new element, use the FluxSink<T> to emit a new element
                integerFluxSink.next(integer.incrementAndGet());

                this.sleep((long) (random * 1_000));
            }


            // Finally, once we've finished emitting elements, we tell the Subscriber<T> instances.
            integerFluxSink.complete();
        });
    }

    private void sleep(long s) {
        try {
            Thread.sleep(s);
        } catch (Exception e) {
            log.error(e);
        }
    }
}
