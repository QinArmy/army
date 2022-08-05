package io.army.criteria.impl;

import io.army.criteria.SQLWords;

/**
 * <p>
 * package enum
 * </p>
 *
 * @since 1.0
 */
enum ItemWord implements SQLWords {

    LATERAL(" LATERAL"),
    ONLY(" ONLY");


    private final String word;

    ItemWord(String word) {
        this.word = word;
    }

    @Override
    public final String render() {
        return this.word;
    }


    @Override
    public final String toString() {
        return String.format("%s.%s", ItemWord.class.getSimpleName(), this.name());
    }


}
