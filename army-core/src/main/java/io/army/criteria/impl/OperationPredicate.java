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
abstract class OperationPredicate<I extends Item> extends OperationExpression<I> implements _Predicate
        , AliasPredicate<I> {

    OperationPredicate(Function<TypeInfer, I> function) {
        super(function);
    }

    @Override
    public final TypeMeta typeMeta() {
        return BooleanType.INSTANCE;
    }

    @Override
    public final OperationPredicate<I> bracket() {
        return Expressions.bracketPredicate(this);
    }

    @Override
    public final OperationPredicate<I> or(final @Nullable IPredicate predicate) {
        if (predicate == null) {
            throw ContextStack.nullPointer(ContextStack.peek());
        }
        return Expressions.orPredicate(this, predicate);
    }

    @Override
    public final OperationPredicate<I> or(Supplier<IPredicate> supplier) {
        return this.or(supplier.get());
    }

    @Override
    public final OperationPredicate<I> or(Function<Expression, IPredicate> expOperator, Expression operand) {
        return this.or(expOperator.apply(operand));
    }

    @Override
    public final <E extends RightOperand> OperationPredicate<I> or(Function<E, IPredicate> expOperator, Supplier<E> supplier) {
        return this.or(expOperator.apply(supplier.get()));
    }


    @Override
    public final <T> OperationPredicate<I> or(ExpressionOperator<Expression, T, IPredicate> expOperator
            , BiFunction<Expression, T, Expression> operator, Supplier<T> getter) {
        return this.or(expOperator.apply(operator, getter.get()));
    }

    @Override
    public final OperationPredicate<I> or(ExpressionOperator<Expression, Object, IPredicate> expOperator
            , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        return this.or(expOperator.apply(operator, function.apply(keyName)));
    }

    @Override
    public final <T> OperationPredicate<I> or(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator
            , Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter) {
        return this.or(expOperator.apply(operator, firstGetter.get(), and, secondGetter.get()));
    }

    @Override
    public final OperationPredicate<I> or(BetweenValueOperator<Object> expOperator
            , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function
            , String firstKey, SQLs.WordAnd and, String secondKey) {
        return this.or(expOperator.apply(operator, function.apply(firstKey), and, function.apply(secondKey)));
    }

    @Override
    public final OperationPredicate<I> or(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second) {
        return this.or(expOperator.apply(first, and, second));
    }

    @Override
    public final OperationPredicate<I> or(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator
            , String paramName, int size) {
        return this.or(expOperator.apply(namedOperator, paramName, size));
    }

    @Override
    public final OperationPredicate<I> or(Consumer<Consumer<IPredicate>> consumer) {
        final List<IPredicate> list = new ArrayList<>();
        consumer.accept(list::add);
        final OperationPredicate<I> predicate;
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
    public final OperationPredicate<I> ifOr(Supplier<IPredicate> supplier) {
        final OperationPredicate<I> result;
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
        final OperationPredicate<I> predicate;
        if (operand == null) {
            predicate = this;
        } else {
            predicate = this.or(expOperator.apply(operand));
        }
        return predicate;
    }


    @Override
    public final <T> OperationPredicate<I> ifOr(ExpressionOperator<Expression, T, IPredicate> expOperator
            , BiFunction<Expression, T, Expression> operator, Supplier<T> getter) {
        final OperationPredicate<I> predicate;
        final T operand;
        if ((operand = getter.get()) == null) {
            predicate = this;
        } else {
            predicate = this.or(expOperator.apply(operator, operand));
        }
        return predicate;
    }

    @Override
    public final OperationPredicate<I> ifOr(ExpressionOperator<Expression, Object, IPredicate> expOperator
            , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        final OperationPredicate<I> predicate;
        final Object operand;
        if ((operand = function.apply(keyName)) == null) {
            predicate = this;
        } else {
            predicate = this.or(expOperator.apply(operator, operand));
        }
        return predicate;
    }


    @Override
    public final <T> OperationPredicate<I> ifOr(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator
            , Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter) {
        final OperationPredicate<I> predicate;
        final T first, second;
        if ((first = firstGetter.get()) == null || (second = secondGetter.get()) == null) {
            predicate = this;
        } else {
            predicate = this.or(expOperator.apply(operator, first, and, second));
        }
        return predicate;
    }

    @Override
    public final OperationPredicate<I> ifOr(BetweenValueOperator<Object> expOperator
            , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey
            , SQLs.WordAnd and, String secondKey) {
        final OperationPredicate<I> predicate;
        final Object first, second;
        if ((first = function.apply(firstKey)) == null || (second = function.apply(secondKey)) == null) {
            predicate = this;
        } else {
            predicate = this.or(expOperator.apply(operator, first, and, second));
        }
        return predicate;
    }

    @Override
    public final OperationPredicate<I> ifOr(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator
            , String paramName, @Nullable Integer size) {
        final OperationPredicate<I> predicate;
        if (size == null) {
            predicate = this;
        } else {
            predicate = this.or(expOperator.apply(namedOperator, paramName, size));
        }
        return predicate;
    }

    @Override
    public final OperationPredicate<I> ifOr(Consumer<Consumer<IPredicate>> consumer) {
        final List<IPredicate> list = new ArrayList<>();
        consumer.accept(list::add);
        final OperationPredicate<I> predicate;
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
    public final OperationPredicate<I> and(final @Nullable IPredicate predicate) {
        if (predicate == null) {
            throw ContextStack.nullPointer(ContextStack.peek());
        }
        return Expressions.andPredicate(this, predicate);
    }

    @Override
    public final OperationPredicate<I> and(Supplier<IPredicate> supplier) {
        return this.and(supplier.get());
    }

    @Override
    public final OperationPredicate<I> and(UnaryOperator<IPredicate> expOperator, IPredicate operand) {
        return this.and(expOperator.apply(operand));
    }

    @Override
    public final OperationPredicate<I> and(Function<Expression, IPredicate> expOperator, Expression operand) {
        return this.and(expOperator.apply(operand));
    }

    @Override
    public final <E extends RightOperand> OperationPredicate<I> and(Function<E, IPredicate> expOperator, Supplier<E> supplier) {
        return this.and(expOperator.apply(supplier.get()));
    }

    @Override
    public final <T> OperationPredicate<I> and(ExpressionOperator<Expression, T, IPredicate> expOperator
            , BiFunction<Expression, T, Expression> operator, Supplier<T> getter) {
        return this.and(expOperator.apply(operator, getter.get()));
    }

    @Override
    public final OperationPredicate<I> and(ExpressionOperator<Expression, Object, IPredicate> expOperator
            , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        return this.and(expOperator.apply(operator, function.apply(keyName)));
    }


    @Override
    public final <T> OperationPredicate<I> and(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator
            , Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter) {
        return this.and(expOperator.apply(operator, firstGetter.get(), and, secondGetter.get()));
    }

    @Override
    public final OperationPredicate<I> and(UnaryOperator<IPredicate> predicateOperator, BetweenOperator expOperator
            , Expression first, SQLs.WordAnd and, Expression second) {
        return this.and(predicateOperator.apply(expOperator.apply(first, and, second)));
    }

    @Override
    public final OperationPredicate<I> and(BetweenValueOperator<Object> expOperator
            , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKe
            , SQLs.WordAnd and, String secondKey) {
        return this.and(expOperator.apply(operator, function.apply(firstKe), and, function.apply(secondKey)));
    }

    @Override
    public final OperationPredicate<I> and(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second) {
        return this.and(expOperator.apply(first, and, second));
    }

    @Override
    public final OperationPredicate<I> and(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator
            , String paramName, int size) {
        return this.and(expOperator.apply(namedOperator, paramName, size));
    }

    @Override
    public final OperationPredicate<I> and(Function<BiFunction<DataField, String, Expression>, IPredicate> fieldOperator
            , BiFunction<DataField, String, Expression> namedOperator) {
        return this.and(fieldOperator.apply(namedOperator));
    }

    @Override
    public final <T> OperationPredicate<I> and(UnaryOperator<IPredicate> predicateOperator, BetweenValueOperator<T> expOperator
            , BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter
            , SQLs.WordAnd and, Supplier<T> secondGetter) {
        return this.and(predicateOperator.apply(expOperator.apply(operator, firstGetter.get(), and, secondGetter.get())));
    }

    @Override
    public final OperationPredicate<I> and(UnaryOperator<IPredicate> predicateOperator, BetweenValueOperator<Object> expOperator
            , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function
            , String firstKey, SQLs.WordAnd and, String secondKey) {
        return this.and(predicateOperator.apply(expOperator.apply(operator, function.apply(firstKey), and, function.apply(secondKey))));
    }

    @Override
    public final OperationPredicate<I> ifAnd(Supplier<IPredicate> supplier) {
        final OperationPredicate<I> predicate;
        final IPredicate operand;
        if ((operand = supplier.get()) == null) {
            predicate = this;
        } else {
            predicate = Expressions.andPredicate(this, operand);
        }
        return predicate;
    }

    @Override
    public final <E extends RightOperand> OperationPredicate<I> ifAnd(Function<E, IPredicate> expOperator, Supplier<E> supplier) {
        final OperationPredicate<I> predicate;
        final E value;
        if ((value = supplier.get()) == null) {
            predicate = this;
        } else {
            predicate = this.and(expOperator.apply(value));
        }
        return predicate;
    }

    @Override
    public final <T> OperationPredicate<I> ifAnd(ExpressionOperator<Expression, T, IPredicate> expOperator
            , BiFunction<Expression, T, Expression> operator, Supplier<T> getter) {
        final OperationPredicate<I> predicate;
        final T operand;
        if ((operand = getter.get()) == null) {
            predicate = this;
        } else {
            predicate = this.and(expOperator.apply(operator, operand));
        }
        return predicate;
    }

    @Override
    public final OperationPredicate<I> ifAnd(ExpressionOperator<Expression, Object, IPredicate> expOperator
            , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        final OperationPredicate<I> predicate;
        final Object operand;
        if ((operand = function.apply(keyName)) == null) {
            predicate = this;
        } else {
            predicate = this.and(expOperator.apply(operator, operand));
        }
        return predicate;
    }

    @Override
    public final <T> OperationPredicate<I> ifAnd(BetweenValueOperator<T> expOperator
            , BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and
            , Supplier<T> secondGetter) {
        final OperationPredicate<I> predicate;
        final T first, second;
        if ((first = firstGetter.get()) == null || (second = secondGetter.get()) == null) {
            predicate = this;
        } else {
            predicate = this.and(expOperator.apply(operator, first, and, second));
        }
        return predicate;
    }

    @Override
    public final OperationPredicate<I> ifAnd(BetweenValueOperator<Object> expOperator
            , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey
            , SQLs.WordAnd and, String secondKey) {
        final OperationPredicate<I> predicate;
        final Object first, second;
        if ((first = function.apply(firstKey)) == null || (second = function.apply(secondKey)) == null) {
            predicate = this;
        } else {
            predicate = this.and(expOperator.apply(operator, first, and, second));
        }
        return predicate;
    }


    @Override
    public final OperationPredicate<I> ifAnd(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator
            , String paramName, @Nullable Integer size) {
        final OperationPredicate<I> predicate;
        if (size == null) {
            predicate = this;
        } else {
            predicate = this.and(expOperator.apply(namedOperator, paramName, size));
        }
        return predicate;
    }

    @Override
    public final <T> OperationPredicate<I> ifAnd(UnaryOperator<IPredicate> predicateOperator
            , BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator
            , Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter) {
        final OperationPredicate<I> predicate;
        final T first, second;
        if ((first = firstGetter.get()) == null || (second = secondGetter.get()) == null) {
            predicate = this;
        } else {
            predicate = this.and(predicateOperator.apply(expOperator.apply(operator, first, and, second)));
        }
        return predicate;
    }

    @Override
    public final OperationPredicate<I> ifAnd(UnaryOperator<IPredicate> predicateOperator
            , BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator
            , Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey) {
        final OperationPredicate<I> predicate;
        final Object first, second;
        if ((first = function.apply(firstKey)) == null || (second = function.apply(secondKey)) == null) {
            predicate = this;
        } else {
            predicate = this.and(predicateOperator.apply(expOperator.apply(operator, first, and, second)));
        }
        return predicate;
    }

    @Override
    public final OperationPredicate<I> not() {
        return Expressions.notPredicate(this);
    }

    @Override
    public final OperationPredicate<I> ifNot(BooleanSupplier supplier) {
        final OperationPredicate<I> result;
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
        final Expressions.DualPredicate<?> predicate;
        if (!(this instanceof Expressions.DualPredicate)
                || (predicate = (Expressions.DualPredicate<?>) this).operator != DualOperator.EQUAL) {
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
    public final boolean isIdPredicate() {
        final Expressions.DualPredicate<?> predicate;
        final boolean match;
        if (!(this instanceof Expressions.DualPredicate)) {
            match = false;
        } else if (!((predicate = (Expressions.DualPredicate<?>) this).left instanceof PrimaryFieldMeta)) {
            match = false;
        } else if (predicate.operator == DualOperator.EQUAL) {
            match = predicate.right instanceof SqlValueParam.SingleValue
                    && (predicate.right instanceof ParamExpression
                    || predicate.right instanceof LiteralExpression);
        } else if (predicate.operator == DualOperator.IN) {
            match = predicate.right instanceof MultiValueExpression
                    && (predicate.right instanceof ParamExpression
                    || predicate.right instanceof LiteralExpression);
        } else {
            match = false;
        }
        return match;
    }


    @Override
    public final TableField findParentId(final ChildTableMeta<?> child, final String alias) {
        final TableField parentId;
        final Expressions.DualPredicate<?> predicate;
        final TableMeta<?> leftTable, rightTable;
        final TableField leftField, rightField;


        if (!(this instanceof Expressions.DualPredicate) || (predicate = (Expressions.DualPredicate<?>) this).operator != DualOperator.EQUAL) {
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
