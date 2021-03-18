package com.github.lrmiguel.reactivespringbook.repository;

import com.github.lrmiguel.reactivespringbook.config.ConnectionManager;
import com.github.lrmiguel.reactivespringbook.entity.Customer;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

// @Repository is another Spring stereotype annotation. It's meta-annotated with @Component. It
// is little more than documentation; it's functionally just a @Component.
@Repository
@Log4j2
@RequiredArgsConstructor
public class CustomerRepositoryConnectionManager implements SimpleCustomerRepository {

    // The ConnectionManager is the primary interface through which connections are obtained (and
    // recycled). The ConnectionManager#inConnection method accepts a callback that does work on
    // the Connection. The callback mechanism allows connection pools to work efficiently, as well.
    private final ConnectionManager connectionManager;

    private final BiFunction<Row, RowMetadata, Customer> mapper = (row, rowMetadata) ->
            new Customer(row.get("id", Integer.class), row.get("email", String.class));

    public Mono<Customer> update(Customer customer) {

        // The first method, update, creates a statement, binds the parameter against the positional
        // parameters (a number starting with a dollar sign, $1, $2, and so on), and then execute the
        // statement. Most writes or updates to the database look like this.
        return connectionManager.inConnection(conn -> Flux
                .from(conn.createStatement("update customer set email = $1 where id = $2")
                        .bind("$1", customer.getEmail())
                        .bind("$2", customer.getId())
                        .execute()
                ))
                .then(findById(customer.getId()));
    }

    public Mono<Customer> findById(Integer id) {

        // The following method, findById, queries the database, and when the results arrive, it maps
        // those results using a BiFunction<Row, RowMetadata, Customer>. Most methods that query or
        // read from the database look like this.
        return connectionManager
                .inConnection(conn -> Flux
                        .from(conn.createStatement("select * from customer where id = $1")
                                .bind("$1", id)
                                .execute()))
                .flatMap(result -> result.map(this.mapper))
                .single()
                .log();
    }

    public Mono<Void> deleteById(Integer id) {
        return connectionManager.inConnection(conn -> Flux
                .from(conn.createStatement("delete from customer where id = $1")
                        .bind("$1", id)
                        .execute())
        )
                .then();
    }

    @Override
    public Flux<Customer> findAll() {
        return connectionManager.inConnection(conn -> Flux
                .from(conn.createStatement("select * from customer").execute()))
                .flatMap(result -> result.map(mapper));
    }

    @Override
    public Mono<Customer> save(Customer c) {
        return connectionManager
                .inConnection(conn -> Flux
                        .from(conn.createStatement("insert into customer(email) values($1)")
                                .bind("$1", c.getEmail())
                                .returnGeneratedValues("id")
                                .execute())
                        .flatMap(r -> r.map((row, rowMetadata) -> {
                            var id = row.get("id", Integer.class);
                            return new Customer(id, c.getEmail());
                        })))
                .single()
                .log();
    }
}
