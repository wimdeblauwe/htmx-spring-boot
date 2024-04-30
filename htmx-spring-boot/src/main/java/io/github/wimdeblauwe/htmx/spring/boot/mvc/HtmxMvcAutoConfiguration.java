package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.util.Assert;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;

@AutoConfiguration
@ConditionalOnWebApplication
public class HtmxMvcAutoConfiguration implements WebMvcRegistrations, WebMvcConfigurer {

    private final ObjectFactory<ViewResolver> resolver;
    private final ObjectFactory<LocaleResolver> locales;
    private final ObjectMapper objectMapper;

    HtmxMvcAutoConfiguration(@Qualifier("viewResolver") ObjectFactory<ViewResolver> resolver,
                             ObjectFactory<LocaleResolver> locales,
                             ObjectMapper objectMapper) {
        Assert.notNull(resolver, "ViewResolver must not be null!");
        Assert.notNull(locales, "LocaleResolver must not be null!");

        this.resolver = resolver;
        this.locales = locales;
        this.objectMapper = objectMapper;
    }

    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new HtmxRequestMappingHandlerMapping();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HtmxHandlerInterceptor(objectMapper));
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new HtmxHandlerMethodArgumentResolver());
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {
        handlers.add(new HtmxResponseHandlerMethodReturnValueHandler(resolver.getObject(), locales, objectMapper));
    }
}
