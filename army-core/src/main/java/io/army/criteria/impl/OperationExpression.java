package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.mapping._MappingFactory;
import io.army.meta.ParamMeta;
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
        if (this instanceof GenericField) {
            selection = FieldSelectionImpl.create((GenericField<?>) this, alias);
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
        return DualPredicate.create(this, DualOperator.EQ, SQLs.nonNullParam(this, operand));
    }

    @Override
    public final IPredicate equalLiteral(Object operand) {
        return DualPredicate.create(this, DualOperator.EQ, SQLs.nonNullLiteral(this, operand));
    }

    @Override
    public final IPredicate equalNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.EQ, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C> IPredicate equalExp(Function<C, Expression> function) {
        return DualPredicate.create(this, DualOperator.EQ, function.apply(CriteriaContextStack.getCriteria()));
    }

    @Override
    public final IPredicate equalExp(Supplier<Expression> supplier) {
        return DualPredicate.create(this, DualOperator.EQ, supplier.get());
    }

    @Nullable
    @Override
    public final IPredicate ifEqual(Supplier<Object> operand) {
        final Object value;
        value = operand.get();
        return value == null ? null : this.equal(value);
    }

    @Override
    public final <C> IPredicate ifEqual(Function<C, Object> operand) {
        final Object value;
        value = operand.apply(CriteriaContextStack.getCriteria());
        return value == null ? null : this.equal(value);
    }

    @Override
    public final IPredicate ifEqual(Function<String, Object> operand, String keyName) {
        final Object value;
        value = operand.apply(keyName);
        return value == null ? null : this.equal(value);
    }

    @Override
    public final IPredicate ifEqualLiteral(Supplier<Object> operand) {
        final Object value;
        value = operand.get();
        return value == null ? null : this.equalLiteral(value);
    }

    @Override
    public final IPredicate ifEqualLiteral(Function<String, Object> operand, String keyName) {
        final Object value;
        value = operand.apply(keyName);
        return value == null ? null : this.equalLiteral(value);
    }


    @Override
    public final <C> IPredicate equalAny(Function<C, SubQuery> subQuery) {
        final SubQuery query;
        query = subQuery.apply(CriteriaContextStack.getCriteria());
        return SubQueryPredicate.create(this, DualOperator.EQ, SubQueryOperator.ANY, query);
    }

    @Override
    public final IPredicate equalAny(Supplier<SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.EQ, SubQueryOperator.ANY, supplier.get());
    }

    @Override
    public final <C> IPredicate equalSome(Function<C, SubQuery> subQuery) {
        final SubQuery query;
        query = subQuery.apply(CriteriaContextStack.getCriteria());
        return SubQueryPredicate.create(this, DualOperator.EQ, SubQueryOperator.SOME, query);
    }

    @Override
    public final IPredicate equalSome(Supplier<SubQuery> subQuery) {
        return SubQueryPredicate.create(this, DualOperator.EQ, SubQueryOperator.SOME, subQuery.get());
    }

    @Override
    public final IPredicate lessThan(Object operand) {
        return DualPredicate.create(this, DualOperator.LT, SQLs.nonNullParam(this, operand));
    }

    @Override
    public final IPredicate lessThanLiteral(Object operand) {
        return DualPredicate.create(this, DualOperator.LT, SQLs.nonNullLiteral(this, operand));
    }

    @Override
    public final IPredicate lessThanNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.LT, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C> IPredicate lessThanExp(Function<C, Expression> function) {
        return DualPredicate.create(this, DualOperator.LT, function.apply(CriteriaContextStack.getCriteria()));
    }

    @Override
    public final IPredicate lessThanExp(Supplier<Expression> supplier) {
        return DualPredicate.create(this, DualOperator.LT, supplier.get());
    }

    @Nullable
    @Override
    public final IPredicate ifLessThan(Supplier<Object> operand) {
        final Object value;
        value = operand.get();
        return value == null ? null : this.lessThan(value);
    }

    @Override
    public final <C> IPredicate ifLessThan(Function<C, Object> operand) {
        final Object value;
        value = operand.apply(CriteriaContextStack.getCriteria());
        return value == null ? null : this.lessThan(value);
    }

    @Override
    public final IPredicate ifLessThan(Function<String, Object> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        return value == null ? null : this.lessThan(value);
    }

    @Override
    public final IPredicate ifLessThanLiteral(Supplier<Object> operand) {
        final Object value;
        value = operand.get();
        return value == null ? null : this.lessThanLiteral(value);
    }

    @Override
    public final IPredicate ifLessThanLiteral(Function<String, Object> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        return value == null ? null : this.lessThanLiteral(value);
    }


    @Override
    public final <C> IPredicate lessThanAny(Function<C, SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getCriteria());
        return SubQueryPredicate.create(this, DualOperator.LT, SubQueryOperator.ANY, query);
    }

    @Override
    public final IPredicate lessThanAny(Supplier<SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.LT, SubQueryOperator.ANY, supplier.get());
    }

    @Override
    public final <C> IPredicate lessThanSome(Function<C, SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getCriteria());
        return SubQueryPredicate.create(this, DualOperator.LT, SubQueryOperator.SOME, query);
    }

    @Override
    public final IPredicate lessThanSome(Supplier<SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.LT, SubQueryOperator.SOME, supplier.get());
    }

    @Override
    public final <C> IPredicate lessThanAll(Function<C, SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getCriteria());
        return SubQueryPredicate.create(this, DualOperator.LT, SubQueryOperator.ALL, query);
    }

    @Override
    public final IPredicate lessThanAll(Supplier<SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.LT, SubQueryOperator.ALL, supplier.get());
    }

    @Override
    public final IPredicate lessEqual(Object operand) {
        return DualPredicate.create(this, DualOperator.LE, SQLs.nonNullParam(this, operand));
    }

    @Override
    public final IPredicate lessEqualLiteral(Object operand) {
        return DualPredicate.create(this, DualOperator.LE, SQLs.nonNullLiteral(this, operand));
    }

    @Override
    public final IPredicate lessEqualNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.LE, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C> IPredicate lessEqualExp(Function<C, Expression> function) {
        return DualPredicate.create(this, DualOperator.LE, function.apply(CriteriaContextStack.getCriteria()));
    }

    @Override
    public final IPredicate lessEqualExp(Supplier<Expression> supplier) {
        return DualPredicate.create(this, DualOperator.LE, supplier.get());
    }


    @Override
    public final IPredicate ifLessEqual(Supplier<Object> operand) {
        final Object value;
        value = operand.get();
        return value == null ? null : this.lessEqual(value);
    }

    @Override
    public final <C> IPredicate ifLessEqual(Function<C, Object> operand) {
        final Object value;
        value = operand.apply(CriteriaContextStack.getCriteria());
        return value == null ? null : this.lessEqual(value);
    }

    @Override
    public final IPredicate ifLessEqual(Function<String, Object> operand, String keyName) {
        final Object value;
        value = operand.apply(keyName);
        return value == null ? null : this.lessEqual(value);
    }

    @Override
    public final IPredicate ifLessEqualLiteral(Supplier<Object> operand) {
        final Object value;
        value = operand.get();
        return value == null ? null : this.lessEqualLiteral(value);
    }

    @Override
    public final IPredicate ifLessEqualLiteral(Function<String, Object> operand, String keyName) {
        final Object value;
        value = operand.apply(keyName);
        return value == null ? null : this.lessEqualLiteral(value);
    }

    @Override
    public final <C> IPredicate lessEqualAny(Function<C, SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getCriteria());
        return SubQueryPredicate.create(this, DualOperator.LE, SubQueryOperator.ANY, query);
    }

    @Override
    public final IPredicate lessEqualAny(Supplier<SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.LE, SubQueryOperator.ANY, supplier.get());
    }

    @Override
    public final <C> IPredicate lessEqualSome(Function<C, SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getCriteria());
        return SubQueryPredicate.create(this, DualOperator.LE, SubQueryOperator.SOME, query);
    }

    @Override
    public final IPredicate lessEqualSome(Supplier<SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.LE, SubQueryOperator.SOME, supplier.get());
    }


    @Override
    public final <C> IPredicate lessEqualAll(Function<C, SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getCriteria());
        return SubQueryPredicate.create(this, DualOperator.LE, SubQueryOperator.ALL, query);
    }

    @Override
    public final IPredicate lessEqualAll(Supplier<SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.LE, SubQueryOperator.ALL, supplier.get());
    }

    @Override
    public final IPredicate greatThan(Object operand) {
        return DualPredicate.create(this, DualOperator.GT, SQLs.nonNullParam(this, operand));
    }

    @Override
    public final IPredicate greatThanLiteral(Object operand) {
        return DualPredicate.create(this, DualOperator.GT, SQLs.nonNullLiteral(this, operand));
    }

    @Override
    public final IPredicate greatThanNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.GT, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C> IPredicate greatThanExp(Function<C, Expression> function) {
        return DualPredicate.create(this, DualOperator.GT, function.apply(CriteriaContextStack.getCriteria()));
    }

    @Override
    public final IPredicate greatThanExp(Supplier<Expression> supplier) {
        return DualPredicate.create(this, DualOperator.GT, supplier.get());
    }

    @Override
    public final IPredicate ifGreatThan(Supplier<Object> operand) {
        final Object value;
        value = operand.get();
        return value == null ? null : this.greatThan(value);
    }

    @Override
    public final <C> IPredicate ifGreatThan(Function<C, Object> operand) {
        final Object value;
        value = operand.apply(CriteriaContextStack.getCriteria());
        return value == null ? null : this.greatThan(value);
    }

    @Override
    public final IPredicate ifGreatThan(Function<String, Object> operand, String keyName) {
        final Object value;
        value = operand.apply(keyName);
        return value == null ? null : this.greatThan(value);
    }


    @Override
    public final IPredicate ifGreatThanLiteral(Supplier<Object> operand) {
        final Object value;
        value = operand.get();
        return value == null ? null : this.greatThanLiteral(value);
    }

    @Override
    public final IPredicate ifGreatThanLiteral(Function<String, Object> operand, String keyName) {
        final Object value;
        value = operand.apply(keyName);
        return value == null ? null : this.greatThanLiteral(value);
    }

    @Override
    public final <C> IPredicate greatThanAny(Function<C, SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getCriteria());
        return SubQueryPredicate.create(this, DualOperator.GT, SubQueryOperator.ANY, query);
    }

    @Override
    public final IPredicate greatThanAny(Supplier<SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.GT, SubQueryOperator.ANY, supplier.get());
    }


    @Override
    public final <C> IPredicate greatThanSome(Function<C, SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getCriteria());
        return SubQueryPredicate.create(this, DualOperator.GT, SubQueryOperator.SOME, query);
    }

    @Override
    public final IPredicate greatThanSome(Supplier<SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.GT, SubQueryOperator.SOME, supplier.get());
    }

    @Override
    public final <C> IPredicate greatThanAll(Function<C, SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getCriteria());
        return SubQueryPredicate.create(this, DualOperator.GT, SubQueryOperator.ALL, query);
    }

    @Override
    public final IPredicate greatThanAll(Supplier<SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.GT, SubQueryOperator.ALL, supplier.get());
    }

    @Override
    public final IPredicate greatEqual(Object operand) {
        return DualPredicate.create(this, DualOperator.GE, SQLs.nonNullParam(this, operand));
    }

    @Override
    public final IPredicate greatEqualLiteral(Object operand) {
        return DualPredicate.create(this, DualOperator.GE, SQLs.nonNullLiteral(this, operand));
    }

    @Override
    public final IPredicate greatEqualNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.GE, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C> IPredicate greatEqualExp(Function<C, Expression> function) {
        return DualPredicate.create(this, DualOperator.GE, function.apply(CriteriaContextStack.getCriteria()));
    }

    @Override
    public final IPredicate greatEqualExp(Supplier<Expression> supplier) {
        return DualPredicate.create(this, DualOperator.GE, supplier.get());
    }

    @Override
    public final IPredicate ifGreatEqual(Supplier<Object> operand) {
        final Object value;
        value = operand.get();
        return value == null ? null : this.greatEqual(value);
    }

    @Override
    public final <C> IPredicate ifGreatEqual(Function<C, Object> operand) {
        final Object value;
        value = operand.apply(CriteriaContextStack.getCriteria());
        return value == null ? null : this.greatEqual(value);
    }

    @Override
    public final IPredicate ifGreatEqual(Function<String, Object> operand, String keyName) {
        final Object value;
        value = operand.apply(keyName);
        return value == null ? null : this.greatEqual(value);
    }

    @Override
    public final IPredicate ifGreatEqualLiteral(Supplier<Object> operand) {
        final Object value;
        value = operand.get();
        return value == null ? null : this.greatEqualLiteral(value);
    }

    @Override
    public final IPredicate ifGreatEqualLiteral(Function<String, Object> operand, String keyName) {
        final Object value;
        value = operand.apply(keyName);
        return value == null ? null : this.greatEqualLiteral(value);
    }

    @Override
    public final <C> IPredicate greatEqualAny(Function<C, SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getCriteria());
        return SubQueryPredicate.create(this, DualOperator.GE, SubQueryOperator.ANY, query);
    }

    @Override
    public final IPredicate greatEqualAny(Supplier<SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.GE, SubQueryOperator.ANY, supplier.get());
    }

    @Override
    public final <C> IPredicate greatEqualSome(Function<C, SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getCriteria());
        return SubQueryPredicate.create(this, DualOperator.GE, SubQueryOperator.SOME, query);
    }

    @Override
    public final IPredicate greatEqualSome(Supplier<SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.GE, SubQueryOperator.SOME, supplier.get());
    }

    @Override
    public final <C> IPredicate greatEqualAll(Function<C, SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getCriteria());
        return SubQueryPredicate.create(this, DualOperator.GE, SubQueryOperator.ALL, query);
    }

    @Override
    public final IPredicate greatEqualAll(Supplier<SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.GE, SubQueryOperator.ALL, supplier.get());
    }


    @Override
    public final IPredicate notEqual(Object operand) {
        return DualPredicate.create(this, DualOperator.NOT_EQ, SQLs.nonNullParam(this, operand));
    }

    @Override
    public final IPredicate notEqualLiteral(Object operand) {
        return DualPredicate.create(this, DualOperator.NOT_EQ, SQLs.nonNullLiteral(this, operand));

    }

    @Override
    public final IPredicate notEqualNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.NOT_EQ, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C> IPredicate notEqualExp(Function<C, Expression> function) {
        return DualPredicate.create(this, DualOperator.NOT_EQ, function.apply(CriteriaContextStack.getCriteria()));
    }

    @Override
    public final IPredicate notEqualExp(Supplier<Expression> supplier) {
        return DualPredicate.create(this, DualOperator.NOT_EQ, supplier.get());
    }

    @Override
    public final IPredicate ifNotEqual(Supplier<Object> operand) {
        final Object value;
        value = operand.get();
        return value == null ? null : this.notEqual(value);
    }


    @Override
    public final <C> IPredicate ifNotEqual(Function<C, Object> operand) {
        final Object value;
        value = operand.apply(CriteriaContextStack.getCriteria());
        return value == null ? null : this.notEqual(value);
    }

    @Override
    public final IPredicate ifNotEqual(Function<String, Object> operand, String keyName) {
        final Object value;
        value = operand.apply(keyName);
        return value == null ? null : this.notEqual(value);
    }

    @Override
    public final IPredicate ifNotEqualLiteral(Supplier<Object> operand) {
        final Object value;
        value = operand.get();
        return value == null ? null : this.notEqualLiteral(value);
    }

    @Override
    public final IPredicate ifNotEqualLiteral(Function<String, Object> operand, String keyName) {
        final Object value;
        value = operand.apply(keyName);
        return value == null ? null : this.notEqualLiteral(value);
    }


    @Override
    public final <C> IPredicate notEqualAny(Function<C, SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getCriteria());
        return SubQueryPredicate.create(this, DualOperator.NOT_EQ, SubQueryOperator.ANY, query);
    }

    @Override
    public final IPredicate notEqualAny(Supplier<SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.NOT_EQ, SubQueryOperator.ANY, supplier.get());
    }

    @Override
    public final <C> IPredicate notEqualSome(Function<C, SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getCriteria());
        return SubQueryPredicate.create(this, DualOperator.NOT_EQ, SubQueryOperator.SOME, query);
    }

    @Override
    public final IPredicate notEqualSome(Supplier<SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.NOT_EQ, SubQueryOperator.SOME, supplier.get());
    }

    @Override
    public final <C> IPredicate notEqualAll(Function<C, SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getCriteria());
        return SubQueryPredicate.create(this, DualOperator.NOT_EQ, SubQueryOperator.ALL, query);
    }

    @Override
    public final IPredicate notEqualAll(Supplier<SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.NOT_EQ, SubQueryOperator.ALL, supplier.get());
    }

    @Override
    public final IPredicate between(Object firstOperand, Object secondOperand) {
        final Expression first, second;
        first = SQLs.nonNullParam(this, firstOperand);
        second = SQLs.nonNullParam(this, secondOperand);
        return BetweenPredicate.between(this, first, second);
    }

    @Override
    public final IPredicate betweenLiteral(Object firstOperand, Object secondOperand) {
        final Expression first, second;
        first = SQLs.nonNullLiteral(this, firstOperand);
        second = SQLs.nonNullLiteral(this, secondOperand);
        return BetweenPredicate.between(this, first, second);
    }

    @Override
    public final <C> IPredicate between(Function<C, ExpressionPair> function) {
        final ExpressionPair wrapper;
        wrapper = function.apply(CriteriaContextStack.getCriteria());
        assert wrapper != null;
        return BetweenPredicate.between(this, wrapper.first(), wrapper.second());
    }

    @Nullable
    @Override
    public final IPredicate ifBetween(Supplier<Object> firstOperand, Supplier<Object> secondOperand) {
        final Object firstValue, secondValue;
        firstValue = firstOperand.get();
        secondValue = secondOperand.get();
        final IPredicate predicate;
        if (firstValue != null && secondValue != null) {
            predicate = this.between(firstValue, secondValue);
        } else {
            predicate = null;
        }
        return predicate;
    }

    @Override
    public final IPredicate ifBetween(Function<String, Object> function, String firstKey, String secondKey) {
        final Object firstValue, secondValue;
        firstValue = function.apply(firstKey);
        secondValue = function.apply(secondKey);
        final IPredicate predicate;
        if (firstValue != null && secondValue != null) {
            predicate = this.between(firstValue, secondValue);
        } else {
            predicate = null;
        }
        return predicate;
    }

    @Override
    public final IPredicate ifBetweenLiteral(Supplier<Object> firstOperand, Supplier<Object> secondOperand) {
        final Object firstValue, secondValue;
        firstValue = firstOperand.get();
        secondValue = secondOperand.get();
        final IPredicate predicate;
        if (firstValue != null && secondValue != null) {
            predicate = this.betweenLiteral(firstValue, secondValue);
        } else {
            predicate = null;
        }
        return predicate;
    }

    @Override
    public final IPredicate ifBetweenLiteral(Function<String, Object> function, String firstKey, String secondKey) {
        final Object firstValue, secondValue;
        firstValue = function.apply(firstKey);
        secondValue = function.apply(secondKey);
        final IPredicate predicate;
        if (firstValue != null && secondValue != null) {
            predicate = this.betweenLiteral(firstValue, secondValue);
        } else {
            predicate = null;
        }
        return predicate;
    }

    @Nullable
    @Override
    public final <C> IPredicate ifBetween(Function<C, ExpressionPair> function) {
        final ExpressionPair pair;
        pair = function.apply(CriteriaContextStack.getCriteria());
        return pair == null ? null : BetweenPredicate.between(this, pair.first(), pair.second());
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
    public final IPredicate in(final Object collectionOrExp) {
        final Expression exp;
        if (collectionOrExp instanceof Expression) {
            exp = (Expression) collectionOrExp;
        } else {
            exp = SQLs.optimizingParams(this.paramMeta(), (Collection<?>) collectionOrExp);
        }
        return DualPredicate.create(this, DualOperator.IN, exp);
    }

    @Override
    public final IPredicate inParam(final Object collectionOrExp) {
        final Expression exp;
        if (collectionOrExp instanceof Expression) {
            exp = (Expression) collectionOrExp;
        } else {
            exp = SQLs.params(this.paramMeta(), (Collection<?>) collectionOrExp);
        }
        return DualPredicate.create(this, DualOperator.IN, exp);
    }

    @Override
    public final IPredicate inNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.IN, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C> IPredicate inExp(Function<C, Expression> function) {
        return DualPredicate.create(this, DualOperator.IN, function.apply(CriteriaContextStack.getCriteria()));
    }

    @Override
    public final IPredicate inExp(Supplier<Expression> supplier) {
        return DualPredicate.create(this, DualOperator.IN, supplier.get());
    }

    @Override
    public final IPredicate ifIn(Supplier<Object> collectionOrExp) {
        return this.ifInOrNotIn(DualOperator.IN, collectionOrExp.get(), true);
    }

    @Override
    public final <C> IPredicate ifIn(Function<C, Object> collectionOrExp) {
        return this.ifInOrNotIn(DualOperator.IN, collectionOrExp.apply(CriteriaContextStack.getCriteria()), true);
    }

    @Override
    public final IPredicate ifIn(Function<String, Object> function, String keyName) {
        return this.ifInOrNotIn(DualOperator.IN, function.apply(keyName), true);
    }

    @Override
    public <C> IPredicate ifInParam(Function<C, Object> collectionOrExp) {
        return this.ifInOrNotIn(DualOperator.IN, collectionOrExp.apply(CriteriaContextStack.getCriteria()), false);
    }

    @Override
    public IPredicate ifInParam(Supplier<Object> collectionOrExp) {
        return this.ifInOrNotIn(DualOperator.IN, collectionOrExp.get(), false);
    }

    @Override
    public IPredicate ifInParam(Function<String, Object> function, String keyName) {
        return this.ifInOrNotIn(DualOperator.IN, function.apply(keyName), false);
    }

    @Override
    public final IPredicate notIn(Object collectionOrExp) {
        final Expression exp;
        if (collectionOrExp instanceof Expression) {
            exp = (Expression) collectionOrExp;
        } else {
            exp = SQLs.optimizingParams(this.paramMeta(), (Collection<?>) collectionOrExp);
        }
        return DualPredicate.create(this, DualOperator.NOT_IN, exp);
    }

    @Override
    public final IPredicate notInParam(Object collectionOrExp) {
        final Expression exp;
        if (collectionOrExp instanceof Expression) {
            exp = (Expression) collectionOrExp;
        } else {
            exp = SQLs.params(this.paramMeta(), (Collection<?>) collectionOrExp);
        }
        return DualPredicate.create(this, DualOperator.NOT_IN, exp);
    }

    @Override
    public final IPredicate notInNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.NOT_IN, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C> IPredicate notInExp(Function<C, Expression> function) {
        return DualPredicate.create(this, DualOperator.NOT_IN, function.apply(CriteriaContextStack.getCriteria()));
    }

    @Override
    public final IPredicate notInExp(Supplier<Expression> supplier) {
        return DualPredicate.create(this, DualOperator.NOT_IN, supplier.get());
    }

    @Override
    public final <C> IPredicate ifNotIn(Function<C, Object> collectionOrExp) {
        return this.ifInOrNotIn(DualOperator.NOT_IN, collectionOrExp.apply(CriteriaContextStack.getCriteria()), true);
    }

    @Override
    public final IPredicate ifNotIn(Supplier<Object> collectionOrExp) {
        return this.ifInOrNotIn(DualOperator.NOT_IN, collectionOrExp.get(), true);
    }

    @Override
    public final IPredicate ifNotIn(Function<String, Object> function, String keyName) {
        return this.ifInOrNotIn(DualOperator.NOT_IN, function.apply(keyName), true);
    }

    @Override
    public final <C> IPredicate ifNotInParam(Function<C, Object> collectionOrExp) {
        return this.ifInOrNotIn(DualOperator.NOT_IN, collectionOrExp.apply(CriteriaContextStack.getCriteria()), false);
    }

    @Override
    public final IPredicate ifNotInParam(Supplier<Object> collectionOrExp) {
        return this.ifInOrNotIn(DualOperator.NOT_IN, collectionOrExp.get(), false);
    }

    @Override
    public final IPredicate ifNotInParam(Function<String, Object> function, String keyName) {
        return this.ifInOrNotIn(DualOperator.NOT_IN, function.apply(keyName), false);
    }


    @Override
    public final IPredicate like(Object pattern) {
        return this.pattern(DualOperator.LIKE, pattern);
    }

    @Override
    public final IPredicate likeNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.LIKE, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C> IPredicate likeExp(Function<C, Expression> function) {
        final Expression exp;
        exp = function.apply(CriteriaContextStack.getCriteria());
        return DualPredicate.create(this, DualOperator.LIKE, exp);
    }

    @Override
    public final IPredicate likeExp(Supplier<Expression> supplier) {
        final Expression exp;
        exp = supplier.get();
        return DualPredicate.create(this, DualOperator.LIKE, exp);
    }

    @Nullable
    @Override
    public final IPredicate ifLike(Supplier<Object> pattern) {
        final Object value;
        value = pattern.get();
        return value == null ? null : this.pattern(DualOperator.LIKE, value);
    }

    @Override
    public final <C> IPredicate ifLike(Function<C, Object> pattern) {
        final Object value;
        value = pattern.apply(CriteriaContextStack.getCriteria());
        return value == null ? null : this.pattern(DualOperator.LIKE, value);
    }

    @Override
    public final IPredicate ifLike(Function<String, Object> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        return value == null ? null : this.pattern(DualOperator.LIKE, value);
    }

    @Override
    public final IPredicate notLike(Object pattern) {
        return pattern(DualOperator.NOT_LIKE, pattern);
    }

    @Override
    public final IPredicate notLikeNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.NOT_LIKE, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C> IPredicate notLikeExp(Function<C, Expression> function) {
        final Expression exp;
        exp = function.apply(CriteriaContextStack.getCriteria());
        return DualPredicate.create(this, DualOperator.NOT_LIKE, exp);
    }

    @Override
    public final IPredicate notLikeExp(Supplier<Expression> supplier) {
        final Expression exp;
        exp = supplier.get();
        return DualPredicate.create(this, DualOperator.NOT_LIKE, exp);
    }

    @Override
    public final IPredicate ifNotLike(Supplier<Object> pattern) {
        final Object value;
        value = pattern.get();
        return value == null ? null : this.pattern(DualOperator.NOT_LIKE, value);
    }

    @Override
    public final <C> IPredicate ifNotLike(Function<C, Object> pattern) {
        final Object value;
        value = pattern.apply(CriteriaContextStack.getCriteria());
        return value == null ? null : this.pattern(DualOperator.NOT_LIKE, value);
    }

    @Override
    public final IPredicate ifNotLike(Function<String, Object> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        return value == null ? null : this.pattern(DualOperator.NOT_LIKE, value);
    }

    @Override
    public final Expression mod(Object operand) {
        return DualExpression.create(this, DualOperator.MOD, SQLs.nonNullParam(this, operand));
    }

    @Override
    public final Expression modLiteral(Object operand) {
        return DualExpression.create(this, DualOperator.MOD, SQLs.nonNullLiteral(this, operand));
    }

    @Override
    public final Expression modNamed(String paramName) {
        return DualExpression.create(this, DualOperator.MOD, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C> Expression modExp(Function<C, Expression> function) {
        return DualExpression.create(this, DualOperator.MOD, function.apply(CriteriaContextStack.getCriteria()));
    }

    @Override
    public final Expression modExp(Supplier<Expression> supplier) {
        return DualExpression.create(this, DualOperator.MOD, supplier.get());
    }

    @Override
    public final Expression multiply(Object multiplicand) {
        return DualExpression.create(this, DualOperator.MULTIPLY, SQLs.nonNullParam(this, multiplicand));
    }

    @Override
    public final Expression multiplyLiteral(Object multiplicand) {
        return DualExpression.create(this, DualOperator.MULTIPLY, SQLs.nonNullLiteral(this, multiplicand));
    }

    @Override
    public final Expression multiplyNamed(String paramName) {
        return DualExpression.create(this, DualOperator.MULTIPLY, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C> Expression multiplyExp(Function<C, Expression> function) {
        return DualExpression.create(this, DualOperator.MULTIPLY, function.apply(CriteriaContextStack.getCriteria()));
    }

    @Override
    public final Expression multiplyExp(Supplier<Expression> supplier) {
        return DualExpression.create(this, DualOperator.MULTIPLY, supplier.get());
    }

    @Override
    public final Expression plus(Object augend) {
        return DualExpression.create(this, DualOperator.PLUS, SQLs.nonNullParam(this, augend));
    }

    @Override
    public final Expression plusLiteral(Object augend) {
        return DualExpression.create(this, DualOperator.PLUS, SQLs.nonNullLiteral(this, augend));
    }

    @Override
    public final Expression plusNamed(String paramName) {
        return DualExpression.create(this, DualOperator.PLUS, SQLs.namedParam(paramName, this.paramMeta()));
    }


    @Override
    public final <C> Expression plusExp(Function<C, Expression> function) {
        return DualExpression.create(this, DualOperator.PLUS, function.apply(CriteriaContextStack.getCriteria()));
    }

    @Override
    public final Expression plusExp(Supplier<Expression> supplier) {
        return DualExpression.create(this, DualOperator.PLUS, supplier.get());
    }

    @Override
    public final Expression minus(Object minuend) {
        return DualExpression.create(this, DualOperator.MINUS, SQLs.nonNullParam(this, minuend));
    }

    @Override
    public final Expression minusLiteral(Object minuend) {
        return DualExpression.create(this, DualOperator.MINUS, SQLs.nonNullLiteral(this, minuend));
    }

    @Override
    public final Expression minusNamed(String paramName) {
        return DualExpression.create(this, DualOperator.MINUS, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C> Expression minusExp(Function<C, Expression> function) {
        return DualExpression.create(this, DualOperator.MINUS, function.apply(CriteriaContextStack.getCriteria()));
    }

    @Override
    public final Expression minusExp(Supplier<Expression> supplier) {
        return DualExpression.create(this, DualOperator.MINUS, supplier.get());
    }

    @Override
    public final Expression divide(Object divisor) {
        return DualExpression.create(this, DualOperator.DIVIDE, SQLs.nonNullParam(this, divisor));
    }

    @Override
    public final Expression divideLiteral(Object divisor) {
        return DualExpression.create(this, DualOperator.DIVIDE, SQLs.nonNullLiteral(this, divisor));
    }

    @Override
    public final Expression divideNamed(String paramName) {
        return DualExpression.create(this, DualOperator.DIVIDE, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C> Expression divideExp(Function<C, Expression> function) {
        return DualExpression.create(this, DualOperator.DIVIDE, function.apply(CriteriaContextStack.getCriteria()));
    }

    @Override
    public final Expression divideExp(Supplier<Expression> supplier) {
        return DualExpression.create(this, DualOperator.DIVIDE, supplier.get());
    }

    @Override
    public final Expression negate() {
        return UnaryExpression.create(this, UnaryOperator.NEGATED);
    }

    @Override
    public final Expression bitwiseAnd(Object operand) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, SQLs.nonNullParam(this, operand));
    }

    @Override
    public final Expression bitwiseAndLiteral(Object operand) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, SQLs.nonNullLiteral(this, operand));
    }

    @Override
    public final Expression bitwiseAndNamed(String paramName) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C> Expression bitwiseAndExp(Function<C, Expression> function) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, function.apply(CriteriaContextStack.getCriteria()));
    }

    @Override
    public final Expression bitwiseAndExp(Supplier<Expression> supplier) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, supplier.get());
    }

    @Override
    public final Expression bitwiseOr(Object operand) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, SQLs.nonNullParam(this, operand));
    }

    @Override
    public final Expression bitwiseOrLiteral(Object operand) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, SQLs.nonNullLiteral(this, operand));
    }

    @Override
    public final Expression bitwiseOrNamed(String paramName) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C> Expression bitwiseOrExp(Function<C, Expression> function) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, function.apply(CriteriaContextStack.getCriteria()));
    }

    @Override
    public final Expression bitwiseOrExp(Supplier<Expression> supplier) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, supplier.get());
    }

    @Override
    public final Expression xor(Object operand) {
        return DualExpression.create(this, DualOperator.XOR, SQLs.nonNullParam(this, operand));
    }

    @Override
    public final Expression xorLiteral(Object operand) {
        return DualExpression.create(this, DualOperator.XOR, SQLs.nonNullLiteral(this, operand));
    }

    @Override
    public final Expression xorNamed(String paramName) {
        return DualExpression.create(this, DualOperator.XOR, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C> Expression xorExp(Function<C, Expression> function) {
        return DualExpression.create(this, DualOperator.XOR, function.apply(CriteriaContextStack.getCriteria()));
    }

    @Override
    public final Expression xorExp(Supplier<Expression> supplier) {
        return DualExpression.create(this, DualOperator.XOR, supplier.get());
    }

    @Override
    public final Expression inversion() {
        return UnaryExpression.create(this, UnaryOperator.INVERT);
    }

    @Override
    public final Expression rightShift(Object bitNumber) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, SQLs.nonNullParam(this, bitNumber));
    }

    @Override
    public final Expression rightShiftLiteral(Object bitNumber) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, SQLs.nonNullLiteral(this, bitNumber));
    }

    @Override
    public final Expression rightShiftNamed(String paramName) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C> Expression rightShiftExp(Function<C, Expression> function) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, function.apply(CriteriaContextStack.getCriteria()));
    }

    @Override
    public final Expression rightShiftExp(Supplier<Expression> supplier) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, supplier.get());
    }

    @Override
    public final Expression leftShift(Object bitNumber) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, SQLs.nonNullParam(this, bitNumber));
    }

    @Override
    public final Expression leftShiftLiteral(Object bitNumber) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, SQLs.nonNullLiteral(this, bitNumber));
    }

    @Override
    public final Expression leftShiftNamed(String paramName) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C> Expression leftShiftExp(Function<C, Expression> function) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, function.apply(CriteriaContextStack.getCriteria()));
    }

    @Override
    public final Expression leftShiftExp(Supplier<Expression> supplier) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, supplier.get());
    }

    @Override
    public final Expression asType(Class<?> convertType) {
        return CastExpression.cast(this, _MappingFactory.getMapping(convertType));
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
        } else if (pattern instanceof String) {
            valueExp = SQLs.nonNullParam(this, pattern);
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
        } else if ((collection = (Collection<?>) value).size() == 0) {
            predicate = null;
        } else if (optimizing) {
            predicate = DualPredicate.create(this, operator, SQLs.optimizingParams(this.paramMeta(), collection));
        } else {
            predicate = DualPredicate.create(this, operator, SQLs.params(this.paramMeta(), collection));
        }
        return predicate;
    }

}
