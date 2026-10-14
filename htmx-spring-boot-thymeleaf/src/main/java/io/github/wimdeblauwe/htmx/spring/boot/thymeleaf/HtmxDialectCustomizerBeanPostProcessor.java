package io.github.wimdeblauwe.htmx.spring.boot.thymeleaf;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Bean post processor that applies {@link HtmxDialectCustomizer} beans to {@link HtmxDialect} beans.
 *
 * @since 6.0.x
 */
public class HtmxDialectCustomizerBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware {

    @SuppressWarnings("NullAway.Init")
    private ListableBeanFactory beanFactory;
    private @Nullable List<HtmxDialectCustomizer> customizers;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        Assert.isInstanceOf(ListableBeanFactory.class, beanFactory, "'beanFactory' must be a ListableBeanFactory");
        this.beanFactory = (ListableBeanFactory) beanFactory;
    }

    @Override
    public @Nullable Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof HtmxDialect htmxDialect) {
            postProcessBeforeInitialization(htmxDialect);
        }
        return bean;
    }

    @Override
    public @Nullable Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    private Collection<HtmxDialectCustomizer> getCustomizers() {

        if (this.customizers == null) {
            // Look up does not include the parent context
            this.customizers = new ArrayList<>(getCustomizerBeans());
            this.customizers.sort(AnnotationAwareOrderComparator.INSTANCE);
            this.customizers = Collections.unmodifiableList(this.customizers);
        }

        return this.customizers;
    }

    private Collection<HtmxDialectCustomizer> getCustomizerBeans() {
        return this.beanFactory.getBeansOfType(HtmxDialectCustomizer.class, false, false).values();
    }

    private void postProcessBeforeInitialization(HtmxDialect htmxDialect) {
        for (HtmxDialectCustomizer customizer : getCustomizers()) {
            customizer.customize(htmxDialect);
        }
    }
}
