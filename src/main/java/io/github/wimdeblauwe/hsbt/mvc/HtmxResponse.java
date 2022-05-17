/*
 * Copyright 2021-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.wimdeblauwe.hsbt.mvc;

import java.util.*;

/**
 * Representation of HTMX partials.
 *
 * @author Oliver Drotbohm
 * @author Clint Checketts
 */
public class HtmxResponse {

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
