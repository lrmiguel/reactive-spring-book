package com.github.lrmiguel.reactivespringbook.project_reactor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class HotStreamTest3 {

    private final List<Integer> one = new ArrayList<>();

    private final List<Integer> two = new ArrayList<>();

    private final List<Integer> three = new ArrayList<>();

    private Consumer<Integer> subscribe(List<Integer> list) {
        return list::add;
    }

    @Test
    public void publish() throws Exception {

        Flux<Integer> pileOn = Flux.just(1, 2, 3).publish().autoConnect(3)
                .subscribeOn(Schedulers.immediate());// Force the subscription on the same thread so we can observe the interactions

        pileOn.subscribe(subscribe(one));
        Assertions.assertEquals(0, this.one.size());

        pileOn.subscribe(subscribe(two));
        Assertions.assertEquals(0, this.two.size());

        pileOn.subscribe(subscribe(three));
        Assertions.assertEquals(3, this.three.size());

        Assertions.assertEquals(3, this.two.size());
        Assertions.assertEquals(3, this.one.size());

    }

}
