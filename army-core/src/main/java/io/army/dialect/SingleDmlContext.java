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
import io.army.criteria.impl.inner._SingleDml;
import io.army.meta.FieldMeta;
import io.army.util._Exceptions;

import javax.annotation.Nullable;

abstract class SingleDmlContext extends SingleTableDmlContext {


    SingleDmlContext(@Nullable StatementContext outerContext, _SingleDml stmt, ArmyParser parser, Visible visible) {
        super(outerContext, stmt, parser, visible);
    }

    SingleDmlContext(_SingleDml stmt, SingleTableDmlContext parentContext) {
        super(stmt, parentContext);
    }


    @Override
    public final void appendField(final String tableAlias, final FieldMeta<?> field) {
        if (this.tableAlias.equals(tableAlias)) {
            throw _Exceptions.unknownColumn(tableAlias, field);
        }
        this.appendField(field);
    }

    @Override
    public final void appendField(final FieldMeta<?> field) {
        if (field.tableMeta() != this.targetTable) {
            throw _Exceptions.unknownColumn(field);
        }
        final StringBuilder sqlBuilder = this.sqlBuilder;
        sqlBuilder.append(_Constant.SPACE);
        if (this.safeTargetTableName == null) {
            sqlBuilder.append(this.safeTableAlias);
        } else {
            sqlBuilder.append(this.safeTargetTableName);
        }
        sqlBuilder.append(_Constant.PERIOD);
        this.parser.safeObjectName(field, sqlBuilder);

    }


}
