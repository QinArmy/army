package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.dialect._Constant;
import io.army.mapping.*;
import io.army.meta.FieldMeta;
import io.army.util._StringUtils;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * <p>
 * Package class
 * </p>
 *
 * @since 1.0
 */
abstract class PostgreSyntax extends PostgreMiscellaneousFunctions {

    /**
     * Package constructor
     */
    PostgreSyntax() {
    }


    public interface Modifier extends Query.SelectModifier {

    }

    public interface WordDistinct extends Modifier, SqlSyntax.ArgDistinct {

    }

    public interface WordMaterialized extends SQLWords {

    }

    public interface _PeriodOverlapsClause {

        IPredicate overlaps(Expression start, Expression endOrLength);

        <T> IPredicate overlaps(Expression start, BiFunction<Expression, T, Expression> valueOperator, T value);

        <T> IPredicate overlaps(BiFunction<Expression, T, Expression> valueOperator, T value, Expression endOrLength);

        IPredicate overlaps(TypeInfer type, BiFunction<TypeInfer, Object, Expression> valueOperator, Object start, Object endOrLength);


    }

    private enum SelectModifier implements Modifier {

        ALL(" ALL");

        private final String spaceWord;

        SelectModifier(String spaceWord) {
            this.spaceWord = spaceWord;
        }


        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return keyWordToString(this);
        }


    }//SelectModifier


    private enum KeyWordDistinct implements WordDistinct {

        DISTINCT(" DISTINCT");

        private final String spaceWord;

        KeyWordDistinct(String spaceWord) {
            this.spaceWord = spaceWord;
        }


        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return keyWordToString(this);
        }
    }//KeyWordDistinct


    private enum KeyWordMaterialized implements WordMaterialized {

        MATERIALIZED(" MATERIALIZED"),
        NOT_MATERIALIZED(" NOT MATERIALIZED");

        private final String spaceWord;

        KeyWordMaterialized(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }


        @Override
        public final String toString() {
            return keyWordToString(this);
        }


    }//KeyWordMaterialized

    private enum FromNormalizedWord implements SQLsSyntax.BooleanTestWord {
        FROM_NORMALIZED(" FROM NORMALIZED"),
        NORMALIZED(" NORMALIZED");

        private final String spaceWords;

        FromNormalizedWord(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWords;
        }


        @Override
        public String toString() {
            return keyWordToString(this);
        }


    }//FromNormalizedWord


    public static final Modifier ALL = SelectModifier.ALL;

    public static final WordDistinct DISTINCT = KeyWordDistinct.DISTINCT;

    public static final WordMaterialized MATERIALIZED = KeyWordMaterialized.MATERIALIZED;

    public static final WordMaterialized NOT_MATERIALIZED = KeyWordMaterialized.NOT_MATERIALIZED;

    public static final SQLsSyntax.BooleanTestWord FROM_NORMALIZED = FromNormalizedWord.FROM_NORMALIZED;

    public static final SQLsSyntax.BooleanTestWord NORMALIZED = FromNormalizedWord.NORMALIZED;


    public static Expression excluded(FieldMeta<?> field) {
        return ContextStack.peek().insertValueField(field, PostgreExcludedField::excludedField);
    }


    /**
     * @param expression couldn't be multi-value parameter/literal, for example {@link SQLs#multiParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/15/sql-syntax-calling-funcs.html#SQL-SYNTAX-CALLING-FUNCS-POSITIONAL">Using Positional Notation</a>
     * @see <a href="https://www.postgresql.org/docs/15/sql-syntax-calling-funcs.html#SQL-SYNTAX-CALLING-FUNCS-NAMED">Using Named Notation</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">make_interval ( [ years int [, months int [, weeks int [, days int [, hours int [, mins int [, secs double precision ]]]]]]] ) → interval</a>
     */
    public static Expression namedNotation(String name, Expression expression) {
        return FunctionUtils.namedNotation(name, expression);
    }

    /**
     * @param valueOperator couldn't return multi-value parameter/literal, for example {@link SQLs#multiParam(TypeInfer, Collection)}
     * @see <a href="https://www.postgresql.org/docs/15/sql-syntax-calling-funcs.html#SQL-SYNTAX-CALLING-FUNCS-POSITIONAL">Using Positional Notation</a>
     * @see <a href="https://www.postgresql.org/docs/15/sql-syntax-calling-funcs.html#SQL-SYNTAX-CALLING-FUNCS-NAMED">Using Named Notation</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">make_interval ( [ years int [, months int [, weeks int [, days int [, hours int [, mins int [, secs double precision ]]]]]]] ) → interval</a>
     */
    public static <T> Expression namedNotation(String name, Function<T, Expression> valueOperator, T value) {
        return FunctionUtils.namedNotation(name, valueOperator.apply(value));
    }




    /*-------------------below operator method -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of exp
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">Absolute value operator</a>
     */
    public static Expression at(Expression operand) {
        return Expressions.dialectUnaryExp(ExpUnaryOperator.AT, operand, Expressions::identityType);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">@-@ geometric_type → double precision<br/>
     * Computes the total length. Available for lseg, path.
     * </a>
     */
    public static Expression atHyphenAt(Expression operand) {
        return Expressions.dialectUnaryExp(ExpUnaryOperator.AT_HYPHEN_AT, operand, PostgreExpressions::atHyphenAtType);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">@@ geometric_type → point<br>
     * Computes the center point. Available for box, lseg, polygon, circle.
     * </a>
     */
    public static Expression atAt(Expression operand) {
        return Expressions.dialectUnaryExp(ExpUnaryOperator.AT_AT, operand, PostgreExpressions::atAtType);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE"># geometric_type → integer<br>
     * Returns the number of points. Available for path, polygon.
     * </a>
     */
    public static Expression pound(Expression operand) {
        return Expressions.dialectUnaryExp(ExpUnaryOperator.POUND, operand, PostgreExpressions::unaryPoundType);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type # geometric_type → point<br>
     * Computes the point of intersection, or NULL if there is none. Available for lseg, line.
     * </a>
     */
    public static Expression pound(Expression left, Expression right) {
        return Expressions.dialectDualExp(left, ExpDualOperator.POUND, right, PostgreExpressions::dualPoundType);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type <-> geometric_type → double precision<br/>
     * Computes the distance between the objects. Available for all seven geometric types, for all combinations of point with another geometric type, and for these additional pairs of types: (box, lseg), (lseg, line), (polygon, circle) (and the commutator cases).
     * </a>
     */
    public static Expression ltHyphenGt(Expression left, Expression right) {
        return Expressions.dialectDualExp(left, ExpDualOperator.LT_HYPHEN_GT, right, PostgreExpressions::lessHyphenGreaterType);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">numeric_type + numeric_type → numeric_type</a>
     */
    public static Expression plus(Expression left, Expression right) {
        return Expressions.dialectDualExp(left, ExpDualOperator.PLUS, right, PostgreExpressions::plusType);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">numeric_type - numeric_type → numeric_type<br/>
     * Subtraction</a>
     */
    public static Expression minus(Expression left, Expression right) {
        return Expressions.dialectDualExp(left, ExpDualOperator.MINUS, right, PostgreExpressions::minusType);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">numeric_type * numeric_type → numeric_type<br/>
     * Multiplication</a>
     */
    public static Expression times(Expression left, Expression right) {
        return Expressions.dialectDualExp(left, ExpDualOperator.TIMES, right, PostgreExpressions::timesType);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">numeric_type / numeric_type → numeric_type<br/>
     * Division (for integral types, division truncates the result towards zero)</a>
     */
    public static Expression divide(Expression left, Expression right) {
        return Expressions.dialectDualExp(left, ExpDualOperator.DIVIDE, right, PostgreExpressions::divideType);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">numeric_type % numeric_type → numeric_type<br/>
     * Modulo (remainder); available for smallint, integer, bigint, and numeric</a>
     */
    public static Expression mode(Expression left, Expression right) {
        return Expressions.dialectDualExp(left, ExpDualOperator.MOD, right, Expressions::mathExpType);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: follow <code><pre><br>
     *    private static MappingType caretResultType(final MappingType left, final MappingType right) {
     *        final MappingType returnType;
     *        if (left instanceof MappingType.IntegerOrDecimalType
     *                && right instanceof MappingType.IntegerOrDecimalType) {
     *            returnType = BigDecimalType.INSTANCE;
     *        } else {
     *            returnType = DoubleType.INSTANCE;
     *        }
     *        return returnType;
     *    }
     * </pre></code>
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">numeric ^ numeric → numeric <br/>
     * double precision ^ double precision → double precision <br/>
     * Exponentiation</a>
     */
    public static Expression caret(final Expression left, final Expression right) {
        return Expressions.dialectDualExp(left, ExpDualOperator.EXPONENTIATION, right, PostgreSyntax::caretResultType);
    }


    /**
     * <p>
     * The {@link MappingType} of operator return type: follow  <code><pre><br/>
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
     * </pre></code>
     * </p>
     *
     * @param left  not {@link SQLs#DEFAULT} etc.
     * @param right not {@link SQLs#DEFAULT} etc.
     * @see Expression#apply(BiFunction, Expression)
     * @see SimpleExpression#apply(BiFunction, BiFunction, Object)
     * @see <a href="https://www.postgresql.org/docs/current/functions-bitstring.html#FUNCTIONS-BIT-STRING-OP-TABLE">bit || bit → bit</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">text || text → text <br/>
     * text || anynonarray → text <br/>
     * anynonarray || text → text
     * </a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-SQL">bytea || bytea → bytea</a>
     */
    public static Expression doubleVertical(final Expression left, final Expression right) {
        return Expressions.dialectDualExp(left, ExpDualOperator.DOUBLE_VERTICAL, right, PostgreSyntax::doubleVerticalType);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">|/ double precision → double precision<br/>
     * Square root
     * </a>
     */
    public static Expression verticalSlash(final Expression exp) {
        return Expressions.dialectUnaryExp(ExpUnaryOperator.VERTICAL_SLASH, exp, Expressions::doubleType);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">||/ double precision → double precision<br/>
     * Cube root
     * </a>
     */
    public static Expression verticalVerticalSlash(final Expression operand) {
        return Expressions.dialectUnaryExp(ExpUnaryOperator.VERTICAL_VERTICAL_SLASH, operand, Expressions::doubleType);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">?- line → boolean<br/>
     * ?- lseg → boolean<br/>
     * Is line horizontal?
     */
    public static IPredicate questionHyphen(Expression operand) {
        return PostgreExpressions.unaryPredicate(PostgreBooleanUnaryOperator.QUESTION_HYPHEN, operand);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">?| line → boolean<br/>
     * ?| lseg → boolean<br/>
     * Is line vertical?
     */
    public static IPredicate questionVertical(Expression operand) {
        return PostgreExpressions.unaryPredicate(PostgreBooleanUnaryOperator.QUESTION_VERTICAL, operand);
    }


    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type @> geometric_type → boolean<br/>
     * Does first object contain second? Available for these pairs of types: (box, point), (box, box), (path, point), (polygon, point), (polygon, polygon), (circle, point), (circle, circle).
     */
    public static IPredicate atGt(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreBooleanDualOperator.AT_GT, right);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type &lt;@ geometric_type → boolean<br/>
     * Is first object contained in or on second? Available for these pairs of types: (point, box), (point, lseg), (point, line), (point, path), (point, polygon), (point, circle), (box, box), (lseg, box), (lseg, line), (polygon, polygon), (circle, circle).
     */
    public static IPredicate ltAt(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreBooleanDualOperator.LT_AT, right);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type &amp;&amp; geometric_type → boolean<br/>
     * Do these objects overlap? (One point in common makes this true.) Available for box, polygon, circle.
     */
    public static IPredicate amp(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreBooleanDualOperator.AMP_AMP, right);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type &lt;&lt; geometric_type → boolean<br/>
     * Is first object strictly left of second? Available for point, box, polygon, circle.
     */
    public static IPredicate ltLt(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreBooleanDualOperator.LT_LT, right);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type >> geometric_type → boolean<br/>
     * Is first object strictly right of second? Available for point, box, polygon, circle.
     */
    public static IPredicate gtGt(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreBooleanDualOperator.GT_GT, right);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type &amp;&lt; geometric_type → boolean<br/>
     * Does first object not extend to the right of second? Available for box, polygon, circle.
     */
    public static IPredicate ampLt(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreBooleanDualOperator.AMP_LT, right);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type &amp;&gt; geometric_type → boolean<br/>
     * Does first object not extend to the left of second? Available for box, polygon, circle.
     */
    public static IPredicate ampGt(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreBooleanDualOperator.AMP_GT, right);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type &lt;&lt;| geometric_type → boolean<br/>
     * Is first object strictly below second? Available for point, box, polygon, circle.
     */
    public static IPredicate ltLtVertical(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreBooleanDualOperator.LT_LT_VERTICAL, right);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type |>> geometric_type → boolean<br/>
     * Is first object strictly above second? Available for point, box, polygon, circle.
     */
    public static IPredicate verticalGtGt(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreBooleanDualOperator.VERTICAL_GT_GT, right);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type &amp;&lt;| geometric_type → boolean<br/>
     * Does first object not extend above second? Available for box, polygon, circle.
     */
    public static IPredicate ampLtVertical(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreBooleanDualOperator.AMP_LT_VERTICAL, right);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type |&amp;> geometric_type → boolean<br/>
     * Does first object not extend below second? Available for box, polygon, circle.
     */
    public static IPredicate verticalAmpGt(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreBooleanDualOperator.VERTICAL_AMP_GT, right);
    }


    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">box &lt;^ box → boolean<br/>
     * Is first object below second (allows edges to touch)?
     */
    public static IPredicate ltCaret(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreBooleanDualOperator.LT_CARET, right);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">box >^ box → boolean<br/>
     * Is first object above second (allows edges to touch)?
     */
    public static IPredicate gtCaret(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreBooleanDualOperator.GT_CARET, right);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type ?# geometric_type → boolean<br/>
     * Is first object above second (allows edges to touch)?
     */
    public static IPredicate questionPound(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreBooleanDualOperator.QUESTION_POUND, right);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">point ?- point → boolean<br/>
     * Are points horizontally aligned (that is, have same y coordinate)?
     */
    public static IPredicate questionHyphen(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreBooleanDualOperator.QUESTION_HYPHEN, right);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">point ?| point → boolean<br/>
     * Are points vertically aligned (that is, have same x coordinate)?
     */
    public static IPredicate questionVertical(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreBooleanDualOperator.QUESTION_VERTICAL, right);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">line ?-| line → boolean<br/>
     * lseg ?-| lseg → boolean
     */
    public static IPredicate questionHyphenVertical(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreBooleanDualOperator.QUESTION_HYPHEN_VERTICAL, right);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">line ?|| line → boolean<br/>
     * lseg ?|| lseg → boolean
     */
    public static IPredicate questionVerticalVertical(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreBooleanDualOperator.QUESTION_VERTICAL_VERTICAL, right);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-OP-TABLE">geometric_type ~= geometric_type → boolean<br/>
     * Are these objects the same? Available for point, box, polygon, circle.<br/>
     * </a>
     */
    public static IPredicate tildeEqual(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreBooleanDualOperator.TILDE_EQUAL, right);
    }


    /**
     * <p>
     * The {@link MappingType} of operator return type: {@link  BooleanType} .
     * </p>
     *
     * @param left not {@link SQLs#DEFAULT} etc.
     * @see Expression#apply(BiFunction, Expression)
     * @see SimpleExpression#apply(BiFunction, BiFunction, Object)
     * @see Postgres#startsWith(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">text ^@ text → boolean</a>
     */
    public static IPredicate caretAt(Expression left, Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreBooleanDualOperator.CARET_AT, right);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     * </p>
     *
     * @see Postgres#regexpLike(Expression, Expression)
     * @see Postgres#regexpLike(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-matching.html#FUNCTIONS-POSIX-TABLE">text ~ text → boolean<br/>
     * String matches regular expression, case sensitively</a>
     */
    public static IPredicate tilde(final Expression left, final Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreBooleanDualOperator.TILDE, right);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     * </p>
     *
     * @see Postgres#regexpLike(Expression, Expression)
     * @see Postgres#regexpLike(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-matching.html#FUNCTIONS-POSIX-TABLE">text !~ text → boolean<br/>
     * String does not match regular expression, case sensitively</a>
     */
    public static IPredicate notTilde(final Expression left, final Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreBooleanDualOperator.NOT_TILDE, right);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-matching.html#FUNCTIONS-POSIX-TABLE">text ~* text → boolean<br/>
     * String matches regular expression, case insensitively</a>
     */
    public static IPredicate tildeStar(final Expression left, final Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreBooleanDualOperator.TILDE_STAR, right);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-matching.html#FUNCTIONS-POSIX-TABLE">text !~* text → boolean<br/>
     * String does not match regular expression, case insensitively</a>
     */
    public static IPredicate notTildeStar(final Expression left, final Expression right) {
        return PostgreExpressions.dualPredicate(left, PostgreBooleanDualOperator.NOT_TILDE_STAR, right);
    }


    /**
     * <p>
     * OVERLAPS operator
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html"> OVERLAPS operato</a>
     */
    public static _PeriodOverlapsClause period(final Expression start, final Expression endOrLength) {
        return PostgreExpressions.overlaps(start, endOrLength);
    }


    /**
     * <p>
     * OVERLAPS operator
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html"> OVERLAPS operato</a>
     */
    public static <T> _PeriodOverlapsClause period(Expression start, BiFunction<Expression, T, Expression> valueOperator, T value) {
        return period(start, valueOperator.apply(start, value));
    }

    /**
     * <p>
     * OVERLAPS operator
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html"> OVERLAPS operato</a>
     */
    public static <T> _PeriodOverlapsClause period(BiFunction<Expression, T, Expression> valueOperator, T value, Expression endOrLength) {
        return period(valueOperator.apply(endOrLength, value), endOrLength);
    }

    /**
     * <p>
     * OVERLAPS operator
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html"> OVERLAPS operato</a>
     */
    public static _PeriodOverlapsClause period(TypeInfer type, BiFunction<TypeInfer, Object, Expression> valueOperator, Object start, Object endOrLength) {
        return period(valueOperator.apply(type, start), valueOperator.apply(type, endOrLength));
    }


    /**
     * <p>
     * AT TIME ZONE operator,The {@link MappingType} of operator return type:
     *     <ol>
     *         <li>If The {@link MappingType} of source is {@link MappingType.SqlLocalDateTimeType},then {@link OffsetDateTimeType}</li>
     *         <li>If The {@link MappingType} of source is {@link MappingType.SqlOffsetDateTimeType},then {@link LocalDateTimeType}</li>
     *         <li>If The {@link MappingType} of source is {@link MappingType.SqlLocalTimeType},then {@link OffsetTimeType}</li>
     *         <li>If The {@link MappingType} of source is {@link MappingType.SqlOffsetTimeType},then {@link LocalTimeType}</li>
     *         <li>Else raise {@link CriteriaException}</li>
     *     </ol>
     * </p>
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
    public static Expression atTimeZone(final Expression source, final Expression zone) {
        return Expressions.dialectDualExp(source, ExpDualOperator.AT_TIME_ZONE, zone, PostgreSyntax::atTimeZoneType);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-matching.html#FUNCTIONS-SIMILARTO-REGEXP">SIMILAR TO Regular Expressions</a>
     */
    public static IPredicate similarTo(Expression exp, Expression pattern) {
        return Expressions.likePredicate(exp, BooleanDualOperator.SIMILAR_TO, pattern, SQLs.ESCAPE, null);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-matching.html#FUNCTIONS-SIMILARTO-REGEXP">SIMILAR TO Regular Expressions</a>
     */
    public static IPredicate similarTo(Expression exp, Expression pattern, WordEscape escape, Expression escapeChar) {
        return Expressions.likePredicate(exp, BooleanDualOperator.SIMILAR_TO, pattern, escape, escapeChar);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-matching.html#FUNCTIONS-SIMILARTO-REGEXP">SIMILAR TO Regular Expressions</a>
     */
    public static IPredicate notSimilarTo(Expression exp, Expression pattern) {
        return Expressions.likePredicate(exp, BooleanDualOperator.NOT_SIMILAR_TO, pattern, SQLs.ESCAPE, null);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-matching.html#FUNCTIONS-SIMILARTO-REGEXP">SIMILAR TO Regular Expressions</a>
     */
    public static IPredicate notSimilarTo(Expression exp, Expression pattern, WordEscape escape, Expression escapeChar) {
        return Expressions.likePredicate(exp, BooleanDualOperator.NOT_SIMILAR_TO, pattern, escape, escapeChar);
    }




    /*-------------------below package method -------------------*/

    static String keyWordToString(Enum<?> keyWordEnum) {
        return _StringUtils.builder()
                .append(Postgres.class.getSimpleName())
                .append(_Constant.POINT)
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
     * @see #doubleVertical(Expression, Expression)
     */
    private static MappingType doubleVerticalType(final MappingType left, final MappingType right) {
        final MappingType returnType;
        if (left instanceof MappingType.SqlStringType || right instanceof MappingType.SqlStringType) {
            returnType = TextType.INSTANCE;
        } else if (left instanceof MappingType.SqlBinaryType || right instanceof MappingType.SqlBinaryType) {
            if (left instanceof MappingType.SqlBitType || right instanceof MappingType.SqlBitType) {
                throw CriteriaUtils.dualOperandError(ExpDualOperator.DOUBLE_VERTICAL, left, right);
            }
            returnType = PrimitiveByteArrayType.INSTANCE;
        } else if (left instanceof MappingType.SqlBitType || right instanceof MappingType.SqlBitType) {
            returnType = BitSetType.INSTANCE;
        } else {
            throw CriteriaUtils.dualOperandError(ExpDualOperator.DOUBLE_VERTICAL, left, right);
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
