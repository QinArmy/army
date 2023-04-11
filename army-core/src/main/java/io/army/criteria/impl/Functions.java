package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.standard.SQLFunction;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.mapping.*;
import io.army.meta.TypeMeta;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * <p>
 * This class is util class used to create standard sql element :
 * <ul>
 *     <li>statement parameter</li>
 *     <li>sql literal</li>
 *     <li>standard sql function</li>
 * </ul>
 * </p>
 *
 * @see SQLs
 */
@SuppressWarnings("unused")
abstract class Functions extends SqlSyntax {


    /**
     * package constructor,forbid application developer directly extend this util class.
     */
    Functions() {
        throw new UnsupportedOperationException();
    }


    interface _NullTreatmentClause<NR> {

        NR respectNulls();

        NR ignoreNulls();

        NR ifRespectNulls(BooleanSupplier predicate);

        NR ifIgnoreNulls(BooleanSupplier predicate);

    }

    public interface _FromFirstLastClause<FR> {
        FR fromFirst();

        FR fromLast();

        FR ifFromFirst(BooleanSupplier predicate);

        FR ifFromLast(BooleanSupplier predicate);

    }


    public interface WordIn {

    }

    public interface WordFrom {

    }

    public interface WordFor {

    }

    public interface WordSimilar {

    }

    public interface WordEscape {

    }

    public interface TrimPosition {

    }


    enum FuncWord implements ArmyKeyWord {

        INTERVAL(" INTERVAL"),
        COMMA(_Constant.SPACE_COMMA),
        USING(_Constant.SPACE_USING),
        AT_TIME_ZONE(" AT TIME ZONE"),
        LEFT_PAREN(_Constant.SPACE_LEFT_PAREN),
        RIGHT_PAREN(_Constant.SPACE_RIGHT_PAREN);

        private final String spaceWords;

        FuncWord(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String render() {
            return this.spaceWords;
        }

        @Override
        public final String toString() {
            return String.format("%s.%s", FuncWord.class.getSimpleName(), this.name());
        }


    }//Word


    private enum KeyWordIn implements WordIn, ArmyKeyWord {

        IN(" IN");

        private final String spaceWord;

        KeyWordIn(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String render() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return functionsKeyWordToString(this);
        }


    }//KeyWordIn

    private enum KeyWordSimilar implements WordSimilar, ArmyKeyWord, SQLWords {

        SIMILAR(" SIMILAR");

        private final String spaceWord;

        KeyWordSimilar(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String render() {
            return this.spaceWord;
        }


        @Override
        public final String toString() {
            return functionsKeyWordToString(this);
        }

    }//KeyWordSimilar

    private enum KeyWordEscape implements WordEscape, ArmyKeyWord {

        ESCAPE(" ESCAPE");

        private final String spaceWord;

        KeyWordEscape(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String render() {
            return this.spaceWord;
        }


        @Override
        public final String toString() {
            return functionsKeyWordToString(this);
        }

    }//KeyWordEscape

    private enum KeyWordFrom implements WordFrom, ArmyKeyWord {

        FROM(" FROM");

        private final String spaceWords;

        KeyWordFrom(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String render() {
            return this.spaceWords;
        }

        @Override
        public final String toString() {
            return functionsKeyWordToString(this);
        }


    }//KeyWordFrom

    private enum KeyWordFor implements WordFor, SQLWords, ArmyKeyWord {

        FOR(" FOR");

        private final String spaceWords;

        KeyWordFor(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String render() {
            return this.spaceWords;
        }

        @Override
        public final String toString() {
            return functionsKeyWordToString(this);
        }


    }//KeyWordFor


    enum WordTrimPosition implements TrimPosition, ArmyKeyWord, SQLWords {

        BOTH(" BOTH"),
        LEADING(" LEADING"),
        TRAILING(" TRAILING");

        private final String spaceWords;

        WordTrimPosition(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String render() {
            return this.spaceWords;
        }

        @Override
        public final String toString() {
            return functionsKeyWordToString(this);
        }


    }//WordTrimPosition


    public static final TrimPosition BOTH = WordTrimPosition.BOTH;
    public static final TrimPosition LEADING = WordTrimPosition.LEADING;
    public static final TrimPosition TRAILING = WordTrimPosition.TRAILING;

    public static final WordIn IN = KeyWordIn.IN;

    public static final WordFor FOR = KeyWordFor.FOR;

    public static final WordFrom FROM = KeyWordFrom.FROM;
    public static final WordEscape ESCAPE = KeyWordEscape.ESCAPE;
    public static final WordSimilar SIMILAR = KeyWordSimilar.SIMILAR;




    /*################################## blow number function method ##################################*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  MappingType} of expr
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_abs">ABS(X)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">ABS(numeric_type)</a>
     */
    public static Expression abs(final Expression expr) {
        return FunctionUtils.oneArgFunc("ABS", expr, expr.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_acos">ACOS(X)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">acos ( double precision ) → double precision</a>
     */
    public static Expression acos(final Expression expr) {
        return FunctionUtils.oneArgFunc("ACOS", expr, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_asin">ASIN(X)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">asin ( double precision ) → double precision</a>
     */
    public static Expression asin(final Expression expr) {
        return FunctionUtils.oneArgFunc("ASIN", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_atan">ATAN(X)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">atan ( double precision ) → double precision</a>
     */
    public static Expression atan(final Expression expr) {
        return FunctionUtils.oneArgFunc("ATAN", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_atan2">ATAN(X,y)</a>
     */
    public static Expression atan(final Expression x, final Expression y) {
        return FunctionUtils.twoArgFunc("ATAN", x, y, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     * <ul>
     *     <li>if {@link MappingType} of exp is number type,then {@link MappingType} of exp </li>
     *     <li>else {@link BigDecimalType} </li>
     * </ul>
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_ceil">CEIL(X)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">ceil ( numeric ) → numeric,ceil ( double precision ) → double precision</a>
     */
    public static Expression ceil(final Expression exp) {
        return FunctionUtils.oneArgFunc("CEIL", exp, _returnType(exp, Functions::_numberOrDecimal));
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} or expr
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_conv">CONV(X)</a>
     */
    public static Expression conv(final Expression expr, final Expression fromBase, final Expression toBase) {
        return FunctionUtils.threeArgFunc("CONV", expr, fromBase, toBase, expr.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_cos">COS(X)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">cos ( double precision ) → double precision</a>
     */
    public static Expression cos(final Expression expr) {
        return FunctionUtils.oneArgFunc("COS", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_cot">COT(X)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">cot ( double precision ) → double precision</a>
     */
    public static Expression cot(final Expression expr) {
        return FunctionUtils.oneArgFunc("COT", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  IntegerType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_crc32">CRC32(expr)</a>
     */
    public static Expression crc32(final Expression expr) {
        return FunctionUtils.oneArgFunc("CRC32", expr, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_degrees">DEGREES(x)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">degrees ( double precision )</a>
     */
    public static Expression degrees(final Expression expr) {
        return FunctionUtils.oneArgFunc("DEGREES", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     * <ul>
     *     <li>If the {@link MappingType} of exp is float number type,then {@link DoubleType}</li>
     *     <li>Else {@link BigDecimalType}</li>
     * </ul>
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_exp">EXP(x)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">exp ( numeric )</a>
     */
    public static Expression exp(final Expression exp) {
        return FunctionUtils.oneArgFunc("EXP", exp, _returnType(exp, Functions::_doubleOrDecimal));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LongType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_floor">FLOOR(x)</a>
     */
    public static Expression floor(final Expression expr) {
        return FunctionUtils.oneArgFunc("FLOOR", expr, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  StringType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_format">FORMAT(x,d)</a>
     */
    public static Expression format(final Expression x, final Expression d) {
        return FunctionUtils.twoArgFunc("FORMAT", x, d, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  StringType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_hex">HEX(n_or_s)</a>
     */
    public static Expression hex(final Expression numOrStr) {
        return FunctionUtils.oneArgFunc("HEX", numOrStr, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     * <ul>
     *     <li>If the {@link MappingType} of exp is float number type,then {@link DoubleType}</li>
     *     <li>Else {@link BigDecimalType}</li>
     * </ul>
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_ln">LN(x)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">ln ( numeric ) → numeric,ln ( double precision ) → double precision</a>
     */
    public static Expression ln(final Expression x) {
        return FunctionUtils.oneArgFunc("LN", x, _returnType(x, Functions::_doubleOrDecimal));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     * <ul>
     *     <li>If the {@link MappingType} of exp is float number type,then {@link DoubleType}</li>
     *     <li>Else {@link BigDecimalType}</li>
     * </ul>
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_log">LOG(x)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">log ( numeric ) → numeric,log ( double precision ) → double precision</a>
     */
    public static Expression log(final Expression x) {
        return FunctionUtils.oneArgFunc("LOG", x, _returnType(x, Functions::_doubleOrDecimal));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  BigDecimalType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_log">LOG(B,X)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">log ( b numeric, x numeric ) → numeric</a>
     */
    public static Expression log(final Expression b, final Expression x) {
        return FunctionUtils.twoArgFunc("LOG", b, x, BigDecimalType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:
     * <ul>
     *     <li>If the {@link MappingType} of exp is float number type,then {@link DoubleType}</li>
     *     <li>Else {@link BigDecimalType}</li>
     * </ul>
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_log10">LOG10(x)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">log10 ( numeric ) → numeric,log10 ( double precision ) → double precision</a>
     */
    public static Expression log10(final Expression x) {
        return FunctionUtils.oneArgFunc("LOG10", x, _returnType(x, Functions::_doubleOrDecimal));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of n.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_mod">MOD(N,M), N % M, N MOD M</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">mod ( y numeric_type, x numeric_type ) → numeric_type</a>
     */
    public static Expression mod(final Expression n, final Expression m) {
        return FunctionUtils.twoArgFunc("MOD", n, m, n.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType} .
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_pi">PI()</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">pi ( ) → double precision</a>
     */
    public static Expression pi() {
        return FunctionUtils.noArgFunc("PI", DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link MappingType} of x
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_pow">POW(x,y)</a>
     */
    public static Expression pow(final Expression x, final Expression y) {
        return FunctionUtils.twoArgFunc("POW", x, y, x.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType} .
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_radians">RADIANS(x)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">radians ( double precision ) → double precision</a>
     */
    public static Expression radians(final Expression x) {
        return FunctionUtils.oneArgFunc("RADIANS", x, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType} .
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_rand">RAND([N])</a>
     */
    public static Expression rand() {
        return FunctionUtils.noArgFunc("RAND", DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType} .
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_rand">RAND([N])</a>
     */
    public static Expression rand(final Expression n) {
        return FunctionUtils.oneArgFunc("RAND", n, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     * <ul>
     *     <li>If the {@link MappingType} of exp is number type,then {@link MappingType} of exp</li>
     *     <li>Else {@link BigDecimalType}</li>
     * </ul>
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_round">ROUND(x)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">round ( numeric ) → numeric</a>
     */
    public static Expression round(final Expression x) {
        return FunctionUtils.oneArgFunc("ROUND", x, _returnType(x, Functions::_numberOrDecimal));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BigDecimalType} .
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_round">ROUND(x,d)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">round ( v numeric, s integer ) → numeric</a>
     */
    public static Expression round(final Expression x, final Expression d) {
        return FunctionUtils.twoArgFunc("ROUND", x, d, BigDecimalType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType} .
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_sign">SIGN(x)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">sign ( numeric ) → numeric</a>
     */
    public static Expression sign(final Expression x) {
        return FunctionUtils.oneArgFunc("SIGN", x, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType} .
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_sin">SIN(x)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">sin ( numeric ) → numeric</a>
     */
    public static Expression sin(final Expression x) {
        return FunctionUtils.oneArgFunc("SIN", x, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     * <ul>
     *     <li>If the {@link MappingType} of exp is float number type,then {@link DoubleType}</li>
     *     <li>Else {@link BigDecimalType}</li>
     * </ul>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_sqrt">SQRT(x)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">sqrt ( numeric ) → numeric,sqrt ( double precision ) → double precision</a>
     */
    public static Expression sqrt(final Expression x) {
        return FunctionUtils.oneArgFunc("SQRT", x, _returnType(x, Functions::_doubleOrDecimal));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType} .
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_tan">TAN(x)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">tan ( numeric ) → numeric</a>
     */
    public static Expression tan(final Expression x) {
        return FunctionUtils.oneArgFunc("TAN", x, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType} .
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_truncate">TRUNCATE(x,d)</a>
     */
    public static Expression truncate(final Expression x, final Expression d) {
        return FunctionUtils.twoArgFunc("TRUNCATE", x, d, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * This function is standard sql92 functions
     * </p>
     * <p>
     * The {@link MappingType} of function return type:the {@link  MappingType} of expr1
     * </p>
     *
     * @throws CriteriaException throw when any arg is multi-value expression
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/flow-control-functions.html#function_ifnull">IFNULL(expr1,expr2)</a>
     */
    public static Expression nullIf(final Expression expr1, final Expression expr2) {
        return FunctionUtils.twoArgFunc("NULLIF", expr1, expr2, expr1.typeMeta());
    }

    /**
     * <p>
     * standard sql92 functions,The {@link MappingType} of function return type: {@link IntegerType} .
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_length">LENGTH(str)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">length ( text ) → integer</a>
     */
    public static Expression length(Expression exp) {
        return FunctionUtils.oneArgFunc("LENGTH", exp, IntegerType.INSTANCE);
    }


    public static SQLFunction._CaseFuncWhenClause cases() {
        return FunctionUtils.caseFunction(null);
    }


    /*-------------------below Aggregate Function-------------------*/

    public static Expression countStar() {
        return CountStartFunction.INSTANCE;
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LongType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static Expression count(Expression expr) {
        return FunctionUtils.oneArgFunc("COUNT", expr, LongType.INSTANCE);
    }




    /*################################## blow date time function method ##################################*/


    /*-------------------below custom function -------------------*/

    static final Pattern FUN_NAME_PATTER = Pattern.compile("^[_a-zA-Z][_\\w]*$");

    public static Expression customFunc(String name, TypeMeta returnType) {
        if (!FUN_NAME_PATTER.matcher(name).matches()) {
            throw _customFuncNameError(name);
        }
        return FunctionUtils.noArgFunc(name, returnType);
    }

    public static IPredicate customFunc(String name) {
        if (!FUN_NAME_PATTER.matcher(name).matches()) {
            throw _customFuncNameError(name);
        }
        return FunctionUtils.noArgFuncPredicate(name);
    }

    public static Expression customFunc(String name, Expression expr, TypeMeta returnType) {
        if (!FUN_NAME_PATTER.matcher(name).matches()) {
            throw _customFuncNameError(name);
        }
        return FunctionUtils.oneArgFunc(name, expr, returnType);
    }

    public static IPredicate customFunc(String name, Expression expr) {
        if (!FUN_NAME_PATTER.matcher(name).matches()) {
            throw _customFuncNameError(name);
        }
        return FunctionUtils.oneArgFuncPredicate(name, expr);
    }

    public static Expression customFunc(String name, Expression expr1, Expression expr2, TypeMeta returnType) {
        if (!FUN_NAME_PATTER.matcher(name).matches()) {
            throw _customFuncNameError(name);
        }
        return FunctionUtils.twoArgFunc(name, expr1, expr2, returnType);
    }

    public static IPredicate customFunc(String name, Expression expr1, Expression expr2) {
        if (!FUN_NAME_PATTER.matcher(name).matches()) {
            throw _customFuncNameError(name);
        }
        return FunctionUtils.twoArgPredicateFunc(name, expr1, expr2);
    }

    public static Expression customFunc(String name, List<Expression> expList, TypeMeta returnType) {
        if (!FUN_NAME_PATTER.matcher(name).matches()) {
            throw _customFuncNameError(name);
        }
        return FunctionUtils.complexArgFunc(name, _createSimpleMultiArgList(expList), returnType);
    }

    public static IPredicate customFunc(String name, List<Expression> expList) {
        if (!FUN_NAME_PATTER.matcher(name).matches()) {
            throw _customFuncNameError(name);
        }
        return FunctionUtils.complexArgPredicate(name, _createSimpleMultiArgList(expList));
    }


    static CriteriaException _customFuncNameError(String name) {
        String m = String.format("custom function name[%s] error.", name);
        return ContextStack.criteriaError(ContextStack.peek(), m);
    }


    /*################################## blow static inner class  ##################################*/


    /*-------------------below package method -------------------*/


    static TypeMeta _returnType(final ArmyExpression keyExpr, final ArmyExpression valueExpr
            , BiFunction<MappingType, MappingType, MappingType> function) {
        final TypeMeta keyType, valueType;
        keyType = keyExpr.typeMeta();
        valueType = valueExpr.typeMeta();
        final TypeMeta returnType;
        if (keyType instanceof TypeMeta.Delay || valueType instanceof TypeMeta.Delay) {
            returnType = CriteriaSupports.biDelayWrapper(keyType, valueType, function);
        } else {
            returnType = function.apply(keyType.mappingType(), valueType.mappingType());
        }
        return returnType;
    }

    static TypeMeta _returnType(ArmyExpression expression, Function<MappingType, MappingType> function) {
        final TypeMeta exprType, returnType;
        exprType = expression.typeMeta();
        if (exprType instanceof TypeMeta.Delay && !((TypeMeta.Delay) exprType).isPrepared()) {
            returnType = CriteriaSupports.delayWrapper((TypeMeta.Delay) exprType, function);
        } else if (exprType instanceof MappingType) {
            returnType = function.apply((MappingType) exprType);
        } else {
            returnType = function.apply(exprType.mappingType());
        }
        return returnType;
    }

    static List<Object> _createSimpleMultiArgList(final List<Expression> expList) {
        final int expSize = expList.size();
        assert expSize > 1;
        final List<Object> argList = new ArrayList<>((expSize << 1) - 1);
        Expression expression;
        for (int i = 0; i < expSize; i++) {
            if (i > 0) {
                argList.add(FuncWord.COMMA);
            }
            expression = expList.get(i);
            if (expression instanceof SqlValueParam.MultiValue) {
                String m = "support multi parameter or literal";
                throw ContextStack.criteriaError(ContextStack.peek(), m);
            }
            argList.add(expression);
        }
        return argList;
    }


    static Expression _simpleTowArgFunc(final String name, final Expression g1,
                                        final Expression g2, final TypeMeta returnType) {
        if (g1 instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, g1);
        }
        if (g2 instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, g2);
        }
        final List<Object> argList = new ArrayList<>(3);
        argList.add(g1);
        argList.add(FuncWord.COMMA);
        argList.add(g2);
        return FunctionUtils.complexArgFunc(name, argList, returnType);
    }

    static Expression _simpleThreeArgFunc(final String name, final Expression e1
            , final Expression e2, final Expression e3, final TypeMeta returnType) {
        if (e1 instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, e1);
        }
        if (e2 instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, e2);
        }
        if (e3 instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, e3);
        }
        final List<Object> argList = new ArrayList<>(5);

        argList.add(e1);
        argList.add(FuncWord.COMMA);
        argList.add(e2);
        argList.add(FuncWord.COMMA);

        argList.add(e3);
        return FunctionUtils.complexArgFunc(name, argList, returnType);
    }

    static Expression _simpleMaxThreeArgFunc(final String name, final List<Expression> expList
            , final TypeMeta returnType) {
        final Expression func;
        switch (expList.size()) {
            case 1:
                func = FunctionUtils.oneArgFunc(name, expList.get(0), returnType);
                break;
            case 2:
                func = FunctionUtils.twoArgFunc(name, expList.get(0), expList.get(1), returnType);
                break;
            case 3:
                func = FunctionUtils.threeArgFunc(name, expList.get(0), expList.get(1), expList.get(2), returnType);
                break;
            default:
                throw CriteriaUtils.funcArgError(name, expList);
        }
        return func;
    }

    static Expression _simpleMaxTwoArgFunc(final String name, final List<Expression> expList
            , final TypeMeta returnType) {
        final Expression func;
        switch (expList.size()) {
            case 1:
                func = FunctionUtils.oneArgFunc(name, expList.get(0), returnType);
                break;
            case 2:
                func = FunctionUtils.complexArgFunc(name, _createSimpleMultiArgList(expList), returnType);
                break;
            default:
                throw CriteriaUtils.funcArgError(name, expList);
        }
        return func;
    }

    static Expression _singleAndMultiArgFunc(final String name, final Expression single, final Expression multi
            , final TypeMeta returnType) {
        if (single instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, single);
        }
        final List<Object> argLit = new ArrayList<>(3);
        argLit.add(single);
        argLit.add(FuncWord.COMMA);
        argLit.add(multi);
        return FunctionUtils.complexArgFunc(name, argLit, returnType);
    }


    static Expression _singleAndListFunc(final String name, final Expression expr
            , final TypeMeta elementType, final Object exprList, final TypeMeta returnType) {
        if (expr instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, expr);
        }
        final List<Object> argList;

        if (exprList instanceof List) {
            final List<?> actualExprList = (List<?>) exprList;
            final int exprSize = actualExprList.size();
            if (exprSize == 0) {
                throw CriteriaUtils.funcArgError(name, exprList);
            }
            argList = new ArrayList<>(((1 + exprSize) << 1) - 1);
            for (Object o : actualExprList) {
                argList.add(FuncWord.COMMA);
                if (o instanceof Expression) {
                    argList.add(o);
                } else {
                    argList.add(SQLs.literal(elementType.mappingType(), o));
                }
            }
        } else {
            argList = new ArrayList<>(3);
            argList.add(expr);
            argList.add(FuncWord.COMMA);
            if (exprList instanceof Expression) {
                argList.add(exprList);
            } else {
                argList.add(SQLs.literal(elementType.mappingType(), exprList));
            }
        }
        return FunctionUtils.complexArgFunc(name, argList, returnType);
    }


    @Deprecated
    static TypeMeta _doubleOrNumeric(final Expression exp) {
        final TypeMeta typeMeta;
        typeMeta = exp.typeMeta();

        final TypeMeta returnType;
        if (typeMeta instanceof TypeMeta.Delay && !((TypeMeta.Delay) typeMeta).isPrepared()) {
            returnType = CriteriaSupports.delayWrapper((TypeMeta.Delay) typeMeta, Functions::_doubleOrNumberType);
        } else {
            returnType = _doubleOrNumberType(typeMeta.mappingType());
        }
        return returnType;
    }

    @Deprecated
    static TypeMeta _numericOrDecimal(final Expression exp) {
        final TypeMeta typeMeta;
        typeMeta = exp.typeMeta();

        final TypeMeta returnType;
        if (typeMeta instanceof TypeMeta.Delay && !((TypeMeta.Delay) typeMeta).isPrepared()) {
            returnType = CriteriaSupports.delayWrapper((TypeMeta.Delay) typeMeta, Functions::_numberOrDecimal);
        } else {
            returnType = _numberOrDecimal(typeMeta.mappingType());
        }
        return returnType;
    }

    static TypeMeta _returnType(final Expression exp, Function<MappingType, MappingType> function) {
        final TypeMeta typeMeta;
        typeMeta = exp.typeMeta();

        final TypeMeta returnType;
        if (typeMeta instanceof TypeMeta.Delay && !((TypeMeta.Delay) typeMeta).isPrepared()) {
            returnType = CriteriaSupports.delayWrapper((TypeMeta.Delay) typeMeta, function);
        } else {
            returnType = function.apply(typeMeta.mappingType());
        }
        return returnType;
    }


    /**
     * @see #_doubleOrNumeric(Expression)
     */
    static MappingType _doubleOrNumberType(final MappingType type) {
        final MappingType returnType;
        if (type instanceof _NumericType._FloatNumeric) {
            returnType = DoubleType.INSTANCE;
        } else if (type instanceof _NumericType) {
            returnType = type;
        } else {
            returnType = BigDecimalType.INSTANCE;
        }
        return returnType;
    }

    /**
     * @see #round(Expression)
     */
    static MappingType _numberOrDecimal(final MappingType type) {
        final MappingType returnType;
        if (type instanceof _NumericType) {
            returnType = type;
        } else {
            returnType = BigDecimalType.INSTANCE;
        }
        return returnType;
    }

    static MappingType _doubleOrDecimal(final MappingType type) {
        final MappingType returnType;
        if (type instanceof _NumericType._FloatNumeric) {
            returnType = DoubleType.INSTANCE;
        } else {
            returnType = BigDecimalType.INSTANCE;
        }
        return returnType;
    }

    static MappingType _sqlStringType(final MappingType type) {
        final MappingType returnType;
        if (type instanceof _SQLStringType) {
            returnType = type;
        } else {
            returnType = StringType.INSTANCE;
        }
        return returnType;
    }



    /*-------------------below private method-------------------*/

    private static String functionsKeyWordToString(final Enum<?> e) {
        return _StringUtils.builder()
                .append(Functions.class.getName())
                .append(_Constant.POINT)
                .append(e.name())
                .toString();
    }


    /**
     * private class, standard count(*) function expression
     *
     * @see #countStar()
     * @since 1.0
     */
    private static final class CountStartFunction extends OperationExpression implements SQLFunction {

        private static final CountStartFunction INSTANCE = new CountStartFunction();

        private CountStartFunction() {
        }

        @Override
        public CountStartFunction bracket() {
            //return this,don't create new instance
            return this;
        }

        @Override
        public TypeMeta typeMeta() {
            return LongType.INSTANCE;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            context.sqlBuilder().append(" COUNT(*)");
        }


    }//CountStartFunction


}
