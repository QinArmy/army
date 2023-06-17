package io.army.criteria.impl;

import io.army.criteria.Item;
import io.army.criteria.Statement;
import io.army.criteria.TableField;
import io.army.criteria.impl.inner.*;
import io.army.meta.TableMeta;
import io.army.util._Assert;
import io.army.util._Collections;

import java.util.List;


abstract class SingleUpdateStatement<I extends Item, F extends TableField, SR, WR, WA, OR, OD, LR, LO, LF>
        extends SetWhereClause<F, SR, WR, WA, OR, OD, LR, LO, LF>
        implements Statement,
        Statement._DmlUpdateSpec<I>,
        _Update,
        _SingleUpdate {


    private Boolean prepared;

    SingleUpdateStatement(CriteriaContext context, TableMeta<?> updateTable, String tableAlias) {
        super(context, updateTable, tableAlias);
    }


    @Override
    public final void prepared() {
        _Assert.prepared(this.prepared);
    }

    @Override
    public final boolean isPrepared() {
        final Boolean prepared = this.prepared;
        return prepared != null && prepared;
    }


    @Override
    public final I asUpdate() {
        this.endUpdateStatement();
        return this.onAsUpdate();
    }

    @Override
    public final void clear() {
        _Assert.prepared(this.prepared);
        this.prepared = Boolean.FALSE;
        this.onClear();
    }


    @Override
    public final String tableAlias() {
        return this.tableAlias;
    }


    void onClear() {
        //no-op
    }


    abstract I onAsUpdate();


    final void endUpdateStatement() {
        _Assert.nonPrepared(this.prepared);
        this.endUpdateSetClause();
        this.endWhereClauseIfNeed();
        this.endOrderByClauseIfNeed();
        ContextStack.pop(this.context);
        this.prepared = Boolean.TRUE;
    }


    protected static abstract class ArmySingleBathUpdate extends CriteriaSupports.StatementMockSupport
            implements _SingleUpdate,
            _BatchStatement,
            Statement,
            _Statement._WithClauseSpec {

        private final boolean recursive;

        private final List<_Cte> cteList;

        private final TableMeta<?> updateTable;

        private final String tableAlias;

        private final List<_ItemPair> itemPairList;

        private final List<_Predicate> wherePredicateList;

        private final List<?> paramList;

        private boolean prepared = true;

        protected ArmySingleBathUpdate(SingleUpdateStatement<?, ?, ?, ?, ?, ?, ?, ?, ?, ?> statement,
                                       List<?> paramList) {
            super(statement.context);
            statement.prepared();
            if (statement instanceof _Statement._WithClauseSpec) {
                this.recursive = ((_WithClauseSpec) statement).isRecursive();
                this.cteList = ((_WithClauseSpec) statement).cteList();
            } else {
                this.recursive = false;
                this.cteList = _Collections.emptyList();
            }
            this.updateTable = statement.updateTable;
            this.tableAlias = statement.tableAlias;
            this.itemPairList = statement.itemPairList();
            this.wherePredicateList = statement.wherePredicateList();
            this.paramList = paramList;
        }

        @Override
        public final boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public final List<_Cte> cteList() {
            return this.cteList;
        }

        @Override
        public final TableMeta<?> table() {
            return this.updateTable;
        }


        @Override
        public final String tableAlias() {
            return this.tableAlias;
        }

        @Override
        public final List<_ItemPair> itemPairList() {
            return this.itemPairList;
        }

        @Override
        public final List<_Predicate> wherePredicateList() {
            return this.wherePredicateList;
        }

        @Override
        public final List<?> paramList() {
            return this.paramList;
        }

        @Override
        public final void prepared() {
            _Assert.prepared(this.prepared);
        }

        @Override
        public final boolean isPrepared() {
            return this.prepared;
        }

        @Override
        public final void clear() {
            if (!this.prepared) {
                return;
            }
            this.prepared = false;
            this.onClear();
        }

        void onClear() {
            //no-op
        }


    }//ArmySingleBathUpdate


}
