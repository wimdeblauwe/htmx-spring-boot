package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmxResponse.class);

    private final Set<ModelAndView> templates;
    private final Set<HtmxTrigger> triggers;
    private final Set<HtmxTrigger> triggersAfterSettle;
    private final Set<HtmxTrigger> triggersAfterSwap;
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

    HtmxResponse(Set<ModelAndView> templates, Set<HtmxTrigger> triggers, Set<HtmxTrigger> triggersAfterSettle, Set<HtmxTrigger> triggersAfterSwap, String retarget, boolean refresh, String redirect, String pushUrl, HxSwapType reswap) {
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

    public Collection<HtmxTrigger> getTriggers() {
        return Collections.unmodifiableCollection(this.triggers);
    }

    public Collection<HtmxTrigger> getTriggersAfterSettle() {
        return Collections.unmodifiableCollection(this.triggersAfterSettle);
    }

    public Collection<HtmxTrigger> getTriggersAfterSwap() {
        return Collections.unmodifiableCollection(this.triggersAfterSwap);
    }

    public static final class Builder {

        private Set<ModelAndView> templates = new LinkedHashSet<>();
        private Set<HtmxTrigger> triggers = new LinkedHashSet<>();
        private Set<HtmxTrigger> triggersAfterSettle = new LinkedHashSet<>();
        private Set<HtmxTrigger> triggersAfterSwap = new LinkedHashSet<>();
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
         * @param eventName the event name
         * @param eventDetail details along with the event
         * @return the builder
         * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger Response Headers</a>
         */
        public Builder trigger(String eventName, Map<String, Object> eventDetail) {
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
         * @param eventName the event name
         * @param eventDetail details along with the event
         * @return the builder
         * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger Response Headers</a>
         */
        public Builder triggerAfterSettle(String eventName, Map<String, Object> eventDetail) {
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
         * @param eventName the event name
         * @param eventDetail details along with the event
         * @return the builder
         * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger Response Headers</a>
         */
        public Builder triggerAfterSwap(String eventName, Map<String, Object> eventDetail) {
            Assert.hasText(eventName, "eventName should not be blank");
            triggersAfterSwap.add(new HtmxTrigger(eventName, eventDetail));
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
                    LOGGER.warn("Duplicate template '{}' found while merging HtmxResponse", otherTemplate);
                } else {
                    templates.add(otherTemplate);
                }
            });

            mergeTriggers(this.triggers, otherResponse.triggers);
            mergeTriggers(this.triggersAfterSettle, otherResponse.triggersAfterSettle);
            mergeTriggers(this.triggersAfterSwap, otherResponse.triggersAfterSwap);

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

        private void mergeTriggers(Collection<HtmxTrigger> triggers, Collection<HtmxTrigger> otherTriggers) {
            for (HtmxTrigger otherTrigger : otherTriggers) {
                if (LOGGER.isWarnEnabled()) {
                    Optional<HtmxTrigger> otrigger = triggers.stream().filter(t -> t.getEventName().equals(otherTrigger.getEventName())).findFirst();
                    if (otrigger.isPresent()) {
                        LOGGER.warn("Duplicate trigger event '{}' found. Details '{}' will be overwritten by with '{}'", otherTrigger.getEventName(), otrigger.get().getEventDetail(), otherTrigger.getEventDetail());
                    }
                }
                triggers.add(otherTrigger);
            }
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
