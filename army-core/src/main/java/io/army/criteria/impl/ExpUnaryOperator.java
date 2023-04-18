package io.army.criteria.impl;


/**
 * representing Unary SQL Operator
 *
 * @see BooleanUnaryOperator
 */
enum ExpUnaryOperator implements Operator.SqlUnaryOperator {

    NEGATE(" -"),
    POSITIVE(" +"),
    BITWISE_NOT(" ~"),

    // postgre only

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">|/ double precision → double precision<br/>
     * Square root
     * </a>
     */
    VERTICAL_SLASH(" |/"), // postgre only

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">||/ double precision → double precision<br/>
     * Cube root
     * </a>
     */
    DOUBLE_VERTICAL_SLASH(" ||/"),// postgre only

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">@-@ geometric_type → double precision<br/>
     * Computes the total length. Available for lseg, path.
     * </a>
     */
    AT_HYPHEN_AT(" @-@"),
    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">Absolute value operator</a>
     */
    AT(" @"),// postgre only

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">@@ geometric_type → point<br>
     * Computes the center point. Available for box, lseg, polygon, circle.
     * </a>
     */
    AT_AT(" @@"),// postgre only

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE"># geometric_type → integer<br>
     * Returns the number of points. Available for path, polygon.
     * </a>
     */
    POUND(" #");// postgre only


    final String spaceOperator;

    ExpUnaryOperator(String spaceOperator) {
        this.spaceOperator = spaceOperator;
    }


    @Override
    public final String toString() {
        return CriteriaUtils.enumToString(this);
    }


}
