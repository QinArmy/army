package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Statement;
import io.army.criteria.impl.inner._UnionRowSet;
import io.army.dialect.Dialect;
import io.army.dialect.DialectParser;
import io.army.dialect._MockDialects;
import io.army.stmt.Stmt;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unchecked")
abstract class OrderByClause<OR> extends CriteriaSupports.StatementMockSupport
        implements CriteriaContextSpec
        , Statement._StaticOrderByClause<OR>
        , Statement._StaticOrderByNullsCommaClause<OR>
        , _Statement._OrderByListSpec
        , Statement.StatementMockSpec {

    final CriteriaContext context;

    private List<ArmySortItem> orderByList;

    OrderByClause(CriteriaContext context) {
        super(context);
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
    public final OR comma(Expression exp, Statement.AscDesc ascDesc) {
        this.onAddOrderBy(SortItems.create(exp, ascDesc));
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


    static abstract class UnionRowSet
            implements _UnionRowSet, Statement, CriteriaContextSpec
            , Statement.StatementMockSpec {

        final RowSet left;

        private final UnionType unionType;

        private final RowSet right;

        UnionRowSet(RowSet left, UnionType unionType, RowSet right) {
            this.left = left;
            this.unionType = unionType;
            this.right = right;
        }

        @Override
        public final CriteriaContext getContext() {
            return ((CriteriaContextSpec) this.left).getContext();
        }

        @Override
        public final RowSet leftRowSet() {
            return this.left;
        }

        @Override
        public final SQLWords unionType() {
            return this.unionType;
        }

        @Override
        public final RowSet rightRowSet() {
            return this.right;
        }

        @Override
        public final void prepared() {
            //no-op
        }

        @Override
        public final boolean isPrepared() {
            return true;
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

        @Override
        public final String toString() {
            final String s;
            if (this instanceof PrimaryStatement && this.isPrepared()) {
                s = this.mockAsString(this.statementDialect(), Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }

        abstract Dialect statementDialect();

        private Stmt parseStatement(final DialectParser parser, final Visible visible) {
            if (!(this instanceof PrimaryStatement)) {
                throw _Exceptions.castCriteriaApi();
            }
            final Stmt stmt;
            if (this instanceof Select) {
                stmt = parser.select((Select) this, visible);
            } else if (this instanceof DialectStatement) {
                stmt = parser.dialectStmt((DialectStatement) this, visible);
            } else {
                throw new IllegalStateException("unknown statement");
            }
            return stmt;
        }


    }//UnionRowSet

    static abstract class UnionSubRowSet extends UnionRowSet
            implements DerivedTable {

        UnionSubRowSet(RowSet left, UnionType unionType, RowSet right) {
            super(left, unionType, right);
        }

        @Override
        public final List<? extends SelectItem> selectItemList() {
            return ((DerivedTable) this.left).selectItemList();
        }

        @Override
        public final Selection selection(String derivedAlias) {
            return ((DerivedTable) this.left).selection(derivedAlias);
        }

        @Override
        final Dialect statementDialect() {
            throw _Exceptions.castCriteriaApi();
        }


    }//UnionSubRowSet


}
