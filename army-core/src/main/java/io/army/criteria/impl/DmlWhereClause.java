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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
abstract class DmlWhereClause<C, WR, WA> implements Statement, Statement.WhereClause<C, WR, WA>
        , Statement.WhereAndClause<C, WA>, _Dml {

    final CriteriaContext criteriaContext;

    final C criteria;

    List<_Predicate> predicateList = new ArrayList<>();

    DmlWhereClause(CriteriaContext criteriaContext) {
        this.criteriaContext = criteriaContext;
        this.criteria = criteriaContext.criteria();
    }


    @Override
    public final WR where(List<IPredicate> predicateList) {
        final List<_Predicate> predicates = this.predicateList;
        for (IPredicate predicate : predicateList) {
            if (!(predicate instanceof OperationPredicate)) {
                throw CriteriaUtils.nonArmyExpression(predicate);
            }
            predicates.add((OperationPredicate) predicate);
        }
        return (WR) this;
    }

    @Override
    public final WR where(Function<C, List<IPredicate>> function) {
        return this.where(function.apply(this.criteria));
    }

    @Override
    public final WR where(Supplier<List<IPredicate>> supplier) {
        return this.where(supplier.get());
    }

    @Override
    public final WR where(Consumer<List<IPredicate>> consumer) {
        final List<IPredicate> list = new ArrayList<>();
        consumer.accept(list);
        return this.where(list);
    }

    @Override
    public final WA where(final @Nullable IPredicate predicate) {
        if (predicate != null) {
            this.predicateList.add((OperationPredicate) predicate);
        }
        return (WA) this;
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
    public final WA ifAnd(@Nullable IPredicate predicate) {
        if (predicate != null) {
            this.predicateList.add((OperationPredicate) predicate);
        }
        return (WA) this;
    }

    @Override
    public final WA ifAnd(Supplier<IPredicate> supplier) {
        return this.ifAnd(supplier.get());
    }

    @Override
    public final WA ifAnd(Function<C, IPredicate> function) {
        return this.ifAnd(function.apply(this.criteria));
    }

    @Override
    public final List<_Predicate> predicateList() {
        prepared();
        return this.predicateList;
    }


    abstract Dialect defaultDialect();

    abstract void validateDialect(Dialect dialect);


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


}
