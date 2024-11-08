package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for mapping htmx requests onto specific handler method.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HxRequest {

    /**
     * Whether the mapping applies also for requests that have been boosted.
     * Defaults to {@code true}.
     *
     * @see <a href="https://htmx.org/reference/#request_headers">HX-Boosted</a>
     * @since 3.6.0
     */
    boolean boosted() default true;

    /**
     * Restricts the mapping to the {@code id} of a specific target element.
     *
     * @see <a href="https://htmx.org/reference/#request_headers">HX-Target</a>
     */
    String target() default "";

    /**
     * Restricts the mapping to the {@code id} of a specific triggered element.
     *
     * @see <a href="https://htmx.org/reference/#request_headers">HX-Trigger</a>
     */
    String triggerId() default "";

    /**
     * Restricts the mapping to the {@code name} of a specific triggered element.
     *
     * @see <a href="https://htmx.org/reference/#request_headers">HX-Trigger-Name</a>
     */
    String triggerName() default "";

    /**
     * Restricts the mapping to the {@code id}, if any, or to the {@code name} of a specific triggered element.
     * <p>
     * If you want to be explicit use {@link #triggerId()} or {@link #triggerName()}.
     *
     * @see <a href="https://htmx.org/reference/#request_headers">HX-Trigger</a>
     * @see <a href="https://htmx.org/reference/#request_headers">HX-Trigger-Name</a>
     */
    String value() default "";

}
