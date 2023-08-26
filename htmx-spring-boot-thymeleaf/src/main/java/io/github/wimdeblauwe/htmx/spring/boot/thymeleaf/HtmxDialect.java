package io.github.wimdeblauwe.htmx.spring.boot.thymeleaf;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.processor.IProcessor;

import java.util.HashSet;
import java.util.Set;

public class HtmxDialect extends AbstractProcessorDialect implements IExpressionObjectDialect {

    private HtmxExpressionObjectFactory expressionObjectFactory;

    private final ObjectMapper mapper;

    public HtmxDialect(ObjectMapper mapper) {
        super("Htmx", "hx", 1000);
        this.mapper = mapper;
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        Set<IProcessor> htmxProcessors = new HashSet<>();

        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "boost", mapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "confirm", mapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "delete", mapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "disable", mapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "disinherit", mapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "encoding", mapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "ext", mapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "get", mapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "headers", mapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "history-elt", mapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "include", mapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "indicator", mapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "params", mapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "patch", mapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "post", mapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "preserve", mapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "prompt", mapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "put", mapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "push-url", mapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "request", mapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "select", mapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "swap", mapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "swap-oob", mapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "sync", mapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "target", mapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "trigger", mapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "validate", mapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "vals", mapper));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "vars", mapper));

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
