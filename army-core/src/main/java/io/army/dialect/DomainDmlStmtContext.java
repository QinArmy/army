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

import io.army.criteria.impl.inner._Delete;
import io.army.criteria.impl.inner._DomainUpdate;
import io.army.criteria.impl.inner._SingleDml;
import io.army.meta.*;
import io.army.session.SessionSpec;
import io.army.util._Exceptions;

import io.army.lang.Nullable;

abstract class DomainDmlStmtContext extends SingleTableDmlContext implements _SingleTableContext,
        _DmlContext._DomainUpdateSpec {


    private final String safeRelatedAlias;

    private final boolean childCteModeChildFiledSimplyOutput;

    private boolean existsChildFiledInSetClause;


    DomainDmlStmtContext(@Nullable StatementContext outerContext, _SingleDml stmt, ArmyParser parser,
                         SessionSpec sessionSpec) {
        super(outerContext, stmt, parser, sessionSpec);
        if (this.domainTable == this.targetTable) {
            this.safeRelatedAlias = null;
        } else if (this.safeTargetTableName == null) {
            this.safeRelatedAlias = parser.identifier(this.domainTableAlias);
        } else {
            this.safeRelatedAlias = parser.safeObjectName(this.domainTable);
        }

        if (stmt instanceof _Delete) {
            this.childCteModeChildFiledSimplyOutput = parser.childUpdateMode == ArmyParser.ChildUpdateMode.CTE;
        } else {
            this.childCteModeChildFiledSimplyOutput = parser.childUpdateMode == ArmyParser.ChildUpdateMode.CTE
                    && this.domainTable instanceof ChildTableMeta
                    && stmt instanceof _DomainUpdate
                    && ((_DomainUpdate) stmt).childItemPairList().size() > 0;
        }


    }

    DomainDmlStmtContext(_SingleDml stmt, DomainDmlStmtContext parentContext) {
        super(stmt, parentContext);
        if (this.safeTargetTableName == null) {
            this.safeRelatedAlias = parentContext.safeTargetTableAlias;
        } else {
            this.safeRelatedAlias = this.parser.safeObjectName(parentContext.targetTable);
        }
        this.childCteModeChildFiledSimplyOutput = true;
    }

    @Override
    public final void appendField(final String tableAlias, final FieldMeta<?> field) {
        if (!this.domainTableAlias.equals(tableAlias)) {
            throw _Exceptions.unknownColumn(tableAlias, field);
        }
        this.appendField(field);
    }

    @Override
    public final void appendField(final FieldMeta<?> field) {
        final TableMeta<?> fieldTable = field.tableMeta();
        final StringBuilder sqlBuilder = this.sqlBuilder;
        final TableMeta<?> targetTable = this.targetTable;
        if (fieldTable == targetTable || field instanceof PrimaryFieldMeta) {
            if (targetTable instanceof ChildTableMeta) {
                if (fieldTable != targetTable && fieldTable != ((ChildTableMeta<?>) targetTable).parentMeta()) {
                    throw _Exceptions.unknownColumn(field);
                }
            } else if (fieldTable != targetTable && fieldTable != this.domainTable) {
                throw _Exceptions.unknownColumn(field);
            }

            sqlBuilder.append(_Constant.SPACE);
            if (this.safeTargetTableName != null) {
                sqlBuilder.append(this.safeTargetTableName);
            } else {
                sqlBuilder.append(this.safeTargetTableAlias);
            }
            sqlBuilder.append(_Constant.PERIOD);
            this.parser.safeObjectName(field, sqlBuilder);
        } else if (targetTable instanceof ChildTableMeta
                && fieldTable == ((ChildTableMeta<?>) targetTable).parentMeta()) {
            // parent table filed
            if (this.parser.childUpdateMode == ArmyParser.ChildUpdateMode.CTE) {
                assert this.safeRelatedAlias != null;
                sqlBuilder.append(_Constant.SPACE)
                        .append(this.safeRelatedAlias)
                        .append(_Constant.PERIOD);
                this.parser.safeObjectName(field, sqlBuilder);
            } else {
                this.parentColumnFromSubQuery(field);
            }
        } else if (targetTable instanceof ParentTableMeta && fieldTable == this.domainTable) {
            if (this.childCteModeChildFiledSimplyOutput) {
                assert this.safeRelatedAlias != null;
                sqlBuilder.append(_Constant.SPACE)
                        .append(this.safeRelatedAlias)
                        .append(_Constant.PERIOD);
                this.parser.safeObjectName(field, sqlBuilder);
            } else {
                this.childColumnFromSubQuery(field);
            }
            this.existsChildFiledInSetClause = true;
        } else {
            throw _Exceptions.unknownColumn(field);
        }

    }


    @Override
    public final boolean isExistsChildFiledInSetClause() {
        return this.existsChildFiledInSetClause;
    }

    final void parentColumnFromSubQuery(final FieldMeta<?> parentField) {
        final ParentTableMeta<?> parentTable = (ParentTableMeta<?>) parentField.tableMeta();
        final ChildTableMeta<?> childTable = (ChildTableMeta<?>) this.domainTable;
        assert childTable == this.targetTable && childTable.parentMeta() == parentTable;

        final String safeParentAlias = this.safeRelatedAlias;
        assert safeParentAlias != null;

        final ArmyParser parser = this.parser;

        final StringBuilder sqlBuilder = this.sqlBuilder
                //below sub query left bracket
                .append(_Constant.SPACE_LEFT_PAREN)
                .append(_Constant.SPACE_SELECT_SPACE)
                //below target parent column
                .append(safeParentAlias)
                .append(_Constant.PERIOD);

        parser.safeObjectName(parentField, sqlBuilder)
                .append(_Constant.SPACE_FROM_SPACE);

        parser.safeObjectName(parentTable, sqlBuilder);


        if (parser.aliasAfterAs) {
            sqlBuilder.append(_Constant.SPACE_AS_SPACE);
        } else {
            sqlBuilder.append(_Constant.SPACE);
        }
        sqlBuilder.append(safeParentAlias);


        final String safeIdColumnName;
        safeIdColumnName = parser.safeObjectName(parentTable.id());

        //below where clause
        sqlBuilder.append(_Constant.SPACE_WHERE)
                .append(_Constant.SPACE)

                .append(safeParentAlias)
                .append(_Constant.PERIOD)
                .append(safeIdColumnName)

                .append(_Constant.SPACE_EQUAL_SPACE)

                .append(this.safeTargetTableAlias)
                .append(_Constant.PERIOD)
                .append(safeIdColumnName)

                .append(_Constant.SPACE_RIGHT_PAREN);


    } // parentColumnFromSubQuery


    private void childColumnFromSubQuery(final FieldMeta<?> childField) {
        final ChildTableMeta<?> childTable = (ChildTableMeta<?>) childField.tableMeta();
        assert childTable == this.domainTable && childTable.parentMeta() == this.targetTable;

        final String safeChildAlias = this.safeRelatedAlias;
        assert safeChildAlias != null;

        final ArmyParser parser = this.parser;

        final StringBuilder sqlBuilder = this.sqlBuilder;
        //below sub query left bracket
        sqlBuilder.append(_Constant.SPACE_LEFT_PAREN)
                .append(_Constant.SPACE_SELECT_SPACE)
                //below target parent column
                .append(safeChildAlias)
                .append(_Constant.PERIOD);

        parser.safeObjectName(childField, sqlBuilder)
                .append(_Constant.SPACE_FROM_SPACE);

        parser.safeObjectName(childTable, sqlBuilder);

        if (parser.aliasAfterAs) {
            sqlBuilder.append(_Constant.SPACE_AS_SPACE);
        } else {
            sqlBuilder.append(_Constant.SPACE);
        }

        final String safeIdColumnName;
        safeIdColumnName = parser.safeObjectName(childTable.id());

        sqlBuilder.append(safeChildAlias)
                .append(_Constant.SPACE_WHERE)
                .append(_Constant.SPACE)
                .append(safeChildAlias)
                .append(_Constant.PERIOD)
                .append(safeIdColumnName)
                .append(_Constant.SPACE_EQUAL_SPACE)
                .append(this.safeTargetTableAlias)
                .append(_Constant.PERIOD)
                .append(safeIdColumnName)
                .append(_Constant.SPACE_RIGHT_PAREN);


    } // childColumnFromSubQuery


}
