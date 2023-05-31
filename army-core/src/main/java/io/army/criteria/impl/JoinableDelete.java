package io.army.criteria.impl;

import io.army.criteria.CteBuilderSpec;
import io.army.criteria.DialectStatement;
import io.army.criteria.Item;
import io.army.criteria.Statement;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._Delete;
import io.army.criteria.impl.inner._Statement;
import io.army.criteria.impl.inner._TabularBlock;
import io.army.lang.Nullable;
import io.army.util._Assert;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;


/**
 * <p>
 * This class is base class of multi-table delete implementation.
 * </p>
 */
abstract class JoinableDelete<I extends Item, FT, FS, FC, FF, JT, JS, JC, JF, WR, WA>
        extends JoinableClause<FT, FS, FC, FF, JT, JS, JC, JF, WR, WA, Object, Object, Object, Object, Object>
        implements _Delete,
        _Statement._JoinableStatement,
        Statement._DmlDeleteSpec<I>,
        Statement {


    private Boolean prepared;

    private List<_TabularBlock> tableBlockList;


    JoinableDelete(CriteriaContext context) {
        super(context);
        ContextStack.push(this.context);
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

    abstract void onClear();

    void onEndStatement() {
        throw new UnsupportedOperationException();
    }


    final void endDeleteStatement() {
        _Assert.nonPrepared(this.prepared);
        this.endWhereClause();
        this.onEndStatement();
        final CriteriaContext context = this.context;
        this.tableBlockList = context.endContext();
        ContextStack.pop(context);
        this.prepared = Boolean.TRUE;
    }


    static abstract class WithJoinableDelete<I extends Item, B extends CteBuilderSpec, WE extends Item, FT, FS, FC, FF, JT, JS, JC, JF, WR, WA>
            extends JoinableDelete<I, FT, FS, FC, FF, JT, JS, JC, JF, WR, WA>
            implements DialectStatement._DynamicWithClause<B, WE>,
            _Statement._WithClauseSpec {

        private boolean recursive;

        private List<_Cte> cteList;

        WithJoinableDelete(@Nullable _Statement._WithClauseSpec spec, CriteriaContext context) {
            super(context);
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
            ((CriteriaSupports.CteBuilder) builder).endLastCte();

            final boolean recursive;
            recursive = builder.isRecursive();
            this.recursive = recursive;
            this.cteList = this.context.endWithClause(recursive, required);
            return (WE) this;
        }

    }//WithMultiDelete


}
