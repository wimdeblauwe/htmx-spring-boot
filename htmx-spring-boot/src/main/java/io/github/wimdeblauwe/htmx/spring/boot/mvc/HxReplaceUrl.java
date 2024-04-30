package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

/**
 * Annotation to replace the current URL in the location bar.
 *
 * @see <a href="https://htmx.org/headers/hx-replace-url/">HX-Replace-Url</a>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HxReplaceUrl {

    /**
     * The URL to replace the current URL in the location bar.
     * This may be relative or absolute, as per
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/History/replaceState">history.replaceState()</a>,
     * but must have the same origin as the current URL.
     * Or {@link HtmxValue#FALSE} which prevents the browser’s current URL from being updated.
     */
    String value();

}
