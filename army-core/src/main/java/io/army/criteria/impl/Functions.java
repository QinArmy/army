package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.mapping.*;
import io.army.meta.TypeMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
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
abstract class Functions {

    /**
     * package constructor,forbid application developer directly extend this util class.
     */
    Functions() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see SQLs#DISTINCT
     */
    public interface FuncDistinct {

    }


    interface _NullTreatmentClause<NR> {

        NR respectNulls();

        NR ignoreNulls();

    }

    public interface _FromFirstLastClause<FR> {
        FR fromFirst();

        FR fromLast();

    }


    public interface _FuncCommaClause<CR> {

        CR comma(Expression expression);

        CR comma(Supplier<? extends Expression> supplier);

        CR comma(Function<Object, ? extends Expression> operator, Supplier<?> supplier);

        CR comma(Function<Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

        CR comma(BiFunction<Object, Object, ? extends Expression> operator, Supplier<?> firstOperand, Supplier<?> secondOperand);

        CR comma(BiFunction<Object, Object, ? extends Expression> operator, Function<String, ?> function, String firstKey, String secondKey);

    }

    public interface _FuncLastArgClause extends Functions._FuncCommaClause<Statement._RightParenClause<Expression>> {

    }

    public interface _FuncSecondArgClause extends Functions._FuncCommaClause<_FuncLastArgClause> {

    }

    public interface _FuncConditionClause<LR> {

        LR leftParen(IPredicate condition);

        LR leftParen(Supplier<? extends IPredicate> supplier);

        LR leftParen(Function<Object, ? extends IPredicate> operator, Supplier<?> supplier);

        LR leftParen(Function<Object, ? extends IPredicate> operator, Function<String, ?> function, String keyName);

        LR leftParen(BiFunction<Object, Object, ? extends IPredicate> operator, Supplier<?> firstOperand, Supplier<?> secondOperand);

        LR leftParen(BiFunction<Object, Object, ? extends IPredicate> operator, Function<String, ?> function, String firstKey, String secondKey);

    }

    public interface _FuncConditionTowClause extends _FuncConditionClause<_FuncSecondArgClause> {

    }





    /*################################## blow number function method ##################################*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_abs">ABS(X)</a>
     */
    public static Expression abs(final Expression expr) {
        return FunctionUtils.oneArgFunc("ABS", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_acos">ACOS(X)</a>
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
     * The {@link MappingType} of function return type: {@link  LongType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_ceil">CEIL(X)</a>
     */
    public static Expression cell(final Expression expr) {
        return FunctionUtils.oneArgFunc("CEIL", expr, LongType.INSTANCE);
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
     */
    public static Expression degrees(final Expression expr) {
        return FunctionUtils.oneArgFunc("DEGREES", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_exp">EXP(x)</a>
     */
    public static Expression exp(final Expression expr) {
        return FunctionUtils.oneArgFunc("EXP", expr, DoubleType.INSTANCE);
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
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_ln">LN(x)</a>
     */
    public static Expression ln(final Expression x) {
        return FunctionUtils.oneArgFunc("LN", x, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_log">LOG(x)</a>
     */
    public static Expression log(final Expression x) {
        return FunctionUtils.oneArgFunc("LOG", x, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_log">LOG(x)</a>
     */
    public static Expression log(final Expression b, final Expression x) {
        return FunctionUtils.twoArgFunc("LOG", b, x, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_log2">LOG2(x)</a>
     */
    public static Expression log2(final Expression x) {
        return FunctionUtils.oneArgFunc("LOG2", x, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_log10">LOG10(x)</a>
     */
    public static Expression log10(final Expression x) {
        return FunctionUtils.oneArgFunc("LOG10", x, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} or n.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_log10">LOG10(x)</a>
     */
    public static Expression mod(final Expression n, final Expression m) {
        return FunctionUtils.twoArgFunc("LOG10", n, m, n.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType} .
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_pi">PI()</a>
     */
    public static Expression pi() {
        return FunctionUtils.noArgFunc("PI", DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType} .
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
     * The {@link MappingType} of function return type: {@link DoubleType} .
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_round">ROUND(x)</a>
     */
    public static Expression round(final Expression x) {
        return FunctionUtils.oneArgFunc("ROUND", x, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType} .
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_round">ROUND(x,d)</a>
     */
    public static Expression round(final Expression x, final Expression d) {
        return FunctionUtils.twoArgFunc("ROUND", x, d, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType} .
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_sign">SIGN(x)</a>
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
     */
    public static Expression sin(final Expression x) {
        return FunctionUtils.oneArgFunc("SIN", x, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType} .
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_sqrt">SQRT(x)</a>
     */
    public static Expression sqrt(final Expression x) {
        return FunctionUtils.oneArgFunc("SQRT", x, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType} .
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_tan">TAN(x)</a>
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
            returnType = CriteriaSupports.delayParamMeta(keyType, valueType, function);
        } else {
            returnType = function.apply(keyType.mappingType(), valueType.mappingType());
        }
        return returnType;
    }

    static TypeMeta _returnType(ArmyExpression expression, Function<MappingType, MappingType> function) {
        final TypeMeta exprType, returnType;
        exprType = expression.typeMeta();
        if (exprType instanceof TypeMeta.Delay && !((TypeMeta.Delay) exprType).isPrepared()) {
            returnType = CriteriaSupports.delayParamMeta((TypeMeta.Delay) exprType, function);
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
                argList.add(FunctionUtils.FuncWord.COMMA);
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


    static Expression _simpleTowArgFunc(final String name, final Expression g1
            , final Expression g2, final TypeMeta returnType) {
        if (g1 instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, g1);
        }
        if (g2 instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, g2);
        }
        final List<Object> argList = new ArrayList<>(3);
        argList.add(g1);
        argList.add(FunctionUtils.FuncWord.COMMA);
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
        argList.add(FunctionUtils.FuncWord.COMMA);
        argList.add(e2);
        argList.add(FunctionUtils.FuncWord.COMMA);

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
        argLit.add(FunctionUtils.FuncWord.COMMA);
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
                argList.add(FunctionUtils.FuncWord.COMMA);
                if (o instanceof Expression) {
                    argList.add(o);
                } else {
                    argList.add(SQLs.literal(elementType.mappingType(), o));
                }
            }
        } else {
            argList = new ArrayList<>(3);
            argList.add(expr);
            argList.add(FunctionUtils.FuncWord.COMMA);
            if (exprList instanceof Expression) {
                argList.add(exprList);
            } else {
                argList.add(SQLs.literal(elementType.mappingType(), exprList));
            }
        }
        return FunctionUtils.complexArgFunc(name, argList, returnType);
    }






    /*-------------------below private method -------------------*/


}
