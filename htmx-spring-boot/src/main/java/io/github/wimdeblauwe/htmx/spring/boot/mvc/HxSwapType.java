package io.github.wimdeblauwe.htmx.spring.boot.mvc;

/**
 * Represents the swap options.
 *
 * @see <a href="https://htmx.org/attributes/hx-swap/">hx-swap</a>
 */
public enum HxSwapType {

    /**
     * Use the default swap behavior as configured by {@code htmx.config.defaultSwapStyle}
     * or {@code innerHTML} for boosted requests.
     */
    DEFAULT(""),
    /**
     * Replace the inner html of the target element.
     */
    INNER_HTML("innerHTML"),
    /**
     * Replace the entire target element with the response.
     */
    OUTER_HTML("outerHTML"),
    /**
     * Insert the response before the target element.
     */
    BEFORE_BEGIN("beforebegin"),
    /**
     * Insert the response before the first child of the target element.
     */
    AFTER_BEGIN("afterbegin"),
    /**
     * Insert the response after the last child of the target element.
     */
    BEFORE_END("beforeend"),
    /**
     * Insert the response after the target element.
     */
    AFTER_END("afterend"),
    /**
     * Deletes the target element regardless of the response.
     */
    DELETE("delete"),
    /**
     * Does not append the response to target element (out of band items will still be processed).
     */
    NONE("none");


    private final String value;

    HxSwapType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
