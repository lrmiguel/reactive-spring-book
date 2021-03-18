package com.github.lrmiguel.reactivespringbook.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    @Id
    private Integer id;

    private String email;

    Customer(String email) {
        this.email = email;
    }
}
