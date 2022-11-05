package io.army.dialect;

import io.army.annotation.GeneratorType;
import io.army.criteria.LiteralMode;
import io.army.criteria.SqlValueParam;
import io.army.criteria.Visible;
import io.army.criteria.impl._Pair;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.lang.Nullable;
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

final class AssignmentInsertContext extends InsertContext
        implements _AssignmentInsertContext, _InsertStmtParams._AssignmentParams {

    static AssignmentInsertContext forSingle(@Nullable _SqlContext outerContext, _Insert._AssignmentInsert stmt
            , ArmyParser0 dialect, Visible visible) {
        assert !(stmt instanceof _Insert._ChildAssignmentInsert);
        return new AssignmentInsertContext((StatementContext) outerContext, stmt, dialect, visible);
    }

    static AssignmentInsertContext forParent(@Nullable _SqlContext outerContext, _Insert._ChildAssignmentInsert stmt
            , ArmyParser0 dialect, Visible visible) {
        assert outerContext == null || outerContext instanceof LiteralMultiStmtContext;
        if (outerContext != null && stmt.parentStmt().table().id().generatorType() == GeneratorType.POST) {
            throw _Exceptions.multiStmtDontSupportPostParent((ChildTableMeta<?>) stmt.table());
        }
        return new AssignmentInsertContext((StatementContext) outerContext, stmt, dialect, visible);
    }

    static AssignmentInsertContext forChild(_Insert._ChildAssignmentInsert stmt
            , AssignmentInsertContext parentContext) {
        return new AssignmentInsertContext(stmt, parentContext);
    }


    private final List<_Pair<FieldMeta<?>, _Expression>> pairList;


    private final AssignmentWrapper rowWrapper;


    /**
     * <p>
     * For {@link  io.army.meta.SingleTableMeta}
     * </p>
     *
     * @see #forSingle(_SqlContext, _Insert._AssignmentInsert, ArmyParser0, Visible)
     * @see #forParent(_SqlContext, _Insert._ChildAssignmentInsert, ArmyParser0, Visible)
     */
    private AssignmentInsertContext(@Nullable StatementContext outerContext, _Insert._AssignmentInsert domainStmt
            , ArmyParser0 dialect, Visible visible) {
        super(outerContext, domainStmt, dialect, visible);

        if (domainStmt instanceof _Insert._ChildAssignmentInsert) {
            this.pairList = ((_Insert._ChildAssignmentInsert) domainStmt).parentStmt().assignmentPairList();
        } else {
            this.pairList = domainStmt.assignmentPairList();
        }

        this.rowWrapper = new AssignmentWrapper(this, domainStmt);
        assert this.rowWrapper.domainTable == domainTable;

    }

    /**
     * <p>
     * For {@link  io.army.meta.ChildTableMeta}
     * </p>
     *
     * @see #forChild(_Insert._ChildAssignmentInsert, AssignmentInsertContext)
     */
    private AssignmentInsertContext(_Insert._ChildAssignmentInsert stmt
            , AssignmentInsertContext parentContext) {
        super(stmt, parentContext);

        this.pairList = stmt.assignmentPairList();
        ;
        this.rowWrapper = parentContext.rowWrapper;
        assert this.rowWrapper.domainTable == this.insertTable;
        assert this.pairList.size() == this.rowWrapper.childPairMap.size();

    }


    @Override
    void doAppendAssignments() {

        final ArmyParser0 dialect = this.parser;

        final TableMeta<?> insertTable = this.insertTable;

        final AssignmentWrapper wrapper = this.rowWrapper;
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
    public Function<Object, Object> function() {
        final DelayIdParam delayIdParam = this.rowWrapper.delayIdParam;
        assert delayIdParam != null && isValuesClauseEnd() && this.insertTable instanceof ParentTableMeta;
        this.rowWrapper.delayIdParam = null;
        return delayIdParam::parentPostId;
    }

    private void appendArmyManageFields() {
        assert !this.migration;

        final ArmyParser0 dialect = this.parser;
        final StringBuilder sqlBuilder = this.sqlBuilder;

        final Map<FieldMeta<?>, Object> generatedMap = this.rowWrapper.generatedMap;

        final List<FieldMeta<?>> fieldList = this.fieldList;
        final int fieldSize = fieldList.size();
        assert fieldSize > 0;

        final LiteralMode literalMode = this.literalMode;
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
            } else if ((value = generatedMap.get(field)) != null) {
                this.appendInsertValue(literalMode, field, value);
            } else {
                assert mockEnv;
                this.appendInsertValue(literalMode, field, null);
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

    private static final class AssignmentWrapper extends _DialectUtils.ExpRowWrapper {

        private final Map<FieldMeta<?>, _Expression> nonChildPairMap;

        private final Map<FieldMeta<?>, _Expression> childPairMap;

        private final Map<FieldMeta<?>, Object> generatedMap;

        private Map<FieldMeta<?>, Object> tempGeneratedMap;

        private DelayIdParam delayIdParam;

        private AssignmentWrapper(AssignmentInsertContext context, _Insert._AssignmentInsert domainStmt) {
            super(domainStmt.table(), context.parser.mappingEnv());

            if (domainStmt instanceof _Insert._ChildAssignmentInsert) {
                this.nonChildPairMap = ((_Insert._ChildAssignmentInsert) domainStmt).parentStmt().assignmentMap();
                this.childPairMap = domainStmt.assignmentMap();
            } else {
                this.nonChildPairMap = domainStmt.assignmentMap();
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
        public void set(final FieldMeta<?> field, final @Nullable Object value) {
            final Map<FieldMeta<?>, Object> map = this.tempGeneratedMap;
            assert map != null;
            if (value == null) {
                //here mock environment
                return;
            }
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
        public TypeMeta typeMeta() {
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
