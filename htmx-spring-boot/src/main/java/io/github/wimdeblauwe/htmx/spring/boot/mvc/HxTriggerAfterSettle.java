package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to trigger client side events after the 
 * <a href="https://htmx.org/docs/#request-operations">settling step</a>
 * on the target element.
 * <br>
 * You can trigger a single event or as many uniquely named events as you would like.
 *
 * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger Response Headers</a>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HxTriggerAfterSettle {

    /**
     * The events to trigger after the 
     * <a href="https://htmx.org/docs/#request-operations">settling step</a>
     * on the target element.
     */
    String[] value();

}
