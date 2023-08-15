package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@HxTrigger("")
public @interface HxTriggerWithAliasFor {
    @AliasFor(annotation = HxTrigger.class, attribute = "value")
    String event();
}
