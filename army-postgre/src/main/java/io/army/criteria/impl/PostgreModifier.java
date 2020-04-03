package io.army.criteria.impl;

import io.army.criteria.SQLModifier;

enum PostgreModifier implements SQLModifier {

    RECURSIVE("RECURSIVE"),
    LATERAL("LATERAL"),

    INTERSECT("INTERSECT"),
    INTERSECT_ALL("INTERSECT ALL"),
    INTERSECT_DISTINCT("INTERSECT DISTINCT"),

    EXCEPT("EXCEPT"),
    EXCEPT_ALL("EXCEPT ALL"),
    EXCEPT_DISTINCT("EXCEPT DISTINCT"),
    ;

    private final String keyWords;

    PostgreModifier(String keyWords) {
        this.keyWords = keyWords;
    }

    @Override
    public String render() {
        return this.keyWords;
    }
}
