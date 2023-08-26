package io.github.wimdeblauwe.htmx.spring.boot.thymeleaf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeDefinition;
import org.thymeleaf.engine.AttributeDefinitions;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.IAttributeDefinitionsAware;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring6.requestdata.RequestDataValueProcessorUtils;
import org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor;
import org.thymeleaf.standard.util.StandardProcessorUtils;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;
import org.unbescape.html.HtmlEscape;

import java.util.LinkedHashMap;

public class HtmxAttributeProcessor extends AbstractStandardExpressionAttributeTagProcessor
        implements IAttributeDefinitionsAware {

    public static final int ATTR_PRECEDENCE = 1000;
    private final String attrName;
    private final ObjectMapper mapper;

    private static final TemplateMode TEMPLATE_MODE = TemplateMode.HTML;

    private AttributeDefinition targetAttributeDefinition;

    public HtmxAttributeProcessor(String dialectPrefix,
                                  String attrName,
                                  ObjectMapper mapper) {
        super(TEMPLATE_MODE, dialectPrefix, attrName, ATTR_PRECEDENCE, false, true);
        this.attrName = attrName;
        this.mapper = mapper;
    }

    public void setAttributeDefinitions(final AttributeDefinitions attributeDefinitions) {
        Validate.notNull(attributeDefinitions, "Attribute Definitions cannot be null");
        // We precompute the AttributeDefinition of the target attribute in order to being able to use much
        // faster methods for setting/replacing attributes on the ElementAttributes implementation
        this.targetAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, this.attrName);
    }

    @Override
    protected final void doProcess(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final AttributeName attributeName,
            final String attributeValue,
            final Object expressionResult,
            final IElementTagStructureHandler structureHandler) {
        if (expressionResult == null) {
            structureHandler.removeAttribute(attributeName);
        } else {
            String expressionResultString;
            if (expressionResult instanceof LinkedHashMap) {
                try {
                    expressionResultString = this.mapper.writeValueAsString(expressionResult);
                } catch (JsonProcessingException e) {
                    throw new TemplateProcessingException("Exception writing map", tag.getTemplateName(), tag.getLine(), tag.getLine(), e);
                }
            } else {
                expressionResultString = expressionResult.toString();
            }

            String newAttributeValue = HtmlEscape.escapeHtml4Xml(expressionResultString);

            // Let RequestDataValueProcessor modify the attribute value if needed
            newAttributeValue = RequestDataValueProcessorUtils.processUrl(context, newAttributeValue);

            // Set the real, non prefixed attribute
            StandardProcessorUtils.replaceAttribute(structureHandler, attributeName, this.targetAttributeDefinition, "hx-" + this.attrName, (newAttributeValue == null ? "" : newAttributeValue));
        }
    }
}
