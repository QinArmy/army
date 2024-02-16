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

import io.army.criteria.CriteriaException;
import io.army.criteria.Expression;
import io.army.criteria.SqlField;
import io.army.criteria.TabularItem;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.session.SessionSpec;
import io.army.stmt.Stmt;
import io.army.stmt.StmtType;
import io.army.stmt.Stmts;

import javax.annotation.Nullable;
import java.util.List;

final class JoinableMergeContext extends StatementContext implements _JoinableMergeContext {


    private final MultiTableContext multiTableContext;

    private JoinableMergeContext(@Nullable StatementContext outerContext, TableContext tableContext,
                                 ArmyParser parser, SessionSpec sessionSpec) {
        super(outerContext, parser, sessionSpec);
        this.multiTableContext = new MultiTableContext(this, tableContext, null, null);
    }


    @Override
    public void appendField(final String tableAlias, final FieldMeta<?> field) {
        this.multiTableContext.appendField(tableAlias, field);
    }

    @Override
    public void appendField(final FieldMeta<?> field) {
        this.multiTableContext.appendField(field);
    }

    @Override
    public void appendFieldOnly(FieldMeta<?> field) {
        this.multiTableContext.appendFieldOnly(field);
    }

    @Override
    public String safeTableAlias(TableMeta<?> table, String alias) {
        return this.multiTableContext.safeTableAlias(table, alias);
    }

    @Override
    public String safeTableAlias(String alias) {
        return this.multiTableContext.safeTableAlias(alias);
    }

    @Override
    public String saTableAliasOf(TableMeta<?> table) {
        return this.multiTableContext.saTableAliasOf(table);
    }

    @Override
    public TabularItem tabularItemOf(String tableAlias) {
        return this.multiTableContext.tabularItemOf(tableAlias);
    }

    @Override
    public void appendSetLeftItem(SqlField dataField, @Nullable Expression updateTimePlaceholder) {
        this.multiTableContext.appendSetLeftItem(dataField, updateTimePlaceholder);
    }

    @Override
    public boolean isAppendedUpdateTime() {
        return this.multiTableContext.isAppendedUpdateTime();
    }


    @Override
    public void appendConditionFields() {
        final List<SqlField> conditionFieldList = this.multiTableContext.conditionFieldList();

        final int size;
        if ((size = conditionFieldList.size()) == 0) {
            return;
        }
        final StringBuilder builder = new StringBuilder(80);
        builder.append("Merge statement don't support update condition filed:\n");
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                builder.append(_Constant.SPACE_COMMA_SPACE);
            }
            builder.append(conditionFieldList.get(i));
        }
        throw new CriteriaException(builder.toString());
    }

    @Override
    public boolean hasOptimistic() {
        return false;
    }

    @Override
    public StmtType stmtType() {
        return StmtType.UPDATE;
    }

    @Override
    public Stmt build() {
        return Stmts.minSimple(this);
    }


}
