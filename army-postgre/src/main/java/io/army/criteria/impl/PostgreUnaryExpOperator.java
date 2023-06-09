package io.army.criteria.impl;

import io.army.dialect.Database;

enum PostgreUnaryExpOperator implements Operator.SqlUnaryExpOperator {

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">|/ double precision → double precision<br/>
     * Square root
     * </a>
     */
    VERTICAL_SLASH(" |/"),
    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">||/ double precision → double precision<br/>
     * Cube root
     * </a>
     */
    DOUBLE_VERTICAL_SLASH(" ||/"),
    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">@-@ geometric_type → double precision<br/>
     * Computes the total length. Available for lseg, path.
     * </a>
     */
    AT_HYPHEN_AT(" @-@"),
    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">Absolute value operator</a>
     */
    AT(" @"),
    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">@@ geometric_type → point<br>
     * Computes the center point. Available for box, lseg, polygon, circle.
     * </a>
     */
    AT_AT(" @@"),
    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-OPERATORS-TABLE">!! tsquery → tsquery<br>
     * Negates a tsquery, producing a query that matches documents that do not match the input query.
     * </a>
     */
    DOUBLE_EXCLAMATION(" !!"),
    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE"># geometric_type → integer<br>
     * Returns the number of points. Available for path, polygon.
     * </a>
     */
    POUND(" #");

    private final String spaceOperator;

    PostgreUnaryExpOperator(String spaceOperator) {
        this.spaceOperator = spaceOperator;
    }


    @Override
    public final String spaceRender() {
        return this.spaceOperator;
    }

    @Override
    public final Database database() {
        return Database.Postgre;
    }

    @Override
    public final String toString() {
        return CriteriaUtils.enumToString(this);
    }


}
