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
import io.army.criteria.QualifiedField;
import io.army.criteria.SqlField;
import io.army.criteria.TableField;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner.*;
import io.army.meta.ChildTableMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;
import io.army.modelgen._MetaBridge;
import io.army.util._Exceptions;

import javax.annotation.Nullable;

/**
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@link  DomainDmlStmtContext}</li>
 *         <li>{@link  SingleDmlContext}</li>
 *         <li>{@link  SingleJoinableDmlContext}</li>
 *     </ul>
*/
abstract class SingleTableDmlContext extends NarrowDmlStmtContext implements _SingleTableContext
        , _DmlContext._SetClauseContextSpec {


    final TableMeta<?> domainTable;

    final TableMeta<?> targetTable;

    final String tableAlias;

    final String safeTableAlias;

    final String safeTargetTableName;


    /**
     * <p>
     * For {@link SingleTableMeta}
     *
     */
    SingleTableDmlContext(@Nullable StatementContext outerContext, _SingleDml stmt, ArmyParser parser,
                          Visible visible) {
        super(outerContext, stmt, parser, visible);

        this.domainTable = stmt.table();
        if (this.domainTable instanceof ChildTableMeta) {
            this.targetTable = ((ChildTableMeta<?>) this.domainTable).parentMeta();
        } else {
            this.targetTable = this.domainTable;
        }
        if (stmt instanceof _DomainUpdate || stmt instanceof _DomainDelete) {
            this.tableAlias = _DialectUtils.parentAlias(stmt.tableAlias());
        } else {
            this.tableAlias = stmt.tableAlias();
        }
        this.safeTableAlias = parser.identifier(this.tableAlias);

        if ((stmt instanceof _Update && parser.supportSingleUpdateAlias)
                || (stmt instanceof _Delete && parser.supportSingleDeleteAlias)) {
            this.safeTargetTableName = null;
        } else {
            this.safeTargetTableName = parser.safeObjectName(this.targetTable);
        }

    }


    /**
     * <p>
     * For {@link  ChildTableMeta}
     *
     *
     * @see #decideParentContext(SingleTableDmlContext)
     */
    SingleTableDmlContext(_SingleDml stmt, SingleTableDmlContext parentContext) {
        super(decideParentContext(parentContext), stmt, parentContext.parser, parentContext.visible);

        this.domainTable = stmt.table();
        this.targetTable = this.domainTable;
        this.tableAlias = stmt.tableAlias();
        this.safeTableAlias = this.parser.identifier(this.tableAlias);

        assert this.domainTable instanceof ChildTableMeta;
        assert parentContext.targetTable == ((ChildTableMeta<?>) this.domainTable).parentMeta()
                && parentContext.domainTable == this.domainTable;

        if ((stmt instanceof _Update && parser.supportSingleUpdateAlias)
                || (stmt instanceof _Delete && parser.supportSingleDeleteAlias)) {
            this.safeTargetTableName = null;
        } else {
            this.safeTargetTableName = parser.safeObjectName(this.targetTable);
        }
    }

    @Override
    public final TableMeta<?> domainTable() {
        return this.domainTable;
    }

    @Override
    public final TableMeta<?> targetTable() {
        return this.targetTable;
    }

    @Override
    public final String targetTableAlias() {
        return this.tableAlias;
    }

    @Override
    public final String safeTargetTableAlias() {
        return this.safeTableAlias;
    }

    @Override
    public final void appendSetLeftItem(final SqlField dataField) {
        assert this instanceof _UpdateContext;
        if (!(dataField instanceof TableField)) {
            throw _Exceptions.immutableField(dataField);
        }
        final TableField field = (TableField) dataField;
        final UpdateMode updateMode;
        if (field.tableMeta() != this.targetTable) {
            throw _Exceptions.unknownColumn(field);
        } else if ((updateMode = field.updateMode()) == UpdateMode.IMMUTABLE) {
            throw _Exceptions.immutableField(field);
        } else if (field instanceof QualifiedField
                && !this.tableAlias.equals(((QualifiedField<?>) field).tableAlias())) {
            throw _Exceptions.unknownColumn(field);
        } else if (this.targetTable instanceof SingleTableMeta) {
            final String fieldName = field.fieldName();
            if (_MetaBridge.UPDATE_TIME.equals(fieldName) || _MetaBridge.VERSION.equals(fieldName)) {
                throw _Exceptions.armyManageField(field);
            }
        }

        final StringBuilder sqlBuilder;
        sqlBuilder = this.sqlBuilder.append(_Constant.SPACE);
        if (this.parser.setClauseTableAlias) {
            if (this.safeTargetTableName == null) {
                sqlBuilder.append(this.safeTableAlias);
            } else {
                sqlBuilder.append(this.safeTargetTableName);
            }
            sqlBuilder.append(_Constant.PERIOD);
        }
        this.parser.safeObjectName(field, sqlBuilder);

        switch (updateMode) {
            case ONLY_NULL:
            case ONLY_DEFAULT: {
                if (updateMode == UpdateMode.ONLY_DEFAULT && !this.parser.supportOnlyDefault) {
                    throw _Exceptions.dontSupportOnlyDefault(this.parser.dialect);
                }
                this.onAddConditionField(field);
            }
            break;
            default:
                //no-op
        }
    }


    void onAddConditionField(TableField field) {
        throw new UnsupportedOperationException();
    }


    @Nullable
    private static SingleTableDmlContext decideParentContext(final SingleTableDmlContext parentContext) {
        final SingleTableDmlContext actual;
        if (parentContext.parser.childUpdateMode == ArmyParser.ChildUpdateMode.CTE) {
            actual = parentContext; //same StringBuilder instance
        } else {
            actual = null;
        }
        return actual;
    }


}
