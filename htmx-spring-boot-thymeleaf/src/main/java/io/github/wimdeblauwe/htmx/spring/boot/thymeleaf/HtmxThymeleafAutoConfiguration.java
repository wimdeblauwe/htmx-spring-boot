package io.github.wimdeblauwe.htmx.spring.boot.thymeleaf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnWebApplication
public class HtmxThymeleafAutoConfiguration {

    @Bean
    public HtmxDialect htmxDialect() {
        return new HtmxDialect(JsonMapper.builder().build());
    }

}
