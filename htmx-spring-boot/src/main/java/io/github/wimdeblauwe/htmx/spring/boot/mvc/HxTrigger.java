package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to trigger client side actions on the target element within a response to htmx.
 *
 * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger Response Headers</a>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HxTrigger {
    String value();

    HxTriggerLifecycle lifecycle() default HxTriggerLifecycle.RECEIVE;
}
