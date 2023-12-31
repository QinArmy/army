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

import io.army.criteria.TableField;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._DomainUpdate;
import io.army.criteria.impl.inner._JoinableUpdate;
import io.army.criteria.impl.inner._SingleDml;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.meta.ChildTableMeta;

import javax.annotation.Nullable;

final class SingleJoinableUpdateContext extends SingleJoinableDmlContext implements _SingleUpdateContext {


    static SingleJoinableUpdateContext create(@Nullable _SqlContext outerContext, _SingleUpdate stmt,
                                              ArmyParser parser, Visible visible) {
        final TableContext tableContext;
        if (stmt instanceof _JoinableUpdate) {
            tableContext = TableContext.forUpdate((_JoinableUpdate) stmt, parser, visible);
        } else if (stmt instanceof _DomainUpdate && stmt.table() instanceof ChildTableMeta) {
            tableContext = TableContext.forChild((ChildTableMeta<?>) stmt.table(), stmt.tableAlias(), parser);
        } else {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        return new SingleJoinableUpdateContext((StatementContext) outerContext, stmt, tableContext, parser, visible);
    }

    static SingleJoinableUpdateContext forCte(SingleJoinableUpdateContext outerContext, _SingleUpdate stmt) {

        final TableContext tableContext;
        if (stmt instanceof _JoinableUpdate) {
            tableContext = TableContext.forUpdate((_JoinableUpdate) stmt, outerContext.parser, outerContext.visible);
        } else if (stmt instanceof _DomainUpdate && stmt.table() instanceof ChildTableMeta) {
            tableContext = TableContext.forChild((ChildTableMeta<?>) stmt.table(), stmt.tableAlias(), outerContext.parser);
        } else {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        return new SingleJoinableUpdateContext(outerContext, stmt, tableContext, outerContext.parser, outerContext.visible);
    }


    static SingleJoinableUpdateContext forParent(_SingleUpdate._ChildUpdate stmt, ArmyParser parser
            , Visible visible) {
        //TODO
        throw new UnsupportedOperationException();
    }


    static SingleJoinableUpdateContext forChild(_SingleUpdate._ChildUpdate stmt
            , SingleJoinableUpdateContext parentContext) {
        //TODO
        throw new UnsupportedOperationException();
    }


    private SingleJoinableUpdateContext(@Nullable StatementContext outerContext, _SingleDml stmt,
                                        TableContext tableContext, ArmyParser parser, Visible visible) {
        super(outerContext, stmt, tableContext, parser, visible);
    }

    private SingleJoinableUpdateContext(_SingleUpdate stmt, SingleJoinableUpdateContext parentOrOuterContext,
                                        TableContext tableContext) {
        super(stmt, parentOrOuterContext, tableContext);
    }


    @Override
    public _UpdateContext parentContext() {
        // null
        return null;
    }

    @Override
    public void appendConditionFields() {

    }


    @Override
    void onAddConditionField(TableField field) {
        super.onAddConditionField(field);
    }


}
