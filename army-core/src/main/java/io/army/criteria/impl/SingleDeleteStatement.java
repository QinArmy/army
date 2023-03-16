package io.army.criteria.impl;

import io.army.criteria.CteBuilderSpec;
import io.army.criteria.DialectStatement;
import io.army.criteria.Item;
import io.army.criteria.Statement;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._SingleDelete;
import io.army.criteria.impl.inner._Statement;
import io.army.lang.Nullable;
import io.army.util._Assert;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;


abstract class SingleDeleteStatement<I extends Item, WR, WA, OR, LR, LO, LF>
        extends WhereClause<WR, WA, OR, LR, LO, LF>
        implements _SingleDelete,
        Statement,
        Statement._DmlDeleteSpec<I> {


    private Boolean prepared;

    SingleDeleteStatement(CriteriaContext context) {
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


    private void endDeleteStatement() {
        _Assert.nonPrepared(this.prepared);
        this.endOrderByClause();
        this.endWhereClause();
        ContextStack.pop(this.context);
        this.prepared = Boolean.TRUE;
    }


    static abstract class WithSingleDelete<I extends Item, B extends CteBuilderSpec, WE, WR, WA, OR, LR, LO, LF>
            extends SingleDeleteStatement<I, WR, WA, OR, LR, LO, LF>
            implements DialectStatement._DynamicWithClause<B, WE>
            , _Statement._WithClauseSpec {

        private boolean recursive;

        private List<_Cte> cteList;

        WithSingleDelete(@Nullable _WithClauseSpec withSpec, CriteriaContext context) {
            super(context);
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


    }//WithSingleDelete


}