package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._MultiDelete;
import io.army.criteria.impl.inner._Statement;
import io.army.criteria.impl.inner._TableBlock;
import io.army.dialect.Dialect;
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
abstract class MultiDelete<I extends Item, Q extends Item, FT, FS, FC, JT, JS, JC, WR, WA>
        extends JoinableClause<FT, FS, FC, JT, JS, JC, WR, WA, Object, Object, Object, Object>
        implements _MultiDelete
        , Statement._DmlDeleteSpec<I>
        , Statement._DqlDeleteSpec<Q>
        , Statement {


    private Boolean prepared;

    private List<_TableBlock> tableBlockList;


    MultiDelete(CriteriaContext context) {
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
    public final Q asReturningDelete() {
        this.endDeleteStatement();
        return this.onAsReturningDelete();
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
    public final List<_TableBlock> tableBlockList() {
        final List<_TableBlock> list = this.tableBlockList;
        if (list == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
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

    abstract I onAsDelete();

    Q onAsReturningDelete() {
        throw new UnsupportedOperationException();
    }

    abstract void onClear();

    void onEndStatement() {
        throw new UnsupportedOperationException();
    }


    private void endDeleteStatement() {
        _Assert.nonPrepared(this.prepared);
        this.endWhereClause();
        this.onEndStatement();
        final CriteriaContext context = this.context;
        this.tableBlockList = context.endContext();
        ContextStack.pop(context);
        this.prepared = Boolean.TRUE;
    }


    static abstract class WithMultiDelete<I extends Item, Q extends Item, B extends CteBuilderSpec, WE, FT, FS, FC, JT, JS, JC, WR, WA>
            extends MultiDelete<I, Q, FT, FS, FC, JT, JS, JC, WR, WA> implements DialectStatement._DynamicWithClause<B, WE>
            , _Statement._WithClauseSpec {

        private boolean recursive;

        private List<_Cte> cteList;

        WithMultiDelete(@Nullable _WithClauseSpec withSpec, CriteriaContext context) {
            super(context);
            if (withSpec != null) {
                this.recursive = withSpec.isRecursive();
                this.cteList = withSpec.cteList();
                assert this.cteList != null;
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


        final void endStaticWithClause(final boolean recursive) {
            if (this.cteList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.recursive = recursive;
            this.cteList = this.context.endWithClause(true);//static with syntax is required
        }


        abstract B createCteBuilder(boolean recursive);


        @SuppressWarnings("unchecked")
        private WE endDynamicWithClause(final B builder, final boolean required) {
            if (this.cteList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.recursive = builder.isRecursive();
            this.cteList = this.context.endWithClause(required);
            return (WE) this;
        }

    }//WithMultiDelete


}
