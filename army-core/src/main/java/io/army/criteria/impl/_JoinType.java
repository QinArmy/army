package io.army.criteria.impl;

import io.army.criteria.SQLModifier;


public enum _JoinType implements SQLModifier {

    NONE(" "),
    LEFT_JOIN(" LEFT JOIN"),
    JOIN(" JOIN"),
    RIGHT_JOIN(" RIGHT JOIN"),
    FULL_JOIN(" FULL JOIN"),
    CROSS_JOIN(" CROSS JOIN"),

    /**
     * MySQL
     */
    STRAIGHT_JOIN(" STRAIGHT_JOIN");

    public final String keyWords;

    _JoinType(String keyWords) {
        this.keyWords = keyWords;
    }


    @Override
    public final String render() {
        return this.keyWords;
    }

}
