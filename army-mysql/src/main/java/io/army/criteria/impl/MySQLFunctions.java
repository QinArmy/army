package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.mysql.MySQLCastType;
import io.army.criteria.mysql.MySQLCharset;
import io.army.criteria.mysql.MySQLUnit;
import io.army.lang.Nullable;
import io.army.mapping.*;
import io.army.mapping.optional.JsonType;
import io.army.mapping.optional.OffsetDateTimeType;
import io.army.mapping.optional.OffsetTimeType;
import io.army.mapping.optional.ZonedDateTimeType;
import io.army.meta.FieldMeta;
import io.army.meta.TypeMeta;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.List;

public abstract class MySQLFunctions extends Functions {

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

    /*-------------------below Miscellaneous Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link  MappingType} of arg
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_any-value">ANY_VALUE(arg)</a>
     */
    public static Expression anyValue(final Expression arg) {
        return FunctionUtils.oneArgFunc("ANY_VALUE", arg, arg.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_bin-to-uuid">BIN_TO_UUID(binary_uuid, swap_flag)</a>
     */
    public static Expression binToUuid(final Expression binaryUuid) {
        return FunctionUtils.oneArgFunc("BIN_TO_UUID", binaryUuid, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_bin-to-uuid">BIN_TO_UUID(binary_uuid, swap_flag)</a>
     */
    public static Expression binToUuid(final Expression binaryUuid, final Expression swapFlag) {
        return FunctionUtils.twoArgFunc("BIN_TO_UUID", binaryUuid, swapFlag, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link  MappingType} of field
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_default">DEFAULT(col_name)</a>
     */
    public static Expression defaultValue(final TableField field) {
        return FunctionUtils.oneArgFunc("DEFAULT", field, field);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     * </p>
     *
     * @param expr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_default">GROUPING(expr [, expr] ...)</a>
     */
    public static IPredicate grouping(final Expression expr) {
        return FunctionUtils.oneArgFuncPredicate("GROUPING", expr);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     * </p>
     *
     * @param expList size greater than one
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_default">GROUPING(expr [, expr] ...)</a>
     */
    public static IPredicate grouping(final List<Expression> expList) {
        final String funcName = "GROUPING";
        if (expList.size() == 0) {
            throw CriteriaUtils.funcArgError(funcName, expList);
        }
        return FunctionUtils.complexArgPredicate(funcName, _createSimpleMultiArgList(expList));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * </p>
     *
     * @param expr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_inet-aton">INET_ATON(expr)</a>
     */
    public static Expression inetAton(final Expression expr) {
        return FunctionUtils.oneArgFunc("INET_ATON", expr, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @param expr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_inet-ntoa">INET_NTOA(expr)</a>
     */
    public static Expression inetNtoa(final Expression expr) {
        return FunctionUtils.oneArgFunc("INET_NTOA", expr, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link ByteArrayType}
     * </p>
     *
     * @param expr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_inet6-aton">INET6_ATON(expr)</a>
     */
    public static Expression inet6Aton(final Expression expr) {
        return FunctionUtils.oneArgFunc("INET6_ATON", expr, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @param expr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_inet6-ntoa">INET6_NTOA(expr)</a>
     */
    public static Expression inet6Ntoa(final Expression expr) {
        return FunctionUtils.oneArgFunc("INET6_NTOA", expr, StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     * </p>
     *
     * @param expr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_is-ipv4">IS_IPV4(expr)</a>
     */
    public static IPredicate isIpv4(final Expression expr) {
        return FunctionUtils.oneArgFuncPredicate("IS_IPV4", expr);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     * </p>
     *
     * @param expr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_is-ipv4-compat">IS_IPV4_COMPAT(expr)</a>
     */
    public static IPredicate isIpv4Compat(final Expression expr) {
        return FunctionUtils.oneArgFuncPredicate("IS_IPV4_COMPAT", expr);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     * </p>
     *
     * @param expr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_is-ipv4-mapped">IS_IPV4_MAPPED(expr)</a>
     */
    public static IPredicate isIpv4Mapped(final Expression expr) {
        return FunctionUtils.oneArgFuncPredicate("IS_IPV4_MAPPED", expr);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     * </p>
     *
     * @param expr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_is-ipv6">IS_IPV6(expr)</a>
     */
    public static IPredicate isIpv6(final Expression expr) {
        return FunctionUtils.oneArgFuncPredicate("IS_IPV6", expr);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     * </p>
     *
     * @param stringUuid non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_is-uuid">IS_UUID(string_uuid)</a>
     */
    public static IPredicate isUuid(final Expression stringUuid) {
        return FunctionUtils.oneArgFuncPredicate("IS_UUID", stringUuid);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * </p>
     *
     * @param expList non-null,size is 0 or in [2,4]
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_master-pos-wait">MASTER_POS_WAIT(log_name,log_pos[,timeout][,channel])</a>
     */
    public static Expression masterPosWait(final List<Expression> expList) {
        final String name = "MASTER_POS_WAIT";
        final Expression func;
        switch (expList.size()) {
            case 0:
                func = FunctionUtils.noArgFunc(name, IntegerType.INSTANCE);
                break;
            case 2:
            case 3:
            case 4:
                func = FunctionUtils.complexArgFunc(name, _createSimpleMultiArgList(expList), IntegerType.INSTANCE);
                break;
            default:
                throw CriteriaUtils.funcArgError(name, expList);
        }
        return func;
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of value
     * </p>
     *
     * @param name  non-null,parameter {@link Expression} or literal {@link Expression}
     *              ,couldn't be named parameter {@link Expression} or named literal {@link Expression}
     * @param value non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_name-const">NAME_CONST(name,value)</a>
     */
    public static NamedExpression nameConst(final Expression name, final Expression value) {
        final String funcName = "NAME_CONST";
        final Object paramValue;
        if (!(name instanceof SqlValueParam.SingleNonNamedValue
                && (paramValue = ((SqlValueParam.SingleNonNamedValue) name).value()) instanceof String)) {
            throw CriteriaUtils.funcArgError(funcName, name);
        }
        final List<Object> argList = new ArrayList<>(3);
        argList.add(name);
        argList.add(FunctionUtils.FuncWord.COMMA);
        argList.add(value);
        return FunctionUtils.namedComplexArgFunc("NAME_CONST", argList, value.typeMeta(), (String) paramValue);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * </p>
     *
     * @param duration non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_sleep">SLEEP(duration)</a>
     */
    public static Expression sleep(final Expression duration) {
        return FunctionUtils.oneArgFunc("SLEEP", duration, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * </p>
     *
     * @param expList non-null,size is 0 or in [2,4]
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_source-pos-wait">SOURCE_POS_WAIT(log_name,log_pos[,timeout][,channel])</a>
     */
    public static Expression sourcePosWait(final List<Expression> expList) {
        final String name = "SOURCE_POS_WAIT";
        final Expression func;
        switch (expList.size()) {
            case 0:
                func = FunctionUtils.noArgFunc(name, IntegerType.INSTANCE);
                break;
            case 2:
            case 3:
            case 4:
                func = FunctionUtils.complexArgFunc(name, _createSimpleMultiArgList(expList), IntegerType.INSTANCE);
                break;
            default:
                throw CriteriaUtils.funcArgError(name, expList);
        }
        return func;
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_uuid">UUID()</a>
     */
    public static Expression uuid() {
        return FunctionUtils.noArgFunc("UUID", StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LongType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_uuid-short">UUID_SHORT()</a>
     */
    public static Expression uuidShort() {
        return FunctionUtils.noArgFunc("UUID_SHORT", LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link ByteArrayType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_uuid-to-bin">UUID_TO_BIN(string_uuid)</a>
     */
    public static Expression uuidToBin(final Expression stringUuid) {
        return FunctionUtils.oneArgFunc("UUID_TO_BIN", stringUuid, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link ByteArrayType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_uuid-to-bin">UUID_TO_BIN(string_uuid, swap_flag)</a>
     */
    public static Expression uuidToBin(final Expression stringUuid, final Expression swapFlag) {
        return _simpleTowArgFunc("UUID_TO_BIN", stringUuid, swapFlag, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of field
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/miscellaneous-functions.html#function_values">VALUES(col_name)</a>
     */
    public static Expression values(final FieldMeta<?> field) {
        return FunctionUtils.oneArgFunc("VALUES", field, field);
    }




    /*-------------------below Encryption and Compression Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param cryptStr   non-null
     * @param keyStr     non-null
     * @param argExpList non-null,size in [0,4]
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_aes-decrypt">AES_DECRYPT(crypt_str,key_str[,init_vector][,kdf_name][,salt][,info | iterations])</a>
     */
    public static Expression aesDecrypt(final Expression cryptStr, final Expression keyStr
            , final List<Expression> argExpList) {
        return _aesEncryptOrDecrypt("AES_DECRYPT", cryptStr, keyStr, argExpList);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str        non-null
     * @param keyStr     non-null
     * @param argExpList non-null,size in [0,4]
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_aes-encrypt">AES_ENCRYPT(str,key_str[,init_vector][,kdf_name][,salt][,info | iterations])</a>
     */
    public static Expression aesEncrypt(final Expression str, final Expression keyStr
            , final List<Expression> argExpList) {
        return _aesEncryptOrDecrypt("AES_ENCRYPT", str, keyStr, argExpList);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param stringToCompress non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_compress">COMPRESS(string_to_compress)</a>
     */
    public static Expression compress(final Expression stringToCompress) {
        return FunctionUtils.oneArgFunc("COMPRESS", stringToCompress, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param stringToUnCompress non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_uncompress">UNCOMPRESS(string_to_uncompress)</a>
     */
    public static Expression unCompress(final Expression stringToUnCompress) {
        return FunctionUtils.oneArgFunc("UNCOMPRESS", stringToUnCompress, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_md5">MD5(str)</a>
     */
    public static Expression md5(final Expression str) {
        return FunctionUtils.oneArgFunc("MD5", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param len non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_md5">RANDOM_BYTES(len)</a>
     */
    public static Expression randomBytes(final Expression len) {
        return FunctionUtils.oneArgFunc("RANDOM_BYTES", len, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_sha1">SHA1(str)</a>
     */
    public static Expression sha1(final Expression str) {
        return FunctionUtils.oneArgFunc("SHA1", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_sha1">SHA(str)</a>
     */
    public static Expression sha(final Expression str) {
        return FunctionUtils.oneArgFunc("SHA", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str        non-null
     * @param hashLength non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_sha2">SHA2(str, hash_length)</a>
     */
    public static Expression sha2(final Expression str, final Expression hashLength) {
        final List<Object> argList = new ArrayList<>(3);
        argList.add(str);
        argList.add(FunctionUtils.FuncWord.COMMA);
        argList.add(hashLength);
        return FunctionUtils.complexArgFunc("SHA2", argList, StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param stmt    non-null
     * @param visible non-null
     * @param literal true:output literal
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_statement-digest">STATEMENT_DIGEST(statement)</a>
     */
    public static Expression statementDigest(final PrimaryStatement stmt, final Visible visible, final boolean literal) {
        return MySQLFunctionUtils.statementDigest(stmt, visible, literal);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param stmt    non-null ,sql
     * @param visible non-null
     * @param literal true:output literal
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_statement-digest">STATEMENT_DIGEST(statement)</a>
     */
    public static Expression statementDigest(final String stmt, final Visible visible, final boolean literal) {
        return MySQLFunctionUtils.statementDigest(stmt, visible, literal);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param stmt    non-null
     * @param visible non-null
     * @param literal true:output literal
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_statement-digest-text">STATEMENT_DIGEST_TEXT(statement)</a>
     */
    public static Expression statementDigestText(final PrimaryStatement stmt, final Visible visible
            , final boolean literal) {
        return MySQLFunctionUtils.statementDigestText(stmt, visible, literal);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param stmt    non-null,sql
     * @param visible non-null
     * @param literal true:output literal
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_statement-digest-text">STATEMENT_DIGEST_TEXT(statement)</a>
     */
    public static Expression statementDigestText(final String stmt, final Visible visible, final boolean literal) {
        return MySQLFunctionUtils.statementDigestText(stmt, visible, literal);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param compressedString non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_uncompressed-length">UNCOMPRESSED_LENGTH(compressed_string)</a>
     */
    public static Expression unCompressedLength(final Expression compressedString) {
        return FunctionUtils.oneArgFunc("UNCOMPRESSED_LENGTH", compressedString, IntegerType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_validate-password-strength">VALIDATE_PASSWORD_STRENGTH(str)</a>
     */
    public static Expression validatePasswordStrength(final Expression str) {
        return FunctionUtils.oneArgFunc("VALIDATE_PASSWORD_STRENGTH", str, IntegerType.INSTANCE);
    }


    /*-------------------below XML Functions-------------------*/


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param xmlFrag   non-null
     * @param xpathExpr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/xml-functions.html#function_extractvalue">ExtractValue(xml_frag, xpath_expr)</a>
     */
    public static Expression extractValue(final Expression xmlFrag, final Expression xpathExpr) {
        return FunctionUtils.twoArgFunc("ExtractValue", xmlFrag, xpathExpr, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param xmlTarget non-null
     * @param xpathExpr non-null
     * @param newXml    non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/xml-functions.html#function_updatexml">UpdateXML(xml_target, xpath_expr, new_xml)</a>
     */
    public static Expression updateXml(final Expression xmlTarget, final Expression xpathExpr, final Expression newXml) {
        return FunctionUtils.threeArgFunc("UpdateXML", xmlTarget, xpathExpr, newXml, StringType.INSTANCE);
    }



    /*-------------------below Locking Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/locking-functions.html#function_get-lock">GET_LOCK(str,timeout)</a>
     */
    public static Expression getLock(final Expression str, final Expression timeout) {
        return FunctionUtils.twoArgFunc("GET_LOCK", str, timeout, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/locking-functions.html#function_is-free-lock">IS_FREE_LOCK(str)</a>
     */
    public static IPredicate isFreeLock(final Expression str) {
        return FunctionUtils.oneArgFuncPredicate("IS_FREE_LOCK", str);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/locking-functions.html#function_is-used-lock">IS_USED_LOCK(str)</a>
     */
    public static IPredicate isUsedLock(final Expression str) {
        return FunctionUtils.oneArgFuncPredicate("IS_USED_LOCK", str);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/locking-functions.html#function_release-all-locks">RELEASE_ALL_LOCKS()</a>
     */
    public static Expression releaseAllLocks() {
        return FunctionUtils.noArgFunc("RELEASE_ALL_LOCKS()", IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/locking-functions.html#function_release-lock">RELEASE_LOCK(str)</a>
     */
    public static Expression releaseLock(final Expression str) {
        return FunctionUtils.oneArgFunc("RELEASE_LOCK", str, IntegerType.INSTANCE);
    }



    /*-------------------below Information Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param count non-null
     * @param expr  non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_benchmark">BENCHMARK(count,expr)</a>
     */
    public static Expression benchmark(final Expression count, final Expression expr) {
        return FunctionUtils.twoArgFunc("BENCHMARK", count, expr, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_charset">CHARSET(str)</a>
     */
    public static Expression charset(final Expression str) {
        return FunctionUtils.oneArgFunc("CHARSET", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_coercibility">COERCIBILITY(str)</a>
     */
    public static Expression coercibility(final Expression str) {
        return FunctionUtils.oneArgFunc("COERCIBILITY", str, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_collation">COLLATION(str)</a>
     */
    public static Expression collation(final Expression str) {
        return FunctionUtils.oneArgFunc("COLLATION", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_connection-id">CONNECTION_ID()</a>
     */
    public static Expression connectionId() {
        return FunctionUtils.noArgFunc("CONNECTION_ID", LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_current-role">CURRENT_ROLE()</a>
     */
    public static Expression currentRole() {
        return FunctionUtils.noArgFunc("CURRENT_ROLE", StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_current-user">CURRENT_USER()</a>
     */
    public static Expression currentUser() {
        return FunctionUtils.noArgFunc("CURRENT_USER", StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_database">DATABASE()</a>
     */
    public static Expression database() {
        return FunctionUtils.noArgFunc("DATABASE", StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_icu-version">ICU_VERSION()</a>
     */
    public static Expression icuVersion() {
        return FunctionUtils.noArgFunc("ICU_VERSION", StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_last-insert-id">LAST_INSERT_ID()</a>
     */
    public static Expression lastInsertId() {
        return FunctionUtils.noArgFunc("LAST_INSERT_ID", LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @param expr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_last-insert-id">LAST_INSERT_ID()</a>
     */
    public static Expression lastInsertId(final Expression expr) {
        return FunctionUtils.oneArgFunc("LAST_INSERT_ID", expr, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_roles-graphml">ROLES_GRAPHML()</a>
     */
    public static Expression rolesGraphml() {
        return FunctionUtils.noArgFunc("ROLES_GRAPHML", StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_row-count">ROW_COUNT()</a>
     */
    public static Expression rowCount() {
        return FunctionUtils.noArgFunc("ROW_COUNT", LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_user">USER()</a>
     */
    public static Expression user() {
        return FunctionUtils.noArgFunc("USER", StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_version">VERSION()</a>
     */
    public static Expression version() {
        return FunctionUtils.noArgFunc("VERSION", StringType.INSTANCE);
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
