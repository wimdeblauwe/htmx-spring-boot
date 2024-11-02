package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to do a client-side redirect to a new location.
 *
 * @see <a href="https://htmx.org/reference/#response_headers">HX-Redirect</a>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HxRedirect {

    /**
     * The URL to use to do a client-side redirect to a new location.
     */
    String value();
    /**
     * If the URL should be interpreted as context relative if it starts with a slash ("/").
     */
    boolean contextRelative() default true;

}
