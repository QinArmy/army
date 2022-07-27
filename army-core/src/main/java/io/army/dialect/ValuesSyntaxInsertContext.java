package io.army.dialect;

import io.army.annotation.GeneratorType;
import io.army.criteria.NullHandleMode;
import io.army.criteria.Selection;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.stmt._InsertStmtParams;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.List;
import java.util.Map;

abstract class ValuesSyntaxInsertContext extends StatementContext implements _ValueInsertContext, _InsertStmtParams {


    final TableMeta<?> insertTable;

    final boolean migration;

    final NullHandleMode nullHandleMode;

    final boolean preferLiteral;

    final boolean duplicateKeyClause;

    private final List<FieldMeta<?>> fieldList;

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


    /**
     * <p>
     * For {@link  io.army.meta.SingleTableMeta}
     * </p>
     */
    ValuesSyntaxInsertContext(ArmyDialect dialect, final _Insert._ValuesSyntaxInsert domainStmt, Visible visible) {
        super(dialect, true, visible);

        final _Insert._ValuesSyntaxInsert nonChildStmt;
        if (domainStmt instanceof _Insert._ChildInsert) {
            nonChildStmt = (_Insert._ValuesSyntaxInsert) ((_Insert._ChildInsert) domainStmt).parentStmt();
        } else {
            nonChildStmt = domainStmt;
        }
        this.migration = nonChildStmt.isMigration();
        final NullHandleMode handleMode = nonChildStmt.nullHandle();
        this.nullHandleMode = handleMode == null ? NullHandleMode.INSERT_DEFAULT : handleMode;
        this.preferLiteral = nonChildStmt.isPreferLiteral();

        this.duplicateKeyClause = nonChildStmt instanceof _Insert._DuplicateKeyClause;
        this.insertTable = nonChildStmt.table();
        assert this.insertTable instanceof SingleTableMeta;

        final List<FieldMeta<?>> fieldList = nonChildStmt.fieldList();
        if (fieldList.size() == 0) {
            this.fieldList = castFieldList(this.insertTable);
        } else if (this.migration) {
            this.fieldList = fieldList;// because have validated by the implementation of Insert
        } else {
            final List<FieldMeta<?>> mergeFieldList;
            mergeFieldList = _DialectUtils.createNonChildFieldList((SingleTableMeta<?>) insertTable
                    , nonChildStmt.defaultValueMap()::containsKey);
            mergeFieldList.addAll(fieldList);// because have validated by the implementation of Insert
            this.fieldList = Collections.unmodifiableList(mergeFieldList);
        }

        final PrimaryFieldMeta<?> idField = this.insertTable.id();
        if (this.migration || idField.generatorType() != GeneratorType.POST) {
            this.returnId = null;
            this.idSelectionAlias = null;
        } else if (dialect.supportInsertReturning()) {
            //TODO
            throw new UnsupportedOperationException();
        } else if (this.duplicateKeyClause) {
            if (domainStmt.table() instanceof ChildTableMeta) {
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
    ValuesSyntaxInsertContext(ValuesSyntaxInsertContext parentContext, _Insert._ValuesSyntaxInsert stmt
            , ArmyDialect dialect, Visible visible) {
        super(dialect, true, visible);

        assert stmt instanceof _Insert._ChildInsert;

        this.migration = stmt.isMigration();
        final NullHandleMode handleMode = stmt.nullHandle();
        this.nullHandleMode = handleMode == null ? NullHandleMode.INSERT_DEFAULT : handleMode;
        this.preferLiteral = stmt.isPreferLiteral();

        this.duplicateKeyClause = stmt instanceof _Insert._DuplicateKeyClause;
        this.insertTable = stmt.table();

        assert this.insertTable instanceof ChildTableMeta
                && this.migration == parentContext.migration
                && this.nullHandleMode == parentContext.nullHandleMode
                && this.preferLiteral == parentContext.preferLiteral
                && parentContext.insertTable == ((ChildTableMeta<?>) this.insertTable).parentMeta();

        final List<FieldMeta<?>> fieldList = stmt.fieldList();
        if (fieldList.size() == 0) {
            this.fieldList = castFieldList(this.insertTable);
        } else if (this.migration) {
            assert fieldList.get(0) == this.insertTable.id();
            this.fieldList = fieldList;// because have validated by the implementation of Insert
        } else {
            final List<FieldMeta<?>> mergeFieldList;
            mergeFieldList = _DialectUtils.createChildFieldList((ChildTableMeta<?>) this.insertTable);
            mergeFieldList.addAll(fieldList);// because have validated by the implementation of Insert
            this.fieldList = Collections.unmodifiableList(mergeFieldList);
        }
        this.returnId = null;
        this.idSelectionAlias = null;

    }

    @Override
    public final TableMeta<?> insertTable() {
        return this.insertTable;
    }

    @Override
    public final void appendFieldList() {
        assert !this.columnListClauseEnd;
        final List<FieldMeta<?>> fieldList = this.fieldList;
        final int fieldSize = fieldList.size();
        final ArmyDialect dialect = this.dialect;
        final StringBuilder sqlBuilder = this.sqlBuilder
                .append(_Constant.SPACE_LEFT_PAREN);

        FieldMeta<?> field;
        for (int i = 0, actualIndex = 0; i < fieldSize; i++) {
            field = fieldList.get(i);
            if (!field.insertable()) {
                // fieldList have be checked,fieldList possibly is io.army.meta.TableMeta.fieldList()
                continue;
            }
            if (actualIndex > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            dialect.safeObjectName(field, sqlBuilder);
            actualIndex++;
        }
        sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

        this.columnListClauseEnd = true;
    }

    @Override
    public final void appendValueList() {
        assert this.columnListClauseEnd && !this.valuesClauseEnd;
        this.doAppendValuesList(this.fieldList);
        this.valuesClauseEnd = true;
    }

    @Override
    public final void appendField(String tableAlias, FieldMeta<?> field) {
        throw _Exceptions.unknownColumn(tableAlias, field);
    }

    @Override
    public final void appendField(final FieldMeta<?> field) {
        if (!(this.valuesClauseEnd && this.duplicateKeyClause && field.tableMeta() == this.insertTable)) {
            throw _Exceptions.unknownColumn(field);
        }
        final StringBuilder sqlBuilder = this.sqlBuilder
                .append(_Constant.SPACE);
        this.dialect.safeObjectName(field, sqlBuilder);

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

        final ArmyDialect dialect = this.dialect;
        //TODO for dialect table alias
        dialect.safeObjectName(returnId, sqlBuilder)
                .append(_Constant.SPACE_AS_SPACE);

        dialect.identifier(this.idSelectionAlias, sqlBuilder);
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

    @Override
    public final List<Selection> selectionList() {
        //TODO
        return Collections.emptyList();
    }


    abstract void doAppendValuesList(List<FieldMeta<?>> fieldList);

    final boolean isValuesClauseEnd() {
        return this.valuesClauseEnd;
    }


    static boolean isManageVisible(TableMeta<?> insertTable, Map<FieldMeta<?>, _Expression> defaultValueMap) {
        return insertTable instanceof SingleTableMeta
                && insertTable.containField(_MetaBridge.VISIBLE)
                && !defaultValueMap.containsKey(insertTable.getField(_MetaBridge.VISIBLE));
    }

    @SuppressWarnings("unchecked")
    private static List<FieldMeta<?>> castFieldList(final TableMeta<?> table) {
        final List<?> list;
        list = table.fieldList();
        return (List<FieldMeta<?>>) list;
    }


    static IllegalStateException parentStmtDontExecute(PrimaryFieldMeta<?> filed) {
        String m = String.format("parent stmt don't execute so %s parameter value is null", filed);
        return new IllegalStateException(m);
    }


}
