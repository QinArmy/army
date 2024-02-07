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
import io.army.criteria.impl.inner._DomainDelete;
import io.army.criteria.impl.inner._JoinableDelete;
import io.army.criteria.impl.inner._SingleDelete;
import io.army.meta.ChildTableMeta;
import io.army.session.SessionSpec;

import javax.annotation.Nullable;

final class SingleJoinableDeleteContext extends SingleJoinableDmlContext implements _SingleDeleteContext {


    static SingleJoinableDeleteContext create(@Nullable _SqlContext outerContext, _SingleDelete stmt,
                                              ArmyParser parser, SessionSpec sessionSpec) {
        final TableContext tableContext;
        if (stmt instanceof _JoinableDelete) {
            tableContext = TableContext.forDelete((_JoinableDelete) stmt, parser, sessionSpec.visible());
        } else if (stmt instanceof _DomainDelete && stmt.table() instanceof ChildTableMeta) {
            tableContext = TableContext.forChild((ChildTableMeta<?>) stmt.table(), stmt.tableAlias(), parser);
        } else {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        return new SingleJoinableDeleteContext((StatementContext) outerContext, stmt, tableContext, parser, sessionSpec);
    }

    static SingleJoinableDeleteContext forCte(_SqlContext outerContext, _SingleDelete stmt) {
        assert stmt instanceof SubStatement;
        final StatementContext context = (StatementContext) outerContext;
        final TableContext tableContext;
        if (stmt instanceof _JoinableDelete) {
            tableContext = TableContext.forDelete((_JoinableDelete) stmt, context.parser, context.visible);
        } else {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        return new SingleJoinableDeleteContext(context, stmt, tableContext, context.parser, context.sessionSpec);
    }


    static SingleJoinableDeleteContext forParent(_SingleDelete._ChildDelete stmt, ArmyParser parser,
                                                 SessionSpec sessionSpec) {
        //TODO
        throw new UnsupportedOperationException();
    }


    static SingleJoinableDeleteContext forChild(_SingleDelete._ChildDelete stmt,
                                                SingleJoinableDeleteContext parentContext) {
        //TODO
        throw new UnsupportedOperationException();
    }


    final SingleJoinableDeleteContext parentContext;


    private SingleJoinableDeleteContext(@Nullable StatementContext outerContext, _SingleDelete stmt,
                                        TableContext tableContext, ArmyParser parser, SessionSpec sessionSpec) {
        super(outerContext, stmt, tableContext, parser, sessionSpec);
        this.parentContext = null;
    }


    @Override
    public _DeleteContext parentContext() {
        return this.parentContext;
    }


}
