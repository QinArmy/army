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

import io.army.criteria.impl.inner._DomainDelete;
import io.army.session.SessionSpec;

import javax.annotation.Nullable;

final class DomainDeleteContext extends DomainDmlStmtContext implements _SingleDeleteContext {

    static DomainDeleteContext forSingle(@Nullable _SqlContext outerContext, _DomainDelete stmt, ArmyParser parser,
                                         SessionSpec sessionSpec) {
        return new DomainDeleteContext((StatementContext) outerContext, stmt, parser, sessionSpec);
    }

    static DomainDeleteContext forChild(_DomainDelete stmt, DomainDeleteContext parentContext) {
        return new DomainDeleteContext(stmt, parentContext);
    }


    final DomainDeleteContext parentContext;

    private DomainDeleteContext(@Nullable StatementContext outerContext, _DomainDelete stmt,
                                ArmyParser parser, SessionSpec sessionSpec) {
        super(outerContext, stmt, parser, sessionSpec);
        this.parentContext = null;
    }

    private DomainDeleteContext(_DomainDelete stmt, DomainDeleteContext parentContext) {
        super(stmt, parentContext);
        this.parentContext = parentContext;
    }


    @Override
    public _DeleteContext parentContext() {
        return this.parentContext;
    }


}
