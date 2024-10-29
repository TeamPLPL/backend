package com.kosa.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class backendApplication {

    public static void main(String[] args) {
        SpringApplication.run(backendApplication.class, args);
        System.out.println("       /\\_/\\  ");
        System.out.println("      ( o.o )  ");
        System.out.println("       > ^ <  ");
        System.out.println("      /\\_^_/\\ ");
        System.out.println("SERVER START...");
    }

}
