package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

/**
 * Used as a controller method return type to specify htmx-related response headers
 * and returning multiple template partials in a single response.
 *
 * @author Oliver Drotbohm
 * @author Clint Checketts
 * @author Sascha Woo
 * @see <a href="https://htmx.org/reference/#response_headers">Response Headers Reference</a>
 */
public final class HtmxResponse {

    private static final Logger LOG = LoggerFactory.getLogger(HtmxResponse.class);

    private final Set<ModelAndView> views;
    private final Map<String, String> triggers;
    private final Map<String, String> triggersAfterSettle;
    private final Map<String, String> triggersAfterSwap;
    // TODO should also be final after switching to builder pattern
    private String retarget;
    private boolean refresh;
    private String redirect;
    private String pushUrl;
    private HxSwapType reswap;

    /**
     * Return a builder to build a {@link HtmxResponse}.
     *
     * @return the builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * @deprecated use {@link #builder()} instead. Will be removed in 4.0.
     */
    @Deprecated
    public HtmxResponse() {
        this.views = new LinkedHashSet<>();
        this.triggers = new HashMap<>();
        this.triggersAfterSettle = new HashMap<>();
        this.triggersAfterSwap = new HashMap<>();
    }

    HtmxResponse(Set<ModelAndView> views, Map<String, String> triggers, Map<String, String> triggersAfterSettle, Map<String, String> triggersAfterSwap, String retarget, boolean refresh, String redirect, String pushUrl, HxSwapType reswap) {
        this.views = views;
        this.triggers = triggers;
        this.triggersAfterSettle = triggersAfterSettle;
        this.triggersAfterSwap = triggersAfterSwap;
        this.retarget = retarget;
        this.refresh = refresh;
        this.redirect = redirect;
        this.pushUrl = pushUrl;
        this.reswap = reswap;
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
     * Set a HX-Trigger header. Multiple trigger were automatically be merged into the same header.
     *
     * @param eventName must not be {@literal null} or empty.
     * @return same HtmxResponse for chaining
     * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger Response Headers</a>
     * @deprecated use {@link Builder#trigger(String)} instead.  Will be removed in 4.0.
     */
    @Deprecated
    public HtmxResponse addTrigger(String eventName) {
        Assert.hasText(eventName, "eventName should not be blank");
        return addTrigger(eventName, null, HxTriggerLifecycle.RECEIVE);
    }

    /**
     * Set a HX-Trigger (or HX-Trigger-After-Settle or HX-Trigger-After-Swap headers.
     * Multiple trigger were
     * automatically be merged into the same header.
     *
     * @param eventName must not be {@literal null} or empty.
     * @param eventDetail can be {@literal null}.
     * @param step must not be {@literal null} or empty.
     * @return same HtmxResponse for chaining
     * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger Response Headers</a>
     * @deprecated use {@link Builder#trigger(String, String, HxTriggerLifecycle)} instead.  Will be removed in 4.0.
     */
    @Deprecated
    public HtmxResponse addTrigger(String eventName, String eventDetail, HxTriggerLifecycle step) {
        Assert.hasText(eventName, "eventName should not be blank");
        switch (step) {
            case RECEIVE:
                triggers.put(eventName, eventDetail);
                break;
            case SETTLE:
                triggersAfterSettle.put(eventName, eventDetail);
                break;
            case SWAP:
                triggersAfterSwap.put(eventName, eventDetail);
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
        this.reswap = swapType;
        return this;
    }

    /**
     * @param otherResponse Another HtmxResponse that will be merged into this response.
     * @return this for chaining
     * @deprecated use {@link Builder#and(HtmxResponse)} instead.  Will be removed in 4.0.
     */
    @Deprecated
    public HtmxResponse and(HtmxResponse otherResponse){
        otherResponse.views.forEach(otherTemplate -> {
            if(this.views.stream().anyMatch(mav -> same(otherTemplate, mav))) {
                LOG.info("Duplicate template '{}' found while merging HtmxResponse", otherTemplate);
            } else {
                views.add(otherTemplate);
            }
        });
        mergeMapAndLog(HxTriggerLifecycle.RECEIVE, this.triggers, otherResponse.triggers);
        mergeMapAndLog(HxTriggerLifecycle.SETTLE, this.triggersAfterSettle, otherResponse.triggersAfterSettle);
        mergeMapAndLog(HxTriggerLifecycle.SWAP, this.triggersAfterSwap, otherResponse.triggersAfterSwap);

        if(otherResponse.getPushUrl() != null) {
            this.pushUrl = otherResponse.getPushUrl();
        }
        if(otherResponse.getRedirect() != null) {
            this.redirect = otherResponse.getRedirect();
        }
        if(otherResponse.isRefresh()) {
            this.refresh = true;
        }
        if(otherResponse.getRetarget() != null) {
            this.retarget = otherResponse.getRetarget();
        }
        if(otherResponse.getReswap() != null) {
            this.reswap = otherResponse.getReswap();
        }

        return this;
    }

    @Deprecated
    private boolean same(ModelAndView one, ModelAndView two) {
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

    @Deprecated
    private void mergeMapAndLog(HxTriggerLifecycle receive, Map<String, String> triggers, Map<String, String> otherTriggers) {
        otherTriggers.forEach((key, value) -> {
            if (LOG.isInfoEnabled()) {
                if (triggers.containsKey(key)) {
                    String matchingTrigger = triggers.get(key);
                    LOG.info("Duplicate {} entry: event '{}' details '{}' will be overwritten by with '{}'", receive.getHeaderName(), key, matchingTrigger, value);
                }
            }
            triggers.put(key, value);
        });
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
        return reswap.getValue();
    }

    /**
     * @deprecated use {@link #getViews()} instead. Will be removed in 4.0.
     */
    @Deprecated
    public Collection<ModelAndView> getTemplates() {
        return Collections.unmodifiableCollection(views);
    }

    public String getPushUrl() {
        return pushUrl;
    }

    public String getRedirect() {
        return redirect;
    }

    public HxSwapType getReswap() {
        return reswap;
    }

    public String getRetarget() {
        return retarget;
    }

    public Map<String, String> getTriggers() {
        return Collections.unmodifiableMap(this.triggers);
    }

    public Map<String, String> getTriggersAfterSettle() {
        return Collections.unmodifiableMap(this.triggersAfterSettle);
    }

    public Map<String, String> getTriggersAfterSwap() {
        return Collections.unmodifiableMap(this.triggersAfterSwap);
    }

    public Collection<ModelAndView> getViews() {
        return Collections.unmodifiableCollection(views);
    }

    public boolean isRefresh() {
        return refresh;
    }

    public static final class Builder {

        private Set<ModelAndView> views = new LinkedHashSet<>();
        private Map<String, String> triggers = new HashMap<>();
        private Map<String, String> triggersAfterSettle = new HashMap<>();
        private Map<String, String> triggersAfterSwap = new HashMap<>();
        private String pushUrl;
        private String redirect;
        private boolean refresh;
        private HxSwapType reswap;
        private String retarget;

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

            mergeTrigger(HxTriggerLifecycle.RECEIVE, this.triggers, otherResponse.triggers);
            mergeTrigger(HxTriggerLifecycle.SETTLE, this.triggersAfterSettle, otherResponse.triggersAfterSettle);
            mergeTrigger(HxTriggerLifecycle.SWAP, this.triggersAfterSwap, otherResponse.triggersAfterSwap);

            if (otherResponse.pushUrl != null) {
                this.pushUrl = otherResponse.pushUrl;
            }
            if (otherResponse.redirect != null) {
                this.redirect = otherResponse.redirect;
            }
            if (otherResponse.refresh) {
                this.refresh = true;
            }
            if (otherResponse.retarget != null) {
                this.retarget = otherResponse.retarget;
            }
            if (otherResponse.reswap != null) {
                this.reswap = otherResponse.reswap;
            }

            return this;
        }

        public HtmxResponse build() {
            return new HtmxResponse(views, triggers, triggersAfterSettle, triggersAfterSwap, retarget, refresh, redirect, pushUrl, reswap);
        }

        /**
         * Pushes a new url into the history stack
         *
         * @param url the URL or {@literal false} which prevents the browser history from being updated.
         * @return the builder
         * @see <a href="https://htmx.org/headers/hx-push/">HX-Push Response Header</a> documentation
         */
        public Builder pushUrl(String url) {
            Assert.hasText(url, "url should not be blank");
            this.pushUrl = url;
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
         * Set a new swap to specify how the response will be swapped
         *
         * @param swapType the swap style
         * @return the builder
         */
        public Builder reswap(HxSwapType swapType) {
            Assert.notNull(swapType, "swapType should not be null");
            this.reswap = swapType;
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
         * Set a HX-Trigger header. Multiple trigger were automatically be merged into the same header.
         *
         * @param eventName must not be {@literal null} or empty.
         * @return the builder
         * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger Response Headers</a>
         */
        public Builder trigger(String eventName) {
            Assert.hasText(eventName, "eventName should not be blank");
            return trigger(eventName, null, HxTriggerLifecycle.RECEIVE);
        }

        /**
         * Set a HX-Trigger (or HX-Trigger-After-Settle or HX-Trigger-After-Swap headers.
         * Multiple trigger were
         * automatically be merged into the same header.
         *
         * @param eventName   must not be {@literal null} or empty.
         * @param eventDetail can be {@literal null}.
         * @param step        must not be {@literal null} or empty.
         * @return the builder
         * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger Response Headers</a>
         */
        public Builder trigger(String eventName, String eventDetail, HxTriggerLifecycle step) {
            Assert.hasText(eventName, "eventName should not be blank");
            switch (step) {
                case RECEIVE:
                    triggers.put(eventName, eventDetail);
                    break;
                case SETTLE:
                    triggersAfterSettle.put(eventName, eventDetail);
                    break;
                case SWAP:
                    triggersAfterSwap.put(eventName, eventDetail);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown step " + step);
            }
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

        private void mergeTrigger(HxTriggerLifecycle receive, Map<String, String> triggers, Map<String, String> otherTriggers) {
            otherTriggers.forEach((key, value) -> {
                if (LOG.isInfoEnabled()) {
                    if (triggers.containsKey(key)) {
                        String matchingTrigger = triggers.get(key);
                        LOG.info("Duplicate {} entry: event '{}' details '{}' will be overwritten by with '{}'", receive.getHeaderName(), key, matchingTrigger, value);
                    }
                }
                triggers.put(key, value);
            });
        }

        private boolean same(ModelAndView one, ModelAndView two) {
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
