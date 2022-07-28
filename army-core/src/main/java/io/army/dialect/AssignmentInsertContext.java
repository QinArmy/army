package io.army.dialect;

import io.army.annotation.GeneratorType;
import io.army.criteria.SqlValueParam;
import io.army.criteria.Visible;
import io.army.criteria.impl._Pair;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.lang.Nullable;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.stmt.SimpleStmt;
import io.army.stmt.SingleParam;
import io.army.stmt.Stmts;
import io.army.stmt._InsertStmtParams;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

final class AssignmentInsertContext extends StatementContext
        implements _AssignmentInsertContext, _InsertStmtParams._AssignmentParams {

    static AssignmentInsertContext forSingle(_Insert._AssignmentInsert stmt, ArmyDialect dialect, Visible visible) {
        assert !(stmt instanceof _Insert._ChildAssignmentInsert);
        return new AssignmentInsertContext(stmt, dialect, visible);
    }

    static AssignmentInsertContext forParent(_Insert._ChildAssignmentInsert stmt, ArmyDialect dialect, Visible visible) {
        return new AssignmentInsertContext(stmt, dialect, visible);
    }

    static AssignmentInsertContext forChild(AssignmentInsertContext parentContext, _Insert._ChildAssignmentInsert stmt
            , ArmyDialect dialect, Visible visible) {
        return new AssignmentInsertContext(parentContext, stmt, dialect, visible);
    }

    private final TableMeta<?> insertTable;

    private final boolean migration;

    private final boolean preferLiteral;

    private final boolean duplicateKeyClause;

    private final List<_Pair<FieldMeta<?>, _Expression>> pairList;

    /**
     * the list of the fields that is managed by army
     */
    private final List<FieldMeta<?>> fieldList;

    private final RowObjectWrapper rowWrapper;


    private final PrimaryFieldMeta<?> returnId;

    /**
     * @see #returnId
     */
    private final String idSelectionAlias;

    private boolean assignmentClauseEnd;


    /**
     * <p>
     * For {@link  io.army.meta.SingleTableMeta}
     * </p>
     *
     * @see #forSingle(_Insert._AssignmentInsert, ArmyDialect, Visible)
     * @see #forParent(_Insert._ChildAssignmentInsert, ArmyDialect, Visible)
     */
    private AssignmentInsertContext(_Insert._AssignmentInsert domainStmt, ArmyDialect dialect, Visible visible) {
        super(dialect, true, visible);

        final TableMeta<?> domainTable = domainStmt.table();

        final _Insert._AssignmentInsert nonChildStmt;
        if (domainStmt instanceof _Insert._ChildAssignmentInsert) {
            nonChildStmt = ((_Insert._ChildAssignmentInsert) domainStmt).parentStmt();
            this.insertTable = nonChildStmt.table();
        } else {
            nonChildStmt = domainStmt;
            this.insertTable = domainTable;
        }
        assert this.insertTable instanceof SingleTableMeta;

        this.migration = nonChildStmt.isMigration();
        this.preferLiteral = nonChildStmt.isPreferLiteral();
        this.duplicateKeyClause = nonChildStmt instanceof _Insert._DuplicateKeyClause;
        this.pairList = nonChildStmt.pairList();

        final Map<FieldMeta<?>, _Expression> pairMap;
        pairMap = nonChildStmt.pairMap();
        assert pairMap.size() == this.pairList.size();

        if (this.migration) {
            this.fieldList = Collections.emptyList();
        } else {
            this.fieldList = createNonChildFieldList((SingleTableMeta<?>) this.insertTable, pairMap::containsKey);
        }

        final PrimaryFieldMeta<?> idField = this.insertTable.id();
        if (this.migration || domainTable instanceof SingleTableMeta || idField.generatorType() != GeneratorType.POST) {
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

        this.rowWrapper = new RowObjectWrapper(this, domainStmt);
        assert this.rowWrapper.domainTable == domainTable;

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

        this.insertTable = stmt.table();
        this.migration = stmt.isMigration();
        this.preferLiteral = stmt.isPreferLiteral();
        this.duplicateKeyClause = stmt instanceof _Insert._DuplicateKeyClause;

        assert this.insertTable instanceof ChildTableMeta
                && this.migration == parentContext.migration
                && this.preferLiteral == parentContext.preferLiteral
                && parentContext.insertTable == ((ChildTableMeta<?>) this.insertTable).parentMeta();

        this.pairList = stmt.pairList();

        if (this.migration) {
            this.fieldList = Collections.emptyList();
        } else {
            this.fieldList = createChildFieldList((ChildTableMeta<?>) this.insertTable);
        }
        this.returnId = null;
        this.idSelectionAlias = null;
        this.rowWrapper = parentContext.rowWrapper;
        assert this.rowWrapper.domainTable == this.insertTable;
        assert this.pairList.size() == this.rowWrapper.childPairMap.size();

    }


    @Override
    public TableMeta<?> insertTable() {
        return this.insertTable;
    }

    @Override
    public void appendAssignmentClause() {
        assert !this.assignmentClauseEnd;

        final ArmyDialect dialect = this.dialect;

        final TableMeta<?> insertTable = this.insertTable;

        final RowObjectWrapper wrapper = this.rowWrapper;
        //1. generate or validate
        if (insertTable instanceof SingleTableMeta) {

            final FieldValueGenerator generator;
            generator = dialect.getGenerator();
            if (this.migration) {
                //use wrapper.domainTable not this.insertTable
                generator.validate(wrapper.domainTable, wrapper);
            } else {
                final boolean manageVisible;
                manageVisible = insertTable.containField(_MetaBridge.VISIBLE)
                        && !wrapper.nonChildPairMap.containsKey(insertTable.getField(_MetaBridge.VISIBLE));
                //use wrapper.domainTable not this.insertTable
                generator.generate(wrapper.domainTable, manageVisible, wrapper);
                wrapper.tempGeneratedMap = null;  //clear
            }

        }

        //2. SET keyword and space
        final StringBuilder sqlBuilder = this.sqlBuilder
                .append(_Constant.SPACE_SET_SPACE);

        //3. the fields that is managed by army
        if (!this.migration) {
            this.appendArmyManageFields();
        } else if (insertTable instanceof ChildTableMeta) {
            final _Expression expression;
            expression = wrapper.nonChildPairMap.get(insertTable.nonChildId());
            assert expression instanceof SqlValueParam.SingleNonNamedValue;

            dialect.safeObjectName(insertTable.id(), sqlBuilder)
                    .append(_Constant.SPACE_EQUAL);
            expression.appendSql(this);
        }

        //4. assignment clause of application developer
        final List<_Pair<FieldMeta<?>, _Expression>> pairList = this.pairList;

        final int pariSize = pairList.size();
        _Pair<FieldMeta<?>, _Expression> pair;
        FieldMeta<?> field;
        for (int i = 0; i < pariSize; i++) {
            if (i > 0 || !this.migration || insertTable instanceof ChildTableMeta) {
                sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);
            }
            pair = pairList.get(i);
            field = pair.first;
            assert !(field instanceof PrimaryFieldMeta && insertTable instanceof ChildTableMeta); // child id must be managed by army

            dialect.safeObjectName(field, sqlBuilder)
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
        final SimpleStmt stmt;
        if (this.returnId == null) {
            stmt = Stmts.minSimple(this);
        } else {
            stmt = Stmts.assignmentPost(this);
        }
        return stmt;
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
    public Function<Object, Object> function() {
        final DelayIdParam delayIdParam = this.rowWrapper.delayIdParam;
        assert delayIdParam != null && this.assignmentClauseEnd && this.insertTable instanceof ParentTableMeta;
        this.rowWrapper.delayIdParam = null;
        return delayIdParam::parentPostId;
    }

    private void appendArmyManageFields() {
        assert !this.migration;

        final ArmyDialect dialect = this.dialect;
        final StringBuilder sqlBuilder = this.sqlBuilder;

        final Map<FieldMeta<?>, Object> generatedMap = this.rowWrapper.generatedMap;

        final List<FieldMeta<?>> fieldList = this.fieldList;
        final int fieldSize = fieldList.size();
        assert fieldSize > 0;

        final boolean preferLiteral = this.preferLiteral;
        final boolean mockEnv = dialect.isMockEnv();

        FieldMeta<?> field;
        Object value;
        GeneratorType generatorType;
        DelayIdParam delayIdParam = null;
        for (int i = 0; i < fieldSize; i++) {
            field = fieldList.get(i);
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);
            }

            dialect.safeObjectName(field, sqlBuilder)
                    .append(_Constant.SPACE_EQUAL);

            if (field instanceof PrimaryFieldMeta
                    && this.insertTable instanceof ChildTableMeta
                    && (generatorType = this.insertTable.nonChildId().generatorType()) != GeneratorType.PRECEDE) {//child id must be managed by army

                if (generatorType == null) {
                    final _Expression expression;
                    expression = this.rowWrapper.nonChildPairMap.get(this.insertTable.nonChildId());
                    assert expression instanceof SqlValueParam.SingleNonNamedValue; //validated by FieldValueGenerator
                    expression.appendSql(this);
                } else if (generatorType == GeneratorType.POST) {
                    assert field.tableMeta() == this.insertTable && delayIdParam == null;
                    delayIdParam = new DelayIdParam((PrimaryFieldMeta<?>) field);
                    this.rowWrapper.delayIdParam = delayIdParam;
                    this.appendParam(delayIdParam);
                } else {
                    //no bug,never here
                    throw _Exceptions.unexpectedEnum(generatorType);
                }
            } else if ((value = generatedMap.get(field)) == null) {
                assert mockEnv;
                if (preferLiteral) {
                    sqlBuilder.append(_Constant.SPACE_NULL);
                } else {
                    this.appendParam(SingleParam.build(field, null));
                }
            } else if (preferLiteral && field.mappingType() instanceof _ArmyNoInjectionMapping) {// TODO field codec
                sqlBuilder.append(_Constant.SPACE);
                dialect.literal(field, value, sqlBuilder);
            } else {
                this.appendParam(SingleParam.build(field, value));
            }

        }


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

    private static final class RowObjectWrapper extends _DialectUtils.ExpRowWrapper {

        private final Map<FieldMeta<?>, _Expression> nonChildPairMap;

        private final Map<FieldMeta<?>, _Expression> childPairMap;

        private final Map<FieldMeta<?>, Object> generatedMap;

        private Map<FieldMeta<?>, Object> tempGeneratedMap;

        private DelayIdParam delayIdParam;

        private RowObjectWrapper(AssignmentInsertContext context, _Insert._AssignmentInsert domainStmt) {
            super(domainStmt.table(), context.dialect.mappingEnv());

            if (domainStmt instanceof _Insert._ChildAssignmentInsert) {
                this.nonChildPairMap = ((_Insert._ChildAssignmentInsert) domainStmt).parentStmt().pairMap();
                this.childPairMap = domainStmt.pairMap();
            } else {
                this.nonChildPairMap = domainStmt.pairMap();
                this.childPairMap = Collections.emptyMap();
            }

            if (context.migration) {
                this.generatedMap = Collections.emptyMap();
                this.tempGeneratedMap = null;
            } else {
                int maxSize = 6;

                if (domainTable instanceof ChildTableMeta) {
                    maxSize += ((ChildTableMeta<?>) domainTable).parentMeta().fieldChain().size();
                }
                maxSize += domainTable.fieldChain().size();

                final Map<FieldMeta<?>, Object> map = new HashMap<>((int) (maxSize / 0.75F));
                this.generatedMap = Collections.unmodifiableMap(map);
                this.tempGeneratedMap = map;
            }


        }


        @Override
        public void set(final FieldMeta<?> field, final Object value) {
            final Map<FieldMeta<?>, Object> map = this.tempGeneratedMap;
            assert map != null;
            map.put(field, value);
            if (field instanceof PrimaryFieldMeta) {
                final TableMeta<?> fieldTable = field.tableMeta();
                assert fieldTable instanceof SingleTableMeta;
                if (fieldTable != this.domainTable) {
                    map.put(this.domainTable.id(), value);
                }
            }
        }

        @Override
        Object getGeneratedValue(final FieldMeta<?> field) {
            return this.generatedMap.get(field);
        }

        @Override
        _Expression getExpression(final FieldMeta<?> field) {
            final _Expression expression;
            if (field.tableMeta() instanceof SingleTableMeta) {
                expression = this.nonChildPairMap.get(field);
            } else {
                expression = this.childPairMap.get(field);
            }
            return expression;
        }


    }//RowObjectWrapper


    private static final class DelayIdParam implements SingleParam {

        private final PrimaryFieldMeta<?> field;

        private Object idValue;

        private DelayIdParam(PrimaryFieldMeta<?> field) {
            this.field = field;
        }

        @Override
        public ParamMeta paramMeta() {
            return this.field;
        }

        @Nullable
        private Object parentPostId(final Object idValue) {
            final Object oldValue = this.idValue;
            if (oldValue == null) {
                this.idValue = idValue;
            }
            return oldValue;
        }

        @Override
        public Object value() {
            final Object idValue;
            idValue = this.idValue;
            if (idValue == null) {
                throw ValuesSyntaxInsertContext.parentStmtDontExecute(this.field);
            }
            return idValue;
        }
    }//DelayIdParam


}