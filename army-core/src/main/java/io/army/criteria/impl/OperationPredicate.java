package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Predicate;
import io.army.function.TePredicate;
import io.army.lang.Nullable;
import io.army.mapping.BooleanType;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.TypeMeta;
import io.army.modelgen._MetaBridge;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class is base class of all {@link IPredicate} implementation .
 */
abstract class OperationPredicate extends OperationExpression implements _Predicate {

    @Override
    public final TypeMeta typeMeta() {
        return BooleanType.INSTANCE;
    }

    @Override
    public final IPredicate or(final @Nullable IPredicate predicate) {
        if (predicate == null) {
            throw CriteriaContextStack.nullPointer(CriteriaContextStack.peek());
        }
        return OrPredicate.create(this, predicate);
    }

    @Override
    public final IPredicate or(Supplier<IPredicate> supplier) {
        return this.or(supplier.get());
    }

    @Override
    public final <C> IPredicate or(Function<C, IPredicate> function) {
        return this.or(function.apply(CriteriaContextStack.getTopCriteria()));
    }

    @Override
    public final IPredicate or(Function<Expression, IPredicate> expOperator, Supplier<Expression> supplier) {
        return this.or(expOperator.apply(supplier.get()));
    }

    @Override
    public final <C> IPredicate or(Function<Expression, IPredicate> expOperator, Function<C, Expression> function) {
        return this.or(expOperator.apply(function.apply(CriteriaContextStack.getTopCriteria())));
    }

    @Override
    public final <T> IPredicate or(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, T operand) {
        return this.or(expOperator.apply(operator, operand));
    }

    @Override
    public final <T> IPredicate or(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> supplier) {
        return this.or(expOperator.apply(operator, supplier.get()));
    }

    @Override
    public final IPredicate or(BiFunction<BiFunction<Expression, Object, Expression>, Object, IPredicate> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        return this.or(expOperator.apply(operator, function.apply(keyName)));
    }

    @Override
    public final <T> IPredicate or(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator, BiFunction<Expression, T, Expression> operator, @Nullable T first, @Nullable T second) {
        if (first == null || second == null) {
            throw CriteriaContextStack.nullPointer(CriteriaContextStack.peek());
        }
        return this.or(expOperator.apply(operator, first, second));
    }

    @Override
    public final <T> IPredicate or(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstSupplier, Supplier<T> secondSupplier) {
        final T first, second;
        if ((first = firstSupplier.get()) == null || (second = secondSupplier.get()) == null) {
            throw CriteriaContextStack.nullPointer(CriteriaContextStack.peek());
        }
        return this.or(expOperator.apply(operator, first, second));
    }

    @Override
    public IPredicate or(TePredicate<BiFunction<Expression, Object, Expression>, Object, Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, String secondKey) {
        final Object first, second;
        if ((first = function.apply(firstKey)) == null || (second = function.apply(secondKey)) == null) {
            throw CriteriaContextStack.nullPointer(CriteriaContextStack.peek());
        }
        return this.or(expOperator.apply(operator, first, second));
    }

    @Override
    public final IPredicate or(Consumer<Consumer<IPredicate>> consumer) {
        final List<IPredicate> list = new ArrayList<>();
        consumer.accept(list::add);
        if (list.size() == 0) {
            String m = String.format("You don't add any %s", IPredicate.class.getName());
            throw CriteriaContextStack.criteriaError(CriteriaContextStack.peek(), m);
        }
        return OrPredicate.create(this, list);
    }

    @Override
    public final IPredicate ifOr(Supplier<IPredicate> supplier) {
        final IPredicate predicate, result;
        predicate = supplier.get();
        if (predicate == null) {
            result = this;
        } else {
            result = OrPredicate.create(this, predicate);
        }
        return result;
    }

    @Override
    public final <C> IPredicate ifOr(Function<C, IPredicate> function) {
        final IPredicate predicate, result;
        predicate = function.apply(CriteriaContextStack.getTopCriteria());
        if (predicate == null) {
            result = this;
        } else {
            result = OrPredicate.create(this, predicate);
        }
        return result;
    }

    @Override
    public final IPredicate ifOr(Function<Expression, IPredicate> expOperator, Supplier<Expression> supplier) {
        final Expression expression;
        expression = supplier.get();
        final IPredicate result;
        if (expression == null) {
            result = this;
        } else {
            result = this.or(expOperator.apply(expression));
        }
        return result;
    }

    @Override
    public <C> IPredicate ifOr(Function<Expression, IPredicate> expOperator, Function<C, Expression> function) {
        final Expression expression;
        expression = function.apply(CriteriaContextStack.getTopCriteria());
        final IPredicate result;
        if (expression == null) {
            result = this;
        } else {
            result = this.or(expOperator.apply(expression));
        }
        return result;
    }

    @Override
    public final <T> IPredicate ifOr(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, @Nullable T operand) {
        final IPredicate result;
        if (operand == null) {
            result = this;
        } else {
            result = this.or(expOperator.apply(operator, operand));
        }
        return result;
    }

    @Override
    public final <T> IPredicate ifOr(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> supplier) {
        final T operand;
        operand = supplier.get();
        final IPredicate result;
        if (operand == null) {
            result = this;
        } else {
            result = this.or(expOperator.apply(operator, operand));
        }
        return result;
    }

    @Override
    public IPredicate ifOr(BiFunction<BiFunction<Expression, Object, Expression>, Object, IPredicate> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        final Object operand;
        operand = function.apply(keyName);
        final IPredicate result;
        if (operand == null) {
            result = this;
        } else {
            result = this.or(expOperator.apply(operator, operand));
        }
        return result;
    }

    @Override
    public final <T> IPredicate ifOr(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator, BiFunction<Expression, T, Expression> operator, @Nullable T first, @Nullable T second) {
        final IPredicate result;
        if (first == null || second == null) {
            result = this;
        } else {
            result = this.or(expOperator.apply(operator, first, second));
        }
        return result;
    }

    @Override
    public final <T> IPredicate ifOr(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstSupplier, Supplier<T> secondSupplier) {
        final T first, second;
        final IPredicate result;
        if ((first = firstSupplier.get()) == null || (second = secondSupplier.get()) == null) {
            result = this;
        } else {
            result = this.or(expOperator.apply(operator, first, second));
        }
        return result;
    }

    @Override
    public final IPredicate ifOr(TePredicate<BiFunction<Expression, Object, Expression>, Object, Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, String secondKey) {
        final Object first, second;
        final IPredicate result;
        if ((first = function.apply(firstKey)) == null || (second = function.apply(secondKey)) == null) {
            result = this;
        } else {
            result = this.or(expOperator.apply(operator, first, second));
        }
        return result;
    }

    @Override
    public final IPredicate ifOr(Consumer<Consumer<IPredicate>> consumer) {
        final List<IPredicate> list = new ArrayList<>();
        consumer.accept(list::add);
        final IPredicate result;
        if (list.size() == 0) {
            result = this;
        } else {
            result = OrPredicate.create(this, list);
        }
        return result;
    }

    @Override
    public final IPredicate not() {
        return NotPredicate.not(this);
    }

    @Override
    public final boolean isOptimistic() {
        final boolean match;
        final DualPredicate predicate;
        if (!(this instanceof DualPredicate) || (predicate = (DualPredicate) this).operator != DualOperator.EQUAL) {
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
    public final TableField findParentId(final ChildTableMeta<?> child, final String alias) {
        final TableField parentId;
        final DualPredicate predicate;
        final TableMeta<?> leftTable, rightTable;
        final TableField leftField, rightField;


        if (!(this instanceof DualPredicate) || (predicate = (DualPredicate) this).operator != DualOperator.EQUAL) {
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
