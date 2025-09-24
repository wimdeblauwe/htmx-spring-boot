package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.webmvc.autoconfigure.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@AutoConfiguration
@ConditionalOnWebApplication
public class HtmxMvcAutoConfiguration implements WebMvcRegistrations, WebMvcConfigurer {

    private final HtmxHandlerMethodHandler handlerMethodHandler;

    HtmxMvcAutoConfiguration() {
        JsonMapper jsonMapper = JsonMapper.builder().build();
        this.handlerMethodHandler = new HtmxHandlerMethodHandler(jsonMapper);
    }

    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new HtmxRequestMappingHandlerMapping();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HtmxHandlerInterceptor(handlerMethodHandler));
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new HtmxHandlerMethodArgumentResolver());
        resolvers.add(new HtmxResponseHandlerMethodArgumentResolver());
    }

    @Override
    public ExceptionHandlerExceptionResolver getExceptionHandlerExceptionResolver() {
        return new HtmxExceptionHandlerExceptionResolver(handlerMethodHandler);
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
