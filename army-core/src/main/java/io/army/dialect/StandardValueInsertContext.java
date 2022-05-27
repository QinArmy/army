package io.army.dialect;

import io.army.annotation.GeneratorType;
import io.army.bean.ObjectAccessException;
import io.army.bean.ObjectAccessor;
import io.army.bean.ObjectAccessorFactory;
import io.army.bean.ReadWrapper;
import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._ValuesInsert;
import io.army.domain.IDomain;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.stmt.ParamValue;
import io.army.stmt.SimpleStmt;
import io.army.stmt.StrictParamValue;
import io.army.util._Exceptions;

import java.util.*;

/**
 * <p>
 * This class representing standard value insert context.
 * </p>
 */
final class StandardValueInsertContext extends StmtContext implements _ValueInsertContext {

    static StandardValueInsertContext create(_ValuesInsert insert, ArmyDialect dialect, Visible visible) {
        checkCommonExpMap(insert);
        return new StandardValueInsertContext(insert, dialect, visible);
    }


    private static void checkCommonExpMap(_ValuesInsert insert) {
        final TableMeta<?> table = insert.table();
        for (Map.Entry<FieldMeta<?>, _Expression> e : insert.commonExpMap().entrySet()) {
            _DmlUtils.checkInsertExpField(table, e.getKey(), e.getValue());
        }
    }


    final boolean migration;

    final NullHandleMode nullHandleMode;

    final boolean preferLiteral;

    final TableMeta<?> table;

    final List<FieldMeta<?>> fieldList;

    final Map<FieldMeta<?>, _Expression> commonExpMap;

    final ObjectAccessor domainAccessor;

    final List<IDomain> domainList;

    private final PrimaryFieldMeta<?> returnId;

    private final FieldMeta<?> discriminator;

    private final int discriminatorValue;


    //@see io.army.dialect._DmlUtils.appendStandardValueInsert,for parse comment expression
    private IDomain currentDomain;


    /**
     * create for {@link  ChildTableMeta}
     */
    private StandardValueInsertContext(_ValuesInsert stmt, ArmyDialect dialect, Visible visible) {
        super(dialect, stmt.isPreferLiteral(), visible);

        this.migration = stmt.isMigration();
        this.preferLiteral = stmt.isPreferLiteral();
        this.commonExpMap = stmt.commonExpMap();
        this.domainList = stmt.domainList();

        if (this.migration) {
            this.nullHandleMode = NullHandleMode.INSERT_NULL;
        } else {
            this.nullHandleMode = stmt.nullHandle();
        }
        final ChildTableMeta<?> table = (ChildTableMeta<?>) stmt.table();

        this.table = table;
        this.discriminator = table.discriminator();
        this.discriminatorValue = table.discriminatorValue();
        this.domainAccessor = ObjectAccessorFactory.forBean(table.javaType());

        final List<FieldMeta<?>> childFieldList = stmt.childFieldList();
        if (childFieldList.size() == 0) {
            this.fieldList = mergeFieldList(table);
        } else {
            this.fieldList = mergeFieldList(table, childFieldList);
        }
        this.returnId = null;

    }

    /**
     * create for {@link  SingleTableMeta}
     */
    private StandardValueInsertContext(ArmyDialect dialect, _ValuesInsert stmt, Visible visible) {
        super(dialect, visible);
        this.migration = stmt.isMigration();
        this.preferLiteral = stmt.isPreferLiteral();
        this.commonExpMap = stmt.commonExpMap();
        this.domainList = stmt.domainList();

        if (this.migration) {
            this.nullHandleMode = NullHandleMode.INSERT_NULL;
        } else {
            this.nullHandleMode = stmt.nullHandle();
        }
        final TableMeta<?> table = stmt.table();
        if (table instanceof SimpleTableMeta) {
            this.discriminator = null;
            this.discriminatorValue = 0;
        } else {
            this.discriminator = table.discriminator();
            this.discriminatorValue = table.discriminatorValue();
        }
        if (table instanceof ChildTableMeta) {
            this.table = ((ChildTableMeta<?>) table).parentMeta();
        } else {
            this.table = table;
        }
        this.domainAccessor = ObjectAccessorFactory.forBean(table.javaType());

        final List<FieldMeta<?>> fieldList = stmt.fieldList();
        if (fieldList.size() == 0) {
            this.fieldList = mergeFieldList(table);
        } else {
            this.fieldList = mergeFieldList(table, fieldList);
        }
        if (!dialect.supportInsertReturning()) {
            this.returnId = null;
        } else if (table instanceof ChildTableMeta) {
            this.returnId = ((ChildTableMeta<?>) table).parentMeta().id();
        } else {
            this.returnId = table.id();
        }

    }


    @Override
    public void appendField(String tableAlias, FieldMeta<?> field) {
        // value insert don't support insert any field in expression
        throw _Exceptions.unknownColumn(tableAlias, field);
    }

    @Override
    public void appendField(FieldMeta<?> field) {
        // value insert don't support insert any field in expression
        throw _Exceptions.unknownColumn(null, field);
    }

    @Override
    public String safeTableAlias(TableMeta<?> table, String alias) {
        throw new UnsupportedOperationException();
    }


    @Override
    public void appendFieldList() {
        final List<FieldMeta<?>> fieldList = this.fieldList;
        final int fieldSize = fieldList.size();
        final ArmyDialect dialect = this.dialect;
        final StringBuilder sqlBuilder = this.sqlBuilder;

        dialect.safeObjectName(this.table.tableName(), sqlBuilder)
                .append(_Constant.SPACE_LEFT_PAREN);

        for (int i = 0; i < fieldSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            dialect.safeObjectName(fieldList.get(i).columnName(), sqlBuilder);
        }
        sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
    }

    @Override
    public void appendValueList() {
        final List<IDomain> domainList = this.domainList;
        final List<FieldMeta<?>> fieldList = this.fieldList;
        final int domainSize = domainList.size();
        final int fieldSize = fieldList.size();

        final ObjectAccessor accessor = this.domainAccessor;
        final ArmyDialect dialect = this.dialect;
        final FieldMeta<?> discriminator = this.discriminator;
        final Map<FieldMeta<?>, _Expression> commonExpMap = this.commonExpMap;

        final boolean preferLiteral = this.preferLiteral;
        final boolean mockEnv = dialect.isMockEnv();
        final NullHandleMode nullHandleMode = this.nullHandleMode;
        final StringBuilder sqlBuilder = this.sqlBuilder;

        sqlBuilder.append(_Constant.SPACE_VALUES);

        final FieldValueGenerator generator;
        final BeanReadWrapper readWrapper;
        final TableMeta<?> table = this.table;
        if (table instanceof ChildTableMeta) {
            generator = null;
            readWrapper = null;
        } else {
            generator = dialect.getFieldValueGenerator();
            readWrapper = new BeanReadWrapper(accessor);
        }
        IDomain domain;
        FieldMeta<?> field;
        _Expression expression;
        Object value;
        MappingType mappingType;
        for (int domainIndex = 0; domainIndex < domainSize; domainIndex++) {
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

            for (int fieldIndex = 0; fieldIndex < fieldSize; fieldIndex++) {
                if (fieldIndex > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                field = fieldList.get(fieldIndex);
                if (field == discriminator) {
                    assert field != null;
                    sqlBuilder.append(_Constant.SPACE)
                            .append(this.discriminatorValue);
                } else if ((expression = commonExpMap.get(field)) != null) {
                    this.currentDomain = domain; //update current domain for SubQuery
                    expression.appendSql(this);
                } else if ((value = accessor.get(domain, field.fieldName())) != null) {
                    mappingType = field.mappingType();
                    if (preferLiteral && mappingType instanceof _ArmyNoInjectionMapping) {//TODO field codec
                        dialect.literal(mappingType, value, sqlBuilder);
                    } else {
                        this.appendParam(ParamValue.build(field, value));
                    }
                } else if (field instanceof PrimaryFieldMeta
                        && table instanceof ChildTableMeta
                        && ((ChildTableMeta<?>) table).parentMeta().id().generatorType() == GeneratorType.POST) {
                    this.appendParam(new DelayIdParamValue(field, domain, accessor));
                } else if (field.generatorType() == GeneratorType.PRECEDE) {
                    if (!mockEnv) {
                        throw _Exceptions.generatorFieldIsNull(field);
                    }
                    if (preferLiteral) { //TODO field codec
                        sqlBuilder.append(" mock:{generator}");
                    } else {
                        this.appendParam(ParamValue.build(field, null));
                    }

                } else if (nullHandleMode == NullHandleMode.INSERT_DEFAULT) {
                    sqlBuilder.append(_Constant.SPACE_DEFAULT);
                } else if (field.nullable()) {
                    sqlBuilder.append(_Constant.SPACE_NULL);
                } else {
                    throw _Exceptions.nonNullField(field);
                }

            }

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }

    }

    @Override
    public SimpleStmt build() {
        return null;
    }


    @Override
    List<ParamValue> createParamList() {
        return new ProxyList(this::handleNamedParam);
    }

    private ParamValue handleNamedParam(final NamedParam namedParam) {
        //this.currentDomain @see io.army.dialect._DmlUtils.appendStandardValueInsert
        final IDomain domain = this.currentDomain;
        assert domain != null;
        this.currentDomain = null; //clear for next
        final Object value;
        value = this.domainAccessor.get(domain, namedParam.name());
        if (value == null && namedParam instanceof NonNullNamedParam) {
            throw _Exceptions.nonNullNamedParam((NonNullNamedParam) namedParam);
        }
        return ParamValue.build(namedParam.paramMeta(), value);
    }


    private <T extends IDomain> List<FieldMeta<?>> mergeFieldList(final TableMeta<T> table) {
        final List<FieldMeta<T>> fieldList = table.fieldList();
        final List<FieldMeta<?>> mergeFieldList = new ArrayList<>(fieldList.size());
        for (FieldMeta<T> field : fieldList) {
            if (field.insertable()) {
                mergeFieldList.add(field);
            }
        }
        if (mergeFieldList.size() == 0) {
            String m = String.format("%s no insertable filed.", table);
            throw new MetaException(m);
        }
        return Collections.unmodifiableList(mergeFieldList);
    }

    private <T extends IDomain> List<FieldMeta<?>> mergeFieldList(final TableMeta<T> table
            , final List<FieldMeta<?>> fieldList) {
        final List<FieldMeta<?>> mergeFieldList = new ArrayList<>(fieldList.size());
        final Map<FieldMeta<?>, Boolean> fieldMap = new HashMap<>();
        for (FieldMeta<?> field : fieldList) {
            if (!field.insertable()) {
                throw _Exceptions.nonInsertableField(field);
            }
            if (field.tableMeta() != table) {
                throw _Exceptions.unknownColumn(null, field);
            }
            if (fieldMap.putIfAbsent(field, Boolean.TRUE) != null) {
                String m = String.format("%s duplication.", field);
                throw new CriteriaException(m);
            }
            mergeFieldList.add(field);
        }

        for (FieldMeta<?> field : table.fieldChain()) {
            if (fieldMap.putIfAbsent(field, Boolean.TRUE) == null) {
                mergeFieldList.add(field);
            }
        }

        FieldMeta<?> field;

        field = table.id();
        if (fieldMap.putIfAbsent(field, Boolean.TRUE) == null) {
            mergeFieldList.add(field);
        }

        if (table instanceof ParentTableMeta) {
            field = table.discriminator();
            if (fieldMap.putIfAbsent(field, Boolean.TRUE) == null) {
                mergeFieldList.add(field);
            }
        }

        if (!(table instanceof ChildTableMeta)) {
            field = table.getField(_MetaBridge.CREATE_TIME);
            if (fieldMap.putIfAbsent(field, Boolean.TRUE) == null) {
                mergeFieldList.add(field);
            }
            if (table.containField(_MetaBridge.UPDATE_TIME)) {
                field = table.getField(_MetaBridge.UPDATE_TIME);
                if (fieldMap.putIfAbsent(field, Boolean.TRUE) == null) {
                    mergeFieldList.add(field);
                }
            }
            if (table.containField(_MetaBridge.VERSION)) {
                field = table.getField(_MetaBridge.VERSION);
                if (fieldMap.putIfAbsent(field, Boolean.TRUE) == null) {
                    mergeFieldList.add(field);
                }
            }
            if (table.containField(_MetaBridge.VISIBLE)) {
                field = table.getField(_MetaBridge.VISIBLE);
                if (fieldMap.putIfAbsent(field, Boolean.TRUE) == null) {
                    mergeFieldList.add(field);
                }
            }

        }


        if (mergeFieldList.size() == 0) {
            String m = String.format("%s no insertable filed.", table);
            throw new CriteriaException(m);
        }
        return Collections.unmodifiableList(mergeFieldList);
    }


    private static final class BeanReadWrapper implements ReadWrapper {


        private IDomain domain;
        private final ObjectAccessor accessor;

        private BeanReadWrapper(ObjectAccessor accessor) {
            this.accessor = accessor;
        }

        @Override
        public boolean isReadable(String propertyName) {
            return this.accessor.isReadable(propertyName);
        }

        @Override
        public Object get(String propertyName) throws ObjectAccessException {
            return this.accessor.get(this.domain, propertyName);
        }

        @Override
        public Class<?> getWrappedClass() {
            return this.accessor.getAccessedType();
        }

        @Override
        public ObjectAccessor getObjectAccessor() {
            return this.accessor;
        }

    }//BeanReadWrapper


    private static final class DelayIdParamValue implements StrictParamValue {

        private final ParamMeta paramMeta;

        private final IDomain domain;

        private final ObjectAccessor accessor;

        private DelayIdParamValue(ParamMeta paramMeta, IDomain domain, ObjectAccessor accessor) {
            this.paramMeta = paramMeta;
            this.domain = domain;
            this.accessor = accessor;
        }

        @Override
        public ParamMeta paramMeta() {
            return this.paramMeta;
        }

        @Override
        public Object value() {
            final Object value;
            value = this.accessor.get(this.domain, _MetaBridge.ID);
            if (value == null) {
                throw new IllegalStateException("parent insert statement don't execute.");
            }
            return value;
        }

    }


}
