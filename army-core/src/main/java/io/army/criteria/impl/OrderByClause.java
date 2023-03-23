package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._DerivedTable;
import io.army.criteria.impl.inner._RowSet;
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
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
abstract class OrderByClause<OR> extends CriteriaSupports.StatementMockSupport
        implements CriteriaContextSpec,
        Statement._OrderByClause<OR>,
        _Statement._OrderByListSpec {

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
    public final OR orderBy(SortItem sortItem) {
        this.onAddOrderBy(sortItem);
        return (OR) this;
    }

    @Override
    public final OR orderBy(SortItem sortItem1, SortItem sortItem2) {
        this.onAddOrderBy(sortItem1)
                .onAddOrderBy(sortItem2);
        return (OR) this;
    }

    @Override
    public final OR orderBy(SortItem sortItem1, SortItem sortItem2, SortItem sortItem3) {
        this.onAddOrderBy(sortItem1)
                .onAddOrderBy(sortItem2)
                .onAddOrderBy(sortItem3);
        return (OR) this;
    }

    @Override
    public final OR orderBy(SortItem sortItem1, SortItem sortItem2, SortItem sortItem3, SortItem sortItem4) {
        this.onAddOrderBy(sortItem1)
                .onAddOrderBy(sortItem2)
                .onAddOrderBy(sortItem3)
                .onAddOrderBy(sortItem4);
        return (OR) this;
    }

    @Override
    public final OR orderBy(SortItem sortItem1, SortItem sortItem2, SortItem sortItem3, SortItem sortItem4, SortItem sortItem5) {
        this.onAddOrderBy(sortItem1)
                .onAddOrderBy(sortItem2)
                .onAddOrderBy(sortItem3)
                .onAddOrderBy(sortItem4)
                .onAddOrderBy(sortItem5);
        return (OR) this;
    }

    @Override
    public final OR orderBy(Consumer<Consumer<SortItem>> consumer) {
        consumer.accept(this::onAddOrderBy);
        if (this.orderByList == null) {
            throw ContextStack.criteriaError(this.context, _Exceptions::sortItemListIsEmpty);
        }
        return (OR) this;
    }

    @Override
    public final OR ifOrderBy(Consumer<Consumer<SortItem>> consumer) {
        consumer.accept(this::onAddOrderBy);
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


    final boolean hasOrderByClause() {
        return this.orderByList != null;
    }


    final void clearOrderByList() {
        this.orderByList = null;
    }


    private OrderByClause<?> onAddOrderBy(final SortItem sortItem) {
        List<ArmySortItem> orderByList = this.orderByList;
        if (orderByList == null) {
            orderByList = new ArrayList<>();
            this.orderByList = orderByList;
            if (this instanceof Statement) {
                this.context.onOrderByStart();
            }
        } else if (!(orderByList instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.context);
        }

        if (sortItem instanceof ArmySortItem) {
            orderByList.add((ArmySortItem) sortItem);
        } else {
            orderByList.add((ArmySortItem) sortItem.asSortItem());
        }
        return this;
    }

    interface OrderByEventListener {

        void onOrderByEvent();

    }


    static abstract class UnionRowSet implements _UnionRowSet,
            Statement,
            CriteriaContextSpec,
            Statement.StatementMockSpec {

        final RowSet left;

        private final UnionType unionType;

        private final RowSet right;

        UnionRowSet(RowSet left, UnionType unionType, RowSet right) {
            if (((_RowSet) right).selectionSize() != ((_RowSet) left).selectionSize()) {
                throw leftAndRightNotMatch(left, right);
            }
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
        public final void clear() {
            //no-op
        }

        @Override
        public final int selectionSize() {
            return ((_RowSet) this.left).selectionSize();
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

        private Dialect statementDialect(){
              RowSet left = this.left;
             while ( !(left instanceof CriteriaSupports.StatementMockSupport)){
                 left = ((UnionRowSet) left).left;
             }
             return ((CriteriaSupports.StatementMockSupport) left).statementDialect();
        }

        private Stmt parseStatement(final DialectParser parser, final Visible visible) {
            if (!(this instanceof PrimaryStatement)) {
                throw _Exceptions.castCriteriaApi();
            }
            final Stmt stmt;
            if (this instanceof Select) {
                stmt = parser.select((Select) this, visible);
            } else if (this instanceof Values) {
                stmt = parser.values((Values) this, visible);
            } else if (this instanceof DqlStatement) {
                stmt = parser.dialectDql((DqlStatement) this, visible);
            } else {
                throw new IllegalStateException("unknown statement");
            }
            return stmt;
        }

        private static CriteriaException leftAndRightNotMatch(final RowSet left, final RowSet right) {
            String m = String.format("left selection size[%s] and right selection size[%s] not match.",
                    ((_RowSet) left).selectionSize(),
                    ((_RowSet) right).selectionSize());
            return ContextStack.clearStackAndCriteriaError(m);
        }


    }//UnionRowSet

    static abstract class UnionSubRowSet extends UnionRowSet
            implements _DerivedTable {

        UnionSubRowSet(RowSet left, UnionType unionType, RowSet right) {
            super(left, unionType, right);
        }


        @Override
        public final Selection refSelection(String derivedAlias) {
            return ((_DerivedTable) this.left).refSelection(derivedAlias);
        }

        @Override
        public final List<? extends Selection> refAllSelection() {
            return ((_DerivedTable) this.left).refAllSelection();
        }


    }//UnionSubRowSet


    static abstract class OrderByClauseClause<OR> extends OrderByClause<OR> {

        OrderByClauseClause(CriteriaContext context) {
            super(context);
        }


        @Override
        final Dialect statementDialect() {
            throw ContextStack.castCriteriaApi(this.context);
        }


    } //OrderByClauseClause


}
