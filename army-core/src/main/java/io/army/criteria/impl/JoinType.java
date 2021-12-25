package io.army.criteria.impl;

import io.army.criteria.SQLModifier;

enum JoinType implements SQLModifier {
    NONE(""),
    LEFT_JOIN("LEFT JOIN"),
    JOIN("JOIN"),
    RIGHT_JOIN("RIGHT JOIN"),
    FULL("FULL JOIN"),

    /**
     * MySQL
     */
    STRAIGHT_JOIN("STRAIGHT_JOIN");

    public final String keyWords;

    JoinType(String keyWords) {
        this.keyWords = keyWords;
    }


    @Override
    public String render() {
        return this.keyWords;
    }
}
