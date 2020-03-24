package io.army.criteria.impl;

enum JoinType {
    NONE(""),
    LEFT("LEFT JOIN"),
    JOIN("JOIN"),
    RIGHT("RIGHT JOIN"),
    FULL("FULL JOIN");

    public final String keyWord;

    JoinType(String keyWord) {
        this.keyWord = keyWord;
    }

}
