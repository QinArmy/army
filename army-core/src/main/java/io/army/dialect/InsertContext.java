package io.army.dialect;

import io.army.annotation.GeneratorType;
import io.army.criteria.*;
import io.army.criteria.impl.inner._Insert;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.stmt.BatchStmt;
import io.army.stmt._InsertStmtParams;
import io.army.util._Exceptions;

import java.util.List;

abstract class InsertContext extends StatementContext implements _InsertContext
        , _SqlContext._SetClauseContextSpec
        , _InsertContext._ValueSyntaxSpec
        , _InsertContext._AssignmentsSpec
        , _InsertContext._QuerySyntaxSpec
        , _InsertStmtParams {

    private InsertContext parentContext;

    final TableMeta<?> domainTable;

    final TableMeta<?> insertTable;

    final boolean migration;

    final LiteralMode literalMode;

    final List<FieldMeta<?>> fieldList;

    private final String rowAlias;

    private final String safeRowAlias;

    final boolean conflictClause;

    /**
     * {@link #insertTable} instanceof {@link  SingleTableMeta} and  dialect support returning clause nad generated key.
     */
    final PrimaryFieldMeta<?> returnId;

    /**
     * @see #returnId
     */
    final String idSelectionAlias;

    private boolean columnListClauseEnd;

    private boolean valuesClauseEnd;

    private int outputColumnSize;


    /**
     * <p>
     * For {@link  io.army.meta.SingleTableMeta}
     * </p>
     */
    InsertContext(@Nullable StatementContext outerContext, final _Insert domainStmt
            , ArmyParser0 parser, Visible visible) {
        super(outerContext, parser, visible);
        this.parentContext = null;
        final _Insert nonChildStmt;
        if (domainStmt instanceof _Insert._ChildInsert) {
            nonChildStmt = ((_Insert._ChildInsert) domainStmt).parentStmt();
            this.insertTable = nonChildStmt.table();
            this.domainTable = domainStmt.table();
        } else {
            nonChildStmt = domainStmt;
            this.insertTable = nonChildStmt.table();
            this.domainTable = this.insertTable;
        }
        assert this.insertTable instanceof SingleTableMeta;

        if (nonChildStmt instanceof _Insert._InsertOption) {
            final _Insert._InsertOption option = (_Insert._InsertOption) nonChildStmt;
            this.literalMode = option.literalMode();
            this.migration = nonChildStmt instanceof _Insert._QueryInsert || option.isMigration();
        } else {
            this.migration = nonChildStmt instanceof _Insert._QueryInsert;
            this.literalMode = LiteralMode.DEFAULT;
        }

        if (nonChildStmt instanceof _Insert._ConflictActionClauseSpec) {
            this.rowAlias = ((_Insert._ConflictActionClauseSpec) nonChildStmt).rowAlias();
            this.safeRowAlias = this.rowAlias == null ? null : parser.identifier(this.rowAlias);
        } else {
            this.safeRowAlias = this.rowAlias = null;
        }
        final List<FieldMeta<?>> fieldList;
        if (nonChildStmt instanceof _Insert._ColumnListInsert) {
            fieldList = ((_Insert._ColumnListInsert) nonChildStmt).fieldList();
        } else {
            fieldList = null;
        }
        if (fieldList != null && fieldList.size() > 0) {
            this.fieldList = fieldList;
        } else if (nonChildStmt instanceof _Insert._AssignmentInsert) {
            this.fieldList = null;
        } else {
            assert !(nonChildStmt instanceof _Insert._QueryInsert);
            this.fieldList = castFieldList(this.insertTable);
        }
        this.conflictClause = nonChildStmt instanceof _Insert._SupportConflictClauseSpec
                && ((_Insert._SupportConflictClauseSpec) nonChildStmt).hasConflictAction();

        final PrimaryFieldMeta<?> idField = this.insertTable.id();
        if (this.migration || idField.generatorType() != GeneratorType.POST) {
            this.returnId = null;
            this.idSelectionAlias = null;
        } else if (this.parser.supportInsertReturning()) {
            //TODO
            throw new UnsupportedOperationException();
        } else if (this.conflictClause) {
            if (nonChildStmt != domainStmt) {
                //the implementations of io.army.criteria.Insert no bug,never here
                throw _Exceptions.duplicateKeyAndPostIdInsert((ChildTableMeta<?>) domainStmt.table());
            }
            this.returnId = null;
            this.idSelectionAlias = null;
        } else {
            this.returnId = idField;
            this.idSelectionAlias = idField.fieldName();
        }

    }


    /**
     * <p>
     * For {@link  io.army.meta.ChildTableMeta}
     * </p>
     */
    InsertContext(final _Insert._ChildInsert stmt, final InsertContext parentContext) {
        super(null, parentContext.parser, parentContext.visible);
        this.parentContext = parentContext;
        this.insertTable = stmt.table();
        this.domainTable = this.insertTable;

        if (stmt instanceof _Insert._InsertOption) {
            final _Insert._InsertOption option = (_Insert._InsertOption) stmt;
            this.literalMode = option.literalMode();
            this.migration = stmt instanceof _Insert._QueryInsert || option.isMigration();
        } else {
            this.migration = stmt instanceof _Insert._QueryInsert;
            this.literalMode = LiteralMode.DEFAULT;
        }
        assert this.insertTable instanceof ChildTableMeta
                && this.domainTable == parentContext.domainTable
                && this.migration == parentContext.migration
                && this.literalMode == parentContext.literalMode
                && ((ChildTableMeta<?>) this.insertTable).parentMeta() == parentContext.insertTable;

        if (stmt instanceof _Insert._ConflictActionClauseSpec) {
            this.rowAlias = ((_Insert._ConflictActionClauseSpec) stmt).rowAlias();
            this.safeRowAlias = this.rowAlias == null ? null : parser.identifier(this.rowAlias);
        } else {
            this.safeRowAlias = this.rowAlias = null;
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
            this.fieldList = null;
        } else {
            assert !(stmt instanceof _Insert._QueryInsert);
            this.fieldList = castFieldList(this.insertTable);
        }
        this.conflictClause = stmt instanceof _Insert._SupportConflictClauseSpec
                && ((_Insert._SupportConflictClauseSpec) stmt).hasConflictAction();

        this.returnId = null;
        this.idSelectionAlias = null;


    }

    @Override
    public final _InsertContext parentContext() {
        return this.parentContext;
    }

    @Override
    public final BatchStmt build(List<?> paramList) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final TableMeta<?> insertTable() {
        return this.insertTable;
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
    public final void appendField(String tableAlias, FieldMeta<?> field) {
        this.appendFieldForConflict(tableAlias, field);
    }

    @Override
    public final void appendField(FieldMeta<?> field) {
        this.appendFieldForConflict(null, field);
    }

    @Override
    public void appendSetLeftItem(final DataField dataField) {
        if (!(dataField instanceof TableField)) {
            String m = String.format("Insert statement conflict clause don't support %s", dataField);
            throw new CriteriaException(m);
        } else if (((TableField) dataField).tableMeta() != this.insertTable) {
            throw _Exceptions.unknownColumn(dataField);
        }

        final StringBuilder sqlBuilder;
        sqlBuilder = this.sqlBuilder.append(_Constant.SPACE);
        if (dataField instanceof QualifiedField) {
            final String rowAlias = this.rowAlias;
            if (rowAlias == null || !rowAlias.equals(((QualifiedField<?>) dataField).tableAlias())) {
                throw _Exceptions.unknownColumn(dataField);
            }
            sqlBuilder.append(this.safeRowAlias)
                    .append(_Constant.POINT);
            this.parser.safeObjectName(((QualifiedField<?>) dataField).fieldMeta(), sqlBuilder);
        } else {
            this.parser.safeObjectName((FieldMeta<?>) dataField, sqlBuilder);
        }

    }

    @Override
    public final void appendFieldList() {
        assert !this.columnListClauseEnd;

        final List<FieldMeta<?>> fieldList = this.fieldList;
        assert fieldList != null; //when assignment insert, fieldList is null.
        final ArmyParser0 parser = this.parser;
        final StringBuilder sqlBuilder = this.sqlBuilder
                .append(_Constant.SPACE_LEFT_PAREN);

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
    public final void appendReturnIdIfNeed() {
        final PrimaryFieldMeta<?> returnId = this.returnId;
        if (returnId == null) {
            return;
        }
        final StringBuilder sqlBuilder;
        sqlBuilder = this.sqlBuilder
                .append(_Constant.SPACE_RETURNING)
                .append(_Constant.SPACE);

        final ArmyParser0 dialect = this.parser;
        //TODO for dialect table alias
        dialect.safeObjectName(returnId, sqlBuilder)
                .append(_Constant.SPACE_AS_SPACE);

        dialect.identifier(this.idSelectionAlias, sqlBuilder);
    }


    @Override
    public List<Selection> selectionList() {
        return super.selectionList();
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

    private void appendFieldForConflict(@Nullable String tableAlias, FieldMeta<?> field) {
        if (!(this.valuesClauseEnd && this.conflictClause && field.tableMeta() == this.insertTable)) {
            throw _Exceptions.unknownColumn(field);
        }
        final StringBuilder sqlBuilder;
        sqlBuilder = this.sqlBuilder
                .append(_Constant.SPACE);
        if (tableAlias != null) {
            if (!tableAlias.equals(this.rowAlias)) {
                throw _Exceptions.unknownColumn(tableAlias, field);
            }
            sqlBuilder.append(this.safeRowAlias)
                    .append(_Constant.POINT);
        }
        this.parser.safeObjectName(field, sqlBuilder);
    }


    @SuppressWarnings("unchecked")
    private static List<FieldMeta<?>> castFieldList(final TableMeta<?> table) {
        final List<?> list;
        list = table.fieldList();
        return (List<FieldMeta<?>>) list;
    }


}
