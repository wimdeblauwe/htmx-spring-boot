package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify a CSS selector that allows you to choose which part
 * of the response is used to be swapped in.
 *
 * @see <a href="https://htmx.org/reference/#response_headers">HX-Retarget</a>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HxReselect {

    /**
     * A CSS selector that allows you to choose which part of the response is used to be swapped in.
     * <p>Overrides an existing <a href="https://htmx.org/attributes/hx-select/">hx-select</a> on the triggering element.
     */
    String value();

}
