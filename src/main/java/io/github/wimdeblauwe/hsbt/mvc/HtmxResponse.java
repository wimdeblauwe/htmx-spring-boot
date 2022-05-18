package io.github.wimdeblauwe.hsbt.mvc;

import org.springframework.util.Assert;

import java.util.*;

/**
 * Representation of HTMX partials.
 *
 * @author Oliver Drotbohm
 * @author Clint Checketts
 */
final public class HtmxResponse {

	private final Collection<String> templates;
	private final Map<String, String> triggers;
	private final Map<String, String> triggersAfterSettle;
	private final Map<String, String> triggersAfterSwap;

	public HtmxResponse() {
        this.templates = new ArrayList<>();
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
        this.templates.addAll(otherResponse.templates);
        this.triggers.putAll(otherResponse.triggers);
        this.triggersAfterSettle.putAll(otherResponse.triggersAfterSettle);
        this.triggersAfterSwap.putAll(otherResponse.triggersAfterSwap);

        return this;
    }


	Iterable<String> toIterable() {
		return () -> templates.iterator();
	}

    public Map<String, String> getTriggers() {
        if(this.triggers.isEmpty()) {
            return Collections.EMPTY_MAP;
        }
        return new HashMap<>(this.triggers);
    }

    public Map<String, String> getTriggersAfterSettle() {
        if(this.triggers.isEmpty()) {
            return Collections.EMPTY_MAP;
        }
        return new HashMap<>(this.triggersAfterSettle);
    }

    public Map<String, String> getTriggersAfterSwap() {
        if(this.triggers.isEmpty()) {
            return Collections.EMPTY_MAP;
        }
        return new HashMap<>(this.triggersAfterSwap);
    }
}
