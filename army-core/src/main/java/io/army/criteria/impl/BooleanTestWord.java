package io.army.criteria.impl;

/**
 * <p>
 * package enum
 * </p>
 *
 * @since 1.0
 */
enum BooleanTestWord implements SQLsSyntax.BooleanTestWord, Functions.ArmyKeyWord {

    JSON(" JSON");

    final String spaceOperator;

    BooleanTestWord(String spaceOperator) {
        this.spaceOperator = spaceOperator;
    }


    @Override
    public final String spaceRender() {
        return this.spaceOperator;
    }


    @Override
    public final String toString() {
        return CriteriaUtils.enumToString(this);
    }


}
