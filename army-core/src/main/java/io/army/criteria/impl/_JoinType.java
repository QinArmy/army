package io.army.criteria.impl;

import io.army.criteria.SQLWords;


public enum _JoinType implements SQLWords {

    NONE(""),
    LEFT_JOIN("LEFT JOIN"),
    JOIN("JOIN"),
    RIGHT_JOIN("RIGHT JOIN"),
    FULL_JOIN("FULL JOIN"),
    CROSS_JOIN("CROSS JOIN"),

    /**
     * MySQL
     */
    STRAIGHT_JOIN("STRAIGHT_JOIN");

    private final String keyWords;

    _JoinType(String keyWords) {
        this.keyWords = keyWords;
    }


    @Override
    public final String render() {
        return this.keyWords;
    }


    @Override
    public final String toString() {
        return String.format("%s.%s", _JoinType.class.getName(), this.name());
    }



}
