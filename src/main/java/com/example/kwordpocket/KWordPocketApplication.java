package com.example.kwordpocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class KWordPocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(KWordPocketApplication.class, args);
    }

}
