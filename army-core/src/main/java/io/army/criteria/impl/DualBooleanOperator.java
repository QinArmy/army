package io.army.criteria.impl;


import io.army.dialect.Database;
import io.army.util._Exceptions;

/**
 * Interface representing sql dual operator.
 */
enum DualBooleanOperator implements Operator.SqlDualBooleanOperator {


    EQUAL(" ="),
    NOT_EQUAL(" !="),

    /**
     * MySQL
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/comparison-operators.html#operator_equal-to">NULL-safe equal.</a>
     */
    NULL_SAFE_EQUAL(" <=>"),

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
    public final String spaceRender(final Database database) {
        if (this != NULL_SAFE_EQUAL) {
            return this.spaceOperator;
        }
        final String operator;
        switch (database) {
            case MySQL:
                operator = " <=>";
                break;
            case Postgre:
            case H2:
                operator = " IS NOT DISTINCT FROM";
                break;
            default:
                throw _Exceptions.unexpectedEnum(database);
        }
        return operator;
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
