package io.github.wimdeblauwe.htmx.spring.boot.thymeleaf;

import java.util.function.Predicate;

/**
 * Functional interface representing a condition to determine whether
 * a CSRF token should be included based on the value of an attribute.
 *
 * @since 6.0.x
 */
@FunctionalInterface
public interface CsrfTokenCondition extends Predicate<String> {

    /**
     * Evaluates a condition based on the provided attribute value.
     *
     * @param attributeValue the value of the attribute to be evaluated.
     * @return {@code true} if the condition is satisfied based on the input attribute value, {@code false} otherwise.
     */
    @Override
    boolean test(String attributeValue);
}
