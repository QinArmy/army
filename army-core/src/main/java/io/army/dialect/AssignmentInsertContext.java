package io.army.dialect;

import io.army.annotation.GeneratorType;
import io.army.criteria.LiteralMode;
import io.army.criteria.SqlValueParam;
import io.army.criteria.Visible;
import io.army.criteria.impl._Pair;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.meta.*;
import io.army.stmt.InsertStmtParams;
import io.army.stmt.SingleParam;
import io.army.struct.CodeEnum;
import io.army.util._Collections;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.ObjIntConsumer;

final class AssignmentInsertContext extends InsertContext
        implements _AssignmentInsertContext, InsertStmtParams {

    static AssignmentInsertContext forSingle(@Nullable _SqlContext outerContext, _Insert._AssignmentInsert stmt
            , ArmyParser dialect, Visible visible) {
        assert !(stmt instanceof _Insert._ChildAssignmentInsert);
        return new AssignmentInsertContext((StatementContext) outerContext, stmt, dialect, visible);
    }

    static AssignmentInsertContext forParent(@Nullable _SqlContext outerContext, _Insert._ChildAssignmentInsert stmt
            , ArmyParser dialect, Visible visible) {
        assert outerContext == null || outerContext instanceof MultiStmtContext;

        return new AssignmentInsertContext((StatementContext) outerContext, stmt, dialect, visible);
    }

    static AssignmentInsertContext forChild(@Nullable _SqlContext outerContext, _Insert._ChildAssignmentInsert stmt
            , AssignmentInsertContext parentContext) {
        return new AssignmentInsertContext((StatementContext) outerContext, stmt, parentContext);
    }


    private final List<_Pair<FieldMeta<?>, _Expression>> pairList;


    private final AssignmentWrapper rowWrapper;


    /**
     * <p>
     * For {@link  io.army.meta.SingleTableMeta}
     *
     * @see #forSingle(_SqlContext, _Insert._AssignmentInsert, ArmyParser, Visible)
     * @see #forParent(_SqlContext, _Insert._ChildAssignmentInsert, ArmyParser, Visible)
     */
    private AssignmentInsertContext(@Nullable StatementContext outerContext, _Insert._AssignmentInsert domainStmt,
                                    ArmyParser dialect, Visible visible) {
        super(outerContext, domainStmt, dialect, visible);

        if (domainStmt instanceof _Insert._ChildAssignmentInsert) {
            this.pairList = ((_Insert._ChildAssignmentInsert) domainStmt).parentStmt().assignmentPairList();
        } else {
            this.pairList = domainStmt.assignmentPairList();
        }

        this.rowWrapper = new AssignmentWrapper(this, domainStmt);

    }

    /**
     * <p>
     * For {@link  io.army.meta.ChildTableMeta}
     *
     * @see #forChild(_SqlContext, _Insert._ChildAssignmentInsert, AssignmentInsertContext)
     */
    private AssignmentInsertContext(@Nullable StatementContext outerContext, _Insert._ChildAssignmentInsert stmt,
                                    AssignmentInsertContext parentContext) {
        super(outerContext, stmt, parentContext);

        this.pairList = stmt.assignmentPairList();
        this.rowWrapper = parentContext.rowWrapper;
        assert this.rowWrapper.domainTable == this.insertTable;
        assert this.pairList.size() == this.rowWrapper.childPairMap.size();

    }


    @Override
    void doAppendAssignments() {

        final ArmyParser parser = this.parser;
        final TableMeta<?> insertTable = this.insertTable;
        final AssignmentWrapper wrapper = this.rowWrapper;
        //1. generate or validate
        if (insertTable instanceof SingleTableMeta) {

            final FieldValueGenerator generator = parser.generator;
            if (this.migration) {
                //use wrapper.domainTable not this.insertTable
                generator.validate(wrapper.domainTable, wrapper);
            } else {
                //use wrapper.domainTable not this.insertTable
                generator.generate(wrapper.domainTable, wrapper);
                wrapper.tempGeneratedMap = null;  //clear
            }
        }

        //2. SET keyword and space
        final StringBuilder sqlBuilder;
        sqlBuilder = this.sqlBuilder.append(_Constant.SPACE_SET_SPACE);

        //3. the fields that is managed by army
        if (!this.migration) {
            this.appendArmyManageFields();
        } else if (insertTable instanceof ChildTableMeta) {
            parser.safeObjectName(insertTable.id(), sqlBuilder)
                    .append(_Constant.SPACE_EQUAL);

            final _Expression expression;
            expression = wrapper.nonChildPairMap.get(insertTable.nonChildId());
            assert expression instanceof SqlValueParam.SingleAnonymousValue; //validated by FieldValueGenerator
            expression.appendSql(sqlBuilder, this);
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

            parser.safeObjectName(field, sqlBuilder)
                    .append(_Constant.SPACE_EQUAL);
            pair.second.appendSql(sqlBuilder, this);
        }

    }


    @Override
    public int rowSize() {
        return 1;
    }

    @Override
    public ObjIntConsumer<Object> idConsumer() {
        final DelayIdParam delayIdParam = this.rowWrapper.delayIdParam;
        assert delayIdParam != null && isValuesClauseEnd() && this.insertTable instanceof ParentTableMeta;
        this.rowWrapper.delayIdParam = null;
        return delayIdParam::parentPostId;
    }

    private void appendArmyManageFields() {
        assert !this.migration;

        final ArmyParser parser = this.parser;
        final StringBuilder sqlBuilder = this.sqlBuilder;
        final AssignmentWrapper rowWrapper = this.rowWrapper;
        final TableMeta<?> insertTable = this.insertTable, domainTable = rowWrapper.domainTable;

        final FieldMeta<?> discriminator = domainTable.discriminator();


        final Map<FieldMeta<?>, Object> generatedMap = rowWrapper.generatedMap;

        final List<FieldMeta<?>> fieldList = this.fieldList;
        final int fieldSize = fieldList.size();
        assert fieldSize > 0;

        final LiteralMode literalMode = this.literalMode;
        final boolean mockEnv = parser.mockEnv;

        FieldMeta<?> field;
        Object value, idValue = null;

        for (int i = 0; i < fieldSize; i++) {
            field = fieldList.get(i);
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);
            }

            parser.safeObjectName(field, sqlBuilder)
                    .append(_Constant.SPACE_EQUAL);

            if (field == discriminator) {
                appendDiscriminator();
                continue;
            }

            if (!(field instanceof PrimaryFieldMeta)) {//child id must be managed by army
                value = generatedMap.get(field);
                assert value != null || mockEnv;
                this.appendInsertValue(literalMode, field, value);
                continue;
            }

            assert idValue == null;
            final GeneratorType generatorType;
            generatorType = insertTable.nonChildId().generatorType();
            if (generatorType == null) {
                final _Expression expression;
                expression = rowWrapper.nonChildPairMap.get(insertTable.nonChildId());
                assert expression instanceof SqlValueParam.SingleAnonymousValue; //validated by FieldValueGenerator
                expression.appendSql(sqlBuilder, this);
                idValue = expression;
            } else if (generatorType == GeneratorType.PRECEDE) {
                idValue = generatedMap.get(field);
                assert idValue != null || mockEnv;
                this.appendInsertValue(literalMode, field, idValue);
                idValue = Boolean.TRUE;
            } else if (generatorType == GeneratorType.POST) {
                assert insertTable instanceof ChildTableMeta;
                assert rowWrapper.delayIdParam == null;
                assert field.tableMeta() == insertTable;

                final DelayIdParam delayIdParam;
                delayIdParam = new DelayIdParam((PrimaryFieldMeta<?>) field);
                rowWrapper.delayIdParam = delayIdParam;
                this.appendParam(delayIdParam);
                idValue = delayIdParam;
            } else {
                //no bug,never here
                throw _Exceptions.unexpectedEnum(generatorType);
            }


        }


    }


    private void appendDiscriminator() {
        assert this.insertTable instanceof ParentTableMeta;

        final TableMeta<?> domainTable = this.rowWrapper.domainTable;
        final FieldMeta<?> discriminator = domainTable.discriminator();

        final String discriminatorLiteral;
        final SingleParam discriminatorParam;
        if (domainTable instanceof SimpleTableMeta) {
            discriminatorLiteral = null;
            discriminatorParam = null;
        } else if (this.literalMode == LiteralMode.DEFAULT) {
            assert discriminator != null;
            final CodeEnum codeEnum = domainTable.discriminatorValue();
            assert codeEnum != null;
            discriminatorLiteral = null;
            discriminatorParam = SingleParam.build(discriminator.mappingType(), codeEnum);
        } else {
            final CodeEnum codeEnum = domainTable.discriminatorValue();
            assert codeEnum != null;
            discriminatorLiteral = Integer.toString(codeEnum.code());
            discriminatorParam = null;
        }

        if (discriminatorParam == null) {
            assert discriminatorLiteral != null;
            this.sqlBuilder.append(_Constant.SPACE)
                    .append(discriminatorLiteral);
        } else {
            appendParam(discriminatorParam);
        }

    }


    private static final class AssignmentWrapper extends ExpRowWrapper {

        private final boolean mockEnv;

        private final Map<FieldMeta<?>, _Expression> nonChildPairMap;

        private final Map<FieldMeta<?>, _Expression> childPairMap;

        private final Map<FieldMeta<?>, Object> generatedMap;


        private Map<FieldMeta<?>, Object> tempGeneratedMap;

        private DelayIdParam delayIdParam;

        private AssignmentWrapper(AssignmentInsertContext context, _Insert._AssignmentInsert domainStmt) {
            super(context, domainStmt);
            this.mockEnv = context.parser.mockEnv;
            if (domainStmt instanceof _Insert._ChildAssignmentInsert) {
                assert context.insertTable == ((ChildTableMeta<?>) this.domainTable).parentMeta();
                this.nonChildPairMap = ((_Insert._ChildAssignmentInsert) domainStmt).parentStmt().assignmentMap();
                this.childPairMap = domainStmt.assignmentMap();
            } else {
                assert context.insertTable == this.domainTable;
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

                final Map<FieldMeta<?>, Object> map = _Collections.hashMap((int) (maxSize / 0.75F));
                this.generatedMap = _Collections.unmodifiableMap(map);
                this.tempGeneratedMap = map;
            }

        }


        @Override
        public void set(final FieldMeta<?> field, final @Nullable Object value) {
            final Map<FieldMeta<?>, Object> map = this.tempGeneratedMap;
            assert map != null;
            if (value == null) {
                assert this.mockEnv;
                //here mock environment
                return;
            }
            map.put(field, value);
            if (field instanceof PrimaryFieldMeta) {
                final TableMeta<?> fieldTable = field.tableMeta();
                assert fieldTable instanceof SingleTableMeta;
                if (fieldTable != this.domainTable) {
                    map.put(fieldTable.nonChildId(), value);
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
        private void parentPostId(final @Nullable Object idValue, final int indexBasedZero) {
            if (idValue == null) {
                //no bug,never here
                throw new NullPointerException();
            } else if (indexBasedZero != 0) {
                //no bug,never here
                throw new IllegalArgumentException();
            }
            if (this.idValue != null) {
                throw _Exceptions.duplicateIdValue(0, this.field, idValue);
            }
            this.idValue = idValue;
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
