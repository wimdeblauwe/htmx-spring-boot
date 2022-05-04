package io.github.wimdeblauwe.hsbt.thymeleaf;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;

import java.util.HashSet;
import java.util.Set;

public class HtmxDialect extends AbstractProcessorDialect {

    public HtmxDialect() {
        super("Htmx", "hx", 1000);
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        Set<IProcessor> htmxProcessors = new HashSet<>();

        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "get"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "post"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "put"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "delete"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "patch"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "target"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "trigger"));
        htmxProcessors.add(new HtmxAttributeProcessor(dialectPrefix, "swap"));

        return htmxProcessors;
    }
}
