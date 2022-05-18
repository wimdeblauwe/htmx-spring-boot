package io.github.wimdeblauwe.hsbt.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.*;

/**
 * Representation of HTMX partials.
 *
 * @author Oliver Drotbohm
 * @author Clint Checketts
 */
final public class HtmxResponse {
    private static final Logger LOG = LoggerFactory.getLogger(HtmxResponse.class);

	private final Set<String> templates;
	private final Map<String, String> triggers;
	private final Map<String, String> triggersAfterSettle;
	private final Map<String, String> triggersAfterSwap;

	public HtmxResponse() {
        this.templates = new HashSet<>();
        this.triggers = new HashMap<>();
        this.triggersAfterSettle = new HashMap<>();
        this.triggersAfterSwap = new HashMap<>();
	}
	
	/**
	 * Append the rendered fragment.
	 *
	 * @param template must not be {@literal null} or empty.
	 * @return same HtmxResponse for chaining
	 */
	public HtmxResponse addTemplate(String template) {
        Assert.hasText(template, "template should not be blank");
        templates.add(template);
        return this;
	}

    public HtmxResponse addTrigger(String eventName) {
        return addTrigger(eventName, null, HxTriggerLifecycle.RECEIVE);
    }

    public HtmxResponse addTrigger(String eventName, String eventDetail, HxTriggerLifecycle step) {
        switch(step) {
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
                throw new IllegalArgumentException("Unknown step "+ step);
        }
        return this;
    }

    public HtmxResponse and(HtmxResponse otherResponse){
        otherResponse.templates.forEach(otherTemplate -> {
            if(!this.templates.add(otherTemplate)) {
                LOG.info("Duplicate template '{}' found while merging HtmxResponse", otherTemplate);
            }
        });
        mergeMapAndLog(HxTriggerLifecycle.RECEIVE, this.triggers, otherResponse.triggers);
        mergeMapAndLog(HxTriggerLifecycle.SETTLE, this.triggersAfterSettle, otherResponse.triggersAfterSettle);
        mergeMapAndLog(HxTriggerLifecycle.SWAP, this.triggersAfterSwap, otherResponse.triggersAfterSwap);

        return this;
    }

    private void mergeMapAndLog(HxTriggerLifecycle receive, Map<String, String> triggers, Map<String, String> otherTriggers) {
        otherTriggers.forEach((key, value) -> {
            if(LOG.isInfoEnabled()) {
                if(triggers.containsKey(key)) {
                    String matchingTrigger = triggers.get(key);
                    LOG.info("Duplicate {} entry: event '{}' details '{}' will be overwritten by with '{}'", receive.getHeaderName(), key, matchingTrigger, value);
                }
            }
            triggers.put(key, value);
        });
    }


	Set<String> getTemplates() {
		return new HashSet<>(templates);
	}

    Map<String, String> getTriggers() {
        if(this.triggers.isEmpty()) {
            return Collections.EMPTY_MAP;
        }
        return new HashMap<>(this.triggers);
    }

    Map<String, String> getTriggersAfterSettle() {
        if(this.triggers.isEmpty()) {
            return Collections.EMPTY_MAP;
        }
        return new HashMap<>(this.triggersAfterSettle);
    }

    Map<String, String> getTriggersAfterSwap() {
        if(this.triggers.isEmpty()) {
            return Collections.EMPTY_MAP;
        }
        return new HashMap<>(this.triggersAfterSwap);
    }
}
