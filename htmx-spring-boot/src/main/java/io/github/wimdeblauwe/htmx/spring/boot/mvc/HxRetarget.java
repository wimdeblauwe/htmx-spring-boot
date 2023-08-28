package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify a CSS selector that updates the target of
 * the content update to a different element on the page.
 *
 * @see <a href="https://htmx.org/reference/#response_headers">HX-Retarget</a>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HxRetarget {

    /**
     * A CSS selector that updates the target of the content update to a different element on the page.
     */
    String value();

}
