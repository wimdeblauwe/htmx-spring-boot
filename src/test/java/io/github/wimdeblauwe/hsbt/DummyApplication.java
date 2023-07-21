package io.github.wimdeblauwe.hsbt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;

import io.github.wimdeblauwe.hsbt.mvc.PartialsController.TodoRepository;

/**
 * Just created this here to make Spring Boot test slices work
 */
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class}) // Security is on by default
public class DummyApplication {

    public static void main(String[] args) {
        SpringApplication.run(DummyApplication.class);
    }

    @Bean
    TodoRepository repository() {
        return () -> 2;
    }
}
