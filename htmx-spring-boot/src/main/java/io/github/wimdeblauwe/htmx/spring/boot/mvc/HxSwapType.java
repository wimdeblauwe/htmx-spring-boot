package io.github.wimdeblauwe.htmx.spring.boot.mvc;

/**
 * Represents the swap options.
 *
 * @see <a href="https://four.htmx.org/reference/attributes/hx-swap/">hx-swap</a>
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
     *
     * @deprecated Use {@link #BEFORE} instead.
     */
    @Deprecated
    BEFORE_BEGIN("beforebegin"),
    /**
     * Insert the response before the first child of the target element.
     *
     * @deprecated Use {@link #PREPEND} instead.
     */
    @Deprecated
    AFTER_BEGIN("afterbegin"),
    /**
     * Insert the response after the last child of the target element.
     *
     * @deprecated Use {@link #BEFORE_END} instead.
     */
    @Deprecated
    BEFORE_END("beforeend"),
    /**
     * Insert the response after the target element.
     *
     * @deprecated Use {@link #AFTER} instead.
     */
    @Deprecated
    AFTER_END("afterend"),
    /**
     * Deletes the target element regardless of the response.
     */
    DELETE("delete"),
    /**
     * Does not append the response to the target element (out-of-band elements or partials will still be processed).
     */
    NONE("none"),
    /**
     * Replaces the text content of the element without parsing the response as HTML.
     *
     * @since 6.0.0
     */
    TEXT_CONTENT("textContent"),
    /**
     * Insert the response before the target element.
     *
     * @since 6.0.0
     */
    BEFORE("before"),
    /**
     * Insert the response before the first child of the target element.
     *
     * @since 6.0.0
     */
    PREPEND("prepend"),
    /**
     * Insert the response after the last child of the target element.
     *
     * @since 6.0.0
     */
    APPEND("append"),
    /**
     * Insert the response after the target element.
     *
     * @since 6.0.0
     */
    AFTER("after"),
    /**
     * Morphs the response inside the element, preserving state and focus.
     *
     * @since 6.0.0
     */
    INNER_MORPH("innerMorph"),
    /**
     * Morphs the response with the entire element, preserving state and focus.
     *
     * @since 6.0.0
     */
    OUTER_MORPH("outerMorph"),
    /**
     * Morphs the target’s attributes, then replaces its children.
     *
     * @since 6.0.0
     */
    OUTER_SYNC("outerSync"),
    /**
     * Updates existing elements by ID and inserts new ones.
     * <p>
     * <b>Note:</b>
     * Requires the <a href="https://four.htmx.org/extensions/hx-upsert">hx-upsert</a> extension.
     *
     * @since 6.0.0
     */
    UPSERT("upsert");

    private final String value;

    HxSwapType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
