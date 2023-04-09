package io.army.dialect;

import io.army.annotation.GeneratorType;
import io.army.annotation.UpdateMode;
import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.lang.Nullable;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.stmt.SingleParam;
import io.army.stmt._InsertStmtParams;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

abstract class InsertContext extends StatementContext
        implements _InsertContext,
        _DmlContext._SetClauseContextSpec,
        _InsertContext._ValueSyntaxSpec,
        _InsertContext._AssignmentsSpec,
        _InsertContext._QuerySyntaxSpec,
        _DmlContext._SingleTableContextSpec,
        _InsertStmtParams,
        SelectItemListContext {

    private final InsertContext parentContext;

    final TableMeta<?> insertTable;

    final boolean migration;

    final LiteralMode literalMode;

    final List<FieldMeta<?>> fieldList;

    private final String tableAlias;

    private final String safeTableAlias;

    private final String rowAlias;

    private final String safeRowAlias;

    private final String safeTableName;

    final boolean hasConflictClause;

    final boolean conflictPredicateClause;

    /**
     * {@link #insertTable} instanceof {@link  SingleTableMeta} and  dialect support returning clause nad generated key.
     */
    final PrimaryFieldMeta<?> returnId;

    /**
     * @see #returnId
     */
    final String idSelectionAlias;

    final List<? extends _SelectItem> returningList;

    private final boolean appendReturningClause;

    private boolean columnListClauseEnd;

    private boolean valuesClauseEnd;

    private int outputColumnSize;

    private List<FieldMeta<?>> conditionFieldList;


    /**
     * <p>
     * For {@link  io.army.meta.SingleTableMeta}
     * </p>
     */
    InsertContext(@Nullable StatementContext outerContext, final _Insert domainStmt
            , ArmyParser parser, Visible visible) {
        super(outerContext, parser, visible);
        this.parentContext = null;
        final _Insert targetStmt;
        if (domainStmt instanceof _Insert._ChildInsert) {
            targetStmt = ((_Insert._ChildInsert) domainStmt).parentStmt();
        } else {
            if (domainStmt instanceof _Insert._QueryInsert) {
                //validate,because criteria implementation can't do this
                ((_Insert._QueryInsert) domainStmt).validateOnlyParen();
            }
            targetStmt = domainStmt;
        }
        this.insertTable = targetStmt.insertTable();
        assert this.insertTable instanceof SingleTableMeta;

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
            this.fieldList = castFieldList(this.insertTable);
        }

        final PrimaryFieldMeta<?> idField = this.insertTable.id();
        final boolean needReturnId, cannotReturnId;
        needReturnId = !this.migration
                && targetStmt instanceof PrimaryStatement
                && idField.generatorType() == GeneratorType.POST
                && ((targetStmt instanceof _Insert._DomainInsert && !((_Insert._DomainInsert) targetStmt).isIgnoreReturnIds()) || domainStmt instanceof _Insert._ChildInsert);


        cannotReturnId = this.hasConflictClause
                && targetStmt.insertRowCount() > 1
                && (!(targetStmt instanceof _Statement._ReturningListSpec) || ((_Insert._SupportConflictClauseSpec) targetStmt).isIgnorableConflict());

        if (needReturnId && cannotReturnId) {
            throw _Exceptions.cannotReturnPostId(domainStmt);
        }

        if (targetStmt instanceof _ReturningDml) {
            this.returningList = ((_ReturningDml) targetStmt).returningList();
            if (needReturnId) {
                this.returnId = idField;
                this.idSelectionAlias = returnIdSelection(parser, idField, this.returningList)
                        .selectionName();
            } else {
                this.returnId = null;
                this.idSelectionAlias = null;
            }
            this.appendReturningClause = false;
        } else if (needReturnId) {
            if (targetStmt instanceof _Statement._ReturningListSpec) {
                this.returningList = Collections.singletonList((_Selection) idField);
                this.appendReturningClause = true;
            } else {
                this.appendReturningClause = false;
                this.returningList = Collections.emptyList();
            }
            this.returnId = idField;
            this.idSelectionAlias = idField.selectionName();
        } else {
            this.returningList = Collections.emptyList();
            this.returnId = null;
            this.idSelectionAlias = null;
            this.appendReturningClause = false;
        }


    }


    /**
     * <p>
     * For {@link  io.army.meta.ChildTableMeta}
     * </p>
     */
    InsertContext(@Nullable StatementContext outerContext, final _Insert._ChildInsert stmt
            , final InsertContext parentContext) {
        super(outerContext, parentContext.parser, parentContext.visible);
        this.parentContext = parentContext;
        this.insertTable = stmt.insertTable();

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
            this.fieldList = castFieldList(this.insertTable);
        }

        if (stmt instanceof _ReturningDml) {
            this.returningList = ((_ReturningDml) stmt).returningList();
        } else {
            this.returningList = Collections.emptyList();
        }
        this.returnId = null;
        this.idSelectionAlias = null;
        this.appendReturningClause = false;
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
    public final void appendField(final @Nullable String tableAlias, final FieldMeta<?> field) {
        final String safeAlias;
        if (!(this.valuesClauseEnd
                && (this.hasConflictClause || this.returningList.size() > 0)
                && field.tableMeta() == this.insertTable)) {
            throw _Exceptions.unknownColumn(field);
        } else if (tableAlias == null) {
            throw new NullPointerException();
        } else if (tableAlias.equals(this.rowAlias)) {
            safeAlias = this.safeRowAlias;
        } else if (tableAlias.equals(this.tableAlias)) {
            safeAlias = this.safeTableAlias;
        } else if (this.rowAlias == null && this.parser.supportRowAlias) {
            throw _Exceptions.unknownColumn(tableAlias, field);
        } else if (this.rowAlias == null) {
            String m = String.format("%s don't support row alias.", this.parser.dialect);
            throw new CriteriaException(m);
        } else {
            throw _Exceptions.unknownColumn(tableAlias, field);
        }

        final StringBuilder sqlBuilder;
        sqlBuilder = this.sqlBuilder.append(_Constant.SPACE)
                .append(safeAlias)
                .append(_Constant.POINT);
        this.parser.safeObjectName(field, sqlBuilder);

    }

    @Override
    public final void appendField(final FieldMeta<?> field) {
        if (!(this.valuesClauseEnd
                && (this.hasConflictClause || this.returningList.size() > 0)
                && field.tableMeta() == this.insertTable)) {
            throw _Exceptions.unknownColumn(field);
        }
        final StringBuilder sqlBuilder;
        sqlBuilder = this.sqlBuilder.append(_Constant.SPACE);
        this.parser.safeObjectName(field, sqlBuilder);
    }

    @Override
    public final void appendFieldFromSub(FieldMeta<?> field) {
        if (!(this.valuesClauseEnd && this.hasConflictClause && field.tableMeta() == this.insertTable)) {
            throw _Exceptions.unknownColumn(field);
        }

        final String safeTableName = this.safeTableName;
        assert safeTableName != null;
        final StringBuilder sqlBuilder;
        sqlBuilder = this.sqlBuilder.append(_Constant.SPACE)
                .append(safeTableName)
                .append(_Constant.POINT);
        this.parser.safeObjectName(field, sqlBuilder);
    }

    @Override
    public void appendSetLeftItem(final DataField dataField) {
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
        } else if (_MetaBridge.UPDATE_TIME.equals(fieldName) || _MetaBridge.VERSION.equals(fieldName)) {
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
                    conditionFieldList = new ArrayList<>();
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
                    .append(_Constant.POINT);

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
                            .append(_Constant.POINT)
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
        assert this.parser.childUpdateMode == _ChildUpdateMode.CTE;

        final PrimaryFieldMeta<?> returnId = this.returnId;
        if (returnId == null || !this.appendReturningClause) {
            return;
        }
        assert this.returningList.size() == 1 && this.returningList.get(0) == returnId;
        final StringBuilder sqlBuilder;
        sqlBuilder = this.sqlBuilder
                .append(_Constant.SPACE_RETURNING)
                .append(_Constant.SPACE);

        final ArmyParser dialect = this.parser;
        dialect.safeObjectName(returnId, sqlBuilder)
                .append(_Constant.SPACE_AS_SPACE);

        dialect.identifier(this.idSelectionAlias, sqlBuilder);
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
        final List<? extends _SelectItem> selectItemList = this.returningList;
        final List<? extends Selection> selectionList;
        if (selectItemList.size() == 0) {
            selectionList = Collections.emptyList();
        } else {
            selectionList = _DialectUtils.flatSelectItem(selectItemList);
        }
        return selectionList;
    }

    @Override
    public final PrimaryFieldMeta<?> idField() {
        final PrimaryFieldMeta<?> field = this.returnId;
        assert field != null;
        return field;
    }

    @Override
    public final String idReturnAlias() {
        final String alias = this.idSelectionAlias;
        assert alias != null;
        return alias;
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
                if (!(field.mappingType() instanceof _ArmyNoInjectionMapping)) {//TODO field codec
                    this.appendParam(SingleParam.build(field, value));
                } else if (value == null) {
                    this.sqlBuilder.append(_Constant.SPACE_NULL);
                } else {
                    this.parser.literal(field, value, this.sqlBuilder.append(_Constant.SPACE));
                }
            }
            break;
            case LITERAL: {
                if (value == null) {
                    this.sqlBuilder.append(_Constant.SPACE_NULL);
                } else {
                    this.parser.literal(field, value, this.sqlBuilder.append(_Constant.SPACE));
                }
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(mode);
        }

    }


    @SuppressWarnings("unchecked")
    private static List<FieldMeta<?>> castFieldList(final TableMeta<?> table) {
        final List<?> list;
        list = table.fieldList();
        return (List<FieldMeta<?>>) list;
    }


    private static List<FieldMeta<?>> createNonChildFieldList(final SingleTableMeta<?> insertTable
            , final Predicate<FieldMeta<?>> predicate) {

        final List<FieldMeta<?>> insertFieldChain;
        insertFieldChain = insertTable.fieldChain();

        final ArrayList<FieldMeta<?>> fieldList = new ArrayList<>(6 + insertFieldChain.size());

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
            fieldList = new ArrayList<>(1 + chainSize);
            fieldList.add(insertTable.id());

            for (FieldMeta<?> field : fieldChain) {
                assert !(field instanceof PrimaryFieldMeta);
                fieldList.add(field);
            }
            fieldList = Collections.unmodifiableList(fieldList);
        }
        return fieldList;

    }

    private static Selection returnIdSelection(final DialectParser parser, final PrimaryFieldMeta<?> idField,
                                               final List<? extends _SelectItem> selectItemList) {
        final int selectItemSize;
        selectItemSize = selectItemList.size();

        assert selectItemSize > 0;

        final TableMeta<?> insertTable;
        insertTable = idField.tableMeta();

        _SelectItem selectItem;
        Selection selection = null;
        for (int i = 0; i < selectItemSize; i++) {
            selectItem = selectItemList.get(i);

            if (selectItem instanceof _SelectionGroup.TableFieldGroup) {
                if (((_SelectionGroup.TableFieldGroup) selectItem).isLegalGroup(insertTable)
                        && ((_SelectionGroup.TableFieldGroup) selectItem).selectionList().contains(idField)) {
                    selection = idField;
                    break;
                }
            } else if (selectItem instanceof TableField) {
                if (selectItem == idField
                        || (selectItem instanceof QualifiedField
                        && ((QualifiedField<?>) selectItem).fieldMeta() == idField)) {
                    selection = (Selection) selectItem;
                    break;
                }
            }

        }// for

        if (selection == null) {
            String m = String.format("%s RETURNING clause must contain %s,because it's %s is %s.",
                    parser.dialect().database(), idField, GeneratorType.class.getName(), GeneratorType.POST);
            throw new CriteriaException(m);
        }
        return selection;
    }

}
