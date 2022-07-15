package io.army.dialect;

import io.army.annotation.GeneratorType;
import io.army.bean.ObjectAccessException;
import io.army.bean.ObjectAccessor;
import io.army.bean.ObjectWrapper;
import io.army.bean.ReadWrapper;
import io.army.criteria.NullHandleMode;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.PrimaryFieldMeta;
import io.army.meta.TableMeta;
import io.army.stmt.InsertStmtParams;
import io.army.stmt.ParamValue;
import io.army.stmt.SimpleStmt;
import io.army.util._Exceptions;

import java.util.List;
import java.util.Map;

final class ValuesInsertContext extends ValuesSyntaxInsertContext implements InsertStmtParams {


    static ValuesInsertContext forSingle(_Insert._ValueInsert insert, ArmyDialect dialect, Visible visible) {
        _DialectUtils.checkCommonExpMap(insert);
        return new ValuesInsertContext(dialect, insert, visible);
    }

    static ValuesInsertContext forChild(_Insert._ValueInsert insert, ArmyDialect dialect, Visible visible) {
        return new ValuesInsertContext(insert, dialect, visible);
    }

    private final List<Map<FieldMeta<?>, _Expression>> rowValuesList;


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


    }

    /**
     * <p>
     * For {@link  io.army.meta.ChildTableMeta}
     * </p>
     *
     * @see #forChild(_Insert._ValueInsert, ArmyDialect, Visible)
     */
    private ValuesInsertContext(_Insert._ValueInsert stmt, ArmyDialect dialect, Visible visible) {
        super(stmt, dialect, visible);

        this.rowValuesList = stmt.rowValuesList();
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
        final boolean mockEnv = dialect.isMockEnv();
        final NullHandleMode nullHandleMode = this.nullHandleMode;
        final StringBuilder sqlBuilder = this.sqlBuilder;

        sqlBuilder.append(_Constant.SPACE_VALUES);

        final _FieldValueGenerator generator;
        final DomainInsertContext.BeanReadWrapper readWrapper;
        final TableMeta<?> table = this.table;
        if (table instanceof ChildTableMeta) {
            generator = null;
            readWrapper = null;
        } else {
            generator = dialect.getFieldValueGenerator();
            readWrapper = new DomainInsertContext.BeanReadWrapper(accessor);
        }
        IDomain domain;
        FieldMeta<?> field;
        _Expression expression;
        Object value;
        MappingType mappingType;
        for (int domainIndex = 0; domainIndex < rowSize; domainIndex++) {
            domain = domainList.get(domainIndex);
            if (generator != null) {
                //only non-child table
                readWrapper.domain = domain; // update domain value
                if (migration) {
                    generator.validate(table, domain, accessor);
                } else {
                    generator.generate(table, domain, accessor, readWrapper);
                }
            }

            if (domainIndex > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }

            sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);

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
                    sqlBuilder.append(_Constant.SPACE)
                            .append(this.discriminatorValue);
                } else if ((value = accessor.get(domain, field.fieldName())) != null) {
                    mappingType = field.mappingType();
                    if (preferLiteral && mappingType instanceof _ArmyNoInjectionMapping) {//TODO field codec
                        sqlBuilder.append(_Constant.SPACE);
                        dialect.literal(mappingType, value, sqlBuilder);
                    } else {
                        this.appendParam(ParamValue.build(field, value));
                    }
                } else if ((expression = commonExpMap.get(field)) != null) {
                    this.currentDomain = domain; //update current domain for SubQuery
                    expression.appendSql(this);
                } else if (field instanceof PrimaryFieldMeta
                        && table instanceof ChildTableMeta
                        && ((ChildTableMeta<?>) table).parentMeta().id().generatorType() == GeneratorType.POST) {
                    this.appendParam(new DomainInsertContext.DelayIdParamValue(field, domain, accessor));
                } else if (field.generatorType() == GeneratorType.PRECEDE) {
                    if ((migration && !field.nullable()) || (!migration && !mockEnv)) {
                        throw _Exceptions.generatorFieldIsNull(field);
                    }
                    this.appendParam(ParamValue.build(field, null));
                } else if (nullHandleMode == NullHandleMode.INSERT_DEFAULT) {
                    sqlBuilder.append(_Constant.SPACE_DEFAULT);
                } else if (!field.nullable()) {
                    throw _Exceptions.nonNullField(field);
                } else {
                    this.appendParam(ParamValue.build(field, null));
                }

            }//inner for

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }
    }


    @Override
    public List<IDomain> domainList() {
        return null;
    }

    @Override
    public ObjectAccessor domainAccessor() {
        return null;
    }

    @Override
    public PrimaryFieldMeta<?> returnId() {
        return null;
    }

    @Override
    public String idReturnAlias() {
        return null;
    }


    @Override
    public SimpleStmt build() {
        return null;
    }


    private static final class RowReadWrapper implements ReadWrapper {

        private final RowObjectWrapper actualWrapper;

        private RowReadWrapper(RowObjectWrapper actualWrapper) {
            this.actualWrapper = actualWrapper;
        }

        @Override
        public boolean isReadable(String propertyName) {
            return false;
        }

        @Override
        public Object get(String propertyName) throws ObjectAccessException {
            return null;
        }


    }//RowReadWrapper


    private static final class RowObjectWrapper implements ObjectWrapper {

        private final TableMeta<?> domainTable;

        private final ReadWrapper readWrapper;

        private Map<String, Object> fieldValueMap;

        private Map<FieldMeta<?>, _Expression> fieldExpMap;

        private RowObjectWrapper(TableMeta<?> domainTable) {
            this.domainTable = domainTable;
            this.readWrapper = new RowReadWrapper(this);
        }

        @Override
        public boolean isWritable(final String propertyName) {
            final TableMeta<?> domainTable = this.domainTable;
            boolean match;
            match = domainTable.containField(propertyName);
            if (!match && domainTable instanceof ChildTableMeta) {
                match = ((ChildTableMeta<?>) domainTable).parentMeta().containField(propertyName);
            }
            return match;
        }

        @Override
        public void set(final String propertyName, final @Nullable Object value) throws ObjectAccessException {
            if (!isWritable(propertyName)) {
                String m = String.format("%s don't contain filed[%s]", this.domainTable, propertyName);
                throw new ObjectAccessException(m);
            }
            if (value == null) {
                this.fieldExpMap.remove(this.domainTable.getField(propertyName));
            }
        }

        @Override
        public ReadWrapper readonlyWrapper() {
            return null;
        }

        @Override
        public boolean isReadable(String propertyName) {
            return false;
        }

        @Override
        public Object get(String propertyName) throws ObjectAccessException {
            return null;
        }


    }//RowObjectWrapper


}
