package io.army.dialect;

import io.army.ErrorCode;
import io.army.GenericSessionFactory;
import io.army.beans.ObjectWrapper;
import io.army.beans.PropertyAccessorFactory;
import io.army.beans.ReadonlyWrapper;
import io.army.boot.FieldValuesGenerator;
import io.army.codec.FieldCodec;
import io.army.criteria.*;
import io.army.criteria.impl.SQLS;
import io.army.criteria.impl.inner.InnerStandardBatchInsert;
import io.army.criteria.impl.inner.InnerStandardDomainDML;
import io.army.domain.IDomain;
import io.army.generator.FieldGenerator;
import io.army.generator.PreFieldGenerator;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.IndexFieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.BeanUtils;
import io.army.wrapper.*;

import java.util.*;

abstract class DMLUtils {

    DMLUtils() {
        throw new UnsupportedOperationException();
    }

    static int selectionCount(SubQuery subQuery) {
        int count = 0;
        for (SelectPart selectPart : subQuery.selectPartList()) {
            if (selectPart instanceof SelectionGroup) {
                count += ((SelectionGroup) selectPart).selectionList().size();
            } else {
                count++;
            }
        }
        return count;
    }

    static List<SimpleSQLWrapper> mergeDomainSQLWrappers(SimpleSQLWrapper childSql, List<SimpleSQLWrapper> parentSqlList) {
        List<SimpleSQLWrapper> sqlWrapperList;
        if (parentSqlList.size() == 2) {
            final SimpleSQLWrapper queryChildSql = parentSqlList.get(0);
            if (!(queryChildSql instanceof DomainSQLWrapper)) {
                throw DialectUtils.createArmyCriteriaException();
            }
            sqlWrapperList = new ArrayList<>(3);
            // 1. query child table sql.
            sqlWrapperList.add(queryChildSql);
            // 2. update child table sql.
            sqlWrapperList.add(childSql);
            // 3. update parent table sql.
            sqlWrapperList.add(parentSqlList.get(1));

        } else if (parentSqlList.size() == 1) {
            sqlWrapperList = new ArrayList<>(2);
            // 1. update child table sql.
            sqlWrapperList.add(childSql);
            // 2. update parent table sql.
            sqlWrapperList.add(parentSqlList.get(0));
        } else {
            throw DialectUtils.createArmyCriteriaException();
        }
        return sqlWrapperList;
    }

    static void assertSingleUpdateSetClauseField(FieldMeta<?, ?> fieldMeta, TableMeta<?> tableMeta) {
        if (fieldMeta.tableMeta() == tableMeta) {
            throw new IllegalArgumentException(String.format(
                    "FieldMeta[%s] don't belong to TableMeta[%s]", fieldMeta, tableMeta));
        }

        assertSetClauseField(fieldMeta);
    }

    static DomainSQLWrapper createQueryChildBeanSQLWrapper(InnerStandardDomainDML domainDML
            , List<FieldMeta<?, ?>> childFieldList, Dialect dialect, final Visible visible) {

        final ChildTableMeta<?> childMeta = (ChildTableMeta<?>) domainDML.tableMeta();
        final IndexFieldMeta<?, Object> primaryField = childMeta.primaryKey();
        final Object primaryKeyValue = domainDML.primaryKeyValue();

        Assert.isInstanceOf(primaryField.javaType(), primaryKeyValue);

        final ParamExpression<Object> paramExp = SQLS.param(primaryKeyValue, primaryField.mappingType());

        Select select = SQLS.multiSelect()
                .select(childFieldList)
                .from(childMeta, "child")
                .where(primaryField.eq(paramExp))
                .asSelect();
        // parse query child table sql.
        List<SelectSQLWrapper> sqlWrapperList = dialect.select(select, visible);

        Assert.isTrue(sqlWrapperList.size() == 1, "DomainUpdate query child sql error.");
        SimpleSQLWrapper sqlWrapper = sqlWrapperList.get(0);

        return DomainSQLWrapper.build(
                sqlWrapper.sql()
                , sqlWrapper.paramList()
                , PropertyAccessorFactory.forDomainPropertyAccess(
                        BeanUtils.instantiateClass(childMeta.javaType()))
        );
    }

    static void assertSetClauseField(FieldMeta<?, ?> fieldMeta) {
        if (!fieldMeta.updatable() || fieldMeta == fieldMeta.tableMeta().discriminator()) {
            throw new NonUpdateAbleException(ErrorCode.NON_UPDATABLE
                    , String.format("domain[%s] field[%s] is non-updatable."
                    , fieldMeta.tableMeta().javaType(), fieldMeta.propertyName()));
        }
        if (TableMeta.VERSION.equals(fieldMeta.propertyName())
                || TableMeta.UPDATE_TIME.equals(fieldMeta.propertyName())) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "version or updateTime is managed by army.");
        }
    }

    /**
     * @return a unmodifiable set
     */
    static Set<FieldMeta<?, ?>> mergeInsertFields(TableMeta<?> tableMeta, Dialect dialect
            , Collection<FieldMeta<?, ?>> targetFields) {


        Set<FieldMeta<?, ?>> fieldMetaSet = new HashSet<>(targetFields);

        appendGeneratorFields(fieldMetaSet, tableMeta, dialect.sessionFactory());
        if (tableMeta instanceof ChildTableMeta) {
            ChildTableMeta<?> childMeta = (ChildTableMeta<?>) tableMeta;
            appendGeneratorFields(fieldMetaSet, childMeta.parentMeta(), dialect.sessionFactory());
        }

        FieldMeta<?, ?> discriminator = tableMeta.discriminator();
        if (discriminator != null && discriminator.tableMeta() == tableMeta) {
            fieldMetaSet.add(discriminator);
        }
        if (tableMeta.isMappingProp(TableMeta.CREATE_TIME)) {
            fieldMetaSet.add(tableMeta.getField(TableMeta.CREATE_TIME));
        }
        if (tableMeta.isMappingProp(TableMeta.UPDATE_TIME)) {
            fieldMetaSet.add(tableMeta.getField(TableMeta.UPDATE_TIME));
        }
        if (tableMeta.isMappingProp(TableMeta.VERSION)) {
            fieldMetaSet.add(tableMeta.getField(TableMeta.VERSION));
        }
        return Collections.unmodifiableSet(fieldMetaSet);
    }

    static void createStandardInsertForSimple(TableMeta<?> physicalTable, TableMeta<?> logicalTable
            , Collection<FieldMeta<?, ?>> fieldMetas, ReadonlyWrapper domainWrapper
            , AbstractStandardInsertContext context) {

        final GenericSessionFactory sessionFactory = context.dialect.sessionFactory();
        context.currentClause(Clause.INSERT_INTO);
        // append table name
        context.appendTable(physicalTable);
        StringBuilder fieldBuilder = context.fieldsBuilder().append(" (");

        StringBuilder valueBuilder = context.sqlBuilder();
        context.currentClause(Clause.VALUE);
        valueBuilder.append(" (");
        final SQL sql = context.dql();

        Object value;
        int count = 0;
        for (FieldMeta<?, ?> fieldMeta : fieldMetas) {
            if (!fieldMeta.insertalbe()) {
                continue;
            }
            value = domainWrapper.getPropertyValue(fieldMeta.propertyName());

            if (count > 0) {
                fieldBuilder.append(",");
                valueBuilder.append(",");
            }
            // field
            fieldBuilder.append(sql.quoteIfNeed(fieldMeta.fieldName()));

            if (isConstant(fieldMeta)) {
                valueBuilder.append(createConstant(fieldMeta, logicalTable));
            } else {
                valueBuilder.append("?");
                FieldCodec fieldCodec = sessionFactory.fieldCodec(fieldMeta);
                if (fieldCodec != null) {
                    context.appendParam(ParamWrapper.build(fieldMeta, value));
                } else {
                    context.appendParam(ParamWrapper.build(fieldMeta.mappingType(), value));
                }

            }
            count++;
        }

        fieldBuilder.append(" )");
        valueBuilder.append(" )");

    }


    static void createBatchInsertForSimple(TableMeta<?> tableMeta, Collection<FieldMeta<?, ?>> fieldMetas
            , InsertContext context) {

        StringBuilder fieldBuilder = context.fieldsBuilder();
        context.currentClause(Clause.INSERT_INTO);
        // append table name
        context.appendTable(tableMeta);
        fieldBuilder.append(" ( ");

        /// VALUE clause
        context.currentClause(Clause.VALUE);
        StringBuilder valueBuilder = context.sqlBuilder()
                .append(" ( ");

        final SQL sql = context.dql();
        int index = 0;
        for (FieldMeta<?, ?> fieldMeta : fieldMetas) {
            if (!fieldMeta.insertalbe()) {
                continue;
            }
            if (index > 0) {
                fieldBuilder.append(",");
            }
            // field
            fieldBuilder.append(sql.quoteIfNeed(fieldMeta.fieldName()));

            if (isConstant(fieldMeta)) {
                valueBuilder.append(createConstant(fieldMeta, tableMeta));
            } else {
                valueBuilder.append("?");
                context.appendParam(FieldParamWrapper.build(fieldMeta));
            }
            index++;
        }
        fieldBuilder.append(" )");
        valueBuilder.append(" )");
    }

    static List<SimpleBatchSQLWrapper> createBatchInsertWrapper(InnerStandardBatchInsert insert
            , List<SimpleSQLWrapper> sqlWrapperList, final FieldValuesGenerator valuesGenerator) {
        if (sqlWrapperList.size() < 3) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "sqlWrapperList size must less than 3.");
        }
        List<FieldMeta<?, ?>> fieldList = insert.fieldList();
        List<IDomain> domainList = insert.valueList();
        final TableMeta<?> tableMeta = insert.tableMeta();

        ObjectWrapper beanWrapper;

        List<SimpleBatchSQLWrapper> batchWrapperList = new ArrayList<>(domainList.size() * sqlWrapperList.size());
        Map<IDomain, ObjectWrapper> beanWrapperMap = new HashMap<>();
        for (SimpleSQLWrapper sqlWrapper : sqlWrapperList) {
            List<List<ParamWrapper>> paramGroupList = new ArrayList<>(fieldList.size());

            for (IDomain domain : domainList) {

                beanWrapper = beanWrapperMap.computeIfAbsent(domain
                        // create required value
                        , key -> valuesGenerator.createValues(tableMeta, key));

                Assert.state(beanWrapper.getWrappedInstance() == domain
                        , () -> String.format("IDomain[%s] hasCode() and equals() implementation error.", domain));

                List<ParamWrapper> paramWrapperList = new ArrayList<>(sqlWrapper.paramList().size());
                for (ParamWrapper paramWrapper : sqlWrapper.paramList()) {
                    if (paramWrapper instanceof FieldParamWrapper) {
                        FieldParamWrapper wrapper = (FieldParamWrapper) paramWrapper;
                        paramWrapperList.add(
                                ParamWrapper.build(
                                        wrapper.paramMeta().mappingType()
                                        , beanWrapper.getPropertyValue(wrapper.paramMeta().propertyName())
                                )
                        );
                    } else {
                        paramWrapperList.add(paramWrapper);
                    }
                }
                paramGroupList.add(paramWrapperList);
            }
            batchWrapperList.add(
                    SimpleBatchSQLWrapper.build(
                            sqlWrapper.sql()
                            , Collections.unmodifiableList(paramGroupList)
                            , tableMeta
                    )
            );
        }
        return batchWrapperList;
    }

    static boolean isConstant(FieldMeta<?, ?> fieldMeta) {
        return TableMeta.VERSION.equals(fieldMeta.propertyName())
                || fieldMeta == fieldMeta.tableMeta().discriminator()
                ;
    }

    static Object createConstant(FieldMeta<?, ?> fieldMeta, TableMeta<?> logicalTable) {
        Object value;
        if (TableMeta.VERSION.equals(fieldMeta.propertyName())) {
            value = 0;
        } else if (fieldMeta == logicalTable.discriminator()) {
            value = logicalTable.discriminatorValue();
        } else {
            throw new IllegalArgumentException(String.format("Entity[%s] prop[%s] cannot create constant value"
                    , fieldMeta.tableMeta().javaType().getName()
                    , fieldMeta.propertyName()));
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    static List<IPredicate> mergeDomainUpdatePredicateList(List<IPredicate> originalPredicateList
            , IndexFieldMeta<?, ?> primaryField, Object primaryKeyValue) {

        List<IPredicate> mergedPredicateList = new ArrayList<>(originalPredicateList.size() + 1);
        final IndexFieldMeta<?, Object> primaryKey = (IndexFieldMeta<?, Object>) primaryField;
        Assert.isInstanceOf(primaryKey.javaType(), primaryKeyValue);

        // firstly ,add predicate sql fragment 'id = ?'
        mergedPredicateList.add(
                primaryKey.eq(
                        SQLS.param(primaryKeyValue, primaryKey.mappingType())
                )
        );
        // secondly, add update.predicateList()
        mergedPredicateList.addAll(originalPredicateList);

        return mergedPredicateList;
    }


    /*################################## blow private method ##################################*/

    private static void appendGeneratorFields(Set<FieldMeta<?, ?>> fieldMetaSet, TableMeta<?> tableMeta
            , GenericSessionFactory factory) {

        List<FieldMeta<?, ?>> chain = factory.generatorChain(tableMeta);
        FieldGenerator fieldGenerator;
        for (FieldMeta<?, ?> fieldMeta : chain) {
            fieldGenerator = factory.fieldGenerator(fieldMeta);
            if (fieldGenerator instanceof PreFieldGenerator) {
                fieldMetaSet.add(fieldMeta);
            }

        }

    }
}
