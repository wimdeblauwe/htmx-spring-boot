package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import org.springframework.core.annotation.AliasFor;

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
     * @see <a href="https://four.htmx.org/reference/headers/HX-Boosted">HX-Boosted</a>
     * @since 3.6.0
     */
    boolean boosted() default true;

    /**
     * Whether the mapping applies also for requests that have been made for history restoration.
     *
     * @see <a href="https://four.htmx.org/reference/headers/HX-History-Restore-Request">HX-History-Restore-Request</a>
     * @since 4.1.0
     */
    boolean historyRestoreRequest() default false;

    /**
     * Restricts the mapping to the {@code id} of a specific target element.
     *
     * @see <a href="https://four.htmx.org/reference/headers/HX-Target">HX-Target</a>
     */
    String target() default "";

    /**
     * Restricts the mapping to the element that triggered the request.
     * <p>
     * Format is {@code tag#id} like {@code button#submit}.
     * Elements without an ID use only the tag name like {@code div} or {@code form}.
     *
     * @see <a href="https://four.htmx.org/reference/headers/HX-Source">HX-Source</a>
     * @since 6.0.0
     */
    String source() default "";

    /**
     * Restricts the mapping to the element that triggered the request.
     * <p>
     * Format is {@code tag#id} like {@code button#submit}.
     * Elements without an ID use only the tag name like {@code div} or {@code form}.
     *
     * @see <a href="https://four.htmx.org/reference/headers/HX-Source">HX-Source</a>
     */
    @AliasFor("source")
    String value() default "";

}
