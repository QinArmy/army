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

import io.army.criteria.Selection;
import io.army.criteria.Values;
import io.army.criteria.ValuesQuery;
import io.army.criteria.impl.inner._Selection;
import io.army.criteria.impl.inner._ValuesQuery;
import io.army.meta.FieldMeta;
import io.army.session.SessionSpec;
import io.army.stmt.SimpleStmt;
import io.army.stmt.StmtType;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.List;

final class ValuesContext extends StatementContext implements _ValuesContext {

    static ValuesContext create(@Nullable _SqlContext outerContext, ValuesQuery stmt, ArmyParser dialect, SessionSpec sessionSpec) {
        return new ValuesContext((StatementContext) outerContext, stmt, dialect, sessionSpec);
    }


    private final List<_Selection> selectionList;

    private ValuesContext(@Nullable StatementContext outerContext, ValuesQuery stmt, ArmyParser dialect,
                          SessionSpec sessionSpec) {
        super(outerContext, dialect, sessionSpec);
        if (outerContext == null && stmt instanceof Values) {
            this.selectionList = ((_ValuesQuery) stmt).selectItemList();
        } else {
            this.selectionList = null;
        }
    }


    @Override
    public boolean hasOptimistic() {
        return false;
    }

    @Override
    public StmtType stmtType() {
        return StmtType.QUERY;
    }

    @Override
    public void appendField(String tableAlias, FieldMeta<?> field) {
        throw _Exceptions.unknownColumn(tableAlias, field);
    }

    @Override
    public void appendField(FieldMeta<?> field) {
        throw _Exceptions.unknownColumn(field);
    }


    @Override
    public SimpleStmt build() {
        if (this.selectionList == null) {
            //no bug,never here
            throw nonTopContext();
        }
        return Stmts.queryStmt(this);
    }


    @Override
    public List<? extends Selection> selectionList() {
        return this.selectionList;
    }


}
