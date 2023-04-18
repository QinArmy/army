package io.army.criteria.impl;

import io.army.dialect.Database;

enum PostgreBooleanDualOperator implements Operator.BooleanDualOperator {

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">text ^@ text → boolean</a>
     */
    CARET_AT(" ^@"),
    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-matching.html#FUNCTIONS-POSIX-TABLE">text ~ text → boolean<br/>
     * String matches regular expression, case sensitively</a>
     */
    TILDE(" ~"),
    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-matching.html#FUNCTIONS-POSIX-TABLE">text !~ text → boolean<br/>
     * String does not match regular expression, case sensitively</a>
     */
    NOT_TILDE(" !~"),
    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-matching.html#FUNCTIONS-POSIX-TABLE">text ~* text → boolean<br/>
     * String matches regular expression, case insensitively</a>
     */
    TILDE_STAR(" ~*"),
    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-matching.html#FUNCTIONS-POSIX-TABLE">text !~* text → boolean<br/>
     * String does not match regular expression, case insensitively</a>
     */
    NOT_TILDE_STAR(" !~*"),
    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type @> geometric_type → boolean<br/>
     * Does first object contain second? Available for these pairs of types: (box, point), (box, box), (path, point), (polygon, point), (polygon, polygon), (circle, point), (circle, circle).
     */
    AT_GT(" @>"),
    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type <@ geometric_type → boolean<br/>
     * Is first object contained in or on second? Available for these pairs of types: (point, box), (point, lseg), (point, line), (point, path), (point, polygon), (point, circle), (box, box), (lseg, box), (lseg, line), (polygon, polygon), (circle, circle).
     */
    LT_AT(" <@"),
    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type &lt;&lt; geometric_type → boolean<br/>
     * Is first object strictly left of second? Available for point, box, polygon, circle.
     */
    LT_LT(" <<"),
    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type >> geometric_type → boolean<br/>
     * Is first object strictly right of second? Available for point, box, polygon, circle.
     */
    GT_GT(" >>"),
    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type &amp;&amp; geometric_type → boolean<br/>
     * Do these objects overlap? (One point in common makes this true.) Available for box, polygon, circle.
     */
    AMP_AMP(" &&"),
    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type &amp;&lt; geometric_type → boolean<br/>
     * Does first object not extend to the right of second? Available for box, polygon, circle.
     */
    AMP_LT(" &<"),
    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type &lt;&lt;| geometric_type → boolean<br/>
     * Is first object strictly below second? Available for point, box, polygon, circle.
     */
    LT_LT_VERTICAL(" <<|"),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type |>> geometric_type → boolean<br/>
     * Is first object strictly above second? Available for point, box, polygon, circle.
     */
    VERTICAL_GT_GT(" |>>"),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type &amp;&lt;| geometric_type → boolean<br/>
     * Does first object not extend above second? Available for box, polygon, circle.
     */
    AMP_LT_VERTICAL(" &<|"),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type |&amp;> geometric_type → boolean<br/>
     * Does first object not extend below second? Available for box, polygon, circle.
     */
    VERTICAL_AMP_GT(" |&>"),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">box &lt;^ box → boolean<br/>
     * Is first object below second (allows edges to touch)?
     */
    LT_CARET(" <^"),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">box >^ box → boolean<br/>
     * Is first object above second (allows edges to touch)?
     */
    GT_CARET(" >^"),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type ?# geometric_type → boolean<br/>
     * Is first object above second (allows edges to touch)?
     */
    QUESTION_POUND(" ?#"),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">point ?- point → boolean<br/>
     * Are points horizontally aligned (that is, have same y coordinate)?
     */
    QUESTION_HYPHEN(" ?-"),


    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">point ?| point → boolean<br/>
     * Are points vertically aligned (that is, have same x coordinate)?
     */
    QUESTION_VERTICAL(" ?|"),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">line ?-| line → boolean<br/>
     * lseg ?-| lseg → boolean
     */
    QUESTION_HYPHEN_VERTICAL(" ?-|"),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">line ?|| line → boolean<br/>
     * lseg ?|| lseg → boolean
     */
    QUESTION_VERTICAL_VERTICAL(" ?||"),


    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type ~= geometric_type → boolean<br/>
     * Are these objects the same? Available for point, box, polygon, circle.<br/>
     * </a>
     */
    TILDE_EQUAL(" ~="),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type &amp;&gt; geometric_type → boolean<br/>
     * Does first object not extend to the left of second? Available for box, polygon, circle.
     */
    AMP_GT(" &>");
    private final String spaceOperator;

    PostgreBooleanDualOperator(String spaceOperator) {
        this.spaceOperator = spaceOperator;
    }

    @Override
    public final String spaceRender() {
        return this.spaceOperator;
    }

    @Override
    public final Database database() {
        return Database.PostgreSQL;
    }


    @Override
    public final String toString() {
        return CriteriaUtils.enumToString(this);
    }


}
