package io.army.criteria.mysql;

import io.army.criteria.SQLWords;

public enum MySQLTimes implements SQLWords {

    TIME(" TIME"),
    DATE(" DATE"),
    DATETIME(" DATETIME");

    private final String words;

    MySQLTimes(String words) {
        this.words = words;
    }

    @Override
    public final String render() {
        return this.words;
    }


    @Override
    public final String toString() {
        return String.format("%s.%s", MySQLTimes.class.getName(), this.name());
    }


}
