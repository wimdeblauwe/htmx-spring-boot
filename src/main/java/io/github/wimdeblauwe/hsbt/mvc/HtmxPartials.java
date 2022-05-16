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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

/**
 * Representation of HTMX partials.
 *
 * @author Oliver Drotbohm
 */
public class HtmxPartials {

	private final Collection<Partial> partials;
    protected static final String MAIN_PARTIAL_ID = "__main__";

	public HtmxPartials() {
		this.partials = new ArrayList<>();
	}
	
	HtmxPartials(Collection<Partial> partials) {
		this.partials = partials;
	}

	/**
	 * Append the rendered fragment.
	 *
	 * @param target must not be {@literal null} or empty.
	 * @return
	 */
	public HtmxPartialsBuilder append(String target) {
		return new HtmxPartialsBuilder(partials, target, Action.APPEND);
	}

	/**
	 * Prepend the rendered fragment.
	 *
	 * @param target must not be {@literal null} or empty.
	 * @return
	 */
	public HtmxPartialsBuilder prepend(String target) {
		return new HtmxPartialsBuilder(partials, target, Action.PREPEND);
	}

	/**
	 * Remove the rendered fragment.
	 *
	 * @param target must not be {@literal null} or empty.
	 * @return
	 */
	public HtmxPartials remove(String target) {
		return new HtmxPartialsBuilder(partials, target, Action.REMOVE)
				.with("¯\\_(ツ)_/¯");
	}

	/**
	 * Replace the rendered fragment.
	 *
	 * @param target must not be {@literal null} or empty.
	 * @return
	 */
	public HtmxPartialsBuilder replace(String target) {
		return new HtmxPartialsBuilder(partials, target, Action.REPLACE);
	}

	/**
	 * Update the rendered fragment.
	 *
	 * @param target must not be {@literal null} or empty.
	 * @return
	 */
	public HtmxPartialsBuilder update(String target) {
		return new HtmxPartialsBuilder(partials, target, Action.UPDATE);
	}
	
	Iterable<Partial> toIterable() {
		return () -> partials.iterator();
	}

    public <V> HtmxPartials main(String templateOrFragment) {
        return new HtmxPartialsBuilder(partials, MAIN_PARTIAL_ID, Action.REPLACE).with(templateOrFragment);
    }

    public static class HtmxPartialsBuilder {

        private final Collection<Partial> streams;
        private final String target;
        private final Action action;

		HtmxPartialsBuilder(Collection<Partial> streams, String target, Action action) {
			this.streams = streams;
			this.target = target;
			this.action = action;
		}

		/**
		 * @param templateOrFragment the identifier of a template or fragment.
		 * @return will never be {@literal null}.
		 */
		public HtmxPartials with(String templateOrFragment) {
			return and(new Partial(action, target, templateOrFragment));
		}

		/**
		 * Renders the fragment with the current target name within the given template.
		 *
		 * @param template must not be {@literal null} or empty.
		 * @return will never be {@literal null}.
		 */
		public HtmxPartials withinTemplate(String template) {

			Assert.hasText(template, "Template name must not be null or empty!");

			return and(new Partial(action, target, template.concat(" :: ".concat(target))));
		}

		/**
		 * Renders the given fragment as Turbo Stream.
		 *
		 * @param fragment must not be {@literal null} or empty and a valid fragment identifier.
		 * @return will never be {@literal null}.
		 */
		public HtmxPartials withFragment(String fragment) {

			Assert.hasText(fragment, "Fragment must not be null or empty!");
			Assert.isTrue(fragment.contains("::"), () -> "Invalid fragment identifier " + fragment + "!");

			return and(new Partial(action, target, fragment));
		}

		private HtmxPartials and(Partial stream) {

			List<Partial> list = new ArrayList<>(streams);
			list.add(stream);

			return new HtmxPartials(list);
		}
	}


	enum Action {

		APPEND,

		PREPEND,

		REPLACE,

		UPDATE,

		REMOVE;

		String toAttribute() {

			switch (this) {
				case APPEND:
					return "beforeend";
				default:
					return "true";
			}
		}
	}
	
	static class Partial {

		private final Action action;
		private final String target, template;
		
		Partial(Action action, String target, String template) {
			this.action = action;
			this.target = target;
			this.template = template;
		}
		
		/**
		 * @return the action
		 */
		public Action getAction() {
			return action;
		}
		
		/**
		 * @return the template
		 */
		String getTemplate() {
			return template;
		}
		

        String openWrapper() {
            if (target.equals(MAIN_PARTIAL_ID)) {
                return "";
            } else {
                return String.format("<div id=\"%s\" hx-swap-oob=\"%s\">\n", target,
                                     action.toAttribute());
            }
        }

        String closeWrapper() {
            if (target.equals(MAIN_PARTIAL_ID)) {
                return "";
            } else {
                return "\n</div>\n";
            }
        }

		boolean isRemove() {
			return Action.REMOVE.equals(action);
		}

		boolean isReplace() {
			return Action.REPLACE.equals(action);
		}
	}
}
