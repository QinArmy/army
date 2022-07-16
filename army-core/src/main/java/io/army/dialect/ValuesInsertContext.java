package io.army.dialect;

import io.army.annotation.GeneratorType;
import io.army.bean.ObjectAccessException;
import io.army.bean.ObjectWrapper;
import io.army.bean.ReadWrapper;
import io.army.criteria.NullHandleMode;
import io.army.criteria.ValueExpression;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.*;
import io.army.stmt.InsertStmtParams;
import io.army.stmt.ParamValue;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmts;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.BiConsumer;

final class ValuesInsertContext extends ValuesSyntaxInsertContext implements InsertStmtParams.ValueParams {


    static ValuesInsertContext forSingle(_Insert._ValueInsert stmt, ArmyDialect dialect, Visible visible) {
        final TableMeta<?> table = stmt.table();
        if (table instanceof ChildTableMeta
                && stmt instanceof _Insert._DuplicateKeyClause
                && ((ChildTableMeta<?>) table).parentMeta().id().generatorType() == GeneratorType.POST) {
            throw _Exceptions.duplicateKeyAndPostIdInsert((ChildTableMeta<?>) table);
        }
        _DialectUtils.checkCommonExpMap(stmt);
        return new ValuesInsertContext(dialect, stmt, visible);
    }

    static ValuesInsertContext forChild(ValuesInsertContext parentContext, _Insert._ValueInsert insert
            , ArmyDialect dialect, Visible visible) {
        return new ValuesInsertContext(parentContext, insert, dialect, visible);
    }

    private final List<Map<FieldMeta<?>, _Expression>> rowValuesList;

    private final RowObjectWrapper rowWrapper;

    private final List<Map<FieldMeta<?>, Object>> rowGeneratedValuesList;

    private final Map<Integer, Object> rowIdMap;

    private List<Map<FieldMeta<?>, Object>> tempRowGeneratedValuesList;

    private List<BiConsumer<Integer, Object>> consumerList;

    private Map<Integer, Object> tempRowIdMap;

    /**
     * <p>
     * For {@link  io.army.meta.SingleTableMeta}
     * </p>
     *
     * @see #forSingle(_Insert._ValueInsert, ArmyDialect, Visible)
     */
    private ValuesInsertContext(ArmyDialect dialect, _Insert._ValueInsert stmt, Visible visible) {
        super(dialect, stmt, visible);

        this.rowValuesList = stmt.rowValuesList();
        //must be stmt.table() , not this.table
        this.rowWrapper = new RowObjectWrapper(stmt.table());

        final int rowSize = this.rowValuesList.size();
        if (this.migration) {
            this.rowGeneratedValuesList = null;
        } else {
            final List<Map<FieldMeta<?>, Object>> list = new ArrayList<>(rowSize);
            this.rowGeneratedValuesList = Collections.unmodifiableList(list);
            this.tempRowGeneratedValuesList = list;
        }

        if (!this.duplicateKeyClause && this.table.id().generatorType() == GeneratorType.POST) {
            final Map<Integer, Object> rowIdMap = new HashMap<>((int) (rowSize / 0.75F));
            this.rowIdMap = Collections.unmodifiableMap(rowIdMap);
            this.tempRowIdMap = rowIdMap;
        } else {
            this.rowIdMap = null;
        }
    }

    /**
     * <p>
     * For {@link  io.army.meta.ChildTableMeta}
     * </p>
     *
     * @see #forChild(ValuesInsertContext, _Insert._ValueInsert, ArmyDialect, Visible)
     */
    private ValuesInsertContext(ValuesInsertContext parentContext, _Insert._ValueInsert stmt
            , ArmyDialect dialect, Visible visible) {
        super(stmt, dialect, visible);
        assert ((ChildTableMeta<?>) this.table).parentMeta() == parentContext.table;
        this.rowValuesList = stmt.rowValuesList();
        assert this.rowValuesList == parentContext.rowValuesList;
        this.rowGeneratedValuesList = parentContext.rowGeneratedValuesList;
        this.rowWrapper = parentContext.rowWrapper;
        this.rowIdMap = null;

    }


    @Override
    public void appendField(final String tableAlias, final FieldMeta<?> field) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void appendField(FieldMeta<?> field) {
        throw new UnsupportedOperationException();
    }


    @Override
    public void appendValueList() {
        final List<Map<FieldMeta<?>, _Expression>> rowValuesList = this.rowValuesList;
        final List<FieldMeta<?>> fieldList = this.fieldList;
        final int rowSize = rowValuesList.size();
        final int fieldSize = fieldList.size();

        final ArmyDialect dialect = this.dialect;
        final FieldMeta<?> discriminator = this.discriminator;
        final Map<FieldMeta<?>, _Expression> commonExpMap = this.commonExpMap;

        final boolean migration = this.migration;
        final NullHandleMode nullHandleMode = this.nullHandleMode;
        final boolean preferLiteral = this.preferLiteral;
        final boolean mockEnv = dialect.isMockEnv();

        final StringBuilder sqlBuilder = this.sqlBuilder;

        sqlBuilder.append(_Constant.SPACE_VALUES);

        final _FieldValueGenerator generator;
        final RowObjectWrapper rowWrapper = this.rowWrapper;
        final TableMeta<?> table = this.table;
        final List<Map<FieldMeta<?>, Object>> generatedValuesList;
        final Map<Integer, Object> rowIdMap;
        final List<BiConsumer<Integer, Object>> consumerList;
        if (table instanceof ChildTableMeta) {
            generator = null;
            generatedValuesList = this.rowGeneratedValuesList;
            consumerList = null;
            rowIdMap = this.rowIdMap;
        } else {
            generator = dialect.getFieldValueGenerator();
            generatedValuesList = this.tempRowGeneratedValuesList;
            assert generatedValuesList instanceof ArrayList;
            this.tempRowGeneratedValuesList = null;

            consumerList = new ArrayList<>(rowSize);
            rowIdMap = this.tempRowIdMap;
            assert rowIdMap instanceof HashMap;
            this.tempRowIdMap = null;
        }
        Map<FieldMeta<?>, _Expression> fieldExpMap;
        Map<FieldMeta<?>, Object> fieldValueMap;
        DelayIdParamValue delayIdParam;
        FieldMeta<?> field;
        _Expression expression;
        Object value;
        MappingType mappingType;
        for (int rowIndex = 0; rowIndex < rowSize; rowIndex++) {
            fieldExpMap = rowValuesList.get(rowIndex);
            rowWrapper.fieldExpMap = fieldExpMap;
            if (generator == null) {
                fieldValueMap = generatedValuesList.get(rowIndex);
                rowWrapper.fieldValueMap = fieldValueMap;
            } else if (migration) {
                fieldValueMap = Collections.emptyMap();
                rowWrapper.fieldValueMap = fieldValueMap;

                generator.validate(table, rowWrapper);// validate the values that is managed by army
                generatedValuesList.add(fieldValueMap);
            } else {
                fieldValueMap = new HashMap<>();
                rowWrapper.fieldValueMap = fieldValueMap; // update domain value

                generator.generate(table, rowWrapper); // create the values that is managed by army

                fieldValueMap = Collections.unmodifiableMap(fieldValueMap);
                generatedValuesList.add(fieldValueMap);
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
                    assert table instanceof SingleTableMeta;
                    sqlBuilder.append(_Constant.SPACE)
                            .append(this.discriminatorValue);
                } else if (field instanceof PrimaryFieldMeta
                        && table instanceof ChildTableMeta
                        && ((ChildTableMeta<?>) table).parentMeta().id().generatorType() == GeneratorType.POST) {
                    if (delayIdParam != null || consumerList != null || rowIdMap == null) {
                        //no bug,never here
                        throw new IllegalStateException();
                    }
                    delayIdParam = new DelayIdParamValue((PrimaryFieldMeta<?>) field, rowIndex, rowIdMap);
                    this.appendParam(delayIdParam);
                } else if ((value = fieldValueMap.get(field)) != null) {
                    mappingType = field.mappingType();
                    if (preferLiteral && mappingType instanceof _ArmyNoInjectionMapping) {//TODO field codec
                        sqlBuilder.append(_Constant.SPACE);
                        dialect.literal(mappingType, value, sqlBuilder);
                    } else {
                        this.appendParam(ParamValue.build(field, value));
                    }
                } else if ((expression = fieldExpMap.get(field)) != null) {
                    expression.appendSql(this);
                } else if ((expression = commonExpMap.get(field)) != null) {
                    expression.appendSql(this);
                } else if (field.generatorType() == GeneratorType.PRECEDE) {
                    if ((migration && !field.nullable()) || (!migration && !mockEnv)) {
                        throw _Exceptions.generatorFieldIsNull(field);
                    }
                    this.appendParam(ParamValue.build(field, null));
                } else if (nullHandleMode == NullHandleMode.INSERT_DEFAULT) {
                    sqlBuilder.append(_Constant.SPACE_DEFAULT);
                } else if (!field.nullable()) {
                    throw _Exceptions.nonNullField(field);
                } else if (preferLiteral) {
                    sqlBuilder.append(_Constant.SPACE_NULL);
                } else {
                    this.appendParam(ParamValue.build(field, null));
                }

            }//inner for

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            if (consumerList != null) {
                consumerList.add(rowIdMap::putIfAbsent);
            }

        }//outer for

        if (consumerList != null) {
            this.consumerList = _CollectionUtils.unmodifiableList(consumerList);
        }

    }


    @Override
    public List<BiConsumer<Integer, Object>> consumerList() {
        final List<BiConsumer<Integer, Object>> consumerList = this.consumerList;
        if (consumerList == null || consumerList.size() != this.rowValuesList.size()) {
            //no bug,never here
            throw new IllegalStateException();
        }
        return consumerList;
    }

    @Override
    public SimpleStmt build() {
        final SimpleStmt stmt;
        final TableMeta<?> table = this.table;
        if (this.returnId != null) {
            //dialect support returning clause
            stmt = Stmts.valuePost(this);
        } else if (this.duplicateKeyClause
                || table instanceof ChildTableMeta
                || table.id().generatorType() != GeneratorType.POST) {
            stmt = Stmts.minSimple(this);
        } else {
            stmt = Stmts.valuePost(this);
        }
        return stmt;
    }


    @Override
    Object readNamedParam(final String name) {
        final RowObjectWrapper wrapper = this.rowWrapper;
        final TableMeta<?> domainTable = wrapper.domainTable;
        final FieldMeta<?> field;
        field = domainTable.tryGetComplexFiled(name);
        if (field == null) {
            return null;
        }

        final Map<FieldMeta<?>, Object> fieldValueMap = wrapper.fieldValueMap;
        assert fieldValueMap != null;

        Object value;
        value = fieldValueMap.get(field);
        if (value != null) {
            return value;
        }

        final Map<FieldMeta<?>, _Expression> fieldExpMap = wrapper.fieldExpMap;
        assert fieldExpMap != null;
        _Expression expression;
        expression = fieldExpMap.get(field);
        if (expression == null && domainTable instanceof ChildTableMeta && field instanceof PrimaryFieldMeta) {
            expression = fieldExpMap.get(domainTable.getNonChildId());
        }
        if (expression instanceof ValueExpression) {
            value = ((ValueExpression) expression).value();
        }
        if (value != null) {
            return value;
        }
        if ((expression = this.commonExpMap.get(field)) == null
                && domainTable instanceof ChildTableMeta
                && field instanceof ParentTableMeta) {
            expression = this.commonExpMap.get(domainTable.getNonChildId());
        }
        if (expression instanceof ValueExpression) {
            value = ((ValueExpression) expression).value();
        }
        return value;
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

        private final ReadWrapper readWrapper;

        private Map<FieldMeta<?>, Object> fieldValueMap;

        private Map<FieldMeta<?>, _Expression> fieldExpMap;

        private RowObjectWrapper(TableMeta<?> domainTable) {
            this.domainTable = domainTable;
            this.readWrapper = new RowReadWrapper(this);
        }

        @Override
        public boolean isWritable(final String propertyName) {
            final TableMeta<?> domainTable = this.domainTable;
            final FieldMeta<?> field;
            field = domainTable.tryGetComplexFiled(propertyName);

            final Map<FieldMeta<?>, _Expression> filedExpMap = this.fieldExpMap;
            assert filedExpMap != null;

            final boolean writable;
            if (field == null) {
                writable = false;
            } else if (field == domainTable.discriminator()) {
                writable = true;
            } else if (!(this.fieldValueMap instanceof HashMap)) {
                writable = false;
            } else if (domainTable instanceof ChildTableMeta || !(field instanceof PrimaryFieldMeta)) {
                writable = !filedExpMap.containsKey(field);
            } else if (filedExpMap.containsKey(field)) {
                writable = false;
            } else {
                writable = !filedExpMap.containsKey(parent.id());
            }
            return writable;
        }

        @Override
        public void set(final String propertyName, final @Nullable Object value) throws ObjectAccessException {
            final TableMeta<?> domainTable = this.domainTable;
            final ParentTableMeta<?> parent;
            final FieldMeta<?> field;
            if (domainTable instanceof ChildTableMeta) {
                parent = ((ChildTableMeta<?>) domainTable).parentMeta();
            } else {
                parent = null;
            }

            if (domainTable.containField(propertyName)) {
                field = domainTable.getField(propertyName);
            } else if (parent != null && parent.containField(propertyName)) {
                field = parent.getField(propertyName);
            } else {
                field = null;
            }

            final Map<FieldMeta<?>, _Expression> filedExpMap = this.fieldExpMap;
            assert filedExpMap != null;
            if (field == null
                    || filedExpMap.containsKey(field)
                    || (parent != null && field instanceof PrimaryFieldMeta && filedExpMap.containsKey(parent.id()))) {
                String m = String.format("property[%s] isn't writable.", propertyName);
                throw new ObjectAccessException(m);
            }

            final Map<FieldMeta<?>, Object> fieldValueMap = this.fieldValueMap;
            if (!(fieldValueMap instanceof HashMap)) {
                if (field == domainTable.discriminator()) {
                    //ignore,here io.army.dialect._FieldValueGenerator.validate
                    return;
                }
                String m = String.format("property[%s] isn't writable.", propertyName);
                throw new ObjectAccessException(m);
            }
            if (value == null) {
                fieldValueMap.remove(field);
                if (parent != null && field instanceof PrimaryFieldMeta) {
                    fieldValueMap.remove(parent.id());
                }
            } else {
                fieldValueMap.put(field, value);
                if (parent != null && field instanceof PrimaryFieldMeta) {
                    fieldValueMap.put(parent.id(), value);
                }
            }

        }

        @Override
        public ReadWrapper readonlyWrapper() {
            return this.readWrapper;
        }

        @Override
        public boolean isReadable(final String propertyName) {
            final TableMeta<?> domainTable = this.domainTable;
            final boolean readable;
            if (!(domainTable instanceof ChildTableMeta)) {
                readable = domainTable.containField(propertyName);
            } else if (domainTable.containField(propertyName)) {
                readable = true;
            } else {
                readable = ((ChildTableMeta<?>) domainTable).parentMeta().containField(propertyName);
            }
            return readable;
        }

        @Override
        public Object get(final String propertyName) throws ObjectAccessException {
            final TableMeta<?> domainTable = this.domainTable;
            final ParentTableMeta<?> parent;
            final FieldMeta<?> field;
            if (domainTable instanceof ChildTableMeta) {
                parent = ((ChildTableMeta<?>) domainTable).parentMeta();
            } else {
                parent = null;
            }

            if (domainTable.containField(propertyName)) {
                field = domainTable.getField(propertyName);
            } else if (parent != null && parent.containField(propertyName)) {
                field = parent.getField(propertyName);
            } else {
                field = null;
            }

            if (field == null) {
                String m = String.format("%s isn't readable.", propertyName);
                throw new ObjectAccessException(m);
            }

            final Map<FieldMeta<?>, _Expression> filedExpMap = this.fieldExpMap;
            assert filedExpMap != null;

            _Expression expression;
            expression = filedExpMap.get(field);
            if (expression == null && parent != null && field instanceof PrimaryFieldMeta) {
                expression = filedExpMap.get(parent.id());
            }
            final Object value;
            if (expression instanceof ValueExpression) {
                value = ((ValueExpression) expression).value();
            } else {
                final Map<FieldMeta<?>, Object> fieldValueMap = this.fieldValueMap;
                assert fieldValueMap != null;
                value = fieldValueMap.get(field);
            }
            return value;
        }


    }//RowObjectWrapper


    private static final class DelayIdParamValue implements ParamValue {

        private final PrimaryFieldMeta<?> field;

        private final int rowIndex;

        private final Map<Integer, Object> rowIdMap;

        private DelayIdParamValue(PrimaryFieldMeta<?> field, int rowIndex, Map<Integer, Object> rowIdMap) {
            this.field = field;
            this.rowIndex = rowIndex;
            this.rowIdMap = rowIdMap;
        }

        @Override
        public ParamMeta paramMeta() {
            return this.field;
        }

        @Override
        public Object value() {
            final Object value;
            value = this.rowIdMap.get(this.rowIndex);
            if (value == null) {
                //no bug,never here
                throw new IllegalStateException("value is null");
            }
            return value;
        }


    }//DelayIdParamValue


}
