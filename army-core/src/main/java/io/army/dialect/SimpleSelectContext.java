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

import io.army.criteria.Query;
import io.army.criteria.SelectStatement;
import io.army.criteria.impl.inner._Query;
import io.army.session.SessionSpec;

import io.army.lang.Nullable;

final class SimpleSelectContext extends MultiTableQueryContext implements  _SelectContext {


    static SimpleSelectContext create(@Nullable _SqlContext outerContext, SelectStatement select, ArmyParser parser,
                                      SessionSpec sessionSpec) {
        final TableContext tableContext;
        tableContext = TableContext.forQuery(((_Query) select).tableBlockList(), parser, sessionSpec.visible());
        return new SimpleSelectContext((StatementContext) outerContext, select, tableContext, parser, sessionSpec);
    }

    static SimpleSelectContext create(final _SqlContext outerCtx, final SelectStatement select) {
        final StatementContext outerContext = (StatementContext) outerCtx;
        final ArmyParser parser = outerContext.parser;

        final TableContext tableContext;
        tableContext = TableContext.forQuery(((_Query) select).tableBlockList(), parser, outerContext.visible);
        return new SimpleSelectContext(outerContext, select, tableContext, parser, outerContext.sessionSpec);
    }


    private SimpleSelectContext(@Nullable StatementContext outerContext, Query query,
                                TableContext tableContext, ArmyParser parser, SessionSpec sessionSpec) {
        super(outerContext, query, tableContext, parser, sessionSpec);
    }




}
