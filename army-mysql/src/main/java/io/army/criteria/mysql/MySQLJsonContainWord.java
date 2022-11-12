package io.army.criteria.mysql;

import io.army.criteria.SQLWords;

@Deprecated
public enum MySQLJsonContainWord implements SQLWords {

    one("'one'"),
    all("'all'");

    private final String word;

    MySQLJsonContainWord(String word) {
        this.word = word;
    }

    @Override
    public final String render() {
        return this.word;
    }


    @Override
    public final String toString() {
        return String.format("%s.%s", MySQLJsonContainWord.one, this.name());
    }


}
