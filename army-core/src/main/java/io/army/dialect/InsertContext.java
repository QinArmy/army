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

import io.army.annotation.GeneratorType;
import io.army.annotation.UpdateMode;
import io.army.bean.ObjectAccessException;
import io.army.bean.ReadWrapper;
import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.env.EscapeMode;
import io.army.mapping.MappingEnv;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.session.SessionSpec;
import io.army.stmt.*;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._TimeUtils;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.function.Predicate;

abstract class InsertContext extends StatementContext
        implements _InsertContext,
        _DmlContext._SetClauseContextSpec,
        _InsertContext._ValueSyntaxSpec,
        _InsertContext._AssignmentsSpec,
        _InsertContext._QuerySyntaxSpec,
        _DmlContext._SingleTableContextSpec,
        InsertStmtParams,
        SelectItemListContext {

    final _SqlContext outerContext;

    final InsertContext parentContext;

    final boolean twoStmtMode;

    private final boolean twoStmtQuery;

    final TableMeta<?> insertTable;

    final boolean migration;

    final LiteralMode literalMode;

    final List<FieldMeta<?>> fieldList;

    /**
     * nullable map
     */
    final Map<FieldMeta<?>, Boolean> fieldMap;

    private final boolean joinableInsertStmt;

    private final String tableAlias;

    private final String safeTableAlias;

    private final String rowAlias;

    private final String safeRowAlias;

    private final String safeTableName;

    final boolean hasConflictClause;

    final boolean conflictPredicateClause;

    final List<? extends _SelectItem> returningList;

    final List<? extends Selection> returnSelectionList;

    /**
     * {@link #insertTable} instanceof {@link  SingleTableMeta} and  dialect support returning clause nad generated key.
     */
    final PrimaryFieldMeta<?> returnId;

    /**
     * @see #returnId
     */
    final int idSelectionIndex;


    private final boolean appendReturningClause;

    /**
     * @see #twoStmtQuery
     */
    private final int maxColumnSize;

    private boolean columnListClauseEnd;

    private boolean valuesClauseEnd;

    private int outputColumnSize;

    private List<FieldMeta<?>> conditionFieldList;

    private boolean inSetClause;

    /**
     * @see #outputFieldTableAlias(boolean)
     */
    private boolean outputFieldTableAlias;

    private boolean appendedUpdateTime;


    /**
     * <p>
     * For {@link  io.army.meta.SingleTableMeta}
     */
    InsertContext(@Nullable StatementContext outerContext, final _Insert domainStmt,
                  ArmyParser parser, SessionSpec sessionSpec) {
        super(outerContext, parser, sessionSpec);
        this.outerContext = outerContext;
        this.parentContext = null;
        final _Insert targetStmt;
        if (domainStmt instanceof _Insert._ChildInsert) {
            targetStmt = ((_Insert._ChildInsert) domainStmt).parentStmt();
            this.twoStmtMode = true;
            this.twoStmtQuery = domainStmt instanceof _ReturningDml && targetStmt instanceof _ReturningDml;
        } else {
            targetStmt = domainStmt;
            this.twoStmtQuery = this.twoStmtMode = false;
        }

        this.joinableInsertStmt = targetStmt instanceof _Insert._JoinableInsert;

        this.insertTable = targetStmt.table();
        assert this.insertTable instanceof SingleTableMeta || parser.childUpdateMode == ArmyParser.ChildUpdateMode.CTE;

        if (targetStmt instanceof _Insert._InsertOption) {
            final _Insert._InsertOption option = (_Insert._InsertOption) targetStmt;
            this.literalMode = option.literalMode();
            this.migration = targetStmt instanceof _Insert._QueryInsert || option.isMigration();
        } else {
            this.migration = targetStmt instanceof _Insert._QueryInsert;
            this.literalMode = LiteralMode.DEFAULT;
        }

        this.tableAlias = targetStmt.tableAlias();
        if (this.tableAlias == null) {
            this.safeTableAlias = null;
        } else {
            this.safeTableAlias = this.parser.identifier(this.tableAlias);
        }

        if (targetStmt instanceof _Insert._SupportConflictClauseSpec) {
            final _Insert._SupportConflictClauseSpec spec = (_Insert._SupportConflictClauseSpec) targetStmt;
            this.hasConflictClause = spec.hasConflictAction();
            this.rowAlias = parser.supportRowAlias ? spec.rowAlias() : null;
            this.safeRowAlias = this.rowAlias == null ? null : this.parser.identifier(this.rowAlias);
            this.safeTableName = this.hasConflictClause ? this.parser.safeObjectName(this.insertTable) : null;
            this.conflictPredicateClause = targetStmt instanceof _Insert._ConflictActionPredicateClauseSpec;
        } else {
            this.hasConflictClause = false;
            this.safeTableName = this.safeRowAlias = this.rowAlias = null;
            this.conflictPredicateClause = false;
        }
        final List<FieldMeta<?>> fieldList;
        if (targetStmt instanceof _Insert._ColumnListInsert) {
            fieldList = ((_Insert._ColumnListInsert) targetStmt).fieldList();
        } else {
            fieldList = null;
        }
        if (fieldList != null && fieldList.size() > 0) {
            this.fieldList = fieldList;
        } else if (targetStmt instanceof _Insert._AssignmentInsert) {
            final Map<FieldMeta<?>, _Expression> fieldMap;
            fieldMap = ((_Insert._AssignmentInsert) targetStmt).assignmentMap();
            this.fieldList = createNonChildFieldList((SingleTableMeta<?>) this.insertTable, fieldMap::containsKey);
        } else {
            assert !(targetStmt instanceof _Insert._QueryInsert);
            this.fieldList = _DialectUtils.castFieldList(this.insertTable);
        }


        final PrimaryFieldMeta<?> idField = this.insertTable.id();
        final boolean needReturnId, cannotReturnId;
        if (!(!this.migration
                && targetStmt instanceof PrimaryStatement
                && idField.generatorType() == GeneratorType.POST)) {
            needReturnId = false;
        } else if (targetStmt instanceof _Insert._QueryInsert) {
            needReturnId = false;
        } else if (domainStmt instanceof _Insert._ChildInsert) {
            needReturnId = true;
        } else if (targetStmt instanceof _Insert._DomainInsert) {
            needReturnId = !((_Insert._DomainInsert) targetStmt).isIgnoreReturnIds();
        } else {
            // here , just for ResultStates.lastInsertedId() of JDBC executor;
            needReturnId = parser.supportLastInsertedId
                    && !(targetStmt instanceof _ReturningDml)
                    && !this.hasConflictClause;
        }

        cannotReturnId = this.hasConflictClause
                && targetStmt.insertRowCount() > 1
                && (!(targetStmt instanceof _Statement._ReturningListSpec) || ((_Insert._SupportConflictClauseSpec) targetStmt).isIgnorableConflict());

        if (needReturnId && cannotReturnId) {
            throw _Exceptions.cannotReturnPostId(domainStmt);
        }

        if (this.hasConflictClause
                && this.rowAlias != null
                && !((_Insert._SupportConflictClauseSpec) targetStmt).isDoNothing()
                && this.fieldList.size() < this.insertTable.fieldList().size()) {
            // for example : MySQL conflict clause , column list less than table field list size
            this.fieldMap = createFieldMap(targetStmt, this.fieldList);
        } else {
            this.fieldMap = null;
        }

        if (targetStmt instanceof _ReturningDml) {
            this.returningList = ((_ReturningDml) targetStmt).returningList();
            this.returnSelectionList = ((_ReturningDml) targetStmt).flatSelectItem();
            this.returnId = needReturnId ? idField : null;
            if (needReturnId || this.twoStmtQuery) {
                this.idSelectionIndex = returnIdSelection(parser, idField, this.returnSelectionList);
            } else {
                this.idSelectionIndex = -1;
            }
            this.appendReturningClause = false;
        } else if (needReturnId) {
            this.returningList = _Collections.emptyList();
            if (parser.supportReturningClause) { // here don't use io.army.criteria.impl.inner._Statement._ReturningListSpec, because of standard insert
                this.returnSelectionList = _Collections.singletonList(idField);
                this.idSelectionIndex = 0;
                this.appendReturningClause = true;
            } else {
                this.appendReturningClause = false;
                this.idSelectionIndex = -1;
                this.returnSelectionList = Collections.emptyList();
            }
            this.returnId = idField;
        } else {
            this.returningList = _Collections.emptyList();
            this.returnSelectionList = _Collections.emptyList();
            this.returnId = null;
            this.idSelectionIndex = -1;
            this.appendReturningClause = false;
        }

        if (this.twoStmtQuery) {
            this.maxColumnSize = Math.max(this.returnSelectionList.size(),
                    _DialectUtils.flatSelectItem(((_ReturningDml) domainStmt).returningList()).size());
        } else {
            this.maxColumnSize = this.returnSelectionList.size();
        }

    }


    /**
     * <p>
     * For {@link  io.army.meta.ChildTableMeta}
     */
    InsertContext(@Nullable StatementContext outerContext, final _Insert._ChildInsert stmt,
                  final InsertContext parentContext) {
        super(outerContext, parentContext.parser, parentContext.sessionSpec);
        this.outerContext = outerContext;
        this.parentContext = parentContext;
        this.twoStmtMode = parentContext.twoStmtMode;
        this.twoStmtQuery = parentContext.twoStmtQuery;
        this.insertTable = stmt.table();

        this.joinableInsertStmt = stmt instanceof _Insert._JoinableInsert;

        if (stmt instanceof _Insert._InsertOption) {
            final _Insert._InsertOption option = (_Insert._InsertOption) stmt;
            this.literalMode = option.literalMode();
            this.migration = stmt instanceof _Insert._QueryInsert || option.isMigration();
        } else {
            this.migration = stmt instanceof _Insert._QueryInsert;
            this.literalMode = LiteralMode.DEFAULT;
        }
        assert this.insertTable instanceof ChildTableMeta
                && this.migration == parentContext.migration
                && this.literalMode == parentContext.literalMode
                && ((ChildTableMeta<?>) this.insertTable).parentMeta() == parentContext.insertTable;

        this.tableAlias = stmt.tableAlias();
        if (this.tableAlias == null) {
            this.safeTableAlias = null;
        } else {
            this.safeTableAlias = this.parser.identifier(this.tableAlias);
        }

        if (stmt instanceof _Insert._SupportConflictClauseSpec) {
            final _Insert._SupportConflictClauseSpec spec = (_Insert._SupportConflictClauseSpec) stmt;
            this.hasConflictClause = spec.hasConflictAction();
            this.rowAlias = this.parser.supportRowAlias ? spec.rowAlias() : null;
            this.safeRowAlias = this.rowAlias == null ? null : this.parser.identifier(this.rowAlias);
            this.safeTableName = this.hasConflictClause ? this.parser.safeObjectName(this.insertTable) : null;
            this.conflictPredicateClause = stmt instanceof _Insert._ConflictActionPredicateClauseSpec;
        } else {
            this.hasConflictClause = false;
            this.safeTableName = this.safeRowAlias = this.rowAlias = null;
            this.conflictPredicateClause = false;
        }
        final List<FieldMeta<?>> fieldList;
        if (stmt instanceof _Insert._ColumnListInsert) {
            fieldList = ((_Insert._ColumnListInsert) stmt).fieldList();
        } else {
            fieldList = null;
        }
        if (fieldList != null && fieldList.size() > 0) {
            this.fieldList = fieldList;
        } else if (stmt instanceof _Insert._AssignmentInsert) {
            this.fieldList = createChildFieldList((ChildTableMeta<?>) this.insertTable);
        } else {
            assert !(stmt instanceof _Insert._QueryInsert);
            this.fieldList = _DialectUtils.castFieldList(this.insertTable);
        }

        if (this.hasConflictClause
                && this.rowAlias != null
                && !((_Insert._SupportConflictClauseSpec) stmt).isDoNothing()
                && this.fieldList.size() < this.insertTable.fieldList().size()) {
            // for example : MySQL conflict clause , column list less than table field list size
            this.fieldMap = createFieldMap(stmt, this.fieldList);
        } else {
            this.fieldMap = null;
        }

        if (stmt instanceof _ReturningDml) {
            this.returningList = ((_ReturningDml) stmt).returningList();
            this.returnSelectionList = _DialectUtils.flatSelectItem(this.returningList);
        } else {
            this.returningList = _Collections.emptyList();
            this.returnSelectionList = _Collections.emptyList();
        }

        if (this.twoStmtQuery) {
            this.idSelectionIndex = returnIdSelection(this.parser, this.insertTable.id(), this.returnSelectionList);
        } else {
            this.idSelectionIndex = -1;
        }
        this.returnId = null;
        this.appendReturningClause = false;
        this.maxColumnSize = parentContext.maxColumnSize;
    }

    @Override
    public final boolean hasOptimistic() {
        return false;
    }

    @Override
    public final StmtType stmtType() {
        return this.returnId == null ? StmtType.UPDATE : StmtType.INSERT;
    }

    @Override
    public final boolean isTwoStmtQuery() {
        return this.twoStmtQuery;
    }

    @Override
    public final int maxColumnSize() {
        return this.maxColumnSize;
    }


    @Override
    public final boolean hasConflictClause() {
        return this.hasConflictClause;
    }

    @Override
    public final boolean isArmyAppendReturning() {
        return this.appendReturningClause;
    }

    @Override
    public final _InsertContext parentContext() {
        return this.parentContext;
    }

    @Override
    public final TableMeta<?> insertTable() {
        return this.insertTable;
    }

    @Override
    public final String tableAlias() {
        return this.tableAlias;
    }

    @Override
    public final String safeTableAlias() {
        return this.safeTableAlias;
    }

    @Override
    public final String safeTableName() {
        return this.safeTableName;
    }

    @Override
    public final String safeTableAliasOrSafeTableName() {
        String safeAlias;
        safeAlias = this.safeTableAlias;
        if (safeAlias == null) {
            safeAlias = this.safeTableName;
        }
        assert safeAlias != null; // failure ,bug
        return safeAlias;
    }

    @Override
    public final String rowAlias() {
        return this.rowAlias;
    }

    @Override
    public final String safeRowAlias() {
        return this.safeRowAlias;
    }

    @Override
    public final LiteralMode literalMode() {
        return this.literalMode;
    }

    @Override
    public final void inConflictSetClause(boolean inSetClause) {
        this.inSetClause = inSetClause;
    }

    @Override
    public final void outputFieldTableAlias(final boolean output) {
        this.outputFieldTableAlias = output;
    }

    @Override
    public final void appendField(final @Nullable String tableAlias, final FieldMeta<?> field) {
        if (tableAlias == null) {
            throw new NullPointerException();
        }

        final _SqlContext outerContext = this.outerContext;

        final boolean thisTableField;

        final String safeAlias;
        if (tableAlias.equals(this.tableAlias)) {
            thisTableField = true;
            safeAlias = this.safeTableAlias;
        } else if (tableAlias.equals(this.rowAlias)) {
            if (this.inSetClause && this.fieldMap != null && !this.fieldMap.containsKey(field)) {
                // here ,MySQL couldn't recognize field
                String m = String.format("%s not present in column list clause,so %s couldn't recognize.", field, this.parser.dialectDatabase.name());
                throw new CriteriaException(m);
            }
            thisTableField = true;
            safeAlias = this.safeRowAlias;
        } else if (this.joinableInsertStmt) { // for postgre merge statement
            thisTableField = false;
            if (!(outerContext instanceof _JoinableMergeContext)) {
                throw _Exceptions.unknownColumn(field);
            }
            safeAlias = ((_JoinableMergeContext) outerContext).safeTableAlias(field.tableMeta(), tableAlias);
        } else if (this.rowAlias == null && this.parser.supportRowAlias) {
            throw _Exceptions.unknownColumn(tableAlias, field);
        } else if (this.rowAlias == null) {
            String m = String.format("%s don't support row alias.", this.parser.dialect);
            throw new CriteriaException(m);
        } else {
            throw _Exceptions.unknownColumn(tableAlias, field);
        }

        if (thisTableField && !this.valuesClauseEnd) {
            throw _Exceptions.targetTableFiledAsInsertValue(field);
        } else if (thisTableField && !(this.hasConflictClause || this.returnSelectionList.size() > 0)) {
            throw _Exceptions.unknownColumn(field);
        } else if (thisTableField && field.tableMeta() != this.insertTable) {
            throw _Exceptions.unknownColumn(tableAlias, field);
        }


        final StringBuilder sqlBuilder;
        sqlBuilder = this.sqlBuilder.append(_Constant.SPACE)
                .append(safeAlias)
                .append(_Constant.PERIOD);
        this.parser.safeObjectName(field, sqlBuilder);

    }

    @Override
    public final void appendField(final FieldMeta<?> field) {
        final TableMeta<?> fieldTable = field.tableMeta();
        final _SqlContext outerContext = this.outerContext;

        final boolean thisTableField;

        final String safeRelativeTableAlias;
        if (fieldTable == this.insertTable) {
            thisTableField = true;
            safeRelativeTableAlias = null;
        } else if (this.joinableInsertStmt) {
            thisTableField = false;
            if (!(outerContext instanceof _JoinableMergeContext)) {
                throw _Exceptions.unknownColumn(field);
            } else if ((safeRelativeTableAlias = ((_JoinableMergeContext) outerContext).trySaTableAliasOf(fieldTable)) == null) { // postgre merger
                throw _Exceptions.unknownColumn(field);
            }
        } else {
            throw _Exceptions.unknownColumn(field);
        }

        if (thisTableField && !this.valuesClauseEnd) {
            throw _Exceptions.targetTableFiledAsInsertValue(field);
        } else if (thisTableField && !(this.hasConflictClause || this.returnSelectionList.size() > 0)) {
            throw _Exceptions.unknownColumn(field);
        }

        final StringBuilder sqlBuilder;
        sqlBuilder = this.sqlBuilder.append(_Constant.SPACE);

        if (thisTableField) {
            String safeAlias;
            if ((this.outputFieldTableAlias || this.rowAlias != null)
                    && ((safeAlias = this.safeTableAlias) != null || (safeAlias = this.safeTableName) != null)) {
                sqlBuilder.append(safeAlias)
                        .append(_Constant.PERIOD);
            }
        } else {
            sqlBuilder.append(safeRelativeTableAlias)
                    .append(_Constant.PERIOD);
        }
        this.parser.safeObjectName(field, sqlBuilder);


    }

    @Override
    public final void appendFieldOnly(final FieldMeta<?> field) {
        if (!(this.valuesClauseEnd
                && (this.hasConflictClause || this.returnSelectionList.size() > 0)
                && field.tableMeta() == this.insertTable)) {
            throw _Exceptions.unknownColumn(field);
        }
        this.parser.safeObjectName(field, this.sqlBuilder);
    }

    @Override
    public final void appendFieldFromSub(final FieldMeta<?> field) {
        if (!(this.valuesClauseEnd && this.hasConflictClause && field.tableMeta() == this.insertTable)) {
            throw _Exceptions.unknownColumn(field);
        }

        String safeTableAlias = this.safeTableAlias;
        if (safeTableAlias == null) {
            safeTableAlias = this.safeTableName;
            assert safeTableAlias != null;
        }
        final StringBuilder sqlBuilder;
        sqlBuilder = this.sqlBuilder.append(_Constant.SPACE)
                .append(safeTableAlias)
                .append(_Constant.PERIOD);
        this.parser.safeObjectName(field, sqlBuilder);
    }

    @Override
    public final void appendFieldOnlyFromSub(final FieldMeta<?> field) {
        if (!(this.valuesClauseEnd && this.hasConflictClause && field.tableMeta() == this.insertTable)) {
            throw _Exceptions.unknownColumn(field);
        }
        this.parser.safeObjectName(field, this.sqlBuilder);
    }

    @Override
    public final boolean isAppendedUpdateTime() {
        return this.appendedUpdateTime;
    }

    @Override
    public void appendSetLeftItem(final SqlField dataField, final @Nullable Expression updateTimePlaceholder) {
        final FieldMeta<?> field;
        final String fieldName = dataField.fieldName();
        final UpdateMode mode;
        if (!(dataField instanceof FieldMeta)) {
            String m = String.format("Insert statement conflict clause don't support %s", dataField);
            throw new CriteriaException(m);
        } else if ((field = (FieldMeta<?>) dataField).tableMeta() != this.insertTable) {
            throw _Exceptions.unknownColumn(dataField);
        } else if (!(this.valuesClauseEnd && this.hasConflictClause && field.tableMeta() == this.insertTable)) {
            throw _Exceptions.unknownColumn(field);
        } else if (updateTimePlaceholder == null && _MetaBridge.UPDATE_TIME.equals(fieldName)) {
            throw _Exceptions.armyManageField(field);
        } else if (_MetaBridge.VERSION.equals(fieldName)) {
            throw _Exceptions.armyManageField(field);
        } else switch ((mode = field.updateMode())) {
            case IMMUTABLE:
                throw _Exceptions.immutableField(field);
            case ONLY_NULL:
            case ONLY_DEFAULT: {
                if (!this.conflictPredicateClause) {
                    String m = String.format("%s don't support update the field with %s mode.",
                            this.parser.dialect, mode);
                    throw new CriteriaException(m);
                } else if (mode == UpdateMode.ONLY_DEFAULT && this.parser.supportOnlyDefault) {
                    throw _Exceptions.dontSupportOnlyDefault(this.parser.dialect);
                }
                List<FieldMeta<?>> conditionFieldList = this.conditionFieldList;
                if (conditionFieldList == null) {
                    conditionFieldList = _Collections.arrayList();
                    this.conditionFieldList = conditionFieldList;
                }
                conditionFieldList.add(field);
            }
            break;
            case UPDATABLE:
                break;
            default:
                throw _Exceptions.unexpectedEnum(mode);
        }

        final StringBuilder sqlBuilder;
        sqlBuilder = this.sqlBuilder.append(_Constant.SPACE);
        this.parser.safeObjectName(field, sqlBuilder);

        if (updateTimePlaceholder != null) {
            this.appendedUpdateTime = true;
            appendUpdateTimePlaceholder(field, updateTimePlaceholder);
        }

    }


    @Override
    public final void appendFieldList() {
        assert !this.columnListClauseEnd;

        final List<FieldMeta<?>> fieldList = this.fieldList;
        assert fieldList != null; //when assignment insert, fieldList is null.
        final ArmyParser parser = this.parser;
        final StringBuilder sqlBuilder;
        sqlBuilder = this.sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);

        final boolean migration = this.migration;
        final int fieldSize = fieldList.size();
        FieldMeta<?> field;
        int outputColumnSize = 0;
        for (int i = 0, actualIndex = 0; i < fieldSize; i++) {
            field = fieldList.get(i);
            if (!migration && !field.insertable()) {
                // fieldList have be checked,fieldList possibly is io.army.meta.TableMeta.fieldList()
                continue;
            }

            assert migration
                    || !(field instanceof PrimaryFieldMeta)
                    || field.generatorType() != GeneratorType.POST;

            if (actualIndex > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);
            } else {
                sqlBuilder.append(_Constant.SPACE);
            }
            parser.safeObjectName(field, sqlBuilder);
            actualIndex++;
            outputColumnSize = actualIndex;
        }

        sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

        assert outputColumnSize > 0;
        this.outputColumnSize = outputColumnSize;
        this.columnListClauseEnd = true;
    }

    @Override
    public final void appendValueList() {
        assert this.columnListClauseEnd && !this.valuesClauseEnd;
        final int outputColumnSize, outValueSize;
        outputColumnSize = this.outputColumnSize;
        outValueSize = this.doAppendValuesList(outputColumnSize, this.fieldList);
        assert outValueSize == outputColumnSize;

        this.valuesClauseEnd = true;
    }

    @Override
    public final void appendAssignmentClause() {
        assert !this.columnListClauseEnd && !this.valuesClauseEnd;
        this.columnListClauseEnd = true;
        this.doAppendAssignments();
        this.valuesClauseEnd = true;
    }

    @Override
    public final void appendSubQuery() {
        assert this.columnListClauseEnd && !this.valuesClauseEnd;
        final int outputColumnSize, outValueSize;
        outputColumnSize = this.outputColumnSize;
        outValueSize = this.doAppendSubQuery(outputColumnSize, this.fieldList);
        assert outValueSize == outputColumnSize;

        this.valuesClauseEnd = true;

    }

    @Override
    public final boolean hasConditionPredicate() {
        final List<FieldMeta<?>> conditionFieldList = this.conditionFieldList;
        return conditionFieldList != null && conditionFieldList.size() > 0;
    }

    @Override
    public final void appendConditionPredicate(final boolean firstPredicate) {
        final List<FieldMeta<?>> conditionFieldList = this.conditionFieldList;
        if (conditionFieldList == null) {
            return;
        }
        final ArmyParser parser = this.parser;
        final StringBuilder sqlBuilder = this.sqlBuilder;
        final int fieldSize = conditionFieldList.size();
        final String safeTableAlias;
        if (this.safeTableAlias == null) {
            safeTableAlias = parser.safeObjectName(this.insertTable);
        } else {
            safeTableAlias = this.safeTableAlias;
        }
        String safeColumnName;
        FieldMeta<?> field;
        for (int i = 0; i < fieldSize; i++) {
            field = conditionFieldList.get(i);
            if (i == 0 && firstPredicate) {
                sqlBuilder.append(_Constant.SPACE);
            } else {
                sqlBuilder.append(_Constant.SPACE_AND_SPACE);
            }

            sqlBuilder.append(safeTableAlias)
                    .append(_Constant.PERIOD);

            safeColumnName = parser.safeObjectName(field);
            sqlBuilder.append(safeColumnName);
            switch (field.updateMode()) {
                case ONLY_NULL:
                    sqlBuilder.append(_Constant.SPACE_IS_NULL);
                    break;
                case ONLY_DEFAULT: {
                    sqlBuilder.append(_Constant.SPACE)
                            .append(parser.defaultFuncName())
                            .append(_Constant.LEFT_PAREN)
                            .append(_Constant.SPACE)
                            .append(safeTableAlias)
                            .append(_Constant.PERIOD)
                            .append(safeColumnName)
                            .append(_Constant.SPACE_RIGHT_PAREN);

                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(field.updateMode());

            }
        }

    }


    @Override
    public final void appendReturnIdIfNeed() {
        assert this.parser.supportReturningClause;

        final PrimaryFieldMeta<?> returnId = this.returnId;
        if (returnId == null || !this.appendReturningClause) {
            return;
        }
        assert this.returnSelectionList.size() == 1 && this.returnSelectionList.get(0) == returnId;
        final StringBuilder sqlBuilder;
        sqlBuilder = this.sqlBuilder
                .append(_Constant.SPACE_RETURNING)
                .append(_Constant.SPACE);

        final ArmyParser dialect = this.parser;
        final String safeTableAlias = this.safeTableAlias;
        if (safeTableAlias != null) {
            sqlBuilder.append(safeTableAlias)
                    .append(_Constant.PERIOD);
        }
        dialect.safeObjectName(returnId, sqlBuilder)
                .append(_Constant.SPACE_AS_SPACE);

        dialect.identifier(returnId.label(), sqlBuilder);
    }


    @Override
    public final SimpleStmt build() {
        final SimpleStmt stmt;
        if (this.returnId != null) {
            stmt = Stmts.postStmt(this);
        } else if (this.returnSelectionList.size() == 0) {
            stmt = Stmts.minSimple(this);
        } else {
            stmt = Stmts.queryStmt(this);
        }
        return stmt;
    }

    @Override
    public final List<? extends _SelectItem> selectItemList() {
        final List<? extends _SelectItem> selectItemList = this.returningList;
        if (selectItemList.size() == 0) {
            throw new IllegalStateException("no RETURNING clause");
        }
        return selectItemList;
    }

    @Override
    public final List<? extends Selection> selectionList() {
        return this.returnSelectionList;
    }

    @Override
    public final PrimaryFieldMeta<?> idField() {
        final PrimaryFieldMeta<?> field = this.returnId;
        assert field != null;
        return field;
    }

    @Override
    public final int idSelectionIndex() {
        return this.idSelectionIndex;
    }


    final Temporal createCreateTime(final SingleTableMeta<?> table) {
        final FieldMeta<?> field;
        field = table.getField(_MetaBridge.CREATE_TIME);


        final Class<?> javaType;
        javaType = field.javaType();
        final Temporal now;
        if (javaType == LocalDateTime.class) {
            if (this.parser.truncatedTimeType) {
                now = _TimeUtils.truncatedIfNeed(field.scale(), LocalDateTime.now());
            } else {
                now = LocalDateTime.now();
            }
        } else if (javaType == OffsetDateTime.class) {
            if (this.parser.truncatedTimeType) {
                now = _TimeUtils.truncatedIfNeed(field.scale(), OffsetDateTime.now(this.parser.mappingEnv.zoneOffset()));
            } else {
                now = OffsetDateTime.now(this.parser.mappingEnv.zoneOffset());
            }
        } else if (javaType == ZonedDateTime.class) {
            if (this.parser.truncatedTimeType) {
                now = _TimeUtils.truncatedIfNeed(field.scale(), ZonedDateTime.now(this.parser.mappingEnv.zoneOffset()));
            } else {
                now = ZonedDateTime.now(this.parser.mappingEnv.zoneOffset());
            }
        } else {
            // FieldMeta no bug,never here
            throw _Exceptions.dontSupportJavaType(field, javaType);
        }
        return now;
    }


    /**
     * @return output values size
     */
    int doAppendValuesList(int outputColumnSize, List<FieldMeta<?>> fieldList) {
        throw new UnsupportedOperationException();
    }

    void doAppendAssignments() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return selection count of sub query
     */
    int doAppendSubQuery(int outputColumnSize, List<FieldMeta<?>> fieldList) {
        throw new UnsupportedOperationException();
    }

    final boolean isValuesClauseEnd() {
        return this.valuesClauseEnd;
    }

    final void appendInsertValue(final LiteralMode mode, final FieldMeta<?> field, final @Nullable Object value) {
        switch (mode) {
            case DEFAULT:
                this.appendParam(SingleParam.build(field, value));
                break;
            case PREFERENCE: {
                if (!(field.mappingType() instanceof _ArmyNoInjectionMapping)) {
                    this.appendParam(SingleParam.build(field, value));
                } else if (value == null) {
                    this.sqlBuilder.append(_Constant.SPACE_NULL);
                } else {
                    this.parser.literal(field, value, EscapeMode.DEFAULT, this.sqlBuilder.append(_Constant.SPACE));
                }
            }
            break;
            case LITERAL: {
                if (value == null) {
                    this.sqlBuilder.append(_Constant.SPACE_NULL);
                } else {
                    this.parser.literal(field, value, EscapeMode.DEFAULT, this.sqlBuilder.append(_Constant.SPACE));
                }
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(mode);
        }

    }

    /*-------------------below static method -------------------*/

    static CriteriaException valuesClauseEndNoBatchNo() {
        return new CriteriaException("VALUES clause have ended,couldn't reference batch no");
    }


    private static List<FieldMeta<?>> createNonChildFieldList(final SingleTableMeta<?> insertTable,
                                                              final Predicate<FieldMeta<?>> predicate) {

        final List<FieldMeta<?>> insertFieldChain;
        insertFieldChain = insertTable.fieldChain();

        final ArrayList<FieldMeta<?>> fieldList = _Collections.arrayList(6 + insertFieldChain.size());

        FieldMeta<?> reservedField;
        reservedField = insertTable.id();
        if (reservedField.insertable() && reservedField.generatorType() != null) {
            fieldList.add(reservedField);
        }

        reservedField = insertTable.getField(_MetaBridge.CREATE_TIME);
        fieldList.add(reservedField);

        reservedField = insertTable.tryGetField(_MetaBridge.UPDATE_TIME);
        if (reservedField != null) {
            fieldList.add(reservedField);
        }

        reservedField = insertTable.tryGetField(_MetaBridge.VERSION);
        if (reservedField != null) {
            fieldList.add(reservedField);
        }


        reservedField = insertTable.tryGetField(_MetaBridge.VISIBLE);
        if (reservedField != null && !predicate.test(reservedField)) {
            fieldList.add(reservedField);
        }

        if (insertTable instanceof ParentTableMeta) {
            fieldList.add(insertTable.discriminator());
        }

        for (FieldMeta<?> field : insertFieldChain) {
            if (field instanceof PrimaryFieldMeta) {
                continue;
            }
            fieldList.add(field);
        }

        return Collections.unmodifiableList(fieldList);
    }

    private static List<FieldMeta<?>> createChildFieldList(final ChildTableMeta<?> insertTable) {
        final List<FieldMeta<?>> fieldChain;
        fieldChain = insertTable.fieldChain();

        final int chainSize = fieldChain.size();
        List<FieldMeta<?>> fieldList;
        if (chainSize == 0) {
            fieldList = Collections.singletonList(insertTable.id());
        } else {
            fieldList = _Collections.arrayList(1 + chainSize);
            fieldList.add(insertTable.id());

            for (FieldMeta<?> field : fieldChain) {
                assert !(field instanceof PrimaryFieldMeta);
                fieldList.add(field);
            }
            fieldList = Collections.unmodifiableList(fieldList);
        }
        return fieldList;

    }

    private static int returnIdSelection(final DialectParser parser, final PrimaryFieldMeta<?> idField,
                                         final List<? extends Selection> selectionList) {
        final int selectionSize;
        selectionSize = selectionList.size();

        assert selectionSize > 0;

        int idIndex = -1;
        Selection selection;
        for (int i = 0; i < selectionSize; i++) {
            selection = selectionList.get(i);

            if (selection instanceof TableField) {
                if (selection == idField
                        || (selection instanceof QualifiedField
                        && ((QualifiedField<?>) selection).fieldMeta() == idField)) {
                    idIndex = i;
                    break;
                }
            } else if (!(selection instanceof DerivedField) && selection instanceof FieldSelection) {
                if (((FieldSelection) selection).fieldMeta() == idField) {
                    idIndex = i;
                    break;
                }
            }

        }// for

        if (idIndex < 0) {
            String m = String.format("%s RETURNING clause must contain %s,because it's %s is %s.",
                    parser.dialect().database(), idField, GeneratorType.class.getName(), GeneratorType.POST);
            throw new CriteriaException(m);
        }
        return idIndex;
    }

    private static Map<FieldMeta<?>, Boolean> createFieldMap(final _Insert targetStmt, final List<FieldMeta<?>> fieldList) {
        int fieldSize = fieldList.size();

        final Set<FieldMeta<?>> assignmentFieldSet;
        if (targetStmt instanceof _Insert._AssignmentInsert) {
            assignmentFieldSet = ((_Insert._AssignmentInsert) targetStmt).assignmentMap().keySet();
            fieldSize += assignmentFieldSet.size();
        } else {
            assignmentFieldSet = null;
        }

        final Map<FieldMeta<?>, Boolean> map = _Collections.hashMap((int) (fieldSize / 0.75F));
        for (FieldMeta<?> field : fieldList) {
            map.put(field, Boolean.TRUE);
        }

        if (assignmentFieldSet != null) {
            for (FieldMeta<?> field : assignmentFieldSet) {
                map.put(field, Boolean.TRUE);
            }
        }
        return Collections.unmodifiableMap(map);
    }

    static abstract class InsertRowWrapper implements RowWrapper {

        final TableMeta<?> domainTable;

        private final Temporal createTime;

        final boolean manageVisible;

        InsertRowWrapper(final InsertContext context, final _Insert statement) {
            if (statement instanceof _Insert._ParentSubInsert) {
                this.domainTable = ((_Insert._ParentSubInsert) statement).domainTable();
            } else {
                this.domainTable = statement.table();
            }
            // assert this.domainTable == context.insertTable;

            final FieldMeta<?> visibleField;
            visibleField = this.domainTable.tryGetComplexFiled(_MetaBridge.VISIBLE);

            if (statement instanceof _Insert._ValuesSyntaxInsert) {
                if (statement instanceof _Insert._ChildValuesInsert) {
                    this.manageVisible = visibleField != null
                            && !((_Insert._ChildValuesInsert) statement).defaultValueMap().containsKey(visibleField);
                } else {
                    this.manageVisible = visibleField != null
                            && !((_Insert._ValuesSyntaxInsert) statement).defaultValueMap().containsKey(visibleField);
                }
            } else if (statement instanceof _Insert._ChildAssignmentInsert) {
                this.manageVisible = visibleField != null
                        && !((_Insert._ChildAssignmentInsert) statement).assignmentMap().containsKey(visibleField);
            } else if (statement instanceof _Insert._AssignmentInsert) {
                this.manageVisible = visibleField != null
                        && !((_Insert._AssignmentInsert) statement).assignmentMap().containsKey(visibleField);
            } else {
                //no bug,never here
                throw _Exceptions.unexpectedStatement(statement);
            }

            if (this.domainTable instanceof ChildTableMeta) {
                this.createTime = context.createCreateTime(((ChildTableMeta<?>) this.domainTable).parentMeta());
            } else {
                this.createTime = context.createCreateTime((SingleTableMeta<?>) this.domainTable);
            }
        }

        @Override
        public final Temporal getCreateTime() {
            return this.createTime;
        }

        @Override
        public final boolean isManageVisible() {
            return this.manageVisible;
        }


    }//InsertRowWrapper

    static abstract class ExpRowWrapper extends InsertRowWrapper {

        private final ReadWrapper readWrapper;

        ExpRowWrapper(InsertContext context, _Insert statement) {
            super(context, statement);
            this.readWrapper = new RowReadWrapper(this, context.parser.mappingEnv);
        }


        @Override
        public final boolean isNullValueParam(final FieldMeta<?> field) {
            final _Expression expression;
            expression = this.getExpression(field);
            final boolean match;
            if (expression == null) {
                match = true;
            } else if (expression instanceof SqlValueParam.SingleAnonymousValue) {
                match = ((SqlValueParam.SingleAnonymousValue) expression).value() == null;
            } else {
                match = true; //the fields that is managed by field must be value param
            }
            return match;
        }

        @Override
        public final ReadWrapper readonlyWrapper() {
            return this.readWrapper;
        }


        @Nullable
        abstract Object getGeneratedValue(FieldMeta<?> field);

        /**
         * <p>
         * Must read row value not default value of column
         */
        @Nullable
        abstract _Expression getExpression(FieldMeta<?> field);


    }//ExpRowWrapper

    private static final class RowReadWrapper implements ReadWrapper {

        private final ExpRowWrapper wrapper;

        private final MappingEnv mappingEnv;

        private RowReadWrapper(ExpRowWrapper wrapper, MappingEnv mappingEnv) {
            this.wrapper = wrapper;
            this.mappingEnv = mappingEnv;
        }

        @Override
        public boolean isReadable(final String propertyName) {
            return this.wrapper.domainTable.containComplexField(propertyName);
        }

        @Override
        public Object get(final String propertyName) throws ObjectAccessException {
            final ExpRowWrapper wrapper = this.wrapper;
            final TableMeta<?> domainTable = wrapper.domainTable;
            final FieldMeta<?> field;
            field = domainTable.tryGetComplexFiled(propertyName);
            if (field == null) {
                throw _Exceptions.nonReadableProperty(domainTable, propertyName);
            }
            final Object value;
            value = wrapper.getGeneratedValue(field);
            if (value != null) {
                return value;
            }

            final _Expression expression;
            if (field instanceof PrimaryFieldMeta && field.tableMeta() instanceof ChildTableMeta) {
                expression = wrapper.getExpression(field.tableMeta().nonChildId());
            } else {
                expression = wrapper.getExpression(field);
            }
            return _DialectUtils.readParamValue(field, expression, this.mappingEnv);
        }


    }//RowReadWrapper


}
