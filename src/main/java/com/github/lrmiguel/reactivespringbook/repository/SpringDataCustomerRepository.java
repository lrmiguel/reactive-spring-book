package com.github.lrmiguel.reactivespringbook.repository;

import com.github.lrmiguel.reactivespringbook.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Primary
public class SpringDataCustomerRepository implements SimpleCustomerRepository {

    private final CustomerRepository repository;

    @Override
    public Mono<Customer> save(Customer c) {
        return repository.save(c);
    }

    public Mono<Customer> update(Customer c) {
        return repository.save(c);
    }

    public Mono<Customer> findById(Integer id) {
        return repository.findById(id);
    }

    public Mono<Void> deleteById(Integer id) {
        return repository.deleteById(id);
    }

    public Flux<Customer> findAll() {
        return repository.findAll();
    }

}
