package io.army.dialect;

import io.army.annotation.GeneratorType;
import io.army.bean.ObjectAccessor;
import io.army.bean.ObjectAccessorFactory;
import io.army.criteria.NullHandleMode;
import io.army.criteria.Selection;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
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

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * This class representing standard value insert context.
 * </p>
 */
final class DomainInsertContext extends ValuesSyntaxInsertContext implements InsertStmtParams {

    static DomainInsertContext forSingle(_Insert._DomainInsert insert, ArmyDialect dialect, Visible visible) {
        _DialectUtils.checkCommonExpMap(insert);
        return new DomainInsertContext(dialect, insert, visible);
    }

    static DomainInsertContext forChild(_Insert._DomainInsert insert, ArmyDialect dialect, Visible visible) {
        return new DomainInsertContext(insert, dialect, visible);
    }


    private final boolean preferLiteral;

    private final ObjectAccessor domainAccessor;

    private final List<IDomain> domainList;


    private IDomain currentDomain;


    /**
     * create for {@link  SingleTableMeta}
     */
    private DomainInsertContext(ArmyDialect dialect, _Insert._DomainInsert stmt, Visible visible) {
        super(dialect, stmt, visible);

        this.preferLiteral = stmt.isPreferLiteral();
        this.domainList = stmt.domainList();
        this.domainAccessor = ObjectAccessorFactory.forBean(stmt.table().javaType());

    }

    /**
     * create for {@link  ChildTableMeta}
     */
    private DomainInsertContext(_Insert._DomainInsert stmt, ArmyDialect dialect, Visible visible) {
        super(stmt, dialect, visible);

        this.preferLiteral = stmt.isPreferLiteral();
        this.domainList = stmt.domainList();
        this.domainAccessor = ObjectAccessorFactory.forBean(this.table.javaType());

    }


    @Override
    public void appendField(String tableAlias, FieldMeta<?> field) {
        // domain insert don't support insert any field in expression
        throw _Exceptions.unknownColumn(tableAlias, field);
    }

    @Override
    public void appendField(FieldMeta<?> field) {
        // domain insert don't support insert any field in expression
        throw _Exceptions.unknownColumn(null, field);
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
        final boolean migration = this.migration;
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
                    this.appendParam(new DelayIdParamValue(field, domain, accessor));
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
        }

    }


    @Override
    public SimpleStmt build() {
        final SimpleStmt stmt;
        if (this.table.id().generatorType() != GeneratorType.POST || this.duplicateKeyClause) {
            stmt = Stmts.minSimple(this);
        } else {
            stmt = Stmts.post(this);
        }
        return stmt;
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
