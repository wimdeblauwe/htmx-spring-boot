package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify how the response will be swapped.
 * See <a href="https://four.htmx.org/reference/attributes/hx-swap">hx-swap</a> for possible values.
 *
 * @see <a href="https://four.htmx.org/reference/headers/HX-Reswap">HX-Reswap</a>
 * @see <a href="https://four.htmx.org/reference/attributes/hx-swap">hx-swap</a>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HxReswap {

    /**
     * A value to specify how the response will be swapped. The default value is {@link HxSwapType#DEFAULT}
     * which uses the default swap behavior as configured by {@code htmx.config.defaultSwapStyle}
     *
     * @see <a href="https://four.htmx.org/reference/attributes/hx-swap">hx-swap</a>
     */
    HxSwapType value() default HxSwapType.DEFAULT;

    /**
     * Enables the use of the new
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/View_Transitions_API">View Transitions API</a>
     * when a swap occurs.
     */
    boolean transition() default false;

    /**
     * Set the time in milliseconds that should elapse after receiving a response to swap the content.
     */
    long swap() default -1;

    /**
     * Set the time in milliseconds that should elapse between the swap and the settle logic.
     */
    long settle() default -1;

    /**
     * Prevents updating the page {@code <title>}.
     *
     * @see <a href="https://four.htmx.org/reference/attributes/hx-swap#ignoretitle">hx-swap#ignoreTitle</a>
     * @since 6.0.0
     */
    boolean ignoreTitle() default false;

    /**
     * Changes the scrolling behavior of the target element.
     */
    Position scroll() default Position.UNDEFINED;

    /**
     * Used to target a different element for scrolling.
     */
    String scrollTarget() default "";

    /**
     * Changes the scrolling behavior of the target element.
     */
    Position show() default Position.UNDEFINED;

    /**
     * Used to target a different element for showing.
     */
    String showTarget() default "";

    /**
     * Enable or disable auto-scrolling to focused inputs between requests.
     */
    FocusScroll focusScroll() default FocusScroll.UNDEFINED;

    /**
     * Sets the swap target.
     *
     * @see <a href="https://four.htmx.org/reference/attributes/hx-swap#target">hx-swap#target</a>
     * @since 6.0.0
     */
    String target() default "";

    /**
     * Controls whether the response’s outer element is removed.
     *
     * @see <a href="https://four.htmx.org/reference/attributes/hx-swap#strip">hx-swap#strip</a>
     * @since 6.0.0
     */
    boolean strip() default false;

    /**
     * Controls the main target when no main content remains.
     *
     * @see <a href="https://four.htmx.org/reference/attributes/hx-swap#swapEmpty">hx-swap#swapEmpty</a>
     * @since 6.0.0
     */
    boolean swapEmpty() default false;

    /**
     * Represents the values for {@link #focusScroll()}
     */
    enum FocusScroll {
        TRUE,
        FALSE,
        UNDEFINED
    }

    /**
     * Represents the position values for {@link #show()} and {@link #scroll()}
     */
    enum Position {
        NONE,
        TOP,
        BOTTOM,
        UNDEFINED
    }

}
