package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Statement;
import io.army.dialect.Dialect;
import io.army.dialect.DialectParser;
import io.army.dialect._MockDialects;
import io.army.stmt.Stmt;
import io.army.util._CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unchecked")
abstract class OrderByClause<OR> implements CriteriaContextSpec
        , Statement._StaticOrderByClause<OR>
        , Statement._StaticOrderByNullsCommaClause<OR>
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
    public final OR orderBy(Expression exp) {
        this.onAddOrderBy(exp);
        return (OR) this;
    }

    @Override
    public final OR orderBy(Expression exp, Statement.AscDesc ascDesc) {
        this.onAddOrderBy(SortItems.create(exp, ascDesc));
        return (OR) this;
    }

    @Override
    public final OR orderBy(Expression exp1, Expression exp2) {
        this.onAddOrderBy(exp1)
                .add((ArmySortItem) exp2);
        return (OR) this;
    }

    @Override
    public final OR orderBy(Expression exp1, Statement.AscDesc ascDesc1, Expression exp2) {
        this.onAddOrderBy(SortItems.create(exp1, ascDesc1))
                .add((ArmySortItem) exp2);
        return (OR) this;
    }

    @Override
    public final OR orderBy(Expression exp1, Expression exp2, Statement.AscDesc ascDesc2) {
        this.onAddOrderBy(exp1)
                .add(SortItems.create(exp2, ascDesc2));
        return (OR) this;
    }

    @Override
    public final OR orderBy(Expression exp1, Statement.AscDesc ascDesc1, Expression exp2, Statement.AscDesc ascDesc2) {
        this.onAddOrderBy(SortItems.create(exp1, ascDesc1))
                .add(SortItems.create(exp2, ascDesc2));
        return (OR) this;
    }

    @Override
    public final OR comma(Expression exp) {
        this.onAddOrderBy(exp);
        return (OR) this;
    }

    @Override
    public final OR comma(Expression exp, Statement.AscDesc ascDesc) {
        this.onAddOrderBy(SortItems.create(exp, ascDesc));
        return (OR) this;
    }

    @Override
    public final OR comma(Expression exp1, Expression exp2) {
        this.onAddOrderBy(exp1)
                .add((ArmySortItem) exp2);
        return (OR) this;
    }

    @Override
    public final OR comma(Expression exp1, Statement.AscDesc ascDesc1, Expression exp2) {
        this.onAddOrderBy(SortItems.create(exp1, ascDesc1))
                .add((ArmySortItem) exp2);
        return (OR) this;
    }

    @Override
    public final OR comma(Expression exp1, Expression exp2, Statement.AscDesc ascDesc2) {
        this.onAddOrderBy(exp1)
                .add(SortItems.create(exp2, ascDesc2));
        return (OR) this;
    }

    @Override
    public final OR comma(Expression exp1, Statement.AscDesc ascDesc1, Expression exp2, Statement.AscDesc ascDesc2) {
        this.onAddOrderBy(SortItems.create(exp1, ascDesc1))
                .add(SortItems.create(exp2, ascDesc2));
        return (OR) this;
    }

    @Override
    public final OR comma(Expression exp, Statement.NullsFirstLast nullOption) {
        this.onAddOrderBy(SortItems.create(exp, nullOption));
        return (OR) this;
    }

    @Override
    public final OR comma(Expression exp, Statement.AscDesc ascDesc, Statement.NullsFirstLast nullOption) {
        this.onAddOrderBy(SortItems.create(exp, ascDesc, nullOption));
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


    private List<ArmySortItem> onAddOrderBy(final SortItem sortItem) {
        List<ArmySortItem> orderByList = this.orderByList;
        if (orderByList == null) {
            orderByList = new ArrayList<>();
            this.orderByList = orderByList;
        } else if (!(orderByList instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        orderByList.add((ArmySortItem) sortItem);
        return orderByList;
    }

    interface OrderByEventListener {

        void onOrderByEvent();

    }


}
