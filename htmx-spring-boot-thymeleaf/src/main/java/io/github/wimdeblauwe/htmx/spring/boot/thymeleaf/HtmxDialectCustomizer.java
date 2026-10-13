package io.github.wimdeblauwe.htmx.spring.boot.thymeleaf;

/**
 * Strategy interface for customizing the {@link HtmxDialect}.
 * Any beans of this type will receive a callback with the {@link HtmxDialect} before the dialect is used.
 *
 * @since 6.0.x
 */
@FunctionalInterface
public interface HtmxDialectCustomizer {

    /**
     * Customize the {@link HtmxDialect}.
     *
     * @param htmxDialect the dialect to customize.
     */
    void customize(HtmxDialect htmxDialect);

}
