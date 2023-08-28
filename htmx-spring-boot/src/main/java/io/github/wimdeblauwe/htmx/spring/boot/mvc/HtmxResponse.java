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
 * Can be used as a controller method return value to specify htmx
 * related response options and returning multiple partials in a single response.
 *
 * @author Oliver Drotbohm
 * @author Clint Checketts
 * @author Sascha Woo
 * @see <a href="https://htmx.org/reference/#response_headers">Response Headers Reference</a>
 */
public final class HtmxResponse {

    private static final Logger LOG = LoggerFactory.getLogger(HtmxResponse.class);

    private final Set<ModelAndView> templates;
    private final Map<String, String> triggers;
    private final Map<String, String> triggersAfterSettle;
    private final Map<String, String> triggersAfterSwap;
    private final String retarget;
    private final boolean refresh;
    private final String redirect;
    private final String pushUrl;
    private final HxSwapType reswap;

    /**
     * Return a builder to build a {@link HtmxResponse}.
     *
     * @return the builder
     */
    public static Builder builder() {
        return new Builder();
    }

    HtmxResponse(Set<ModelAndView> templates, Map<String, String> triggers, Map<String, String> triggersAfterSettle, Map<String, String> triggersAfterSwap, String retarget, boolean refresh, String redirect, String pushUrl, HxSwapType reswap) {
        this.templates = templates;
        this.triggers = triggers;
        this.triggersAfterSettle = triggersAfterSettle;
        this.triggersAfterSwap = triggersAfterSwap;
        this.retarget = retarget;
        this.refresh = refresh;
        this.redirect = redirect;
        this.pushUrl = pushUrl;
        this.reswap = reswap;
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

    public boolean isRefresh() {
        return refresh;
    }

    public Collection<ModelAndView> getTemplates() {
        return Collections.unmodifiableCollection(templates);
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

    public static final class Builder {

        private Set<ModelAndView> templates = new LinkedHashSet<>();
        private Map<String, String> triggers = new HashMap<>();
        private Map<String, String> triggersAfterSettle = new HashMap<>();
        private Map<String, String> triggersAfterSwap = new HashMap<>();
        private String pushUrl;
        private String redirect;
        private boolean refresh;
        private HxSwapType reswap;
        private String retarget;

        /**
         * Append the rendered template or fragment.
         *
         * @param template must not be {@literal null} or empty.
         * @return the builder
         */
        public Builder template(String template) {
            Assert.hasText(template, "template should not be blank");
            if (!templates.stream().anyMatch(mav -> template.equals(mav.getViewName()))) {
                templates.add(new ModelAndView(template));
            }
            return this;
        }

        /**
         * Append the rendered template or fragment as a resolved {@link View}.
         *
         * @param template must not be {@literal null}.
         * @return the builder
         */
        public Builder template(View template) {
            Assert.notNull(template, "template should not be null");
            if (!templates.stream().anyMatch(mav -> template.equals(mav.getView()))) {
                templates.add(new ModelAndView(template));
            }
            return this;
        }

        /**
         * Append the rendered template or fragment as a {@link ModelAndView}.
         *
         * @param template must not be {@literal null}.
         * @return the builder
         */
        public Builder template(ModelAndView template) {
            Assert.notNull(template, "template should not be null");
            templates.add(template);
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
         * Merges another {@link HtmxResponse} into this builder.
         *
         * @param otherResponse Another HtmxResponse that will be merged into this response.
         * @return the builder
         */
        public Builder and(HtmxResponse otherResponse) {

            otherResponse.templates.forEach(otherTemplate -> {
                if (this.templates.stream().anyMatch(mav -> same(otherTemplate, mav))) {
                    LOG.info("Duplicate template '{}' found while merging HtmxResponse", otherTemplate);
                } else {
                    templates.add(otherTemplate);
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
                this.redirect = otherResponse.retarget;
            }
            if (otherResponse.reswap != null) {
                this.reswap = otherResponse.reswap;
            }

            return this;
        }

        public HtmxResponse build() {
            return new HtmxResponse(templates, triggers, triggersAfterSettle, triggersAfterSwap, retarget, refresh, redirect, pushUrl, reswap);
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
