package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Predicate;
import io.army.function.*;
import io.army.lang.Nullable;
import io.army.mapping.BooleanType;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.function.*;

/**
 * This class is base class of all {@link IPredicate} implementation .
 */
abstract class OperationPredicate extends OperationExpression implements _Predicate {


    @Override
    public final TypeMeta typeMeta() {
        return BooleanType.INSTANCE;
    }

    @Override
    public final OperationPredicate bracket() {
        return Expressions.bracketPredicate(this);
    }

    @Override
    public final OperationPredicate or(final @Nullable IPredicate predicate) {
        if (predicate == null) {
            throw ContextStack.nullPointer(ContextStack.peek());
        }
        return Expressions.orPredicate(this, predicate);
    }

    @Override
    public final OperationPredicate or(Supplier<IPredicate> supplier) {
        return this.or(supplier.get());
    }

    @Override
    public final OperationPredicate or(Function<Expression, IPredicate> expOperator, Expression operand) {
        return this.or(expOperator.apply(operand));
    }

    @Override
    public final <E extends RightOperand> OperationPredicate or(Function<E, IPredicate> expOperator, Supplier<E> supplier) {
        return this.or(expOperator.apply(supplier.get()));
    }

    @Override
    public final OperationPredicate or(ExpressionOperator<Expression, Expression, IPredicate> expOperator,
                                       BiFunction<Expression, Expression, Expression> operator, Expression expression) {
        return this.or(expOperator.apply(operator, expression));
    }

    @Override
    public final OperationPredicate or(ExpressionOperator<Expression, Object, IPredicate> expOperator,
                                       BiFunction<Expression, Object, Expression> operator, Object value) {
        return this.or(expOperator.apply(operator, value));
    }

    @Override
    public final <T> OperationPredicate or(ExpressionOperator<Expression, T, IPredicate> expOperator
            , BiFunction<Expression, T, Expression> operator, Supplier<T> getter) {
        return this.or(expOperator.apply(operator, getter.get()));
    }

    @Override
    public final OperationPredicate or(BiFunction<TeNamedOperator<DataField>, Integer, IPredicate> expOperator,
                                       TeNamedOperator<DataField> namedOperator, int size) {
        return this.or(expOperator.apply(namedOperator, size));
    }

    @Override
    public final OperationPredicate or(ExpressionOperator<Expression, Object, IPredicate> expOperator
            , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        return this.or(expOperator.apply(operator, function.apply(keyName)));
    }

    @Override
    public final <T> OperationPredicate or(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator
            , Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter) {
        return this.or(expOperator.apply(operator, firstGetter.get(), and, secondGetter.get()));
    }

    @Override
    public final OperationPredicate or(BetweenValueOperator<Object> expOperator
            , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function
            , String firstKey, SQLs.WordAnd and, String secondKey) {
        return this.or(expOperator.apply(operator, function.apply(firstKey), and, function.apply(secondKey)));
    }

    @Override
    public final OperationPredicate or(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second) {
        return this.or(expOperator.apply(first, and, second));
    }

    @Override
    public final OperationPredicate or(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator
            , String paramName, int size) {
        return this.or(expOperator.apply(namedOperator, paramName, size));
    }

    @Override
    public final OperationPredicate or(BetweenValueOperator<Object> expOperator,
                                       BiFunction<Expression, Object, Expression> operator, Object firstValue,
                                       SQLsSyntax.WordAnd and, Object secondValue) {
        return this.or(expOperator.apply(operator, firstValue, and, secondValue));
    }


    @Override
    public final OperationPredicate or(Consumer<Consumer<IPredicate>> consumer) {
        final List<IPredicate> list = new ArrayList<>();
        consumer.accept(list::add);
        final OperationPredicate predicate;
        switch (list.size()) {
            case 0:
                throw ContextStack.criteriaError(ContextStack.peek(), "You don't add any predicate");
            case 1:
                predicate = Expressions.orPredicate(this, list.get(0));
                break;
            default:
                predicate = Expressions.orPredicate(this, list);
        }
        return predicate;
    }

    @Override
    public final OperationPredicate ifOr(Supplier<IPredicate> supplier) {
        final OperationPredicate result;
        final IPredicate predicate;
        predicate = supplier.get();
        if (predicate == null) {
            result = this;
        } else {
            result = Expressions.orPredicate(this, predicate);
        }
        return result;
    }

    @Override
    public final <E> OperationPredicate ifOr(Function<E, IPredicate> expOperator, Supplier<E> supplier) {
        final E operand;
        operand = supplier.get();
        final OperationPredicate predicate;
        if (operand == null) {
            predicate = this;
        } else {
            predicate = this.or(expOperator.apply(operand));
        }
        return predicate;
    }


    @Override
    public final <T> OperationPredicate ifOr(ExpressionOperator<Expression, T, IPredicate> expOperator,
                                             BiFunction<Expression, T, Expression> operator, Supplier<T> getter) {
        final OperationPredicate predicate;
        final T operand;
        if ((operand = getter.get()) == null) {
            predicate = this;
        } else {
            predicate = this.or(expOperator.apply(operator, operand));
        }
        return predicate;
    }

    @Override
    public final OperationPredicate ifOr(BiFunction<TeNamedOperator<DataField>, Integer, IPredicate> expOperator,
                                         TeNamedOperator<DataField> namedOperator, Supplier<Integer> supplier) {
        final OperationPredicate predicate;
        final Integer size;
        if ((size = supplier.get()) == null) {
            predicate = this;
        } else {
            predicate = this.or(expOperator.apply(namedOperator, size));
        }
        return predicate;
    }

    @Override
    public final OperationPredicate ifOr(ExpressionOperator<Expression, Object, IPredicate> expOperator
            , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        final OperationPredicate predicate;
        final Object operand;
        if ((operand = function.apply(keyName)) == null) {
            predicate = this;
        } else {
            predicate = this.or(expOperator.apply(operator, operand));
        }
        return predicate;
    }


    @Override
    public final <T> OperationPredicate ifOr(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator
            , Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter) {
        final OperationPredicate predicate;
        final T first, second;
        if ((first = firstGetter.get()) == null || (second = secondGetter.get()) == null) {
            predicate = this;
        } else {
            predicate = this.or(expOperator.apply(operator, first, and, second));
        }
        return predicate;
    }

    @Override
    public final OperationPredicate ifOr(BetweenValueOperator<Object> expOperator
            , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey
            , SQLs.WordAnd and, String secondKey) {
        final OperationPredicate predicate;
        final Object first, second;
        if ((first = function.apply(firstKey)) == null || (second = function.apply(secondKey)) == null) {
            predicate = this;
        } else {
            predicate = this.or(expOperator.apply(operator, first, and, second));
        }
        return predicate;
    }

    @Override
    public final OperationPredicate ifOr(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator
            , String paramName, Supplier<Integer> supplier) {
        final OperationPredicate predicate;
        final Integer size;
        if ((size = supplier.get()) == null) {
            predicate = this;
        } else {
            predicate = this.or(expOperator.apply(namedOperator, paramName, size));
        }
        return predicate;
    }

    @Override
    public final OperationPredicate ifOr(Consumer<Consumer<IPredicate>> consumer) {
        final List<IPredicate> list = new ArrayList<>();
        consumer.accept(list::add);
        final OperationPredicate predicate;
        switch (list.size()) {
            case 0:
                predicate = this;
                break;
            case 1:
                predicate = Expressions.orPredicate(this, list.get(0));
                break;
            default:
                predicate = Expressions.orPredicate(this, list);
        }
        return predicate;
    }

    @Override
    public final OperationPredicate and(final @Nullable IPredicate predicate) {
        if (predicate == null) {
            throw ContextStack.nullPointer(ContextStack.peek());
        }
        return Expressions.andPredicate(this, predicate);
    }

    @Override
    public final OperationPredicate and(Supplier<IPredicate> supplier) {
        return this.and(supplier.get());
    }

    @Override
    public final OperationPredicate and(UnaryOperator<IPredicate> expOperator, IPredicate operand) {
        return this.and(expOperator.apply(operand));
    }

    @Override
    public final OperationPredicate and(Function<Expression, IPredicate> expOperator, Expression operand) {
        return this.and(expOperator.apply(operand));
    }

    @Override
    public final <E extends RightOperand> OperationPredicate and(Function<E, IPredicate> expOperator,
                                                                 Supplier<E> supplier) {
        return this.and(expOperator.apply(supplier.get()));
    }

    @Override
    public final OperationPredicate and(ExpressionOperator<Expression, Expression, IPredicate> expOperator,
                                        BiFunction<Expression, Expression, Expression> valueOperator, Expression expression) {
        return this.and(expOperator.apply(valueOperator, expression));
    }

    @Override
    public final OperationPredicate and(ExpressionOperator<Expression, Object, IPredicate> expOperator,
                                        BiFunction<Expression, Object, Expression> valueOperator, Object value) {
        return this.and(expOperator.apply(valueOperator, value));
    }

    @Override
    public final <T> OperationPredicate and(ExpressionOperator<Expression, T, IPredicate> expOperator
            , BiFunction<Expression, T, Expression> operator, Supplier<T> getter) {
        return this.and(expOperator.apply(operator, getter.get()));
    }

    @Override
    public final OperationPredicate and(ExpressionOperator<Expression, Object, IPredicate> expOperator
            , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        return this.and(expOperator.apply(operator, function.apply(keyName)));
    }

    @Override
    public final OperationPredicate and(BiFunction<TeNamedOperator<DataField>, Integer, IPredicate> expOperator,
                                        TeNamedOperator<DataField> namedOperator, int size) {
        return this.and(expOperator.apply(namedOperator, size));
    }

    @Override
    public final OperationPredicate and(BetweenValueOperator<Object> expOperator,
                                        BiFunction<Expression, Object, Expression> operator, Object firstValue,
                                        SQLsSyntax.WordAnd and, Object secondValue) {
        return this.and(expOperator.apply(operator, firstValue, and, secondValue));
    }

    @Override
    public final <T> OperationPredicate and(BetweenValueOperator<T> expOperator,
                                            BiFunction<Expression, T, Expression> operator,
                                            Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter) {
        return this.and(expOperator.apply(operator, firstGetter.get(), and, secondGetter.get()));
    }

    @Override
    public final OperationPredicate and(BetweenValueOperator<Object> expOperator,
                                        BiFunction<Expression, Object, Expression> operator,
                                        Function<String, ?> function, String firstKe,
                                        SQLs.WordAnd and, String secondKey) {
        return this.and(expOperator.apply(operator, function.apply(firstKe), and, function.apply(secondKey)));
    }

    @Override
    public final OperationPredicate and(BetweenOperator expOperator, Expression first, SQLs.WordAnd and,
                                        Expression second) {
        return this.and(expOperator.apply(first, and, second));
    }

    @Override
    public final OperationPredicate and(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator,
                                        String paramName, int size) {
        return this.and(expOperator.apply(namedOperator, paramName, size));
    }

    @Override
    public final OperationPredicate and(Function<BiFunction<DataField, String, Expression>, IPredicate> fieldOperator,
                                        BiFunction<DataField, String, Expression> namedOperator) {
        return this.and(fieldOperator.apply(namedOperator));
    }

    @Override
    public final OperationPredicate ifAnd(Supplier<IPredicate> supplier) {
        final OperationPredicate predicate;
        final IPredicate operand;
        if ((operand = supplier.get()) == null) {
            predicate = this;
        } else {
            predicate = Expressions.andPredicate(this, operand);
        }
        return predicate;
    }

    @Override
    public final <E extends RightOperand> OperationPredicate ifAnd(Function<E, IPredicate> expOperator,
                                                                   Supplier<E> supplier) {
        final OperationPredicate predicate;
        final E value;
        if ((value = supplier.get()) == null) {
            predicate = this;
        } else {
            predicate = this.and(expOperator.apply(value));
        }
        return predicate;
    }

    @Override
    public final <T> OperationPredicate ifAnd(ExpressionOperator<Expression, T, IPredicate> expOperator
            , BiFunction<Expression, T, Expression> operator, Supplier<T> getter) {
        final OperationPredicate predicate;
        final T operand;
        if ((operand = getter.get()) == null) {
            predicate = this;
        } else {
            predicate = this.and(expOperator.apply(operator, operand));
        }
        return predicate;
    }

    @Override
    public final OperationPredicate ifAnd(BiFunction<TeNamedOperator<DataField>, Integer, IPredicate> expOperator,
                                          TeNamedOperator<DataField> namedOperator, Supplier<Integer> supplier) {
        final OperationPredicate predicate;
        final Integer size;
        if ((size = supplier.get()) == null) {
            predicate = this;
        } else {
            predicate = this.and(expOperator.apply(namedOperator, size));
        }
        return predicate;
    }

    @Override
    public final OperationPredicate ifAnd(ExpressionOperator<Expression, Object, IPredicate> expOperator
            , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        final OperationPredicate predicate;
        final Object operand;
        if ((operand = function.apply(keyName)) == null) {
            predicate = this;
        } else {
            predicate = this.and(expOperator.apply(operator, operand));
        }
        return predicate;
    }

    @Override
    public final <T> OperationPredicate ifAnd(BetweenValueOperator<T> expOperator
            , BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and
            , Supplier<T> secondGetter) {
        final OperationPredicate predicate;
        final T first, second;
        if ((first = firstGetter.get()) == null || (second = secondGetter.get()) == null) {
            predicate = this;
        } else {
            predicate = this.and(expOperator.apply(operator, first, and, second));
        }
        return predicate;
    }

    @Override
    public final OperationPredicate ifAnd(BetweenValueOperator<Object> expOperator
            , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey
            , SQLs.WordAnd and, String secondKey) {
        final OperationPredicate predicate;
        final Object first, second;
        if ((first = function.apply(firstKey)) == null || (second = function.apply(secondKey)) == null) {
            predicate = this;
        } else {
            predicate = this.and(expOperator.apply(operator, first, and, second));
        }
        return predicate;
    }


    @Override
    public final OperationPredicate ifAnd(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator
            , String paramName, Supplier<Integer> supplier) {
        final OperationPredicate predicate;
        final Integer size;
        if ((size = supplier.get()) == null) {
            predicate = this;
        } else {
            predicate = this.and(expOperator.apply(namedOperator, paramName, size));
        }
        return predicate;
    }

    @Override
    public final OperationPredicate not() {
        return Expressions.notPredicate(this);
    }

    @Override
    public final OperationPredicate ifNot(BooleanSupplier supplier) {
        final OperationPredicate result;
        if (supplier.getAsBoolean()) {
            result = Expressions.notPredicate(this);
        } else {
            result = this;
        }
        return result;
    }

    @Override
    public final boolean isOptimistic() {
        final boolean match;
        final Expressions.DualPredicate predicate;
        if (!(this instanceof Expressions.DualPredicate)
                || (predicate = (Expressions.DualPredicate) this).operator != DualOperator.EQUAL) {
            match = false;
        } else if (predicate.left instanceof TableField
                && _MetaBridge.VERSION.equals(((TableField) predicate.left).fieldName())) {
            match = predicate.right instanceof SqlValueParam.SingleValue
                    || predicate.right instanceof NamedParam;
        } else if (predicate.right instanceof TableField
                && _MetaBridge.VERSION.equals(((TableField) predicate.right).fieldName())) {
            match = predicate.left instanceof SqlValueParam.SingleValue
                    || predicate.left instanceof NamedParam;

        } else {
            match = false;
        }
        return match;
    }

    @Override
    public final OperationPredicate getIdPredicate() {
        OperationPredicate predicate = this;
        while (predicate instanceof Expressions.AndPredicate) {
            predicate = ((Expressions.AndPredicate) predicate).left;
        }

        final Expressions.DualPredicate dualPredicate;
        final boolean match;
        if (!(predicate instanceof Expressions.DualPredicate)) {
            match = false;
        } else if (!((dualPredicate = (Expressions.DualPredicate) predicate).left instanceof PrimaryFieldMeta)) {
            match = false;
        } else if (dualPredicate.operator == DualOperator.EQUAL) {
            match = dualPredicate.right instanceof SqlValueParam.SingleValue
                    && (dualPredicate.right instanceof ParamExpression
                    || dualPredicate.right instanceof LiteralExpression);
        } else if (dualPredicate.operator == DualOperator.IN) {
            match = dualPredicate.right instanceof MultiValueExpression
                    && (dualPredicate.right instanceof ParamExpression
                    || dualPredicate.right instanceof LiteralExpression);
        } else {
            match = false;
        }
        return match ? predicate : null;
    }


    @Override
    public final TableField findParentId(final ChildTableMeta<?> child, final String alias) {
        final TableField parentId;
        final Expressions.DualPredicate predicate;
        final TableMeta<?> leftTable, rightTable;
        final TableField leftField, rightField;


        if (!(this instanceof Expressions.DualPredicate) || (predicate = (Expressions.DualPredicate) this).operator != DualOperator.EQUAL) {
            parentId = null;
        } else if (!(predicate.left instanceof TableField && predicate.right instanceof TableField)) {
            parentId = null;
        } else if (!((leftField = (TableField) predicate.left).fieldName().equals(_MetaBridge.ID)
                && (rightField = (TableField) predicate.right).fieldName().equals(_MetaBridge.ID))) {
            parentId = null;
        } else if ((leftTable = leftField.tableMeta()) != child & (rightTable = rightField.tableMeta()) != child) {
            parentId = null;
        } else if ((leftTable == child && rightTable == child.parentMeta())) {
            if (leftField instanceof FieldMeta || ((QualifiedField<?>) leftField).tableAlias().equals(alias)) {
                parentId = rightField;
            } else {
                parentId = null;
            }
        } else if (rightTable == child && leftTable == child.parentMeta()) {
            if (rightField instanceof FieldMeta || ((QualifiedField<?>) rightField).tableAlias().equals(alias)) {
                parentId = leftField;
            } else {
                parentId = null;
            }
        } else {
            parentId = null;
        }
        return parentId;
    }

    /*################################## blow private method ##################################*/


}
