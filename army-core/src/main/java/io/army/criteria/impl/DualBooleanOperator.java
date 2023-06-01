package io.army.criteria.impl;


import io.army.dialect.Database;

/**
 * Interface representing sql dual operator.
 */
enum DualBooleanOperator implements Operator.SqlDualBooleanOperator {


    EQUAL(" ="),

    /**
     * MySQL
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/comparison-operators.html#operator_equal-to">NULL-safe equal.</a>
     */
    NULL_SAFE_EQUAL(" <=>"),
    NOT_EQUAL(" !="),
    LESS(" <"),
    LESS_EQUAL(" <="),
    GREATER_EQUAL(" >="),
    GREATER(" >"),


    IN(" IN"),
    NOT_IN(" NOT IN"),


    LIKE(" LIKE"),
    NOT_LIKE(" NOT LIKE"),
    SIMILAR_TO(" SIMILAR TO"), // currently,postgre only
    NOT_SIMILAR_TO(" NOT SIMILAR TO"); // currently,postgre only


    final String spaceOperator;

    /**
     * @param spaceOperator space and sign
     */
    DualBooleanOperator(String spaceOperator) {
        this.spaceOperator = spaceOperator;
    }


    @Override
    public final String spaceRender() {
        return this.spaceOperator;
    }

    @Override
    public final Database database() {
        // no bug ,never here
        throw new UnsupportedOperationException();
    }

    @Override
    public final String toString() {
        return CriteriaUtils.enumToString(this);
    }


}
