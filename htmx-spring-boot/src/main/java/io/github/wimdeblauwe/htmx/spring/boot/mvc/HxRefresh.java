package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that signals htmx that a full page refresh should be performed.
 *
 * @see <a href="https://htmx.org/reference/#response_headers">Response Headers Reference</a>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HxRefresh {

}
