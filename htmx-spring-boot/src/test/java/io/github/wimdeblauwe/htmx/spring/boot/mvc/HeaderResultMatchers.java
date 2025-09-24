package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import org.springframework.test.json.JsonAssert;
import org.springframework.test.json.JsonComparator;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.ResultMatcher;

public class HeaderResultMatchers extends org.springframework.test.web.servlet.result.HeaderResultMatchers {

    public static HeaderResultMatchers header() {
        return new HeaderResultMatchers();
    }

    public ResultMatcher json(String name, String jsonContent) {
        return this.json(name, jsonContent, JsonCompareMode.LENIENT);
    }

    public ResultMatcher json(String name, String jsonContent, JsonComparator comparator) {
        return (result) -> {
            String content = result.getResponse().getHeader(name);
            comparator.assertIsMatch(jsonContent, content);
        };
    }

    public ResultMatcher json(String name, String jsonContent, JsonCompareMode compareMode) {
        return this.json(name, jsonContent, JsonAssert.comparator(compareMode));
    }

}
