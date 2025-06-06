package com.example.borrowingbooks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BorrowingBooksApplication {

    public static void main(String[] args) {
        SpringApplication.run(BorrowingBooksApplication.class, args);
    }

}
