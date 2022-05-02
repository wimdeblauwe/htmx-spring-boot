package io.github.wimdeblauwe.hsbt.thymeleaf;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication
public class HtmxThymeleafConfiguration {
    @Bean
    public HtmxDialect htmxDialect() {
        return new HtmxDialect();
    }
}
