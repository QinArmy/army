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

package io.army.dialect;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Query;
import io.army.criteria.impl.inner._SelectItem;
import io.army.criteria.impl.inner._Statement;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.stmt.SimpleStmt;
import io.army.stmt.StmtType;
import io.army.stmt.Stmts;

import javax.annotation.Nullable;
import java.util.List;

abstract class MultiTableQueryContext extends BatchSpecStatementContext implements _MultiTableStmtContext, _SimpleQueryContext {

    final MultiTableContext multiTableContext;

    private final List<? extends _SelectItem> selectItemList;


    MultiTableQueryContext(@Nullable StatementContext outerContext, Query query, TableContext tableContext,
                           ArmyParser parser, Visible visible) {
        super(outerContext, (_Statement) query, parser, visible);

        this.selectItemList = ((_Query) query).selectItemList();
        if (query instanceof SubQuery) {
            this.multiTableContext = new MultiTableContext(this, tableContext, this::appendOuterField, this::appendOuterFieldOnly);
        } else {
            this.multiTableContext = new MultiTableContext(this, tableContext, null, null);
        }

    }

    @Override
    public final String safeTableAlias(final TableMeta<?> table, final String alias) {
        return this.multiTableContext.safeTableAlias(table, alias);
    }

    @Override
    public final String safeTableAlias(String alias) {
        return this.multiTableContext.safeTableAlias(alias);
    }

    @Override
    public final String saTableAliasOf(TableMeta<?> table) {
        return this.multiTableContext.saTableAliasOf(table);
    }

    @Override
    public final TabularItem tableItemOf(String tableAlias) {
        return this.multiTableContext.tableItemOf(tableAlias);
    }

    @Override
    public final void appendField(String tableAlias, FieldMeta<?> field) {
        this.multiTableContext.appendField(tableAlias, field);
    }

    @Override
    public final void appendField(FieldMeta<?> field) {
        this.multiTableContext.appendField(field);
    }

    @Override
    public final void appendFieldOnly(FieldMeta<?> field) {
        this.multiTableContext.appendFieldOnly(field);
    }

    @Override
    public List<? extends _SelectItem> selectItemList() {
        return this.selectItemList;
    }

    @Override
    public final List<? extends Selection> selectionList() {
        return _DialectUtils.flatSelectItem(this.selectItemList);
    }


    @Override
    public final boolean hasOptimistic() {
        // query must false
        return false;
    }

    @Override
    public final StmtType stmtType() {
        // query must QUERY
        return StmtType.QUERY;
    }

    @Override
    public final SimpleStmt build() {
        if (this instanceof _SubQueryContext) {
            //sub query don't support,no bug,never here
            String m = String.format("%s don't support build() method", this.getClass().getName());
            throw new UnsupportedOperationException();
        } else if (this.hasNamedParam()) {
            throw new CriteriaException("Query statement don't support named value");
        }
        return Stmts.queryStmt(this);
    }

    void appendOuterField(@Nullable String tableAlias, FieldMeta<?> field) {
        throw new UnsupportedOperationException();
    }

    void appendOuterFieldOnly(FieldMeta<?> field) {
        throw new UnsupportedOperationException();
    }


}
