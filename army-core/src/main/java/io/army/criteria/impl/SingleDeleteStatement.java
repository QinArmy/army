package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._Assert;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


abstract class SingleDeleteStatement<I extends Item, BI extends Item, B extends CteBuilderSpec, WE extends Item, WR, WA, OR, OD, LR, LO, LF>
        extends WhereClause<WR, WA, OR, OD, LR, LO, LF>
        implements _SingleDelete,
        Statement,
        Statement._DmlDeleteSpec<I>,
        Statement._BatchParamClause<BI>,
        DialectStatement._DynamicWithClause<B, WE>,
        _Statement._WithClauseSpec {

    private boolean recursive;

    private List<_Cte> cteList;

    private Boolean prepared;

    SingleDeleteStatement(@Nullable _WithClauseSpec withSpec, CriteriaContext context) {
        super(context);
        ContextStack.push(this.context);
        if (withSpec != null) {
            this.recursive = withSpec.isRecursive();
            this.cteList = withSpec.cteList();
        }
    }


    @Override
    public final WE with(Consumer<B> consumer) {
        final B builder;
        builder = this.createCteBuilder(false);
        consumer.accept(builder);
        return this.endDynamicWithClause(builder, true);
    }


    @Override
    public final WE withRecursive(Consumer<B> consumer) {
        final B builder;
        builder = this.createCteBuilder(true);
        consumer.accept(builder);
        return this.endDynamicWithClause(builder, true);
    }


    @Override
    public final WE ifWith(Consumer<B> consumer) {
        final B builder;
        builder = this.createCteBuilder(false);
        consumer.accept(builder);
        return this.endDynamicWithClause(builder, false);
    }


    @Override
    public final WE ifWithRecursive(Consumer<B> consumer) {
        final B builder;
        builder = this.createCteBuilder(true);
        consumer.accept(builder);
        return this.endDynamicWithClause(builder, false);
    }

    @Override
    public final boolean isRecursive() {
        return this.recursive;
    }

    @Override
    public final List<_Cte> cteList() {
        List<_Cte> cteList = this.cteList;
        if (cteList == null) {
            cteList = Collections.emptyList();
            this.cteList = cteList;
        }
        return cteList;
    }

    @Override
    public final <P> BI namedParamList(@Nullable List<P> paramList) {
        return this.onAsBatchDelete(CriteriaUtils.paramList(paramList));
    }

    @Override
    public final <P> BI namedParamList(Supplier<List<P>> supplier) {
        return this.onAsBatchDelete(CriteriaUtils.paramList(supplier.get()));
    }

    @Override
    public final <K> BI namedParamList(Function<K, ?> function, K key) {
        return this.onAsBatchDelete(CriteriaUtils.paramListFromMap(function, key));
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
    public final void clear() {
        _Assert.prepared(this.prepared);
        this.onClear();
        this.prepared = Boolean.FALSE;
    }

    @Override
    public final I asDelete() {
        this.endDeleteStatement();
        return this.onAsDelete();
    }


    void onClear() {

    }

    abstract I onAsDelete();

    abstract BI onAsBatchDelete(List<?> paramList);


    @SuppressWarnings("unchecked")
    final WE endStaticWithClause(final boolean recursive) {
        if (this.cteList != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        this.recursive = recursive;
        this.cteList = this.context.endWithClause(recursive, true);//static with syntax is required
        return (WE) this;
    }


    abstract B createCteBuilder(boolean recursive);


    @SuppressWarnings("unchecked")
    private WE endDynamicWithClause(final B builder, final boolean required) {
        if (this.cteList != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        final boolean recursive;
        recursive = builder.isRecursive();
        this.recursive = recursive;
        this.cteList = this.context.endWithClause(recursive, required);
        return (WE) this;
    }


    private void endDeleteStatement() {
        _Assert.nonPrepared(this.prepared);
        this.endOrderByClauseIfNeed();
        this.endWhereClauseIfNeed();
        ContextStack.pop(this.context);
        this.prepared = Boolean.TRUE;
    }


    protected static abstract class ArmyBathDelete extends CriteriaSupports.StatementMockSupport
            implements _SingleDelete,
            _BatchStatement,
            DeleteStatement,
            _Statement._WithClauseSpec {

        private final boolean recursive;

        private final List<_Cte> cteList;

        private final TableMeta<?> deleteTable;

        private final String tableAlias;

        private final List<_Predicate> wherePredicateList;

        private final List<?> paramList;

        private boolean prepared = true;

        protected ArmyBathDelete(SingleDeleteStatement<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> statement,
                                 List<?> paramList) {
            super(statement.context);
            statement.prepared();
            this.recursive = statement.isRecursive();
            this.cteList = statement.cteList();

            this.deleteTable = statement.table();
            this.tableAlias = statement.tableAlias();
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
            return this.deleteTable;
        }


        @Override
        public final String tableAlias() {
            return this.tableAlias;
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
