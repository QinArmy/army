package io.army.criteria.impl;

import io.army.criteria.SQLWords;
import io.army.util._StringUtils;

public enum _UnionType implements SQLWords {
    UNION(" UNION"),
    UNION_ALL(" UNION ALL"),
    UNION_DISTINCT(" UNION DISTINCT"),

    INTERSECT(" INTERSECT"),
    INTERSECT_ALL(" INTERSECT ALL"),
    INTERSECT_DISTINCT(" INTERSECT DISTINCT"),

    EXCEPT(" EXCEPT"),
    EXCEPT_ALL(" EXCEPT ALL"),
    EXCEPT_DISTINCT(" EXCEPT DISTINCT"),

    MINUS(" MINUS"),
    MINUS_ALL(" MINUS ALL"),
    MINUS_DISTINCT(" MINUS DISTINCT");

    private final String spaceWords;

    _UnionType(String keyWords) {
        this.spaceWords = keyWords;
    }


    @Override
    public final String spaceRender() {
        return this.spaceWords;
    }


    @Override
    public final String toString() {
        return _StringUtils.enumToString(this);
    }



}
