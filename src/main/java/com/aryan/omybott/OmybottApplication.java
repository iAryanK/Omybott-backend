package com.aryan.omybott;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class OmybottApplication {

    public static void main(String[] args) {
        SpringApplication.run(OmybottApplication.class, args);
    }

}
