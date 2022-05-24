package io.army.dialect;

import io.army.annotation.GeneratorType;
import io.army.bean.ObjectAccessException;
import io.army.bean.ObjectAccessor;
import io.army.bean.ReadWrapper;
import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._Update;
import io.army.criteria.impl.inner._ValuesInsert;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.stmt.ParamValue;
import io.army.stmt.StrictParamValue;
import io.army.util.ArrayUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.*;

public abstract class _DmlUtils {

    _DmlUtils() {
        throw new UnsupportedOperationException();
    }


    static final Collection<String> FORBID_INSERT_FIELDS = ArrayUtils.asUnmodifiableList(
            _MetaBridge.ID, _MetaBridge.CREATE_TIME, _MetaBridge.UPDATE_TIME, _MetaBridge.VERSION
    );


    public static boolean hasOptimistic(List<_Predicate> predicateList) {
        boolean match = false;
        for (_Predicate predicate : predicateList) {
            if (predicate.isOptimistic()) {
                match = true;
                break;
            }
        }
        return match;
    }


    public static void checkInsertExpField(final TableMeta<?> table, final FieldMeta<?> field
            , final _Expression value) {

        if (table instanceof ChildTableMeta) {
            final TableMeta<?> belongOf = field.tableMeta();
            if (belongOf != table && belongOf != ((ChildTableMeta<?>) table).parentMeta()) {
                throw _Exceptions.unknownColumn(null, field);
            }
        } else if (field.tableMeta() != table) {
            throw _Exceptions.unknownColumn(null, field);
        }
        if (!field.insertable()) {
            throw _Exceptions.nonInsertableField(field);
        }
        if (field == table.discriminator()) {
            throw _Exceptions.armyManageField(field);
        }
        if (!field.nullable() && value.isNullableValue()) {
            throw _Exceptions.nonNullField(field);
        }
        if (FORBID_INSERT_FIELDS.contains(field.fieldName())) {
            throw _Exceptions.armyManageField(field);
        }
        if (field.generator() != null) {
            throw _Exceptions.insertExpDontSupportField(field);
        }
    }


    static void appendStandardValueInsert(final StandardValueInsertContext context, final @Nullable FieldValueGenerator generator) {
        final _Dialect dialect = context.dialect();
        final ObjectAccessor accessor = context.domainAccessor();

        final _InsertBlock block;
        final _SqlContext blockContext;
        final TableMeta<?> domainTable;
        final BeanReadWrapper readWrapper;
        if (generator == null) {
            // child table;
            domainTable = null;
            readWrapper = null;
            block = context.childBlock();
            assert block != null;
            blockContext = block.getContext();
        } else {
            // parent table or simple table
            final _InsertBlock childBlock = context.childBlock();
            domainTable = childBlock == null ? context.table() : childBlock.table();
            readWrapper = new BeanReadWrapper(accessor);
            block = context;
            blockContext = context;
        }

        final StringBuilder builder = blockContext.sqlBuilder(); // use sql builder of blockContext
        final TableMeta<?> table = block.table();

        // 1. INSERT INTO clause
        builder.append(_Constant.INSERT_INTO)
                .append(_Constant.SPACE);
        //append table name
        dialect.safeObjectName(table.tableName(), builder);
        final List<FieldMeta<?>> fieldList = block.fieldLis();
        // 1.1 append table fields
        builder.append(_Constant.SPACE_LEFT_PAREN);
        int index = 0;
        for (FieldMeta<?> field : fieldList) {
            if (index > 0) {
                builder.append(_Constant.SPACE_COMMA);
            }
            builder.append(_Constant.SPACE);
            dialect.safeObjectName(field.columnName(), builder);
            index++;
        }
        builder.append(_Constant.SPACE_RIGHT_PAREN);

        // 2. values clause
        builder.append(_Constant.SPACE_VALUES);

        final List<IDomain> domainList = context.domainList();
        //2.1 get domainTable and discriminator
        final FieldMeta<?> discriminator = context.table().discriminator();

        int batch = 0;
        final Map<FieldMeta<?>, _Expression> expMap = context.commonExpMap();
        final boolean mockEnvironment = dialect instanceof _MockDialects;
        final NullHandleMode nullHandleMode = context.nullHandle();
        final boolean migration = context.migration();

        _Expression expression;
        GeneratorType generatorType;
        Object value;
        //2.2 append values
        for (IDomain domain : domainList) {
            if (generator != null) {
                //only non-child table
                readWrapper.domain = domain; // update domain value
                if (migration) {
                    generator.validate(domainTable, domain, accessor);
                } else {
                    generator.generate(domainTable, domain, accessor, readWrapper);
                }

            }
            if (batch > 0) {
                builder.append(_Constant.SPACE_COMMA);
            }
            builder.append(_Constant.SPACE_LEFT_PAREN);
            index = 0;
            for (FieldMeta<?> field : fieldList) {
                if (index > 0) {
                    builder.append(_Constant.SPACE_COMMA);
                }
                if (field == discriminator) {
                    assert field != null;
                    builder.append(_Constant.SPACE)
                            .append(context.discriminatorValue());
                } else if ((expression = expMap.get(field)) != null) { // common expression have validated
                    context.currentDomain = domain;
                    expression.appendSql(blockContext); // common append to block context
                } else if ((value = accessor.get(domain, field.fieldName())) != null) {
                    final MappingType mappingType = field.mappingType();
                    if (mappingType instanceof _ArmyNoInjectionMapping) {
                        builder.append(_Constant.SPACE)
                                .append(dialect.literal(field, value)); // for safe default mapping type, use literal to reduce '?' and add batch insert count.
                    } else {
                        blockContext.appendParam(ParamValue.build(field, value)); // parameter append block context
                    }
                } else if (field instanceof PrimaryFieldMeta
                        && table instanceof ChildTableMeta
                        && ((ChildTableMeta<?>) table).parentMeta().id().generatorType() == GeneratorType.POST) {
                    blockContext.appendParam(new DelayIdParamValue(field, domain, accessor)); // parameter append block context
                } else if (field.generatorType() == GeneratorType.PRECEDE) {
                    if (mockEnvironment) {
                        builder.append("mock:{generator}");
                    } else {
                        throw _Exceptions.generatorFieldIsNull(field);
                    }
                } else if (nullHandleMode == NullHandleMode.INSERT_DEFAULT) {
                    builder.append(_Constant.SPACE_DEFAULT);
                } else if (field.nullable()) {
                    builder.append(_Constant.SPACE_NULL);
                } else {
                    throw _Exceptions.nonNullField(field);
                }
                index++;
            }
            builder.append(_Constant.SPACE_RIGHT_PAREN);
            batch++;
        }

    }


    static List<FieldMeta<?>> mergeInsertFields(final boolean parent, final _ValuesInsert insert) {
        final TableMeta<?> table, relativeTable;
        final List<FieldMeta<?>> fieldList = insert.fieldList();
        if (parent) {
            relativeTable = insert.table();
            table = ((ChildTableMeta<?>) relativeTable).parentMeta();
        } else {
            table = insert.table();
            if (table instanceof ChildTableMeta) {
                relativeTable = ((ChildTableMeta<?>) table).parentMeta();
            } else {
                relativeTable = null;
            }
        }
        final List<FieldMeta<?>> mergeFieldList;
        if (fieldList.size() == 0) {
            final Collection<?> fieldCollection = table.fieldList();
            mergeFieldList = new ArrayList<>(fieldCollection.size());
            @SuppressWarnings("unchecked")
            Collection<FieldMeta<?>> tableFields = (Collection<FieldMeta<?>>) fieldCollection;
            for (FieldMeta<?> field : tableFields) {
                if (field.insertable()) {
                    mergeFieldList.add(field);
                }
            }
        } else {
            final Set<FieldMeta<?>> fieldSet = new HashSet<>();
            TableMeta<?> belongOf;
            for (FieldMeta<?> field : fieldList) {
                belongOf = field.tableMeta();
                if (belongOf == relativeTable) {
                    continue;
                }
                if (belongOf != table) {
                    throw _Exceptions.unknownColumn(null, field);
                }
                if (!field.insertable()) {
                    throw _Exceptions.nonInsertableField(field);
                }
                fieldSet.add(field);
            }
            appendInsertFields(table, fieldSet);
            mergeFieldList = new ArrayList<>(fieldSet);
        }
        return Collections.unmodifiableList(mergeFieldList);
    }


    /**
     * @return a unmodifiable List
     */
    static List<Selection> selectionList(SubQuery subQuery) {
        List<Selection> selectionList = new ArrayList<>();
        for (SelectItem selectItem : subQuery.selectItemList()) {
            if (selectItem instanceof SelectionGroup) {
                selectionList.addAll(((SelectionGroup) selectItem).selectionList());
            } else {
                selectionList.add((Selection) selectItem);
            }
        }
        return Collections.unmodifiableList(selectionList);
    }


    static void assertUpdateSetAndWhereClause(_Update update) {
        final List<? extends SetLeftItem> fieldList = update.leftItemList();
        if (_CollectionUtils.isEmpty(fieldList)) {
            throw new CriteriaException("update must have set clause.");
        }
        final List<? extends SetRightItem> valueExpList = update.rightItemList();
        if (fieldList.size() != valueExpList.size()) {
            String m;
            m = String.format("update set clause field list size[%s] and value expression list size[%s] not match."
                    , fieldList.size(), valueExpList.size());
            throw new CriteriaException(m);
        }
        if (_CollectionUtils.isEmpty(update.predicateList())) {
            throw new CriteriaException("update must have where clause.");
        }
    }


    static void appendInsertFields(final TableMeta<?> domainTable, final Set<FieldMeta<?>> fieldSet) {

        fieldSet.addAll(domainTable.fieldChain());
        final FieldMeta<?> idField;
        idField = domainTable.id();
        if (idField.insertable()) {
            fieldSet.add(idField);
        }
        if (domainTable instanceof ParentTableMeta) {
            fieldSet.add(((ParentTableMeta<?>) domainTable).discriminator());
        }
        if (!(domainTable instanceof ChildTableMeta)) {
            fieldSet.add(domainTable.getField(_MetaBridge.CREATE_TIME));

            if (domainTable.containField(_MetaBridge.UPDATE_TIME)) {
                fieldSet.add(domainTable.getField(_MetaBridge.UPDATE_TIME));
            }
            if (domainTable.containField(_MetaBridge.VERSION)) {
                fieldSet.add(domainTable.getField(_MetaBridge.VERSION));
            }
            if (domainTable.containField(_MetaBridge.VISIBLE)) {
                fieldSet.add(domainTable.getField(_MetaBridge.VISIBLE));
            }
        }


    }

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

    private static final class BeanReadWrapper implements ReadWrapper {

        private final ObjectAccessor accessor;

        private BeanReadWrapper(ObjectAccessor accessor) {
            this.accessor = accessor;
        }

        private IDomain domain;

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


}
