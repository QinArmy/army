package io.army.dialect;

import io.army.annotation.GeneratorType;
import io.army.bean.ObjectAccessException;
import io.army.bean.ObjectAccessor;
import io.army.bean.ObjectAccessorFactory;
import io.army.bean.ReadWrapper;
import io.army.criteria.LiteralMode;
import io.army.criteria.NullMode;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.lang.Nullable;
import io.army.mapping.MappingEnv;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.stmt.InsertStmtParams;
import io.army.stmt.SingleParam;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.ObjIntConsumer;

/**
 * <p>
 * This class representing standard value insert context.
 * </p>
 */
final class DomainInsertContext extends ValuesSyntaxInsertContext implements InsertStmtParams {

    static DomainInsertContext forSingle(@Nullable _SqlContext outerContext, _Insert._DomainInsert insert
            , ArmyParser dialect, Visible visible) {
        assert !(insert instanceof _Insert._ChildDomainInsert);
        return new DomainInsertContext((StatementContext) outerContext, insert, dialect, visible);
    }


    static DomainInsertContext forParent(@Nullable _SqlContext outerContext, _Insert._ChildDomainInsert domainStmt
            , ArmyParser dialect, Visible visible) {

        return new DomainInsertContext((StatementContext) outerContext, domainStmt, dialect, visible);
    }

    static DomainInsertContext forChild(@Nullable _SqlContext outerContext, _Insert._ChildDomainInsert insert
            , DomainInsertContext parentContext) {

        return new DomainInsertContext((StatementContext) outerContext, insert, parentContext);
    }

    private final DomainWrapper wrapper;

    private final List<?> domainList;


    /**
     * create for {@link  SingleTableMeta}
     */
    private DomainInsertContext(@Nullable StatementContext outerContext, _Insert._DomainInsert domainStmt
            , ArmyParser dialect, Visible visible) {
        super(outerContext, domainStmt, dialect, visible);

        this.domainList = domainStmt.domainList();
        this.wrapper = new DomainWrapper(this, domainStmt);
    }


    /**
     * create for {@link  ChildTableMeta}
     */
    private DomainInsertContext(@Nullable StatementContext outerContext, _Insert._ChildDomainInsert stmt
            , DomainInsertContext parentContext) {
        super(outerContext, stmt, parentContext);

        this.domainList = stmt.domainList();
        assert this.domainList == parentContext.domainList;//must check for criteria api implementation
        this.wrapper = parentContext.wrapper;
        assert this.wrapper.domainTable == this.insertTable;
        assert this.wrapper.childDefaultMap == stmt.defaultValueMap();
    }


    @Override
    int doAppendValuesList(final int outputColumnSize, final List<FieldMeta<?>> fieldList) {

        final List<?> domainList = this.domainList;
        final int rowSize = domainList.size();
        assert rowSize > 0; //must check for criteria api implementation
        final int fieldSize = fieldList.size();


        final ArmyParser dialect = this.parser;
        final Map<FieldMeta<?>, _Expression> defaultValueMap;

        final LiteralMode literalMode = this.literalMode;
        final boolean migration = this.migration;
        final boolean mockEnv = dialect.mockEnv;
        final NullMode nullMode = this.nullMode;

        final DomainWrapper wrapper = this.wrapper;
        final ObjectAccessor accessor = wrapper.accessor;
        final TableMeta<?> insertTable = this.insertTable, domainTable = wrapper.domainTable;
        final boolean postParentId = insertTable.nonChildId().generatorType() == GeneratorType.POST;

        final boolean manageVisible;
        final FieldMeta<?> discriminator = domainTable.discriminator();
        final String spaceDiscriminator;
        if (domainTable instanceof ChildTableMeta) {
            spaceDiscriminator = _Constant.SPACE + Integer.toString(domainTable.discriminatorValue().code());
        } else {
            spaceDiscriminator = _Constant.SPACE_ZERO;
        }
        final FieldValueGenerator generator;
        if (insertTable instanceof ChildTableMeta) {
            assert insertTable == domainTable;
            generator = null;
            manageVisible = false;
            defaultValueMap = wrapper.childDefaultMap;
        } else {
            generator = dialect.generator;
            final FieldMeta<?> visibleField;
            visibleField = insertTable.tryGetField(_MetaBridge.VISIBLE);
            manageVisible = visibleField != null && !wrapper.nonChildDefaultMap.containsKey(visibleField);
            defaultValueMap = wrapper.nonChildDefaultMap;
        }
        FieldMeta<?> field;
        _Expression expression;
        Object value, currentDomain;
        DelayIdParamValue delayIdParam;
        int outputValueSize = 0;

        final StringBuilder sqlBuilder = this.sqlBuilder;
        sqlBuilder.append(_Constant.SPACE_VALUES); // VALUES key words
        for (int rowIndex = 0; rowIndex < rowSize; rowIndex++) {
            currentDomain = domainList.get(rowIndex);
            wrapper.domain = currentDomain; //firstly,update current domain

            if (generator != null) {
                if (migration) {
                    //use ths.domainTable,not this.insertTable
                    generator.validate(domainTable, wrapper);
                } else {
                    //use ths.domainTable,not this.insertTable
                    generator.generate(domainTable, manageVisible, wrapper);
                }
            }

            if (rowIndex > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);

            delayIdParam = null;//clear
            for (int fieldIndex = 0, actualFieldIndex = 0; fieldIndex < fieldSize; fieldIndex++) {
                field = fieldList.get(fieldIndex);
                if (!migration && !field.insertable()) {
                    // fieldList have be checked,fieldList possibly is io.army.meta.TableMeta.fieldList()
                    continue;
                }
                if (actualFieldIndex > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                actualFieldIndex++;
                outputValueSize = actualFieldIndex;
                if (field == discriminator) {
                    assert insertTable instanceof ParentTableMeta;
                    sqlBuilder.append(spaceDiscriminator);
                } else if (postParentId && field instanceof PrimaryFieldMeta && insertTable instanceof ChildTableMeta) {
                    assert delayIdParam == null;
                    delayIdParam = new DelayIdParamValue((PrimaryFieldMeta<?>) field, currentDomain, accessor);
                    this.appendParam(delayIdParam);
                } else if ((value = accessor.get(currentDomain, field.fieldName())) != null) {
                    this.appendInsertValue(literalMode, field, value);
                } else if (field instanceof PrimaryFieldMeta && insertTable instanceof ChildTableMeta) {//child id must be managed by army
                    assert mockEnv; // must assert
                    this.appendInsertValue(literalMode, field, null);
                } else if ((expression = defaultValueMap.get(field)) != null) {
                    expression.appendSql(this);
                } else if (field.generatorType() == GeneratorType.PRECEDE) {
                    assert mockEnv || migration;
                    if (migration && !field.nullable()) {
                        throw _Exceptions.nonNullField(field);
                    }
                    this.appendInsertValue(literalMode, field, null);
                } else if (nullMode == NullMode.INSERT_DEFAULT) {
                    sqlBuilder.append(_Constant.SPACE_DEFAULT);
                } else if (nullMode != NullMode.INSERT_NULL) {
                    throw _Exceptions.unexpectedEnum(nullMode);
                } else if (!field.nullable()) {
                    throw _Exceptions.nonNullField(field);
                } else {
                    this.appendInsertValue(literalMode, field, null);
                }

            }//inner for

            assert outputValueSize == outputColumnSize; //assert value size and column size match.
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

        }//outer for

        wrapper.domain = null; //finally must clear
        return outputValueSize;
    }


    @Override
    public int rowSize() {
        return this.domainList.size();
    }

    @Override
    public ObjIntConsumer<Object> idConsumer() {
        final PrimaryFieldMeta<?> idField = this.returnId;
        assert idField != null;
        final String fieldName = idField.fieldName();
        final List<?> domainList = this.domainList;
        final int domainSize = domainList.size();
        final ObjectAccessor accessor = this.wrapper.accessor;

        return (idValue, indexBaseZero) -> {
            if (idValue == null) {
                //no bug,never here
                throw new NullPointerException();
            }
            if (indexBaseZero < 0 || indexBaseZero >= domainSize) {
                //no bug never here
                throw new IllegalArgumentException();
            }
            final Object domain;
            domain = domainList.get(indexBaseZero);
            if (accessor.get(domain, fieldName) != null) {
                throw _Exceptions.duplicateIdValue(indexBaseZero, idField, idValue);
            }
            accessor.set(domain, fieldName, idValue);

        };
    }

    @Nullable
    @Override
    Object currentRowNamedValue(final String name) {
        final DomainWrapper wrapper = this.wrapper;
        final Object domain = wrapper.domain;
        assert domain != null;
        return wrapper.accessor.get(domain, name);
    }


    private static final class DomainReadWrapper implements ReadWrapper {

        private final DomainWrapper wrapper;

        private final MappingEnv mappingEnv;

        DomainReadWrapper(DomainWrapper wrapper, MappingEnv mappingEnv) {
            this.wrapper = wrapper;
            this.mappingEnv = mappingEnv;
        }

        @Override
        public boolean isReadable(final String propertyName) {
            return this.wrapper.domainTable.containComplexField(propertyName);
        }

        @Override
        public Object get(final String propertyName) throws ObjectAccessException {
            final DomainWrapper wrapper = this.wrapper;

            final FieldMeta<?> field;
            field = wrapper.domainTable.tryGetComplexFiled(propertyName);
            if (field == null) {
                throw _Exceptions.nonReadableProperty(wrapper.domainTable, propertyName);
            }
            final Object domain = wrapper.domain;
            assert domain != null;

            Object value;
            value = wrapper.accessor.get(domain, propertyName);
            if (value != null) {
                return value;
            }
            final _Expression expression;
            if (field.tableMeta() instanceof ChildTableMeta) {
                expression = wrapper.childDefaultMap.get(field);
            } else {
                expression = wrapper.nonChildDefaultMap.get(field);
            }
            return _DialectUtils.readParamValue(field, expression, this.mappingEnv);
        }


    }//BeanReadWrapper


    private static final class DomainWrapper implements RowWrapper {

        private final TableMeta<?> domainTable;

        private final Map<FieldMeta<?>, _Expression> nonChildDefaultMap;

        private final Map<FieldMeta<?>, _Expression> childDefaultMap;

        private final ObjectAccessor accessor;

        private final DomainReadWrapper readWrapper;

        private Object domain;

        private DomainWrapper(DomainInsertContext context, _Insert._DomainInsert domainStmt) {
            this.domainTable = domainStmt.table();

            if (domainStmt instanceof _Insert._ChildDomainInsert) {
                assert ((ChildTableMeta<?>) this.domainTable).parentMeta() == context.insertTable;
                final _Insert._DomainInsert parentStmt = ((_Insert._ChildDomainInsert) domainStmt).parentStmt();
                this.nonChildDefaultMap = parentStmt.defaultValueMap();
                this.childDefaultMap = domainStmt.defaultValueMap();
            } else {
                assert this.domainTable == context.insertTable;
                this.nonChildDefaultMap = domainStmt.defaultValueMap();
                this.childDefaultMap = Collections.emptyMap();
            }
            this.accessor = ObjectAccessorFactory.forBean(this.domainTable.javaType());
            this.readWrapper = new DomainReadWrapper(this, context.parser.mappingEnv);
        }

        @Override
        public void set(final FieldMeta<?> field, final @Nullable Object value) {
            final Object domain = this.domain;
            assert domain != null;
            this.accessor.set(domain, field.fieldName(), value);
        }

        @Override
        public boolean isNullValueParam(final FieldMeta<?> field) {
            final Object domain = this.domain;
            assert domain != null;
            return this.accessor.get(domain, field.fieldName()) == null;
        }

        @Override
        public ReadWrapper readonlyWrapper() {
            return this.readWrapper;
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
        public TypeMeta typeMeta() {
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
