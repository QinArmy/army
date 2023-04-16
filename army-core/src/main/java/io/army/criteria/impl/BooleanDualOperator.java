package io.army.criteria.impl;


/**
 * Interface representing sql dual operator.
 */
enum BooleanDualOperator implements Operator.DualOperator {

    EQUAL(" ="),
    NOT_EQUAL(" !="),
    LESS(" <"),
    LESS_EQUAL(" <="),
    GREAT_EQUAL(" >="),
    GREAT(" >"),


    IN(" IN"),
    NOT_IN(" NOT IN"),


    LIKE(" LIKE"),
    NOT_LIKE(" NOT LIKE"),
    SIMILAR_TO(" SIMILAR TO"), // currently,postgre only
    NOT_SIMILAR_TO(" NOT SIMILAR TO"), // currently,postgre only

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">text ^@ text → boolean</a>
     */
    CARET_AT(" ^@"), // postgre only

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-matching.html#FUNCTIONS-POSIX-TABLE">text ~ text → boolean<br/>
     * String matches regular expression, case sensitively</a>
     */
    TILDE(" ~"),// postgre only

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-matching.html#FUNCTIONS-POSIX-TABLE">text !~ text → boolean<br/>
     * String does not match regular expression, case sensitively</a>
     */
    NOT_TILDE(" !~"),// postgre only

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-matching.html#FUNCTIONS-POSIX-TABLE">text ~* text → boolean<br/>
     * String matches regular expression, case insensitively</a>
     */
    TILDE_STAR(" ~*"),// postgre only

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-matching.html#FUNCTIONS-POSIX-TABLE">text !~* text → boolean<br/>
     * String does not match regular expression, case insensitively</a>
     */
    NOT_TILDE_STAR(" !~*");// postgre only

    final String spaceOperator;

    /**
     * @param spaceOperator space and sign
     */
    BooleanDualOperator(String spaceOperator) {
        this.spaceOperator = spaceOperator;
    }


    @Override
    public final int precedence() {
        return 0;
    }


    @Override
    public final String toString() {
        return CriteriaUtils.enumToString(this);
    }


}
