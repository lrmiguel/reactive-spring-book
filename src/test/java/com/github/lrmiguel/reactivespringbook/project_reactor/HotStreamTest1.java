package com.github.lrmiguel.reactivespringbook.project_reactor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.FluxSink;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class HotStreamTest1 {

    @Test
    public void hot() throws Exception {

        var first = new ArrayList<Integer>();
        var second = new ArrayList<Integer>();

        EmitterProcessor<Integer> emitter = EmitterProcessor.create(2);
        FluxSink<Integer> sink = emitter.sink();

        emitter.subscribe(collect(first));
        sink.next(1);
        sink.next(2);

        emitter.subscribe(collect(second));
        sink.next(3);
        sink.complete();

        // There should be more elements captured in the first subscriber's collection than in the second
        // one since the second one only observed one element.
        Assertions.assertTrue(first.size() > second.size());
    }

    Consumer<Integer> collect(List<Integer> collection) {
        return collection::add;
    }
}
