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

package io.army.dialect.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._MultiUpdate;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.criteria.impl.inner._Update;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.session.SessionSpec;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.Map;

final class MultiUpdateContext extends MultiTableDmlContext implements _MultiUpdateContext {

    static MultiUpdateContext create(@Nullable _SqlContext outerContext, _MultiUpdate statement, ArmyParser dialect,
                                     SessionSpec sessionSpec) {
        final TableContext tableContext;
        tableContext = TableContext.forUpdate(statement, dialect, sessionSpec.visible());
        return new MultiUpdateContext((StatementContext) outerContext, statement, tableContext, dialect, sessionSpec);
    }

    static MultiUpdateContext forChild(@Nullable _SqlContext outerContext, _SingleUpdate stmt, ArmyParser dialect
            , SessionSpec sessionSpec) {
        final TableContext tableContext;
        tableContext = TableContext.forChild((ChildTableMeta<?>) stmt.table(), stmt.tableAlias(), dialect);
        return new MultiUpdateContext((StatementContext) outerContext, stmt, tableContext, dialect, sessionSpec);
    }


    private MultiUpdateContext(@Nullable StatementContext outerContext, _Update stmt, TableContext tableContext,
                               ArmyParser dialect, SessionSpec sessionSpec) {
        super(outerContext, stmt, tableContext, dialect, sessionSpec);
    }


    @Override
    public String singleTableAliasOf(final SqlField dataField) {
        final String singleTableAlias;
        if (!(dataField instanceof TableField)) {
            //TODO
            throw new UnsupportedOperationException();
        } else if (dataField instanceof FieldMeta) {
            final TableMeta<?> fieldTable;
            fieldTable = ((FieldMeta<?>) dataField).tableMeta();
            if (fieldTable instanceof ChildTableMeta) {
                singleTableAlias = this.childAliasToParentAlias.get(findTableAlias(fieldTable));
                // TableContext no bug,assert success
                assert singleTableAlias != null;
            } else {
                singleTableAlias = findTableAlias(fieldTable);
            }
        } else {
            final TableMeta<?> fieldTable;
            fieldTable = ((QualifiedField<?>) dataField).tableMeta();
            if (fieldTable instanceof ChildTableMeta) {
                singleTableAlias = this.childAliasToParentAlias.get(((QualifiedField<?>) dataField).tableAlias());
                // TableContext no bug,assert success
                assert singleTableAlias != null;
            } else {
                singleTableAlias = ((QualifiedField<?>) dataField).tableAlias();
            }
        }
        return singleTableAlias;
    }


    @Override
    public void appendSetLeftItem(SqlField dataField, @Nullable Expression updateTimePlaceholder) {
        this.multiTableContext.appendSetLeftItem(dataField, updateTimePlaceholder);
    }

    @Override
    public void appendConditionFields() {
        this.multiTableContext.appendConditionFields();
    }

    @Override
    public boolean isAppendedUpdateTime() {
        return this.multiTableContext.isAppendedUpdateTime();
    }


    @Override
    public _UpdateContext parentContext() {
        //multi-table update always null
        return null;
    }


    private String findTableAlias(final TableMeta<?> table) {
        final String safeTableAlias;
        safeTableAlias = this.multiTableContext.tableToSafeAlias.get(table);
        if (safeTableAlias == null) {
            // no bug, never here.
            throw _Exceptions.tableSelfJoin(table);
        }
        if (this.multiTableContext.aliasToTable.get(safeTableAlias) == table) {
            return safeTableAlias;
        }
        String tableAlias = null;
        for (Map.Entry<String, TabularItem> e : this.multiTableContext.aliasToTable.entrySet()) {
            if (e.getValue() == table) {
                tableAlias = e.getKey();
            }
        }
        if (tableAlias == null) {
            // no bug, never here.
            throw new IllegalStateException(String.format("Not found alias of %s", table));
        }
        return tableAlias;
    }


}
