package io.github.wimdeblauwe.htmx.spring.boot.thymeleaf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.json.JsonMapper;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class HtmxThymeleafAutoConfiguration {

    @Bean
    public HtmxDialect htmxDialect() {
        return new HtmxDialect(JsonMapper.builder().build());
    }

}
