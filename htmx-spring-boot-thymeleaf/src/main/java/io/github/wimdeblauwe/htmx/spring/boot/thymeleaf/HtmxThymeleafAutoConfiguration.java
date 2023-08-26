package io.github.wimdeblauwe.htmx.spring.boot.thymeleaf;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnWebApplication
public class HtmxThymeleafAutoConfiguration {
    @Bean
    public HtmxDialect htmxDialect(ObjectMapper mapper) {
        return new HtmxDialect(mapper);
    }
}
