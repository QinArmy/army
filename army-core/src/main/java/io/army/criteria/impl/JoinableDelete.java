package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.lang.Nullable;
import io.army.util._Assert;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * <p>
 * This class is base class of multi-table delete implementation.
 * </p>
 */
abstract class JoinableDelete<I extends Item, BI extends Item, B extends CteBuilderSpec, WE extends Item, FT, FS, FC, FF, JT, JS, JC, JF, WR, WA>
        extends JoinableClause<FT, FS, FC, FF, JT, JS, JC, JF, WR, WA, Object, Object, Object, Object, Object>
        implements _Delete,
        _Statement._JoinableStatement,
        Statement._DmlDeleteSpec<I>,
        DialectStatement._DynamicWithClause<B, WE>,
        _Statement._WithClauseSpec,
        BatchDeleteSpec<BI>,
        DialectStatement {

    private boolean recursive;

    private List<_Cte> cteList;


    private Boolean prepared;

    private List<_TabularBlock> tableBlockList;


    JoinableDelete(@Nullable _Statement._WithClauseSpec spec, CriteriaContext context) {
        super(context);
        ContextStack.push(this.context);
        if (spec != null) {
            this.recursive = spec.isRecursive();
            this.cteList = spec.cteList();
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
        return this.onAsBatchUpdate(CriteriaUtils.paramList(paramList));
    }

    @Override
    public final <P> BI namedParamList(Supplier<List<P>> supplier) {
        return this.onAsBatchUpdate(CriteriaUtils.paramList(supplier.get()));
    }

    @Override
    public final <K> BI namedParamList(Function<K, ?> function, K key) {
        return this.onAsBatchUpdate(CriteriaUtils.paramListFromMap(function, key));
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
    public final I asDelete() {
        this.endDeleteStatement();
        return this.onAsDelete();
    }


    @Override
    public final void clear() {
        _Assert.prepared(this.prepared);
        this.prepared = Boolean.FALSE;
        this.clearWhereClause();
        this.tableBlockList = null;
        this.onClear();
    }

    @Override
    public final List<_TabularBlock> tableBlockList() {
        final List<_TabularBlock> list = this.tableBlockList;
        if (list == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }


    abstract I onAsDelete();

    abstract BI onAsBatchUpdate(List<?> paramList);

    abstract void onClear();

    void onEndStatement() {
        throw new UnsupportedOperationException();
    }

    abstract B createCteBuilder(boolean recursive);


    final void endDeleteStatement() {
        _Assert.nonPrepared(this.prepared);
        this.endWhereClauseIfNeed();
        this.onEndStatement();
        final CriteriaContext context = this.context;
        this.tableBlockList = context.endContext();
        ContextStack.pop(context);
        this.prepared = Boolean.TRUE;
    }


    @SuppressWarnings("unchecked")
    final WE endStaticWithClause(final boolean recursive) {
        if (this.cteList != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        this.recursive = recursive;
        this.cteList = this.context.endWithClause(recursive, true);//static with syntax is required
        return (WE) this;
    }


    @SuppressWarnings("unchecked")
    private WE endDynamicWithClause(final B builder, final boolean required) {
        if (this.cteList != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        ((CriteriaSupports.CteBuilder) builder).endLastCte();

        final boolean recursive;
        recursive = builder.isRecursive();
        this.recursive = recursive;
        this.cteList = this.context.endWithClause(recursive, required);
        return (WE) this;
    }


    static abstract class ArmyBatchJoinableDelete extends CriteriaSupports.StatementMockSupport
            implements _JoinableDelete,
            DeleteStatement,
            _BatchStatement,
            _Statement._WithClauseSpec {

        private final boolean recursive;

        private final List<_Cte> cteList;

        private final List<_TabularBlock> tableBlockList;

        private final List<_Predicate> wherePredicateList;

        private final List<?> paramList;

        private boolean prepared = true;

        ArmyBatchJoinableDelete(JoinableDelete<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> clause,
                                List<?> paramList) {
            super(clause.context);

            this.recursive = clause.recursive;
            this.cteList = clause.cteList();
            this.tableBlockList = clause.tableBlockList;

            this.wherePredicateList = clause.wherePredicateList();
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
        public final List<_TabularBlock> tableBlockList() {
            return this.tableBlockList;
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


    }//ArmyBatchJoinableUpdate


}
