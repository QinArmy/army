package io.army.dialect;

import io.army.annotation.GeneratorType;
import io.army.bean.ObjectAccessException;
import io.army.bean.ObjectWrapper;
import io.army.bean.ReadWrapper;
import io.army.criteria.NullHandleMode;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.*;
import io.army.stmt.InsertStmtParams;
import io.army.stmt.SimpleStmt;
import io.army.stmt.SingleParam;
import io.army.stmt.Stmts;
import io.army.struct.CodeEnum;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

final class ValuesInsertContext extends ValuesSyntaxInsertContext implements InsertStmtParams.ValueParams {


    static ValuesInsertContext forSingle(_Insert._ValuesInsert stmt, ArmyDialect dialect, Visible visible) {
        _DialectUtils.checkDefaultValueMap(stmt);
        return new ValuesInsertContext(stmt, stmt.table(), dialect, visible);
    }

    static ValuesInsertContext forParent(_Insert._ValuesInsert parentStmt, ChildTableMeta<?> childTable
            , ArmyDialect dialect, Visible visible) {
        return new ValuesInsertContext(parentStmt, childTable, dialect, visible);
    }

    static ValuesInsertContext forChild(ValuesInsertContext parentContext, _Insert._ChildValuesInsert insert
            , ArmyDialect dialect, Visible visible) {
        return new ValuesInsertContext(parentContext, insert, dialect, visible);
    }


    private final List<Map<FieldMeta<?>, _Expression>> rowValuesList;

    private final RowObjectWrapper rowWrapper;

    private final List<Map<FieldMeta<?>, Object>> rowGeneratedValuesList;

    private List<Map<FieldMeta<?>, Object>> tempRowGeneratedValuesList;

    /**
     * <p>
     * For {@link  io.army.meta.SingleTableMeta}
     * </p>
     *
     * @see #forSingle(_Insert._ValuesInsert, ArmyDialect, Visible)
     */
    private ValuesInsertContext(_Insert._ValuesInsert stmt, TableMeta<?> domainTable
            , ArmyDialect dialect, Visible visible) {
        super(dialect, stmt, domainTable, visible);

        this.rowValuesList = stmt.rowValuesList();
        final boolean manageVisible;
        manageVisible = isManageVisible(this.insertTable, this.defaultValueMap);

        final int rowSize = this.rowValuesList.size();
        assert rowSize > 0;

        if (this.migration) {
            this.rowGeneratedValuesList = null;
            this.tempRowGeneratedValuesList = null;
        } else {
            final List<Map<FieldMeta<?>, Object>> list = new ArrayList<>(rowSize);
            this.rowGeneratedValuesList = Collections.unmodifiableList(list);
            this.tempRowGeneratedValuesList = list;
        }

        //must be domainTable , not this.insertTable
        this.rowWrapper = new RowObjectWrapper(domainTable, manageVisible, this.returnId == null ? 0 : rowSize);


    }

    /**
     * <p>
     * For {@link  io.army.meta.ChildTableMeta}
     * </p>
     *
     * @see #forChild(ValuesInsertContext, _Insert._ChildValuesInsert, ArmyDialect, Visible)
     */
    private ValuesInsertContext(ValuesInsertContext parentContext, _Insert._ChildValuesInsert stmt
            , ArmyDialect dialect, Visible visible) {
        super(stmt, dialect, visible);
        assert ((ChildTableMeta<?>) this.insertTable).parentMeta() == parentContext.insertTable;
        this.rowValuesList = stmt.rowValuesList();
        assert this.rowValuesList.size() == parentContext.rowValuesList.size();

        assert this.rowValuesList != parentContext.rowValuesList;
        this.rowGeneratedValuesList = parentContext.rowGeneratedValuesList;
        this.rowWrapper = parentContext.rowWrapper;
        this.tempRowGeneratedValuesList = null;

    }


    @Override
    void doAppendValuesList(final List<FieldMeta<?>> fieldList) {

        final List<Map<FieldMeta<?>, _Expression>> rowValuesList = this.rowValuesList;
        final int rowSize = rowValuesList.size();
        final int fieldSize = fieldList.size();

        final ArmyDialect dialect = this.dialect;
        final Map<FieldMeta<?>, _Expression> defaultValueMap = this.defaultValueMap;

        final boolean migration = this.migration;
        final NullHandleMode nullHandleMode = this.nullHandleMode;
        final boolean preferLiteral = this.preferLiteral;
        final boolean mockEnv = dialect.isMockEnv();

        final _FieldValueGenerator generator;
        final RowObjectWrapper rowWrapper = this.rowWrapper;
        final TableMeta<?> insertTable = this.insertTable, domainTable = this.domainTable;
        final List<Map<FieldMeta<?>, Object>> generatedValuesList;

        final boolean manageVisible = rowWrapper.manageVisible;


        final FieldMeta<?> discriminator = domainTable.discriminator();
        final int discriminatorValue = domainTable.discriminatorValue();
        if (insertTable instanceof ChildTableMeta) {
            generator = null;
            generatedValuesList = this.rowGeneratedValuesList;
        } else {
            generator = dialect.getFieldValueGenerator();
            generatedValuesList = this.tempRowGeneratedValuesList;
            if (generatedValuesList == null) {
                assert migration;
            } else {
                assert generatedValuesList instanceof ArrayList;
                this.tempRowGeneratedValuesList = null;
            }
        }

        final Map<Integer, Object> rowIdMap = rowWrapper.rowIdMap;

        final Map<FieldMeta<?>, Object> emptyMap = Collections.emptyMap();
        Map<FieldMeta<?>, _Expression> rowValuesMap;
        Map<FieldMeta<?>, Object> generatedMap;
        DelayIdParamValue delayIdParam;
        FieldMeta<?> field;
        _Expression expression;
        Object value;
        MappingType mappingType;

        final StringBuilder sqlBuilder = this.sqlBuilder
                .append(_Constant.SPACE_VALUES);
        for (int rowIndex = 0; rowIndex < rowSize; rowIndex++) {
            rowValuesMap = rowValuesList.get(rowIndex);
            rowWrapper.rowValuesMap = rowValuesMap;
            if (generator == null) {//here insertTable is ChildTable
                generatedMap = generatedValuesList == null ? emptyMap : generatedValuesList.get(rowIndex);
                rowWrapper.generatedMap = generatedMap;
            } else if (migration) {
                rowWrapper.generatedMap = generatedMap = emptyMap;
                //use ths.domainTable,not this.insertTable
                generator.validate(domainTable, manageVisible, rowWrapper);// validate the values that is managed by army
            } else {
                generatedMap = new HashMap<>();
                rowWrapper.generatedMap = generatedMap; // update domain value
                //use ths.domainTable,not this.insertTable
                generator.generate(domainTable, manageVisible, rowWrapper); // create the values that is managed by army

                generatedMap = Collections.unmodifiableMap(generatedMap);
                generatedValuesList.add(generatedMap);
            }

            if (rowIndex > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);

            delayIdParam = null; //clear last param
            for (int fieldIndex = 0, actualFieldIndex = 0; fieldIndex < fieldSize; fieldIndex++) {
                field = fieldList.get(fieldIndex);
                if (!field.insertable()) {
                    // fieldList have be checked,fieldList possibly is io.army.meta.TableMeta.fieldList()
                    continue;
                }
                if (actualFieldIndex > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                actualFieldIndex++;

                if (field == discriminator) {
                    assert insertTable instanceof SingleTableMeta;
                    sqlBuilder.append(_Constant.SPACE)
                            .append(discriminatorValue);
                } else if (!migration
                        && field instanceof PrimaryFieldMeta
                        && insertTable instanceof ChildTableMeta
                        && ((ChildTableMeta<?>) insertTable).parentMeta().id().generatorType() == GeneratorType.POST) {
                    assert delayIdParam == null && rowIdMap != null;
                    delayIdParam = new DelayIdParamValue((PrimaryFieldMeta<?>) field, rowIndex, rowIdMap::get);
                    this.appendParam(delayIdParam);
                } else if ((value = generatedMap.get(field)) != null) {
                    mappingType = field.mappingType();
                    if (preferLiteral && mappingType instanceof _ArmyNoInjectionMapping) {//TODO field codec
                        sqlBuilder.append(_Constant.SPACE);
                        dialect.literal(mappingType, value, sqlBuilder);
                    } else {
                        this.appendParam(SingleParam.build(field, value));
                    }
                } else if ((expression = rowValuesMap.get(field)) != null) {
                    expression.appendSql(this);
                } else if ((expression = defaultValueMap.get(field)) != null) {
                    expression.appendSql(this);
                } else if (field.generatorType() == GeneratorType.PRECEDE) {
                    if ((migration && !field.nullable()) || (!migration && !mockEnv)) {
                        throw _Exceptions.generatorFieldIsNull(field);
                    }
                    this.appendParam(SingleParam.build(field, null));
                } else if (nullHandleMode == NullHandleMode.INSERT_DEFAULT) {
                    sqlBuilder.append(_Constant.SPACE_DEFAULT);
                } else if (!field.nullable()) {
                    throw _Exceptions.nonNullField(field);
                } else if (preferLiteral) {
                    sqlBuilder.append(_Constant.SPACE_NULL);
                } else {
                    this.appendParam(SingleParam.build(field, null));
                }

            }//inner for

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

        }//outer for

        rowWrapper.rowValuesMap = null; //finally must clear
        rowWrapper.generatedMap = null;//finally must clear

    }


    @Override
    public int rowSize() {
        return this.rowValuesList.size();
    }

    @Override
    public BiFunction<Integer, Object, Object> function() {
        final Map<Integer, Object> rowIdMap = this.rowWrapper.rowIdMap;
        assert this.isValuesClauseEnd() && rowIdMap != null && this.insertTable instanceof SingleTableMeta;
        return rowIdMap::putIfAbsent;
    }


    @Override
    public SimpleStmt build() {
        final SimpleStmt stmt;
        if (this.returnId == null) {
            stmt = Stmts.minSimple(this);
        } else {
            stmt = Stmts.valuePost(this);
        }
        return stmt;
    }


    @Override
    Object currentRowNamedValue(final String name) {
        final RowObjectWrapper wrapper = this.rowWrapper;
        final TableMeta<?> domainTable = wrapper.domainTable;
        final FieldMeta<?> field;
        field = domainTable.tryGetComplexFiled(name);
        if (field == null) {
            throw _Exceptions.invalidNamedParam(name);
        }
        final Map<FieldMeta<?>, Object> fieldValueMap = wrapper.generatedMap;
        assert fieldValueMap != null;
        return fieldValueMap.get(field);

    }


    private static final class RowReadWrapper implements ReadWrapper {

        private final RowObjectWrapper actualWrapper;

        private RowReadWrapper(RowObjectWrapper actualWrapper) {
            this.actualWrapper = actualWrapper;
        }

        @Override
        public boolean isReadable(String propertyName) {
            return this.actualWrapper.isReadable(propertyName);
        }

        @Override
        public Object get(String propertyName) throws ObjectAccessException {
            return this.actualWrapper.get(propertyName);
        }


    }//RowReadWrapper


    private static final class RowObjectWrapper implements ObjectWrapper {

        private final TableMeta<?> domainTable;

        private final FieldMeta<?> discriminator;

        private final ReadWrapper readWrapper;

        private final boolean manageVisible;

        private final Map<Integer, Object> rowIdMap;

        private Map<FieldMeta<?>, Object> generatedMap;

        private Map<FieldMeta<?>, _Expression> rowValuesMap;

        private RowObjectWrapper(TableMeta<?> domainTable, boolean manageVisible, int rowIdMapSize) {
            this.domainTable = domainTable;
            this.discriminator = domainTable.discriminator();
            this.readWrapper = new RowReadWrapper(this);
            this.manageVisible = manageVisible;

            if (rowIdMapSize > 0) {
                this.rowIdMap = new HashMap<>((int) (rowIdMapSize / 0.75F));
            } else {
                this.rowIdMap = null;
            }
        }

        @Override
        public boolean isWritable(final String propertyName) {
            final TableMeta<?> domainTable = this.domainTable;
            final FieldMeta<?> field;
            field = domainTable.tryGetComplexFiled(propertyName);

            final Map<FieldMeta<?>, _Expression> rowValuesMap = this.rowValuesMap;
            assert rowValuesMap != null;

            final boolean writable;
            if (field == null) {
                writable = false;
            } else if (field == this.discriminator) {
                writable = true;
            } else if (!(this.generatedMap instanceof HashMap)) {
                writable = false;
            } else if (domainTable instanceof SingleTableMeta || !(field instanceof PrimaryFieldMeta)) {
                writable = !rowValuesMap.containsKey(field);
            } else if (rowValuesMap.containsKey(field)) {
                writable = false;
            } else {
                writable = !rowValuesMap.containsKey(domainTable.nonChildId());
            }
            return writable;
        }

        @Override
        public void set(final String propertyName, final @Nullable Object value) throws ObjectAccessException {
            final TableMeta<?> domainTable = this.domainTable;
            final FieldMeta<?> field;
            field = domainTable.tryGetComplexFiled(propertyName);

            final Map<FieldMeta<?>, _Expression> rowValuesMap = this.rowValuesMap;
            assert rowValuesMap != null;
            if (field == null
                    || rowValuesMap.containsKey(field)
                    || (domainTable instanceof ChildTableMeta
                    && field instanceof PrimaryFieldMeta
                    && rowValuesMap.containsKey(domainTable.nonChildId()))) {
                throw _Exceptions.nonWritableProperty(this.domainTable, propertyName);
            }

            final Map<FieldMeta<?>, Object> generatedMap = this.generatedMap;
            if (!(generatedMap instanceof HashMap)) {
                if (field == this.discriminator
                        && _DialectUtils.isDiscriminatorValue(this.domainTable, value)) {
                    //ignore,here io.army.dialect._FieldValueGenerator.validate
                    return;
                }
                //no bug,never here
                throw _Exceptions.nonWritableProperty(this.domainTable, propertyName);
            }
            if (value == null) {
                generatedMap.remove(field);
                if (domainTable instanceof ChildTableMeta && field instanceof PrimaryFieldMeta) {
                    generatedMap.remove(domainTable.nonChildId());
                }
            } else if (field.javaType().isInstance(value)) {
                generatedMap.put(field, value);
                if (domainTable instanceof ChildTableMeta && field instanceof PrimaryFieldMeta) {
                    generatedMap.put(domainTable.nonChildId(), value);
                }
            } else {
                throw _Exceptions.propertyTypeNotMatch(field, value);
            }

        }

        @Override
        public ReadWrapper readonlyWrapper() {
            return this.readWrapper;
        }

        @Override
        public boolean isReadable(final String propertyName) {
            return this.domainTable.containComplexField(propertyName);
        }

        @Override
        public Object get(final String propertyName) throws ObjectAccessException {
            final TableMeta<?> domainTable = this.domainTable;
            final FieldMeta<?> field;
            field = domainTable.tryGetComplexFiled(propertyName);
            if (field == null) {
                throw _Exceptions.nonReadableProperty(this.domainTable, propertyName);
            }
            final Map<FieldMeta<?>, Object> fieldValueMap = this.generatedMap;
            assert fieldValueMap != null;
            Object value;
            value = fieldValueMap.get(field);
            if (value == null && field == this.discriminator) {
                value = CodeEnum.resolve(field.javaType(), this.domainTable.discriminatorValue());
            }
            return value;
        }


    }//RowObjectWrapper


    private static final class DelayIdParamValue implements SingleParam {

        private final PrimaryFieldMeta<?> field;

        private final int rowIndex;

        private final Function<Integer, Object> function;

        private DelayIdParamValue(PrimaryFieldMeta<?> field, int rowIndex, Function<Integer, Object> function) {
            this.field = field;
            this.rowIndex = rowIndex;
            this.function = function;
        }

        @Override
        public ParamMeta paramMeta() {
            return this.field;
        }

        @Override
        public Object value() {
            final Object value;
            value = this.function.apply(this.rowIndex);
            if (value == null) {
                //no bug,never here
                throw parentStmtDontExecute(this.field);
            }
            return value;
        }


    }//DelayIdParamValue


}
