/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.criteria.impl;

import io.army.criteria.CteBuilderSpec;
import io.army.criteria.DialectStatement;
import io.army.criteria.Item;
import io.army.criteria.Statement;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._Delete;
import io.army.criteria.impl.inner._Statement;
import io.army.criteria.impl.inner._TabularBlock;
import io.army.util._Assert;

import io.army.lang.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;


/**
 * <p>
 * This class is base class of multi-table delete implementation.
*/
abstract class JoinableDelete<I extends Item, B extends CteBuilderSpec, WE extends Item, FT, FS, FC, FF, JT, JS, JC, JF, WR, WA>
        extends JoinableClause<FT, FS, FC, FF, JT, JS, JC, JF, WR, WA, Object, Object, Object, Object, Object>
        implements _Delete,
        _Statement._JoinableStatement,
        Statement._DmlDeleteSpec<I>,
        DialectStatement._DynamicWithClause<B, WE>,
        _Statement._WithClauseSpec,
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
        return endDynamicWithClause(false, consumer, true);
    }


    @Override
    public final WE withRecursive(Consumer<B> consumer) {
        return endDynamicWithClause(true, consumer, true);
    }


    @Override
    public final WE ifWith(Consumer<B> consumer) {
        return endDynamicWithClause(false, consumer, false);
    }


    @Override
    public final WE ifWithRecursive(Consumer<B> consumer) {
        return endDynamicWithClause(true, consumer, false);
    }

    @Override
    public final boolean isRecursive() {
        return this.recursive;
    }

    @Override
    public final List<_Cte> cteList() {
        List<_Cte> cteList = this.cteList;
        if (cteList == null) {
            this.cteList = cteList = Collections.emptyList();
        }
        return cteList;
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
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        return list;
    }


    abstract I onAsDelete();

    abstract void onClear();

    void onEndStatement() {
        throw new UnsupportedOperationException();
    }

    abstract B createCteBuilder(boolean recursive);


    final void endDeleteStatement() {
        _Assert.nonPrepared(this.prepared);
        this.endWhereClauseIfNeed();
        this.onEndStatement();
        this.tableBlockList = ContextStack.pop(this.context);
        this.prepared = Boolean.TRUE;
    }


    @SuppressWarnings("unchecked")
    final WE endStaticWithClause(final boolean recursive) {
        if (this.cteList != null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        this.recursive = recursive;
        this.cteList = this.context.endWithClause(recursive, true);//static with syntax is required
        return (WE) this;
    }


    @SuppressWarnings("unchecked")
    private WE endDynamicWithClause(final boolean recursive, final Consumer<B> consumer, final boolean required) {
        final B builder;
        builder = createCteBuilder(recursive);

        CriteriaUtils.invokeConsumer(builder, consumer);

        ((CriteriaSupports.CteBuilder) builder).endLastCte();

        this.recursive = recursive;
        this.cteList = this.context.endWithClause(recursive, required);
        return (WE) this;
    }



}
