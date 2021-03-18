package com.github.lrmiguel.reactivespringbook.repository;

import com.github.lrmiguel.reactivespringbook.entity.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Log4j2
public class CustomerRepositoryDatabaseClient implements SimpleCustomerRepository {

    private final DatabaseClient databaseClient;

    // The first method returns all the Customer records from the connected database table called
    // customers.
    @Override
    public Flux<Customer> findAll() {
        return databaseClient.sql("select c from Customer")
                .fetch()
                .all()
                .cast(Customer.class);
    }

    // The save method is one method I'd wished we'd had in the JdbcTemplate. It takes a POJO, maps
    // the fields from that POJO to column names, then takes those values and uses them in an insert
    // statement, the result of which we map to Customer objects for return from the method. I
    // don't see how this could be cleaner! You end up doing a fair bit of work to achieve the same
    // effect with one of the longer variants of the JdbcTemplate's execute(...) methods. Is this an
    // ORM? No. However, it's remarkable what you can get done with a smidge of the ever-so convenient
    // convention.
    @Override
    public Mono<Customer> save(Customer c) {
        return this.databaseClient.sql("insert into customer(email) values($1)")
                .bind("$1", c.getEmail())
                .map((row, rowMetadata) -> new Customer(row.get("id", Integer.class),
                        c.getEmail()))
                .first();
    }

   /* @Override
    public Mono<Customer> update(Customer c) {
        return databaseClient.update().table(Customer.class).using(c).fetch()
                .rowsUpdated().filter(countOfUpdates -> countOfUpdates > 0)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("couldn't update " + c.toString())))
                .thenMany(findById(c.getId())).single();
    }

    @Override
    public Mono<Customer> findById(Integer id) {
        return this.databaseClient.execute("select * from customer where id = $1")
                .bind("$1", id)
                .fetch()
                .first()
                .map(map -> new Customer(Integer.class.cast(map.get("id")), String.class.cast(map.get("email"))));
    }

    @Override
    public Mono<Void> deleteById(Integer id) {
        return this.databaseClient.execute("delete from customer where id = $1")
                .bind("$1", id)
                .then();
    }*/
}
