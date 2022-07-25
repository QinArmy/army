package io.army.dialect;

import io.army.annotation.GeneratorType;
import io.army.bean.ObjectAccessException;
import io.army.bean.ReadWrapper;
import io.army.criteria.NullHandleMode;
import io.army.criteria.SqlValueParam;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.lang.Nullable;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.stmt.SimpleStmt;
import io.army.stmt.SingleParam;
import io.army.stmt.Stmts;
import io.army.stmt._InsertStmtParams;
import io.army.struct.CodeEnum;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

final class ValuesInsertContext extends ValuesSyntaxInsertContext implements _InsertStmtParams._ValueParams {


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

    private final ValuesRowWrapper rowWrapper;

    private final List<Map<FieldMeta<?>, Object>> rowGeneratedValuesList;

    private List<Map<FieldMeta<?>, Object>> tempRowGeneratedValuesList;

    /**
     * <p>
     * For {@link  io.army.meta.SingleTableMeta}
     * </p>
     *
     * @see #forSingle(_Insert._ValuesInsert, ArmyDialect, Visible)
     * @see #forParent(_Insert._ValuesInsert, ChildTableMeta, ArmyDialect, Visible)
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
        this.rowWrapper = new ValuesRowWrapper(domainTable, manageVisible, this.returnId == null ? 0 : rowSize);


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
        super(parentContext, stmt, dialect, visible);

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

        final FieldValueGenerator generator;
        final ValuesRowWrapper rowWrapper = this.rowWrapper;
        final TableMeta<?> insertTable = this.insertTable, domainTable = this.domainTable;
        final List<Map<FieldMeta<?>, Object>> generatedValuesList;

        final boolean manageVisible = rowWrapper.manageVisible;


        final FieldMeta<?> discriminator = domainTable.discriminator();
        final int discriminatorValue = domainTable.discriminatorValue();
        if (insertTable instanceof ChildTableMeta) {
            generator = null;
            generatedValuesList = this.rowGeneratedValuesList;
        } else {
            generator = dialect.getGenerator();
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
        final ValuesRowWrapper wrapper = this.rowWrapper;
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

        private final ExpRowWrapper actualWrapper;

        private RowReadWrapper(ExpRowWrapper actualWrapper) {
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


    static abstract class ExpRowWrapper implements RowWrapper {

        final TableMeta<?> domainTable;

        final FieldMeta<?> discriminator;

        final boolean manageVisible;

        private final MappingEnv mappingEnv;

        private final RowReadWrapper readWrapper;


        ExpRowWrapper(TableMeta<?> domainTable, MappingEnv mappingEnv, boolean manageVisible) {
            this.domainTable = domainTable;
            this.mappingEnv = mappingEnv;
            this.manageVisible = manageVisible;
            this.discriminator = domainTable.discriminator();

            this.readWrapper = new RowReadWrapper(this);
        }

        @Override
        public final boolean isReadable(final String propertyName) {
            return this.domainTable.containComplexField(propertyName);
        }


        @Override
        public final void set(final String propertyName, final @Nullable Object value) throws ObjectAccessException {


            final Map<FieldMeta<?>, Object> generatedMap = this.getGeneratedMap();
            if (generatedMap == null
                    || !(field.generatorType() == GeneratorType.PRECEDE
                    || field == this.discriminator
                    || _MetaBridge.isReserved(propertyName))) {
                throw _Exceptions.nonWritableProperty(this.domainTable, propertyName);
            }

            if (!this.manageVisible && _MetaBridge.VISIBLE.equals(propertyName)) {
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
        public final ReadWrapper readonlyWrapper() {
            return this.readWrapper;
        }

        @Nullable
        abstract Map<FieldMeta<?>, Object> getGeneratedMap();


        @Nullable
        final Object readValueFromExpression(final FieldMeta<?> field, final _Expression expression) {
            if (!(expression instanceof SqlValueParam.SingleNonNamedValue)) {
                return null;
            }
            Object value;
            value = ((SqlValueParam.SingleNonNamedValue) expression).value();
            final Class<?> javaType = field.javaType();
            if (value == null || javaType.isInstance(value)) {
                return value;
            }
            value = field.mappingType().convert(this.mappingEnv, value);
            if (!javaType.isInstance(value)) {
                String m = String.format("%s convert method don't return instance of %s"
                        , field.mappingType().getClass().getName(), javaType.getName());
                throw new MetaException(m);
            }
            return value;
        }


    }//RowWrapper


    private static final class ValuesRowWrapper extends ExpRowWrapper {
        private final boolean manageVisible;

        private final Map<Integer, Object> rowIdMap;

        private Map<FieldMeta<?>, Object> generatedMap;

        private Map<FieldMeta<?>, _Expression> rowValuesMap;

        private ValuesRowWrapper(TableMeta<?> domainTable, boolean manageVisible, int rowIdMapSize) {
            super(domainTable);
            this.manageVisible = manageVisible;

            if (rowIdMapSize > 0) {
                this.rowIdMap = new HashMap<>((int) (rowIdMapSize / 0.75F));
            } else {
                this.rowIdMap = null;
            }
        }

        @Override
        public Object get(final String propertyName) throws ObjectAccessException {
            final TableMeta<?> domainTable = this.domainTable;
            final FieldMeta<?> field;
            field = domainTable.tryGetComplexFiled(propertyName);
            if (field == null) {
                throw _Exceptions.nonReadableProperty(this.domainTable, propertyName);
            }
            final Map<FieldMeta<?>, Object> generatedMap = this.generatedMap;
            assert generatedMap != null;
            Object value;
            value = generatedMap.get(field);
            if (value != null) {
                return value;
            }
            if (field == this.discriminator) {
                value = CodeEnum.resolve(field.javaType(), this.domainTable.discriminatorValue());
            } else {
                final Map<FieldMeta<?>, _Expression> rowValuesMap = this.rowValuesMap;
                assert rowValuesMap != null;
                final _Expression expression;
                expression = rowValuesMap.get(field);
                if (expression instanceof SqlValueParam.SingleNonNamedValue) {
                    value = ((SqlValueParam.SingleNonNamedValue) expression).value();
                }
            }
            return value;
        }

        @Override
        Map<FieldMeta<?>, Object> getGeneratedMap() {
            return this.generatedMap;
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
