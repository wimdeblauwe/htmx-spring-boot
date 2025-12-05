package io.github.wimdeblauwe.htmx.spring.boot.thymeleaf;

import org.springframework.security.web.csrf.CsrfToken;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.unbescape.html.HtmlEscape;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Thymeleaf processor for seamless integration of htmx with Spring Boot applications using CSRF protection.
 * <p>
 * Automatically injects the current Spring Security {@link CsrfToken} into htmx request payloads.
 * It obtains the CSRF token from the Thymeleaf context (via the {@code _csrf} variable)
 * and merges it into the {@code hx-vals} attribute, while preserving any existing values.
 * <p>
 * If no CSRF token is available, the processor performs no action.
 * <p>
 * This enables htmx-triggered requests to include the CSRF token automatically,
 * eliminating the need for manual token handling in templates.
 * <p>
 * Example:
 * <pre>{@code
 * <a hx:post="@{/logout}">Log out</a>
 * }</pre>
 * <p>
 * After processing, will render as:
 * <pre>{@code
 * <a hx-post="/logout"
 *    hx-vals="{&quot;_csrf&quot;:&quot;abc123&quot;}">Log out</a>
 * }</pre>
 * ("abc123" represents the real CSRF token that Spring Security provides at runtime)
 *
 * @author LC Nicolau
 * @see <a href="https://htmx.org/attributes/hx-vals/">hx-vals Attribute Reference</a>
 * @since 5.1.0
 */
public class HtmxCsrfAttributeProcessor extends HtmxAttributeProcessor {

    public HtmxCsrfAttributeProcessor(String dialectPrefix,
                                      String attrName,
                                      ObjectMapper mapper) {
        super(dialectPrefix, attrName, ATTR_PRECEDENCE + 1, mapper);
    }

    @Override
    protected void doProcess(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final AttributeName attributeName,
            final String attributeValue,
            final Object expressionResult,
            final IElementTagStructureHandler structureHandler) {
        super.doProcess(context, tag, attributeName, attributeValue, expressionResult, structureHandler);

        var token = (CsrfToken) context.getVariable("_csrf");
        if (token == null || expressionResult == null) {
            return;
        }
        var vals = this.getCurrentVals(tag);
        vals.put(token.getParameterName(), token.getToken());
        try {
            var json = mapper.writeValueAsString(vals);
            var escaped = HtmlEscape.escapeHtml4Xml(json);
            structureHandler.setAttribute("hx-vals", escaped);
        } catch (JacksonException e) {
            throw new TemplateProcessingException("Exception writing map", tag.getTemplateName(), tag.getLine(), tag.getLine(), e);
        }
    }

    protected Map<String, Object> getCurrentVals(IProcessableElementTag tag) {
        var current = tag.getAttributeValue("hx-vals");
        if (current == null || current.isBlank()) {
            return new HashMap<>();
        }
        try {
            var json = HtmlEscape.unescapeHtml(current);
            return this.mapper.readValue(json, new TypeReference<>() {
            });
        } catch (JacksonException e) {
            throw new TemplateProcessingException("Exception reading map", tag.getTemplateName(), tag.getLine(), tag.getLine(), e);
        }
    }

}
