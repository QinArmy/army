package io.army.criteria.impl;

enum AssignOperator {

    PLUS_EQUAL(" +="),
    MINUS_EQUAL(" -=");

    final String text;

    AssignOperator(String text) {
        this.text = text;
    }


}
