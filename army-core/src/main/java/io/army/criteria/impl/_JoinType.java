package io.army.criteria.impl;

import io.army.criteria.SQLWords;
import io.army.util._StringUtils;


public enum _JoinType implements SQLWords {

    NONE(""),
    LEFT_JOIN(" LEFT JOIN"),
    JOIN(" JOIN"),
    RIGHT_JOIN(" RIGHT JOIN"),
    FULL_JOIN(" FULL JOIN"),
    CROSS_JOIN(" CROSS JOIN"),

    /**
     * MySQL
     */
    STRAIGHT_JOIN(" STRAIGHT_JOIN");

    public final String spaceWords;

    _JoinType(String spaceWords) {
        this.spaceWords = spaceWords;
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
