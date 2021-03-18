package com.github.lrmiguel.reactivespringbook.project_reactor;

import org.junit.jupiter.api.Test;
import org.reactivestreams.FlowAdapters;
import org.reactivestreams.Publisher;
import reactor.adapter.JdkFlowAdapter;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.concurrent.Flow;

public class FlowAndReactiveStreamsTest {

    @Test
    public void convert() {

        // The first few lines demonstrate converting to and from Reactive Streams types
        // with the Reactive Streams conversions
        Flux<Integer> original = Flux.range(0, 10);

        Flow.Publisher<Integer> rangeOfIntegersAsJdk9Flow = FlowAdapters
                .toFlowPublisher(original);
        Publisher<Integer> rangeOfIntegersAsReactiveStream = FlowAdapters
                .toPublisher(rangeOfIntegersAsJdk9Flow);

        StepVerifier.create(original).expectNextCount(10).verifyComplete();
        StepVerifier.create(rangeOfIntegersAsReactiveStream).expectNextCount(10).verifyComplete();

        // The second few lines demonstrate converting to and from Reactor Flux<T> and Mono<T> types
        // using the Reactor conversions
        Flux<Integer> rangeOfIntegersAsReactorFluxAgain = JdkFlowAdapter
                .flowPublisherToFlux(rangeOfIntegersAsJdk9Flow);

        StepVerifier.create(rangeOfIntegersAsReactorFluxAgain).expectNextCount(10)
                .verifyComplete();
    }
}
