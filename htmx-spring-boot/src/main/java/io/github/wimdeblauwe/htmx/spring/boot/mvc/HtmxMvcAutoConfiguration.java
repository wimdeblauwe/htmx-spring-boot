package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import java.util.List;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

@AutoConfiguration
@ConditionalOnWebApplication
public class HtmxMvcAutoConfiguration implements WebMvcRegistrations, WebMvcConfigurer {

    private final ObjectMapper objectMapper;

    HtmxMvcAutoConfiguration() {
        this.objectMapper = JsonMapper.builder().build();
    }

    @Bean
    @ConditionalOnMissingBean
    public HtmxHandlerMethodHandler htmxHandlerMethodHandler() {
        return new HtmxHandlerMethodHandler(this.objectMapper);
    }

    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new HtmxRequestMappingHandlerMapping();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HtmxHandlerInterceptor(htmxHandlerMethodHandler()));
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new HtmxHandlerMethodArgumentResolver());
        resolvers.add(new HtmxResponseHandlerMethodArgumentResolver());
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {
        handlers.add(new HtmxViewMethodReturnValueHandler());
    }

    @Override
    public ExceptionHandlerExceptionResolver getExceptionHandlerExceptionResolver() {
        return new HtmxExceptionHandlerExceptionResolver(htmxHandlerMethodHandler());
    }

    @Bean
    @ConditionalOnBean(View.class)
    @ConditionalOnMissingBean
    public HtmxViewResolver htmxViewResolver() {
        HtmxViewResolver resolver = new HtmxViewResolver();
        resolver.setOrder(Ordered.LOWEST_PRECEDENCE - 10);
        return resolver;
    }

}
