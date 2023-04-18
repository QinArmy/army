package io.army.criteria.impl;

enum PostgreBooleanUnaryOperator {

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">?- line → boolean<br/>
     * ?- lseg → boolean<br/>
     * Is line horizontal?
     */
    QUESTION_HYPHEN(" ?-"),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">?| line → boolean<br/>
     * ?| lseg → boolean<br/>
     * Is line vertical?
     */
    QUESTION_VERTICAL(" ?|");

    final String spaceOperator;

    PostgreBooleanUnaryOperator(String spaceOperator) {
        this.spaceOperator = spaceOperator;
    }

    @Override
    public final String toString() {
        return CriteriaUtils.enumToString(this);
    }


}
