package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.mysql.MySQLCastType;
import io.army.criteria.mysql.MySQLCharset;
import io.army.lang.Nullable;
import io.army.mapping.*;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * Package class ,This class is MySQL function utils for application developer.
 * </p>
 *
 * @since 1.0
 */
@SuppressWarnings("unused")
abstract class MySQLFunctions extends MySQLMiscellaneousFunctions {

    /**
     * private constructor
     */
    MySQLFunctions() {
    }



    /*-------------------below Cast Functions and Operators -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:
     *      <ul>
     *          <li>If type is {@link MySQLCastType#BINARY }then {@link BinaryType}</li>
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
     *          <li>Else if type is {@link MySQLCastType#JSON }then {@link StringType}</li>
     *          <li>Else if type is {@link MySQLCastType#Point }then {@link BinaryType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiPoint }then {@link BinaryType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiLineString }then {@link BinaryType}</li>
     *          <li>Else if type is {@link MySQLCastType#LineString }then {@link BinaryType}</li>
     *          <li>Else if type is {@link MySQLCastType#Polygon }then {@link BinaryType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiPolygon }then {@link BinaryType}</li>
     *          <li>Else if type is {@link MySQLCastType#GeometryCollection }then {@link BinaryType}</li>
     *      </ul>
     * </p>
     *
     * @param exp  non-null {@link Expression}
     * @param as   {@link SQLs#AS}
     * @param type non-null {@link  MySQLCastType}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #cast(Expression, SQLs.WordAs, MySQLCastType, Expression)
     * @see #cast(Expression, SQLs.WordAs, MySQLCastType, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/cast-functions.html#function_cast">CAST(expr AS type [ARRAY])</a>
     */
    public static SimpleExpression cast(final Expression exp, final SQLs.WordAs as, final MySQLCastType type) {
        assert as == SQLs.AS;
        final List<Object> argList = new ArrayList<>(3);
        argList.add(exp);
        argList.add(as);
        argList.add(type);
        return FunctionUtils.complexArgFunc("CAST", argList, _castReturnType(type));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     *      <ul>
     *          <li>If type is {@link MySQLCastType#BINARY }then {@link BinaryType}</li>
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
     *          <li>Else if type is {@link MySQLCastType#JSON }then {@link StringType}</li>
     *          <li>Else if type is {@link MySQLCastType#Point }then {@link BinaryType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiPoint }then {@link BinaryType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiLineString }then {@link BinaryType}</li>
     *          <li>Else if type is {@link MySQLCastType#LineString }then {@link BinaryType}</li>
     *          <li>Else if type is {@link MySQLCastType#Polygon }then {@link BinaryType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiPolygon }then {@link BinaryType}</li>
     *          <li>Else if type is {@link MySQLCastType#GeometryCollection }then {@link BinaryType}</li>
     *      </ul>
     * </p>
     *
     * @param exp  non-null {@link Expression}
     * @param as   {@link SQLs#AS}
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
     * @see #cast(Expression, SQLs.WordAs, MySQLCastType)
     * @see #cast(Expression, SQLs.WordAs, MySQLCastType, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_cast">CAST(expr AS type)</a>
     */
    public static SimpleExpression cast(final Expression exp, final SQLs.WordAs as, final MySQLCastType type
            , final Expression n) {
        assert as == SQLs.AS;
        final String funcName = "CAST";
        if (!(n instanceof ArmyLiteralExpression)) {
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

        final List<Object> argList = new ArrayList<>(6);

        argList.add(exp);
        argList.add(as);
        argList.add(type);
        argList.add(Functions.FuncWord.LEFT_PAREN);

        argList.add(n);
        argList.add(Functions.FuncWord.RIGHT_PAREN);
        return FunctionUtils.complexArgFunc(funcName, argList, _castReturnType(type));
    }

    public static SimpleExpression cast(final Expression exp, final SQLs.WordAs as, final MySQLCastType charType
            , final Expression n, MySQLs.WordsCharacterSet characterSet, SQLElement charset) {
        Objects.requireNonNull(n);
        return _castToChar(exp, as, charType, n, characterSet, charset);
    }

    public static SimpleExpression cast(final Expression exp, final SQLs.WordAs as, final MySQLCastType charType
            , final int n, MySQLs.WordsCharacterSet characterSet, SQLElement charset) {
        return _castToChar(exp, as, charType, SQLs.literal(IntegerType.INSTANCE, n), characterSet, charset);
    }

    public static SimpleExpression cast(final Expression exp, final SQLs.WordAs as, final MySQLCastType charType
            , MySQLs.WordsCharacterSet characterSet, SQLElement charset) {
        return _castToChar(exp, as, charType, null, characterSet, charset);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BigDecimalType}
     * </p>
     *
     * @param exp  non-null  {@link Expression}
     * @param as   {@link SQLs#AS}
     * @param type currently,support only {@link MySQLCastType#DECIMAL}
     * @param m    non-null literal {@link Expression},couldn't be parameter {@link  Expression}
     * @param d    non-null literal {@link Expression},couldn't be parameter {@link  Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #cast(Expression, SQLs.WordAs, MySQLCastType)
     * @see #cast(Expression, SQLs.WordAs, MySQLCastType, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_cast">CAST(expr AS DECIMAL(M,D))</a>
     */
    public static SimpleExpression cast(final Expression exp, final SQLs.WordAs as, final MySQLCastType type
            , final Expression m, final Expression d) {
        assert as == SQLs.AS;
        final String funcName = "CAST";

        if (type != MySQLCastType.DECIMAL) {
            throw CriteriaUtils.funcArgError(funcName, type);
        }
        if (!(m instanceof ArmyLiteralExpression)) {
            throw CriteriaUtils.funcArgError(funcName, m);
        }
        if (!(d instanceof ArmyLiteralExpression)) {
            throw CriteriaUtils.funcArgError(funcName, d);
        }

        final List<Object> argList = new ArrayList<>(8);

        argList.add(exp);
        argList.add(as);
        argList.add(type);
        argList.add(Functions.FuncWord.LEFT_PAREN);

        argList.add(m);
        argList.add(Functions.FuncWord.COMMA);
        argList.add(d);
        argList.add(Functions.FuncWord.RIGHT_PAREN);
        return FunctionUtils.complexArgFunc(funcName, argList, BigDecimalType.INSTANCE);
    }

    public static SimpleExpression cast(final Expression exp, final SQLs.WordAs as, final MySQLCastType type
            , final int m, final int d) {
        return cast(exp, as, type, SQLs.literal(IntegerType.INSTANCE, m), SQLs.literal(IntegerType.INSTANCE, d));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LocalDateTimeType}
     * </p>
     *
     * @param timestampValue    non-null {@link Expression}
     * @param atTimeZone        {@link MySQLs#AT_TIME_ZONE}
     * @param timezoneSpecifier non-null
     * @param as                {@link SQLs#AS}
     * @param dateTime          must be {@link MySQLCastType#DATETIME}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_cast">CAST(timestamp_value AT TIME ZONE timezone_specifier AS DATETIME[(precision)])</a>
     */
    public static SimpleExpression cast(final Expression timestampValue, MySQLs.WordsAtTimeZone atTimeZone
            , final Expression timezoneSpecifier, SQLs.WordAs as, MySQLCastType dateTime) {
        return _castDateTime(timestampValue, atTimeZone, timezoneSpecifier, as, dateTime, null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LocalDateTimeType}
     * </p>
     *
     * @param timestampValue    non-null {@link Expression}
     * @param atTimeZone        {@link MySQLs#AT_TIME_ZONE}
     * @param timezoneSpecifier non-null
     * @param as                {@link SQLs#AS}
     * @param dateTime          must be {@link MySQLCastType#DATETIME}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_cast">CAST(timestamp_value AT TIME ZONE timezone_specifier AS DATETIME[(precision)])</a>
     */
    public static SimpleExpression cast(Expression timestampValue, MySQLs.WordsAtTimeZone atTimeZone
            , final Expression timezoneSpecifier, SQLs.WordAs as, MySQLCastType dateTime, Expression precision) {
        Objects.requireNonNull(precision);
        return _castDateTime(timestampValue, atTimeZone, timezoneSpecifier, as, dateTime, precision);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LocalDateTimeType}
     * </p>
     *
     * @param timestampValue    non-null {@link Expression}
     * @param atTimeZone        {@link MySQLs#AT_TIME_ZONE}
     * @param timezoneSpecifier non-null
     * @param as                {@link SQLs#AS}
     * @param dateTime          must be {@link MySQLCastType#DATETIME}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_cast">CAST(timestamp_value AT TIME ZONE timezone_specifier AS DATETIME[(precision)])</a>
     */
    public static SimpleExpression cast(final Expression timestampValue, MySQLs.WordsAtTimeZone atTimeZone
            , final Expression timezoneSpecifier, SQLs.WordAs as, MySQLCastType dateTime, int precision) {
        return _castDateTime(timestampValue, atTimeZone, timezoneSpecifier, as, dateTime
                , SQLs.literal(IntegerType.INSTANCE, precision));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     *      <ul>
     *          <li>If type is {@link MySQLCastType#BINARY }then {@link BinaryType}</li>
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
     *          <li>Else if type is {@link MySQLCastType#JSON }then {@link StringType}</li>
     *          <li>Else if type is {@link MySQLCastType#Point }then {@link BinaryType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiPoint }then {@link BinaryType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiLineString }then {@link BinaryType}</li>
     *          <li>Else if type is {@link MySQLCastType#LineString }then {@link BinaryType}</li>
     *          <li>Else if type is {@link MySQLCastType#Polygon }then {@link BinaryType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiPolygon }then {@link BinaryType}</li>
     *          <li>Else if type is {@link MySQLCastType#GeometryCollection }then {@link BinaryType}</li>
     *      </ul>
     * </p>
     *
     * @param exp             non-null   {@link Expression}
     * @param using           {@link MySQLs#USING}
     * @param transcodingName non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_convert">CONVERT(expr USING transcoding_name)</a>
     */
    public static SimpleExpression convert(final Expression exp, MySQLs.WordUsing using, final SQLElement transcodingName) {
        assert using == MySQLs.USING;
        final String name = "CONVERT";
        if (!(transcodingName instanceof MySQLCharset || transcodingName instanceof SQLIdentifier)) {
            throw CriteriaUtils.funcArgError(name, transcodingName);
        }
        final List<Object> argList = new ArrayList<>(3);
        argList.add(exp);
        argList.add(using);
        argList.add(transcodingName);
        return FunctionUtils.complexArgFunc(name, argList, StringType.INSTANCE);
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
    public static SimpleExpression bitCount(final Expression n) {
        return FunctionUtils.oneArgFunc("BIT_COUNT", n, IntegerType.INSTANCE);
    }



    /*-------------------below Flow Control Functions-------------------*/



    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr1
     * </p>
     *
     * @throws CriteriaException throw when any arg is multi-value expression
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/flow-control-functions.html#function_if">IF(expr1,expr2,expr3)</a>
     */
    public static SimpleExpression ifFunc(final Expression predicate, final Expression expr1, final Expression expr2) {
        return FunctionUtils.threeArgFunc("IF", predicate, expr1, expr2, expr1.typeMeta());
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr1
     * </p>
     *
     * @throws CriteriaException throw when any arg is multi-value expression
     * @see #ifFunc(Expression, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/flow-control-functions.html#function_ifnull">IFNULL(expr1,expr2)</a>
     */
    public static SimpleExpression ifNull(final Expression expr1, final Expression expr2) {
        return FunctionUtils.twoArgFunc("IFNULL", expr1, expr2, expr1.typeMeta());
    }



    /*-------------------below private method-------------------*/

//    /**
//     * @see #ifFunc(Expression, Expression, Expression)
//     */
//    private static MappingType ifFuncReturnType(final MappingType expr2Type, final MappingType expr3Type) {
//        final MappingType returnType;
//        if (expr2Type.getClass() == expr3Type.getClass()) {
//            returnType = expr2Type;
//        } else if (expr2Type instanceof _SQLStringType || expr3Type instanceof _SQLStringType) {
//            returnType = StringType.INSTANCE;
//        } else if (!(expr2Type instanceof _NumericType || expr3Type instanceof _NumericType)) {
//            returnType = StringType.INSTANCE;
//        } else if (expr2Type instanceof _NumericType._FloatNumericType
//                || expr3Type instanceof _NumericType._FloatNumericType) {
//            returnType = DoubleType.INSTANCE;
//        } else if (expr2Type instanceof _NumericType._UnsignedNumeric
//                || expr3Type instanceof _NumericType._UnsignedNumeric) {
//            if (expr2Type instanceof UnsignedBigDecimalType || expr3Type instanceof UnsignedBigDecimalType) {
//                returnType = UnsignedBigDecimalType.INSTANCE;
//            } else if (expr2Type instanceof UnsignedBigIntegerType || expr3Type instanceof UnsignedBigIntegerType) {
//                returnType = UnsignedBigIntegerType.INSTANCE;
//            } else if (expr2Type instanceof UnsignedLongType || expr3Type instanceof UnsignedLongType) {
//                returnType = UnsignedLongType.INSTANCE;
//            } else {
//                returnType = UnsignedIntegerType.INSTANCE;
//            }
//        } else if (expr2Type instanceof BigDecimalType || expr3Type instanceof BigDecimalType) {
//            returnType = BigDecimalType.INSTANCE;
//        } else if (expr2Type instanceof BigIntegerType || expr3Type instanceof BigIntegerType) {
//            returnType = BigIntegerType.INSTANCE;
//        } else if (expr2Type instanceof LongType || expr3Type instanceof LongType) {
//            returnType = LongType.INSTANCE;
//        } else {
//            returnType = IntegerType.INSTANCE;
//        }
//        return returnType;
//    }


//    /**
//     * @see #ifNull(Expression, Expression)
//     */
//    private static MappingType ifNullReturnType(final MappingType expr1Type, final MappingType expr2Type) {
//        final MappingType returnType;
//        if (expr1Type.getClass() == expr2Type.getClass()) {
//            returnType = expr1Type;
//        } else if (!(expr1Type instanceof _NumericType && expr2Type instanceof _NumericType)) {
//            returnType = StringType.INSTANCE;
//        } else if (expr1Type instanceof _NumericType._DecimalNumeric && expr2Type instanceof _NumericType._DecimalNumeric) {
//            if (expr1Type instanceof _NumericType._UnsignedNumeric
//                    && expr2Type instanceof _NumericType._UnsignedNumeric) {
//                returnType = UnsignedBigDecimalType.INSTANCE;
//            } else {
//                returnType = BigDecimalType.INSTANCE;
//            }
//        } else if (!(expr1Type instanceof _NumericType._IntegerNumeric
//                || expr2Type instanceof _NumericType._IntegerNumeric)) {
//            returnType = DoubleType.INSTANCE;
//        } else if (expr1Type instanceof _NumericType._UnsignedNumeric
//                && expr2Type instanceof _NumericType._UnsignedNumeric) {
//            if (expr1Type instanceof UnsignedBigIntegerType || expr2Type instanceof UnsignedBigIntegerType) {
//                returnType = UnsignedBigIntegerType.INSTANCE;
//            } else if (expr1Type instanceof UnsignedLongType || expr2Type instanceof UnsignedLongType) {
//                returnType = UnsignedLongType.INSTANCE;
//            } else {
//                returnType = UnsignedIntegerType.INSTANCE;
//            }
//        } else if (expr1Type instanceof BigIntegerType || expr2Type instanceof BigIntegerType) {
//            returnType = BigIntegerType.INSTANCE;
//        } else if (expr1Type instanceof LongType || expr2Type instanceof LongType) {
//            returnType = LongType.INSTANCE;
//        } else {
//            returnType = IntegerType.INSTANCE;
//        }
//        return returnType;
//    }


    /**
     * @see #cast(Expression, SQLs.WordAs, MySQLCastType)
     * @see #convert(Expression, MySQLs.WordUsing, SQLElement)
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
                returnType = BinaryType.INSTANCE;
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
     * @see #cast(Expression, MySQLs.WordsAtTimeZone, Expression, SQLs.WordAs, MySQLCastType)
     * @see #cast(Expression, MySQLs.WordsAtTimeZone, Expression, SQLs.WordAs, MySQLCastType, int)
     * @see #cast(Expression, MySQLs.WordsAtTimeZone, Expression, SQLs.WordAs, MySQLCastType, Expression)
     */
    private static SimpleExpression _castDateTime(final Expression timestampValue, MySQLs.WordsAtTimeZone atTimeZone
            , final Expression timezoneSpecifier, SQLs.WordAs as, MySQLCastType dateTime
            , @Nullable Expression precision) {
        assert atTimeZone == MySQLs.AT_TIME_ZONE && as == SQLs.AS;

        final String name = "CAST";
        if (dateTime != MySQLCastType.DATETIME) {
            throw CriteriaUtils.funcArgError(name, dateTime);
        }

        final List<Object> argList = new ArrayList<>(6);

        argList.add(timestampValue);
        argList.add(atTimeZone);
        argList.add(timezoneSpecifier);
        argList.add(as);

        argList.add(MySQLCastType.DATETIME);
        if (precision != null) {
            argList.add(precision);
        }
        return FunctionUtils.complexArgFunc(name, argList, LocalDateTimeType.INSTANCE);
    }

    /**
     * @see #cast(Expression, SQLs.WordAs, MySQLCastType, MySQLs.WordsCharacterSet, SQLElement)
     * @see #cast(Expression, SQLs.WordAs, MySQLCastType, Expression, MySQLs.WordsCharacterSet, SQLElement)
     */
    private static SimpleExpression _castToChar(final Expression exp, final SQLs.WordAs as
            , final MySQLCastType charType, final @Nullable Expression n, MySQLs.WordsCharacterSet characterSet
            , SQLElement charset) {
        assert as == SQLs.AS && characterSet == MySQLs.CHARACTER_SET;

        final String name = "CAST";
        if (charType != MySQLCastType.CHAR) {
            throw new IllegalArgumentException(String.format("support only %s", MySQLCastType.CHAR));
        } else if (!(charset instanceof MySQLCharset || charset instanceof SQLs.SQLIdentifierImpl)) {
            throw CriteriaUtils.funcArgError(name, charset);
        }
        final List<Object> argList = new ArrayList<>(n == null ? 5 : 8);

        argList.add(exp);
        argList.add(as);
        argList.add(charType);

        if (n != null) {
            argList.add(Functions.FuncWord.LEFT_PAREN);
            argList.add(n);
            argList.add(Functions.FuncWord.RIGHT_PAREN);
        }
        argList.add(characterSet);
        argList.add(charset);
        return FunctionUtils.complexArgFunc(name, argList, StringType.INSTANCE);
    }


}
