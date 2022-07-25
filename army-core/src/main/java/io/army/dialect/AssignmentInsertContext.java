package io.army.dialect;

import io.army.annotation.GeneratorType;
import io.army.criteria.NullHandleMode;
import io.army.criteria.Visible;
import io.army.criteria.impl._Pair;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.stmt.SimpleStmt;
import io.army.stmt._InsertStmtParams;
import io.army.util._Exceptions;

import java.util.*;
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


    private final List<_Pair<FieldMeta<?>, _Expression>> rowPairList;

    private final Map<FieldMeta<?>, _Expression> rowPairMap;

    private final RowObjectWrapper rowWrapper;


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

        assert this.insertTable instanceof SingleTableMeta;
        if (domainTable instanceof ChildTableMeta) {
            assert this.insertTable == ((ChildTableMeta<?>) domainTable).parentMeta();
        } else {
            assert this.insertTable == domainTable;
        }

        this.rowPairList = stmt.rowPairList();
        this.rowPairMap = stmt.rowPairMap();

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

        final FieldMeta<?> visibleField;
        visibleField = this.insertTable.tryGetComplexFiled(_MetaBridge.VISIBLE);
        final boolean manageVisible;
        manageVisible = visibleField != null && !this.rowPairMap.containsKey(visibleField);
        this.rowWrapper = new RowObjectWrapper(this, manageVisible);

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
        this.rowPairMap = stmt.rowPairMap();

        this.generatedMap = parentContext.generatedMap;
        this.tempGeneratedValueMap = null;
        this.returnId = null;
        this.idSelectionAlias = null;

        this.rowWrapper = parentContext.rowWrapper;

    }


    @Override
    public TableMeta<?> insertTable() {
        return this.insertTable;
    }

    @Override
    public void appendAssignmentClause() {
        assert !this.assignmentClauseEnd;

        final ArmyDialect dialect = this.dialect;

        final TableMeta<?> insertTable;
        insertTable = this.insertTable;


        if (insertTable instanceof SingleTableMeta) {
            final RowObjectWrapper wrapper = this.rowWrapper;
            final FieldValueGenerator generator;
            generator = this.dialect.getGenerator();
            if (this.migration) {
                //use this.domainTable not this.insertTable
                generator.validate(this.domainTable, wrapper);
            } else {
                //use this.domainTable not this.insertTable
                generator.generate(this.domainTable, wrapper.manageVisible, wrapper);
            }

        }

        final StringBuilder sqlBuilder = this.sqlBuilder
                .append(_Constant.SPACE_SET);
        if (!this.migration) {

        }
        FieldMeta<?> field;
        field = insertTable.id();
        if (insertTable instanceof SingleTableMeta) {
            if (field.insertable()) {

            }
        }


        final List<_Pair<FieldMeta<?>, _Expression>> pairList = this.rowPairList;

        final int pariSize = pairList.size();
        _Pair<FieldMeta<?>, _Expression> pair;
        for (int i = 0; i < pariSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);
            } else {
                sqlBuilder.append(_Constant.SPACE);
            }
            pair = pairList.get(i);
            dialect.safeObjectName(pair.first, sqlBuilder)
                    .append(_Constant.SPACE_EQUAL);
            pair.second.appendSql(this);
        }

        this.assignmentClauseEnd = true;
    }


    @Override
    public void appendField(String tableAlias, FieldMeta<?> field) {
        throw _Exceptions.unknownColumn(tableAlias, field);
    }

    @Override
    public void appendField(final FieldMeta<?> field) {
        if (field.tableMeta() != this.insertTable) {
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


    private static List<FieldMeta<?>> createArmyManageFieldList(final TableMeta<?> insertTable
            , final Map<FieldMeta<?>, _Expression> rowPairMap) {

        final int fieldSize;
        fieldSize = 6 + insertTable.fieldChain().size();

        final List<FieldMeta<?>> fieldList = new ArrayList<>(fieldSize);
        final Map<FieldMeta<?>, Boolean> fieldMap = new HashMap<>((int) (fieldSize / 0.75F));

        if (insertTable instanceof SingleTableMeta) {
            _DialectUtils.appendSingleTableField((SingleTableMeta<?>) insertTable, fieldList, fieldMap
                    , rowPairMap::containsKey);
        } else {
            _DialectUtils.appendChildTableField((ChildTableMeta<?>) insertTable, fieldList, fieldMap);
        }
        return Collections.unmodifiableList(fieldList);

    }


    private static final class RowObjectWrapper extends _DialectUtils.ExpRowWrapper {

        private final Map<FieldMeta<?>, _Expression> nonChildPairMap;

        private final

        private Map<FieldMeta<?>, _Expression> childPairMap;


        private final Map<FieldMeta<?>, Object> generatedMap;

        private Map<FieldMeta<?>, Object> tempGeneratedMap;

        private RowObjectWrapper(AssignmentInsertContext context, boolean manageVisible) {
            super(context.domainTable, context.dialect.mappingEnv());
            this.nonChildPairMap = context.rowPairMap;

            final Map<FieldMeta<?>, Object> map = new HashMap<>();
            this.generatedMap = Collections.unmodifiableMap(map);
            this.tempGeneratedMap = map;

        }


        @Override
        public void set(final FieldMeta<?> field, final Object value) {
            final Map<FieldMeta<?>, Object> map = this.tempGeneratedMap;
            assert map != null;
            map.put(field, value);
            if (field instanceof PrimaryFieldMeta && field.tableMeta() != this.domainTable) {
                map.put(this.domainTable.id(), value);
            }
        }

        @Override
        Object getGeneratedValue(final FieldMeta<?> field) {
            return this.generatedMap.get(field);
        }

        @Override
        _Expression getExpression(final FieldMeta<?> field) {
            return null;
        }


    }//RowObjectWrapper


}
