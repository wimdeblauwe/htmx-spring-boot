package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to replace the current URL in the browser
 * <a href="https://developer.mozilla.org/en-US/docs/Web/API/History_API">location history</a>.
 * <p>
 * The possible values are:
 * <ul>
 * <li>{@link HtmxValue#TRUE}, which replaces the current URL with the fetched URL in the history.</li>
 * <li>{@link HtmxValue#FALSE}, which prevents the browser’s current URL from being updated.</li>
 * <li>A URL to replace the current URL in the location bar. This may be relative or absolute, as per 
 * <a href="https://developer.mozilla.org/en-US/docs/Web/API/History/replaceState">history.replaceState()</a>,
 * but must have the same origin as the current URL.</li>
 * </ul>
 *
 * @see <a href="https://htmx.org/headers/hx-replace-url/">HX-Replace-Url</a>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HxReplaceUrl {

    /**
     * The value for the {@code HX-Replace-Url} response header.
     */
    String value() default HtmxValue.TRUE;
    /**
     * If the URL should be interpreted as context relative if it starts with a slash ("/").
     */
    boolean contextRelative() default true;

}
