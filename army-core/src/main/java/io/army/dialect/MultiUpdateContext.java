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

import io.army.annotation.UpdateMode;
import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.modelgen._MetaBridge;
import io.army.session.SessionSpec;
import io.army.util._Collections;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

final class MultiUpdateContext extends MultiTableDmlContext implements _MultiUpdateContext {

    static MultiUpdateContext create(@Nullable _SqlContext outerContext, _MultiUpdate statement, ArmyParser dialect,
                                     SessionSpec sessionSpec) {
        final TableContext tableContext;
        tableContext = TableContext.forUpdate(statement, dialect, sessionSpec.visible());
        return new MultiUpdateContext((StatementContext) outerContext, statement, tableContext, dialect, sessionSpec);
    }

    static MultiUpdateContext forChild(@Nullable _SqlContext outerContext, _SingleUpdate stmt, ArmyParser dialect
            , SessionSpec sessionSpec) {
        final TableContext tableContext;
        tableContext = TableContext.forChild((ChildTableMeta<?>) stmt.table(), stmt.tableAlias(), dialect);
        return new MultiUpdateContext((StatementContext) outerContext, stmt, tableContext, dialect, sessionSpec);
    }


    private List<SqlField> conditionFieldList;

    private boolean appendedUpdateTime;

    private MultiUpdateContext(@Nullable StatementContext outerContext, _Update stmt, TableContext tableContext,
                               ArmyParser dialect, SessionSpec sessionSpec) {
        super(outerContext, stmt, tableContext, dialect, sessionSpec);
    }


    @Override
    public String singleTableAliasOf(final SqlField dataField) {
        final String singleTableAlias;
        if (!(dataField instanceof TableField)) {
            //TODO
            throw new UnsupportedOperationException();
        } else if (dataField instanceof FieldMeta) {
            final TableMeta<?> fieldTable;
            fieldTable = ((FieldMeta<?>) dataField).tableMeta();
            if (fieldTable instanceof ChildTableMeta) {
                singleTableAlias = this.childAliasToParentAlias.get(findTableAlias(fieldTable));
                // TableContext no bug,assert success
                assert singleTableAlias != null;
            } else {
                singleTableAlias = findTableAlias(fieldTable);
            }
        } else {
            final TableMeta<?> fieldTable;
            fieldTable = ((QualifiedField<?>) dataField).tableMeta();
            if (fieldTable instanceof ChildTableMeta) {
                singleTableAlias = this.childAliasToParentAlias.get(((QualifiedField<?>) dataField).tableAlias());
                // TableContext no bug,assert success
                assert singleTableAlias != null;
            } else {
                singleTableAlias = ((QualifiedField<?>) dataField).tableAlias();
            }
        }
        return singleTableAlias;
    }


    @Override
    public boolean isAppendedUpdateTime() {
        return this.appendedUpdateTime;
    }

    @Override
    public void appendSetLeftItem(final SqlField dataField, final @Nullable Expression updateTimePlaceholder) {
        final UpdateMode updateMode;
        if (dataField instanceof TableField) {
            updateMode = ((TableField) dataField).updateMode();
        } else if (this.parser.supportUpdateDerivedField) {
            final TableField f;
            f = ((_Selection) dataField).tableField();
            if (f == null) {
                throw _Exceptions.immutableField(dataField);
            }
            updateMode = f.updateMode();
        } else {
            throw _Exceptions.immutableField(dataField);
        }

        if (updateMode == UpdateMode.IMMUTABLE) {
            throw _Exceptions.immutableField(dataField);
        }

        final String fieldName = dataField.fieldName();

        if (updateTimePlaceholder == null && dataField instanceof TableField && _MetaBridge.UPDATE_TIME.equals(fieldName)) {
            throw _Exceptions.armyManageField((TableField) dataField);
        }

        final StringBuilder sqlBuilder = this.sqlBuilder;
        if (!(dataField instanceof TableField)) {
            final DerivedField field = (DerivedField) dataField;
            final String tableAlias = field.tableAlias();
            final TabularItem tableItem = this.multiTableContext.aliasToTable.get(tableAlias);
            if (!(tableItem instanceof DerivedTable)
                    || ((_DerivedTable) tableItem).refSelection(fieldName) == null) {
                throw _Exceptions.unknownColumn(field);
            }
            final String safeTableAlias;
            safeTableAlias = this.multiTableContext.getAliasToSafeAlias().get(tableAlias);
            assert safeTableAlias != null;
            sqlBuilder.append(_Constant.SPACE)
                    .append(safeTableAlias)
                    .append(_Constant.PERIOD);
            this.parser.identifier(fieldName, sqlBuilder);
        } else if (dataField instanceof FieldMeta) {
            final FieldMeta<?> field = (FieldMeta<?>) dataField;
            final String safeTableAlias;
            safeTableAlias = this.multiTableContext.tableToSafeAlias.get(field.tableMeta());
            if (safeTableAlias == null) {
                //self-join
                throw _Exceptions.selfJoinNonQualifiedField(field);
            }
            sqlBuilder.append(_Constant.SPACE)
                    .append(safeTableAlias)
                    .append(_Constant.PERIOD);
            this.parser.safeObjectName(field, sqlBuilder);
        } else if (dataField instanceof QualifiedField) {
            final QualifiedField<?> field = (QualifiedField<?>) dataField;
            final String tableAlias = field.tableAlias();
            if (this.multiTableContext.aliasToTable.get(tableAlias) != field.tableMeta()) {
                throw _Exceptions.unknownColumn(field);
            }
            final String safeTableAlias;
            safeTableAlias = this.multiTableContext.getAliasToSafeAlias().get(tableAlias);
            assert safeTableAlias != null;
            sqlBuilder
                    .append(_Constant.SPACE)
                    .append(safeTableAlias)
                    .append(_Constant.PERIOD);
            this.parser.safeObjectName(field, sqlBuilder);
        } else {
            throw _Exceptions.immutableField(dataField);
        }

        switch (updateMode) {
            case ONLY_NULL:
            case ONLY_DEFAULT: {
                if (updateMode == UpdateMode.ONLY_DEFAULT && !this.parser.isSupportOnlyDefault()) {
                    throw _Exceptions.dontSupportOnlyDefault(this.parser.dialect());
                }
                List<SqlField> conditionFieldList = this.conditionFieldList;
                if (conditionFieldList == null) {
                    conditionFieldList = _Collections.arrayList();
                    this.conditionFieldList = conditionFieldList;
                }
                conditionFieldList.add(dataField);
            }
            break;
            default:
                //no-op
        }

        if (updateTimePlaceholder != null) {
            this.appendedUpdateTime = true;
            if (dataField instanceof FieldMeta) {
                appendUpdateTimePlaceholder((FieldMeta<?>) dataField, updateTimePlaceholder);
            } else if (dataField instanceof QualifiedField) {
                appendUpdateTimePlaceholder(((QualifiedField<?>) dataField).fieldMeta(), updateTimePlaceholder);
            } else {
                // no bug,never here
                throw _Exceptions.illegalExpression(dataField);
            }
        }

    }

    @Override
    public void appendConditionFields() {
        final List<SqlField> conditionFieldList = this.conditionFieldList;
        if (conditionFieldList == null || conditionFieldList.size() == 0) {
            return;
        }
        final ArmyParser dialect = this.parser;
        final StringBuilder sqlBuilder = this.sqlBuilder;
        String safeTableAlias, objectName;
        UpdateMode updateMode;
        TableField tableField;
        for (SqlField field : conditionFieldList) {

            if (field instanceof FieldMeta) {
                safeTableAlias = this.multiTableContext.tableToSafeAlias.get(((FieldMeta<?>) field).tableMeta());
            } else if (field instanceof QualifiedField) {
                safeTableAlias = this.multiTableContext.getAliasToSafeAlias()
                        .get(((QualifiedField<?>) field).tableAlias());
            } else {
                safeTableAlias = this.multiTableContext.getAliasToSafeAlias()
                        .get(((DerivedField) field).tableAlias());
            }
            assert safeTableAlias != null;

            sqlBuilder.append(_Constant.SPACE_AND_SPACE)
                    .append(safeTableAlias)
                    .append(_Constant.PERIOD);

            if (field instanceof TableField) {
                objectName = dialect.safeObjectName((TableField) field);
                updateMode = ((TableField) field).updateMode();
            } else {
                objectName = dialect.identifier(field.fieldName());
                tableField = ((_Selection) field).tableField();
                assert tableField != null;
                updateMode = tableField.updateMode();
            }
            sqlBuilder.append(objectName);
            switch (updateMode) {
                case ONLY_NULL:
                    sqlBuilder.append(_Constant.SPACE_IS_NULL);
                    break;
                case ONLY_DEFAULT: {
                    sqlBuilder.append(_Constant.SPACE)
                            .append(dialect.defaultFuncName())
                            .append(_Constant.SPACE_LEFT_PAREN)
                            .append(_Constant.SPACE)
                            .append(safeTableAlias)
                            .append(_Constant.PERIOD)
                            .append(objectName);
                    sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(updateMode);

            }

        }
    }


    @Override
    public _UpdateContext parentContext() {
        //multi-table update always null
        return null;
    }


    private String findTableAlias(final TableMeta<?> table) {
        final String safeTableAlias;
        safeTableAlias = this.multiTableContext.tableToSafeAlias.get(table);
        if (safeTableAlias == null) {
            // no bug, never here.
            throw _Exceptions.tableSelfJoin(table);
        }
        if (this.multiTableContext.aliasToTable.get(safeTableAlias) == table) {
            return safeTableAlias;
        }
        String tableAlias = null;
        for (Map.Entry<String, TabularItem> e : this.multiTableContext.aliasToTable.entrySet()) {
            if (e.getValue() == table) {
                tableAlias = e.getKey();
            }
        }
        if (tableAlias == null) {
            // no bug, never here.
            throw new IllegalStateException(String.format("Not found alias of %s", table));
        }
        return tableAlias;
    }


}
