package io.github.wimdeblauwe.hsbt.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HxTrigger {
    String value();

    HxTriggerPolicy policy() default HxTriggerPolicy.RECEIVE;
}
