package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.Expression;
import io.army.criteria.TypeInfer;
import io.army.mapping.MappingType;
import io.army.meta.ParamMeta;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

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


    interface _NullTreatmentClause<NR> {

        NR respectNulls();

        NR ignoreNulls();

    }

    public interface _FromFirstLastClause<FR> {
        FR fromFirst();

        FR fromLast();

    }

    public interface _FuncTypeUpdateClause extends TypeInfer.TypeUpdateSpec {

        @Override
        Expression asType(ParamMeta paramMeta);

    }

    public interface _CaseEndClause {

        _FuncTypeUpdateClause end();

    }

    public interface _CaseElseClause extends _CaseEndClause {
        _CaseEndClause elseExp(Expression expression);

        _CaseEndClause elseExp(Supplier<? extends Expression> supplier);

        _CaseEndClause elseExp(Function<Object, ? extends Expression> operator, Supplier<?> supplier);

        _CaseEndClause elseExp(Function<Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

        _CaseEndClause elseExp(BiFunction<Object, Object, ? extends Expression> operator, Supplier<?> firstOperand, Supplier<?> secondOperand);

        _CaseEndClause elseExp(BiFunction<Object, Object, ? extends Expression> operator, Function<String, ?> function, String firstKey, String secondKey);

        _CaseEndClause ifElse(Supplier<? extends Expression> supplier);

        _CaseEndClause ifElse(Function<Object, ? extends Expression> operator, Supplier<?> supplier);

        _CaseEndClause ifElse(Function<Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

        _CaseEndClause ifElse(BiFunction<Object, Object, ? extends Expression> operator, Supplier<?> firstOperand, Supplier<?> secondOperand);

        _CaseEndClause ifElse(BiFunction<Object, Object, ? extends Expression> operator, Function<String, ?> function, String firstKey, String secondKey);


    }

    public interface _CaseThenClause {

        _CaseWhenSpec then(Expression expression);

        _CaseWhenSpec then(Supplier<? extends Expression> supplier);

        _CaseWhenSpec then(Function<Object, ? extends Expression> operator, Supplier<?> supplier);

        _CaseWhenSpec then(Function<Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

        _CaseWhenSpec then(BiFunction<Object, Object, ? extends Expression> operator, Supplier<?> firstOperand, Supplier<?> secondOperand);

        _CaseWhenSpec then(BiFunction<Object, Object, ? extends Expression> operator, Function<String, ?> function, String firstKey, String secondKey);


    }


    public interface _CaseWhenClause {

        _CaseThenClause when(Expression expression);

        _CaseThenClause when(Supplier<? extends Expression> supplier);

        _CaseThenClause when(Function<Object, ? extends Expression> operator, Supplier<?> supplier);

        _CaseThenClause when(Function<Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

        _CaseThenClause when(BiFunction<Object, Object, ? extends Expression> operator, Supplier<?> firstOperand, Supplier<?> secondOperand);

        _CaseThenClause when(BiFunction<Object, Object, ? extends Expression> operator, Function<String, ?> function, String firstKey, String secondKey);

        _CaseThenClause ifWhen(Supplier<? extends Expression> supplier);

        _CaseThenClause ifWhen(Function<Object, ? extends Expression> operator, Supplier<?> supplier);

        _CaseThenClause ifWhen(Function<Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

        _CaseThenClause ifWhen(BiFunction<Object, Object, ? extends Expression> operator, Supplier<?> firstOperand, Supplier<?> secondOperand);

        _CaseThenClause ifWhen(BiFunction<Object, Object, ? extends Expression> operator, Function<String, ?> function, String firstKey, String secondKey);


    }

    public interface _CaseWhenSpec extends _CaseWhenClause, _CaseElseClause {

    }





    /*################################## blow number function method ##################################*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/mathematical-functions.html#function_abs">
     * MySQL ABS function</a>
     */
    public static Expression abs(Object expr) {
        final ArmyExpression expression;
        expression = SQLFunctions.funcParam(expr);
        return SQLFunctions.oneArgOptionFunc("ABS", null, expression, null, expression.paramMeta());
    }

    public static Expression acos(Expression x) {
        throw new UnsupportedOperationException();
    }


    public static Expression asin(Expression x) {
        throw new UnsupportedOperationException();
    }

    public static Expression atan(Expression x) {
        throw new UnsupportedOperationException();
    }

    public static Expression atan(Expression one, Expression two) {
        throw new UnsupportedOperationException();
    }

    public static Expression cell(Expression x) {
        throw new UnsupportedOperationException();
    }

    public static Expression cellAsLong(Expression x) {
        throw new UnsupportedOperationException();
    }

    public static Expression conv(Expression number, int fromBase, int toBase) {
        throw new UnsupportedOperationException();
    }

    public static Expression cos(Expression x) {
        throw new UnsupportedOperationException();
    }

    public static Expression cot(Expression x) {
        throw new UnsupportedOperationException();
    }

    public static Expression crc32(Expression expression) {
        throw new UnsupportedOperationException();
    }

    public static Expression degrees(Expression radian) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see #ln(Expression)
     */
    public static Expression exp(Expression index) {
        throw new UnsupportedOperationException();
    }

    public static Expression floor(Expression number) {
        throw new UnsupportedOperationException();
    }

    public static Expression floorAsLong(Expression number) {
        throw new UnsupportedOperationException();
    }

    public static Expression format(Expression number, Expression decimal) {
        throw new UnsupportedOperationException();
    }

    public static Expression format(Expression number, int decimal) {
        throw new UnsupportedOperationException();
    }

    public static Expression hex(Expression number) {
        throw new UnsupportedOperationException();
    }

    public static Expression hex(Number number) {
        throw new UnsupportedOperationException();
    }

    public static Expression hex(String numberText) {
        throw new UnsupportedOperationException();
    }

    public static Expression hexForText(Expression numberText) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see #exp(Expression)
     */
    public static Expression ln(Expression power) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see #ln(Expression)
     */
    public static Expression log(Expression power) {
        throw new UnsupportedOperationException();
    }

    public static Expression log(Expression bottomNumber
            , Expression power) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see #ln(Expression)
     */
    public static Expression log2(Expression power) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see #ln(Expression)
     */
    public static Expression log10(Expression power) {
        throw new UnsupportedOperationException();
    }

    public static Expression mod(Expression dividend
            , Expression divisor) {
        throw new UnsupportedOperationException();
    }


    public static Expression pi() {
        throw new UnsupportedOperationException();
    }


    /*################################## blow date time function method ##################################*/

    public static Expression now() {
        throw new UnsupportedOperationException();
    }

    public static Expression currentDate() {
        throw new UnsupportedOperationException();
    }

    public static _CaseWhenClause caseFunc() {
        return SQLFunctions.caseFunc(null);
    }


    /**
     * package class
     */
    static abstract class CaseValueFunctions extends Functions {

        /**
         * package constructor
         */
        CaseValueFunctions() {
        }

        /**
         * @param expression non-null {@link Expression} ,if null then use CASE WHEN condition THEN result syntax
         *                   ,else use CASE value WHEN compare_value THEN result syntax.
         * @see #caseFunc(Supplier)
         * @see #caseFunc(Function, Supplier)
         * @see #caseFunc(Function, Function, String)
         * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/flow-control-functions.html#operator_case">case function</a>
         */
        public static _CaseWhenClause caseFunc(final Expression expression) {
            Objects.requireNonNull(expression);
            return SQLFunctions.caseFunc(expression);
        }

        /**
         * @param supplier supplier of nullable {@link Expression},if null then use CASE WHEN condition THEN result syntax
         *                 ,else use CASE value WHEN compare_value THEN result syntax.
         * @see #caseFunc(Expression)
         * @see #caseFunc(Function, Supplier)
         * @see #caseFunc(Function, Function, String)
         * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/flow-control-functions.html#operator_case">case function</a>
         */
        public static _CaseWhenClause caseFunc(Supplier<? extends Expression> supplier) {
            final Expression caseValue;
            if ((caseValue = supplier.get()) == null) {
                throw CriteriaContextStack.nullPointer(CriteriaContextStack.peek());
            }
            return SQLFunctions.caseFunc(caseValue);
        }

        /**
         * @param supplier supplier of non-null parameter,if null then use CASE WHEN condition THEN result syntax
         *                 ,else use CASE value WHEN compare_value THEN result syntax.
         * @see #caseFunc(Expression)
         * @see #caseFunc(Supplier)
         * @see #caseFunc(Function, Function, String)
         * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/flow-control-functions.html#operator_case">case function</a>
         */
        public static _CaseWhenClause caseFunc(Function<Object, ? extends Expression> operator, Supplier<?> supplier) {
            final Object value;
            if ((value = supplier.get()) == null) {
                throw CriteriaContextStack.nullPointer(CriteriaContextStack.peek());
            }
            final Expression caseValue;
            if ((caseValue = operator.apply(value)) == null) {
                throw CriteriaContextStack.nullPointer(CriteriaContextStack.peek());
            }
            return SQLFunctions.caseFunc(caseValue);
        }

        /**
         * <p>
         * <pre>
         *          <code><br/>
         *              public void caseFunc(Map&lt;String,Object> criteria){
         *                   final Select stmt;
         *                   stmt = MySQLs.query(criteria)
         *                            .select(this::simpleCaseFunc)
         *                            .from(Numbers_.T, "u")
         *                            .asQuery();
         *                   printStmt(stmt);
         *              }
         *
         *               private void simpleCaseFunc(Map&lt;String,Object> criteria,Consumer<SelectItem> consumer) {
         *                   Selection selection;
         *                   selection = MySQLs.caseFunc(Numbers_.number::plusLiteral,criteria,"number")
         *                           .when(SQLs.literal(88))
         *                           .then(SQLs.literal(1))
         *
         *                           .when(SQLs.literal(66))
         *                           .then(SQLs.literal(2))
         *
         *                           .when(SQLs.literal(99))
         *                           .then(SQLs.literal(3))
         *
         *                           .elseExp(SQLs.literal(0))
         *
         *                           .end()
         *                           //.asType(StringType.INSTANCE)
         *                           .as("result");
         *
         *                           consumer.accept(selection);
         *                }
         *
         *          </code>
         *     </pre>
         * </p>
         *
         * @param function {@link Function#apply(Object keyName)} return non-null parameter
         *                 ,use CASE value WHEN compare_value THEN result syntax.
         * @param keyName  pass to {@link Function#apply(Object)} of function
         * @throws NullPointerException throw when function return null or operator return null.
         * @see #caseFunc(Expression)
         * @see #caseFunc(Supplier)
         * @see #caseFunc(Function, Supplier)
         * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/flow-control-functions.html#operator_case">case function</a>
         */
        public static _CaseWhenClause caseFunc(Function<Object, ? extends Expression> operator
                , Function<String, ?> function, String keyName) {
            final Object value;
            value = function.apply(keyName);
            if (value == null) {
                throw CriteriaContextStack.nullPointer(CriteriaContextStack.peek());
            }
            final Expression caseValue;
            caseValue = operator.apply(value);
            if (caseValue == null) {
                throw CriteriaContextStack.nullPointer(CriteriaContextStack.peek());
            }
            return SQLFunctions.caseFunc(caseValue);
        }

        /**
         * @param supplier supplier of nullable {@link Expression},if null then use CASE WHEN condition THEN result syntax
         *                 ,else use CASE value WHEN compare_value THEN result syntax.
         * @see #caseFunc(Expression)
         * @see #caseFunc(Function, Supplier)
         * @see #caseFunc(Function, Function, String)
         * @see #caseIf(Function, Supplier)
         * @see #caseIf(Function, Function, String)
         * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/flow-control-functions.html#operator_case">case function</a>
         */
        public static _CaseWhenClause caseIf(Supplier<? extends Expression> supplier) {
            return SQLFunctions.caseFunc(supplier.get());
        }

        /**
         * <p>
         * <pre>
         *          <code><br/>
         *              public void caseFunc(Criteria criteria){
         *                   final Select stmt;
         *                   stmt = MySQLs.query(criteria)
         *                            .select(this::simpleCaseFunc)
         *                            .from(Numbers_.T, "u")
         *                            .asQuery();
         *                   printStmt(stmt);
         *              }
         *
         *               private void simpleCaseFunc(Criteria criteria,Consumer<SelectItem> consumer) {
         *                   Selection selection;
         *                   selection = MySQLs.caseFunc(Numbers_.number::plusLiteral,criteria::getNumber)
         *                           .when(SQLs.literal(88))
         *                           .then(SQLs.literal(1))
         *
         *                           .when(SQLs.literal(66))
         *                           .then(SQLs.literal(2))
         *
         *                           .when(SQLs.literal(99))
         *                           .then(SQLs.literal(3))
         *
         *                           .elseExp(SQLs.literal(0))
         *
         *                           .end()
         *                           //.asType(StringType.INSTANCE)
         *                           .as("result");
         *
         *                           consumer.accept(selection);
         *                }
         *
         *          </code>
         *     </pre>
         * </p>
         *
         * @param supplier supplier of nullable parameter,if null then use CASE WHEN condition THEN result syntax
         *                 ,else use CASE value WHEN compare_value THEN result syntax.
         * @throws NullPointerException throw when operator return null.
         * @throws CriteriaException    throw when invoking this method in non-statement context.
         * @see #caseFunc(Expression)
         * @see #caseFunc(Supplier)
         * @see #caseFunc(Function, Function, String)
         * @see #caseIf(Supplier)
         * @see #caseIf(Function, Function, String)
         * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/flow-control-functions.html#operator_case">case function</a>
         */
        public static _CaseWhenClause caseIf(Function<Object, ? extends Expression> operator, Supplier<?> supplier) {
            final Object value;
            final Expression caseValue;
            if ((value = supplier.get()) == null) {
                caseValue = null;
            } else if ((caseValue = operator.apply(value)) == null) {
                throw CriteriaContextStack.nullPointer(CriteriaContextStack.peek());
            }
            return SQLFunctions.caseFunc(caseValue);
        }

        /**
         * <p>
         * <pre>
         *          <code><br/>
         *              public void caseFunc(Map&lt;String,Object> criteria){
         *                   final Select stmt;
         *                   stmt = MySQLs.query(criteria)
         *                            .select(this::simpleCaseFunc)
         *                            .from(Numbers_.T, "u")
         *                            .asQuery();
         *                   printStmt(stmt);
         *              }
         *
         *               private void simpleCaseFunc(Map&lt;String,Object> criteria,Consumer<SelectItem> consumer) {
         *                   Selection selection;
         *                   selection = MySQLs.caseFunc(Numbers_.number::plusLiteral,criteria,"number")
         *                           .when(SQLs.literal(88))
         *                           .then(SQLs.literal(1))
         *
         *                           .when(SQLs.literal(66))
         *                           .then(SQLs.literal(2))
         *
         *                           .when(SQLs.literal(99))
         *                           .then(SQLs.literal(3))
         *
         *                           .elseExp(SQLs.literal(0))
         *
         *                           .end()
         *                           //.asType(StringType.INSTANCE)
         *                           .as("result");
         *
         *                           consumer.accept(selection);
         *                }
         *
         *          </code>
         *     </pre>
         * </p>
         *
         * @param function {@link Function#apply(Object keyName)} return nullable parameter,if null then use CASE WHEN condition THEN result syntax
         *                 ,else use CASE value WHEN compare_value THEN result syntax.
         * @param keyName  pass to {@link Function#apply(Object)} of function
         * @throws NullPointerException throw when operator return null.
         * @throws CriteriaException    throw when invoking this method in non-statement context.
         * @see #caseFunc(Expression)
         * @see #caseFunc(Supplier)
         * @see #caseFunc(Function, Supplier)
         * @see #caseFunc(Function, Function, String)
         * @see #caseIf(Supplier)
         * @see #caseIf(Function, Supplier)
         * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/flow-control-functions.html#operator_case">case function</a>
         */
        public static _CaseWhenClause caseIf(Function<Object, ? extends Expression> operator
                , Function<String, ?> function, String keyName) {
            final Object value;
            final Expression caseValue;
            if ((value = function.apply(keyName)) == null) {
                caseValue = null;
            } else if ((caseValue = operator.apply(value)) == null) {
                throw CriteriaContextStack.nullPointer(CriteriaContextStack.peek());
            }
            return SQLFunctions.caseFunc(caseValue);
        }


    }

    /*################################## blow static inner class  ##################################*/


    /*-------------------below package method -------------------*/

    static ParamMeta _returnType(final ArmyExpression keyExpr, final ArmyExpression valueExpr
            , BiFunction<MappingType, MappingType, MappingType> function) {
        final ParamMeta keyType, valueType;
        keyType = keyExpr.paramMeta();
        valueType = valueExpr.paramMeta();
        final ParamMeta returnType;
        if (keyType instanceof ParamMeta.Delay || valueType instanceof ParamMeta.Delay) {
            returnType = CriteriaSupports.delayParamMeta(keyType, valueType, function);
        } else {
            returnType = function.apply(keyType.mappingType(), valueType.mappingType());
        }
        return returnType;
    }


}
