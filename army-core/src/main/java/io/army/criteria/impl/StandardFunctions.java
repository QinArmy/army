package io.army.criteria.impl;


import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.SimpleExpression;
import io.army.criteria.dialect.Window;
import io.army.criteria.standard.SQLFunction;
import io.army.mapping.DoubleType;
import io.army.mapping.LongType;
import io.army.mapping.MappingType;
import io.army.meta.TypeMeta;

import java.util.List;


/**
 * <p>
 * Hold standard sql function.
 * </p>
 *
 * @since 1.0
 */
abstract class StandardFunctions extends Functions {

    StandardFunctions() {
        throw new UnsupportedOperationException();
    }


    public interface _OverSpec extends Window._OverWindowClause<Window._StandardPartitionBySpec> {


    }

    public interface _AggregateWindowFunc extends _OverSpec, SQLFunction.AggregateFunction, SimpleExpression {

    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_cume-dist">CUME_DIST() over_clause</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">cume_dist () → double precision<br/>
     * Returns the cumulative distribution, that is (number of partition rows preceding or peers with current row) / (total partition rows). The value thus ranges from 1/N to 1.
     * </a>
     */
    public static _OverSpec cumeDist() {
        return WindowFunctionUtils.zeroArgWindowFunc("cume_dist", DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_row-number">ROW_NUMBER() over_clause</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">row_number () → bigint<br/>
     * Returns the number of the current row within its partition, counting from 1.
     * </a>
     */
    public static _OverSpec rowNumber() {
        return WindowFunctionUtils.zeroArgWindowFunc("row_number", LongType.INSTANCE);
    }


    public static SimpleExpression customFunc(String name, TypeMeta returnType) {
        if (!FUN_NAME_PATTER.matcher(name).matches()) {
            throw _customFuncNameError(name);
        }
        return FunctionUtils.zeroArgFunc(name, returnType);
    }

    public static IPredicate customFunc(String name) {
        if (!FUN_NAME_PATTER.matcher(name).matches()) {
            throw _customFuncNameError(name);
        }
        return FunctionUtils.zeroArgFuncPredicate(name);
    }

    public static SimpleExpression customFunc(String name, Expression expr, TypeMeta returnType) {
        if (!FUN_NAME_PATTER.matcher(name).matches()) {
            throw _customFuncNameError(name);
        }
        return FunctionUtils.oneArgFunc(name, expr, returnType);
    }

    public static IPredicate customFunc(String name, Expression expr) {
        if (!FUN_NAME_PATTER.matcher(name).matches()) {
            throw _customFuncNameError(name);
        }
        return FunctionUtils.oneArgPredicateFunc(name, expr);
    }

    public static SimpleExpression customFunc(String name, Expression expr1, Expression expr2, TypeMeta returnType) {
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

    public static SimpleExpression customFunc(String name, List<Expression> expList, TypeMeta returnType) {
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


}
