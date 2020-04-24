package io.army.dialect;

import io.army.ErrorCode;
import io.army.beans.BeanWrapper;
import io.army.beans.PropertyAccessorFactory;
import io.army.beans.ReadonlyWrapper;
import io.army.boot.FieldValuesGenerator;
import io.army.criteria.*;
import io.army.criteria.impl.SQLS;
import io.army.criteria.impl.inner.InnerStandardBatchInsert;
import io.army.criteria.impl.inner.InnerStandardDomainDML;
import io.army.domain.IDomain;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.IndexFieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.BeanUtils;

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

    static List<SQLWrapper> createDomainSQLWrapperList(SQLWrapper childSql, List<SQLWrapper> parentSqlList) {
        List<SQLWrapper> sqlWrapperList;
        if (parentSqlList.size() == 2) {
            final SQLWrapper queryChildSql = parentSqlList.get(0);
            if (!(queryChildSql instanceof BeanSQLWrapper)) {
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

    @SuppressWarnings("unchecked")
    static BeanSQLWrapper createQueryChildBeanSQLWrapper(InnerStandardDomainDML domainDML
            , List<FieldMeta<?, ?>> childFieldList, Dialect dialect, final Visible visible) {

        final ChildTableMeta<?> childMeta = (ChildTableMeta<?>) domainDML.tableMeta();
        final IndexFieldMeta<?, Object> primaryField = (IndexFieldMeta<?, Object>) childMeta.primaryKey();
        final Object primaryKeyValue = domainDML.primaryKeyValue();

        Assert.isInstanceOf(primaryField.javaType(), primaryKeyValue);

        final ParamExpression<Object> paramExp = SQLS.param(primaryKeyValue, primaryField.mappingType());

        Select select = SQLS.multiSelect()
                .select(childFieldList)
                .from(childMeta, "child")
                .where(primaryField.eq(paramExp))
                .asSelect();
        // parse query child table sql.
        List<SQLWrapper> sqlWrapperList = dialect.select(select, visible);

        Assert.isTrue(sqlWrapperList.size() == 1, "DomainUpdate query child sql error.");
        SQLWrapper sqlWrapper = sqlWrapperList.get(0);

        return BeanSQLWrapper.build(
                sqlWrapper.sql()
                , sqlWrapper.paramList()
                , PropertyAccessorFactory.forBeanPropertyAccess(
                        BeanUtils.instantiateClass(childMeta.javaType()))
        );
    }

    static void assertSetClauseField(FieldMeta<?, ?> fieldMeta) {
        if (!fieldMeta.updatable()) {
            throw new NonUpdateAbleException(ErrorCode.NON_UPDATABLE
                    , String.format("domain[%s] field[%s] is non-updatable."
                    , fieldMeta, fieldMeta.propertyName()));
        }
        if (TableMeta.VERSION.equals(fieldMeta.propertyName())
                || TableMeta.UPDATE_TIME.equals(fieldMeta.propertyName())) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "version or updateTime is managed by army.");
        }
    }

    static void createInsertForSimple(TableMeta<?> tableMeta, Collection<FieldMeta<?, ?>> fieldMetas
            , ReadonlyWrapper domainWrapper, InsertContext context) {

        StringBuilder fieldBuilder = context.fieldStringBuilder().append(Keywords.INSERT_INTO);
        StringBuilder valueBuilder = context.sqlBuilder()
                .append(" ")
                .append(Keywords.VALUE)
                .append(" (");

        final DQL dql = context.dql();
        final boolean defaultIfNull = context.defaultIfNull();
        // append table name
        context.appendTable(tableMeta);
        fieldBuilder.append("(");

        Object value;
        int count = 0;
        Expression<?> commonExp;
        for (FieldMeta<?, ?> fieldMeta : fieldMetas) {
            if (!fieldMeta.insertalbe()) {
                continue;
            }
            value = domainWrapper.getPropertyValue(fieldMeta.propertyName());

            if (value == null && !fieldMeta.nullable() && !defaultIfNull) {
                continue;
            }
            if (count > 0) {
                fieldBuilder.append(",");
                valueBuilder.append(",");
            }
            // field
            fieldBuilder.append(dql.quoteIfNeed(fieldMeta.fieldName()));
            // value
            commonExp = context.commonExp(fieldMeta);
            if (isConstant(fieldMeta)) {
                valueBuilder.append(createConstant(fieldMeta));
            } else if (commonExp != null) {
                if (fieldMeta.generator() != null) {
                    throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                            , "FieldMeta[%s] has Generator,can't specify common expression.", fieldMeta);
                }
                commonExp.appendSQL(context);
            } else if (value == null && defaultIfNull) {
                valueBuilder.append("DEFAULT");
            } else {
                valueBuilder.append("?");
                context.appendParam(ParamWrapper.build(fieldMeta.mappingType(), value));
            }
            count++;
        }

        fieldBuilder.append(" )");
        valueBuilder.append(" )");

    }

    static void createBatchInsertForSimple(TableMeta<?> tableMeta, InsertContext context) {

        StringBuilder fieldBuilder = context.fieldStringBuilder().append("INSERT INTO ");
        StringBuilder valueBuilder = context.sqlBuilder().append(" VALUE ( ");
        context.appendTable(tableMeta);
        fieldBuilder.append(" ( ");

        final SQL sql = context.dql();
        final boolean ignoreGeneratorIfCash = ((InnerStandardBatchInsert) context.innerInsert())
                .ignoreGenerateValueIfCrash();
        int index = 0;
        Expression<?> commonExp;
        for (FieldMeta<?, ?> fieldMeta : tableMeta.fieldCollection()) {
            if (index > 0) {
                fieldBuilder.append(",");
            }
            // field
            fieldBuilder.append(sql.quoteIfNeed(fieldMeta.fieldName()));
            // value or placeholder
            commonExp = context.commonExp(fieldMeta);
            if (!fieldMeta.insertalbe()) {
                valueBuilder.append("DEFAULT");
            } else if (isConstant(fieldMeta)) {
                valueBuilder.append(createConstant(fieldMeta));
            } else if (commonExp != null && (ignoreGeneratorIfCash || fieldMeta.generator() == null)) {
                commonExp.appendSQL(context);
            } else {
                valueBuilder.append("?");
                context.appendParam(FieldParamWrapper.build(fieldMeta));
            }
            index++;
        }
        fieldBuilder.append(" )");
        valueBuilder.append(" )");
    }

    static List<BatchSQLWrapper> createBatchInsertWrapper(InnerStandardBatchInsert insert
            , List<SQLWrapper> sqlWrapperList, final FieldValuesGenerator valuesGenerator) {
        if (sqlWrapperList.size() < 3) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "sqlWrapperList size must less than 3.");
        }
        List<FieldMeta<?, ?>> fieldList = insert.fieldList();
        List<IDomain> domainList = insert.valueList();
        final TableMeta<?> tableMeta = insert.tableMeta();

        BeanWrapper beanWrapper;

        List<BatchSQLWrapper> batchWrapperList = new ArrayList<>(domainList.size() * sqlWrapperList.size());
        Map<IDomain, BeanWrapper> beanWrapperMap = new HashMap<>();
        for (SQLWrapper sqlWrapper : sqlWrapperList) {
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
                        FieldParamWrapperImpl wrapper = (FieldParamWrapperImpl) paramWrapper;
                        paramWrapperList.add(
                                ParamWrapper.build(
                                        wrapper.fieldMeta().mappingType()
                                        , beanWrapper.getPropertyValue(wrapper.fieldMeta().propertyName())
                                )
                        );
                    } else {
                        paramWrapperList.add(paramWrapper);
                    }
                }
                paramGroupList.add(paramWrapperList);
            }
            batchWrapperList.add(
                    BatchSQLWrapper.build(
                            sqlWrapper.sql()
                            , Collections.unmodifiableList(paramGroupList)
                    )
            );
        }
        return batchWrapperList;
    }


    private static SQLWrapper createSQLWrapper(InsertContext context) {
        return SQLWrapper.build(
                context.fieldStringBuilder().toString() + context.sqlBuilder().toString()
                , context.paramList()
        );
    }


    private static BeanSQLWrapper createSQLWrapper(InsertContext context, BeanWrapper beanWrapper) {
        return BeanSQLWrapper.build(
                context.fieldStringBuilder().toString() + context.sqlBuilder().toString()
                , context.paramList()
                , beanWrapper
        );
    }

    static boolean isConstant(FieldMeta<?, ?> fieldMeta) {
        return TableMeta.VERSION.equals(fieldMeta.propertyName())
                || fieldMeta == fieldMeta.tableMeta().discriminator()
                ;
    }

    static Object createConstant(FieldMeta<?, ?> fieldMeta) {
        Object value;
        if (TableMeta.VERSION.equals(fieldMeta.propertyName())) {
            value = 0;
        } else if (fieldMeta == fieldMeta.tableMeta().discriminator()) {
            value = fieldMeta.tableMeta().discriminatorValue();
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

}
