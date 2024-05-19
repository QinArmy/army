/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.criteria.postgre;

import io.army.dialect.Database;
import io.army.util._Exceptions;

enum PostgreDualBooleanOperator implements Operator.SqlDualBooleanOperator {

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
     * </a>
     */
    AT_GT(" @>"),
    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type <@ geometric_type → boolean<br/>
     * Is first object contained in or on second? Available for these pairs of types: (point, box), (point, lseg), (point, line), (point, path), (point, polygon), (point, circle), (box, box), (lseg, box), (lseg, line), (polygon, polygon), (circle, circle).
     */
    LT_AT(" <@"),
    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type &lt;&lt; geometric_type → boolean<br/>
     * Is first object strictly left of second? Available for point, box, polygon, circle.<br/>
     * inet &lt;&lt; inet → boolean<br/>
     * Is subnet strictly contained by subnet? This operator, and the next four, test for subnet inclusion. They consider only the network parts of the two<br/>
     * addresses (ignoring any bits to the right of the netmasks) and determine whether one network is identical to or a subnet of the other. <br/>
     * inet '192.168.1.5'  &lt;&lt; inet '192.168.1/24' → t <br/>
     * inet '192.168.0.5'  &lt;&lt; inet '192.168.1/24' → f<br/>
     * inet '192.168.1/24' &lt;&lt; inet '192.168.1/24' → f
     * </a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#CIDR-INET-OPERATORS-TABLE">inet &lt;&lt; inet → boolean<br/>
     * Is subnet strictly contained by subnet? This operator, and the next four, test for subnet inclusion. They consider only the network parts of the two<br/>
     * addresses (ignoring any bits to the right of the netmasks) and determine whether one network is identical to or a subnet of the other. <br/>
     * inet '192.168.1.5'  &lt;&lt; inet '192.168.1/24' → t <br/>
     * inet '192.168.0.5'  &lt;&lt; inet '192.168.1/24' → f<br/>
     * inet '192.168.1/24' &lt;&lt; inet '192.168.1/24' → f
     * </a>
     */
    LT_LT(" <<"),
    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type >> geometric_type → boolean<br/>
     * Is first object strictly right of second? Available for point, box, polygon, circle.</a>
     */
    GT_GT(" >>"),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#CIDR-INET-OPERATORS-TABLE">inet &lt;&lt;= inet → boolean<br/>
     * Is subnet contained by or equal to subnet?<br/>
     * inet '192.168.1/24' &lt;&lt;= inet '192.168.1/24' → t
     * </a>
     */
    LT_LT_EQUAL(" <<="),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#CIDR-INET-OPERATORS-TABLE">inet >>= inet → boolean<br/>
     * Does subnet contain or equal subnet?<br/>
     * inet '192.168.1/24' >>= inet '192.168.1/24' → t
     * </a>
     */
    GT_GT_EQUAL(" >>="),
    /**
     * double ampersand
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type &amp;&amp; geometric_type → boolean<br/>
     * Do these objects overlap? (One point in common makes this true.) Available for box, polygon, circle.</a>
     */
    DOUBLE_AMP(" &&"),
    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type &amp;&lt; geometric_type → boolean<br/>
     * Does first object not extend to the right of second? Available for box, polygon, circle.</a>
     */
    AMP_LT(" &<"),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type &amp;&gt; geometric_type → boolean<br/>
     * Does first object not extend to the left of second? Available for box, polygon, circle.</a>
     */
    AMP_GT(" &>"),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type &lt;&lt;| geometric_type → boolean<br/>
     * Is first object strictly below second? Available for point, box, polygon, circle.</a>
     */
    LT_LT_VERTICAL(" <<|"),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type |>> geometric_type → boolean<br/>
     * Is first object strictly above second? Available for point, box, polygon, circle.</a>
     */
    VERTICAL_GT_GT(" |>>"),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type &amp;&lt;| geometric_type → boolean<br/>
     * Does first object not extend above second? Available for box, polygon, circle.</a>
     */
    AMP_LT_VERTICAL(" &<|"),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type |&amp;> geometric_type → boolean<br/>
     * Does first object not extend below second? Available for box, polygon, circle.</a>
     */
    VERTICAL_AMP_GT(" |&>"),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">box &lt;^ box → boolean<br/>
     * Is first object below second (allows edges to touch)?</a>
     */
    LT_CARET(" <^"),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">box >^ box → boolean<br/>
     * Is first object above second (allows edges to touch)?</a>
     */
    GT_CARET(" >^"),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSONB-OP-TABLE">jsonb ? text → boolean<br/>
     * Does the text string exist as a top-level key or array element within the JSON value?<br/>
     * '{"a":1, "b":2}'::jsonb ? 'b' → t<br/>
     * '["a", "b", "c"]'::jsonb ? 'b' → t
     * </a>
     */
    QUESTION(" ?"),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type ?# geometric_type → boolean<br/>
     * Is first object above second (allows edges to touch)?</a>
     */
    QUESTION_POUND(" ?#"),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">point ?- point → boolean<br/>
     * Are points horizontally aligned (that is, have same y coordinate)?</a>
     */
    QUESTION_HYPHEN(" ?-"),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSONB-OP-TABLE">jsonb ?& text[] → boolean<br/>
     * Do all of the strings in the text array exist as top-level keys or array elements?<br/>
     * '["a", "b", "c"]'::jsonb ?& array['a', 'b'] → t
     * </a>
     */
    QUESTION_AMP(" ?&"),


    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">point ?| point → boolean<br/>
     * Are points vertically aligned (that is, have same x coordinate)?</a>
     */
    QUESTION_VERTICAL(" ?|"),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">line ?-| line → boolean<br/>
     * lseg ?-| lseg → boolean</a>
     */
    QUESTION_HYPHEN_VERTICAL(" ?-|"),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">line ?|| line → boolean<br/>
     * lseg ?|| lseg → boolean</a>
     */
    QUESTION_VERTICAL_VERTICAL(" ?||"),


    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type ~= geometric_type → boolean<br/>
     * Are these objects the same? Available for point, box, polygon, circle.<br/>
     * </a>
     */
    TILDE_EQUAL(" ~="),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-range.html#RANGE-OPERATORS-TABLE">anyrange -|- anyrange → boolean<br/>
     * Are the ranges adjacent?.<br/>
     * numrange(1.1,2.2) -|- numrange(2.2,3.3) → t
     * </a>
     */
    HYPHEN_VERTICAL_HYPHEN(" -|-"),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSONB-OP-TABLE">jsonb @? jsonpath → boolean<br/>
     * Does JSON path return any item for the specified JSON value?<br/>
     * '{"a":[1,2,3,4,5]}'::jsonb @? '$.a[*] ? (@ > 2)' → t
     * </a>
     */
    AT_QUESTION(" @?"),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-OPERATORS-TABLE">tsvector @@ tsquery → boolean<br/>
     * tsquery @@ tsvector → boolean<br/>
     * text @@ tsquery → boolean<br/>
     * </a>
     */
    DOUBLE_AT(" @@"),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-OPERATORS-TABLE">tsvector @@@ tsquery → boolean<br/>
     * tsquery @@@ tsvector → boolean<br/>
     * </a>
     */
    TRIPLE_AT(" @@@"),

    /**
     * @see <a href="https://www.postgresql.org/docs/15/functions-comparisons.html#ROW-WISE-COMPARISON">row_constructor IS DISTINCT FROM row_constructor<br/>
     * </a>
     */
    IS_DISTINCT_FROM(" IS DISTINCT FROM"),

    /**
     * @see <a href="https://www.postgresql.org/docs/15/functions-comparisons.html#ROW-WISE-COMPARISON">row_constructor IS NOT DISTINCT FROM row_constructor<br/>
     * </a>
     */
    IS_NOT_DISTINCT_FROM(" IS NOT DISTINCT FROM");

    private final String spaceOperator;

    PostgreDualBooleanOperator(String spaceOperator) {
        this.spaceOperator = spaceOperator;
    }

    @Override
    public final String spaceRender() {
        return this.spaceOperator;
    }

    @Override
    public final String spaceRender(final Database database) {
        if (database != Database.PostgreSQL) {
            throw _Exceptions.operatorError(this, database);
        }
        return this.spaceOperator;
    }


    @Override
    public final String toString() {
        return CriteriaUtils.enumToString(this);
    }





}
