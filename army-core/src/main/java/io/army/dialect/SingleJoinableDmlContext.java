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

import io.army.criteria.TabularItem;
import io.army.criteria.impl.inner._SingleDml;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.session.SessionSpec;

import io.army.lang.Nullable;

abstract class SingleJoinableDmlContext extends SingleTableDmlContext implements _MultiTableStmtContext {


    final MultiTableContext multiTableContext;

    // store table context for child
    final TableContext tableContext;

    SingleJoinableDmlContext(@Nullable StatementContext outerContext, _SingleDml stmt,
                             TableContext tableContext, ArmyParser parser, SessionSpec sessionSpec) {
        super(outerContext, stmt, parser, sessionSpec);
        this.tableContext = tableContext;   // store table context for child
        this.multiTableContext = new MultiTableContext(this, tableContext, null, null);
    }

    SingleJoinableDmlContext(_SingleDml stmt, SingleJoinableDmlContext parentContext) {
        super(stmt, parentContext);
        this.tableContext = parentContext.tableContext;
        this.multiTableContext = new MultiTableContext(this, this.tableContext, null, null);
    }


    @Override
    public final void appendField(final String tableAlias, final FieldMeta<?> field) {
        this.multiTableContext.appendField(tableAlias, field);
    }

    @Override
    public final void appendField(final FieldMeta<?> field) {
        this.multiTableContext.appendField(field);
    }

    @Override
    public final void appendFieldOnly(FieldMeta<?> field) {
        this.multiTableContext.appendFieldOnly(field);
    }

    @Override
    public final String safeTableAlias(TableMeta<?> table, String alias) {
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
    public final TabularItem tabularItemOf(String tableAlias) {
        return this.multiTableContext.tabularItemOf(tableAlias);
    }

    @Nullable
    @Override
    public final String trySaTableAliasOf(TableMeta<?> table) {
        return this.multiTableContext.trySaTableAliasOf(table);
    }


}
