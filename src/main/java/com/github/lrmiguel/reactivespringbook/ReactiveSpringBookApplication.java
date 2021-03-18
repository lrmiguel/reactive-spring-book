package com.github.lrmiguel.reactivespringbook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import reactor.tools.agent.ReactorDebugAgent;

@SpringBootApplication
@EnableTransactionManagement
@EnableR2dbcRepositories
public class ReactiveSpringBookApplication {

    public static void main(String[] args) {
        ReactorDebugAgent.init();
        SpringApplication.run(ReactiveSpringBookApplication.class, args);
    }

}
