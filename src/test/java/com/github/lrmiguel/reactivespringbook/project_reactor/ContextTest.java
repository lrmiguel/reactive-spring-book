package com.github.lrmiguel.reactivespringbook.project_reactor;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Signal;
import reactor.core.publisher.SignalType;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
public class ContextTest {

    @Test
    public void context() throws Exception {
        var observedContextValues = new ConcurrentHashMap<String, AtomicInteger>();
        var max = 3;
        var key = "key1";
        var cdl = new CountDownLatch(max);
        Context context = Context.of(key, "value1");
        Flux<Integer> just = Flux
                .range(0, max)
                .delayElements(Duration.ofMillis(1))
                // The doOnEach operator is a handy way to gain access to the current Context, whose contents
                // you can then inspect.
                .doOnEach((Signal<Integer> integerSignal) -> {
                    ContextView currentContext = integerSignal.getContextView();
                    if (integerSignal.getType().equals(SignalType.ON_NEXT)) {
                        String key1 = currentContext.get(key);
                        Assertions.assertNotNull(key1);
                        Assertions.assertEquals(key1, "value1");

                        observedContextValues
                                .computeIfAbsent("key1", k -> new AtomicInteger(0))
                                .incrementAndGet();

                    }
                })
                .contextWrite(context);
        just.subscribe(integer -> {
            log.info("integer: " + integer);
            cdl.countDown();
        });

        cdl.await();

        Assertions.assertEquals(observedContextValues.get(key).get(), max);
    }

}
