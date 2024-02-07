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

import io.army.criteria.SubStatement;
import io.army.criteria.TableField;
import io.army.criteria.impl.inner._DomainUpdate;
import io.army.criteria.impl.inner._JoinableUpdate;
import io.army.criteria.impl.inner._SingleDml;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.meta.ChildTableMeta;
import io.army.session.SessionSpec;

import javax.annotation.Nullable;

final class SingleJoinableUpdateContext extends SingleJoinableDmlContext implements _SingleUpdateContext {


    static SingleJoinableUpdateContext create(@Nullable _SqlContext outerContext, _SingleUpdate stmt,
                                              ArmyParser parser, SessionSpec sessionSpec) {
        final TableContext tableContext;
        if (stmt instanceof _JoinableUpdate) {
            tableContext = TableContext.forUpdate((_JoinableUpdate) stmt, parser, sessionSpec.visible());
        } else if (stmt instanceof _DomainUpdate && stmt.table() instanceof ChildTableMeta) {
            tableContext = TableContext.forChild((ChildTableMeta<?>) stmt.table(), stmt.tableAlias(), parser);
        } else {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        return new SingleJoinableUpdateContext((StatementContext) outerContext, stmt, tableContext, parser, sessionSpec);
    }

    static SingleJoinableUpdateContext forCte(_SqlContext outerContext, _SingleUpdate stmt) {
        assert stmt instanceof SubStatement;
        final StatementContext context = (StatementContext) outerContext;

        final TableContext tableContext;
        if (stmt instanceof _JoinableUpdate) {
            tableContext = TableContext.forUpdate((_JoinableUpdate) stmt, context.parser, context.visible);
        } else {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        return new SingleJoinableUpdateContext(context, stmt, tableContext, context.parser, context.sessionSpec);
    }


    static SingleJoinableUpdateContext forParent(_SingleUpdate._ChildUpdate stmt, ArmyParser parser, SessionSpec sessionSpec) {
        //TODO
        throw new UnsupportedOperationException();
    }


    static SingleJoinableUpdateContext forChild(_SingleUpdate._ChildUpdate stmt,
                                                SingleJoinableUpdateContext parentContext) {
        //TODO
        throw new UnsupportedOperationException();
    }


    private SingleJoinableUpdateContext(@Nullable StatementContext outerContext, _SingleDml stmt,
                                        TableContext tableContext, ArmyParser parser, SessionSpec sessionSpec) {
        super(outerContext, stmt, tableContext, parser, sessionSpec);
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
