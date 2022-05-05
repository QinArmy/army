package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Dml;
import io.army.criteria.impl.inner._Predicate;
import io.army.dialect.Dialect;
import io.army.dialect._MockDialects;
import io.army.lang.Nullable;
import io.army.stmt.BatchStmt;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmt;
import io.army.util._Assert;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class is base class of all dml statement.
 * </p>
 *
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class DmlWhereClause<C, FT, FS, FP, JT, JS, JP, WR, WA>
        extends JoinableClause<C, FT, FS, FP, JT, JS, JP>
        implements Statement, Statement._WhereClause<C, WR, WA>, Statement._WhereAndClause<C, WA>, _Dml {


    private List<_Predicate> predicateList = new ArrayList<>();

    DmlWhereClause(ClauseSupplier clauseSupplier, @Nullable C criteria) {
        super(clauseSupplier, criteria);
    }

    DmlWhereClause(@Nullable C criteria) {
        super(criteria);
    }


    @Override
    public final WR where(Function<C, List<IPredicate>> function) {
        return this.addPredicateList(function.apply(this.criteria));
    }

    @Override
    public final WR where(Supplier<List<IPredicate>> supplier) {
        return this.addPredicateList(supplier.get());
    }

    @Override
    public final WR where(Consumer<List<IPredicate>> consumer) {
        final List<IPredicate> list = new ArrayList<>();
        consumer.accept(list);
        return this.addPredicateList(list);
    }

    @Override
    public final WA where(final @Nullable IPredicate predicate) {
        if (predicate != null) {
            this.predicateList.add((OperationPredicate) predicate);
        }
        return (WA) this;
    }

    @Override
    public final WA where(Function<Object, IPredicate> operator, Supplier<?> operand) {
        return this.and(operator, operand);
    }

    @Override
    public final WA where(Function<Object, IPredicate> operator, Function<String, ?> operand, String keyName) {
        return this.and(operator, operand, keyName);
    }

    @Override
    public final WA where(BiFunction<Object, Object, IPredicate> operator, Supplier<?> firstOperand
            , Supplier<?> secondOperand) {
        return this.and(operator, firstOperand, secondOperand);
    }

    @Override
    public final WA where(BiFunction<Object, Object, IPredicate> operator, Function<String, ?> operand
            , String firstKey, String secondKey) {
        return this.and(operator, operand, firstKey, secondKey);
    }

    @Override
    public final WA and(IPredicate predicate) {
        Objects.requireNonNull(predicate);
        this.predicateList.add((OperationPredicate) predicate);
        return (WA) this;
    }

    @Override
    public final WA and(Supplier<IPredicate> supplier) {
        return this.and(supplier.get());
    }

    @Override
    public final WA and(Function<C, IPredicate> function) {
        return this.and(function.apply(this.criteria));
    }

    @Override
    public final WA and(Function<Object, IPredicate> operator, Supplier<?> operand) {
        return this.and(operator.apply(operand.get()));
    }

    @Override
    public final WA and(Function<Object, IPredicate> operator, Function<String, ?> operand, String keyName) {
        return this.and(operator.apply(operand.apply(keyName)));
    }

    @Override
    public final WA and(BiFunction<Object, Object, IPredicate> operator, Supplier<?> firstOperand
            , Supplier<?> secondOperand) {
        return this.and(operator.apply(firstOperand.get(), secondOperand.get()));
    }

    @Override
    public final WA and(BiFunction<Object, Object, IPredicate> operator, Function<String, ?> operand
            , String firstKey, String secondKey) {
        return this.and(operator.apply(operand.apply(firstKey), operand.apply(secondKey)));
    }

    @Override
    public final WA ifAnd(Supplier<IPredicate> supplier) {
        final IPredicate predicate;
        predicate = supplier.get();
        if (predicate != null) {
            this.predicateList.add((OperationPredicate) predicate);
        }
        return (WA) this;
    }

    @Override
    public final WA ifAnd(Function<C, IPredicate> function) {
        final IPredicate predicate;
        predicate = function.apply(this.criteria);
        if (predicate != null) {
            this.predicateList.add((OperationPredicate) predicate);
        }
        return (WA) this;
    }

    @Override
    public final WA ifAnd(Function<Object, IPredicate> operator, Supplier<?> operand) {
        final Object paramOrExp;
        paramOrExp = operand.get();
        if (paramOrExp != null) {
            final IPredicate predicate;
            predicate = operator.apply(paramOrExp);
            assert predicate != null;
            this.predicateList.add((OperationPredicate) predicate);
        }
        return (WA) this;
    }

    @Override
    public final WA ifAnd(Function<Object, IPredicate> operator, Function<String, ?> operand, String keyName) {
        final Object paramOrExp;
        paramOrExp = operand.apply(keyName);
        if (paramOrExp != null) {
            final IPredicate predicate;
            predicate = operator.apply(paramOrExp);
            assert predicate != null;
            this.predicateList.add((OperationPredicate) predicate);
        }
        return (WA) this;
    }

    @Override
    public final WA ifAnd(BiFunction<Object, Object, IPredicate> operator, Supplier<?> firstOperand
            , Supplier<?> secondOperand) {
        final Object first, second;
        if ((first = firstOperand.get()) != null && (second = secondOperand.get()) != null) {
            final IPredicate predicate;
            predicate = operator.apply(first, second);
            assert predicate != null;
            this.predicateList.add((OperationPredicate) predicate);
        }
        return (WA) this;
    }

    @Override
    public final WA ifAnd(BiFunction<Object, Object, IPredicate> operator, Function<String, ?> operand
            , String firstKey, String secondKey) {
        final Object first, second;
        if ((first = operand.apply(firstKey)) != null && (second = operand.apply(secondKey)) != null) {
            final IPredicate predicate;
            predicate = operator.apply(first, second);
            assert predicate != null;
            this.predicateList.add((OperationPredicate) predicate);
        }
        return (WA) this;
    }

    @Override
    public final List<_Predicate> predicateList() {
        this.prepared();
        return this.predicateList;
    }


    abstract Dialect defaultDialect();

    abstract void validateDialect(Dialect dialect);


    final void asDmlStatement() {
        final List<_Predicate> predicates = this.predicateList;
        if (predicates == null || predicates.size() == 0) {
            throw _Exceptions.dmlNoWhereClause();
        }
        this.predicateList = _CollectionUtils.unmodifiableList(predicates);
    }

    final void clearWherePredicate() {
        this.predicateList = null;
    }


    @Override
    public final String toString() {
        final String s;
        if (!(this instanceof WithElement) && this.isPrepared()) {
            s = this.mockAsString(defaultDialect(), Visible.ONLY_VISIBLE, true);
        } else {
            s = super.toString();
        }
        return s;
    }


    @Override
    public final String mockAsString(Dialect dialect, Visible visible, boolean beautify) {
        final Stmt stmt;
        stmt = this.mockAsStmt(dialect, visible);
        final StringBuilder builder = new StringBuilder();
        if (stmt instanceof SimpleStmt) {
            if (this instanceof Update) {
                builder.append("update sql:\n");
            } else {
                builder.append("delete sql:\n");
            }
            builder.append(((SimpleStmt) stmt).sql());

        } else if (stmt instanceof BatchStmt) {
            if (this instanceof Update) {
                builder.append("batch update sql:\n");
            } else {
                builder.append("batch delete sql:\n");
            }
            builder.append(((BatchStmt) stmt).sql());
        } else {
            throw new IllegalStateException("stmt error.");
        }
        return builder.toString();
    }

    @Override
    public final Stmt mockAsStmt(Dialect dialect, Visible visible) {
        if (this instanceof WithElement) {
            throw new IllegalStateException("mockAsStmt(Dialect,Visible) support only not with element statement.");
        }
        final Stmt stmt;
        if (this instanceof Update) {
            stmt = _MockDialects.from(dialect).update((Update) this, visible);
        } else if (this instanceof Delete) {
            stmt = _MockDialects.from(dialect).delete((Delete) this, visible);
        } else {
            throw new IllegalStateException("non-known statement");
        }

        if (stmt instanceof SimpleStmt) {
            _Assert.noNamedParam(((SimpleStmt) stmt).paramGroup());
        }
        return stmt;
    }


    private WR addPredicateList(final @Nullable List<IPredicate> predicates) {
        if (predicates == null || predicates.size() == 0) {
            throw _Exceptions.dmlNoWhereClause();
        }
        final List<_Predicate> predicateList = this.predicateList;
        for (IPredicate predicate : predicates) {
            predicateList.add((OperationPredicate) predicate);
        }
        return (WR) this;
    }


}
