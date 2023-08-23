package io.github.wimdeblauwe.hsbt.mvc;

public enum HxSwapType {

    INNER_HTML("innerHTML"),
    OUTER_HTML("outerHTML"),
    BEFORE_BEGIN("beforebegin"),
    AFTER_BEGIN("afterbegin"),
    BEFORE_END("beforeend"),
    AFTER_END("afterend"),
    DELETE("delete"),
    NONE("none");


    private final String value;

    HxSwapType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
