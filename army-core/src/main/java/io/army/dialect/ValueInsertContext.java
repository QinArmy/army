package io.army.dialect;

import io.army.annotation.GeneratorType;
import io.army.bean.ObjectAccessException;
import io.army.bean.ObjectAccessor;
import io.army.bean.ObjectAccessorFactory;
import io.army.bean.ReadWrapper;
import io.army.criteria.*;
import io.army.criteria.impl.inner._DomainInsert;
import io.army.criteria.impl.inner._Expression;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.stmt.InsertStmtParams;
import io.army.stmt.ParamValue;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import java.util.*;

/**
 * <p>
 * This class representing standard value insert context.
 * </p>
 */
final class ValueInsertContext extends StatementContext implements _ValueInsertContext, InsertStmtParams {

    static ValueInsertContext nonChild(_DomainInsert insert, ArmyDialect dialect, Visible visible) {
        checkCommonExpMap(insert);
        return new ValueInsertContext(dialect, insert, visible);
    }

    static ValueInsertContext child(_DomainInsert insert, ArmyDialect dialect, Visible visible) {
        return new ValueInsertContext(insert, dialect, visible);
    }


    private static void checkCommonExpMap(_DomainInsert insert) {
        final TableMeta<?> table = insert.table();
        for (Map.Entry<FieldMeta<?>, _Expression> e : insert.commonExpMap().entrySet()) {
            _DialectUtils.checkInsertExpField(table, e.getKey(), e.getValue());
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

    /**
     * return id for standard criteria api
     */
    private final PrimaryFieldMeta<?> returnId;

    private final FieldMeta<?> discriminator;

    private final int discriminatorValue;

    private final String idSelectionAlias;


    private IDomain currentDomain;


    /**
     * create for {@link  SingleTableMeta}
     */
    private ValueInsertContext(ArmyDialect dialect, _DomainInsert stmt, Visible visible) {
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
        final TableMeta<?> domainTable = stmt.table();
        if (domainTable instanceof SimpleTableMeta) {
            this.discriminator = null;
            this.discriminatorValue = 0;
        } else {
            this.discriminator = domainTable.discriminator();
            this.discriminatorValue = domainTable.discriminatorValue();
        }
        if (domainTable instanceof ChildTableMeta) {
            this.table = ((ChildTableMeta<?>) domainTable).parentMeta();
        } else {
            this.table = domainTable;
        }
        this.domainAccessor = ObjectAccessorFactory.forBean(domainTable.javaType());

        final List<FieldMeta<?>> fieldList = stmt.fieldList();
        if (fieldList.size() == 0) {
            this.fieldList = castFieldList(this.table);
        } else {
            this.fieldList = mergeFieldList(this.table, fieldList);
        }
        if (stmt instanceof StandardStatement || !dialect.supportInsertReturning()) {
            this.returnId = null;
            this.idSelectionAlias = null;
        } else {
            this.returnId = this.table.id();
            this.idSelectionAlias = this.returnId.fieldName();
        }

    }

    /**
     * create for {@link  ChildTableMeta}
     */
    private ValueInsertContext(_DomainInsert stmt, ArmyDialect dialect, Visible visible) {
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
            this.fieldList = castFieldList(table);
        } else {
            this.fieldList = mergeFieldList(table, childFieldList);
        }
        this.returnId = null;
        this.idSelectionAlias = null;

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
    public TableMeta<?> table() {
        return this.table;
    }

    @Override
    public void appendFieldList() {
        final List<FieldMeta<?>> fieldList = this.fieldList;
        final int fieldSize = fieldList.size();
        final ArmyDialect dialect = this.dialect;
        final StringBuilder sqlBuilder = this.sqlBuilder
                .append(_Constant.SPACE_LEFT_PAREN);

        FieldMeta<?> field;
        for (int i = 0, actualIndex = 0; i < fieldSize; i++) {
            field = fieldList.get(i);
            if (!field.insertable()) {
                // fieldList have be checked,fieldList possibly is io.army.meta.TableMeta.fieldList()
                continue;
            }
            if (actualIndex > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            dialect.safeObjectName(fieldList.get(i), sqlBuilder);
            actualIndex++;
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

        final _FieldValueGenerator generator;
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
                if (this.migration) {
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
                } else if ((expression = commonExpMap.get(field)) != null) {
                    this.currentDomain = domain; //update current domain for SubQuery
                    expression.appendSql(this);
                } else if ((value = accessor.get(domain, field.fieldName())) != null) {
                    mappingType = field.mappingType();
                    if (preferLiteral && mappingType instanceof _ArmyNoInjectionMapping) {//TODO field codec
                        sqlBuilder.append(_Constant.SPACE);
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
        final SimpleStmt stmt;
        if (this.table.id().generatorType() == GeneratorType.POST) {
            stmt = Stmts.post(this);
        } else {
            stmt = Stmts.minSimple(this);
        }
        return stmt;
    }

    @Override
    public void appendReturnIdIfNeed() {
        final PrimaryFieldMeta<?> returnId = this.returnId;
        if (returnId == null) {
            return;
        }
        final StringBuilder sqlBuilder = this.sqlBuilder
                .append(_Constant.SPACE_RETURNING)
                .append(_Constant.SPACE);

        final ArmyDialect dialect = this.dialect;
        //TODO for dialect table alias
        dialect.safeObjectName(returnId, sqlBuilder)
                .append(_Constant.SPACE_AS_SPACE);

        dialect.identifier(returnId.fieldName(), sqlBuilder);
    }

    @Override
    public List<IDomain> domainList() {
        return this.domainList;
    }

    @Override
    public ObjectAccessor domainAccessor() {
        return this.domainAccessor;
    }

    @Override
    public List<Selection> selectionList() {
        return Collections.emptyList();
    }

    @Override
    public PrimaryFieldMeta<?> returnId() {
        return this.returnId;
    }


    @Override
    public String idReturnAlias() {
        return this.idSelectionAlias;
    }


    @Nullable
    @Override
    Object readNamedParam(final String name) {
        final IDomain domain = this.currentDomain;
        assert domain != null;
        return this.domainAccessor.get(domain, name);
    }

    @SuppressWarnings("unchecked")
    private static <T extends IDomain> List<FieldMeta<?>> castFieldList(final TableMeta<T> table) {
        final List<?> list;
        list = table.fieldList();
        return (List<FieldMeta<?>>) list;
    }

    private static <T extends IDomain> List<FieldMeta<?>> mergeFieldList(final TableMeta<T> table
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


    private static final class DelayIdParamValue implements ParamValue {

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

    }//DelayIdParamValue


}
