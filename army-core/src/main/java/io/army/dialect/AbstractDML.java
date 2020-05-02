package io.army.dialect;

import io.army.ErrorCode;
import io.army.ShardingMode;
import io.army.beans.BeanWrapper;
import io.army.beans.ReadonlyWrapper;
import io.army.boot.FieldValuesGenerator;
import io.army.criteria.*;
import io.army.criteria.impl.CriteriaCounselor;
import io.army.criteria.impl.SQLS;
import io.army.criteria.impl.inner.*;
import io.army.domain.IDomain;
import io.army.generator.PostFieldGenerator;
import io.army.meta.*;
import io.army.util.Assert;
import io.army.util.ClassUtils;
import io.army.util.CollectionUtils;
import io.army.wrapper.BatchSQLWrapper;
import io.army.wrapper.SQLWrapper;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;

//@SuppressWarnings("unused")
public abstract class AbstractDML extends AbstractDMLAndDQL implements DML {

    public AbstractDML(Dialect dialect) {
        super(dialect);
    }

    /*################################## blow DML batchInsert method ##################################*/

    @Override
    public final List<SQLWrapper> insert(Insert insert, final Visible visible) {
        Assert.isTrue(insert.prepared(), "Insert don't invoke asInsert() method.");

        List<SQLWrapper> list;
        if (insert instanceof InnerStandardInsert) {
            InnerStandardInsert standardInsert = (InnerStandardInsert) insert;
            CriteriaCounselor.assertStandardInsert(standardInsert);
            list = standardInsert(standardInsert, visible);

        } else if (insert instanceof InnerStandardSubQueryInsert) {
            InnerStandardSubQueryInsert subQueryInsert = (InnerStandardSubQueryInsert) insert;
            CriteriaCounselor.assertStandardSubQueryInsert(subQueryInsert);
            list = standardSubQueryInsert(subQueryInsert, visible);

        } else if (insert instanceof InnerSpecialGeneralInsert) {
            InnerSpecialGeneralInsert generalInsert = (InnerSpecialGeneralInsert) insert;
            assertSpecialGeneralInsert(generalInsert);
            list = specialGeneralInsert(generalInsert, visible);

        } else if (insert instanceof InnerSpecialSubQueryInsert) {
            InnerSpecialSubQueryInsert subQueryInsert = (InnerSpecialSubQueryInsert) insert;
            assertSpecialSubQueryInsert(subQueryInsert);
            list = specialSubQueryInsert(subQueryInsert, visible);
        } else {
            throw new IllegalArgumentException(String.format("Insert[%s] type unknown.", insert.getClass().getName()));
        }
        return Collections.unmodifiableList(list);
    }

    @Override
    public final List<BatchSQLWrapper> batchInsert(Insert insert, final Visible visible) {
        Assert.state(this.dialect.sessionFactory().shardingMode() == ShardingMode.NO_SHARDING
                , "not support batchInsert without NO_SHARDING");

        Assert.isTrue(insert.prepared(), "Insert don't invoke asInsert() method.");

        List<BatchSQLWrapper> list;
        if (insert instanceof InnerStandardBatchInsert) {
            InnerStandardBatchInsert batchInsert = (InnerStandardBatchInsert) insert;
            CriteriaCounselor.assertStandardBatchInsert(batchInsert);
            list = standardBatchInsert(batchInsert, visible);

        } else if (insert instanceof InnerSpecialBatchInsert) {
            InnerSpecialBatchInsert batchInsert = (InnerSpecialBatchInsert) insert;
            assertSpecialBatchInsert(batchInsert);
            list = specialBatchInsert(batchInsert, visible);

        } else {
            throw new IllegalArgumentException(String.format("Insert[%s] type unknown.", insert));
        }
        return Collections.unmodifiableList(list);
    }

    /*################################## blow update method ##################################*/

    @Override
    public final List<SQLWrapper> update(Update update, final Visible visible) {
        Assert.isTrue(update.prepared(), "Update don't invoke asUpdate() method.");

        List<SQLWrapper> list;
        if (update instanceof InnerStandardDomainUpdate) {
            InnerStandardDomainUpdate domainUpdate = (InnerStandardDomainUpdate) update;
            CriteriaCounselor.assertStandardDomainUpdate(domainUpdate);
            list = Collections.unmodifiableList(
                    standardDomainUpdate(domainUpdate, visible)
            );

        } else if (update instanceof InnerSpecialDomainUpdate) {
            InnerSpecialDomainUpdate domainUpdate = (InnerSpecialDomainUpdate) update;
            assertSpecialDomainUpdate(domainUpdate);
            list = Collections.unmodifiableList(
                    specialDomainUpdate(domainUpdate, visible)
            );

        } else if (update instanceof InnerStandardUpdate) {
            InnerStandardUpdate standardUpdate = (InnerStandardUpdate) update;
            CriteriaCounselor.assertStandardUpdate(standardUpdate);
            list = Collections.singletonList(
                    standardSimpleUpdate(standardUpdate, standardUpdate.tableMeta()
                            , standardUpdate.tableAlias(), visible)
            );

        } else if (update instanceof InnerSpecialUpdate) {
            InnerSpecialUpdate specialUpdate = (InnerSpecialUpdate) update;
            assertSpecialUpdate(specialUpdate);
            list = Collections.unmodifiableList(
                    specialUpdate(specialUpdate, visible)
            );

        } else {
            throw new IllegalArgumentException(String.format("Update[%s] type unknown.", update));
        }
        return list;
    }

    @Override
    public final List<SQLWrapper> delete(Delete delete, final Visible visible) {
        Assert.isTrue(delete.prepared(), "Delete don't invoke asDelete() method.");

        List<SQLWrapper> list;
        if (delete instanceof InnerStandardDomainDelete) {
            InnerStandardDomainDelete domainDelete = (InnerStandardDomainDelete) delete;
            CriteriaCounselor.assertStandardDomainDelete(domainDelete);
            list = standardDomainDeleteDispatcher(domainDelete, visible);

        } else if (delete instanceof InnerSpecialDomainDelete) {
            InnerSpecialDomainDelete domainDelete = (InnerSpecialDomainDelete) delete;
            assertSpecialDomainDelete(domainDelete);
            list = Collections.unmodifiableList(
                    specialDomainDeleteDispatcher(domainDelete, visible)
            );

        } else if (delete instanceof InnerStandardDelete) {
            InnerStandardDelete standardDelete = (InnerStandardDelete) delete;
            CriteriaCounselor.assertStandardDelete(standardDelete);
            list = Collections.singletonList(
                    standardSingleDelete(standardDelete, standardDelete.tableMeta()
                            , standardDelete.tableAlias(), visible)
            );

        } else if (delete instanceof InnerSpecialDelete) {
            InnerSpecialDelete specialDelete = (InnerSpecialDelete) delete;
            assertSpecialDelete(specialDelete);
            list = Collections.unmodifiableList(
                    specialDelete(specialDelete, visible)
            );

        } else {
            throw new IllegalArgumentException(String.format("Delete[%s] type unknown.", delete));
        }
        return Collections.unmodifiableList(list);
    }

    /*################################## blow protected template method ##################################*/

    protected void tableOnlyModifier(SQLContext context) {

    }

    protected abstract boolean singleDeleteHasTableAlias();

    /*################################## blow insert template method ##################################*/

    protected void assertSpecialGeneralInsert(InnerSpecialGeneralInsert insert) {
        throw new UnsupportedOperationException(String.format("dialect [%s] not support special general insert."
                , sqlDialect())
        );
    }

    protected void assertSpecialBatchInsert(InnerSpecialBatchInsert insert) {
        throw new UnsupportedOperationException(String.format("dialect [%s] not support special abstract insert."
                , sqlDialect())
        );
    }

    protected void assertSpecialSubQueryInsert(InnerSpecialSubQueryInsert insert) {
        throw new UnsupportedOperationException(String.format("dialect [%s] not support special sub query insert."
                , sqlDialect())
        );
    }

    protected List<SQLWrapper> specialGeneralInsert(InnerSpecialGeneralInsert insert, Visible visible) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special general insert."
                , sqlDialect())
        );
    }

    protected List<BatchSQLWrapper> specialBatchInsert(InnerSpecialBatchInsert insert, Visible visible) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special batch insert."
                , sqlDialect())
        );
    }

    protected List<SQLWrapper> specialSubQueryInsert(InnerSpecialSubQueryInsert insert, Visible visible) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special sub query insert."
                , sqlDialect())
        );
    }

    protected InsertContext createBeanInsertContext(InnerInsert insert, ReadonlyWrapper readonlyWrapper
            , final Visible visible) {
        InsertContext context;
        if (insert instanceof InnerStandardInsert) {
            context = AbstractStandardInsertContext.buildGeneral(this.dialect, visible, readonlyWrapper
                    , (InnerStandardInsert) insert);
        } else {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "unknown InnerInsert[%s] type.", insert);
        }
        return context;
    }

    protected InsertContext createBatchInsertContext(InnerBatchInsert insert, Visible visible) {
        return AbstractStandardInsertContext.buildBatch(this.dialect, visible, (InnerStandardBatchInsert) insert);
    }

    protected InsertContext createSubQueryInsertContext(InnerSubQueryInsert insert, Visible visible) {
        throw new UnsupportedOperationException();
    }

    /*################################## blow update template method ##################################*/

    protected void assertSpecialDomainUpdate(InnerSpecialDomainUpdate domainUpdate) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special domain update."
                , sqlDialect())
        );
    }

    protected void assertSpecialUpdate(InnerSpecialUpdate update) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special domain update."
                , sqlDialect())
        );
    }

    protected List<SQLWrapper> specialDomainUpdate(InnerSpecialDomainUpdate domainUpdate, Visible visible) {
        throw new UnsupportedOperationException(String.format("dialect [%s] not support special domain update."
                , sqlDialect())
        );
    }

    protected List<SQLWrapper> specialUpdate(InnerSpecialUpdate update, Visible visible) {
        throw new UnsupportedOperationException(String.format("dialect [%s] not support special update."
                , sqlDialect())
        );
    }

    protected UpdateContext createUpdateContext(InnerUpdate update, final Visible visible) {
        return new StandardUpdateContext(this.dialect, visible, (InnerStandardUpdate) update);
    }

    protected ChildDomainUpdateContext createChildDomainUpdateContext(InnerDomainUpdate update
            , List<FieldMeta<?, ?>> parentFieldList, final Visible visible) {
        Collection<FieldMeta<?, ?>> parentFields;
        if (parentFieldList.size() < 3) {
            parentFields = parentFieldList;
        } else {
            parentFields = new HashSet<>(parentFieldList);
        }
        return new StandardChildDomainUpdateContext(this.dialect, visible
                , (InnerStandardDomainUpdate) update, parentFields);
    }

    protected ParentDomainUpdateContext createParentDomainUpdateContext(InnerDomainUpdate update
            , List<FieldMeta<?, ?>> childFieldList, Visible visible) {
        Collection<FieldMeta<?, ?>> parentFields;
        if (childFieldList.size() < 3) {
            parentFields = childFieldList;
        } else {
            parentFields = new HashSet<>(childFieldList);
        }
        return new StandardParentDomainUpdateContext(this.dialect, visible
                , (InnerStandardDomainUpdate) update, parentFields);
    }

    /*################################## blow delete template method ##################################*/

    protected void assertSpecialDelete(InnerSpecialDelete delete) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special delete."
                , sqlDialect())
        );
    }

    protected void assertSpecialDomainDelete(InnerSpecialDomainDelete delete) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special domain delete."
                , sqlDialect())
        );
    }

    protected List<SQLWrapper> specialDomainDeleteDispatcher(InnerSpecialDomainDelete delete, Visible visible) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special domain delete."
                , sqlDialect())
        );
    }

    protected List<SQLWrapper> specialDelete(InnerSpecialDelete delete, Visible visible) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special delete."
                , sqlDialect())
        );
    }

    protected ParentDomainDeleteContext createParentDomainDeleteContext(InnerDomainDelete delete
            , final Visible visible) {
        return new StandardParentDomainDeleteContext(this.dialect, visible, (InnerStandardDomainDelete) delete);
    }

    protected ChildDomainDeleteContext createChildDomainDeleteContext(InnerDomainDelete delete
            , final Visible visible) {
        return new StandardChildDomainDeleteContext(this.dialect, visible, (InnerStandardDomainDelete) delete);
    }

    protected DeleteContext createDeleteContext(InnerDelete delete, final Visible visible) {
        return new StandardSingleDeleteContext(this.dialect, visible, (InnerStandardDelete) delete);
    }

    /*################################## blow protected method ##################################*/

    /*################################## blow private batchInsert method ##################################*/

    /**
     * @return a modifiable list
     */
    private List<SQLWrapper> standardInsert(InnerStandardInsert insert, final Visible visible) {

        final TableMeta<?> tableMeta = insert.tableMeta();
        // 1. merge target fields.
        Set<FieldMeta<?, ?>> fieldMetaSet = DMLUtils.mergeInsertFields(tableMeta, this.dialect, insert.fieldList());

        List<IDomain> domainList = insert.valueList();
        List<SQLWrapper> sqlWrapperList;
        if (tableMeta instanceof ChildTableMeta) {
            sqlWrapperList = new ArrayList<>(domainList.size() * 2);
        } else {
            sqlWrapperList = new ArrayList<>(domainList.size());
        }

        FieldValuesGenerator valuesGenerator = FieldValuesGenerator.build(this.dialect.sessionFactory());
        BeanWrapper beanWrapper;

        for (IDomain domain : domainList) {
            // 2. create required values.
            beanWrapper = valuesGenerator.createValues(tableMeta, domain);
            sqlWrapperList.addAll(
                    // 3. create sql of domain
                    insertDomain(tableMeta, beanWrapper, fieldMetaSet, insert, visible)
            );
        }
        return sqlWrapperList;
    }

    private List<SQLWrapper> standardSubQueryInsert(InnerStandardSubQueryInsert insert, final Visible visible) {

        TableMeta<?> tableMeta = insert.tableMeta();
        final List<FieldMeta<?, ?>> fieldMetaList = insert.fieldList();
        int subQuerySelectionCount = DMLUtils.selectionCount(insert.subQuery());

        if (subQuerySelectionCount != fieldMetaList.size()) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                    , "selection size[%s] of SubQuery and targetFieldList size[%s] not match."
                    , subQuerySelectionCount, fieldMetaList.size());
        }

        InsertContext context = createSubQueryInsertContext(insert, visible);
        StringBuilder builder = context.sqlBuilder().append(Keywords.INSERT_INTO);
        context.appendTable(tableMeta);
        builder.append(" ( ");

        int index = 0;
        for (FieldMeta<?, ?> fieldMeta : fieldMetaList) {
            if (index > 0) {
                builder.append(",");
            }
            context.appendField(fieldMeta);
            index++;
        }
        builder.append(" )");
        insert.subQuery().appendSQL(context);
        return Collections.singletonList(context.build());
    }


    private List<SQLWrapper> insertDomain(TableMeta<?> tableMeta, BeanWrapper entityWrapper
            , Collection<FieldMeta<?, ?>> fieldMetas
            , InnerInsert innerInsert, final Visible visible) {

        List<SQLWrapper> sqlWrapperList;
        switch (tableMeta.mappingMode()) {
            case PARENT:
            case SIMPLE:
                sqlWrapperList = Collections.singletonList(
                        createInsertForSimple(tableMeta, entityWrapper, fieldMetas, innerInsert, visible)
                );
                break;
            case CHILD:
                sqlWrapperList = createInsertForChild((ChildTableMeta<?>) tableMeta
                        , entityWrapper, fieldMetas, innerInsert, visible);
                break;
            default:
                throw DialectUtils.createMappingModeUnknownException(tableMeta.mappingMode());

        }
        return sqlWrapperList;
    }

    /**
     * @param mergedFields merged by {@link DMLUtils#mergeInsertFields(TableMeta, Dialect, Collection)}
     */
    private List<SQLWrapper> createInsertForChild(final ChildTableMeta<?> childMeta
            , BeanWrapper beanWrapper, Collection<FieldMeta<?, ?>> mergedFields
            , InnerInsert insert, final Visible visible) {

        Assert.notEmpty(mergedFields, "mergedFields must not empty.");

        final ParentTableMeta<?> parentMeta = childMeta.parentMeta();
        final Set<FieldMeta<?, ?>> parentFields = new HashSet<>(), childFields = new HashSet<>();
        // 1.separate fields
        DialectUtils.separateFields(childMeta, mergedFields, parentFields, childFields);

        List<SQLWrapper> sqlWrapperList = new ArrayList<>(2);
        if (!parentFields.isEmpty()) {
            //2.  add parent sql.
            InsertContext context = createBeanInsertContext(insert, beanWrapper, visible);
            DMLUtils.createStandardInsertForSimple(parentMeta, parentFields, beanWrapper, context);
            sqlWrapperList.add(context.build());
        }

        if (!childFields.isEmpty()) {
            InsertContext context = createBeanInsertContext(insert, beanWrapper, visible);
            DMLUtils.createStandardInsertForSimple(childMeta, childFields, beanWrapper, context);
            //3. add child sql.
            sqlWrapperList.add(context.build());
        }
        return sqlWrapperList;
    }

    /**
     * @param mergedFields merged by {@link DMLUtils#mergeInsertFields(TableMeta, Dialect, Collection)}
     */
    private SQLWrapper createInsertForSimple(TableMeta<?> tableMeta, BeanWrapper beanWrapper
            , Collection<FieldMeta<?, ?>> mergedFields, InnerInsert insert
            , final Visible visible) {

        InsertContext context = createBeanInsertContext(insert, beanWrapper, visible);
        DMLUtils.createStandardInsertForSimple(tableMeta, mergedFields, beanWrapper, context);

        SQLWrapper sqlWrapper;
        GeneratorMeta generatorMeta = tableMeta.primaryKey().generator();

        if (generatorMeta != null
                && ClassUtils.isAssignable(PostFieldGenerator.class, generatorMeta.type())) {
            sqlWrapper = context.build(beanWrapper);
        } else {
            sqlWrapper = context.build();
        }
        return sqlWrapper;
    }

    private List<BatchSQLWrapper> standardBatchInsert(InnerStandardBatchInsert insert, final Visible visible) {

        TableMeta<?> tableMeta = insert.tableMeta();
        List<SQLWrapper> sqlWrapperList;
        switch (tableMeta.mappingMode()) {
            case SIMPLE:
            case PARENT:
                sqlWrapperList = Collections.singletonList(
                        standardBatchInsertForSimple(insert, visible)
                );
                break;
            case CHILD:
                sqlWrapperList = standardBatchInsertForChild(insert, visible);
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("unknown MappingMode[%s]", tableMeta.mappingMode()));

        }
        return DMLUtils.createBatchInsertWrapper(
                insert
                , sqlWrapperList
                , FieldValuesGenerator.build(this.dialect.sessionFactory())
        );
    }


    private SQLWrapper standardBatchInsertForSimple(InnerStandardBatchInsert insert, final Visible visible) {
        TableMeta<?> tableMeta = insert.tableMeta();
        // 1.merge fields
        Set<FieldMeta<?, ?>> fieldMetaSet = DMLUtils.mergeInsertFields(tableMeta, this.dialect, insert.fieldList());

        InsertContext context = createBatchInsertContext(insert, visible);
        // single table insert sql
        DMLUtils.createBatchInsertForSimple(tableMeta, fieldMetaSet, context);
        return context.build();
    }

    private List<SQLWrapper> standardBatchInsertForChild(InnerStandardBatchInsert insert, final Visible visible) {
        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) insert.tableMeta();
        ParentTableMeta<?> parentMeta = childMeta.parentMeta();

        Set<FieldMeta<?, ?>> parentFieldSet = new HashSet<>(), childFieldSet = new HashSet<>();
        // 1.  separate target fields
        DialectUtils.separateFields(childMeta, insert.fieldList(), parentFieldSet, childFieldSet);
        // 2. merge fields
        parentFieldSet = DMLUtils.mergeInsertFields(parentMeta, this.dialect, parentFieldSet);
        childFieldSet = DMLUtils.mergeInsertFields(childMeta, this.dialect, childFieldSet);
        // separate fields end.

        List<SQLWrapper> sqlWrapperList = new ArrayList<>(2);
        // 3. parent sql wrapper
        final InsertContext parentContext = createBatchInsertContext(insert, visible);
        DMLUtils.createBatchInsertForSimple(parentMeta, parentFieldSet, parentContext);
        sqlWrapperList.add(parentContext.build());

        //4. child sql wrapper
        final InsertContext childContext = createBatchInsertContext(insert, visible);
        DMLUtils.createBatchInsertForSimple(childMeta, childFieldSet, childContext);
        sqlWrapperList.add(childContext.build());
        return Collections.unmodifiableList(sqlWrapperList);
    }

    /*################################## blow update private method ##################################*/

    private SQLWrapper standardSimpleUpdate(InnerUpdate update, TableMeta<?> tableMeta, String tableAlias
            , final Visible visible) {
        UpdateContext context = createUpdateContext(update, visible);

        StringBuilder builder = context.sqlBuilder().append(Keywords.UPDATE);
        tableOnlyModifier(context);
        // append table name and alias
        context.appendTable(tableMeta);
        if (tableAliasAfterAs()) {
            builder.append(" AS");
        }
        context.appendText(tableAlias);
        // set clause
        standardSimpleUpdateSetClause(context, tableMeta, tableAlias
                , update.targetFieldList(), update.valueExpList());
        // where clause
        simpleTableWhereClause(context, tableMeta, tableAlias
                , update.predicateList());

        return context.build();
    }

    private List<SQLWrapper> standardDomainUpdate(InnerStandardDomainUpdate update, Visible visible) {
        List<SQLWrapper> list;
        switch (update.tableMeta().mappingMode()) {
            case SIMPLE:
            case PARENT:
                list = Collections.singletonList(
                        standardSimpleUpdate(update, update.tableMeta(), update.tableAlias(), visible)
                );
                break;
            case CHILD:
                list = standardDomainUpdateChildDispatcher(update, visible);
                break;
            default:
                throw new IllegalArgumentException(String.format("unknown MappingMode[%s]"
                        , update.tableMeta().mappingMode()));

        }
        return list;
    }


    private void standardSimpleUpdateSetClause(DMLContext context, TableMeta<?> tableMeta, String tableAlias
            , List<FieldMeta<?, ?>> fieldMetaList, List<Expression<?>> valueExpList) {

        Assert.notEmpty(fieldMetaList, "set clause must not empty");
        Assert.isTrue(fieldMetaList.size() == valueExpList.size(), "field list ifAnd value exp list size not match");

        final int size = fieldMetaList.size();
        FieldMeta<?, ?> fieldMeta;
        Expression<?> valueExp;

        StringBuilder builder = context.sqlBuilder()
                .append(" ")
                .append(Keywords.SET);
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                builder.append(",");
            }

            fieldMeta = fieldMetaList.get(i);
            valueExp = valueExpList.get(i);

            DMLUtils.assertSingleUpdateSetClauseField(fieldMeta, tableMeta);

            // fieldMeta self-describe
            context.appendField(tableAlias, fieldMeta);
            builder.append(" =");
            // expression self-describe
            valueExp.appendSQL(context);

        }
        if (tableMeta.mappingMode() != MappingMode.CHILD) {
            // appendText version And updateTime
            setClauseFieldsManagedByArmy(context, tableMeta, tableAlias);
        }
    }

    private void setClauseFieldsManagedByArmy(ClauseSQLContext context, TableMeta<?> tableMeta, String tableAlias) {
        //1. version field
        final FieldMeta<?, ?> versionField = tableMeta.getField(TableMeta.VERSION);
        StringBuilder builder = context.sqlBuilder();

        builder.append(",");
        context.appendField(tableAlias, versionField);

        builder.append(" =");
        context.appendField(tableAlias, versionField);
        builder.append(" + 1 ");

        //2. updateTime field
        final FieldMeta<?, ?> updateTimeField = tableMeta.getField(TableMeta.UPDATE_TIME);

        builder.append(",");
        // updateTime field self-describe
        context.appendField(tableAlias, updateTimeField);
        builder.append(" =");

        final ZonedDateTime now = ZonedDateTime.now(this.dialect.zoneId());

        if (updateTimeField.javaType() == LocalDateTime.class) {
            SQLS.param(now.toLocalDateTime(), updateTimeField.mappingType())
                    .appendSQL(context);
        } else if (updateTimeField.javaType() == ZonedDateTime.class) {
            if (!this.dialect.supportZoneId()) {
                throw new MetaException(ErrorCode.META_ERROR
                        , "dialec[%s]t not supported zone.", this.dialect.sqlDialect());
            }
            SQLS.param(now, updateTimeField.mappingType())
                    .appendSQL(context);
        } else {
            throw new MetaException(ErrorCode.META_ERROR
                    , "createTime or updateTime only support LocalDateTime or ZonedDateTime,please check.");
        }
    }

    private void simpleTableWhereClause(ClauseSQLContext context, TableMeta<?> tableMeta, String tableAlias
            , List<IPredicate> predicateList) {

        if (CollectionUtils.isEmpty(predicateList)) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "update or delete must have where clause.");
        }
        context.currentClause(Clause.WHERE);
        StringBuilder builder = context.sqlBuilder();
        int index = 0;
        for (IPredicate predicate : predicateList) {
            if (index > 0) {
                builder.append(" ")
                        .append(Keywords.AND);
            }
            // predicate self-describe
            predicate.appendSQL(context);
            index++;
        }

        switch (tableMeta.mappingMode()) {
            case SIMPLE:
            case PARENT:
                visibleConstantPredicate(context, tableMeta, tableAlias);
                break;
            case CHILD:
                visibleSubQueryPredicateForChild(context, (ChildTableMeta<?>) tableMeta, tableAlias);
                break;
            default:
                throw new IllegalArgumentException(String.format("unknown MappingMode[%s].", tableMeta.mappingMode()));
        }
    }


    private List<SQLWrapper> standardDomainUpdateChildDispatcher(InnerStandardDomainUpdate update
            , final Visible visible) {

        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) update.tableMeta();
        ParentTableMeta<?> parentMeta = childMeta.parentMeta();

        final List<FieldMeta<?, ?>> targetFieldList = update.targetFieldList();
        final List<Expression<?>> valueList = update.valueExpList();

        Assert.notEmpty(targetFieldList, "set clause must not empty");
        Assert.isTrue(targetFieldList.size() == valueList.size(), "field list size and value list size not match");

        //1. separate target FieldMeta and value Expression.
        List<FieldMeta<?, ?>> parentFieldList = new ArrayList<>(), childFieldList = new ArrayList<>();
        List<Expression<?>> parentValueList = new ArrayList<>(), childValueList = new ArrayList<>();

        FieldMeta<?, ?> fieldMeta;
        Expression<?> valueExp;
        final int size = targetFieldList.size();
        for (int i = 0; i < size; i++) {
            fieldMeta = targetFieldList.get(i);
            valueExp = valueList.get(i);

            if (fieldMeta.tableMeta() == parentMeta) {
                parentFieldList.add(fieldMeta);
                parentValueList.add(valueExp);
            } else if (fieldMeta.tableMeta() == childMeta) {
                childFieldList.add(fieldMeta);
                childValueList.add(valueExp);
            } else {
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                        , "FieldMeta[%s] don't belong to TableMeta[%s] or its parent.", fieldMeta, childMeta);
            }
        }
        // separate target FieldMeta and value Expression end.

        //2.  parent table update sql,maybe contains select
        final List<SQLWrapper> parentSqlList = standardDomainUpdateForParent(update, parentFieldList
                , parentValueList, childFieldList, visible);

        //3.  child table update sql
        final SQLWrapper childSqlWrapper = standardDomainUpdateForChild(update, childFieldList
                , childValueList, parentFieldList, visible);
        // merge childSqlWrapper and parentSqlList
        return DMLUtils.mergeDomainSQLWrappers(childSqlWrapper, parentSqlList);
    }

    private List<SQLWrapper> standardDomainUpdateForParent(InnerStandardDomainUpdate update
            , List<FieldMeta<?, ?>> parentFieldList, List<Expression<?>> parentValueList,
                                                           List<FieldMeta<?, ?>> childFieldList, final Visible visible) {

        ParentDomainUpdateContext context = createParentDomainUpdateContext(update, childFieldList, visible);

        StringBuilder builder = context.sqlBuilder().append("UPDATE");
        tableOnlyModifier(context);
        // append table name and alias
        context.appendTable(update.tableMeta());
        if (tableAliasAfterAs()) {
            builder.append(" AS");
        }
        context.appendText(update.tableAlias());
        // set clause
        standardSimpleUpdateSetClause(context, update.tableMeta(), update.tableAlias()
                , parentFieldList, parentValueList);
        // merge sql fragment 'id = ?' and update.predicateList()
        List<IPredicate> mergedPredicateList = DMLUtils.mergeDomainUpdatePredicateList(
                update.predicateList(), context.tableMeta().primaryKey(), update.primaryKeyValue());

        // where clause with mergedPredicateList
        simpleTableWhereClause(context, context.tableMeta(), update.tableAlias()
                , mergedPredicateList);

        SQLWrapper parentSQLWrapper = context.build();

        List<SQLWrapper> sqlWrapperList;
        if (context.needQueryChild()) {
            sqlWrapperList = new ArrayList<>(2);
            // firstly, add query child sql
            sqlWrapperList.add(
                    // create sql that query child's updated fields for update parent
                    DMLUtils.createQueryChildBeanSQLWrapper(update, childFieldList, this.dialect, visible)
            );
            // secondly, add parent update sql.
            sqlWrapperList.add(parentSQLWrapper);
        } else {
            sqlWrapperList = Collections.singletonList(parentSQLWrapper);
        }
        return sqlWrapperList;
    }

    private SQLWrapper standardDomainUpdateForChild(InnerStandardDomainUpdate update
            , List<FieldMeta<?, ?>> childFieldList, List<Expression<?>> childValueList
            , List<FieldMeta<?, ?>> parentFieldList, final Visible visible) {

        ChildDomainUpdateContext context = createChildDomainUpdateContext(update, parentFieldList, visible);
        // update clause
        StringBuilder builder = context.sqlBuilder().append("UPDATE");
        // eg: oracle need add 'ONLY' prefix.
        tableOnlyModifier(context);
        // append table name and alias
        context.appendTable(update.tableMeta());
        if (tableAliasAfterAs()) {
            builder.append(" AS");
        }
        context.appendText(update.tableAlias());
        // set clause
        standardSimpleUpdateSetClause(context, update.tableMeta(), update.tableAlias()
                , childFieldList, childValueList);

        // merge sql fragment 'id = ?' and update.predicateList()
        List<IPredicate> mergedPredicateList = DMLUtils.mergeDomainUpdatePredicateList(
                update.predicateList(), context.tableMeta().primaryKey(), update.primaryKeyValue());

        // where clause with mergedPredicateList
        simpleTableWhereClause(context, context.tableMeta(), context.tableAlias()
                , mergedPredicateList);

        return context.build();
    }

    /*################################## blow delete private method ##################################*/

    private SQLWrapper standardSingleDelete(InnerDelete delete, TableMeta<?> tableMeta, String tableAlias
            , final Visible visible) {
        DeleteContext context = createDeleteContext(delete, visible);
        StringBuilder builder = context.sqlBuilder().append("DELETE FROM");
        tableOnlyModifier(context);
        // append table name
        context.appendTable(tableMeta);

        if (this.singleDeleteHasTableAlias()) {
            if (this.tableAliasAfterAs()) {
                builder.append(" AS");
            }
            context.appendText(tableAlias);
        }
        // where clause
        simpleTableWhereClause(context, tableMeta, tableAlias, delete.predicateList());
        return context.build();
    }


    private List<SQLWrapper> standardDomainDeleteDispatcher(InnerStandardDomainDelete delete, final Visible visible) {
        List<SQLWrapper> sqlWrapperList;
        switch (delete.tableMeta().mappingMode()) {
            case PARENT:
            case SIMPLE:
                sqlWrapperList = Collections.singletonList(
                        standardSingleDelete(delete, delete.tableMeta(), delete.tableAlias(), visible)
                );
                break;
            case CHILD:
                final SQLWrapper childSql = standardDomainDeleteForChild(delete, visible);
                final List<SQLWrapper> parentSqlList = standardDomainDeleteForParent(delete, visible);
                if (parentSqlList.size() != 2) {
                    throw DialectUtils.createArmyCriteriaException();
                }
                sqlWrapperList = DMLUtils.mergeDomainSQLWrappers(childSql, parentSqlList);
                break;
            default:
                throw DialectUtils.createMappingModeUnknownException(delete.tableMeta().mappingMode());
        }
        return sqlWrapperList;
    }


    private SQLWrapper standardDomainDeleteForChild(InnerStandardDomainDelete delete, final Visible visible) {
        ChildDomainDeleteContext context = createChildDomainDeleteContext(delete, visible);
        doStandardDomainDelete(context, delete, visible);
        return context.build();
    }

    private List<SQLWrapper> standardDomainDeleteForParent(InnerStandardDomainDelete delete, final Visible visible) {
        List<SQLWrapper> parentSqlList = new ArrayList<>(2);
        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) delete.tableMeta();

        List<FieldMeta<?, ?>> childFieldList = new ArrayList<>(childMeta.fieldCollection());
        // firstly, add query child sql.
        parentSqlList.add(
                DMLUtils.createQueryChildBeanSQLWrapper(delete, childFieldList, this.dialect, visible)
        );

        ParentDomainDeleteContext context = createParentDomainDeleteContext(delete, visible);
        doStandardDomainDelete(context, delete, visible);
        // secondly, add parent delete sql.
        parentSqlList.add(context.build());
        return parentSqlList;
    }

    private void doStandardDomainDelete(DomainDeleteContext context
            , InnerStandardDomainDelete delete, final Visible visible) {

        StringBuilder builder = context.sqlBuilder().append("DELETE FROM");
        tableOnlyModifier(context);
        // append table name
        context.appendTable(context.tableMeta());

        if (this.singleDeleteHasTableAlias()) {
            if (this.tableAliasAfterAs()) {
                builder.append(" AS");
            }
            context.appendText(context.tableAlias());
        }
        // merge sql fragment 'id = ?' and update.predicateList()
        List<IPredicate> mergedPredicateList = DMLUtils.mergeDomainUpdatePredicateList(
                delete.predicateList(), context.tableMeta().primaryKey(), delete.primaryKeyValue());

        // where clause with mergedPredicateList
        simpleTableWhereClause(context, context.tableMeta(), context.tableAlias()
                , mergedPredicateList);
    }


}
