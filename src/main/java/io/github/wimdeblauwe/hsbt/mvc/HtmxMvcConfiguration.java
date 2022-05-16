package io.github.wimdeblauwe.hsbt.mvc;

import java.util.List;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication
public class HtmxMvcConfiguration implements WebMvcRegistrations, WebMvcConfigurer {

    private final ViewResolver resolver;
    private final ObjectFactory<LocaleResolver> locales;

    HtmxMvcConfiguration(ThymeleafViewResolver resolver, ObjectFactory<LocaleResolver> locales) {

      Assert.notNull(resolver, "ViewResovler must not be null!");
      Assert.notNull(locales, "LocaleResolver must not be null!");

      this.resolver = resolver;
      this.locales = locales;
    }

    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new HtmxRequestMappingHandlerMapping();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HtmxHandlerInterceptor());
        registry.addInterceptor(new HtmxViewHandlerInterceptor(resolver, locales));
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new HtmxHandlerMethodArgumentResolver());
    }
}
