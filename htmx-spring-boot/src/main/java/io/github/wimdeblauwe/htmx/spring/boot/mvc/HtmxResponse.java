package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import org.springframework.util.Assert;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A holder for htmx-related response headers that can be used as method argument in controllers.
 *
 * <p>Example usage in an {@code @Controller}:
 * <pre class="code">
 * &#064;GetMapping(value = "/user")
 * public String view(@RequestParam Long id, HtmxResponse htmxResponse) {
 *   htmxResponse.addTrigger("user-viewed");
 *   return "/user/view";
 * }
 * </pre>
 *
 * @author Oliver Drotbohm
 * @author Clint Checketts
 * @author Sascha Woo
 * @see <a href="https://four.htmx.org/reference/headers#response">Response Headers Reference</a>
 */
public final class HtmxResponse {

    private final Set<HtmxTrigger> triggers = new LinkedHashSet<>();
    private String replaceUrl;
    private String reselect;
    private boolean contextRelative = true;
    private String retarget;
    private String pushUrl;
    private HtmxReswap reswap;

    /**
     * Create a new HtmxResponse.
     */
    public HtmxResponse() {
    }

    /**
     * Adds an event that will be triggered once the response is received.
     * <p>Multiple trigger will automatically be merged into the same header.
     *
     * @param eventName the event name
     * @see <a href="https://four.htmx.org/reference/headers/HX-Trigger">HX-Trigger</a>
     */
    public void addTrigger(String eventName) {
        Assert.hasText(eventName, "eventName should not be blank");
        triggers.add(new HtmxTrigger(eventName, null));
    }

    /**
     * Adds an event that will be triggered once the response is received.
     * <p>Multiple trigger were automatically be merged into the same header.
     *
     * @param eventName   the event name
     * @param eventDetail details along with the event
     * @see <a href="https://four.htmx.org/reference/headers/HX-Trigger">HX-Trigger</a>
     * @since 3.6.0
     */
    public void addTrigger(String eventName, Object eventDetail) {
        Assert.hasText(eventName, "eventName should not be blank");
        triggers.add(new HtmxTrigger(eventName, eventDetail));
    }

    /**
     * Prevents the browser history stack from being updated.
     *
     * @see <a href="https://four.htmx.org/reference/headers/HX-Push-Url">HX-Push-Url</a>
     * @see <a href="https://four.htmx.org/reference/headers/HX-Replace-Url">HX-Replace-Url</a>
     * @since 3.6.0
     */
    public void preventHistoryUpdate() {
        this.pushUrl = "false";
        this.replaceUrl = null;
    }

    /**
     * Set whether URLs used in the htmx response that starts with a slash ("/") should be interpreted as
     * relative to the current ServletContext, i.e. as relative to the web application root.
     * Default is "true": A URL that starts with a slash will be interpreted as relative to
     * the web application root, i.e. the context path will be prepended to the URL.
     *
     * @param contextRelative whether to interpret URLs in the htmx response as relative to the current ServletContext
     */
    public void setContextRelative(boolean contextRelative) {
        this.contextRelative = contextRelative;
    }

    /**
     * Pushes a new URL into the history stack of the browser.
     * <p>
     * If you want to prevent the history stack from being updated, use {@link #preventHistoryUpdate()}.
     *
     * @param url the URL to push into the history stack. The URL can be any URL in the same origin as the current URL.
     * @see <a href="https://four.htmx.org/reference/headers/HX-Push-Url">HX-Push-Url</a>
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/History/replaceState">history.pushState()</a>
     * @since 3.6.0
     */
    public void setPushUrl(String url) {
        Assert.hasText(url, "url should not be blank");
        this.pushUrl = url;
        this.replaceUrl = null;
    }

    /**
     * Allows you to replace the most recent entry, i.e. the current URL, in the browser history stack.
     * <p>
     * If you want to prevent the history stack from being updated, use {@link #preventHistoryUpdate()}.
     *
     * @param url the URL to replace in the history stack. The URL can be any URL in the same origin as the current URL.
     * @see <a href="https://four.htmx.org/reference/headers/HX-Replace-Url">HX-Replace-Url</a>
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/History/replaceState">history.replaceState()</a>
     * @since 3.6.0
     */
    public void setReplaceUrl(String url) {
        this.replaceUrl = url;
        this.pushUrl = null;
    }

    /**
     * Set a CSS selector that allows you to choose which part of the response is used to be swapped in.
     * Overrides an existing <a href="https://four.htmx.org/reference/attributes/hx-select/">hx-select</a> on the triggering element.
     *
     * @param cssSelector the CSS selector
     * @see <a href="https://four.htmx.org/reference/headers/HX-Reselect">HX-Reselect</a>
     * @since 3.6.0
     */
    public void setReselect(String cssSelector) {
        Assert.hasText(cssSelector, "cssSelector should not be blank");
        this.reselect = cssSelector;
    }

    /**
     * Allows you to specify how the response will be swapped.
     * See <a href="https://four.htmx.org/reference/attributes/hx-swap/">hx-swap</a> for possible values.
     *
     * @param reswap the reswap options.
     * @see <a href="https://four.htmx.org/reference/headers/HX-Reswap">HX-Reswap</a>
     * @since 3.6.0
     */
    public void setReswap(HtmxReswap reswap) {
        Assert.notNull(reswap, "reswap should not be null");
        this.reswap = reswap;
    }

    /**
     * Set a CSS selector that updates the target of the content update to a different element on the page
     *
     * @param cssSelector the CSS selector
     * @see <a href="https://four.htmx.org/reference/headers/HX-Retarget">HX-Retarget</a>
     * @since 3.6.0
     */
    public void setRetarget(String cssSelector) {
        Assert.hasText(cssSelector, "cssSelector should not be blank");
        this.retarget = cssSelector;
    }

    public String getPushUrl() {
        return pushUrl;
    }

    public String getReplaceUrl() {
        return replaceUrl;
    }

    public String getReselect() {
        return reselect;
    }

    public HtmxReswap getReswap() {
        return reswap;
    }

    public String getRetarget() {
        return retarget;
    }

    public Collection<HtmxTrigger> getTriggers() {
        return this.triggers;
    }

    public boolean isContextRelative() {
        return contextRelative;
    }

}
