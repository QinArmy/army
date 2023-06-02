package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.standard.SQLFunction;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.function.*;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.PrimaryFieldMeta;
import io.army.meta.TableMeta;
import io.army.modelgen._MetaBridge;
import io.army.util._Collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.*;

/**
 * This class is base class of all {@link IPredicate} implementation .
 */
abstract class OperationPredicate extends OperationExpression.PredicateExpression {

    /**
     * <p>
     * Private constructor .
     * </p>
     *
     * @see OperationSimplePredicate#OperationSimplePredicate()
     * @see OperationCompoundPredicate#OperationPredicate()
     */
    private OperationPredicate() {

    }


    @Override
    public final SimplePredicate or(final @Nullable IPredicate predicate) {
        if (predicate == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return orPredicate(this, predicate);
    }

    @Override
    public final SimplePredicate or(Supplier<IPredicate> supplier) {
        return this.or(supplier.get());
    }

    @Override
    public final SimplePredicate or(Function<Expression, IPredicate> expOperator, Expression operand) {
        return this.or(expOperator.apply(operand));
    }

    @Override
    public final <E extends RightOperand> SimplePredicate or(Function<E, IPredicate> expOperator, Supplier<E> supplier) {
        return this.or(expOperator.apply(supplier.get()));
    }

    @Override
    public final <T> SimplePredicate or(ExpressionOperator<SimpleExpression, T, IPredicate> expOperator,
                                        BiFunction<SimpleExpression, T, Expression> operator, T value) {
        return this.or(expOperator.apply(operator, value));
    }


    @Override
    public final SimplePredicate or(BiFunction<TeNamedOperator<SQLField>, Integer, IPredicate> expOperator,
                                    TeNamedOperator<SQLField> namedOperator, int size) {
        return this.or(expOperator.apply(namedOperator, size));
    }

    @Override
    public final SimplePredicate or(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second) {
        return this.or(expOperator.apply(first, and, second));
    }

    @Override
    public final SimplePredicate or(InNamedOperator expOperator, TeNamedOperator<SimpleExpression> namedOperator
            , String paramName, int size) {
        return this.or(expOperator.apply(namedOperator, paramName, size));
    }

    @Override
    public final <T> SimplePredicate or(BetweenValueOperator<T> expOperator,
                                        BiFunction<SimpleExpression, T, Expression> operator, T firstValue,
                                        SQLsSyntax.WordAnd and, T secondValue) {
        return this.or(expOperator.apply(operator, firstValue, and, secondValue));
    }

    @Override
    public final <T, U> SimplePredicate or(BetweenDualOperator<T, U> expOperator,
                                           BiFunction<SimpleExpression, T, Expression> firstFunc, T firstValue,
                                           SqlSyntax.WordAnd and, BiFunction<SimpleExpression, U, Expression> secondFunc,
                                           U secondValue) {
        return this.or(expOperator.apply(firstFunc, firstValue, and, secondFunc, secondValue));
    }

    @Override
    public final SimplePredicate or(Consumer<Consumer<IPredicate>> consumer) {
        final List<IPredicate> list = _Collections.emptyList();
        consumer.accept(list::add);
        final SimplePredicate predicate;
        switch (list.size()) {
            case 0:
                throw CriteriaUtils.dontAddAnyItem();
            case 1:
                predicate = orPredicate(this, list.get(0));
                break;
            default:
                predicate = orPredicate(this, list);
        }
        return predicate;
    }

    @Override
    public final IPredicate ifOr(Supplier<IPredicate> supplier) {
        final IPredicate result;
        final IPredicate predicate;
        predicate = supplier.get();
        if (predicate == null) {
            result = this;
        } else {
            result = this.or(predicate);
        }
        return result;
    }

    @Override
    public final <T> IPredicate ifOr(Function<T, IPredicate> expOperator, Supplier<T> supplier) {
        final T operand;
        operand = supplier.get();
        final IPredicate predicate;
        if (operand == null) {
            predicate = this;
        } else {
            predicate = this.or(expOperator.apply(operand));
        }
        return predicate;
    }

    @Override
    public final IPredicate ifOr(BiFunction<TeNamedOperator<SQLField>, Integer, IPredicate> expOperator,
                                 TeNamedOperator<SQLField> namedOperator, Supplier<Integer> supplier) {
        final IPredicate predicate;
        final Integer size;
        if ((size = supplier.get()) == null) {
            predicate = this;
        } else {
            predicate = this.or(expOperator.apply(namedOperator, size));
        }
        return predicate;
    }

    @Override
    public final <T> IPredicate ifOr(ExpressionOperator<SimpleExpression, T, IPredicate> expOperator,
                                     BiFunction<SimpleExpression, T, Expression> operator, Supplier<T> getter) {
        final IPredicate predicate;
        final T value;
        if ((value = getter.get()) == null) {
            predicate = this;
        } else {
            predicate = this.or(expOperator.apply(operator, value));
        }
        return predicate;
    }

    @Override
    public final <K, V> IPredicate ifOr(ExpressionOperator<SimpleExpression, V, IPredicate> expOperator,
                                        BiFunction<SimpleExpression, V, Expression> operator, Function<K, V> function,
                                        K keyName) {
        final IPredicate predicate;
        final V value;
        if ((value = function.apply(keyName)) == null) {
            predicate = this;
        } else {
            predicate = this.or(expOperator.apply(operator, value));
        }
        return predicate;
    }

    @Override
    public final <T> IPredicate ifOr(BetweenValueOperator<T> expOperator,
                                     BiFunction<SimpleExpression, T, Expression> operator,
                                     Supplier<T> firstGetter, SqlSyntax.WordAnd and, Supplier<T> secondGetter) {
        final IPredicate predicate;
        final T first, second;
        if ((first = firstGetter.get()) == null || (second = secondGetter.get()) == null) {
            predicate = this;
        } else {
            predicate = this.or(expOperator.apply(operator, first, and, second));
        }
        return predicate;
    }

    @Override
    public final <T, U> IPredicate ifOr(BetweenDualOperator<T, U> expOperator,
                                        BiFunction<SimpleExpression, T, Expression> firstFunc, Supplier<T> firstGetter,
                                        SqlSyntax.WordAnd and, BiFunction<SimpleExpression, U, Expression> secondFunc,
                                        Supplier<U> secondGetter) {
        final IPredicate predicate;
        final T first;
        final U second;
        if ((first = firstGetter.get()) == null || (second = secondGetter.get()) == null) {
            predicate = this;
        } else {
            predicate = this.or(expOperator.apply(firstFunc, first, and, secondFunc, second));
        }
        return predicate;
    }

    @Override
    public final IPredicate ifOr(InNamedOperator expOperator, TeNamedOperator<SimpleExpression> namedOperator,
                                 String paramName, Supplier<Integer> supplier) {
        final IPredicate predicate;
        final Integer value;
        if ((value = supplier.get()) == null) {
            predicate = this;
        } else {
            predicate = this.or(expOperator.apply(namedOperator, paramName, value));
        }
        return predicate;
    }

    @Override
    public final IPredicate ifOr(Consumer<Consumer<IPredicate>> consumer) {
        final List<IPredicate> list = new ArrayList<>();
        consumer.accept(list::add);
        final IPredicate predicate;
        switch (list.size()) {
            case 0:
                predicate = this;
                break;
            case 1:
                predicate = orPredicate(this, list.get(0));
                break;
            default:
                predicate = orPredicate(this, list);
        }
        return predicate;
    }

    @Override
    public final IPredicate and(final @Nullable IPredicate predicate) {
        if (predicate == null) {
            throw ContextStack.nullPointer(ContextStack.peek());
        }
        return andPredicate(this, predicate);
    }

    @Override
    public final IPredicate and(Supplier<IPredicate> supplier) {
        return this.and(supplier.get());
    }

    @Override
    public final IPredicate and(UnaryOperator<IPredicate> expOperator, IPredicate operand) {
        return this.and(expOperator.apply(operand));
    }

    @Override
    public final IPredicate and(Function<Expression, IPredicate> expOperator, Expression operand) {
        return this.and(expOperator.apply(operand));
    }

    @Override
    public final IPredicate and(BiFunction<TeNamedOperator<SQLField>, Integer, IPredicate> expOperator,
                                TeNamedOperator<SQLField> namedOperator, int size) {
        return this.and(expOperator.apply(namedOperator, size));
    }


    @Override
    public final IPredicate and(BetweenOperator expOperator, Expression first, SQLs.WordAnd and,
                                Expression second) {
        return this.and(expOperator.apply(first, and, second));
    }

    @Override
    public final IPredicate and(InNamedOperator expOperator, TeNamedOperator<SimpleExpression> namedOperator,
                                String paramName, int size) {
        return this.and(expOperator.apply(namedOperator, paramName, size));
    }

    @Override
    public final IPredicate and(Function<BiFunction<SQLField, String, Expression>, IPredicate> fieldOperator,
                                BiFunction<SQLField, String, Expression> namedOperator) {
        return this.and(fieldOperator.apply(namedOperator));
    }

    @Override
    public final <T> IPredicate and(Function<T, IPredicate> expOperator, Supplier<T> supplier) {
        return this.and(expOperator.apply(supplier.get()));
    }

    @Override
    public final <T> IPredicate and(ExpressionOperator<SimpleExpression, T, IPredicate> expOperator,
                                    BiFunction<SimpleExpression, T, Expression> valueOperator, T value) {
        return this.and(expOperator.apply(valueOperator, value));
    }

    @Override
    public final <T> IPredicate and(DialectBooleanOperator<T> fieldOperator,
                                    BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                                    BiFunction<SimpleExpression, T, Expression> func, @Nullable T value) {
        return this.and(fieldOperator.apply(operator, func, value));
    }

    @Override
    public final <T> IPredicate and(BetweenValueOperator<T> expOperator,
                                    BiFunction<SimpleExpression, T, Expression> operator, T firstValue,
                                    SqlSyntax.WordAnd and, T secondValue) {
        return this.and(expOperator.apply(operator, firstValue, and, secondValue));
    }

    @Override
    public final <T, U> IPredicate and(BetweenDualOperator<T, U> expOperator,
                                       BiFunction<SimpleExpression, T, Expression> firstFuncRef, T first,
                                       SqlSyntax.WordAnd and, BiFunction<SimpleExpression, U, Expression> secondRef,
                                       U second) {
        return this.and(expOperator.apply(firstFuncRef, first, and, secondRef, second));
    }

    @Override
    public final IPredicate ifAnd(Supplier<IPredicate> supplier) {
        final IPredicate predicate;
        final IPredicate operand;
        if ((operand = supplier.get()) == null) {
            predicate = this;
        } else {
            predicate = andPredicate(this, operand);
        }
        return predicate;
    }

    @Override
    public final <E> IPredicate ifAnd(Function<E, IPredicate> expOperator,
                                      Supplier<E> supplier) {
        final IPredicate predicate;
        final E value;
        if ((value = supplier.get()) == null) {
            predicate = this;
        } else {
            predicate = this.and(expOperator.apply(value));
        }
        return predicate;
    }

    @Override
    public final <T> IPredicate ifAnd(ExpressionOperator<SimpleExpression, T, IPredicate> expOperator
            , BiFunction<SimpleExpression, T, Expression> operator, Supplier<T> getter) {
        final IPredicate predicate;
        final T operand;
        if ((operand = getter.get()) == null) {
            predicate = this;
        } else {
            predicate = this.and(expOperator.apply(operator, operand));
        }
        return predicate;
    }

    @Override
    public final <T> IPredicate ifAnd(DialectBooleanOperator<T> fieldOperator,
                                      BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                                      BiFunction<SimpleExpression, T, Expression> func, Supplier<T> getter) {
        final IPredicate predicate;
        final T operand;
        if ((operand = getter.get()) == null) {
            predicate = this;
        } else {
            predicate = this.and(fieldOperator.apply(operator, func, operand));
        }
        return predicate;
    }

    @Override
    public final IPredicate ifAnd(BiFunction<TeNamedOperator<SQLField>, Integer, IPredicate> expOperator,
                                  TeNamedOperator<SQLField> namedOperator, Supplier<Integer> supplier) {
        final IPredicate predicate;
        final Integer size;
        if ((size = supplier.get()) == null) {
            predicate = this;
        } else {
            predicate = this.and(expOperator.apply(namedOperator, size));
        }
        return predicate;
    }

    @Override
    public final <K, V> IPredicate ifAnd(ExpressionOperator<SimpleExpression, V, IPredicate> expOperator
            , BiFunction<SimpleExpression, V, Expression> operator, Function<K, V> function, K keyName) {
        final IPredicate predicate;
        final V operand;
        if ((operand = function.apply(keyName)) == null) {
            predicate = this;
        } else {
            predicate = this.and(expOperator.apply(operator, operand));
        }
        return predicate;
    }

    @Override
    public final <K, V> IPredicate ifAnd(DialectBooleanOperator<V> fieldOperator,
                                         BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                                         BiFunction<SimpleExpression, V, Expression> func, Function<K, V> function,
                                         K key) {
        final IPredicate predicate;
        final V operand;
        if ((operand = function.apply(key)) == null) {
            predicate = this;
        } else {
            predicate = this.and(fieldOperator.apply(operator, func, operand));
        }
        return predicate;
    }

    @Override
    public final <T> IPredicate ifAnd(BetweenValueOperator<T> expOperator,
                                      BiFunction<SimpleExpression, T, Expression> operator,
                                      Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter) {
        final IPredicate predicate;
        final T first, second;
        if ((first = firstGetter.get()) == null || (second = secondGetter.get()) == null) {
            predicate = this;
        } else {
            predicate = this.and(expOperator.apply(operator, first, and, second));
        }
        return predicate;
    }

    @Override
    public final <K, V> IPredicate ifAnd(BetweenValueOperator<V> expOperator,
                                         BiFunction<SimpleExpression, V, Expression> operator,
                                         Function<K, V> function, K firstKey, SQLs.WordAnd and, K secondKey) {
        final IPredicate predicate;
        final V first, second;
        if ((first = function.apply(firstKey)) == null || (second = function.apply(secondKey)) == null) {
            predicate = this;
        } else {
            predicate = this.and(expOperator.apply(operator, first, and, second));
        }
        return predicate;
    }


    @Override
    public final IPredicate ifAnd(InNamedOperator expOperator, TeNamedOperator<SimpleExpression> namedOperator
            , String paramName, Supplier<Integer> supplier) {
        final IPredicate predicate;
        final Integer size;
        if ((size = supplier.get()) == null) {
            predicate = this;
        } else {
            predicate = this.and(expOperator.apply(namedOperator, paramName, size));
        }
        return predicate;
    }

    @Override
    public final <T, U> IPredicate ifAnd(BetweenDualOperator<T, U> expOperator,
                                         BiFunction<SimpleExpression, T, Expression> firstFuncRef,
                                         Supplier<T> firstGetter, SqlSyntax.WordAnd and,
                                         BiFunction<SimpleExpression, U, Expression> secondFuncRef,
                                         Supplier<U> secondGetter) {
        final IPredicate predicate;
        final T first;
        final U second;
        if ((first = firstGetter.get()) == null || (second = secondGetter.get()) == null) {
            predicate = this;
        } else {
            predicate = this.and(expOperator.apply(firstFuncRef, first, and, secondFuncRef, second));
        }
        return predicate;
    }

    @Override
    public final LogicalPredicate blank(BiFunction<IPredicate, IPredicate, LogicalPredicate> funcRef, IPredicate right) {
        final LogicalPredicate predicate;
        predicate = funcRef.apply(this, right);
        if (predicate == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return predicate;
    }

    @Override
    public final LogicalPredicate blank(BiFunction<IPredicate, Consumer<Consumer<IPredicate>>, LogicalPredicate> funcRef,
                                        Consumer<Consumer<IPredicate>> consumer) {
        final LogicalPredicate predicate;
        predicate = funcRef.apply(this, consumer);
        if (predicate == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return predicate;
    }

    @Override
    public final boolean isOptimistic() {
        final boolean match;
        final Expressions.DualPredicate predicate;
        if (!(this instanceof Expressions.DualPredicate)
                || (predicate = (Expressions.DualPredicate) this).operator != DualBooleanOperator.EQUAL) {
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
    public final _Predicate getIdPredicate() {
        OperationPredicate predicate = this;
        while (predicate instanceof AndPredicate) {
            predicate = ((AndPredicate) predicate).left;
        }

        final Expressions.DualPredicate dualPredicate;
        final boolean match;
        if (!(predicate instanceof Expressions.DualPredicate)) {
            match = false;
        } else if (!((dualPredicate = (Expressions.DualPredicate) predicate).left instanceof PrimaryFieldMeta)) {
            match = false;
        } else if (dualPredicate.operator == DualBooleanOperator.EQUAL) {
            match = dualPredicate.right instanceof SqlValueParam.SingleValue
                    && (dualPredicate.right instanceof SingleParamExpression
                    || dualPredicate.right instanceof SingleLiteralExpression);
        } else if (dualPredicate.operator == DualBooleanOperator.IN) {
            match = dualPredicate.right instanceof NonOperationExpression.MultiValueExpression;
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


        if (!(this instanceof Expressions.DualPredicate) || (predicate = (Expressions.DualPredicate) this).operator != DualBooleanOperator.EQUAL) {
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


    static OperationSimplePredicate bracketPredicate(final IPredicate predicate) {
        final OperationSimplePredicate result;
        if (predicate instanceof BracketPredicate) {
            result = (BracketPredicate) predicate;
        } else {
            result = new BracketPredicate((OperationPredicate) predicate);
        }
        return result;
    }

    static OperationSimplePredicate orPredicate(OperationPredicate left, IPredicate right) {
        return new OrPredicate(left, Collections.singletonList((OperationPredicate) right));
    }

    static OperationSimplePredicate orPredicate(OperationPredicate left, List<IPredicate> rightList) {
        final int size = rightList.size();
        assert size > 0;
        final List<OperationPredicate> list = new ArrayList<>(size);
        for (IPredicate right : rightList) {
            list.add((OperationPredicate) right);
        }
        return new OrPredicate(left, Collections.unmodifiableList(list));
    }


    static AndPredicate andPredicate(OperationPredicate left, @Nullable IPredicate right) {
        assert right != null;
        return new AndPredicate(left, (OperationPredicate) right);
    }

    static OperationPredicate notPredicate(final IPredicate predicate) {
        return new NotPredicate((OperationPredicate) predicate);
    }


    /**
     * @see SQLs#TRUE
     * @see SQLs#FALSE
     */
    static SqlSyntax.WordBooleans booleanWord(final boolean value) {
        return value ? BooleanWord.TRUE : BooleanWord.FALSE;
    }

    /**
     * <p>
     * Private class.This class is base class of below:
     * <li>{@link BooleanWord}</li>
     * <li>{@link BracketPredicate}</li>
     * <li>{@link SqlFunctionPredicate}</li>
     * <li>{@link OrPredicate},because OR/XOR operator always have outer parentheses。</li>
     * </p>
     */
    static abstract class OperationSimplePredicate extends OperationPredicate
            implements SimplePredicate, ArmySimpleExpression {

        /**
         * <p>
         * <strong>Private constructor</strong>
         * </p>
         */
        private OperationSimplePredicate() {
        }


    }//OperationSimplePredicate


    static abstract class SqlFunctionPredicate extends OperationSimplePredicate implements SQLFunction {

        final String name;

        SqlFunctionPredicate(String name) {
            this.name = name;
        }

        @Override
        public final String name() {
            return this.name;
        }


    }//SqlFunctionPredicate


    static abstract class OperationCompoundPredicate extends OperationPredicate implements CompoundPredicate {

        OperationCompoundPredicate() {
        }


    }// CompoundPredicate


    private static final class BracketPredicate extends OperationSimplePredicate {

        private final OperationPredicate predicate;

        private BracketPredicate(OperationPredicate predicate) {
            this.predicate = predicate;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE_LEFT_PAREN);

            this.predicate.appendSql(context);

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

        }


    }//BracketPredicate

    private static final class OrPredicate extends OperationSimplePredicate {

        private final OperationPredicate left;

        private final List<OperationPredicate> rightList;

        private OrPredicate(OperationPredicate left, List<OperationPredicate> rightList) {
            this.left = left;
            this.rightList = rightList;
        }


        @Override
        public void appendSql(final _SqlContext context) {
            this.appendOrPredicate(context.sqlBuilder(), context);
        }


        @Override
        public int hashCode() {
            return Objects.hash(this.left, this.rightList);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof OrPredicate) {
                final OrPredicate o = (OrPredicate) obj;
                match = o.left.equals(this.left)
                        && o.rightList.equals(this.rightList);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            final StringBuilder builder;
            builder = new StringBuilder();
            this.appendOrPredicate(builder, null);
            return builder.toString();
        }

        private void appendOrPredicate(final StringBuilder builder, final @Nullable _SqlContext context) {
            builder.append(_Constant.SPACE_LEFT_PAREN);// outer left paren

            final OperationPredicate left = this.left;

            if (context == null) {
                builder.append(left);
            } else {
                left.appendSql(context);
            }

            boolean rightInnerParen;
            for (OperationPredicate right : this.rightList) {

                builder.append(_Constant.SPACE_OR);
                rightInnerParen = right instanceof AndPredicate;
                if (rightInnerParen) {
                    builder.append(_Constant.SPACE_LEFT_PAREN); // inner left bracket
                }

                if (context == null) {
                    builder.append(right);
                } else {
                    right.appendSql(context);
                }

                if (rightInnerParen) {
                    builder.append(_Constant.SPACE_RIGHT_PAREN);// inner right bracket
                }
            }

            builder.append(_Constant.SPACE_RIGHT_PAREN); // outer right paren
        }


    }//OrPredicate

    private static final class AndPredicate extends OperationCompoundPredicate {

        final OperationPredicate left;

        final OperationPredicate right;

        private AndPredicate(OperationPredicate left, OperationPredicate right) {
            this.left = left;
            this.right = right;
        }


        @Override
        public void appendSql(final _SqlContext context) {
            // 1. append left operand
            this.left.appendSql(context);

            final StringBuilder sqlBuilder;
            // 2. append AND operator
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE_AND);

            final OperationPredicate right = this.right;
            final boolean rightOuterParens = right instanceof AndPredicate;

            if (rightOuterParens) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            }
            // 3. append right operand
            right.appendSql(context);
            if (rightOuterParens) {
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }

        }


        @Override
        public String toString() {
            final StringBuilder sqlBuilder = new StringBuilder();

            // 1. append left operand
            sqlBuilder.append(this.left);
            // 2. append AND operator
            sqlBuilder.append(_Constant.SPACE_AND);

            final OperationPredicate right = this.right;
            final boolean rightOuterParens = right instanceof AndPredicate;

            if (rightOuterParens) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            }
            // 3. append right operand
            sqlBuilder.append(right);
            if (rightOuterParens) {
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }
            return sqlBuilder.toString();
        }


    }//AndPredicate

    private static final class NotPredicate extends OperationCompoundPredicate {

        private final OperationPredicate predicate;

        /**
         * @see #notPredicate(IPredicate)
         */
        private NotPredicate(OperationPredicate predicate) {
            this.predicate = predicate;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder builder;
            builder = context.sqlBuilder()
                    .append(" NOT");

            final OperationPredicate predicate = this.predicate;
            final boolean operandOuterParens = predicate instanceof AndPredicate;

            if (operandOuterParens) {
                builder.append(_Constant.SPACE_LEFT_PAREN);
            }
            predicate.appendSql(context);

            if (operandOuterParens) {
                builder.append(_Constant.SPACE_RIGHT_PAREN);
            }

        }

        @Override
        public int hashCode() {
            return this.predicate.hashCode();
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof NotPredicate) {
                match = ((NotPredicate) obj).predicate.equals(this.predicate);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            final StringBuilder builder;
            builder = new StringBuilder()
                    .append(" NOT");

            final OperationPredicate predicate = this.predicate;
            final boolean operandOuterParens = predicate instanceof AndPredicate;

            if (operandOuterParens) {
                builder.append(_Constant.SPACE_LEFT_PAREN);
            }
            builder.append(this.predicate);

            if (operandOuterParens) {
                builder.append(_Constant.SPACE_RIGHT_PAREN);
            }
            return builder.toString();
        }


    }//NotPredicate


    /**
     * @see SQLs#TRUE
     * @see SQLs#FALSE
     */
    private static final class BooleanWord extends OperationSimplePredicate
            implements SqlSyntax.WordBooleans, SqlSyntax.ArmyKeyWord {

        private static final BooleanWord TRUE = new BooleanWord(true);

        private static final BooleanWord FALSE = new BooleanWord(false);

        private final String spaceWord;

        private BooleanWord(boolean value) {
            this.spaceWord = value ? " TRUE" : " FALSE";
        }

        @Override
        public String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            context.sqlBuilder().append(this.spaceWord);
        }

        @Override
        public String toString() {
            return this.spaceWord;
        }


    }//BooleanWord


}
