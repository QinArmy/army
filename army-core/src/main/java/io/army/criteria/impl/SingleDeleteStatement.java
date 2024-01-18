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
import io.army.criteria.impl.inner._SingleDelete;
import io.army.criteria.impl.inner._Statement;
import io.army.util._Assert;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;


abstract class SingleDeleteStatement<I extends Item, B extends CteBuilderSpec, WE extends Item, WR, WA, OR, OD, LR, LO, LF>
        extends WhereClause<WR, WA, OR, OD, LR, LO, LF>
        implements _SingleDelete,
        Statement,
        Statement._DmlDeleteSpec<I>,
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


    private void endDeleteStatement() {
        _Assert.nonPrepared(this.prepared);
        this.endOrderByClauseIfNeed();
        this.endWhereClauseIfNeed();
        ContextStack.pop(this.context);
        this.prepared = Boolean.TRUE;
    }




}
