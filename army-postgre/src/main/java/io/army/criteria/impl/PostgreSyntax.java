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


import io.army.criteria.*;
import io.army.dialect.PostgreDialect;
import io.army.dialect._Constant;
import io.army.mapping.*;
import io.army.mapping.array.TextArrayType;
import io.army.mapping.optional.NoCastTextType;
import io.army.meta.FieldMeta;
import io.army.util.ArrayUtils;
import io.army.util._StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * Package class
 *
 * @since 0.6.0
 */
abstract class PostgreSyntax extends PostgreWindowFunctions {

    /**
     * Package constructor
     */
    PostgreSyntax() {
    }


    /**
     * <p>
     * Static array constructor, array is {@link TextArrayType#LINEAR} type.
     *
     * @see <a href="https://www.postgresql.org/docs/15/sql-expressions.html#SQL-SYNTAX-ARRAY-CONSTRUCTORS">Array Constructors</a>
     */
    public static SQLs._ArrayConstructorSpec array() {
        return Expressions.array();
    }

    /**
     * <p>
     * Static array constructor
     *
     * @see <a href="https://www.postgresql.org/docs/15/sql-expressions.html#SQL-SYNTAX-ARRAY-CONSTRUCTORS">Array Constructors</a>
     */
    public static SQLs._ArrayConstructorSpec array(Object element) {
        SQLs._ArrayConstructorSpec array;
        if (element instanceof SubQuery) {
            array = Expressions.array((SubQuery) element);
        } else {
            array = Expressions.array(Expressions::nonNullFirstArrayType, Collections.singletonList(element));
        }
        return array;
    }


    /**
     * <p>
     * Static array constructor, array is {@link TextArrayType#LINEAR} type.
     *
     * @see <a href="https://www.postgresql.org/docs/15/sql-expressions.html#SQL-SYNTAX-ARRAY-CONSTRUCTORS">Array Constructors</a>
     */
    public static SQLs._ArrayConstructorSpec array(Object one, Object two) {
        return Expressions.array(Expressions::nonNullFirstArrayType, ArrayUtils.of(one, two));
    }

    /**
     * <p>
     * Static array constructor, array is {@link TextArrayType#LINEAR} type.
     *
     * @see <a href="https://www.postgresql.org/docs/15/sql-expressions.html#SQL-SYNTAX-ARRAY-CONSTRUCTORS">Array Constructors</a>
     */
    public static SQLs._ArrayConstructorSpec array(Object one, Object two, Object three) {
        return Expressions.array(Expressions::nonNullFirstArrayType, ArrayUtils.of(one, two, three));
    }

    /**
     * <p>
     * Static array constructor, array is {@link TextArrayType#LINEAR} type.
     *
     * @see <a href="https://www.postgresql.org/docs/15/sql-expressions.html#SQL-SYNTAX-ARRAY-CONSTRUCTORS">Array Constructors</a>
     */
    public static SQLs._ArrayConstructorSpec array(Object one, Object two, Object three, Object four) {
        return Expressions.array(Expressions::nonNullFirstArrayType,
                ArrayUtils.of(one, two, three, four)
        );
    }

    /**
     * <p>
     * Static array constructor, array is {@link TextArrayType#LINEAR} type.
     *
     * @see <a href="https://www.postgresql.org/docs/15/sql-expressions.html#SQL-SYNTAX-ARRAY-CONSTRUCTORS">Array Constructors</a>
     */
    public static SQLs._ArrayConstructorSpec array(Object one, Object two, Object three, Object four, Object five,
                                                   Object... rest) {
        return Expressions.array(Expressions::nonNullFirstArrayType,
                ArrayUtils.of(one, two, three, four, five, rest)
        );
    }

    /**
     * <p>
     * Dynamic array constructor, if empty,then array is {@link TextArrayType#LINEAR} type.
     *
     * @see <a href="https://www.postgresql.org/docs/15/sql-expressions.html#SQL-SYNTAX-ARRAY-CONSTRUCTORS">Array Constructors</a>
     */
    public static SQLs._ArrayConstructorSpec array(Consumer<Consumer<Object>> consumer) {
        return Expressions.array(Expressions::nonNullFirstArrayType, consumer);
    }


    public static Expression excluded(FieldMeta<?> field) {
        return ContextStack.peek().insertValueField(field, PostgreExcludedField::excludedField);
    }


    /**
     * @param expression couldn't be multi-value parameter/literal, for example {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/15/sql-syntax-calling-funcs.html#SQL-SYNTAX-CALLING-FUNCS-POSITIONAL">Using Positional Notation</a>
     * @see <a href="https://www.postgresql.org/docs/15/sql-syntax-calling-funcs.html#SQL-SYNTAX-CALLING-FUNCS-NAMED">Using Named Notation</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">make_interval ( [ years int [, months int [, weeks int [, days int [, hours int [, mins int [, secs double precision ]]]]]]] ) → interval</a>
     */
    public static Expression namedNotation(String name, Expression expression) {
        return FunctionUtils.namedNotation(name, expression);
    }

    /**
     * @param valueOperator couldn't return multi-value parameter/literal, for example {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/15/sql-syntax-calling-funcs.html#SQL-SYNTAX-CALLING-FUNCS-POSITIONAL">Using Positional Notation</a>
     * @see <a href="https://www.postgresql.org/docs/15/sql-syntax-calling-funcs.html#SQL-SYNTAX-CALLING-FUNCS-NAMED">Using Named Notation</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">make_interval ( [ years int [, months int [, weeks int [, days int [, hours int [, mins int [, secs double precision ]]]]]]] ) → interval</a>
     */
    public static <T> Expression namedNotation(String name, Function<T, Expression> valueOperator, T value) {
        return FunctionUtils.namedNotation(name, valueOperator.apply(value));
    }


    /**
     * <p>
     * create empty row. non-empty row see {@link SQLs#row(Object)}
     *
     * @see SQLs#row(Object)
     * @see SQLs#row(SubQuery)
     * @see SQLs#row(Consumer)
     * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SQL-SYNTAX-ROW-CONSTRUCTORS">Row Constructors</a>
     */
    public static RowExpression row() {
        return RowExpressions.emptyRow();
    }




    /*-------------------below operator method -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of exp
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">Absolute value operator</a>
     */
    public static Expression at(Expression operand) {
        return PostgreExpressions.unaryExpression(PostgreUnaryExpOperator.AT, operand, Expressions::identityType);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">@-@ geometric_type → double precision<br/>
     * Computes the total length. Available for lseg, path.
     * </a>
     */
    public static Expression atHyphenAt(Expression operand) {
        return PostgreExpressions.unaryExpression(PostgreUnaryExpOperator.AT_HYPHEN_AT, operand, PostgreExpressions::atHyphenAtType);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">@@ geometric_type → point<br>
     * Computes the center point. Available for box, lseg, polygon, circle.
     * </a>
     */
    public static Expression atAt(Expression operand) {
        return PostgreExpressions.unaryExpression(PostgreUnaryExpOperator.AT_AT, operand, PostgreExpressions::atAtType);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE"># geometric_type → integer<br>
     * Returns the number of points. Available for path, polygon.
     * </a>
     */
    public static Expression pound(Expression operand) {
        return PostgreExpressions.unaryExpression(PostgreUnaryExpOperator.POUND, operand, PostgreExpressions::unaryPoundType);
    }

    /*-------------------below dual operator -------------------*/


    /**
     * <p>Create PostgreSQL-style typecast expression. Format : 'string'::type .
     * This method is used for postgre dialect type, for example : text, regclass .
     *
     * <p><strong>NOTE</strong>: {@link LiteralExpression#typeMeta()} always is {@link NoCastTextType#INSTANCE}
     *
     * <p>examples :
     * <pre><br/>
     *        Postgres.space("my_seq",Postgres.DOUBLE_COLON,"regclass")
     *        Postgres.space('my_seq',DOUBLE_COLON,"regclass")
     *        Postgres.space('QinArmy',DOUBLE_COLON,"text")
     *     </pre>
     *
     * @param literal     text literal
     * @param doubleColon must be {@link Postgres#DOUBLE_COLON}
     * @param typeName    not key word , a simple sql identifier.
     * @return a {@link LiteralExpression} whose {@link LiteralExpression#typeMeta()} always is {@link NoCastTextType#INSTANCE}
     * @throws CriteriaException throw when <ul>
     *                           <li>literal error,here is delay , throw when parsing</li>
     *                           <li>typeName error,here is delay , throw when parsing</li>
     *                           <li>dialect isn't {@link PostgreDialect},here is delay , throw when parsing</li>
     *                           </ul>
     */
    public static LiteralExpression space(String literal, Postgres.DoubleColon doubleColon, String typeName) {
        if (doubleColon != Postgres.DOUBLE_COLON) {
            throw CriteriaUtils.errorSymbol(doubleColon);
        }
        return PostgreDoubleColonCastExpression.cast(literal, typeName);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type # geometric_type → point<br>
     * Computes the point of intersection, or NULL if there is none. Available for lseg, line.
     * </a>
     */
    public static CompoundExpression pound(Expression left, Expression right) {
        return Expressions.dialectDualExp(left, DualExpOperator.POUND, right, PostgreExpressions::dualPoundType);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type &lt;-> geometric_type → double precision<br/>
     * Computes the distance between the objects. Available for all seven geometric types, for all combinations of point with another geometric type, and for these additional pairs of types: (box, lseg), (lseg, line), (polygon, circle) (and the commutator cases).
     * </a>
     */
    public static CompoundExpression ltHyphenGt(Expression left, Expression right) {
        return Expressions.dialectDualExp(left, DualExpOperator.LT_HYPHEN_GT, right, PostgreExpressions::ltHyphenGtType);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">numeric_type + numeric_type → numeric_type</a>
     */
    public static CompoundExpression plus(Expression left, Expression right) {
        return Expressions.dialectDualExp(left, DualExpOperator.PLUS, right, PostgreExpressions::plusType);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">numeric_type - numeric_type → numeric_type<br/>
     * Subtraction</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSONB-OP-TABLE">jsonb - text → jsonb<br/>
     * jsonb - text[] → jsonb<br/>
     * jsonb - integer → jsonb<br/>
     * </a>
     */
    public static CompoundExpression minus(Expression left, Expression right) {
        return Expressions.dialectDualExp(left, DualExpOperator.MINUS, right, PostgreExpressions::minusType);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">numeric_type * numeric_type → numeric_type<br/>
     * Multiplication</a>
     */
    public static CompoundExpression times(Expression left, Expression right) {
        return Expressions.dialectDualExp(left, DualExpOperator.TIMES, right, PostgreExpressions::timesType);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">numeric_type / numeric_type → numeric_type<br/>
     * Division (for integral types, division truncates the result towards zero)</a>
     */
    public static CompoundExpression divide(Expression left, Expression right) {
        return Expressions.dialectDualExp(left, DualExpOperator.DIVIDE, right, PostgreExpressions::divideType);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">numeric_type % numeric_type → numeric_type<br/>
     * Modulo (remainder); available for smallint, integer, bigint, and numeric</a>
     */
    public static CompoundExpression mode(Expression left, Expression right) {
        return Expressions.dialectDualExp(left, DualExpOperator.MOD, right, Expressions::timesDivideType);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: follow <pre><code><br>
     *    private static MappingType caretResultType(final MappingType left, final MappingType right) {
     *        final MappingType returnType;
     *        if (left instanceof MappingType.IntegerOrDecimalType
     *                &amp;&amp; right instanceof MappingType.IntegerOrDecimalType) {
     *            returnType = BigDecimalType.INSTANCE;
     *        } else {
     *            returnType = DoubleType.INSTANCE;
     *        }
     *        return returnType;
     *    }
     * </code></pre>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">numeric ^ numeric → numeric <br/>
     * double precision ^ double precision → double precision <br/>
     * Exponentiation</a>
     */
    public static CompoundExpression caret(final Expression left, final Expression right) {
        return Expressions.dialectDualExp(left, DualExpOperator.EXPONENTIATION, right, PostgreSyntax::caretResultType);
    }


    /**
     * @see #doubleAmp(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-OPERATORS-TABLE">tsquery &amp;&amp; tsquery → tsquery<br>
     * ANDs two tsquerys together, producing a query that matches documents that match both input queries.
     * </a>
     */
    public static CompoundExpression ampAmp(Expression left, Expression right) {
        return Expressions.dialectDualExp(left, DualExpOperator.DOUBLE_AMP, right, PostgreExpressions::doubleAmpType);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING">json -> integer → json<br>
     * jsonb -> integer → jsonb<br/>
     * Extracts n'th element of JSON array (array elements are indexed from zero, but negative integers count from the end).<br/>
     * '[{"a":"foo"},{"b":"bar"},{"c":"baz"}]'::json -> 2 → {"c":"baz"}
     * </a>
     */
    public static CompoundExpression hyphenGt(Expression left, Expression right) {
        return Expressions.dialectDualExp(left, DualExpOperator.HYPHEN_GT, right, PostgreExpressions::hyphenGtType);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING">json ->> integer → text<br>
     * jsonb ->> integer → text<br/>
     * Extracts n'th element of JSON array, as text.<br/>
     * '[1,2,3]'::json ->> 2 → 3<br/>
     * json ->> text → text<br/>
     * jsonb ->> text → text<br/>
     * </a>
     */
    public static CompoundExpression hyphenGtGt(Expression left, Expression right) {
        return Expressions.dialectDualExp(left, DualExpOperator.HYPHEN_GT_GT, right, PostgreExpressions::hyphenGtGtType);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSONB-OP-TABLE">jsonb #- text[] → jsonb<br>
     * Deletes the field or array element at the specified path, where path elements can be either field keys or array indexes.<br/>
     * '["a", {"b":1}]'::jsonb #- '{1,b}' → ["a", {}]
     * </a>
     */
    public static CompoundExpression poundHyphen(Expression left, Expression right) {
        return Expressions.dialectDualExp(left, DualExpOperator.POUND_HYPHEN, right, PostgreExpressions::poundHyphenType);
    }


    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING">json #> text[] → json<br>
     * jsonb #> text[] → jsonb<br/>
     * Extracts JSON sub-object at the specified path, where path elements can be either field keys or array indexes.
     * '{"a": {"b": ["foo","bar"]}}'::json #> '{a,b,1}' → "bar"
     * </a>
     */
    public static CompoundExpression poundGt(Expression left, Expression right) {
        return Expressions.dialectDualExp(left, DualExpOperator.POUND_GT, right, PostgreExpressions::poundGtType);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSON-PROCESSING">json #>> text[] → text<br>
     * jsonb #>> text[] → text<br/>
     * Extracts JSON sub-object at the specified path as text.
     * '{"a": {"b": ["foo","bar"]}}'::json #>> '{a,b,1}' → bar
     * </a>
     */
    public static CompoundExpression poundGtGt(Expression left, Expression right) {
        return Expressions.dialectDualExp(left, DualExpOperator.POUND_GT_GT, right, PostgreExpressions::poundGtGtType);
    }


    /**
     * <p>
     * The {@link MappingType} of operator return type: follow <pre><code><br/>
     *    private static MappingType doubleVerticalType(final MappingType left, final MappingType right) {
     *        final MappingType returnType;
     *        if (left instanceof MappingType.SqlStringType || right instanceof MappingType.SqlStringType) {
     *            returnType = TextType.INSTANCE;
     *        } else if (left instanceof MappingType.SqlBinaryType || right instanceof MappingType.SqlBinaryType) {
     *            if (left instanceof MappingType.SqlBitType || right instanceof MappingType.SqlBitType) {
     *                throw CriteriaUtils.dualOperandError(DualOperator.DOUBLE_VERTICAL, left, right);
     *            }
     *            returnType = PrimitiveByteArrayType.INSTANCE;
     *        } else if (left instanceof MappingType.SqlBitType || right instanceof MappingType.SqlBitType) {
     *            returnType = BitSetType.INSTANCE;
     *        } else {
     *            throw CriteriaUtils.dualOperandError(DualOperator.DOUBLE_VERTICAL, left, right);
     *        }
     *        return returnType;
     *    }
     *    </code>
     * </pre>
     *
     * @param left  not {@link SQLs#DEFAULT} etc.
     * @param right not {@link SQLs#DEFAULT} etc.
     * @see Expression#space(BiFunction, Object)
     * @see DefiniteExpression#space(BiFunction, BiFunction, Object)
     * @see <a href="https://www.postgresql.org/docs/current/functions-bitstring.html#FUNCTIONS-BIT-STRING-OP-TABLE">bit || bit → bit</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">text || text → text <br/>
     * text || anynonarray → text <br/>
     * anynonarray || text → text
     * </a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-SQL">bytea || bytea → bytea</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-OPERATORS-TABLE">tsvector || tsvector → tsvector</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-OPERATORS-TABLE">tsquery || tsquery → tsquery</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-OPERATORS-TABLE">tsquery || tsquery → tsquery</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSONB-OP-TABLE">jsonb || jsonb → jsonb</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html">anycompatiblearray || anycompatiblearray → anycompatiblearray</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html">anycompatible || anycompatiblearray → anycompatiblearray</a>
     */
    public static CompoundExpression doubleVertical(final Expression left, final Expression right) {
        return Expressions.dialectDualExp(left, DualExpOperator.DOUBLE_VERTICAL, right, PostgreExpressions::doubleVerticalType);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">|/ double precision → double precision<br/>
     * Square root
     * </a>
     */
    public static Expression verticalSlash(final Expression exp) {
        return PostgreExpressions.unaryExpression(PostgreUnaryExpOperator.VERTICAL_SLASH, exp, Expressions::doubleType);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-OPERATORS-TABLE">!! tsquery → tsquery<br>
     * Negates a tsquery, producing a query that matches documents that do not match the input query.
     * </a>
     */
    public static Expression doubleExclamation(final Expression exp) {
        return PostgreExpressions.unaryExpression(PostgreUnaryExpOperator.DOUBLE_EXCLAMATION, exp, Expressions::identityType);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">||/ double precision → double precision<br/>
     * Cube root
     * </a>
     */
    public static Expression doubleVerticalSlash(final Expression operand) {
        return PostgreExpressions.unaryExpression(PostgreUnaryExpOperator.DOUBLE_VERTICAL_SLASH, operand, Expressions::doubleType);
    }


    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">?- line → boolean<br/>
     * ?- lseg → boolean<br/>
     * Is line horizontal?
     * </a>
     */
    public static IPredicate questionHyphen(Expression operand) {
        return PostgreExpressions.unaryPredicate(PostgreBooleanUnaryOperator.QUESTION_HYPHEN, operand);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">?| line → boolean<br/>
     * ?| lseg → boolean<br/>
     * Is line vertical?<br/>
     * </a>
     */
    public static IPredicate questionVertical(Expression operand) {
        return PostgreExpressions.unaryPredicate(PostgreBooleanUnaryOperator.QUESTION_VERTICAL, operand);
    }


    /**
     * @see Expression#whiteSpace(BiFunction, Object)
     * @see DefiniteExpression#whiteSpace(BiFunction, BiFunction, Object)
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type @> geometric_type → boolean<br/>
     * Does first object contain second? Available for these pairs of types: (box, point), (box, box), (path, point), (polygon, point), (polygon, polygon), (circle, point), (circle, circle).
     * </a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-OPERATORS-TABLE">tsquery @> tsquery → boolean<br/>
     * </a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSONB-OP-TABLE">jsonb @> jsonb → boolean<br/>
     * </a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html">anyarray @> anyarray → boolean<br/>
     * </a>
     */
    public static CompoundPredicate atGt(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreDualBooleanOperator.AT_GT, right);
    }

    /**
     * @see Expression#whiteSpace(BiFunction, Object)
     * @see DefiniteExpression#whiteSpace(BiFunction, BiFunction, Object)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSONB-OP-TABLE">jsonb @? jsonpath → boolean<br/>
     * Does JSON path return any item for the specified JSON value?<br/>
     * '{"a":[1,2,3,4,5]}'::jsonb @? '$.a[*] ? (@ > 2)' → t
     * </a>
     */
    public static CompoundPredicate atQuestion(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreDualBooleanOperator.AT_QUESTION, right);
    }

    /**
     * @see Expression#whiteSpace(BiFunction, Object)
     * @see DefiniteExpression#whiteSpace(BiFunction, BiFunction, Object)
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type &lt;&#64; geometric_type → boolean<br/>
     * Is first object contained in or on second? Available for these pairs of types: (point, box), (point, lseg), (point, line), (point, path), (point, polygon), (point, circle), (box, box), (lseg, box), (lseg, line), (polygon, polygon), (circle, circle).
     * </a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-OPERATORS-TABLE">tsquery &lt;&#64; tsquery → boolean<br/>
     * </a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSONB-OP-TABLE">jsonb &lt;&#64; jsonb → boolean<br/>
     * </a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html">anyarray &lt;&#64; anyarray → boolean<br/>
     * </a>
     */
    public static CompoundPredicate ltAt(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreDualBooleanOperator.LT_AT, right);
    }

    /**
     * @see Expression#whiteSpace(BiFunction, Object)
     * @see DefiniteExpression#whiteSpace(BiFunction, BiFunction, Object)
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type &amp;&amp; geometric_type → boolean<br/>
     * Do these objects overlap? (One point in common makes this true.) Available for box, polygon, circle.</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#CIDR-INET-OPERATORS-TABLE">inet &amp;&amp; inet → boolean<br/>
     * Does either subnet contain or equal the other?<br/>
     * inet '192.168.1/24' &amp;&amp; inet '192.168.1.80/28' → t<br/>
     * inet '192.168.1/24' &amp;&amp; inet '192.168.2.0/28' → f
     * </a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-array.html">anyarray &amp;&amp; anyarray → boolean<br/>
     * </a>
     */
    public static CompoundPredicate doubleAmp(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreDualBooleanOperator.DOUBLE_AMP, right);
    }

    /**
     * @see Expression#whiteSpace(BiFunction, Object)
     * @see DefiniteExpression#whiteSpace(BiFunction, BiFunction, Object)
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
    public static CompoundPredicate ltLt(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreDualBooleanOperator.LT_LT, right);
    }


    /**
     * @see Expression#whiteSpace(BiFunction, Object)
     * @see DefiniteExpression#whiteSpace(BiFunction, BiFunction, Object)
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type >> geometric_type → boolean<br/>
     * Is first object strictly right of second? Available for point, box, polygon, circle.</a>
     */
    public static CompoundPredicate gtGt(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreDualBooleanOperator.GT_GT, right);
    }

    /**
     * @see Expression#whiteSpace(BiFunction, Object)
     * @see DefiniteExpression#whiteSpace(BiFunction, BiFunction, Object)
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#CIDR-INET-OPERATORS-TABLE">inet &lt;&lt;= inet → boolean<br/>
     * Is subnet contained by or equal to subnet?<br/>
     * inet '192.168.1/24' &lt;&lt;= inet '192.168.1/24' → t
     * </a>
     */
    public static CompoundPredicate ltLtEqual(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreDualBooleanOperator.LT_LT_EQUAL, right);
    }

    /**
     * @see Expression#whiteSpace(BiFunction, Object)
     * @see DefiniteExpression#whiteSpace(BiFunction, BiFunction, Object)
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#CIDR-INET-OPERATORS-TABLE">inet >>= inet → boolean<br/>
     * Does subnet contain or equal subnet?<br/>
     * inet '192.168.1/24' >>= inet '192.168.1/24' → t
     * </a>
     */
    public static CompoundPredicate gtGtEqual(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreDualBooleanOperator.GT_GT_EQUAL, right);
    }

    /**
     * @see Expression#whiteSpace(BiFunction, Object)
     * @see DefiniteExpression#whiteSpace(BiFunction, BiFunction, Object)
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type &amp;&lt; geometric_type → boolean<br/>
     * Does first object not extend to the right of second? Available for box, polygon, circle.</a>
     */
    public static CompoundPredicate ampLt(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreDualBooleanOperator.AMP_LT, right);
    }

    /**
     * @see Expression#whiteSpace(BiFunction, Object)
     * @see DefiniteExpression#whiteSpace(BiFunction, BiFunction, Object)
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type &amp;&gt; geometric_type → boolean<br/>
     * Does first object not extend to the left of second? Available for box, polygon, circle.</a>
     */
    public static CompoundPredicate ampGt(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreDualBooleanOperator.AMP_GT, right);
    }

    /**
     * @see Expression#whiteSpace(BiFunction, Object)
     * @see DefiniteExpression#whiteSpace(BiFunction, BiFunction, Object)
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type &lt;&lt;| geometric_type → boolean<br/>
     * Is first object strictly below second? Available for point, box, polygon, circle.</a>
     */
    public static CompoundPredicate ltLtVertical(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreDualBooleanOperator.LT_LT_VERTICAL, right);
    }

    /**
     * @see Expression#whiteSpace(BiFunction, Object)
     * @see DefiniteExpression#whiteSpace(BiFunction, BiFunction, Object)
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type |>> geometric_type → boolean<br/>
     * Is first object strictly above second? Available for point, box, polygon, circle.</a>
     */
    public static CompoundPredicate verticalGtGt(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreDualBooleanOperator.VERTICAL_GT_GT, right);
    }

    /**
     * @see Expression#whiteSpace(BiFunction, Object)
     * @see DefiniteExpression#whiteSpace(BiFunction, BiFunction, Object)
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type &amp;&lt;| geometric_type → boolean<br/>
     * Does first object not extend above second? Available for box, polygon, circle.</a>
     */
    public static CompoundPredicate ampLtVertical(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreDualBooleanOperator.AMP_LT_VERTICAL, right);
    }

    /**
     * @see Expression#whiteSpace(BiFunction, Object)
     * @see DefiniteExpression#whiteSpace(BiFunction, BiFunction, Object)
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type |&amp;> geometric_type → boolean<br/>
     * Does first object not extend below second? Available for box, polygon, circle.</a>
     */
    public static CompoundPredicate verticalAmpGt(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreDualBooleanOperator.VERTICAL_AMP_GT, right);
    }


    /**
     * @see Expression#whiteSpace(BiFunction, Object)
     * @see DefiniteExpression#whiteSpace(BiFunction, BiFunction, Object)
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">box &lt;^ box → boolean<br/>
     * Is first object below second (allows edges to touch)?</a>
     */
    public static CompoundPredicate ltCaret(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreDualBooleanOperator.LT_CARET, right);
    }

    /**
     * @see Expression#whiteSpace(BiFunction, Object)
     * @see DefiniteExpression#whiteSpace(BiFunction, BiFunction, Object)
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">box >^ box → boolean<br/>
     * Is first object above second (allows edges to touch)?</a>
     */
    public static CompoundPredicate gtCaret(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreDualBooleanOperator.GT_CARET, right);
    }

    /**
     * @see Expression#whiteSpace(BiFunction, Object)
     * @see DefiniteExpression#whiteSpace(BiFunction, BiFunction, Object)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSONB-OP-TABLE">jsonb ? text → boolean<br/>
     * Does the text string exist as a top-level key or array element within the JSON value?<br/>
     * '{"a":1, "b":2}'::jsonb ? 'b' → t<br/>
     * '["a", "b", "c"]'::jsonb ? 'b' → t
     * </a>
     */
    public static CompoundPredicate question(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreDualBooleanOperator.QUESTION, right);
    }

    /**
     * @see Expression#whiteSpace(BiFunction, Object)
     * @see DefiniteExpression#whiteSpace(BiFunction, BiFunction, Object)
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type ?# geometric_type → boolean<br/>
     * Is first object above second (allows edges to touch)?</a>
     */
    public static CompoundPredicate questionPound(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreDualBooleanOperator.QUESTION_POUND, right);
    }

    /**
     * @see Expression#whiteSpace(BiFunction, Object)
     * @see DefiniteExpression#whiteSpace(BiFunction, BiFunction, Object)
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSONB-OP-TABLE">jsonb ?&amp; text[] → boolean<br/>
     * Do all of the strings in the text array exist as top-level keys or array elements?<br/>
     * '["a", "b", "c"]'::jsonb ?&amp; array['a', 'b'] → t
     * </a>
     */
    public static CompoundPredicate questionAmp(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreDualBooleanOperator.QUESTION_AMP, right);
    }

    /**
     * @see Expression#whiteSpace(BiFunction, Object)
     * @see DefiniteExpression#whiteSpace(BiFunction, BiFunction, Object)
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">point ?- point → boolean<br/>
     * Are points horizontally aligned (that is, have same y coordinate)?</a>
     */
    public static CompoundPredicate questionHyphen(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreDualBooleanOperator.QUESTION_HYPHEN, right);
    }

    /**
     * @see Expression#whiteSpace(BiFunction, Object)
     * @see DefiniteExpression#whiteSpace(BiFunction, BiFunction, Object)
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">point ?| point → boolean<br/>
     * Are points vertically aligned (that is, have same x coordinate)?</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSONB-OP-TABLE">jsonb ?| text[] → boolean<br/>
     * Do any of the strings in the text array exist as top-level keys or array elements?<br/>
     * '{"a":1, "b":2, "c":3}'::jsonb ?| array['b', 'd'] → t
     * </a>
     */
    public static CompoundPredicate questionVertical(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreDualBooleanOperator.QUESTION_VERTICAL, right);
    }

    /**
     * @see Expression#whiteSpace(BiFunction, Object)
     * @see DefiniteExpression#whiteSpace(BiFunction, BiFunction, Object)
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">line ?-| line → boolean<br/>
     * lseg ?-| lseg → boolean</a>
     */
    public static CompoundPredicate questionHyphenVertical(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreDualBooleanOperator.QUESTION_HYPHEN_VERTICAL, right);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">line ?|| line → boolean<br/>
     * lseg ?|| lseg → boolean</a>
     */
    public static CompoundPredicate questionVerticalVertical(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreDualBooleanOperator.QUESTION_VERTICAL_VERTICAL, right);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type ~= geometric_type → boolean<br/>
     * Are these objects the same? Available for point, box, polygon, circle.<br/>
     * </a>
     */
    public static CompoundPredicate tildeEqual(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreDualBooleanOperator.TILDE_EQUAL, right);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-OPERATORS-TABLE">tsvector @@ tsquery → boolean<br/>
     * tsquery @@ tsvector → boolean<br/>
     * text @@ tsquery → boolean<br/>
     * </a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-json.html#FUNCTIONS-JSONB-OP-TABLE">jsonb @@ jsonpath → boolean<br/>
     * tReturns the result of a JSON path predicate check for the specified JSON value. Only the first item of the result is taken into account. If the result is not Boolean, then NULL is returned.<br/>
     * '{"a":[1,2,3,4,5]}'::jsonb @@ '$.a[*] > 2' → t<br/>
     * </a>
     */
    public static CompoundPredicate doubleAt(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreDualBooleanOperator.DOUBLE_AT, right);
    }


    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-OPERATORS-TABLE">tsvector @@@ tsquery → boolean<br/>
     * tsquery @@@ tsvector → boolean<br/>
     * </a>
     */
    public static CompoundPredicate tripleAt(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreDualBooleanOperator.TRIPLE_AT, right);
    }


    /**
     * <p>
     * The {@link MappingType} of operator return type: {@link  BooleanType} .
     *
     * @param left not {@link SQLs#DEFAULT} etc.
     * @see Expression#space(BiFunction, Object)
     * @see DefiniteExpression#space(BiFunction, BiFunction, Object)
     * @see Postgres#startsWith(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">text ^@ text → boolean</a>
     */
    public static CompoundPredicate caretAt(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreDualBooleanOperator.CARET_AT, right);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     *
     * @see Postgres#regexpLike(Expression, Expression)
     * @see Postgres#regexpLike(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-matching.html#FUNCTIONS-POSIX-TABLE">text ~ text → boolean<br/>
     * String matches regular expression, case sensitively</a>
     */
    public static CompoundPredicate tilde(final Expression left, final Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreDualBooleanOperator.TILDE, right);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     *
     * @see Postgres#regexpLike(Expression, Expression)
     * @see Postgres#regexpLike(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-matching.html#FUNCTIONS-POSIX-TABLE">text !~ text → boolean<br/>
     * String does not match regular expression, case sensitively</a>
     */
    public static CompoundPredicate notTilde(final Expression left, final Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreDualBooleanOperator.NOT_TILDE, right);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-matching.html#FUNCTIONS-POSIX-TABLE">text ~* text → boolean<br/>
     * String matches regular expression, case insensitively</a>
     */
    public static CompoundPredicate tildeStar(final Expression left, final Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreDualBooleanOperator.TILDE_STAR, right);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-matching.html#FUNCTIONS-POSIX-TABLE">text !~* text → boolean<br/>
     * String does not match regular expression, case insensitively</a>
     */
    public static CompoundPredicate notTildeStar(final Expression left, final Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreDualBooleanOperator.NOT_TILDE_STAR, right);
    }


    /**
     * <p>
     * OVERLAPS operator
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html"> OVERLAPS operato</a>
     */
    public static Postgres._PeriodOverlapsClause period(final Expression start, final Expression endOrLength) {
        return PostgreExpressions.overlaps(start, endOrLength);
    }


    /**
     * <p>
     * OVERLAPS operator
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html"> OVERLAPS operato</a>
     */
    public static <T> Postgres._PeriodOverlapsClause period(Expression start, BiFunction<Expression, T, Expression> valueOperator, T value) {
        return period(start, valueOperator.apply(start, value));
    }

    /**
     * <p>
     * OVERLAPS operator
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html"> OVERLAPS operato</a>
     */
    public static <T> Postgres._PeriodOverlapsClause period(BiFunction<Expression, T, Expression> valueOperator, T value, Expression endOrLength) {
        return period(valueOperator.apply(endOrLength, value), endOrLength);
    }

    /**
     * <p>
     * OVERLAPS operator
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html"> OVERLAPS operato</a>
     */
    public static Postgres._PeriodOverlapsClause period(TypeInfer type, BiFunction<TypeInfer, Object, Expression> valueOperator, Object start, Object endOrLength) {
        return period(valueOperator.apply(type, start), valueOperator.apply(type, endOrLength));
    }


    /**
     * <p>
     * AT TIME ZONE operator,The {@link MappingType} of operator return type:
     * <ol>
     *     <li>If The {@link MappingType} of source is {@link MappingType.SqlLocalDateTimeType},then {@link OffsetDateTimeType}</li>
     *     <li>If The {@link MappingType} of source is {@link MappingType.SqlOffsetDateTimeType},then {@link LocalDateTimeType}</li>
     *     <li>If The {@link MappingType} of source is {@link MappingType.SqlLocalTimeType},then {@link OffsetTimeType}</li>
     *     <li>If The {@link MappingType} of source is {@link MappingType.SqlOffsetTimeType},then {@link LocalTimeType}</li>
     *     <li>Else raise {@link CriteriaException}</li>
     * </ol>
     *
     * @param source non-multi value parameter/literal
     * @param zone   non-multi value parameter/literal
     * @throws CriteriaException throw when <ul>
     *                           <li>source is multi value parameter/literal</li>
     *                           <li>zone is multi value parameter/literal</li>
     *                           <li>The {@link MappingType} of source error</li>
     *                           </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-ZONECONVERT-TABLE"> AT TIME ZONE Variants</a>
     */
    public static CompoundExpression atTimeZone(final Expression source, final Expression zone) {
        return Expressions.dialectDualExp(source, DualExpOperator.AT_TIME_ZONE, zone, PostgreSyntax::atTimeZoneType);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-matching.html#FUNCTIONS-SIMILARTO-REGEXP">SIMILAR TO Regular Expressions</a>
     */
    public static CompoundPredicate similarTo(Expression exp, Expression pattern) {
        return Expressions.likePredicate(exp, DualBooleanOperator.SIMILAR_TO, pattern, SQLs.ESCAPE, null);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-matching.html#FUNCTIONS-SIMILARTO-REGEXP">SIMILAR TO Regular Expressions</a>
     */
    public static CompoundPredicate similarTo(Expression exp, Expression pattern, SQLs.WordEscape escape, Expression escapeChar) {
        return Expressions.likePredicate(exp, DualBooleanOperator.SIMILAR_TO, pattern, escape, escapeChar);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-matching.html#FUNCTIONS-SIMILARTO-REGEXP">SIMILAR TO Regular Expressions</a>
     */
    public static CompoundPredicate notSimilarTo(Expression exp, Expression pattern) {
        return Expressions.likePredicate(exp, DualBooleanOperator.NOT_SIMILAR_TO, pattern, SQLs.ESCAPE, null);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-matching.html#FUNCTIONS-SIMILARTO-REGEXP">SIMILAR TO Regular Expressions</a>
     */
    public static CompoundPredicate notSimilarTo(Expression exp, Expression pattern, SQLs.WordEscape escape, Expression escapeChar) {
        return Expressions.likePredicate(exp, DualBooleanOperator.NOT_SIMILAR_TO, pattern, escape, escapeChar);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/15/functions-comparisons.html#ROW-WISE-COMPARISON">row_constructor IS DISTINCT FROM row_constructor<br/>
     * </a>
     */
    public static <T extends SQLExpression> CompoundPredicate isDistinctFrom(T left, T right) {
        return PostgreExpressions.dualPredicate(left, PostgreDualBooleanOperator.IS_DISTINCT_FROM, right);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/15/functions-comparisons.html#ROW-WISE-COMPARISON">row_constructor IS NOT DISTINCT FROM row_constructor<br/>
     * </a>
     */
    public static <T extends SQLExpression> CompoundPredicate isNotDistinctFrom(T left, T right) {
        return PostgreExpressions.dualPredicate(left, PostgreDualBooleanOperator.IS_NOT_DISTINCT_FROM, right);
    }


    /**
     * @param word see <ul>
     *             <li>{@link SQLs#ALL}</li>
     *             <li>{@link SQLs#SOME}</li>
     *             <li>{@link SQLs#ANY}</li>
     *             </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-comparisons.html#id-1.5.8.30.16">ANY/SOME (array)</a>
     */
    public static CompoundPredicate equal(Expression left, SQLs.QuantifiedWord word, ArrayExpression array) {
        return Expressions.compareQueryPredicate(left, DualBooleanOperator.EQUAL, word, array);
    }

    /**
     * @param word see <ul>
     *             <li>{@link SQLs#ALL}</li>
     *             <li>{@link SQLs#SOME}</li>
     *             <li>{@link SQLs#ANY}</li>
     *             </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-comparisons.html#id-1.5.8.30.16">ANY/SOME (array)</a>
     */
    public static CompoundPredicate notEqual(Expression left, SQLs.QuantifiedWord word, ArrayExpression array) {
        return Expressions.compareQueryPredicate(left, DualBooleanOperator.NOT_EQUAL, word, array);
    }


    /**
     * @param word see <ul>
     *             <li>{@link SQLs#ALL}</li>
     *             <li>{@link SQLs#SOME}</li>
     *             <li>{@link SQLs#ANY}</li>
     *             </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-comparisons.html#id-1.5.8.30.16">ANY/SOME (array)</a>
     */
    public static CompoundPredicate less(Expression left, SQLs.QuantifiedWord word, ArrayExpression array) {
        return Expressions.compareQueryPredicate(left, DualBooleanOperator.LESS, word, array);
    }


    /**
     * @param word see <ul>
     *             <li>{@link SQLs#ALL}</li>
     *             <li>{@link SQLs#SOME}</li>
     *             <li>{@link SQLs#ANY}</li>
     *             </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-comparisons.html#id-1.5.8.30.16">ANY/SOME (array)</a>
     */
    public static CompoundPredicate lessEqual(Expression left, SQLs.QuantifiedWord word, ArrayExpression array) {
        return Expressions.compareQueryPredicate(left, DualBooleanOperator.LESS_EQUAL, word, array);
    }

    /**
     * @param word see <ul>
     *             <li>{@link SQLs#ALL}</li>
     *             <li>{@link SQLs#SOME}</li>
     *             <li>{@link SQLs#ANY}</li>
     *             </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-comparisons.html#id-1.5.8.30.16">ANY/SOME (array)<br/></a>
     */
    public static CompoundPredicate greater(Expression left, SQLs.QuantifiedWord word, ArrayExpression array) {
        return Expressions.compareQueryPredicate(left, DualBooleanOperator.GREATER, word, array);
    }

    /**
     * @param word see <ul>
     *             <li>{@link SQLs#ALL}</li>
     *             <li>{@link SQLs#SOME}</li>
     *             <li>{@link SQLs#ANY}</li>
     *             </ul>
     * @see <a href="https://www.postgresql.org/docs/current/functions-comparisons.html#id-1.5.8.30.16">ANY/SOME (array)<br/></a>
     */
    public static CompoundPredicate greaterEqual(Expression left, SQLs.QuantifiedWord word, ArrayExpression array) {
        return Expressions.compareQueryPredicate(left, DualBooleanOperator.GREATER_EQUAL, word, array);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SQL-SYNTAX-COLLATE-EXPRS">Collation Expressions</a>
     * @see <a href="https://www.postgresql.org/docs/16/collation.html">collation</a>
     */
    public static SimpleResultExpression collate(Expression expr, String collation) {
        return Expressions.collateExp(expr, collation);
    }


    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-select.html">grouping_element</a>
     */
    public static GroupByItem.ExpressionGroup parens() {
        return Expressions.emptyParens();
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-select.html">grouping_element</a>
     */
    public static GroupByItem.ExpressionGroup parens(Expression exp) {
        return (GroupByItem.ExpressionGroup) Expressions.parens(null, exp);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-select.html">grouping_element</a>
     */
    public static GroupByItem.ExpressionGroup parens(Expression exp1, Expression exp2) {
        return (GroupByItem.ExpressionGroup) Expressions.parens(null, exp1, exp2);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-select.html">grouping_element</a>
     */
    public static GroupByItem.ExpressionGroup parens(Expression exp1, Expression exp2, Expression exp3,
                                                     Expression... rest) {
        return (GroupByItem.ExpressionGroup) Expressions.parens(null, exp1, exp2, exp3, rest);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-select.html">grouping_element</a>
     */
    public static GroupByItem.ExpressionGroup parens(Consumer<Consumer<Expression>> consumer) {
        return (GroupByItem.ExpressionGroup) Expressions.parens(null, consumer);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-select.html">grouping_element</a>
     */
    public static GroupByItem rollup(GroupByItem.ExpressionItem exp) {
        return Expressions.parens(Expressions.GroupingModifier.ROLLUP, exp);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-select.html">grouping_element</a>
     */
    public static GroupByItem rollup(GroupByItem.ExpressionItem exp1, GroupByItem.ExpressionItem exp2) {
        return Expressions.parens(Expressions.GroupingModifier.ROLLUP, exp1, exp2);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-select.html">grouping_element</a>
     */
    public static GroupByItem rollup(GroupByItem.ExpressionItem exp1, GroupByItem.ExpressionItem exp2,
                                     GroupByItem.ExpressionItem exp3, GroupByItem.ExpressionItem... rest) {
        return Expressions.parens(Expressions.GroupingModifier.ROLLUP, exp1, exp2, exp3, rest);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-select.html">grouping_element</a>
     */
    public static GroupByItem rollup(Consumer<Consumer<GroupByItem.ExpressionItem>> consumer) {
        return Expressions.parens(Expressions.GroupingModifier.ROLLUP, consumer);
    }


    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-select.html">grouping_element</a>
     */
    public static GroupByItem cube(GroupByItem.ExpressionItem exp) {
        return Expressions.parens(Expressions.GroupingModifier.CUBE, exp);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-select.html">grouping_element</a>
     */
    public static GroupByItem cube(GroupByItem.ExpressionItem exp1, GroupByItem.ExpressionItem exp2) {
        return Expressions.parens(Expressions.GroupingModifier.CUBE, exp1, exp2);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-select.html">grouping_element</a>
     */
    public static GroupByItem cube(GroupByItem.ExpressionItem exp1, GroupByItem.ExpressionItem exp2,
                                   GroupByItem.ExpressionItem exp3, GroupByItem.ExpressionItem... rest) {
        return Expressions.parens(Expressions.GroupingModifier.CUBE, exp1, exp2, exp3, rest);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-select.html">grouping_element</a>
     */
    public static GroupByItem cube(Consumer<Consumer<GroupByItem.ExpressionItem>> consumer) {
        return Expressions.parens(Expressions.GroupingModifier.CUBE, consumer);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-select.html">grouping_element</a>
     */
    public static GroupByItem groupingSets(GroupByItem item) {
        return Expressions.parens(Expressions.GroupingModifier.GROUPING_SETS, item);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-select.html">grouping_element</a>
     */
    public static GroupByItem groupingSets(GroupByItem item1, GroupByItem item2) {
        return Expressions.parens(Expressions.GroupingModifier.GROUPING_SETS, item1, item2);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-select.html">grouping_element</a>
     */
    public static GroupByItem groupingSets(GroupByItem item1, GroupByItem item2, GroupByItem item3, GroupByItem... rest) {
        return Expressions.parens(Expressions.GroupingModifier.GROUPING_SETS, item1, item2, item3, rest);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-select.html">grouping_element</a>
     */
    public static GroupByItem groupingSets(Consumer<Consumer<GroupByItem>> consumer) {
        return Expressions.parens(Expressions.GroupingModifier.GROUPING_SETS, consumer);
    }


    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-select.html">ROWS FROM</a>
     */
    public static _TabularWithOrdinalityFunction rowsFrom(Consumer<Postgres._RowsFromSpaceClause> consumer) {
        return PostgreFunctionUtils.rowsFrom(consumer);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-select.html">ROWS FROM</a>
     */
    public static _TabularWithOrdinalityFunction rowsFrom(SQLs.SymbolSpace space, Consumer<Postgres.RowFromConsumer> consumer) {
        return PostgreFunctionUtils.rowsFrom(space, consumer);
    }


    /*-------------------below package method -------------------*/

    static String keyWordToString(Enum<?> keyWordEnum) {
        return _StringUtils.builder()
                .append(Postgres.class.getSimpleName())
                .append(_Constant.PERIOD)
                .append(keyWordEnum.name())
                .toString();
    }


    /*-------------------below private method -------------------*/


    /**
     * @see #atTimeZone(Expression, Expression)
     */
    private static MappingType atTimeZoneType(final MappingType left, final MappingType right) {
        final MappingType returnType;
        if (left instanceof MappingType.SqlLocalDateTimeType) {
            returnType = OffsetDateTimeType.INSTANCE;
        } else if (left instanceof MappingType.SqlOffsetDateTimeType) {
            returnType = LocalDateTimeType.INSTANCE;
        } else if (left instanceof MappingType.SqlLocalTimeType) {
            returnType = OffsetTimeType.INSTANCE;
        } else if (left instanceof MappingType.SqlOffsetTimeType) {
            returnType = LocalTimeType.INSTANCE;
        } else {
            String m = String.format("AT TIME ZONE operator don't support %s", left);
            throw ContextStack.clearStackAndCriteriaError(m);
        }
        return returnType;
    }

    /**
     * @see #caret(Expression, Expression)
     */
    private static MappingType caretResultType(final MappingType left, final MappingType right) {
        final MappingType returnType;
        if (left instanceof MappingType.SqlIntegerOrDecimalType
                && right instanceof MappingType.SqlIntegerOrDecimalType) {
            returnType = BigDecimalType.INSTANCE;
        } else {
            returnType = DoubleType.INSTANCE;
        }
        return returnType;
    }


}
