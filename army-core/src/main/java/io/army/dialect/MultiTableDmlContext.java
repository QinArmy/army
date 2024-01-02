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
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._DmlStatement;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * <p>
 * This class is base class of below:
 * <ul>
 *     <li>{@link MultiUpdateContext}</li>
 *     <li>{@link MultiDeleteContext}</li>
 * </ul>
 *
 * @since 0.6.0
 */
abstract class MultiTableDmlContext extends NarrowDmlStmtContext implements _MultiTableStmtContext {

    final MultiTableContext multiTableContext;

    final Map<String, String> childAliasToParentAlias;

    MultiTableDmlContext(@Nullable StatementContext outerContext, _DmlStatement stmt
            , TableContext tableContext, ArmyParser parser, Visible visible) {
        super(outerContext, stmt, parser, visible);
        this.childAliasToParentAlias = tableContext.childAliasToParentAlias;
        this.multiTableContext = new MultiTableContext(this, tableContext, null, null);
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


}
