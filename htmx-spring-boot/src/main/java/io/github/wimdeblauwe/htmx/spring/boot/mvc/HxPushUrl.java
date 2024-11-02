package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to push a URL into the browser
 * <a href="https://developer.mozilla.org/en-US/docs/Web/API/History_API">location history</a>.
 * <p>
 * The possible values are:
 * <ul>
 * <li>{@link HtmxValue#TRUE}, which pushes the fetched URL into history.</li>
 * <li>{@link HtmxValue#FALSE}, which disables pushing the fetched URL if it would otherwise be pushed.</li>
 * <li>A URL to be pushed into the location bar. This may be relative or absolute,
 * as per <a href="https://developer.mozilla.org/en-US/docs/Web/API/History/pushState">history.pushState()</a>.</li>
 * </ul>
 *
 * @see <a href="https://htmx.org/headers/hx-push-url/">HX-Push-Url Response Header</a>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HxPushUrl {

    /**
     * The value for the {@code HX-Push-Url} response header.
     */
    String value() default HtmxValue.TRUE;
    /**
     * If the URL should be interpreted as context relative if it starts with a slash ("/").
     */
    boolean contextRelative() default true;

}
