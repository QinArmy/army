package io.army.criteria.impl;

/**
 * <p>
 * package enum
 * </p>
 *
 * @since 1.0
 */
enum BooleanTestKeyWord implements SQLsSyntax.BooleanTestWord, Functions.ArmyKeyWord {

    JSON(" JSON"),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-PREDICATES">xml IS DOCUMENT → boolean<br/>
     * </a>
     */
    DOCUMENT(" DOCUMENT"); // postgre xml

    final String spaceOperator;

    BooleanTestKeyWord(String spaceOperator) {
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
