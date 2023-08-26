package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This shows how a custom annotation can be created with the name of the event
 * already coded in.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@HxTrigger("updatesSidebar")
public @interface HxUpdatesSidebar {
}
