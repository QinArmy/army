package io.army.dialect;

import io.army.beans.ReadWrapper;
import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Update;
import io.army.criteria.impl.inner._ValuesInsert;
import io.army.generator.PostFieldGenerator;
import io.army.generator.PreFieldGenerator;
import io.army.mapping.MappingType;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.stmt.ParamValue;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmt;
import io.army.stmt.Stmts;
import io.army.util.ArrayUtils;
import io.army.util.CollectionUtils;
import io.army.util._Exceptions;

import java.util.*;

public abstract class _DmlUtils {

    _DmlUtils() {
        throw new UnsupportedOperationException();
    }


    static final Collection<String> FORBID_INSERT_FIELDS = ArrayUtils.asUnmodifiableList(
            _MetaBridge.ID, _MetaBridge.CREATE_TIME, _MetaBridge.UPDATE_TIME, _MetaBridge.VERSION
    );


    public static void checkInsertExpField(final TableMeta<?> table, final FieldMeta<?, ?> field
            , final _Expression<?> value) {

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
        if (!field.nullable() && value.nullableExp()) {
            throw _Exceptions.nonNullField(field);
        }
        if (FORBID_INSERT_FIELDS.contains(field.fieldName())) {
            throw _Exceptions.armyManageField(field);
        }
        if (field.generator() != null) {
            throw _Exceptions.insertExpDontSupportField(field);
        }
    }


    static void appendStandardValueInsert(final boolean childBlock, final _ValueInsertContext context) {
        final _Dialect dialect = context.dialect();
        final _InsertBlock block;
        final _SqlContext blockContext;
        if (childBlock) {
            block = context.childBlock();
            assert block != null;
            blockContext = block.getContext();
        } else {
            block = context;
            blockContext = context;
        }

        final StringBuilder builder = blockContext.sqlBuilder(); // use sql builder of blockContext
        final TableMeta<?> table = block.table();

        // 1. INSERT INTO clause
        builder.append(Constant.INSERT_INTO)
                .append(Constant.SPACE);
        //append table name
        builder.append(dialect.safeTableName(table.tableName()));
        final List<FieldMeta<?, ?>> fieldList = block.fieldLis();
        // 1.1 append table fields
        builder.append(Constant.SPACE_LEFT_BRACKET);
        int index = 0;
        for (FieldMeta<?, ?> field : fieldList) {
            if (index > 0) {
                builder.append(Constant.SPACE_COMMA);
            }
            builder.append(Constant.SPACE)
                    .append(dialect.safeColumnName(field.columnName()));
            index++;
        }
        builder.append(Constant.SPACE_RIGHT_BRACKET);

        // 2. values clause
        builder.append(Constant.SPACE_VALUES);

        final List<? extends ReadWrapper> domainList = context.domainList();
        //2.1 get domainTable and discriminator
        final FieldMeta<?, ?> discriminator = context.table().discriminator();

        int batch = 0;
        final Map<FieldMeta<?, ?>, _Expression<?>> expMap = context.commonExpMap();
        final boolean mockEnvironment = dialect instanceof _MockDialects;
        _Expression<?> expression;
        GeneratorMeta generatorMeta;
        Object value;
        //2.2 append values
        for (ReadWrapper domain : domainList) {
            if (batch > 0) {
                builder.append(Constant.SPACE_COMMA);
            }
            builder.append(Constant.SPACE_LEFT_BRACKET);
            index = 0;
            for (FieldMeta<?, ?> field : fieldList) {
                if (index > 0) {
                    builder.append(Constant.SPACE_COMMA);
                }
                if (field == discriminator) {
                    assert field != null;
                    builder.append(Constant.SPACE)
                            .append(context.discriminatorValue());
                } else if ((expression = expMap.get(field)) != null) {
                    expression.appendSql(blockContext); // common append to block context
                } else if ((value = domain.get(field.fieldName())) != null) {
                    final MappingType mappingType = field.mappingType();
                    if (mappingType instanceof _ArmyNoInjectionMapping) {
                        builder.append(Constant.SPACE)
                                .append(dialect.literal(field, value)); // for safe default mapping type, use literal to reduce '?' and add batch insert count.
                    } else {
                        blockContext.appendParam(ParamValue.build(field, value)); // parameter append block context
                    }
                } else if (field.nullable()) {
                    builder.append(Constant.SPACE_NULL);
                } else if ((generatorMeta = field.generator()) == null) {
                    throw _Exceptions.nonNullField(field);
                } else if (PreFieldGenerator.class.isAssignableFrom(generatorMeta.type())) {
                    if (mockEnvironment) {
                        builder.append(Constant.SPACE_NULL);
                    } else {
                        throw _Exceptions.nonNullField(field);
                    }
                } else if (!PostFieldGenerator.class.isAssignableFrom(generatorMeta.type())) {
                    throw _Exceptions.nonNullField(field);
                }
                index++;
            }
            builder.append(Constant.SPACE_RIGHT_BRACKET);
            batch++;
        }

    }


    static Stmt createBatchStmt(final SimpleStmt simpleStmt, final List<ReadWrapper> wrapperList) {
        final List<ParamValue> paramGroup = simpleStmt.paramGroup();
        final int paramSize = paramGroup.size();
        final List<List<ParamValue>> groupList = new ArrayList<>(wrapperList.size());

        for (ReadWrapper wrapper : wrapperList) {
            final List<ParamValue> group = new ArrayList<>(paramSize);

            for (ParamValue param : paramGroup) {
                if (!(param instanceof NamedParam)) {
                    group.add(param);
                    continue;
                }
                final Object value = wrapper.get(((NamedParam<?>) param).name());
                if (value == null && param instanceof NonNullNamedParam) {
                    throw _Exceptions.nonNullNamedParam((NonNullNamedParam<?>) param);
                }
                group.add(ParamValue.build(param.paramMeta(), value));
            }

            groupList.add(group);

        }
        return Stmts.batch(simpleStmt, groupList);
    }


    static List<FieldMeta<?, ?>> mergeInsertFields(final boolean parent, final _ValuesInsert insert) {
        final TableMeta<?> table, relativeTable;
        final List<FieldMeta<?, ?>> fieldList = insert.fieldList();
        final ParentTableMeta<?> parentTable;
        if (parent) {
            relativeTable = insert.table();
            parentTable = ((ChildTableMeta<?>) relativeTable).parentMeta();
            table = parentTable;
        } else {
            table = insert.table();
            if (table instanceof ChildTableMeta) {
                relativeTable = ((ChildTableMeta<?>) table).parentMeta();
            } else {
                relativeTable = null;
            }
        }
        final List<FieldMeta<?, ?>> mergeFieldList;
        if (fieldList.isEmpty()) {
            final Collection<?> fieldCollection = table.fields();
            mergeFieldList = new ArrayList<>(fieldCollection.size());
            @SuppressWarnings("unchecked")
            Collection<FieldMeta<?, ?>> tableFields = (Collection<FieldMeta<?, ?>>) fieldCollection;
            for (FieldMeta<?, ?> fieldMeta : tableFields) {
                if (fieldMeta.insertable()) {
                    mergeFieldList.add(fieldMeta);
                }
            }
        } else {
            final Set<FieldMeta<?, ?>> fieldSet = new HashSet<>();
            TableMeta<?> belongOf;
            for (FieldMeta<?, ?> field : fieldList) {
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
        for (SelectPart selectPart : subQuery.selectPartList()) {
            if (selectPart instanceof SelectionGroup) {
                selectionList.addAll(((SelectionGroup) selectPart).selectionList());
            } else {
                selectionList.add((Selection) selectPart);
            }
        }
        return Collections.unmodifiableList(selectionList);
    }


    static void assertUpdateSetAndWhereClause(_Update update) {
        final List<? extends SetTargetPart> fieldList = update.fieldList();
        if (CollectionUtils.isEmpty(fieldList)) {
            throw new CriteriaException("update must have set clause.");
        }
        final List<? extends SetValuePart> valueExpList = update.valueExpList();
        if (fieldList.size() != valueExpList.size()) {
            String m;
            m = String.format("update set clause field list size[%s] and value expression list size[%s] not match."
                    , fieldList.size(), valueExpList.size());
            throw new CriteriaException(m);
        }
        if (CollectionUtils.isEmpty(update.predicateList())) {
            throw new CriteriaException("update must have where clause.");
        }
    }


    static void appendInsertFields(final TableMeta<?> domainTable, final Set<FieldMeta<?, ?>> fieldSet) {

        fieldSet.addAll(domainTable.generatorChain());

        fieldSet.add(domainTable.id());
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


}
