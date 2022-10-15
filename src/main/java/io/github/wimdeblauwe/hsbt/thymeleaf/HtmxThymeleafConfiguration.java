package io.github.wimdeblauwe.hsbt.thymeleaf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnWebApplication
public class HtmxThymeleafConfiguration {
    @Bean
    public HtmxDialect htmxDialect() {
        return new HtmxDialect();
    }
}
