package io.army.criteria.impl;

import io.army.criteria.SQLWords;

enum UnionType implements SQLWords {
    UNION(" UNION"),
    UNION_ALL(" UNION ALL"),
    UNION_DISTINCT(" UNION DISTINCT");

    final String keyWords;

    UnionType(String keyWords) {
        this.keyWords = keyWords;
    }

    @Override
    public String render() {
        return this.keyWords;
    }


    @Override
    public final String toString() {
        return String.format("%s.%s", UnionType.class.getName(), this.name());
    }


}
