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

import io.army.criteria.Item;
import io.army.criteria.Statement;
import io.army.criteria.TableField;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.criteria.impl.inner._Update;
import io.army.meta.TableMeta;
import io.army.util._Assert;


public abstract class SingleUpdateStatement<I extends Item, F extends TableField, SR, WR, WA, OR, OD, LR, LO, LF>
        extends SetWhereClause<F, SR, WR, WA, OR, OD, LR, LO, LF>
        implements Statement,
        Statement._DmlUpdateSpec<I>,
        _Update,
        _SingleUpdate {


    private Boolean prepared;

    protected SingleUpdateStatement(CriteriaContext context, TableMeta<?> updateTable, String tableAlias) {
        super(context, updateTable, tableAlias);
        context.singleDmlTable(updateTable, tableAlias);
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


    protected void onClear() {
        //no-op
    }


    protected abstract I onAsUpdate();


    protected final void endUpdateStatement() {
        _Assert.nonPrepared(this.prepared);
        this.endUpdateSetClause();
        this.endWhereClauseIfNeed();
        this.endOrderByClauseIfNeed();
        ContextStack.pop(this.context);
        this.prepared = Boolean.TRUE;
    }



}
