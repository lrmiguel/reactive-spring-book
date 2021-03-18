package com.github.lrmiguel.reactivespringbook.service;

import com.github.lrmiguel.reactivespringbook.config.CustomerDatabaseInitializer;
import com.github.lrmiguel.reactivespringbook.entity.Customer;
import com.github.lrmiguel.reactivespringbook.repository.SimpleCustomerRepository;
import com.github.lrmiguel.reactivespringbook.repository.SpringDataCustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Log4j2
@RequiredArgsConstructor
public class CustomerService {

    private final SpringDataCustomerRepository repository;

    private final TransactionalOperator operator;

    private final CustomerDatabaseInitializer initializer;

    public Publisher<Void> resetDatabase() {
        return this.initializer.resetCustomerTable();
    }

    // The upsert finds an existing record by its email or, if it doesn't exist, adds a new one
    public Flux<Customer> upsert(String email) {
        var customers = this.repository.findAll()
                .filter(customer -> customer.getEmail().equalsIgnoreCase(email))
                .flatMap(match -> this.repository.update(new Customer(match.getId(), email)))
                .switchIfEmpty(this.repository.save(new Customer(null, email)));
        var validatedResults = errorIfEmailsAreInvalid(customers);
        return this.operator.transactional(validatedResults);
    }

    // The normalizeEmails method iterates through all the data in the database and confirms that
    // each record's emails are correct
    @Transactional
    public Flux<Customer> normalizeEmails() {
        return errorIfEmailsAreInvalid(this.repository.findAll()
                .flatMap(x -> this.upsert(x.getEmail().toUpperCase())));
    }

    private static Flux<Customer> errorIfEmailsAreInvalid(Flux<Customer> input) {
        return input.filter(c -> c.getEmail().contains("@"))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("the email needs to be of the from a@b.com")));
    }
}
