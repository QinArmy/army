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

package io.army.criteria.impl;

import io.army.dialect.Database;

 enum DualExpOperator implements Operator.SqlDualExpressionOperator {

     /**
      * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">numeric ^ numeric → numeric <br/>
      * double precision ^ double precision → double precision <br/>
      * Exponentiation</a>
      * @see <a href="https://www.postgresql.org/docs/15/sql-syntax-lexical.html#SQL-PRECEDENCE-TABLE">Operator Precedence (highest to lowest) </a>
      */
     EXPONENTIATION(" ^", 90),// postgre only

     BITWISE_XOR(" ^", 85),  // for MySQL , BITWISE_XOR > TIMES

     MOD(" %", 80),
     TIMES(" *", 80),
     DIVIDE(" /", 80),

     /**
      * Integer division. Discards from the division result any fractional part to the right of the decimal point.
      *
      * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/arithmetic-functions.html#operator_div">MySQL Integer division</a>
      */
     DIV(" DIV", 80),

     PLUS(" +", 70),
     MINUS(" -", 70),

     LEFT_SHIFT(" <<", 60),
     RIGHT_SHIFT(" >>", 60),

     BITWISE_AND(" &", 40),
     BITWISE_OR(" |", 30),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-SQL">bytea || bytea → bytea</a>
     */
    DOUBLE_VERTICAL(" ||", 20),// postgre only
    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-ZONECONVERT-TABLE"> AT TIME ZONE Variants</a>
     */
    AT_TIME_ZONE(" AT TIME ZONE", 20),// postgre only

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type # geometric_type → point<br>
     * Computes the point of intersection, or NULL if there is none. Available for lseg, line.
     * </a>
     */
    POUND(" #", 20),// postgre only

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING">json #> text[] → json<br>
     * jsonb #> text[] → jsonb<br/>
     * Extracts JSON sub-object at the specified path, where path elements can be either field keys or array indexes.
     * '{"a": {"b": ["foo","bar"]}}'::json #> '{a,b,1}' → "bar"
     * </a>
     */
    POUND_GT(" #>", 20),// postgre only

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSONB-OP-TABLE">jsonb #- text[] → jsonb<br>
     * Deletes the field or array element at the specified path, where path elements can be either field keys or array indexes.<br/>
     * '["a", {"b":1}]'::jsonb #- '{1,b}' → ["a", {}]
     * </a>
     */
    POUND_HYPHEN(" #-", 20),// postgre only

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING">json #>> text[] → text<br>
     * jsonb #>> text[] → text<br/>
     * Extracts JSON sub-object at the specified path as text.
     * '{"a": {"b": ["foo","bar"]}}'::json #>> '{a,b,1}' → bar
     * </a>
     */
    POUND_GT_GT(" #>>", 20),// postgre only
    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type ## geometric_type → point<br>
     * Computes the closest point to the first object on the second object. Available for these pairs of types: (point, box), (point, lseg), (point, line), (lseg, box), (lseg, lseg), (line, lseg).
     * </a>
     */
    POUND_POUND(" ##", 20),// postgre only

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-OPERATORS-TABLE">tsquery && tsquery → tsquery<br>
     * ANDs two tsquerys together, producing a query that matches documents that match both input queries.
     * </a>
     */
    DOUBLE_AMP(" &&", 20),// postgre only

     /**
      * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING">json -> integer → json<br>
      * jsonb -> integer → jsonb<br/>
      * Extracts n'th element of JSON array (array elements are indexed from zero, but negative integers count from the end).<br/>
      * '[{"a":"foo"},{"b":"bar"},{"c":"baz"}]'::json -> 2 → {"c":"baz"}
      * </a>
      */
     HYPHEN_GT(" ->", 20),// postgre mysql

     /**
      * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING">json ->> integer → text<br>
      * jsonb ->> integer → text<br/>
      * Extracts n'th element of JSON array, as text.<br/>
      * '[1,2,3]'::json ->> 2 → 3<br/>
      * json ->> text → text<br/>
      * jsonb ->> text → text<br/>
      * </a>
      */
    HYPHEN_GT_GT(" ->>", 20),// postgre only

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type &lt;-> geometric_type → double precision<br/>
     * Computes the distance between the objects. Available for all seven geometric types, for all combinations of point with another geometric type, and for these additional pairs of types: (box, lseg), (lseg, line), (polygon, circle) (and the commutator cases).
     * </a>
     */
    LT_HYPHEN_GT(" <->", 20);// postgre only


    final String spaceOperator;

    final byte precedenceValue;

    DualExpOperator(String spaceOperator, int precedenceValue) {
        assert precedenceValue <= Byte.MAX_VALUE;
        this.spaceOperator = spaceOperator;
        this.precedenceValue = (byte) precedenceValue;
    }


    @Override
    public final String spaceRender() {
        return this.spaceOperator;
    }

    @Override
    public final int precedence() {
        return this.precedenceValue;
    }

    @Override
    public final Database database() {
        // no bug,never here
        throw new UnsupportedOperationException();
    }


    @Override
    public final String toString() {
        return CriteriaUtils.enumToString(this);
    }





}


