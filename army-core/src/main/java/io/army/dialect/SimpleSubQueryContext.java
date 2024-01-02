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

import io.army.criteria.SubQuery;
import io.army.criteria.impl.inner._Query;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

import javax.annotation.Nullable;

final class SimpleSubQueryContext extends MultiTableQueryContext implements _SubQueryContext {


    static SimpleSubQueryContext forSimple(final _SqlContext outerCtx, final SubQuery query) {
        final StatementContext outerContext = (StatementContext) outerCtx;
        final ArmyParser parser = outerContext.parser;

        final TableContext tableContext;
        tableContext = TableContext.forQuery(((_Query) query).tableBlockList(), parser, outerContext.visible);
        return new SimpleSubQueryContext(outerContext, query, tableContext);
    }

    private final StatementContext outerContext;

    private SimpleSubQueryContext(StatementContext outerContext, SubQuery subQuery, TableContext tableContext) {
        super(outerContext, subQuery, tableContext, outerContext.parser, outerContext.visible);
        this.outerContext = outerContext;
    }


    @Override
    public void appendThisField(final String tableAlias, final FieldMeta<?> field) {
        if (this.multiTableContext.aliasToTable.get(tableAlias) != field.tableMeta()) {
            throw _Exceptions.unknownColumn(tableAlias, field);
        }
        this.multiTableContext.appendSafeField(tableAlias, field);
    }

    @Override
    public void appendThisField(final FieldMeta<?> field) {
        final TableMeta<?> fieldTable = field.tableMeta();
        final String safeTableAlias;
        safeTableAlias = this.multiTableContext.tableToSafeAlias.get(fieldTable);
        if (safeTableAlias != null) {
            final StringBuilder sqlBuilder = this.sqlBuilder
                    .append(_Constant.SPACE)
                    .append(safeTableAlias)
                    .append(_Constant.PERIOD);
            this.parser.safeObjectName(field, sqlBuilder);
        } else if (this.multiTableContext.aliasToTable.containsValue(fieldTable)) {
            throw _Exceptions.selfJoinNonQualifiedField(field);
        } else {
            throw _Exceptions.unknownColumn(null, field);
        }
    }

    @Override
    public void appendThisFieldOnly(final FieldMeta<?> field) {
        final TableMeta<?> fieldTable = field.tableMeta();
        if (this.multiTableContext.tableToSafeAlias.get(fieldTable) != null) {
            this.parser.safeObjectName(field, this.sqlBuilder);
        } else if (this.multiTableContext.aliasToTable.containsValue(fieldTable)) {
            throw _Exceptions.selfJoinNonQualifiedField(field);
        } else {
            throw _Exceptions.unknownColumn(null, field);
        }
    }

    @Override
    void appendOuterField(final @Nullable String tableAlias, final FieldMeta<?> field) {
        final StatementContext outerContext = this.outerContext;
        if (outerContext instanceof _ParenRowSetContext) {
            if (tableAlias == null) {
                ((_ParenRowSetContext) outerContext).appendOuterField(field);
            } else {
                ((_ParenRowSetContext) outerContext).appendOuterField(tableAlias, field);
            }
        } else if (outerContext instanceof _SubQueryContext) {
            if (tableAlias == null) {
                // TODO fix : 有可能在 select 子句中引入 外部 field
                outerContext.appendField(field);
            } else {
                outerContext.appendField(tableAlias, field);
            }
        } else if (tableAlias != null) {
            outerContext.appendField(tableAlias, field);
        } else if (outerContext instanceof _DmlContext._SingleTableContextSpec) {
            ((_DmlContext._SingleTableContextSpec) outerContext).appendFieldFromSub(field);
        } else {
            outerContext.appendField(field);
        }

    }

    @Override
    void appendOuterFieldOnly(final FieldMeta<?> field) {
        final StatementContext outerContext = this.outerContext;
        if (outerContext instanceof _ParenRowSetContext) {
            ((_ParenRowSetContext) outerContext).appendOuterFieldOnly(field);
        } else if (outerContext instanceof _SubQueryContext) {
            ((_SubQueryContext) outerContext).appendThisFieldOnly(field);
        } else if (outerContext instanceof _DmlContext._SingleTableContextSpec) {
            ((_DmlContext._SingleTableContextSpec) outerContext).appendFieldOnlyFromSub(field);
        } else {
            outerContext.appendFieldOnly(field);
        }

    }


}
