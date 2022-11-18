package io.army.criteria.impl;

/**
 * <p>
 * package enum
 * </p>
 *
 * @since 1.0
 */
enum BooleanTestOperand implements SQLs.BooleanTestOperand, SQLsSyntax.ArmyKeyWord {

    JSON(" JSON");

    final String spaceOperator;

    BooleanTestOperand(String spaceOperator) {
        this.spaceOperator = spaceOperator;
    }


    @Override
    public final String render() {
        return this.spaceOperator;
    }


    @Override
    public final String toString() {
        return CriteriaUtils.sqlWordsToString(this);
    }


}
