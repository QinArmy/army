package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.mapping._MappingFactory;
import io.army.meta.ParamMeta;
import io.army.util._ClassUtils;
import io.army.util._Exceptions;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * this class is base class of most implementation of {@link Expression}
 *
 * @since 1.0
 */
abstract class OperationExpression implements ArmyExpression {

    OperationExpression() {
    }

    @Override
    public final Selection as(final String alias) {
        final Selection selection;
        if (this instanceof TableField) {
            selection = FieldSelectionImpl.create((TableField) this, alias);
        } else if (this instanceof DerivedField) {
            selection = CriteriaContexts.createDerivedSelection((DerivedField) this, alias);
        } else {
            selection = new ExpressionSelection(this, alias);
        }
        return selection;
    }

    @Override
    public final boolean isNullableValue() {
        final boolean nullable;
        if (this instanceof ValueExpression) {
            nullable = ((ValueExpression) this).value() == null;
        } else {
            nullable = this instanceof NamedParam && !(this instanceof NonNullNamedParam);
        }
        return nullable;
    }

    @Override
    public final IPredicate equal(Object operand) {
        return DualPredicate.create(this, DualOperator.EQ, SQLs._nonNullParam(this, operand));
    }

    @Override
    public final IPredicate equalLiteral(Object operand) {
        return DualPredicate.create(this, DualOperator.EQ, SQLs._nonNullLiteral(this, operand));
    }

    @Override
    public final IPredicate equalNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.EQ, SQLs.namedParam(this.paramMeta(), paramName));
    }

    @Override
    public final <C> IPredicate equalExp(Function<C, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.EQ, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    @Override
    public final IPredicate equalExp(Supplier<? extends Expression> supplier) {
        return DualPredicate.create(this, DualOperator.EQ, supplier.get());
    }

    @Override
    public final <C> IPredicate equalAny(Function<C, ? extends SubQuery> subQuery) {
        final SubQuery query;
        query = subQuery.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.EQ, SubQueryOperator.ANY, query);
    }

    @Override
    public final IPredicate equalAny(Supplier<? extends SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.EQ, SubQueryOperator.ANY, supplier.get());
    }

    @Override
    public final <C> IPredicate equalSome(Function<C, ? extends SubQuery> subQuery) {
        final SubQuery query;
        query = subQuery.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.EQ, SubQueryOperator.SOME, query);
    }

    @Override
    public final IPredicate equalSome(Supplier<? extends SubQuery> subQuery) {
        return SubQueryPredicate.create(this, DualOperator.EQ, SubQueryOperator.SOME, subQuery.get());
    }

    @Override
    public final IPredicate lessThan(Object operand) {
        return DualPredicate.create(this, DualOperator.LT, SQLs._nonNullParam(this, operand));
    }

    @Override
    public final IPredicate lessThanLiteral(Object operand) {
        return DualPredicate.create(this, DualOperator.LT, SQLs._nonNullLiteral(this, operand));
    }

    @Override
    public final IPredicate lessThanNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.LT, SQLs.namedParam(this.paramMeta(), paramName));
    }

    @Override
    public final <C> IPredicate lessThanExp(Function<C, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.LT, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    @Override
    public final IPredicate lessThanExp(Supplier<? extends Expression> supplier) {
        return DualPredicate.create(this, DualOperator.LT, SQLs._nonNullParam(this, supplier.get()));
    }

    @Override
    public final <C> IPredicate lessThanAny(Function<C, ? extends SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.LT, SubQueryOperator.ANY, query);
    }

    @Override
    public final IPredicate lessThanAny(Supplier<? extends SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.LT, SubQueryOperator.ANY, supplier.get());
    }

    @Override
    public final <C> IPredicate lessThanSome(Function<C, ? extends SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.LT, SubQueryOperator.SOME, query);
    }

    @Override
    public final IPredicate lessThanSome(Supplier<? extends SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.LT, SubQueryOperator.SOME, supplier.get());
    }

    @Override
    public final <C> IPredicate lessThanAll(Function<C, ? extends SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.LT, SubQueryOperator.ALL, query);
    }

    @Override
    public final IPredicate lessThanAll(Supplier<? extends SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.LT, SubQueryOperator.ALL, supplier.get());
    }

    @Override
    public final IPredicate lessEqual(Object operand) {
        return DualPredicate.create(this, DualOperator.LE, SQLs._nonNullParam(this, operand));
    }

    @Override
    public final IPredicate lessEqualLiteral(Object operand) {
        return DualPredicate.create(this, DualOperator.LE, SQLs._nonNullLiteral(this, operand));
    }

    @Override
    public final IPredicate lessEqualNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.LE, SQLs.namedParam(this.paramMeta(), paramName));
    }

    @Override
    public final <C> IPredicate lessEqualExp(Function<C, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.LE, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    @Override
    public final IPredicate lessEqualExp(Supplier<? extends Expression> supplier) {
        return DualPredicate.create(this, DualOperator.LE, supplier.get());
    }

    @Override
    public final <C> IPredicate lessEqualAny(Function<C, ? extends SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.LE, SubQueryOperator.ANY, query);
    }

    @Override
    public final IPredicate lessEqualAny(Supplier<? extends SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.LE, SubQueryOperator.ANY, supplier.get());
    }

    @Override
    public final <C> IPredicate lessEqualSome(Function<C, ? extends SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.LE, SubQueryOperator.SOME, query);
    }

    @Override
    public final IPredicate lessEqualSome(Supplier<? extends SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.LE, SubQueryOperator.SOME, supplier.get());
    }


    @Override
    public final <C> IPredicate lessEqualAll(Function<C, ? extends SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.LE, SubQueryOperator.ALL, query);
    }

    @Override
    public final IPredicate lessEqualAll(Supplier<? extends SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.LE, SubQueryOperator.ALL, supplier.get());
    }

    @Override
    public final IPredicate greatThan(Object operand) {
        return DualPredicate.create(this, DualOperator.GT, SQLs._nonNullParam(this, operand));
    }

    @Override
    public final IPredicate greatThanLiteral(Object operand) {
        return DualPredicate.create(this, DualOperator.GT, SQLs._nonNullLiteral(this, operand));
    }

    @Override
    public final IPredicate greatThanNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.GT, SQLs.namedParam(this.paramMeta(), paramName));
    }

    @Override
    public final <C> IPredicate greatThanExp(Function<C, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.GT, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    @Override
    public final IPredicate greatThanExp(Supplier<? extends Expression> supplier) {
        return DualPredicate.create(this, DualOperator.GT, supplier.get());
    }

    @Override
    public final <C> IPredicate greatThanAny(Function<C, ? extends SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.GT, SubQueryOperator.ANY, query);
    }

    @Override
    public final IPredicate greatThanAny(Supplier<? extends SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.GT, SubQueryOperator.ANY, supplier.get());
    }


    @Override
    public final <C> IPredicate greatThanSome(Function<C, ? extends SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.GT, SubQueryOperator.SOME, query);
    }

    @Override
    public final IPredicate greatThanSome(Supplier<? extends SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.GT, SubQueryOperator.SOME, supplier.get());
    }

    @Override
    public final <C> IPredicate greatThanAll(Function<C, ? extends SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.GT, SubQueryOperator.ALL, query);
    }

    @Override
    public final IPredicate greatThanAll(Supplier<? extends SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.GT, SubQueryOperator.ALL, supplier.get());
    }

    @Override
    public final IPredicate greatEqual(Object operand) {
        return DualPredicate.create(this, DualOperator.GE, SQLs._nonNullParam(this, operand));
    }

    @Override
    public final IPredicate greatEqualLiteral(Object operand) {
        return DualPredicate.create(this, DualOperator.GE, SQLs._nonNullLiteral(this, operand));
    }

    @Override
    public final IPredicate greatEqualNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.GE, SQLs.namedParam(this.paramMeta(), paramName));
    }

    @Override
    public final <C> IPredicate greatEqualExp(Function<C, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.GE, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    @Override
    public final IPredicate greatEqualExp(Supplier<? extends Expression> supplier) {
        return DualPredicate.create(this, DualOperator.GE, supplier.get());
    }

    @Override
    public final <C> IPredicate greatEqualAny(Function<C, ? extends SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.GE, SubQueryOperator.ANY, query);
    }

    @Override
    public final IPredicate greatEqualAny(Supplier<? extends SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.GE, SubQueryOperator.ANY, supplier.get());
    }

    @Override
    public final <C> IPredicate greatEqualSome(Function<C, ? extends SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.GE, SubQueryOperator.SOME, query);
    }

    @Override
    public final IPredicate greatEqualSome(Supplier<? extends SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.GE, SubQueryOperator.SOME, supplier.get());
    }

    @Override
    public final <C> IPredicate greatEqualAll(Function<C, ? extends SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.GE, SubQueryOperator.ALL, query);
    }

    @Override
    public final IPredicate greatEqualAll(Supplier<? extends SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.GE, SubQueryOperator.ALL, supplier.get());
    }


    @Override
    public final IPredicate notEqual(Object operand) {
        return DualPredicate.create(this, DualOperator.NOT_EQ, SQLs._nonNullParam(this, operand));
    }

    @Override
    public final IPredicate notEqualLiteral(Object operand) {
        return DualPredicate.create(this, DualOperator.NOT_EQ, SQLs._nonNullLiteral(this, operand));
    }

    @Override
    public final IPredicate notEqualNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.NOT_EQ, SQLs.namedParam(this.paramMeta(), paramName));
    }

    @Override
    public final <C> IPredicate notEqualExp(Function<C, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.NOT_EQ, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    @Override
    public final IPredicate notEqualExp(Supplier<? extends Expression> supplier) {
        return DualPredicate.create(this, DualOperator.NOT_EQ, supplier.get());
    }

    @Override
    public final <C> IPredicate notEqualAny(Function<C, ? extends SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.NOT_EQ, SubQueryOperator.ANY, query);
    }

    @Override
    public final IPredicate notEqualAny(Supplier<? extends SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.NOT_EQ, SubQueryOperator.ANY, supplier.get());
    }

    @Override
    public final <C> IPredicate notEqualSome(Function<C, ? extends SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.NOT_EQ, SubQueryOperator.SOME, query);
    }

    @Override
    public final IPredicate notEqualSome(Supplier<? extends SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.NOT_EQ, SubQueryOperator.SOME, supplier.get());
    }

    @Override
    public final <C> IPredicate notEqualAll(Function<C, ? extends SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.NOT_EQ, SubQueryOperator.ALL, query);
    }

    @Override
    public final IPredicate notEqualAll(Supplier<? extends SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.NOT_EQ, SubQueryOperator.ALL, supplier.get());
    }

    @Override
    public final IPredicate between(Object firstOperand, Object secondOperand) {
        final Expression first, second;
        first = SQLs._nonNullParam(this, firstOperand);
        second = SQLs._nonNullParam(this, secondOperand);
        return BetweenPredicate.between(this, first, second);
    }

    @Override
    public final IPredicate betweenLiteral(Object firstOperand, Object secondOperand) {
        final Expression first, second;
        first = SQLs._nonNullLiteral(this, firstOperand);
        second = SQLs._nonNullLiteral(this, secondOperand);
        return BetweenPredicate.between(this, first, second);
    }

    @Override
    public final <C> IPredicate between(Function<C, ExpressionPair> function) {
        final ExpressionPair pair;
        pair = function.apply(CriteriaContextStack.getTopCriteria());
        assert pair != null;
        return BetweenPredicate.between(this, pair.first(), pair.second());
    }

    @Override
    public final IPredicate isNull() {
        return UnaryPredicate.create(UnaryOperator.IS_NULL, this);
    }

    @Override
    public final IPredicate isNotNull() {
        return UnaryPredicate.create(UnaryOperator.IS_NOT_NULL, this);
    }

    @Override
    public final IPredicate in(final Object operand) {
        final Expression exp;
        if (operand instanceof Expression) {
            exp = (Expression) operand;
        } else if (operand instanceof Collection) {
            exp = SQLs.params(this.paramMeta(), (Collection<?>) operand);
        } else if (operand instanceof SubQuery) {
            throw _Exceptions.nonScalarSubQuery((SubQuery) operand);
        } else {
            throw nonCollectionError(DualOperator.IN, operand);
        }
        return DualPredicate.create(this, DualOperator.IN, exp);
    }

    @Override
    public final IPredicate inOptimizing(final Object operand) {
        final Expression exp;
        if (operand instanceof Expression) {
            exp = (Expression) operand;
        } else if (operand instanceof Collection) {
            exp = SQLs.optimizingParams(this.paramMeta(), (Collection<?>) operand);
        } else if (operand instanceof SubQuery) {
            throw _Exceptions.nonScalarSubQuery((SubQuery) operand);
        } else {
            throw nonCollectionError(DualOperator.IN, operand);
        }
        return DualPredicate.create(this, DualOperator.IN, exp);
    }


    @Override
    public final IPredicate inNamed(String paramName, int size) {
        return DualPredicate.create(this, DualOperator.IN, SQLs.namedParams(this.paramMeta(), paramName, size));
    }

    @Override
    public final <C> IPredicate inExp(Function<C, ? extends Expression> function) {
        return this.in(function.apply(CriteriaContextStack.getTopCriteria()));
    }

    @Override
    public final IPredicate inExp(Supplier<? extends Expression> supplier) {
        return this.in(supplier.get());
    }

    @Override
    public final IPredicate notIn(Object operand) {
        final Expression exp;
        if (operand instanceof Expression) {
            exp = (Expression) operand;
        } else if (operand instanceof Collection) {
            exp = SQLs.params(this.paramMeta(), (Collection<?>) operand);
        } else if (operand instanceof SubQuery) {
            throw _Exceptions.nonScalarSubQuery((SubQuery) operand);
        } else {
            throw nonCollectionError(DualOperator.NOT_IN, operand);
        }
        return DualPredicate.create(this, DualOperator.NOT_IN, exp);
    }

    @Override
    public final IPredicate notInOptimizing(Object operand) {
        final Expression exp;
        if (operand instanceof Expression) {
            exp = (Expression) operand;
        } else if (operand instanceof Collection) {
            exp = SQLs.optimizingParams(this.paramMeta(), (Collection<?>) operand);
        } else if (operand instanceof SubQuery) {
            throw _Exceptions.nonScalarSubQuery((SubQuery) operand);
        } else {
            throw nonCollectionError(DualOperator.NOT_IN, operand);
        }
        return DualPredicate.create(this, DualOperator.NOT_IN, exp);
    }

    @Override
    public final IPredicate notInNamed(String paramName, int size) {
        return DualPredicate.create(this, DualOperator.NOT_IN, SQLs.namedParams(this.paramMeta(), paramName, size));
    }

    @Override
    public final <C> IPredicate notInExp(Function<C, ? extends Expression> function) {
        return this.notIn(function.apply(CriteriaContextStack.getTopCriteria()));
    }

    @Override
    public final IPredicate notInExp(Supplier<? extends Expression> supplier) {
        return this.notIn(supplier.get());
    }

    @Override
    public final IPredicate like(Object pattern) {
        return this.pattern(DualOperator.LIKE, pattern);
    }

    @Override
    public final IPredicate likeNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.LIKE, SQLs.namedParam(this.paramMeta(), paramName));
    }

    @Override
    public final <C> IPredicate likeExp(Function<C, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.LIKE, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    @Override
    public final IPredicate likeExp(Supplier<? extends Expression> supplier) {
        return DualPredicate.create(this, DualOperator.LIKE, supplier.get());
    }

    @Override
    public final IPredicate notLike(Object pattern) {
        return this.pattern(DualOperator.NOT_LIKE, pattern);
    }

    @Override
    public final IPredicate notLikeNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.NOT_LIKE, SQLs.namedParam(this.paramMeta(), paramName));
    }

    @Override
    public final <C> IPredicate notLikeExp(Function<C, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.NOT_LIKE, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    @Override
    public final IPredicate notLikeExp(Supplier<? extends Expression> supplier) {
        return DualPredicate.create(this, DualOperator.NOT_LIKE, supplier.get());
    }

    @Override
    public final Expression mod(Object operand) {
        return DualExpression.create(this, DualOperator.MOD, SQLs._nonNullParam(this, operand));
    }

    @Override
    public final Expression modLiteral(Object operand) {
        return DualExpression.create(this, DualOperator.MOD, SQLs._nonNullLiteral(this, operand));
    }

    @Override
    public final Expression modNamed(String paramName) {
        return DualExpression.create(this, DualOperator.MOD, SQLs.namedParam(this.paramMeta(), paramName));
    }

    @Override
    public final <C> Expression modExp(Function<C, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.MOD, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    @Override
    public final Expression modExp(Supplier<? extends Expression> supplier) {
        return DualExpression.create(this, DualOperator.MOD, supplier.get());
    }

    @Override
    public final Expression times(Object multiplicand) {
        return DualExpression.create(this, DualOperator.MULTIPLY, SQLs._nonNullParam(this, multiplicand));
    }

    @Override
    public final Expression timesLiteral(Object multiplicand) {
        return DualExpression.create(this, DualOperator.MULTIPLY, SQLs._nonNullLiteral(this, multiplicand));
    }

    @Override
    public final Expression timesNamed(String paramName) {
        return DualExpression.create(this, DualOperator.MULTIPLY, SQLs.namedParam(this.paramMeta(), paramName));
    }

    @Override
    public final <C> Expression timesExp(Function<C, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.MULTIPLY, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    @Override
    public final Expression timesExp(Supplier<? extends Expression> supplier) {
        return DualExpression.create(this, DualOperator.MULTIPLY, supplier.get());
    }

    @Override
    public final Expression plus(Object augend) {
        return DualExpression.create(this, DualOperator.PLUS, SQLs._nonNullParam(this, augend));
    }

    @Override
    public final Expression plusLiteral(Object augend) {
        return DualExpression.create(this, DualOperator.PLUS, SQLs._nonNullLiteral(this, augend));
    }

    @Override
    public final Expression plusNamed(String paramName) {
        return DualExpression.create(this, DualOperator.PLUS, SQLs.namedParam(this.paramMeta(), paramName));
    }


    @Override
    public final <C> Expression plusExp(Function<C, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.PLUS, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    @Override
    public final Expression plusExp(Supplier<? extends Expression> supplier) {
        return DualExpression.create(this, DualOperator.PLUS, supplier.get());
    }

    @Override
    public final Expression minus(Object minuend) {
        return DualExpression.create(this, DualOperator.MINUS, SQLs._nonNullParam(this, minuend));
    }

    @Override
    public final Expression minusLiteral(Object minuend) {
        return DualExpression.create(this, DualOperator.MINUS, SQLs._nonNullLiteral(this, minuend));
    }

    @Override
    public final Expression minusNamed(String paramName) {
        return DualExpression.create(this, DualOperator.MINUS, SQLs.namedParam(this.paramMeta(), paramName));
    }

    @Override
    public final <C> Expression minusExp(Function<C, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.MINUS, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    @Override
    public final Expression minusExp(Supplier<? extends Expression> supplier) {
        return DualExpression.create(this, DualOperator.MINUS, supplier.get());
    }

    @Override
    public final Expression divide(Object divisor) {
        return DualExpression.create(this, DualOperator.DIVIDE, SQLs._nonNullParam(this, divisor));
    }

    @Override
    public final Expression divideLiteral(Object divisor) {
        return DualExpression.create(this, DualOperator.DIVIDE, SQLs._nonNullLiteral(this, divisor));
    }

    @Override
    public final Expression divideNamed(String paramName) {
        return DualExpression.create(this, DualOperator.DIVIDE, SQLs.namedParam(this.paramMeta(), paramName));
    }

    @Override
    public final <C> Expression divideExp(Function<C, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.DIVIDE, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    @Override
    public final Expression divideExp(Supplier<? extends Expression> supplier) {
        return DualExpression.create(this, DualOperator.DIVIDE, supplier.get());
    }

    @Override
    public final Expression negate() {
        return UnaryExpression.create(this, UnaryOperator.NEGATED);
    }

    @Override
    public final Expression bitwiseAnd(Object operand) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, SQLs._nonNullParam(this, operand));
    }

    @Override
    public final Expression bitwiseAndLiteral(Object operand) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, SQLs._nonNullLiteral(this, operand));
    }

    @Override
    public final Expression bitwiseAndNamed(String paramName) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, SQLs.namedParam(this.paramMeta(), paramName));
    }

    @Override
    public final <C> Expression bitwiseAndExp(Function<C, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    @Override
    public final Expression bitwiseAndExp(Supplier<? extends Expression> supplier) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, supplier.get());
    }

    @Override
    public final Expression bitwiseOr(Object operand) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, SQLs._nonNullParam(this, operand));
    }

    @Override
    public final Expression bitwiseOrLiteral(Object operand) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, SQLs._nonNullLiteral(this, operand));
    }

    @Override
    public final Expression bitwiseOrNamed(String paramName) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, SQLs.namedParam(this.paramMeta(), paramName));
    }

    @Override
    public final <C> Expression bitwiseOrExp(Function<C, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    @Override
    public final Expression bitwiseOrExp(Supplier<? extends Expression> supplier) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, supplier.get());
    }

    @Override
    public final Expression xor(Object operand) {
        return DualExpression.create(this, DualOperator.XOR, SQLs._nonNullParam(this, operand));
    }

    @Override
    public final Expression xorLiteral(Object operand) {
        return DualExpression.create(this, DualOperator.XOR, SQLs._nonNullLiteral(this, operand));
    }

    @Override
    public final Expression xorNamed(String paramName) {
        return DualExpression.create(this, DualOperator.XOR, SQLs.namedParam(this.paramMeta(), paramName));
    }

    @Override
    public final <C> Expression xorExp(Function<C, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.XOR, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    @Override
    public final Expression xorExp(Supplier<? extends Expression> supplier) {
        return DualExpression.create(this, DualOperator.XOR, supplier.get());
    }

    @Override
    public final Expression inversion() {
        return UnaryExpression.create(this, UnaryOperator.INVERT);
    }

    @Override
    public final Expression rightShift(Object bitNumber) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, SQLs._nonNullParam(this, bitNumber));
    }

    @Override
    public final Expression rightShiftLiteral(Object bitNumber) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, SQLs._nonNullLiteral(this, bitNumber));
    }

    @Override
    public final Expression rightShiftNamed(String paramName) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, SQLs.namedParam(this.paramMeta(), paramName));
    }

    @Override
    public final <C> Expression rightShiftExp(Function<C, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    @Override
    public final Expression rightShiftExp(Supplier<? extends Expression> supplier) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, supplier.get());
    }

    @Override
    public final Expression leftShift(Object bitNumber) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, SQLs._nonNullParam(this, bitNumber));
    }

    @Override
    public final Expression leftShiftLiteral(Object bitNumber) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, SQLs._nonNullLiteral(this, bitNumber));
    }

    @Override
    public final Expression leftShiftNamed(String paramName) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, SQLs.namedParam(this.paramMeta(), paramName));
    }

    @Override
    public final <C> Expression leftShiftExp(Function<C, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    @Override
    public final Expression leftShiftExp(Supplier<? extends Expression> supplier) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, supplier.get());
    }

    @Override
    public final Expression asType(Class<?> convertType) {
        return CastExpression.cast(this, _MappingFactory.getDefault(convertType));
    }

    @Override
    public final Expression asType(ParamMeta paramMeta) {
        return CastExpression.cast(this, paramMeta);
    }

    public final Expression bracket() {
        return BracketsExpression.bracket(this);
    }

    @Override
    public final SortItem asc() {
        return new SortItemImpl(this, true);
    }

    @Override
    public final SortItem desc() {
        return new SortItemImpl(this, false);
    }


    /*################################## blow protected template method ##################################*/

    private IPredicate pattern(final DualOperator operator, final Object pattern) {
        switch (operator) {
            case LIKE:
            case NOT_LIKE:
                break;
            default:
                throw new IllegalArgumentException(String.format("%s error.", operator));
        }
        final Expression valueExp;
        if (pattern instanceof Expression) {
            valueExp = (Expression) pattern;
        } else if (pattern instanceof SubQuery) {
            throw _Exceptions.nonScalarSubQuery((SubQuery) pattern);
        } else if (pattern instanceof String) {
            valueExp = SQLs._nonNullParam(this, pattern);
        } else {
            String m = String.format("%s support only %s and %s ."
                    , operator, Expression.class.getName(), String.class.getName());
            throw new CriteriaException(m);
        }
        return DualPredicate.create(this, operator, valueExp);
    }

    @Nullable
    private IPredicate ifInOrNotIn(final DualOperator operator, final @Nullable Object value, final boolean optimizing) {
        switch (operator) {
            case IN:
            case NOT_IN:
                break;
            default:
                throw _Exceptions.unexpectedEnum(operator);
        }
        final IPredicate predicate;
        final Collection<?> collection;
        if (value == null) {
            predicate = null;
        } else if (value instanceof Expression) {
            predicate = DualPredicate.create(this, operator, (Expression) value);
        } else if (value instanceof SubQuery) {
            throw _Exceptions.nonScalarSubQuery((SubQuery) value);
        } else if ((collection = (Collection<?>) value).size() == 0) {
            predicate = null;
        } else if (optimizing) {
            predicate = DualPredicate.create(this, operator, SQLs.optimizingParams(this.paramMeta(), collection));
        } else {
            predicate = DualPredicate.create(this, operator, SQLs.params(this.paramMeta(), collection));
        }
        return predicate;
    }


    private static CriteriaException nonCollectionError(DualOperator operator, Object operand) {
        switch (operator) {
            case IN:
            case NOT_IN:
                break;
            default:
                throw _Exceptions.unexpectedEnum(operator);
        }
        String m = String.format("%s operator support only %s and %s ,but operand is %s"
                , operator.signText, Expression.class.getName()
                , Collection.class.getName()
                , _ClassUtils.safeClassName(operand));
        return new CriteriaException(m);
    }

}
