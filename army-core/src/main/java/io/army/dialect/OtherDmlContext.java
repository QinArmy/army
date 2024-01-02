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
import io.army.meta.FieldMeta;
import io.army.stmt.SimpleStmt;
import io.army.stmt.StmtType;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.function.Predicate;

final class OtherDmlContext extends StatementContext implements _OtherDmlContext {

    static OtherDmlContext create(@Nullable _SqlContext outerContext,Predicate<FieldMeta<?>> predicate
            , ArmyParser parser,  Visible visible) {
        return new OtherDmlContext((StatementContext) outerContext,predicate,parser, visible);
    }

    static OtherDmlContext forChild(@Nullable _SqlContext outerContext,Predicate<FieldMeta<?>> predicate
            ,OtherDmlContext parentContext) {
        return new OtherDmlContext((StatementContext) outerContext, predicate,parentContext);
    }

    private final OtherDmlContext parentContext;
    private final Predicate<FieldMeta<?>> predicate;


    private OtherDmlContext(@Nullable StatementContext outerContext,Predicate<FieldMeta<?>> predicate
            ,ArmyParser parser, Visible visible) {
        super(outerContext,parser, visible);
        this.predicate = predicate;
        this.parentContext = null;
    }

    private OtherDmlContext(@Nullable StatementContext outerContext, Predicate<FieldMeta<?>> predicate
            , OtherDmlContext parentContext) {
        super(outerContext, parentContext.parser, parentContext.visible);
        this.predicate = predicate;
        this.parentContext = parentContext;
    }

    @Override
    public boolean hasOptimistic() {
        return false;
    }

    @Override
    public StmtType stmtType() {
        return StmtType.UPDATE;
    }

    @Override
    public void appendField(final String tableAlias, final FieldMeta<?> field) {
        throw _Exceptions.unknownColumn(tableAlias, field);
    }

    @Override
    public void appendField(final FieldMeta<?> field) {
        if (!this.predicate.test(field)) {
            throw _Exceptions.unknownColumn(field);
        }
        final StringBuilder sqlBuilder = this.sqlBuilder
                .append(_Constant.SPACE);
        this.parser.safeObjectName(field, sqlBuilder);
    }

    @Override
    public _OtherDmlContext parentContext() {
        return this.parentContext;
    }

    @Override
    public SimpleStmt build() {
        return Stmts.minSimple(this);
    }


}
