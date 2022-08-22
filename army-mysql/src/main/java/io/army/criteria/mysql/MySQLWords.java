package io.army.criteria.mysql;

import io.army.criteria.SQLWords;

public enum MySQLWords implements SQLWords {

    TIME(" TIME"),
    DATE(" DATE"),
    DATETIME(" DATETIME"),

    BOTH(" BOTH"),
    LEADING(" LEADING"),
    TRAILING(" TRAILING"),

    CHAR(" CHAR"),
    BINARY(" BINARY");

    private final String words;

    MySQLWords(String words) {
        this.words = words;
    }

    @Override
    public final String render() {
        return this.words;
    }


    @Override
    public final String toString() {
        return String.format("%s.%s", MySQLWords.class.getName(), this.name());
    }


}
