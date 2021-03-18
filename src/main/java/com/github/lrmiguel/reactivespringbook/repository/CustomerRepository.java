package com.github.lrmiguel.reactivespringbook.repository;

import com.github.lrmiguel.reactivespringbook.entity.Customer;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

// Everything we need to support our test lives in the ReactiveCrudRepository
public interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {

    // So why do we need the findByEmail? We don't! I just wanted to show you how easy it'd be to
    // define a custom finder method with a custom query and to bind parameters in those finder
    // methods to the query itself. In this case, email is a parameter for the query created behind the
    // scenes.
    @Query("select id, email from customer c where c.email = $1")
    Flux<Customer> findByEmail(String email);

}
