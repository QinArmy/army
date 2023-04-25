package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.standard.SQLFunction;
import io.army.dialect.DialectParser;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.function.OptionalClauseOperator;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.ServerMeta;
import io.army.meta.TypeMeta;
import io.army.sqltype.SqlType;
import io.army.util._Exceptions;

import java.util.function.BiFunction;

/**
 * <p>
 * This class representing non-operation expression
 * </p>
 */
abstract class NonOperationExpression implements ArmyExpression {


    NonOperationExpression() {
    }


    @Override
    public final boolean isNullValue() {
        final boolean nullable;
        if (this instanceof SqlValueParam.SingleAnonymousValue) {
            nullable = ((SqlValueParam.SingleAnonymousValue) this).value() == null;
        } else {
            nullable = false;
        }
        return nullable;
    }

    @Override
    public final IPredicate equal(Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate equalAny(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate equalSome(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate less(Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final OperationPredicate lessAny(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate lessSome(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate lessAll(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate lessEqual(Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final OperationPredicate lessEqualAny(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate lessEqualSome(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate lessEqualAll(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate greater(Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final OperationPredicate greaterAny(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate greaterSome(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate greaterAll(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate greaterEqual(Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final OperationPredicate greaterEqualAny(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate greaterEqualSome(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate greaterEqualAll(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate notEqual(Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final OperationPredicate notEqualAny(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate notEqualSome(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate notEqualAll(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate between(Expression first, SQLs.WordAnd and, Expression second) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate notBetween(Expression first, SQLs.WordAnd and, Expression second) {
        throw unsupportedOperation(this);
    }


    @Override
    public final IPredicate between(@Nullable SQLs.BetweenModifier modifier, Expression first, SQLs.WordAnd and, Expression second) {
        throw unsupportedOperation(this);
    }


    @Override
    public final IPredicate notBetween(@Nullable SQLs.BetweenModifier modifier, Expression first, SQLs.WordAnd and, Expression second) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate is(SQLsSyntax.BooleanTestWord operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate isNot(SQLsSyntax.BooleanTestWord operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final IPredicate is(SQLsSyntax.IsComparisonWord operator, Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final IPredicate isNot(SQLsSyntax.IsComparisonWord operator, Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final OperationPredicate isNull() {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate isNotNull() {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate in(Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate in(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate notIn(Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate notIn(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }


    @Override
    public final OperationPredicate like(Expression pattern) {
        throw unsupportedOperation(this);
    }


    @Override
    public final IPredicate like(Expression pattern, SQLs.WordEscape escape, char escapeChar) {
        throw unsupportedOperation(this);
    }

    @Override
    public final IPredicate like(Expression pattern, SQLs.WordEscape escape, Expression escapeChar) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate notLike(Expression pattern) {
        throw unsupportedOperation(this);
    }


    @Override
    public final IPredicate notLike(Expression pattern, SQLs.WordEscape escape, char escapeChar) {
        throw unsupportedOperation(this);
    }

    @Override
    public final IPredicate notLike(Expression pattern, SQLs.WordEscape escape, Expression escapeChar) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationExpression mod(Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationExpression times(Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationExpression plus(Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationExpression minus(Expression minuend) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationExpression divide(Expression divisor) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationExpression bitwiseAnd(Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final OperationExpression bitwiseOr(Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationExpression bitwiseXor(Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final OperationExpression rightShift(Expression bitNumber) {
        throw unsupportedOperation(this);
    }


    @Override
    public final OperationExpression leftShift(Expression bitNumber) {
        throw unsupportedOperation(this);
    }


    @Override
    public final Expression apply(BiFunction<Expression, Expression, Expression> operator, Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final <M extends SQLWords> Expression apply(OptionalClauseOperator<M, Expression, Expression> operator, Expression right, M modifier, Expression optionalExp) {
        throw unsupportedOperation(this);
    }

    @Override
    public final <M extends SQLWords> Expression apply(OptionalClauseOperator<M, Expression, Expression> operator, Expression right, M modifier, char escapeChar) {
        throw unsupportedOperation(this);
    }

    @Override
    public final IPredicate test(BiFunction<Expression, Expression, IPredicate> operator, Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final <M extends SQLWords> IPredicate test(OptionalClauseOperator<M, Expression, IPredicate> operator, Expression right, M modifier, Expression optionalExp) {
        throw unsupportedOperation(this);
    }

    @Override
    public final <M extends SQLWords> IPredicate test(OptionalClauseOperator<M, Expression, IPredicate> operator, Expression right, M modifier, char escapeChar) {
        throw unsupportedOperation(this);
    }


    @Override
    public final OperationExpression mapTo(TypeMeta typeMeta) {
        throw unsupportedOperation(this);
    }


    @Override
    public final Selection as(String selectionAlas) {
        throw unsupportedOperation(this);
    }

    @Override
    public final SortItem asSortItem() {
        throw unsupportedOperation(this);
    }

    @Override
    public final SortItem asc() {
        if (!(this instanceof CriteriaContexts.SelectionReference)) {
            throw unsupportedOperation(this);
        }
        return ArmySortItems.create(this, SQLs.ASC, null);
    }

    @Override
    public final SortItem desc() {
        if (!(this instanceof CriteriaContexts.SelectionReference)) {
            throw unsupportedOperation(this);
        }
        return ArmySortItems.create(this, SQLs.DESC, null);
    }

    @Override
    public final SortItem ascSpace(@Nullable Statement.NullsFirstLast firstLast) {
        if (!(this instanceof CriteriaContexts.SelectionReference)) {
            throw unsupportedOperation(this);
        }
        return ArmySortItems.create(this, SQLs.ASC, firstLast);
    }

    @Override
    public final SortItem descSpace(@Nullable Statement.NullsFirstLast firstLast) {
        if (!(this instanceof CriteriaContexts.SelectionReference)) {
            throw unsupportedOperation(this);
        }
        return ArmySortItems.create(this, SQLs.DESC, firstLast);
    }

    String operationErrorMessage() {
        return String.format("%s don't support any operation.", this.getClass().getName());
    }


    static CriteriaException unsupportedOperation(NonOperationExpression expression) {
        String m;
        if (expression instanceof MultiValueExpression) {
            m = String.format("%s support only IN(NOT IN) operator.", expression.getClass().getName());
        } else {
            m = expression.operationErrorMessage();
        }
        return ContextStack.clearStackAndCriteriaError(m);
    }

    static RuntimeException nonOperationExpression(final @Nullable Expression expression) {
        final RuntimeException e;
        if (expression == null) {
            e = ContextStack.clearStackAndNullPointer();
        } else if (expression instanceof NonOperationExpression) {
            e = unsupportedOperation((NonOperationExpression) expression);
        } else {
            String m = String.format("%s isn't army expression", expression.getClass().getName());
            e = ContextStack.clearStackAndCriteriaError(m);
        }
        return e;
    }

    static Expression sqlTypeNameExp(final @Nullable MappingType type, final Class<? extends SqlType> sqlTypeClass) {
        if (type == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return new SqlTypeNameExpression(type, sqlTypeClass);
    }


    /**
     * <p>
     * This class is base class only of below:
     *     <ul>
     *         <li>{@link MultiParamExpression}</li>
     *         <li>{@link MultiLiteralExpression}</li>
     *     </ul>
     * </p>
     *
     * @since 1.0
     */
    static abstract class MultiValueExpression extends NonOperationExpression
            implements SqlValueParam.MultiValue, FunctionArg {


    }//MultiValueExpression

    static abstract class NonOperationFunction extends NonOperationExpression implements SQLFunction {

        NonOperationFunction() {
        }

    }//NonOperationFunction

    static final class SqlTypeNameExpression extends NonOperationExpression implements FunctionArg.SingleFunctionArg {

        private final MappingType type;

        private final Class<? extends SqlType> sqlTypeClass;

        private SqlTypeNameExpression(MappingType type, Class<? extends SqlType> sqlTypeClass) {
            this.type = type;
            this.sqlTypeClass = sqlTypeClass;
        }

        @Override
        public TypeMeta typeMeta() {
            throw unsupportedOperation(this);
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final MappingType type = this.type;
            final DialectParser parser;
            parser = context.parser();

            final ServerMeta serverMeta;
            serverMeta = parser.serverMeta();
            final SqlType sqlType;
            sqlType = type.map(serverMeta);
            if (!this.sqlTypeClass.isInstance(sqlType)) {
                String m = String.format(" %s of map result of %s isn't instance of %s", sqlType, type,
                        this.sqlTypeClass.getName());
                throw new CriteriaException(m);
            }
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE);

            if (!sqlType.isUserDefined()) {
                sqlType.sqlTypeName(type, sqlBuilder);
            } else if (type instanceof MappingType.SqlUserDefinedType) {
                parser.identifier(((MappingType.SqlUserDefinedType) type).sqlTypeName(serverMeta), sqlBuilder);
            } else {
                throw _Exceptions.notUserDefinedType(type, sqlType);
            }


        }


    }//SqlTypeNameExpression


}
