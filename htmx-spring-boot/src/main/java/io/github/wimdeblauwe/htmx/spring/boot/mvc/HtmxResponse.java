package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import java.util.*;

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
 * @see <a href="https://htmx.org/reference/#response_headers">Response Headers Reference</a>
 */
public final class HtmxResponse {

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmxResponse.class);

    private Set<ModelAndView> views = new LinkedHashSet<>();
    private Set<HtmxTrigger> triggers = new LinkedHashSet<>();
    private Set<HtmxTrigger> triggersAfterSettle = new LinkedHashSet<>();
    private Set<HtmxTrigger> triggersAfterSwap = new LinkedHashSet<>();
    private String replaceUrl;
    private String reselect;
    private boolean contextRelative = true;
    private String retarget;
    private boolean refresh;
    private String redirect;
    private String pushUrl;
    private HtmxReswap reswap;
    private HtmxLocation location;

    /**
     * Return a builder to build a {@link HtmxResponse}.
     *
     * @return the builder
     * @deprecated use {@link HtmxResponse} as handler method argument and {@link HtmxView},
     * {@link HtmxRedirectView} or {@link HtmxLocationRedirectView} as handler method return
     * type instead.  Will be removed in 4.0.
     */
    @Deprecated
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Create a new HtmxResponse.
     */
    public HtmxResponse() {
    }

    /**
     * Adds an event that will be triggered once the response is received.
     * <p>Multiple trigger were automatically be merged into the same header.
     *
     * @param eventName the event name
     * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger Response Headers</a>
     * @deprecated Return value is changed to void in 4.0.
     */
    @Deprecated
    public HtmxResponse addTrigger(String eventName) {
        Assert.hasText(eventName, "eventName should not be blank");
        triggers.add(new HtmxTrigger(eventName, null));
        return this;
    }

    /**
     * Adds an event that will be triggered once the response is received.
     * <p>Multiple trigger were automatically be merged into the same header.
     *
     * @param eventName   the event name
     * @param eventDetail details along with the event
     * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger Response Headers</a>
     * @since 3.6.0
     */
    public void addTrigger(String eventName, Object eventDetail) {
        Assert.hasText(eventName, "eventName should not be blank");
        triggers.add(new HtmxTrigger(eventName, eventDetail));
    }

    /**
     * Adds an event that will be triggered after the <a href="https://htmx.org/docs/#request-operations">settling step</a>.
     * <p>Multiple triggers were automatically be merged into the same header.
     *
     * @param eventName the event name
     * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger Response Headers</a>
     * @since 3.6.0
     */
    public void addTriggerAfterSettle(String eventName) {
        Assert.hasText(eventName, "eventName should not be blank");
        triggersAfterSettle.add(new HtmxTrigger(eventName, null));
    }

    /**
     * Adds an event that will be triggered after the <a href="https://htmx.org/docs/#request-operations">settling step</a>.
     * <p>Multiple triggers were automatically be merged into the same header.
     *
     * @param eventName   the event name
     * @param eventDetail details along with the event
     * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger Response Headers</a>
     * @since 3.6.0
     */
    public void addTriggerAfterSettle(String eventName, Object eventDetail) {
        Assert.hasText(eventName, "eventName should not be blank");
        triggersAfterSettle.add(new HtmxTrigger(eventName, eventDetail));
    }

    /**
     * Adds an event that will be triggered after the <a href="https://htmx.org/docs/#request-operations">swap step</a>.
     * <p>Multiple triggers were automatically be merged into the same header.
     *
     * @param eventName the event name
     * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger Response Headers</a>
     * @since 3.6.0
     */
    public void addTriggerAfterSwap(String eventName) {
        Assert.hasText(eventName, "eventName should not be blank");
        triggersAfterSwap.add(new HtmxTrigger(eventName, null));
    }

    /**
     * Adds an event that will be triggered after the <a href="https://htmx.org/docs/#request-operations">swap step</a>.
     * <p>Multiple triggers were automatically be merged into the same header.
     *
     * @param eventName   the event name
     * @param eventDetail details along with the event
     * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger Response Headers</a>
     * @since 3.6.0
     */
    public void addTriggerAfterSwap(String eventName, Object eventDetail) {
        Assert.hasText(eventName, "eventName should not be blank");
        triggersAfterSwap.add(new HtmxTrigger(eventName, eventDetail));
    }

    /**
     * Prevents the browser history stack from being updated.
     *
     * @see <a href="https://htmx.org/headers/hx-push-url/">HX-Push-Url Response Header</a> documentation
     * @see <a href="https://htmx.org/headers/hx-replace-url/">HX-Replace-Url Response Header</a>
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
     * @see <a href="https://htmx.org/headers/hx-push/">HX-Push Response Header</a> documentation
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
     * @see <a href="https://htmx.org/headers/hx-replace-url/">HX-Replace-Url Response Header</a>
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/History/replaceState">history.replaceState()</a>
     * @since 3.6.0
     */
    public void setReplaceUrl(String url) {
        this.replaceUrl = url;
        this.pushUrl = null;
    }

    /**
     * Set a CSS selector that allows you to choose which part of the response is used to be swapped in.
     * Overrides an existing <a href="https://htmx.org/attributes/hx-select/">hx-select</a> on the triggering element.
     *
     * @param cssSelector the CSS selector
     * @see <a href="https://htmx.org/reference/#response_headers">HX-Reselect</a>
     * @since 3.6.0
     */
    public void setReselect(String cssSelector) {
        Assert.hasText(cssSelector, "cssSelector should not be blank");
        this.reselect = cssSelector;
    }

    /**
     * Allows you to specify how the response will be swapped.
     * See <a href="https://htmx.org/attributes/hx-swap/">hx-swap</a> for possible values.
     *
     * @param reswap the reswap options.
     * @see <a href="https://htmx.org/reference/#response_headers">HX-Reswap</a>
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
     * @see <a href="https://htmx.org/reference/#response_headers">HX-Retarget</a>
     * @since 3.6.0
     */
    public void setRetarget(String cssSelector) {
        Assert.hasText(cssSelector, "cssSelector should not be blank");
        this.retarget = cssSelector;
    }

    /**
     * Append the rendered template or fragment.
     *
     * @param template must not be {@literal null} or empty.
     * @return same HtmxResponse for chaining
     * @deprecated use {@link Builder#view(String)} instead.  Will be removed in 4.0.
     */
    @Deprecated
    public HtmxResponse addTemplate(String template) {
        Assert.hasText(template, "template should not be blank");
        if (!views.stream().anyMatch(mav -> template.equals(mav.getViewName()))) {
            views.add(new ModelAndView(template));
        }
        return this;
    }

    /**
     * Append the rendered template or fragment as a resolved {@link View}.
     *
     * @param template must not be {@literal null}.
     * @return same HtmxResponse for chaining
     * @deprecated use {@link Builder#view(View)} instead.  Will be removed in 4.0.
     */
    @Deprecated
    public HtmxResponse addTemplate(View template) {
        Assert.notNull(template, "template should not be null");
        if (!views.stream().anyMatch(mav -> template.equals(mav.getView()))) {
            views.add(new ModelAndView(template));
        }
        return this;
    }

    /**
     * Append the rendered template or fragment as a {@link ModelAndView}.
     *
     * @param template must not be {@literal null}.
     * @return same HtmxResponse for chaining
     * @deprecated use {@link Builder#view(ModelAndView)} instead.  Will be removed in 4.0.
     */
    @Deprecated
    public HtmxResponse addTemplate(ModelAndView template) {
        Assert.notNull(template, "template should not be null");
        views.add(template);
        return this;
    }

    /**
     * Set a HX-Trigger (or HX-Trigger-After-Settle or HX-Trigger-After-Swap headers.
     * Multiple trigger were
     * automatically be merged into the same header.
     *
     * @param eventName   must not be {@literal null} or empty.
     * @param eventDetail can be {@literal null}.
     * @param step        must not be {@literal null} or empty.
     * @return same HtmxResponse for chaining
     * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger Response Headers</a>
     * @deprecated use {@link Builder#trigger(String, Object)} instead.  Will be removed in 4.0.
     */
    @Deprecated
    public HtmxResponse addTrigger(String eventName, String eventDetail, HxTriggerLifecycle step) {
        Assert.hasText(eventName, "eventName should not be blank");
        switch (step) {
            case RECEIVE:
                triggers.add(new HtmxTrigger(eventName, eventDetail));
                break;
            case SETTLE:
                triggersAfterSettle.add(new HtmxTrigger(eventName, eventDetail));
                break;
            case SWAP:
                triggersAfterSwap.add(new HtmxTrigger(eventName, eventDetail));
                break;
            default:
                throw new IllegalArgumentException("Unknown step " + step);
        }
        return this;
    }

    /**
     * Pushes a new url into the history stack
     *
     * @param url must not be {@literal null} or empty. {@literal false} prevents the browser history from being updated
     * @return same HtmxResponse for chaining
     * @see <a href="https://htmx.org/headers/hx-push/">HX-Push Response Header</a> documentation
     * @deprecated use {@link Builder#pushUrl(String)} instead.  Will be removed in 4.0.
     */
    @Deprecated
    public HtmxResponse pushHistory(String url) {
        Assert.hasText(url, "url should not be blank");
        this.pushUrl = url;
        return this;
    }

    /**
     * Can be used to do a client-side redirect to a new location
     *
     * @param url can be a relative or an absolute url
     * @return same HtmxResponse for chaining
     * @deprecated use {@link Builder#redirect(String)} instead.  Will be removed in 4.0.
     */
    @Deprecated
    public HtmxResponse browserRedirect(String url) {
        Assert.hasText(url, "url should not be blank");
        this.redirect = url;
        return this;
    }

    /**
     * If set to "true" the client side will do a full refresh of the page
     *
     * @param refresh boolean to indicate full refresh or not.
     * @return same HtmxResponse for chaining
     * @deprecated use {@link Builder#refresh()} instead.  Will be removed in 4.0.
     */
    @Deprecated
    public HtmxResponse browserRefresh(boolean refresh) {
        this.refresh = refresh;
        return this;
    }

    /**
     * Set a CSS selector that updates the target of the content update to a different element on the page
     *
     * @param cssSelector must not be {@literal null} or empty.
     * @return same HtmxResponse for chaining
     * @deprecated use {@link Builder#retarget(String)} instead.  Will be removed in 4.0.
     */
    @Deprecated
    public HtmxResponse retarget(String cssSelector) {
        Assert.hasText(cssSelector, "cssSelector should not be blank");
        this.retarget = cssSelector;
        return this;
    }

    /**
     * Set a new swap to specify how the response will be swapped
     *
     * @param swapType must not be {@literal null}.
     * @return same HtmxResponse for chaining
     * @deprecated use {@link Builder#reswap(HxSwapType)} instead.  Will be removed in 4.0.
     */
    @Deprecated
    public HtmxResponse reswap(HxSwapType swapType) {
        Assert.notNull(swapType, "swapType should not be null");
        this.reswap = new HtmxReswap(swapType);
        return this;
    }

    /**
     * @param otherResponse Another HtmxResponse that will be merged into this response.
     * @return this for chaining
     * @deprecated use {@link Builder#and(HtmxResponse)} instead.  Will be removed in 4.0.
     */
    @Deprecated
    public HtmxResponse and(HtmxResponse otherResponse) {
        otherResponse.views.forEach(otherTemplate -> {
            if (this.views.stream().anyMatch(mav -> Builder.same(otherTemplate, mav))) {
                LOGGER.warn("Duplicate template '{}' found while merging HtmxResponse", otherTemplate);
            } else {
                views.add(otherTemplate);
            }
        });
        Builder.mergeTriggers(this.triggers, otherResponse.triggers);
        Builder.mergeTriggers(this.triggersAfterSettle, otherResponse.triggersAfterSettle);
        Builder.mergeTriggers(this.triggersAfterSwap, otherResponse.triggersAfterSwap);

        if (otherResponse.getPushUrl() != null) {
            this.pushUrl = otherResponse.getPushUrl();
        }
        if (otherResponse.getRedirect() != null) {
            this.redirect = otherResponse.getRedirect();
        }
        if (otherResponse.isRefresh()) {
            this.refresh = true;
        }
        if (otherResponse.getRetarget() != null) {
            this.retarget = otherResponse.getRetarget();
        }
        if (otherResponse.getReswap() != null) {
            this.reswap = otherResponse.reswap;
        }

        return this;
    }

    /**
     * @deprecated use {@link #getRetarget()} instead. Will be removed in 4.0.
     */
    @Deprecated
    String getHeaderRetarget() {
        return retarget;
    }

    /**
     * @deprecated use {@link #isRefresh()} instead. Will be removed in 4.0.
     */
    @Deprecated
    boolean getHeaderRefresh() {
        return refresh;
    }

    /**
     * @deprecated use {@link #getRedirect()} instead. Will be removed in 4.0.
     */
    @Deprecated
    String getHeaderRedirect() {
        return redirect;
    }

    /**
     * @deprecated use {@link #getPushUrl()} instead. Will be removed in 4.0.
     */
    @Deprecated
    String getHeaderPushHistory() {
        return pushUrl;
    }

    /**
     * @deprecated use {@link #getReswap()} instead. Will be removed in 4.0.
     */
    @Deprecated
    public String getHeaderReswap() {
        return reswap != null ? reswap.getType().getValue() : null;
    }

    /**
     * @deprecated use {@link #getViews()} instead. Will be removed in 4.0.
     */
    @Deprecated
    public Collection<ModelAndView> getTemplates() {
        return Collections.unmodifiableCollection(views);
    }

    /**
     * @deprecated Replaced by {@link HtmxLocationRedirectView}. Will be removed in 4.0.
     */
    @Deprecated
    public HtmxLocation getLocation() {
        return location;
    }

    public String getPushUrl() {
        return pushUrl;
    }

    /**
     * @deprecated Replaced by {@link HtmxRedirectView}. Will be removed in 4.0.
     */
    @Deprecated
    public String getRedirect() {
        return redirect;
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

    public Map<String, String> getTriggers() {
        return getTriggersAsMap(this.triggers);
    }

    Collection<HtmxTrigger> getTriggersInternal() {
        return Collections.unmodifiableCollection(this.triggers);
    }

    public Map<String, String> getTriggersAfterSettle() {
        return getTriggersAsMap(this.triggersAfterSettle);
    }

    Collection<HtmxTrigger> getTriggersAfterSettleInternal() {
        return Collections.unmodifiableCollection(this.triggersAfterSettle);
    }

    public Map<String, String> getTriggersAfterSwap() {
        return getTriggersAsMap(this.triggersAfterSwap);
    }

    Collection<HtmxTrigger> getTriggersAfterSwapInternal() {
        return Collections.unmodifiableCollection(this.triggersAfterSwap);
    }

    /**
     * @deprecated Replaced by {@link HtmxView}. Will be removed in 4.0.
     */
    @Deprecated
    public Collection<ModelAndView> getViews() {
        return Collections.unmodifiableCollection(views);
    }

    /**
     * @deprecated Replaced by {@link HtmxRefreshView}. Will be removed in 4.0.
     */
    @Deprecated
    public boolean isRefresh() {
        return refresh;
    }

    public boolean isContextRelative() {
        return contextRelative;
    }

    /**
     * @deprecated will be removed in 4.0.
     */
    @Deprecated
    private Map<String, String> getTriggersAsMap(Collection<HtmxTrigger> triggers) {
        var map = new HashMap<String, String>();
        for (HtmxTrigger trigger : triggers) {
            map.put(trigger.getEventName(), Objects.toString(trigger.getEventDetail(), null));
        }
        return Collections.unmodifiableMap(map);
    }

    /**
     * @deprecated use {@link HtmxResponse} as handler method argument
     * and {@link HtmxView}, {@link HtmxRedirectView} or {@link HtmxLocationRedirectView}
     * as handler method return type instead.  Will be removed in 4.0.
     */
    @Deprecated
    public static final class Builder {

        private Set<ModelAndView> views = new LinkedHashSet<>();
        private Set<HtmxTrigger> triggers = new LinkedHashSet<>();
        private Set<HtmxTrigger> triggersAfterSettle = new LinkedHashSet<>();
        private Set<HtmxTrigger> triggersAfterSwap = new LinkedHashSet<>();
        private HtmxLocation location;
        private String pushUrl;
        private String redirect;
        private boolean refresh;
        private String replaceUrl;
        private HtmxReswap reswap;
        private String retarget;
        private String reselect;
        private boolean contextRelative = true;

        /**
         * Merges another {@link HtmxResponse} into this builder.
         *
         * @param otherResponse Another HtmxResponse that will be merged into this response.
         * @return the builder
         */
        public Builder and(HtmxResponse otherResponse) {

            otherResponse.views.forEach(otherTemplate -> {
                if (this.views.stream().anyMatch(mav -> same(otherTemplate, mav))) {
                    LOGGER.warn("Duplicate template '{}' found while merging HtmxResponse", otherTemplate);
                } else {
                    views.add(otherTemplate);
                }
            });

            mergeTriggers(this.triggers, otherResponse.triggers);
            mergeTriggers(this.triggersAfterSettle, otherResponse.triggersAfterSettle);
            mergeTriggers(this.triggersAfterSwap, otherResponse.triggersAfterSwap);

            if (otherResponse.location != null) {
                this.location = otherResponse.location;
            }
            if (otherResponse.pushUrl != null) {
                this.pushUrl = otherResponse.pushUrl;
            }
            if (otherResponse.redirect != null) {
                this.redirect = otherResponse.redirect;
            }
            if (otherResponse.refresh) {
                this.refresh = true;
            }
            if (otherResponse.replaceUrl != null) {
                this.replaceUrl = otherResponse.replaceUrl;
            }
            if (otherResponse.reswap != null) {
                this.reswap = otherResponse.reswap;
            }
            if (otherResponse.retarget != null) {
                this.retarget = otherResponse.retarget;
            }
            if (otherResponse.reselect != null) {
                this.reselect = otherResponse.reselect;
            }

            return this;
        }

        /**
         * @deprecated use {@link HtmxResponse} as handler method argument
         * and {@link HtmxView}, {@link HtmxRedirectView} or {@link HtmxLocationRedirectView}
         * as handler method return type instead.  Will be removed in 4.0.
         */
        @Deprecated
        public HtmxResponse build() {
            var htmxResponse = new HtmxResponse();
            htmxResponse.views = views;
            htmxResponse.triggers = triggers;
            htmxResponse.triggersAfterSettle = triggersAfterSettle;
            htmxResponse.triggersAfterSwap = triggersAfterSwap;
            htmxResponse.retarget = retarget;
            htmxResponse.refresh = refresh;
            htmxResponse.redirect = redirect;
            htmxResponse.pushUrl = pushUrl;
            htmxResponse.replaceUrl = replaceUrl;
            htmxResponse.reselect = reselect;
            htmxResponse.reswap = reswap;
            htmxResponse.location = location;
            htmxResponse.contextRelative = contextRelative;

            return htmxResponse;
        }

        /**
         * Set whether URLs used in the htmx response that starts with a slash ("/") should be interpreted as
         * relative to the current ServletContext, i.e. as relative to the web application root.
         * Default is "true": A URL that starts with a slash will be interpreted as relative to
         * the web application root, i.e. the context path will be prepended to the URL.
         *
         * @param contextRelative whether to interpret URLs in the htmx response as relative to the current ServletContext
         * @return the builder
         */
        public Builder contextRelative(boolean contextRelative) {
            this.contextRelative = contextRelative;
            return this;
        }

        /**
         * Allows you to do a client-side redirect that does not do a full page reload.
         *
         * @param path the path
         * @return the builder
         * @see <a href="https://htmx.org/headers/hx-location/">HX-Location Response Header</a>
         */
        public Builder location(String path) {
            this.location = new HtmxLocation(path);
            return this;
        }

        /**
         * Allows you to do a client-side redirect that does not do a full page reload.
         *
         * @param location the location
         * @return the builder
         * @see <a href="https://htmx.org/headers/hx-location/">HX-Location Response Header</a>
         */
        public Builder location(HtmxLocation location) {
            this.location = location;
            return this;
        }

        /**
         * Prevents the browser history stack from being updated.
         *
         * @return the builder
         * @see <a href="https://htmx.org/headers/hx-push/">HX-Push Response Header</a> documentation
         * @see <a href="https://htmx.org/headers/hx-replace-url/">HX-Replace-Url Response Header</a>
         */
        public Builder preventHistoryUpdate() {
            this.pushUrl = "false";
            this.replaceUrl = null;
            return this;
        }

        /**
         * Pushes a new URL into the history stack of the browser.
         * <p>
         * If you want to prevent the history stack from being updated, use {@link #preventHistoryUpdate()}.
         *
         * @param url the URL to push into the history stack. The URL can be any URL in the same origin as the current URL.
         * @return the builder
         * @see <a href="https://htmx.org/headers/hx-push/">HX-Push Response Header</a> documentation
         * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/History/replaceState">history.pushState()</a>
         */
        public Builder pushUrl(String url) {
            Assert.hasText(url, "url should not be blank");
            this.pushUrl = url;
            this.replaceUrl = null;
            return this;
        }

        /**
         * Can be used to do a client-side redirect to a new location
         *
         * @param url the URL. Can be a relative or an absolute url
         * @return the builder
         */
        public Builder redirect(String url) {
            Assert.hasText(url, "url should not be blank");
            this.redirect = url;
            return this;
        }

        /**
         * If set to "true" the client side will do a full refresh of the page
         *
         * @return the builder
         */
        public Builder refresh() {
            this.refresh = true;
            return this;
        }

        /**
         * Allows you to replace the most recent entry, i.e. the current URL, in the browser history stack.
         * <p>
         * If you want to prevent the history stack from being updated, use {@link #preventHistoryUpdate()}.
         *
         * @param url the URL to replace in the history stack. The URL can be any URL in the same origin as the current URL.
         * @return the builder
         * @see <a href="https://htmx.org/headers/hx-replace-url/">HX-Replace-Url Response Header</a>
         * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/History/replaceState">history.replaceState()</a>
         */
        public Builder replaceUrl(String url) {
            this.replaceUrl = url;
            this.pushUrl = null;
            return this;
        }

        /**
         * Set a new swap to specify how the response will be swapped.
         *
         * @param reswap the reswap options.
         * @return the builder
         */
        public Builder reswap(HtmxReswap reswap) {
            Assert.notNull(reswap, "reswap should not be null");
            this.reswap = reswap;
            return this;
        }

        /**
         * Set a CSS selector that updates the target of the content update to a different element on the page
         *
         * @param cssSelector the CSS selector
         * @return the builder
         */
        public Builder retarget(String cssSelector) {
            Assert.hasText(cssSelector, "cssSelector should not be blank");
            this.retarget = cssSelector;
            return this;
        }

        /**
         * Set a CSS selector that allows you to choose which part of the response is used to be swapped in.
         * Overrides an existing <a href="https://htmx.org/attributes/hx-select/">hx-select</a> on the triggering element.
         *
         * @param cssSelector the CSS selector
         * @return the builder
         */
        public Builder reselect(String cssSelector) {
            Assert.hasText(cssSelector, "cssSelector should not be blank");
            this.reselect = cssSelector;
            return this;
        }

        /**
         * Adds an event that will be triggered once the response is received.
         * <p>Multiple trigger were automatically be merged into the same header.
         *
         * @param eventName the event name
         * @return the builder
         * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger Response Headers</a>
         */
        public Builder trigger(String eventName) {
            Assert.hasText(eventName, "eventName should not be blank");
            return trigger(eventName, null);
        }

        /**
         * Adds an event that will be triggered once the response is received.
         * <p>Multiple trigger were automatically be merged into the same header.
         *
         * @param eventName   the event name
         * @param eventDetail details along with the event
         * @return the builder
         * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger Response Headers</a>
         */
        public Builder trigger(String eventName, Object eventDetail) {
            Assert.hasText(eventName, "eventName should not be blank");
            triggers.add(new HtmxTrigger(eventName, eventDetail));
            return this;
        }

        /**
         * Adds an event that will be triggered after the <a href="https://htmx.org/docs/#request-operations">settling step</a>.
         * <p>Multiple triggers were automatically be merged into the same header.
         *
         * @param eventName the event name
         * @return the builder
         * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger Response Headers</a>
         */
        public Builder triggerAfterSettle(String eventName) {
            Assert.hasText(eventName, "eventName should not be blank");
            return triggerAfterSettle(eventName, null);
        }

        /**
         * Adds an event that will be triggered after the <a href="https://htmx.org/docs/#request-operations">settling step</a>.
         * <p>Multiple triggers were automatically be merged into the same header.
         *
         * @param eventName   the event name
         * @param eventDetail details along with the event
         * @return the builder
         * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger Response Headers</a>
         */
        public Builder triggerAfterSettle(String eventName, Object eventDetail) {
            Assert.hasText(eventName, "eventName should not be blank");
            triggersAfterSettle.add(new HtmxTrigger(eventName, eventDetail));
            return this;
        }

        /**
         * Adds an event that will be triggered after the <a href="https://htmx.org/docs/#request-operations">swap step</a>.
         * <p>Multiple triggers were automatically be merged into the same header.
         *
         * @param eventName the event name
         * @return the builder
         * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger Response Headers</a>
         */
        public Builder triggerAfterSwap(String eventName) {
            Assert.hasText(eventName, "eventName should not be blank");
            return triggerAfterSwap(eventName, null);
        }

        /**
         * Adds an event that will be triggered after the <a href="https://htmx.org/docs/#request-operations">swap step</a>.
         * <p>Multiple triggers were automatically be merged into the same header.
         *
         * @param eventName   the event name
         * @param eventDetail details along with the event
         * @return the builder
         * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger Response Headers</a>
         */
        public Builder triggerAfterSwap(String eventName, Object eventDetail) {
            Assert.hasText(eventName, "eventName should not be blank");
            triggersAfterSwap.add(new HtmxTrigger(eventName, eventDetail));
            return this;
        }

        /**
         * Append a view name to be resolved with {@code ViewResolver} implementations and used together with the implicit model.
         *
         * @param viewName the name of the view.
         * @return the builder
         */
        public Builder view(String viewName) {
            Assert.hasText(viewName, "viewName should not be blank");
            if (!views.stream().anyMatch(mav -> viewName.equals(mav.getViewName()))) {
                views.add(new ModelAndView(viewName));
            }
            return this;
        }

        /**
         * Append a {@link View} instance to use for rendering together with the implicit model.
         *
         * @param view the view
         * @return the builder
         */
        public Builder view(View view) {
            Assert.notNull(view, "view should not be null");
            if (!views.stream().anyMatch(mav -> view.equals(mav.getView()))) {
                views.add(new ModelAndView(view));
            }
            return this;
        }

        /**
         * Append a {@link ModelAndView} instance to use for rendering.
         *
         * @param modelAndView the model and view
         * @return the builder
         */
        public Builder view(ModelAndView modelAndView) {
            Assert.notNull(modelAndView, "modelAndView should not be null");
            views.add(modelAndView);
            return this;
        }

        private static void mergeTriggers(Collection<HtmxTrigger> triggers, Collection<HtmxTrigger> otherTriggers) {
            for (HtmxTrigger otherTrigger : otherTriggers) {
                if (LOGGER.isWarnEnabled()) {
                    Optional<HtmxTrigger> otrigger = triggers.stream()
                                                             .filter(t -> t.getEventName().equals(otherTrigger.getEventName()))
                                                             .findFirst();

                    if (otrigger.isPresent()) {
                        LOGGER.warn("Duplicate trigger event '{}' found. Details '{}' will be overwritten by with '{}'", otherTrigger.getEventName(), otrigger.get().getEventDetail(), otherTrigger.getEventDetail());
                    }
                }
                triggers.add(otherTrigger);
            }
        }

        private static boolean same(ModelAndView one, ModelAndView two) {
            if (one == two) {
                return true;
            }
            if (one == null || two == null) {
                return false;
            }
            if (one.getViewName() != null && one.getViewName().equals(two.getViewName())) {
                return true;
            }
            if (one.getView() != null && one.getView().equals(two.getView())) {
                return true;
            }
            return false;
        }

    }
}
