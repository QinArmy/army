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

import io.army.criteria.impl.inner._SingleDelete;
import io.army.session.SessionSpec;

import io.army.lang.Nullable;

final class SingleDeleteContext extends SingleDmlContext implements _SingleDeleteContext {


    static SingleDeleteContext create(@Nullable _SqlContext outerContext, _SingleDelete stmt,
                                      ArmyParser dialect, SessionSpec sessionSpec) {
        return new SingleDeleteContext((StatementContext) outerContext, stmt, dialect, sessionSpec);
    }

    static SingleDeleteContext forParent(_SingleDelete._ChildDelete stmt, ArmyParser dialect, SessionSpec sessionSpec) {
        return new SingleDeleteContext(null, stmt, dialect, sessionSpec);
    }

    static SingleDeleteContext forChild(_SingleDelete._ChildDelete stmt, SingleDeleteContext parentContext) {
        return new SingleDeleteContext(stmt, parentContext);
    }


    final SingleDeleteContext parentContext;

    private SingleDeleteContext(@Nullable StatementContext outerContext, _SingleDelete dml,
                                ArmyParser dialect, SessionSpec sessionSpec) {
        super(outerContext, dml, dialect, sessionSpec);
        this.parentContext = null;
    }

    private SingleDeleteContext(_SingleDelete stmt, SingleDeleteContext parentContext) {
        super(stmt, parentContext);
        this.parentContext = parentContext;
    }


    @Override
    public _DeleteContext parentContext() {
        return this.parentContext;
    }


}
