package io.army.dialect;

import io.army.annotation.GeneratorType;
import io.army.bean.ObjectAccessException;
import io.army.criteria.ItemPair;
import io.army.criteria.NullHandleMode;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Insert;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.PrimaryFieldMeta;
import io.army.meta.TableMeta;
import io.army.stmt.SimpleStmt;
import io.army.stmt._InsertStmtParams;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

final class AssignmentInsertContext extends StatementContext
        implements _AssignmentInsertContext, _InsertStmtParams._AssignmentParams {

    AssignmentInsertContext forSingle(_Insert._AssignmentInsert stmt, ArmyDialect dialect, Visible visible) {
        assert !(stmt instanceof _Insert._ChildAssignmentInsert);
        return new AssignmentInsertContext(stmt, stmt.table(), dialect, visible);
    }

    AssignmentInsertContext forParent(_Insert._AssignmentInsert parentStmt, ChildTableMeta<?> childTable
            , ArmyDialect dialect, Visible visible) {
        assert !(parentStmt instanceof _Insert._ChildAssignmentInsert);
        return new AssignmentInsertContext(parentStmt, childTable, dialect, visible);
    }

    AssignmentInsertContext forChild(AssignmentInsertContext parentContext, _Insert._ChildAssignmentInsert stmt
            , ArmyDialect dialect, Visible visible) {
        return new AssignmentInsertContext(parentContext, stmt, dialect, visible);
    }

    private final boolean migration;

    private final NullHandleMode nullHandleMode;

    private final boolean preferLiteral;

    private final boolean duplicateKeyClause;

    private final TableMeta<?> insertTable;

    private final TableMeta<?> domainTable;

    private final List<ItemPair> rowPairList;

    private final PrimaryFieldMeta<?> returnId;

    /**
     * @see #returnId
     */
    private final String idSelectionAlias;

    private final Map<FieldMeta<?>, Object> generatedMap;

    private Map<FieldMeta<?>, Object> tempGeneratedValueMap;

    private boolean assignmentClauseEnd;


    /**
     * <p>
     * For {@link  io.army.meta.SingleTableMeta}
     * </p>
     *
     * @see #forSingle(_Insert._AssignmentInsert, ArmyDialect, Visible)
     * @see #forParent(_Insert._AssignmentInsert, ChildTableMeta, ArmyDialect, Visible)
     */
    private AssignmentInsertContext(_Insert._AssignmentInsert stmt, TableMeta<?> domainTable
            , ArmyDialect dialect, Visible visible) {
        super(dialect, true, visible);

        this.migration = stmt.isMigration();
        final NullHandleMode handleMode;
        handleMode = stmt.nullHandle();
        this.nullHandleMode = handleMode == null ? NullHandleMode.INSERT_DEFAULT : handleMode;
        this.preferLiteral = stmt.isPreferLiteral();
        this.duplicateKeyClause = stmt instanceof _Insert._DuplicateKeyClause;

        this.insertTable = stmt.table();
        this.domainTable = domainTable;

        if (domainTable instanceof ChildTableMeta) {
            assert this.insertTable == ((ChildTableMeta<?>) domainTable).parentMeta();
        } else {
            assert this.insertTable == domainTable;
        }

        this.rowPairList = stmt.rowPairList();

        final PrimaryFieldMeta<?> idField = this.insertTable.id();
        if (this.migration || idField.generatorType() != GeneratorType.POST) {
            this.returnId = null;
            this.idSelectionAlias = null;
        } else if (dialect.supportInsertReturning()) {
            //TODO
            throw new UnsupportedOperationException();
        } else if (this.duplicateKeyClause) {
            if (domainTable instanceof ChildTableMeta) {
                throw _Exceptions.duplicateKeyAndPostIdInsert((ChildTableMeta<?>) domainTable);
            }
            this.returnId = null;
            this.idSelectionAlias = null;
        } else {
            this.returnId = idField;
            this.idSelectionAlias = idField.fieldName();
        }

        if (this.migration) {
            this.generatedMap = Collections.emptyMap();
            this.tempGeneratedValueMap = null;
        } else {
            final Map<FieldMeta<?>, Object> map = new HashMap<>();
            this.generatedMap = Collections.unmodifiableMap(map);
            this.tempGeneratedValueMap = map;
        }

    }

    /**
     * <p>
     * For {@link  io.army.meta.ChildTableMeta}
     * </p>
     *
     * @see #forChild(AssignmentInsertContext, _Insert._ChildAssignmentInsert, ArmyDialect, Visible)
     */
    private AssignmentInsertContext(AssignmentInsertContext parentContext, _Insert._ChildAssignmentInsert stmt
            , ArmyDialect dialect, Visible visible) {
        super(dialect, true, visible);

        this.migration = stmt.isMigration();
        final NullHandleMode handleMode;
        handleMode = stmt.nullHandle();
        this.nullHandleMode = handleMode == null ? NullHandleMode.INSERT_DEFAULT : handleMode;
        this.preferLiteral = stmt.isPreferLiteral();
        this.duplicateKeyClause = stmt instanceof _Insert._DuplicateKeyClause;

        this.insertTable = stmt.table();
        this.domainTable = this.insertTable;

        assert this.insertTable instanceof ChildTableMeta
                && this.migration == parentContext.migration
                && this.nullHandleMode == parentContext.nullHandleMode
                && this.preferLiteral == parentContext.preferLiteral
                && parentContext.insertTable == ((ChildTableMeta<?>) this.insertTable).parentMeta();

        this.rowPairList = stmt.rowPairList();

        this.generatedMap = parentContext.generatedMap;
        this.tempGeneratedValueMap = null;
        this.returnId = null;
        this.idSelectionAlias = null;

    }


    @Override
    public TableMeta<?> insertTable() {
        return this.insertTable;
    }

    @Override
    public void appendAssignmentClause() {
        assert !this.assignmentClauseEnd;

        this.assignmentClauseEnd = true;
    }


    @Override
    public void appendField(String tableAlias, FieldMeta<?> field) {
        throw _Exceptions.unknownColumn(tableAlias, field);
    }

    @Override
    public void appendField(final FieldMeta<?> field) {
        if (!(this.assignmentClauseEnd && this.duplicateKeyClause && field.tableMeta() == this.insertTable)) {
            throw _Exceptions.unknownColumn(field);
        }
        final StringBuilder sqlBuilder = this.sqlBuilder
                .append(_Constant.SPACE);
        this.dialect.safeObjectName(field, sqlBuilder);
    }

    @Override
    public SimpleStmt build() {
        return null;
    }


    @Override
    public PrimaryFieldMeta<?> idField() {
        final PrimaryFieldMeta<?> field = this.returnId;
        assert field != null;
        return field;
    }

    @Override
    public String idReturnAlias() {
        final String alias = this.idSelectionAlias;
        assert alias != null;
        return alias;
    }

    @Override
    public BiFunction<Integer, Object, Object> function() {
        return null;
    }


    private static final class RowObjectWrapper extends ValuesInsertContext.RowWrapper {

        private Map<FieldMeta<?>, Object> generatedMap;

        private List<ItemPair> rowPairList;

        private Map<FieldMeta<?>, ItemPair> parentSinglePairMap;

        private RowObjectWrapper(TableMeta<?> domainTable) {
            super(domainTable);
        }

        @Override
        public Object get(final String propertyName) throws ObjectAccessException {
            final TableMeta<?> domainTable = this.domainTable;
            final FieldMeta<?> field;
            field = domainTable.tryGetComplexFiled(propertyName);
            if (field == null) {
                throw _Exceptions.nonReadableProperty(domainTable, propertyName);
            }
            final Map<FieldMeta<?>, Object> generatedMap = this.generatedMap;
            assert generatedMap != null;
            Object value;
            value = generatedMap.get(field);
            if (value != null) {
                return value;
            }
            final Map<FieldMeta<?>, ItemPair> parentSinglePairMap = this.parentSinglePairMap;
            assert parentSinglePairMap != null;

            return null;
        }

        @Override
        Map<FieldMeta<?>, Object> getGeneratedMap() {
            return this.generatedMap;
        }


    }//RowObjectWrapper


}
