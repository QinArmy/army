package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Statement;
import io.army.dialect.Dialect;
import io.army.dialect.DialectParser;
import io.army.dialect._MockDialects;
import io.army.stmt.Stmt;
import io.army.util.ArrayUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
abstract class OrderByClause<OR> implements CriteriaContextSpec
        , Statement._StaticOrderByClause<OR>
        , _Statement._OrderByListSpec
        , Statement.StatementMockSpec {

    final CriteriaContext context;

    private List<ArmySortItem> orderByList;

    OrderByClause(CriteriaContext context) {
        this.context = context;
    }

    @Override
    public final CriteriaContext getContext() {
        return this.context;
    }

    @Override
    public final OR orderBy(SortItem sortItem) {
        this.orderByList = Collections.singletonList((ArmySortItem) sortItem);
        if (this instanceof OrderByEventListener) {
            ((OrderByEventListener) this).onOrderByEvent();
            ;
        }
        return (OR) this;
    }

    @Override
    public final OR orderBy(SortItem sortItem1, SortItem sortItem2) {
        this.orderByList = ArrayUtils.asUnmodifiableList(
                (ArmySortItem) sortItem1,
                (ArmySortItem) sortItem2
        );
        if (this instanceof OrderByEventListener) {
            ((OrderByEventListener) this).onOrderByEvent();
            ;
        }
        return (OR) this;
    }

    @Override
    public final OR orderBy(SortItem sortItem1, SortItem sortItem2, SortItem sortItem3) {
        this.orderByList = ArrayUtils.asUnmodifiableList(
                (ArmySortItem) sortItem1,
                (ArmySortItem) sortItem2,
                (ArmySortItem) sortItem3
        );
        if (this instanceof OrderByEventListener) {
            ((OrderByEventListener) this).onOrderByEvent();
            ;
        }
        return (OR) this;
    }

    @Override
    public final OR orderBy(Consumer<Consumer<SortItem>> consumer) {
        consumer.accept(this::onAddOrderBy);
        if (this.orderByList == null) {
            throw ContextStack.criteriaError(this.context, _Exceptions::sortItemListIsEmpty);
        }
        if (this instanceof OrderByEventListener) {
            ((OrderByEventListener) this).onOrderByEvent();
            ;
        }
        return (OR) this;
    }


    @Override
    public final OR ifOrderBy(Consumer<Consumer<SortItem>> consumer) {
        consumer.accept(this::onAddOrderBy);
        if (this instanceof OrderByEventListener) {
            ((OrderByEventListener) this).onOrderByEvent();
            ;
        }
        return (OR) this;
    }

    @Override
    public final String mockAsString(Dialect dialect, Visible visible, boolean none) {
        final DialectParser parser;
        parser = _MockDialects.from(dialect);
        final Stmt stmt;
        stmt = this.parseStatement(parser, visible);
        return parser.printStmt(stmt, none);
    }

    @Override
    public final Stmt mockAsStmt(Dialect dialect, Visible visible) {
        return this.parseStatement(_MockDialects.from(dialect), visible);
    }


    private Stmt parseStatement(final DialectParser parser, final Visible visible) {
        if (!(this instanceof PrimaryStatement)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        final Stmt stmt;
        if (this instanceof Select) {
            stmt = parser.select((Select) this, visible);
        } else if (this instanceof Update) {
            stmt = parser.update((Update) this, visible);
        } else if (this instanceof Delete) {
            stmt = parser.delete((Delete) this, visible);
        } else if (this instanceof DialectStatement) {
            stmt = parser.dialectStmt((DialectStatement) this, visible);
        } else {
            throw new IllegalStateException("unknown statement");
        }
        return stmt;
    }

    @Override
    public final List<? extends SortItem> orderByList() {
        final List<ArmySortItem> orderByList = this.orderByList;
        if (orderByList == null || orderByList instanceof ArrayList) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return orderByList;
    }

    final void endOrderByClause() {
        final List<ArmySortItem> orderByList = this.orderByList;
        if (orderByList == null) {
            this.orderByList = Collections.emptyList();
        } else if (orderByList instanceof ArrayList) {
            this.orderByList = _CollectionUtils.unmodifiableList(orderByList);
        } else {
            throw ContextStack.castCriteriaApi(this.context);
        }
    }


    final void clearOrderByList() {
        this.orderByList = null;
    }


    private void onAddOrderBy(final SortItem sortItem) {
        List<ArmySortItem> orderByList = this.orderByList;
        if (orderByList == null) {
            orderByList = new ArrayList<>();
            this.orderByList = orderByList;
        } else if (!(orderByList instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        orderByList.add((ArmySortItem) sortItem);
    }

    interface OrderByEventListener {

        void onOrderByEvent();

    }


}
