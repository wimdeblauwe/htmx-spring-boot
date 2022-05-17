package io.github.wimdeblauwe.hsbt.mvc;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HxPartials {

    String value() default "htmxPartials";

    @AliasFor("value")
    String attributeName() default "htmxPartials";

    String[] templates() default {};
}
