package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

/**
 * Annotation to do a client side redirect that does not do a full page reload.
 * <p>
 * Note that this annotation does not provide support for specifying {@code values} or {@code headers}.
 * If you want to do this, use {@link HtmxResponse} instead.
 *
 * @see <a href="https://htmx.org/headers/hx-location/">HX-Location Response Header</a>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HxLocation {

    /**
     * The url path to make the redirect.
     * <p>This is an alias for {@link #path}. For example,
     * {@code @HxLocation("/foo")} is equivalent to
     * {@code @HxLocation(path="/foo")}.
     */
    @AliasFor("path")
    String value() default "";
    /**
     * The url path to make the redirect.
     */
    String path() default "";
    /**
     * The source element of the request
     */
    String source() default "";
    /**
     * An event that "triggered" the request
     */
    String event() default "";
    /**
     * A callback that will handle the response HTML.
     */
    String handler() default "";
    /**
     * The target to swap the response into.
     */
    String target() default "";
    /**
     * How the response will be swapped in relative to the target
     */
    String swap() default "";
    /**
     * A CSS selector to select the content you want swapped from a response.
     */
    String select() default "";
    /**
     * If the path should be interpreted as context relative if it starts with a slash ("/").
     */
    boolean contextRelative() default true;

}
