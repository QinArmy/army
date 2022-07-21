package io.army.dialect;

import io.army.annotation.GeneratorType;
import io.army.bean.*;
import io.army.criteria.NullHandleMode;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.*;
import io.army.stmt.InsertStmtParams;
import io.army.stmt.ParamValue;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * This class representing standard value insert context.
 * </p>
 */
final class DomainInsertContext extends ValuesSyntaxInsertContext implements InsertStmtParams.DomainParams {

    static DomainInsertContext forSingle(_Insert._DomainInsert insert, ArmyDialect dialect, Visible visible) {
        _DialectUtils.checkDefaultValueMap(insert);
        return new DomainInsertContext(dialect, insert, visible);
    }

    static DomainInsertContext forChild(DomainInsertContext parentContext, _Insert._DomainInsert insert
            , ArmyDialect dialect, Visible visible) {
        return new DomainInsertContext(parentContext, insert, dialect, visible);
    }


    private final DomainWrapper wrapper;

    private final List<IDomain> domainList;

    private boolean valuesClauseEnd;


    /**
     * create for {@link  SingleTableMeta}
     */
    private DomainInsertContext(ArmyDialect dialect, _Insert._DomainInsert stmt, Visible visible) {
        super(dialect, stmt, visible);
        this.domainList = stmt.domainList();
        //must be stmt.table().javaType()) ,not this.table.javaType()
        this.wrapper = new DomainWrapper(ObjectAccessorFactory.forBean(stmt.table().javaType()));

    }

    /**
     * create for {@link  ChildTableMeta}
     */
    private DomainInsertContext(DomainInsertContext parentContext, _Insert._DomainInsert stmt
            , ArmyDialect dialect, Visible visible) {
        super(stmt, dialect, visible);
        assert parentContext.table == ((ChildTableMeta<?>) this.table).parentMeta();

        this.domainList = stmt.domainList();
        assert this.domainList == parentContext.domainList;
        this.wrapper = parentContext.wrapper;

    }


    @Override
    public void appendField(String tableAlias, FieldMeta<?> field) {
        if (!this.valuesClauseEnd) {
            // domain insert don't support insert any field in expression
            throw _Exceptions.unknownColumn(tableAlias, field);
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendField(FieldMeta<?> field) {
        if (!this.valuesClauseEnd) {
            // domain insert don't support insert any field in expression
            throw _Exceptions.unknownColumn(field);
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendValueList() {
        assert !this.valuesClauseEnd;

        final List<IDomain> domainList = this.domainList;
        final List<FieldMeta<?>> fieldList = this.fieldList;
        final int domainSize = domainList.size();
        final int fieldSize = fieldList.size();

        final ArmyDialect dialect = this.dialect;
        final FieldMeta<?> discriminator = this.discriminator;
        final Map<FieldMeta<?>, _Expression> defaultValueMap = this.defaultValueMap;

        final boolean preferLiteral = this.preferLiteral;
        final boolean migration = this.migration;
        final boolean mockEnv = dialect.isMockEnv();
        final NullHandleMode nullHandleMode = this.nullHandleMode;
        final StringBuilder sqlBuilder = this.sqlBuilder;

        sqlBuilder.append(_Constant.SPACE_VALUES);

        final _FieldValueGenerator generator;
        final DomainWrapper domainWrapper = this.wrapper;
        final ObjectAccessor accessor = domainWrapper.accessor;
        final TableMeta<?> table = this.table;
        if (table instanceof ChildTableMeta) {
            generator = null;
        } else {
            generator = dialect.getFieldValueGenerator();
        }
        IDomain domain;
        FieldMeta<?> field;
        _Expression expression;
        Object value;
        MappingType mappingType;
        DelayIdParamValue delayIdParam;
        for (int rowIndex = 0; rowIndex < domainSize; rowIndex++) {
            domain = domainList.get(rowIndex);
            domainWrapper.domain = domain; //update current domain
            if (generator != null) {
                if (migration) {
                    generator.validate(table, domainWrapper);
                } else {
                    generator.generate(table, domainWrapper);
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
                    assert table instanceof SingleTableMeta;
                    sqlBuilder.append(_Constant.SPACE)
                            .append(this.discriminatorValue);
                } else if (field instanceof PrimaryFieldMeta
                        && table instanceof ChildTableMeta
                        && ((ChildTableMeta<?>) table).parentMeta().id().generatorType() == GeneratorType.POST) {
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
                        this.appendParam(ParamValue.build(field, value));
                    }
                } else if ((expression = defaultValueMap.get(field)) != null) {
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
        }//outer for

        this.valuesClauseEnd = true;
    }


    @Override
    public SimpleStmt build() {
        final SimpleStmt stmt;
        final TableMeta<?> table = this.table;
        if (this.returnId != null) {
            //dialect support returning clause
            stmt = Stmts.domainPost(this);
        } else if (this.duplicateKeyClause
                || table instanceof ChildTableMeta
                || table.id().generatorType() != GeneratorType.POST) {
            stmt = Stmts.minSimple(this);
        } else {
            stmt = Stmts.domainPost(this);
        }
        return stmt;
    }


    @Override
    public List<IDomain> domainList() {
        return this.domainList;
    }

    @Override
    public ObjectAccessor domainAccessor() {
        return this.wrapper.accessor;
    }


    @Nullable
    @Override
    Object readNamedParam(final String name) {
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


    private static final class DomainWrapper implements ObjectWrapper {

        private final ObjectAccessor accessor;

        private final BeanReadWrapper readWrapper;

        private IDomain domain;

        private DomainWrapper(ObjectAccessor accessor) {
            this.accessor = accessor;
            this.readWrapper = new BeanReadWrapper(this);
        }

        @Override
        public boolean isWritable(String propertyName) {
            return this.accessor.isWritable(propertyName);
        }

        @Override
        public void set(String propertyName, @Nullable Object value) throws ObjectAccessException {
            final IDomain domain;
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
            final IDomain domain;
            domain = this.domain;
            assert domain != null;
            return this.accessor.get(domain, propertyName);
        }


    }//DomainWrapper


    private static final class DelayIdParamValue implements ParamValue {

        private final PrimaryFieldMeta<?> field;

        private final IDomain domain;

        private final ObjectAccessor accessor;

        private DelayIdParamValue(PrimaryFieldMeta<?> field, IDomain domain, ObjectAccessor accessor) {
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
