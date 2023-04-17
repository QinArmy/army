package io.army.criteria.impl;


enum QueryOperator {

    ANY(" ANY"),
    SOME(" SOME"),
    ALL(" ALL");

    final String spaceWord;

    QueryOperator(String spaceWord) {
        this.spaceWord = spaceWord;
    }

    @Override
    public final String toString() {
        return CriteriaUtils.enumToString(this);
    }


}


