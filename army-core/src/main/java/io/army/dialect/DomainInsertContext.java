package io.army.dialect;

import io.army.annotation.GeneratorType;
import io.army.bean.ObjectAccessException;
import io.army.bean.ObjectAccessor;
import io.army.bean.ObjectAccessorFactory;
import io.army.bean.ReadWrapper;
import io.army.criteria.NullHandleMode;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.*;
import io.army.stmt.SimpleStmt;
import io.army.stmt.SingleParam;
import io.army.stmt.Stmts;
import io.army.stmt._InsertStmtParams;
import io.army.util._Exceptions;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * This class representing standard value insert context.
 * </p>
 */
final class DomainInsertContext extends ValuesSyntaxInsertContext implements _InsertStmtParams._DomainParams {

    static DomainInsertContext forSingle(_Insert._DomainInsert insert, ArmyDialect dialect, Visible visible) {
        _DialectUtils.checkDefaultValueMap(insert);
        return new DomainInsertContext(dialect, insert, insert.table(), visible);
    }

    static DomainInsertContext forParent(_Insert._DomainInsert parentStmt, ChildTableMeta<?> childTable
            , ArmyDialect dialect, Visible visible) {
        _DialectUtils.checkDefaultValueMap(parentStmt);
        return new DomainInsertContext(dialect, parentStmt, childTable, visible);
    }


    static DomainInsertContext forChild(DomainInsertContext parentContext, _Insert._ChildDomainInsert insert
            , ArmyDialect dialect, Visible visible) {
        return new DomainInsertContext(parentContext, insert, dialect, visible);
    }

    private final DomainWrapper wrapper;

    private final List<?> domainList;


    /**
     * create for {@link  SingleTableMeta}
     */
    private DomainInsertContext(ArmyDialect dialect, _Insert._DomainInsert stmt
            , TableMeta<?> domainTable, Visible visible) {
        super(dialect, stmt, domainTable, visible);
        this.domainList = stmt.domainList();
        //must be domainTable ,not this.insertTable
        final boolean manageVisible;
        manageVisible = isManageVisible(this.insertTable, this.defaultValueMap);

        this.wrapper = new DomainWrapper(ObjectAccessorFactory.forBean(domainTable.javaType()), manageVisible);

    }

    /**
     * create for {@link  ChildTableMeta}
     */
    private DomainInsertContext(DomainInsertContext parentContext, _Insert._DomainInsert stmt
            , ArmyDialect dialect, Visible visible) {
        super(parentContext, stmt, dialect, visible);

        this.domainList = stmt.domainList();//must check for criteria api implementation
        assert this.domainList == parentContext.domainList;
        this.wrapper = parentContext.wrapper;

    }


    @Override
    void doAppendValuesList(final List<FieldMeta<?>> fieldList) {

        final List<?> domainList = this.domainList;
        final int domainSize = domainList.size();
        assert domainSize > 0; //must check for criteria api implementation
        final int fieldSize = fieldList.size();

        final ArmyDialect dialect = this.dialect;
        final Map<FieldMeta<?>, _Expression> defaultValueMap = this.defaultValueMap;

        final boolean preferLiteral = this.preferLiteral;
        final boolean migration = this.migration;
        final boolean mockEnv = dialect.isMockEnv();
        final NullHandleMode nullHandleMode = this.nullHandleMode;

        final FieldValueGenerator generator;
        final DomainWrapper domainWrapper = this.wrapper;
        final ObjectAccessor accessor = domainWrapper.accessor;
        final TableMeta<?> insertTable = this.insertTable, domainTable = this.domainTable;

        final boolean manageVisible = domainWrapper.manageVisible;
        final FieldMeta<?> discriminator = domainTable.discriminator();
        final int discriminatorValue = domainTable.discriminatorValue();
        if (insertTable instanceof ChildTableMeta) {
            generator = null;
        } else {
            generator = dialect.getGenerator();
        }
        FieldMeta<?> field;
        _Expression expression;
        Object value, domain;
        MappingType mappingType;
        DelayIdParamValue delayIdParam;

        final StringBuilder sqlBuilder = this.sqlBuilder
                .append(_Constant.SPACE_VALUES);
        for (int rowIndex = 0; rowIndex < domainSize; rowIndex++) {
            domain = domainList.get(rowIndex);
            domainWrapper.domain = domain; //update current domain
            if (generator != null) {
                if (migration) {
                    //use ths.domainTable,not this.insertTable
                    generator.validate(domainTable, manageVisible, domainWrapper);
                } else {
                    //use ths.domainTable,not this.insertTable
                    generator.generate(domainTable, manageVisible, domainWrapper);
                }
            }

            if (rowIndex > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }

            sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            delayIdParam = null;//clear
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
                    if (delayIdParam != null) {
                        //no bug,never here
                        throw new IllegalStateException();
                    }
                    delayIdParam = new DelayIdParamValue((PrimaryFieldMeta<?>) field, domain, accessor);
                    this.appendParam(delayIdParam);
                } else if ((value = accessor.get(domain, field.fieldName())) != null) {
                    mappingType = field.mappingType();
                    if (preferLiteral && mappingType instanceof _ArmyNoInjectionMapping) {//TODO field codec
                        sqlBuilder.append(_Constant.SPACE);
                        dialect.literal(mappingType, value, sqlBuilder);
                    } else {
                        this.appendParam(SingleParam.build(field, value));
                    }
                } else if ((expression = defaultValueMap.get(field)) != null) {
                    expression.appendSql(this);
                } else if (field.generatorType() == GeneratorType.PRECEDE) {
                    if ((migration && !field.nullable()) || (!migration && !mockEnv)) {
                        throw _Exceptions.generatorFieldIsNull(field);
                    }
                    this.appendParam(SingleParam.build(field, null));
                } else if (nullHandleMode == NullHandleMode.INSERT_DEFAULT) {
                    sqlBuilder.append(_Constant.SPACE_DEFAULT);
                } else if (nullHandleMode != NullHandleMode.INSERT_NULL) {
                    //no bug,never here
                    throw new IllegalStateException();
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

        domainWrapper.domain = null; //finally must clear
    }


    @Override
    public SimpleStmt build() {
        final SimpleStmt stmt;
        if (this.returnId == null) {
            stmt = Stmts.minSimple(this);
        } else {
            stmt = Stmts.domainPost(this);
        }
        return stmt;
    }


    @Override
    public List<?> domainList() {
        return this.domainList;
    }

    @Override
    public ObjectAccessor domainAccessor() {
        return this.wrapper.accessor;
    }


    @Nullable
    @Override
    Object currentRowNamedValue(final String name) {
        return this.wrapper.get(name);
    }


    private static final class BeanReadWrapper implements ReadWrapper {

        private final DomainWrapper actual;

        BeanReadWrapper(DomainWrapper actual) {
            this.actual = actual;
        }

        @Override
        public boolean isReadable(String propertyName) {
            return this.actual.isReadable(propertyName);
        }

        @Override
        public Object get(String propertyName) throws ObjectAccessException {
            return this.actual.get(propertyName);
        }


    }//BeanReadWrapper


    private static final class DomainWrapper implements RowWrapper {

        private final ObjectAccessor accessor;

        private final BeanReadWrapper readWrapper;

        private final boolean manageVisible;

        private Object domain;

        private DomainWrapper(ObjectAccessor accessor, boolean manageVisible) {
            this.accessor = accessor;
            this.readWrapper = new BeanReadWrapper(this);
            this.manageVisible = manageVisible;
        }

        @Override
        public boolean isWritable(String propertyName) {
            return this.accessor.isWritable(propertyName);
        }

        @Override
        public void set(String propertyName, @Nullable Object value) throws ObjectAccessException {
            final Object domain;
            domain = this.domain;
            assert domain != null;
            this.accessor.set(domain, propertyName, value);
        }

        @Override
        public ReadWrapper readonlyWrapper() {
            return this.readWrapper;
        }

        @Override
        public boolean isReadable(String propertyName) {
            return this.accessor.isReadable(propertyName);
        }

        @Override
        public Object get(final String propertyName) throws ObjectAccessException {
            final Object domain;
            domain = this.domain;
            if (domain == null) {
                //eg: MySQL onDuplicateKeyUpdate clause
                throw _Exceptions.namedParamErrorPosition(propertyName);
            }
            return this.accessor.get(domain, propertyName);
        }


    }//DomainWrapper


    private static final class DelayIdParamValue implements SingleParam {

        private final PrimaryFieldMeta<?> field;

        private final Object domain;

        private final ObjectAccessor accessor;

        private DelayIdParamValue(PrimaryFieldMeta<?> field, Object domain, ObjectAccessor accessor) {
            this.field = field;
            this.domain = domain;
            this.accessor = accessor;
        }

        @Override
        public ParamMeta paramMeta() {
            return this.field;
        }

        @Override
        public Object value() {
            final Object value;
            value = this.accessor.get(this.domain, this.field.fieldName());
            if (value == null) {
                throw parentStmtDontExecute(this.field);
            }
            return value;
        }

    }//DelayIdParamValue


}
