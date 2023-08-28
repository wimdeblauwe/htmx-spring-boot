package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to push a new url into the history stack.
 *
 * @see <a href="https://htmx.org/headers/hx-push-url/">HX-Push-Url Response Header</a>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HxPushUrl {

    /**
     * The URL to be pushed into the location bar. This may be relative or absolute,
     * as per <a href="https://developer.mozilla.org/en-US/docs/Web/API/History/pushState">history.pushState()</a>.
     * Or {@link HtmxValue#FALSE}, which prevents the browser’s history from being updated.
     */
    String value();

}
