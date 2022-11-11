package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.SqlValueParam;
import io.army.criteria.mysql.MySQLCastType;
import io.army.criteria.mysql.MySQLCharset;
import io.army.criteria.mysql.MySQLUnit;
import io.army.criteria.standard.SQLFunction;
import io.army.lang.Nullable;
import io.army.mapping.*;
import io.army.mapping.optional.JsonType;
import io.army.mapping.optional.OffsetDateTimeType;
import io.army.mapping.optional.OffsetTimeType;
import io.army.mapping.optional.ZonedDateTimeType;
import io.army.meta.TypeMeta;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This class is MySQL function utils for application developer.
 * </p>
 *
 * @since 1.0
 */
public abstract class MySQLFunctions extends MySQLMiscellaneousFunctions {

    /**
     * private constructor
     */
    private MySQLFunctions() {
    }



    /*-------------------below Cast Functions and Operators -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:
     *      <ul>
     *          <li>If type is {@link MySQLCastType#BINARY }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#CHAR }then {@link StringType}</li>
     *          <li>Else if type is {@link MySQLCastType#NCHAR }then {@link StringType}</li>
     *          <li>Else if type is {@link MySQLCastType#TIME }then {@link LocalTimeType}</li>
     *          <li>Else if type is {@link MySQLCastType#DATE }then {@link LocalDateType}</li>
     *          <li>Else if type is {@link MySQLCastType#YEAR }then {@link YearType}</li>
     *          <li>Else if type is {@link MySQLCastType#DATETIME }then {@link LocalDateTimeType}</li>
     *          <li>Else if type is {@link MySQLCastType#SIGNED_INTEGER }then {@link LongType}</li>
     *          <li>Else if type is {@link MySQLCastType#UNSIGNED_INTEGER }then {@link UnsignedBigIntegerType}</li>
     *          <li>Else if type is {@link MySQLCastType#DECIMAL }then {@link BigDecimalType}</li>
     *          <li>Else if type is {@link MySQLCastType#FLOAT }then {@link FloatType}</li>
     *          <li>Else if type is {@link MySQLCastType#REAL }then {@link DoubleType}</li>
     *          <li>Else if type is {@link MySQLCastType#DOUBLE }then {@link DoubleType}</li>
     *          <li>Else if type is {@link MySQLCastType#JSON }then {@link JsonType}</li>
     *          <li>Else if type is {@link MySQLCastType#Point }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiPoint }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiLineString }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#LineString }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#Polygon }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiPolygon }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#GeometryCollection }then {@link ByteArrayType}</li>
     *      </ul>
     * </p>
     *
     * @param exp  non-null {@link Expression}
     * @param type non-null {@link  MySQLCastType}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #cast(Expression, MySQLCastType, Expression)
     * @see #cast(Expression, MySQLCastType, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_cast">CAST(expr AS type [ARRAY])</a>
     */
    public static Expression cast(final Expression exp, final MySQLCastType type) {

        final MappingType returnType;
        final List<Object> argList = new ArrayList<>(3);

        argList.add(exp);
        argList.add(FunctionUtils.FuncWord.AS);
        argList.add(type);
        return FunctionUtils.complexArgFunc("CAST", argList, _castReturnType(type));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     *      <ul>
     *          <li>If type is {@link MySQLCastType#BINARY }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#CHAR }then {@link StringType}</li>
     *          <li>Else if type is {@link MySQLCastType#NCHAR }then {@link StringType}</li>
     *          <li>Else if type is {@link MySQLCastType#TIME }then {@link LocalTimeType}</li>
     *          <li>Else if type is {@link MySQLCastType#DATE }then {@link LocalDateType}</li>
     *          <li>Else if type is {@link MySQLCastType#YEAR }then {@link YearType}</li>
     *          <li>Else if type is {@link MySQLCastType#DATETIME }then {@link LocalDateTimeType}</li>
     *          <li>Else if type is {@link MySQLCastType#SIGNED_INTEGER }then {@link LongType}</li>
     *          <li>Else if type is {@link MySQLCastType#UNSIGNED_INTEGER }then {@link UnsignedBigIntegerType}</li>
     *          <li>Else if type is {@link MySQLCastType#DECIMAL }then {@link BigDecimalType}</li>
     *          <li>Else if type is {@link MySQLCastType#FLOAT }then {@link FloatType}</li>
     *          <li>Else if type is {@link MySQLCastType#REAL }then {@link DoubleType}</li>
     *          <li>Else if type is {@link MySQLCastType#DOUBLE }then {@link DoubleType}</li>
     *          <li>Else if type is {@link MySQLCastType#JSON }then {@link JsonType}</li>
     *          <li>Else if type is {@link MySQLCastType#Point }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiPoint }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiLineString }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#LineString }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#Polygon }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiPolygon }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#GeometryCollection }then {@link ByteArrayType}</li>
     *      </ul>
     * </p>
     *
     * @param exp  non-null {@link Expression}
     * @param type non-null {@link  MySQLCastType} ,should be below:
     *             <ul>
     *                  <li>{@link  MySQLCastType#BINARY}</li>
     *                  <li>{@link  MySQLCastType#CHAR}</li>
     *                  <li>{@link  MySQLCastType#NCHAR}</li>
     *                  <li>{@link  MySQLCastType#TIME}</li>
     *                  <li>{@link  MySQLCastType#DATETIME}</li>
     *                  <li>{@link  MySQLCastType#DECIMAL}</li>
     *                  <li>{@link  MySQLCastType#FLOAT}</li>
     *             </ul>
     * @param n    non-null literal {@link Expression},couldn't be parameter {@link  Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #cast(Expression, MySQLCastType)
     * @see #cast(Expression, MySQLCastType, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_cast">CAST(expr AS type)</a>
     */
    public static Expression cast(final Expression exp, final MySQLCastType type, final Expression n) {
        ContextStack.assertNonNull(exp);
        final String funcName = "CAST";
        if (!(n instanceof LiteralExpression.SingleLiteral || n instanceof LiteralExpression.NamedSingleLiteral)) {
            throw CriteriaUtils.funcArgError(funcName, n);
        }

        switch (type) {
            case BINARY:
            case CHAR:
            case NCHAR:
            case TIME:
            case DATETIME:
            case DECIMAL:
            case FLOAT:
                break;
            default:
                throw CriteriaUtils.funcArgError(funcName, type);
        }

        final MappingType returnType;
        final List<Object> argList = new ArrayList<>(6);

        argList.add(exp);
        argList.add(FunctionUtils.FuncWord.AS);
        argList.add(type);
        argList.add(FunctionUtils.FuncWord.LEFT_PAREN);

        argList.add(n);
        argList.add(FunctionUtils.FuncWord.RIGHT_PAREN);
        return FunctionUtils.complexArgFunc(funcName, argList, _castReturnType(type));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BigDecimalType}
     * </p>
     *
     * @param exp  non-null  {@link Expression}
     * @param type currently,support only {@link MySQLCastType#DECIMAL}
     * @param m    non-null literal {@link Expression},couldn't be parameter {@link  Expression}
     * @param d    non-null literal {@link Expression},couldn't be parameter {@link  Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #cast(Expression, MySQLCastType)
     * @see #cast(Expression, MySQLCastType, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_cast">CAST(expr AS DECIMAL(M,D))</a>
     */
    public static Expression cast(final Expression exp, final MySQLCastType type, final Expression m
            , final Expression d) {
        ContextStack.assertNonNull(exp);
        final String funcName = "CAST";

        if (type != MySQLCastType.DECIMAL) {
            throw CriteriaUtils.funcArgError(funcName, type);
        }
        if (!(m instanceof LiteralExpression.SingleLiteral || m instanceof LiteralExpression.NamedSingleLiteral)) {
            throw CriteriaUtils.funcArgError(funcName, m);
        }
        if (!(d instanceof LiteralExpression.SingleLiteral || d instanceof LiteralExpression.NamedSingleLiteral)) {
            throw CriteriaUtils.funcArgError(funcName, d);
        }

        final List<Object> argList = new ArrayList<>(8);

        argList.add(exp);
        argList.add(FunctionUtils.FuncWord.AS);
        argList.add(type);
        argList.add(FunctionUtils.FuncWord.LEFT_PAREN);

        argList.add(m);
        argList.add(FunctionUtils.FuncWord.COMMA);
        argList.add(d);
        argList.add(FunctionUtils.FuncWord.RIGHT_PAREN);
        return FunctionUtils.complexArgFunc(funcName, argList, BigDecimalType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LocalDateTimeType}
     * </p>
     *
     * @param timestampValue    non-null {@link Expression}
     * @param timezoneSpecifier non-null literal {@link Expression},couldn't be parameter {@link  Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_cast">CAST(timestamp_value AT TIME ZONE timezone_specifier AS DATETIME[(precision)])</a>
     */
    public static Expression cast(final Expression timestampValue, final Expression timezoneSpecifier) {
        ContextStack.assertNonNull(timestampValue);
        final String funcName = "CAST";

        if (!(timezoneSpecifier instanceof LiteralExpression.SingleLiteral
                || timezoneSpecifier instanceof LiteralExpression.NamedSingleLiteral)) {
            throw CriteriaUtils.funcArgError(funcName, timezoneSpecifier);
        }

        final List<Object> argList = new ArrayList<>(5);

        argList.add(timestampValue);
        argList.add(FunctionUtils.FuncWord.AT_TIME_ZONE);
        argList.add(timezoneSpecifier);
        argList.add(FunctionUtils.FuncWord.AS);

        argList.add(MySQLCastType.DATETIME);
        return FunctionUtils.complexArgFunc(funcName, argList, LocalDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LocalDateTimeType}
     * </p>
     *
     * @param timestampValue    non-null {@link Expression}
     * @param timezoneSpecifier non-null literal {@link Expression},couldn't be parameter {@link  Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_cast">CAST(timestamp_value AT TIME ZONE timezone_specifier AS DATETIME[(precision)])</a>
     */
    public static Expression cast(final Expression timestampValue, final Expression timezoneSpecifier
            , final Expression precision) {
        ContextStack.assertNonNull(timestampValue);
        final String funcName = "CAST";

        if (!(timezoneSpecifier instanceof LiteralExpression.SingleLiteral || timezoneSpecifier instanceof LiteralExpression.NamedSingleLiteral)) {
            throw CriteriaUtils.funcArgError(funcName, timezoneSpecifier);
        }
        if (!(precision instanceof LiteralExpression.SingleLiteral || precision instanceof LiteralExpression.NamedSingleLiteral)) {
            throw CriteriaUtils.funcArgError(funcName, precision);
        }

        final List<Object> argList = new ArrayList<>(8);

        argList.add(timestampValue);
        argList.add(FunctionUtils.FuncWord.AT_TIME_ZONE);
        argList.add(timezoneSpecifier);
        argList.add(FunctionUtils.FuncWord.AS);

        argList.add(MySQLCastType.DATETIME);
        argList.add(FunctionUtils.FuncWord.LEFT_PAREN);
        argList.add(precision);
        argList.add(FunctionUtils.FuncWord.RIGHT_PAREN);
        return FunctionUtils.complexArgFunc(funcName, argList, LocalDateTimeType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:
     *      <ul>
     *          <li>If type is {@link MySQLCastType#BINARY }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#CHAR }then {@link StringType}</li>
     *          <li>Else if type is {@link MySQLCastType#NCHAR }then {@link StringType}</li>
     *          <li>Else if type is {@link MySQLCastType#TIME }then {@link LocalTimeType}</li>
     *          <li>Else if type is {@link MySQLCastType#DATE }then {@link LocalDateType}</li>
     *          <li>Else if type is {@link MySQLCastType#YEAR }then {@link YearType}</li>
     *          <li>Else if type is {@link MySQLCastType#DATETIME }then {@link LocalDateTimeType}</li>
     *          <li>Else if type is {@link MySQLCastType#SIGNED_INTEGER }then {@link LongType}</li>
     *          <li>Else if type is {@link MySQLCastType#UNSIGNED_INTEGER }then {@link UnsignedBigIntegerType}</li>
     *          <li>Else if type is {@link MySQLCastType#DECIMAL }then {@link BigDecimalType}</li>
     *          <li>Else if type is {@link MySQLCastType#FLOAT }then {@link FloatType}</li>
     *          <li>Else if type is {@link MySQLCastType#REAL }then {@link DoubleType}</li>
     *          <li>Else if type is {@link MySQLCastType#DOUBLE }then {@link DoubleType}</li>
     *          <li>Else if type is {@link MySQLCastType#JSON }then {@link JsonType}</li>
     *          <li>Else if type is {@link MySQLCastType#Point }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiPoint }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiLineString }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#LineString }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#Polygon }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiPolygon }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#GeometryCollection }then {@link ByteArrayType}</li>
     *      </ul>
     * </p>
     *
     * @param exp             non-null   {@link Expression}
     * @param transcodingName non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_convert">CONVERT(expr USING transcoding_name)</a>
     */
    public static Expression convert(final Expression exp, final MySQLCharset transcodingName) {
        ContextStack.assertNonNull(exp);
        ContextStack.assertNonNull(transcodingName);

        final MappingType returnType;
        final List<Object> argList = new ArrayList<>(3);

        argList.add(exp);
        argList.add(FunctionUtils.FuncWord.USING);
        argList.add(transcodingName);
        return FunctionUtils.complexArgFunc("CONVERT", argList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     *      <ul>
     *          <li>If type is {@link MySQLCastType#BINARY }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#CHAR }then {@link StringType}</li>
     *          <li>Else if type is {@link MySQLCastType#NCHAR }then {@link StringType}</li>
     *          <li>Else if type is {@link MySQLCastType#TIME }then {@link LocalTimeType}</li>
     *          <li>Else if type is {@link MySQLCastType#DATE }then {@link LocalDateType}</li>
     *          <li>Else if type is {@link MySQLCastType#YEAR }then {@link YearType}</li>
     *          <li>Else if type is {@link MySQLCastType#DATETIME }then {@link LocalDateTimeType}</li>
     *          <li>Else if type is {@link MySQLCastType#SIGNED_INTEGER }then {@link LongType}</li>
     *          <li>Else if type is {@link MySQLCastType#UNSIGNED_INTEGER }then {@link UnsignedBigIntegerType}</li>
     *          <li>Else if type is {@link MySQLCastType#DECIMAL }then {@link BigDecimalType}</li>
     *          <li>Else if type is {@link MySQLCastType#FLOAT }then {@link FloatType}</li>
     *          <li>Else if type is {@link MySQLCastType#REAL }then {@link DoubleType}</li>
     *          <li>Else if type is {@link MySQLCastType#DOUBLE }then {@link DoubleType}</li>
     *          <li>Else if type is {@link MySQLCastType#JSON }then {@link JsonType}</li>
     *          <li>Else if type is {@link MySQLCastType#Point }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiPoint }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiLineString }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#LineString }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#Polygon }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiPolygon }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#GeometryCollection }then {@link ByteArrayType}</li>
     *      </ul>
     * </p>
     *
     * @param exp  non-null
     * @param type non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_convert">CONVERT(expr,type)</a>
     */
    public static Expression convert(final Expression exp, final MySQLCastType type) {
        ContextStack.assertNonNull(exp);
        ContextStack.assertNonNull(type);

        final MappingType returnType;
        final List<Object> argList = new ArrayList<>(3);

        argList.add(exp);
        argList.add(FunctionUtils.FuncWord.COMMA);
        argList.add(type);
        return FunctionUtils.complexArgFunc("CONVERT", argList, _castReturnType(type));
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:
     *      <ul>
     *          <li>If type is {@link MySQLCastType#BINARY }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#CHAR }then {@link StringType}</li>
     *          <li>Else if type is {@link MySQLCastType#NCHAR }then {@link StringType}</li>
     *          <li>Else if type is {@link MySQLCastType#TIME }then {@link LocalTimeType}</li>
     *          <li>Else if type is {@link MySQLCastType#DATE }then {@link LocalDateType}</li>
     *          <li>Else if type is {@link MySQLCastType#YEAR }then {@link YearType}</li>
     *          <li>Else if type is {@link MySQLCastType#DATETIME }then {@link LocalDateTimeType}</li>
     *          <li>Else if type is {@link MySQLCastType#SIGNED_INTEGER }then {@link LongType}</li>
     *          <li>Else if type is {@link MySQLCastType#UNSIGNED_INTEGER }then {@link UnsignedBigIntegerType}</li>
     *          <li>Else if type is {@link MySQLCastType#DECIMAL }then {@link BigDecimalType}</li>
     *          <li>Else if type is {@link MySQLCastType#FLOAT }then {@link FloatType}</li>
     *          <li>Else if type is {@link MySQLCastType#REAL }then {@link DoubleType}</li>
     *          <li>Else if type is {@link MySQLCastType#DOUBLE }then {@link DoubleType}</li>
     *          <li>Else if type is {@link MySQLCastType#JSON }then {@link JsonType}</li>
     *          <li>Else if type is {@link MySQLCastType#Point }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiPoint }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiLineString }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#LineString }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#Polygon }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiPolygon }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#GeometryCollection }then {@link ByteArrayType}</li>
     *      </ul>
     * </p>
     *
     * @param exp  nullable parameter or {@link Expression}
     * @param type non-null {@link  MySQLCastType} ,should be below:
     *             <ul>
     *                  <li>{@link  MySQLCastType#BINARY}</li>
     *                  <li>{@link  MySQLCastType#CHAR}</li>
     *                  <li>{@link  MySQLCastType#NCHAR}</li>
     *                  <li>{@link  MySQLCastType#TIME}</li>
     *                  <li>{@link  MySQLCastType#DATETIME}</li>
     *                  <li>{@link  MySQLCastType#DECIMAL}</li>
     *                  <li>{@link  MySQLCastType#FLOAT}</li>
     *             </ul>
     * @param n    non-null literal {@link Expression},couldn't be parameter {@link  Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_convert">CONVERT(expr,type)</a>
     */
    public static Expression convert(final Expression exp, final MySQLCastType type, final Expression n) {
        ContextStack.assertNonNull(exp);
        final String funcName = "CONVERT";
        if (!MySQLUtils.isSingleParamType(type)) {
            throw CriteriaUtils.funcArgError(funcName, type);
        }
        if (!(n instanceof LiteralExpression.SingleLiteral || n instanceof LiteralExpression.NamedSingleLiteral)) {
            throw CriteriaUtils.funcArgError(funcName, n);
        }
        final MappingType returnType;
        final List<Object> argList = new ArrayList<>(6);

        argList.add(exp);
        argList.add(FunctionUtils.FuncWord.COMMA);
        argList.add(type);
        argList.add(FunctionUtils.FuncWord.LEFT_PAREN);

        argList.add(n);
        argList.add(FunctionUtils.FuncWord.RIGHT_PAREN);
        return FunctionUtils.complexArgFunc(funcName, argList, _castReturnType(type));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BigDecimalType}
     * </p>
     *
     * @param exp  non-null
     * @param type currently must be {@link MySQLCastType#DECIMAL}
     * @param m    non-null literal {@link Expression},couldn't be parameter {@link  Expression}
     * @param d    non-null literal {@link Expression},couldn't be parameter {@link  Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_convert">CONVERT(expr,type)</a>
     */
    public static Expression convert(final Expression exp, final MySQLCastType type, final Expression m
            , final Expression d) {
        ContextStack.assertNonNull(exp);
        final String funcName = "CONVERT";

        if (type != MySQLCastType.DECIMAL) {
            throw CriteriaUtils.funcArgError(funcName, type);
        }
        if (!(m instanceof LiteralExpression.SingleLiteral || m instanceof LiteralExpression.NamedSingleLiteral)) {
            throw CriteriaUtils.funcArgError(funcName, m);
        }
        if (!(d instanceof LiteralExpression.SingleLiteral || d instanceof LiteralExpression.NamedSingleLiteral)) {
            throw CriteriaUtils.funcArgError(funcName, d);
        }

        final MappingType returnType;
        final List<Object> argList = new ArrayList<>(8);

        argList.add(exp);
        argList.add(FunctionUtils.FuncWord.COMMA);
        argList.add(type);
        argList.add(FunctionUtils.FuncWord.LEFT_PAREN);

        argList.add(m);
        argList.add(FunctionUtils.FuncWord.COMMA);
        argList.add(d);
        argList.add(FunctionUtils.FuncWord.RIGHT_PAREN);

        return FunctionUtils.complexArgFunc(funcName, argList, BigDecimalType.INSTANCE);
    }



    /*-------------------below Bit Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param n nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/bit-functions.html#function_bit-count">BIT_COUNT(N)</a>
     */
    public static Expression bitCount(final Expression n) {
        return FunctionUtils.oneArgFunc("BIT_COUNT", n, IntegerType.INSTANCE);
    }



    /*-------------------below Flow Control Functions-------------------*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/flow-control-functions.html">Flow Control Functions</a>
     */
    public static SQLFunction._CaseFuncWhenClause<Expression> Case(Expression exp) {
        return MySQLs.Case(exp, SQLs::_asExp, SQLs::_identity);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:
     *      <ul>
     *          <li>If the {@link MappingType} of expr2 and the {@link MappingType} of expr3 same,then return type is the {@link MappingType} of expr2</li>
     *          <li>If expr2 or expr3 produce a string, the result is {@link StringType}</li>
     *          <li>If expr2 and expr3 are both not numeric, the result is {@link StringType}</li>
     *          <li>If expr2 or expr3 produce a floating-point value,the result is {@link DoubleType}</li>
     *          <li>If expr2 or expr3 produce unsigned numeric:
     *              <ul>
     *                  <li>If expr2 or expr3 {@link MappingType} is {@link  UnsignedBigDecimalType},the result is {@link UnsignedBigDecimalType}</li>
     *                  <li>If expr2 or expr3 {@link MappingType} is {@link  UnsignedBigIntegerType},the result is {@link UnsignedBigIntegerType}</li>
     *                  <li>If expr2 or expr3 {@link MappingType} is {@link UnsignedLongType},the result is {@link UnsignedLongType}</li>
     *                  <li>Otherwise the result is {@link IntegerType}</li>
     *              </ul>
     *          </li>
     *          <li>If expr2 or expr3 {@link MappingType} is {@link  BigDecimalType},the result is {@link BigDecimalType}</li>
     *          <li>If expr2 or expr3 {@link MappingType} is {@link  BigIntegerType},the result is {@link BigIntegerType}</li>
     *          <li>If expr2 or expr3 {@link MappingType} is {@link LongType},the result is {@link LongType}</li>
     *          <li>Otherwise the result is {@link IntegerType}</li>
     *      </ul>
     * </p>
     *
     * @throws CriteriaException throw when expr2 and expr3 are both non-operate {@link Expression},eg:{@link SQLs#nullWord()}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/flow-control-functions.html#function_if">IF(expr1,expr2,expr3)</a>
     */
    public static Expression ifFunc(final IPredicate predicate, final Expression expr2, final Expression expr3) {

        final TypeMeta returnType;
        returnType = Functions._returnType((ArmyExpression) expr2, (ArmyExpression) expr3
                , MySQLFuncSyntax::ifFuncReturnType);
        return FunctionUtils.threeArgFunc("IF", predicate, expr2, expr3, returnType);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:
     * </p>
     *
     * @throws CriteriaException throw when expr2 and expr3 are both non-operate {@link Expression},eg:{@link SQLs#nullWord()}
     * @see #ifFunc(IPredicate, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/flow-control-functions.html#function_ifnull">IFNULL(expr1,expr2)</a>
     */
    public static Expression ifNull(final Expression expr1, final Expression expr2) {
        final TypeMeta returnType;
        returnType = Functions._returnType((ArmyExpression) expr1, (ArmyExpression) expr2
                , MySQLFuncSyntax::ifNullReturnType);
        return FunctionUtils.twoArgFunc("IFNULL", expr1, expr2, returnType);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link  MappingType} of expr1
     * </p>
     *
     * @throws CriteriaException throw when expr2 and expr3 are both non-operate {@link Expression},eg:{@link SQLs#nullWord()}
     * @see #ifFunc(IPredicate, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/flow-control-functions.html#function_ifnull">IFNULL(expr1,expr2)</a>
     */
    public static Expression nullIf(final Expression expr1, final Expression expr2) {
        return FunctionUtils.twoArgFunc("NULLIF", expr1, expr2, expr1.typeMeta());
    }



    /*-------------------below private method-------------------*/


    /**
     * <p>
     * The {@link MappingType} of function return type:
     *      <ul>
     *          <li>If date or expr is NULL, {@link _NullType}</li>
     *          <li>If date {@link MappingType} is {@link LocalDateType} and unit no time parts then {@link LocalDateType},otherwise {@link LocalDateTimeType}</li>
     *          <li>If date {@link MappingType} is {@link LocalTimeType} and unit no date parts then {@link LocalTimeType},otherwise {@link LocalDateTimeType}</li>
     *          <li>If date {@link MappingType} is {@link LocalDateTimeType} or {@link OffsetDateTimeType} or {@link ZonedDateTimeType} then {@link LocalDateTimeType}</li>
     *          <li>otherwise {@link StringType}</li>
     *      </ul>
     * </p>
     *
     * @param funcName DATE_ADD or DATE_SUB
     * @param date     nullable parameter or {@link Expression}
     * @param expr     nullable parameter or {@link Expression}
     * @param unit     non-null
     * @see #dateAdd(Expression, Expression, MySQLUnit)
     * @see #dateSub(Expression, Expression, MySQLUnit)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_date-add">DATE_ADD(date,INTERVAL expr unit), DATE_SUB(date,INTERVAL expr unit)</a>
     */
    static Expression _dateAddOrSub(final String funcName, final Expression date
            , final Expression expr, final MySQLUnit unit) {
        final TypeMeta type, returnType;
        type = date.typeMeta();
        if (type instanceof TypeMeta.Delay) {
            returnType = CriteriaSupports.delayParamMeta((TypeMeta.Delay) type, t -> _dateAddSubReturnType(t, unit));
        } else {
            returnType = _dateAddSubReturnType(type.mappingType(), unit);
        }
        final List<Object> argList = new ArrayList<>(5);

        argList.add(date);
        argList.add(FunctionUtils.FuncWord.COMMA);
        argList.add(FunctionUtils.FuncWord.INTERVAL);
        argList.add(expr);

        argList.add(unit);
        return FunctionUtils.complexArgFunc(funcName, argList, returnType);
    }


    /**
     * @see #dateAdd(Expression, Expression, MySQLUnit)
     */
    private static MappingType _dateAddSubReturnType(final MappingType type, final MySQLUnit unit) {
        final MappingType returnType;
        if (type instanceof _NullType) {
            returnType = type;
        } else if (type instanceof LocalDateType) {
            switch (unit) {
                case YEAR:
                case QUARTER:
                case MONTH:
                case WEEK:
                case DAY:
                    returnType = LocalDateType.INSTANCE;
                    break;
                default:
                    returnType = LocalDateTimeType.INSTANCE;
            }
        } else if (type instanceof LocalTimeType || type instanceof OffsetTimeType) {
            switch (unit) {
                case HOUR:
                case MINUTE:
                case SECOND:
                case MICROSECOND:
                    returnType = LocalTimeType.INSTANCE;
                    break;
                default:
                    returnType = LocalDateTimeType.INSTANCE;
            }
        } else if (type instanceof LocalDateTimeType
                || type instanceof OffsetDateTimeType
                || type instanceof ZonedDateTimeType) {
            returnType = LocalDateTimeType.INSTANCE;
        } else {
            returnType = StringType.INSTANCE;
        }
        return returnType;
    }


    /**
     * @see #strToDate(Expression, Expression)
     */
    private static MappingType _strToDateReturnType(final ArmyExpression formatExp, final MappingType type) {
        final MappingType returnType;
        if (formatExp instanceof SqlValueParam.SingleNonNamedValue
                && type instanceof StringType) {
            final Object value;
            value = ((SqlValueParam.SingleNonNamedValue) formatExp).value();
            if (value instanceof String) {
                returnType = _parseStrToDateReturnType((String) value);
            } else {
                returnType = StringType.INSTANCE;
            }
        } else {
            returnType = StringType.INSTANCE;
        }
        return returnType;
    }

    /**
     * @see #_strToDateReturnType(ArmyExpression, MappingType)
     */
    private static MappingType _parseStrToDateReturnType(final String format) {
        final char[] array = format.toCharArray();
        final int last = array.length - 1;
        boolean date = false, time = false;
        outerFor:
        for (int i = 0; i < array.length; i++) {
            if (array[i] != '%' || i == last) {
                continue;
            }
            switch (array[i + 1]) {
                case 'a'://Abbreviated weekday name (Sun..Sat)
                case 'b'://Abbreviated month name (Jan..Dec)
                case 'c'://Month, numeric (0..12)
                case 'D'://Day of the month with English suffix (0th, 1st, 2nd, 3rd, …)
                case 'd'://Day of the month, numeric (00..31)
                case 'e'://Day of the month, numeric (0..31)
                case 'j'://Day of year (001..366)
                case 'M'://Month name (January..December)
                case 'U'://Week (00..53), where Sunday is the first day of the week; WEEK() mode 0
                case 'u'://Week (00..53), where Monday is the first day of the week; WEEK() mode 1
                case 'V'://Week (01..53), where Sunday is the first day of the week; WEEK() mode 2; used with %X
                case 'v'://Week (01..53), where Monday is the first day of the week; WEEK() mode 3; used with %x
                case 'W'://Weekday name (Sunday..Saturday)
                case 'w'://Day of the week (0=Sunday..6=Saturday)
                case 'X'://Year for the week where Sunday is the first day of the week, numeric, four digits; used with %V
                case 'x'://Year for the week, where Monday is the first day of the week, numeric, four digits; used with %v
                case 'Y'://Year, numeric, four digits
                case 'y': {//Year, numeric (two digits)
                    date = true;
                    if (time) {
                        break outerFor;
                    }
                }
                break;
                case 'H'://Hour (00..23)
                case 'h'://Hour (01..12)
                case 'I'://Hour (01..12)
                case 'k'://Hour (0..23)
                case 'l'://Hour (1..12)
                case 'P'://AM or PM
                case 'i'://Minutes, numeric (00..59)
                case 'r'://Time, 12-hour (hh:mm:ss followed by AM or PM)
                case 'S'://Seconds (00..59)
                case 's'://Seconds (00..59)
                case 'T'://Time, 24-hour (hh:mm:ss)
                case 'f': {//Microseconds (000000..999999)
                    time = true;
                    if (date) {
                        break outerFor;
                    }
                }
                break;
                default:
                    //A literal % character
                    //x, for any “x” not listed above
            }

            i++;
        }

        final MappingType type;
        if (date && time) {
            type = LocalDateTimeType.INSTANCE;
        } else if (date) {
            type = LocalDateType.INSTANCE;
        } else if (time) {
            type = LocalTimeType.INSTANCE;
        } else {
            type = StringType.INSTANCE;
        }
        return type;
    }


    /**
     * @see #addDate(Expression, Expression, MySQLUnit)
     * @see #subDate(Expression, Expression, MySQLUnit)
     */
    private static Expression _dateIntervalFunc(final String funcName, final Expression date
            , final Expression expr, final MySQLUnit unit) {
        final TypeMeta returnType;
        if (unit.isTimePart()) {
            returnType = LocalDateTimeType.INSTANCE;
        } else {
            returnType = LocalDateType.INSTANCE;
        }

        final List<Object> argList = new ArrayList<>(5);

        argList.add(date);
        argList.add(FunctionUtils.FuncWord.COMMA);
        argList.add(FunctionUtils.FuncWord.INTERVAL);
        argList.add(expr);

        argList.add(unit);
        return FunctionUtils.complexArgFunc(funcName, argList, returnType);
    }


    /**
     * @see #addDate(Expression, Expression)
     * @see #subDate(Expression, Expression)
     */
    private static Expression _operateDateFunc(final String funcName, final @Nullable Object date
            , final @Nullable Object days) {
        final List<Object> argList = new ArrayList<>(3);
        argList.add(SQLs._funcParam(date));
        argList.add(FunctionUtils.FuncWord.COMMA);
        argList.add(SQLs._funcParam(days));
        return FunctionUtils.complexArgFunc(funcName, argList, LocalDateType.INSTANCE);
    }

    /**
     * @see #timestampAdd(MySQLUnit, Expression, Expression)
     */
    private static MappingType _timestampAdd(final MappingType type) {
        final MappingType returnType;
        if (type instanceof LocalDateType) {
            returnType = LocalDateType.INSTANCE;
        } else if (type instanceof LocalDateTimeType
                || type instanceof ZonedDateTimeType
                || type instanceof OffsetDateTimeType) {
            returnType = LocalDateTimeType.INSTANCE;
        } else {
            returnType = StringType.INSTANCE;
        }
        return returnType;
    }


    /**
     * @see #ifFunc(IPredicate, Expression, Expression)
     */
    private static MappingType ifFuncReturnType(final MappingType expr2Type, final MappingType expr3Type) {
        final MappingType returnType;
        if (expr2Type.getClass() == expr3Type.getClass()) {
            returnType = expr2Type;
        } else if (expr2Type instanceof _SQLStringType || expr3Type instanceof _SQLStringType) {
            returnType = StringType.INSTANCE;
        } else if (!(expr2Type instanceof _NumericType || expr3Type instanceof _NumericType)) {
            returnType = StringType.INSTANCE;
        } else if (expr2Type instanceof _NumericType._FloatNumericType
                || expr3Type instanceof _NumericType._FloatNumericType) {
            returnType = DoubleType.INSTANCE;
        } else if (expr2Type instanceof _NumericType._UnsignedNumeric
                || expr3Type instanceof _NumericType._UnsignedNumeric) {
            if (expr2Type instanceof UnsignedBigDecimalType || expr3Type instanceof UnsignedBigDecimalType) {
                returnType = UnsignedBigDecimalType.INSTANCE;
            } else if (expr2Type instanceof UnsignedBigIntegerType || expr3Type instanceof UnsignedBigIntegerType) {
                returnType = UnsignedBigIntegerType.INSTANCE;
            } else if (expr2Type instanceof UnsignedLongType || expr3Type instanceof UnsignedLongType) {
                returnType = UnsignedLongType.INSTANCE;
            } else {
                returnType = UnsignedIntegerType.INSTANCE;
            }
        } else if (expr2Type instanceof BigDecimalType || expr3Type instanceof BigDecimalType) {
            returnType = BigDecimalType.INSTANCE;
        } else if (expr2Type instanceof BigIntegerType || expr3Type instanceof BigIntegerType) {
            returnType = BigIntegerType.INSTANCE;
        } else if (expr2Type instanceof LongType || expr3Type instanceof LongType) {
            returnType = LongType.INSTANCE;
        } else {
            returnType = IntegerType.INSTANCE;
        }
        return returnType;
    }


    /**
     * @see #ifNull(Expression, Expression)
     */
    private static MappingType ifNullReturnType(final MappingType expr1Type, final MappingType expr2Type) {
        final MappingType returnType;
        if (expr1Type.getClass() == expr2Type.getClass()) {
            returnType = expr1Type;
        } else if (!(expr1Type instanceof _NumericType && expr2Type instanceof _NumericType)) {
            returnType = StringType.INSTANCE;
        } else if (expr1Type instanceof _NumericType._DecimalNumeric && expr2Type instanceof _NumericType._DecimalNumeric) {
            if (expr1Type instanceof _NumericType._UnsignedNumeric
                    && expr2Type instanceof _NumericType._UnsignedNumeric) {
                returnType = UnsignedBigDecimalType.INSTANCE;
            } else {
                returnType = BigDecimalType.INSTANCE;
            }
        } else if (!(expr1Type instanceof _NumericType._IntegerNumeric
                || expr2Type instanceof _NumericType._IntegerNumeric)) {
            returnType = DoubleType.INSTANCE;
        } else if (expr1Type instanceof _NumericType._UnsignedNumeric
                && expr2Type instanceof _NumericType._UnsignedNumeric) {
            if (expr1Type instanceof UnsignedBigIntegerType || expr2Type instanceof UnsignedBigIntegerType) {
                returnType = UnsignedBigIntegerType.INSTANCE;
            } else if (expr1Type instanceof UnsignedLongType || expr2Type instanceof UnsignedLongType) {
                returnType = UnsignedLongType.INSTANCE;
            } else {
                returnType = UnsignedIntegerType.INSTANCE;
            }
        } else if (expr1Type instanceof BigIntegerType || expr2Type instanceof BigIntegerType) {
            returnType = BigIntegerType.INSTANCE;
        } else if (expr1Type instanceof LongType || expr2Type instanceof LongType) {
            returnType = LongType.INSTANCE;
        } else {
            returnType = IntegerType.INSTANCE;
        }
        return returnType;
    }


    /**
     * @see #lpad(Expression, Expression, Expression)
     * @see #rpad(Expression, Expression, Expression)
     */
    private static Expression _leftOrRightPad(final String funcName, final @Nullable Object str
            , final @Nullable Object len, final @Nullable Object padstr) {
        final List<Object> argList = new ArrayList<>(5);

        argList.add(SQLs._funcParam(StringType.INSTANCE, str));
        argList.add(FunctionUtils.FuncWord.COMMA);
        argList.add(SQLs._funcParam(IntegerType.INSTANCE, len));
        argList.add(FunctionUtils.FuncWord.COMMA);

        argList.add(SQLs._funcParam(StringType.INSTANCE, padstr));
        return FunctionUtils.complexArgFunc(funcName, argList, StringType.INSTANCE);
    }


    /**
     * @see #cast(Expression, MySQLCastType)
     * @see #convert(Expression, MySQLCastType)
     * @see #convert(Expression, MySQLCastType, Expression)
     */
    static MappingType _castReturnType(final MySQLCastType type) {
        final MappingType returnType;
        switch (type) {
            case BINARY:
            case Point:
            case MultiPoint:
            case MultiLineString:
            case LineString:
            case Polygon:
            case MultiPolygon:
            case GeometryCollection:
                returnType = ByteArrayType.INSTANCE;
                break;
            case CHAR:
            case NCHAR:
            case JSON:
                returnType = StringType.INSTANCE;
                break;
            case TIME:
                returnType = LocalTimeType.INSTANCE;
                break;
            case DATE:
                returnType = LocalDateType.INSTANCE;
                break;
            case YEAR:
                returnType = YearType.INSTANCE;
                break;
            case DATETIME:
                returnType = LocalDateTimeType.INSTANCE;
                break;
            case SIGNED_INTEGER:
                returnType = LongType.INSTANCE;
                break;
            case UNSIGNED_INTEGER:
                returnType = UnsignedBigIntegerType.INSTANCE;
                break;
            case DECIMAL:
                returnType = BigDecimalType.INSTANCE;
                break;
            case FLOAT:
                returnType = FloatType.INSTANCE;
                break;
            case REAL:
            case DOUBLE://Added in MySQL 8.0.17.
                returnType = DoubleType.INSTANCE;
                break;
            default:
                throw _Exceptions.unexpectedEnum(type);

        }
        return returnType;
    }


    /**
     * @see #jsonArrayAppend(Expression, List)
     * @see #jsonArrayInsert(Expression, List)
     * @see #jsonInsert(Expression, List)
     */
    private static Expression _jsonPathValOperateFunc(final String name, final Expression jsonDoc
            , final List<Expression> pathValList) {
        if (jsonDoc instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, jsonDoc);
        }
        final int size = pathValList.size();
        if (size == 0 || (size & 1) != 0) {
            throw CriteriaUtils.funcArgError(name, pathValList);
        }
        final List<Object> argList = new ArrayList<>(((1 + size) << 1) - 1);
        argList.add(jsonDoc);
        for (Expression exp : pathValList) {
            if (exp instanceof SqlValueParam.MultiValue) {
                throw CriteriaUtils.funcArgError(name, exp);
            }
            argList.add(FunctionUtils.FuncWord.COMMA);
            argList.add(exp);
        }
        return FunctionUtils.complexArgFunc(name, argList, jsonDoc.typeMeta());
    }


    /**
     * @see #jsonMerge(List)
     */
    private static Expression _jsonMergerFunc(final String name, final List<Expression> jsonDocList) {
        final int size = jsonDocList.size();
        if (size < 2) {
            throw CriteriaUtils.funcArgError(name, jsonDocList);
        }
        final List<Object> argList = new ArrayList<>(size);
        int index = 0;
        for (Expression jsonDoc : jsonDocList) {
            if (index > 0) {
                argList.add(FunctionUtils.FuncWord.COMMA);
            }
            argList.add(jsonDoc);
            index++;
        }
        return FunctionUtils.complexArgFunc(name, argList, jsonDocList.get(0).typeMeta());
    }


}
