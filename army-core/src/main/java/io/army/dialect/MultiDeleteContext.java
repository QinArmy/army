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

import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Delete;
import io.army.criteria.impl.inner._MultiDelete;
import io.army.criteria.impl.inner._SingleDelete;
import io.army.meta.ChildTableMeta;
import io.army.util._Exceptions;

import javax.annotation.Nullable;

final class MultiDeleteContext extends MultiTableDmlContext implements _MultiDeleteContext {

    static MultiDeleteContext create(@Nullable _SqlContext outerContext, _MultiDelete stmt, ArmyParser dialect
            , Visible visible) {
        final TableContext tableContext;
        tableContext = TableContext.forDelete(stmt, dialect, visible);

        return new MultiDeleteContext((StatementContext) outerContext, stmt, tableContext, dialect, visible);
    }

    static MultiDeleteContext forChild(@Nullable _SqlContext outerContext, _SingleDelete stmt, ArmyParser dialect
            , Visible visible) {
        final TableContext tableContext;
        tableContext = TableContext.forChild((ChildTableMeta<?>) stmt.table(), stmt.tableAlias(), dialect);
        return new MultiDeleteContext((StatementContext) outerContext, stmt, tableContext, dialect, visible);
    }



    private MultiDeleteContext(@Nullable StatementContext outerContext, _Delete stmt, TableContext tableContext
            , ArmyParser dialect, Visible visible) {
        super(outerContext,stmt, tableContext, dialect, visible);
    }


    @Override
    public String parentAlias(final String childAlias) {
        final String parentAlias;
        parentAlias = this.childAliasToParentAlias.get(childAlias);
        if (parentAlias == null) {
            // no bug,never here
            if (this.multiTableContext.aliasToTable.containsKey(childAlias)) {
                //TableContext no bug,never here
                throw new IllegalStateException(String.format("Not found parent alias for %s.", childAlias));
            } else {
                throw _Exceptions.unknownTableAlias(childAlias);
            }
        }
        return parentAlias;
    }

    @Override
    public _DeleteContext parentContext() {
        //multi-delete always null
        return null;
    }



}
