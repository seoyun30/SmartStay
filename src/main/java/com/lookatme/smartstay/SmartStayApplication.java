package com.lookatme.smartstay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SmartStayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartStayApplication.class, args);
    }

}
