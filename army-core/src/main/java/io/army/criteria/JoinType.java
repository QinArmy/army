package io.army.criteria;

public enum JoinType {
    NONE(""),
    LEFT("LEFT JOIN"),
    JOIN("JOIN"),
    RIGHT("RIGHT JOIN");

    public final String keyWord;

    JoinType(String keyWord) {
        this.keyWord = keyWord;
    }

}
