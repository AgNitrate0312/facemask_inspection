package org.example.sensenebula;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SensenebulaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SensenebulaApplication.class, args);
    }

}
