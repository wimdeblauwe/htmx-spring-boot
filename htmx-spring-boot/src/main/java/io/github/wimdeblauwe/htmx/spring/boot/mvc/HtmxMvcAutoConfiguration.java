package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.BeanNameViewResolver;

import java.util.List;

@AutoConfiguration
@ConditionalOnWebApplication
public class HtmxMvcAutoConfiguration implements WebMvcRegistrations, WebMvcConfigurer {

    private final ObjectFactory<ViewResolver> viewResolverObjectFactory;
    private final ObjectFactory<LocaleResolver> localeResolverObjectFactory;
    private final ObjectMapper objectMapper;

    HtmxMvcAutoConfiguration(@Qualifier("viewResolver") ObjectFactory<ViewResolver> viewResolverObjectFactory,
                             ObjectFactory<LocaleResolver> localeResolverObjectFactory) {

        Assert.notNull(viewResolverObjectFactory, "viewResolverObjectFactory must not be null!");
        Assert.notNull(localeResolverObjectFactory, "localeResolverObjectFactory must not be null!");

        this.viewResolverObjectFactory = viewResolverObjectFactory;
        this.localeResolverObjectFactory = localeResolverObjectFactory;
        this.objectMapper = JsonMapper.builder().build();
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
        resolvers.add(new HtmxResponseHandlerMethodArgumentResolver());
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {
        handlers.add(new HtmxResponseHandlerMethodReturnValueHandler(viewResolverObjectFactory.getObject(), localeResolverObjectFactory, objectMapper));
        handlers.add(new HtmxViewMethodReturnValueHandler(viewResolverObjectFactory.getObject(), localeResolverObjectFactory.getObject()));
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
