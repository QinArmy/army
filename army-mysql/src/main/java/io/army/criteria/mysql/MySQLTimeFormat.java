package io.army.criteria.mysql;

import io.army.criteria.SQLWords;

public enum MySQLTimeFormat implements SQLWords {

    EUR(" 'EUR'"),
    USA(" 'USA'"),
    JIS(" 'JIS'"),
    ISO(" 'ISO'"),
    INTERNAL(" 'INTERNAL'");

    private final String word;

    MySQLTimeFormat(String word) {
        this.word = word;
    }

    @Override
    public final String render() {
        return this.word;
    }


    @Override
    public final String toString() {
        return String.format("%s.%s", MySQLTimeFormat.class.getName(), this.name());
    }


}
