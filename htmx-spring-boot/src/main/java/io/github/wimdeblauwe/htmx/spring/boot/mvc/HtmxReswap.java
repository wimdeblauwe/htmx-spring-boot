package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import java.time.Duration;
import java.util.Objects;

/**
 * Represents a HX-Reswap response header value.
 *
 * @see <a href="https://htmx.org/attributes/hx-swap/">hx-swap</a>
 * @since 3.1
 */
public class HtmxReswap {

    private final HxSwapType type;
    private Duration swap;
    private Duration settle;
    private Position scroll;
    private String scrollTarget;
    private Position show;
    private String showTarget;
    private boolean transition;
    private Boolean focusScroll;

    /**
     * Insert the response before the first child of the target element.
     */
    public static HtmxReswap afterBegin() {
        return new HtmxReswap(HxSwapType.AFTER_BEGIN);
    }

    /**
     * Insert the response after the target element.
     */
    public static HtmxReswap afterEnd() {
        return new HtmxReswap(HxSwapType.AFTER_END);
    }

    /**
     * Insert the response before the target element.
     */
    public static HtmxReswap beforeBegin() {
        return new HtmxReswap(HxSwapType.BEFORE_BEGIN);
    }

    /**
     * Insert the response after the last child of the target element.
     */
    public static HtmxReswap beforeEnd() {
        return new HtmxReswap(HxSwapType.BEFORE_END);
    }

    /**
     * Deletes the target element regardless of the response.
     */
    public static HtmxReswap delete() {
        return new HtmxReswap(HxSwapType.DELETE);
    }

    /**
     * Replace the inner html of the target element.
     */
    public static HtmxReswap innerHtml() {
        return new HtmxReswap(HxSwapType.INNER_HTML);
    }

    /**
     * Does not append the response to target element (out of band items will still be processed).
     */
    public static HtmxReswap none() {
        return new HtmxReswap(HxSwapType.NONE);
    }

    /**
     * Replace the entire target element with the response.
     */
    public static HtmxReswap outerHtml() {
        return new HtmxReswap(HxSwapType.OUTER_HTML);
    }

    HtmxReswap(HxSwapType type) {
        this.type = type;
    }

    public Boolean getFocusScroll() {
        return focusScroll;
    }

    public Position getScroll() {
        return scroll;
    }

    public String getScrollTarget() {
        return scrollTarget;
    }

    public Duration getSettle() {
        return settle;
    }

    public Position getShow() {
        return show;
    }

    public String getShowTarget() {
        return showTarget;
    }

    public Duration getSwap() {
        return swap;
    }

    public HxSwapType getType() {
        return type;
    }

    public boolean isTransition() {
        return transition;
    }

    /**
     * Returns a string representation for use as an HTTP header value.
     *
     * @return the value
     */
    public String toHeaderValue() {

        var value = new StringBuilder();
        value.append(type.getValue());

        if (transition) {
            value.append(" transition:true");
        }
        if (focusScroll != null) {
            value.append(" focus-scroll:").append(focusScroll);
        }
        if (swap != null) {
            value.append(" swap:").append(swap.toMillis()).append("ms");
        }
        if (settle != null) {
            value.append(" settle:").append(settle.toMillis()).append("ms");
        }
        if (scroll != null) {
            if (scrollTarget != null) {
                value.append(" scroll:").append(scrollTarget).append(":").append(scroll.getValue());
            } else {
                value.append(" scroll:").append(scroll.getValue());
            }
        }
        if (show != null) {
            if (showTarget != null) {
                value.append(" show:").append(showTarget).append(":").append(show.getValue());
            } else {
                value.append(" show:").append(show.getValue());
            }
        }

        return value.toString();
    }

    /**
     * Enable or disable auto-scrolling to focused inputs between requests.
     *
     * @param enabled {@code true} if auto-scrolling to focused inputs between requests is enabled, otherwise {@code false}
     * @return self
     */
    public HtmxReswap focusScroll(boolean enabled) {
        this.focusScroll = enabled;
        return this;
    }

    /**
     * Changes the scrolling behavior of the target element.
     *
     * @param position top or bottom
     * @return self
     */
    public HtmxReswap scroll(Position position) {
        this.scroll = position;
        return this;
    }

    /**
     * Used to target a different element for scrolling.
     *
     * @param cssSelector a CSS selector
     * @return self
     */
    public HtmxReswap scrollTarget(String cssSelector) {
        this.scrollTarget = cssSelector;
        return this;
    }

    /**
     * Set the time that should elapse between the swap and the settle logic.
     *
     * @param duration the time to wait
     * @return self
     */
    public HtmxReswap settle(Duration duration) {
        this.settle = duration;
        return this;
    }

    /**
     * Changes the scrolling behavior of the target element.
     *
     * @param position top or bottom
     * @return self
     */
    public HtmxReswap show(Position position) {
        this.show = position;
        return this;
    }

    /**
     * Used to target a different element for showing.
     *
     * @param cssSelector a CSS selector
     * @return self
     */
    public HtmxReswap showTarget(String cssSelector) {
        this.showTarget = cssSelector;
        return this;
    }

    /**
     * Set the time that should elapse after receiving a response to swap the content.
     *
     * @param duration the time to wait
     * @return self
     */
    public HtmxReswap swap(Duration duration) {
        this.swap = duration;
        return this;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        HtmxReswap that = (HtmxReswap) object;
        return transition == that.transition && Objects.equals(type, that.type) && Objects.equals(swap, that.swap) && Objects.equals(settle, that.settle) && scroll == that.scroll && Objects.equals(scrollTarget, that.scrollTarget) && show == that.show && Objects.equals(
                showTarget, that.showTarget) && Objects.equals(focusScroll, that.focusScroll);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, swap, settle, scroll, scrollTarget, show, showTarget, transition, focusScroll);
    }

    @Override
    public String toString() {
        return toHeaderValue();
    }

    /**
     * Enables the use of the new
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/View_Transitions_API">View Transitions API</a>
     * when a swap occurs.
     *
     * @return self
     */
    public HtmxReswap transition() {
        this.transition = true;
        return this;
    }

    /**
     * Represents the position values used for {@link #scroll} or {@link #show}.
     */
    public enum Position {

        TOP("top"),
        BOTTOM("bottom");

        private final String value;

        Position(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

}
