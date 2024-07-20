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

import io.army.criteria.SelectStatement;
import io.army.criteria.Selection;
import io.army.criteria.impl.inner._PrimaryRowSet;
import io.army.criteria.impl.inner._SelectItem;
import io.army.criteria.impl.inner._Statement;
import io.army.meta.FieldMeta;
import io.army.session.SessionSpec;
import io.army.stmt.SimpleStmt;
import io.army.stmt.StmtType;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import io.army.lang.Nullable;
import java.util.List;

final class ParensSelectContext extends BatchSpecStatementContext implements _SelectContext, _ParenRowSetContext {

    static ParensSelectContext create(@Nullable _SqlContext outerContext, SelectStatement select, ArmyParser dialect,
                                      SessionSpec sessionSpec) {
        return new ParensSelectContext((StatementContext) outerContext, select, dialect, sessionSpec);
    }

    private final List<? extends _SelectItem> selectItemList;

    private final _SqlContext outerContext;

    private ParensSelectContext(@Nullable StatementContext outerContext, SelectStatement select, ArmyParser dialect,
                                SessionSpec sessionSpec) {
        super(outerContext, (_Statement) select, dialect, sessionSpec);
        this.outerContext = outerContext;
        this.selectItemList = ((_PrimaryRowSet) select).selectItemList();
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
    public void appendOuterField(String tableAlias, FieldMeta<?> field) {
        throw _Exceptions.unknownColumn(tableAlias, field);
    }

    @Override
    public void appendOuterField(FieldMeta<?> field) {
        throw _Exceptions.unknownColumn(field);
    }

    @Override
    public void appendOuterFieldOnly(FieldMeta<?> field) {
        throw _Exceptions.unknownColumn(field);
    }

    @Override
    public SimpleStmt build() {
        final _SqlContext outerContext = this.outerContext;
        if (outerContext != null) {
            //no bug,never here
            throw new UnsupportedOperationException();
        }
        return Stmts.queryStmt(this);
    }

    @Override
    public List<? extends Selection> selectionList() {
        return _DialectUtils.flatSelectItem(this.selectItemList);
    }


}
