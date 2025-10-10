package io.github.wimdeblauwe.htmx.spring.boot.jte;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

/**
 * Just created this here to make Spring Boot test slices work
 */
@SpringBootApplication(exclude = SecurityAutoConfiguration.class) // Security is on by default
public class DummyApplication {

    public static void main(String[] args) {
        SpringApplication.run(DummyApplication.class);
    }

}
