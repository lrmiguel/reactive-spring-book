package com.github.lrmiguel.reactivespringbook.project_reactor;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
public class SchedulersSubscribeOnTest {

    @Test
    public void subscribeOn() {
        var rsbThreadName = SchedulersSubscribeOnTest.class.getName();
        var map = new ConcurrentHashMap<String, AtomicInteger>();
        var executor = Executors.newFixedThreadPool(5, runnable -> {
            Runnable wrapper = () -> {
                var key = Thread.currentThread().getName();
                var result = map.computeIfAbsent(key, s -> new AtomicInteger());
                result.incrementAndGet();
                runnable.run();
            };
            return new Thread(wrapper, rsbThreadName);
        });

        // We create our own Scheduler using a custom Executor. Each thread created in our custom
        // Executor ends up wrapped in a custom Runnable that notes the name of the current thread and
        // increments the reference count
        Scheduler scheduler = Schedulers.fromExecutor(executor);

        // Uses the subscribeOn method to move the subscription to our custom Scheduler
        Flux<Integer> integerFlux = Flux.just(1).subscribeOn(scheduler)
                .doFinally(signal -> map.forEach((k, v) -> log.info(k + '=' + v)));

        StepVerifier.create(integerFlux).expectNextCount(1).verifyComplete();
        var atomicInteger = map.get(rsbThreadName);
        Assertions.assertEquals(1, atomicInteger.get());
    }
}
