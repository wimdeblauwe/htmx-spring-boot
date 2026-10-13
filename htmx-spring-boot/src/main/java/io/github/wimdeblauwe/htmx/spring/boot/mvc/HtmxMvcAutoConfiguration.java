package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.webmvc.autoconfigure.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import tools.jackson.core.json.JsonWriteFeature;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class HtmxMvcAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "htmxJsonMapper")
    JsonMapper htmxJsonMapper() {
        return JsonMapper.builder()
                         // header values must be ASCII, so escape non-ASCII characters in the JSON written to them
                         .enable(JsonWriteFeature.ESCAPE_NON_ASCII)
                         .build();
    }

    @Bean
    @ConditionalOnMissingBean
    HtmxHandlerMethodHandler htmxHandlerMethodHandler(@Qualifier("htmxJsonMapper") JsonMapper jsonMapper) {
        return new HtmxHandlerMethodHandler(jsonMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    HtmxResponseBodyAdvice htmxResponseBodyAdvice(HtmxHandlerMethodHandler handlerMethodHandler) {
        return new HtmxResponseBodyAdvice(handlerMethodHandler);
    }

    @Bean
    @ConditionalOnBean(View.class)
    @ConditionalOnMissingBean
    HtmxViewResolver htmxViewResolver() {
        HtmxViewResolver resolver = new HtmxViewResolver();
        resolver.setOrder(Ordered.LOWEST_PRECEDENCE - 10);
        return resolver;
    }

    @Configuration(proxyBeanMethods = false)
    static class WebConfig implements WebMvcRegistrations, WebMvcConfigurer {

        final HtmxHandlerMethodHandler htmxHandlerMethodHandler;
        final HtmxResponseBodyAdvice htmxResponseBodyAdvice;

        WebConfig(HtmxHandlerMethodHandler htmxHandlerMethodHandler,
                  HtmxResponseBodyAdvice htmxResponseBodyAdvice) {

            this.htmxHandlerMethodHandler = htmxHandlerMethodHandler;
            this.htmxResponseBodyAdvice = htmxResponseBodyAdvice;
        }

        @Override
        public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
            return new HtmxRequestMappingHandlerMapping();
        }

        @Override
        public RequestMappingHandlerAdapter getRequestMappingHandlerAdapter() {
            var adapter = new RequestMappingHandlerAdapter();
            adapter.setResponseBodyAdvice(List.of(htmxResponseBodyAdvice));
            return adapter;
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(new HtmxHandlerInterceptor(htmxHandlerMethodHandler));
        }

        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
            resolvers.add(new HtmxHandlerMethodArgumentResolver());
            resolvers.add(new HtmxResponseHandlerMethodArgumentResolver());
        }

        @Override
        public ExceptionHandlerExceptionResolver getExceptionHandlerExceptionResolver() {
            var resolver = new HtmxExceptionHandlerExceptionResolver(htmxHandlerMethodHandler);
            resolver.setResponseBodyAdvice(List.of(htmxResponseBodyAdvice));
            return resolver;
        }
    }
}
