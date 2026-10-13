package io.github.wimdeblauwe.htmx.spring.boot.thymeleaf;

import org.jspecify.annotations.Nullable;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.processor.IProcessor;
import tools.jackson.databind.json.JsonMapper;

import java.util.HashSet;
import java.util.Set;

public class HtmxDialect extends AbstractProcessorDialect implements IExpressionObjectDialect {

    private @Nullable HtmxExpressionObjectFactory expressionObjectFactory;

    private final JsonMapper jsonMapper;

    public HtmxDialect(JsonMapper jsonMapper) {
        super("Htmx", "hx", 1000);
        this.jsonMapper = jsonMapper;
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        Set<IProcessor> htmxProcessors = new HashSet<>();

        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "boost", jsonMapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "confirm", jsonMapper));
        htmxProcessors.add(new HtmxCsrfAttributeProcessor(dialectPrefix, "delete", jsonMapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "disable", jsonMapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "disinherit", jsonMapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "encoding", jsonMapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "ext", jsonMapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "get", jsonMapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "headers", jsonMapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "history-elt", jsonMapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "include", jsonMapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "indicator", jsonMapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "params", jsonMapper));
        htmxProcessors.add(new HtmxCsrfAttributeProcessor(dialectPrefix, "patch", jsonMapper));
        htmxProcessors.add(new HtmxCsrfAttributeProcessor(dialectPrefix, "post", jsonMapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "preserve", jsonMapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "prompt", jsonMapper));
        htmxProcessors.add(new HtmxCsrfAttributeProcessor(dialectPrefix, "put", jsonMapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "push-url", jsonMapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "request", jsonMapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "select", jsonMapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "swap", jsonMapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "swap-oob", jsonMapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "sync", jsonMapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "target", jsonMapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "trigger", jsonMapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "validate", jsonMapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "vals", jsonMapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "vars", jsonMapper));

        return htmxProcessors;
    }

    @Override
    public IExpressionObjectFactory getExpressionObjectFactory() {
        if (this.expressionObjectFactory == null) {
            this.expressionObjectFactory = new HtmxExpressionObjectFactory();
        }
        return this.expressionObjectFactory;
    }
}
