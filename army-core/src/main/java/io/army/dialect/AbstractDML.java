package io.army.dialect;

import io.army.ErrorCode;
import io.army.SessionFactory;
import io.army.beans.BeanWrapper;
import io.army.boot.FieldValuesGenerator;
import io.army.criteria.*;
import io.army.criteria.impl.CriteriaCounselor;
import io.army.criteria.impl.SQLS;
import io.army.criteria.impl.inner.*;
import io.army.domain.IDomain;
import io.army.generator.PostMultiGenerator;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.util.Assert;
import io.army.util.ClassUtils;
import io.army.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;

public abstract class AbstractDML extends AbstractDMLAndDQL implements DML {

    public AbstractDML(Dialect dialect) {
        super(dialect);
    }

    /*################################## blow DML batchInsert method ##################################*/

    @Override
    public final List<SQLWrapper> insert(IDomain domain) {
        Assert.notNull(domain, "domain required");

        final SessionFactory sessionFactory = this.dialect.sessionFactory();

        TableMeta<?> tableMeta = sessionFactory.tableMetaMap().get(domain.getClass());
        //  create necessary value for domain
        BeanWrapper beanWrapper = FieldValuesGenerator.build(sessionFactory)
                .createValues(tableMeta, domain);

        return Collections.unmodifiableList(
                insertDomain(tableMeta, beanWrapper, tableMeta.fieldCollection(), null, Visible.ONLY_VISIBLE)
        );

    }


    @Override
    public final List<SQLWrapper> insert(Insert insert) {
        List<SQLWrapper> list;
        if (insert instanceof InnerStandardInsert) {
            list = standardInsert((InnerStandardInsert) insert, Visible.ONLY_VISIBLE);
        } else if (insert instanceof InnerStandardSubQueryInsert) {
            list = standardSubQueryInsert((InnerStandardSubQueryInsert) insert, Visible.ONLY_VISIBLE);
        } else if (insert instanceof InnerSpecialInsert) {
            list = specialInsert((InnerSpecialInsert) insert);
        } else {
            throw new IllegalArgumentException(String.format("Insert[%s] type unknown.", insert.getClass().getName()));
        }

        return Collections.unmodifiableList(list);
    }

    @Override
    public final List<BatchSQLWrapper> batchInsert(Insert insert) {
        List<BatchSQLWrapper> list;
        if (insert instanceof InnerStandardBatchInsert) {
            list = standardBatchInsert((InnerStandardBatchInsert) insert, Visible.ONLY_VISIBLE);
        } else if (insert instanceof InnerSpecialInsert) {
            list = specialBatchInsert((InnerSpecialInsert) insert);
        } else {
            throw new IllegalArgumentException(String.format("Insert[%s] type unknown.", insert));
        }
        return Collections.unmodifiableList(list);
    }

    /*################################## blow update method ##################################*/

    @Override
    public final List<SQLWrapper> update(Update update, Visible visible) {
        List<SQLWrapper> list;

        if (update instanceof InnerStandardDomainUpdate) {

            CriteriaCounselor.assertStandardUpdate((InnerStandardDomainUpdate) update);
            list = Collections.unmodifiableList(
                    standardDomainUpdate((InnerStandardDomainUpdate) update, visible)
            );

        } else if (update instanceof InnerStandardUpdate) {

            CriteriaCounselor.assertStandardUpdate((InnerStandardUpdate) update);
            list = Collections.singletonList(
                    standardSingleUpdate((InnerStandardUpdate) update, visible)
            );
        } else if (update instanceof InnerSpecialUpdate) {
            list = Collections.unmodifiableList(
                    specialUpdate((InnerSpecialUpdate) update, visible)
            );
        } else {
            throw new IllegalArgumentException(String.format("Update[%s] type unknown.", update));
        }
        return list;
    }

    @Override
    public final List<SQLWrapper> delete(Delete delete, Visible visible) {
        List<SQLWrapper> list;
        if (delete instanceof InnerStandardDomainDelete) {
            CriteriaCounselor.assertStandardDelete((InnerStandardDomainDelete) delete);
            list = standardDomainDeleteDispatcher((InnerStandardDomainDelete) delete, visible);

        } else if (delete instanceof InnerStandardDelete) {
            CriteriaCounselor.assertStandardDelete((InnerStandardDelete) delete);
            list = Collections.singletonList(
                    standardSingleDelete((InnerStandardDelete) delete, visible)
            );

        } else if (delete instanceof InnerSpecialDelete) {
            list = specialDelete((InnerSpecialDelete) delete, visible);
        } else {
            throw new IllegalArgumentException(String.format("Delete[%s] type unknown.", delete));
        }
        return Collections.unmodifiableList(list);
    }

    /*################################## blow package batchInsert template method ##################################*/

    protected InsertContext createInsertContext(InnerInsert insert, final Visible visible) {
        return new StandardInsertContext(this.dialect, visible, insert);
    }


    protected UpdateContext createUpdateContext(InnerUpdate update, final Visible visible) {
        return new StandardUpdateContext(this.dialect, visible, (InnerStandardUpdate) update);
    }

    protected StandardChildDomainUpdateContext createChildDomainUpdateContext(InnerDomainUpdate update
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

    protected DeleteContext createDeleteContext(InnerDelete delete, final Visible visible) {
        return new StandardSingleDeleteContext(this.dialect, visible, (InnerStandardDelete) delete);
    }


    protected ParentDomainDeleteContext createParentDomainDeleteContext(InnerDomainDelete delete
            , final Visible visible) {
        return new StandardParentDomainDeleteContext(this.dialect, visible, (InnerStandardDomainDelete) delete);
    }

    protected ChildDomainDeleteContext createChildDomainDeleteContext(InnerDomainDelete delete
            , final Visible visible) {
        return new StandardChildDomainDeleteContext(this.dialect, visible, (InnerStandardDomainDelete) delete);
    }

    protected abstract List<SQLWrapper> specialInsert(InnerSpecialInsert insert);

    protected abstract List<BatchSQLWrapper> specialBatchInsert(InnerSpecialInsert insert);

    protected abstract List<SQLWrapper> specialUpdate(InnerSpecialUpdate update, Visible visible);

    protected abstract List<SQLWrapper> specialDelete(InnerSpecialDelete delete, Visible visible);

    protected void tableOnlyModifier(SQLContext context) {
        context.sqlBuilder()
                .append(" ");
    }





    /*################################## blow protected method ##################################*/

    /*################################## blow private batchInsert method ##################################*/

    /**
     * @return a modifiable list
     */
    private List<SQLWrapper> standardInsert(InnerStandardInsert insert, final Visible visible) {
        CriteriaCounselor.assertInsert(insert);

        List<IDomain> domainList = insert.valueList();
        TableMeta<?> tableMeta = insert.tableMeta();
        // 1. get target fields.
        Collection<FieldMeta<?, ?>> fieldMetas = insert.fieldList();
        if (CollectionUtils.isEmpty(fieldMetas)) {
            fieldMetas = Collections.unmodifiableCollection(tableMeta.fieldCollection());
        }

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
                    insertDomain(tableMeta, beanWrapper, fieldMetas, insert, visible)
            );
        }
        return sqlWrapperList;
    }

    private List<SQLWrapper> standardSubQueryInsert(InnerStandardSubQueryInsert insert, final Visible visible) {
        CriteriaCounselor.assertInsert(insert);

        TableMeta<?> tableMeta = insert.tableMeta();
        List<FieldMeta<?, ?>> fieldMetaList = insert.fieldList();
        int subQuerySelectionCount = DMLUtils.selectionCount(insert.subQuery());

        if (subQuerySelectionCount != fieldMetaList.size()) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                    , "selection size[%s] of SubQuery and targetFieldList size[%s] not match."
                    , subQuerySelectionCount, fieldMetaList.size());
        }

        InsertContext context = createInsertContext(insert, visible);
        StringBuilder builder = context.fieldStringBuilder().append("INSERT INTO ");
        context.appendTable(tableMeta);
        builder.append(" ( ");

        int index = 0;
        for (FieldMeta<?, ?> fieldMeta : fieldMetaList) {
            if (index > 0) {
                builder.append(",");
            }
            builder.append(this.dialect.quoteIfNeed(fieldMeta.fieldName()));
            index++;
        }
        builder.append(" )");
        insert.subQuery().appendSQL(context);
        return Collections.singletonList(context.build());
    }


    private List<SQLWrapper> insertDomain(TableMeta<?> tableMeta, BeanWrapper entityWrapper
            , @Nullable Collection<? extends FieldMeta<?, ?>> fieldMetas
            , InnerInsert innerInsert, final Visible visible) {

        List<SQLWrapper> sqlWrapperList;
        switch (tableMeta.mappingMode()) {
            case SIMPLE:
                sqlWrapperList = Collections.singletonList(
                        createInsertForSimple(tableMeta, entityWrapper, fieldMetas, innerInsert, visible)
                );
                break;
            case CHILD:
                sqlWrapperList = createInsertForChild((ChildTableMeta<?>) tableMeta
                        , entityWrapper, fieldMetas, innerInsert, visible);
                break;
            case PARENT:
                sqlWrapperList = Collections.singletonList(
                        createInsertForParent(tableMeta, entityWrapper, fieldMetas, innerInsert, visible)
                );
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("unknown MappingMode[%s]", tableMeta.mappingMode()));

        }
        return sqlWrapperList;
    }


    private List<SQLWrapper> createInsertForChild(ChildTableMeta<?> childMeta
            , BeanWrapper beanWrapper, @Nullable Collection<? extends FieldMeta<?, ?>> fieldMetas
            , @Nullable InnerInsert innerInsert, final Visible visible) {

        TableMeta<?> parentMeta = childMeta.parentMeta();
        Collection<FieldMeta<?, ?>> childFields, parentFields;
        if (fieldMetas == null) {
            // unmodifiableCollection for avoid generic error in below part
            parentFields = Collections.unmodifiableCollection(parentMeta.fieldCollection());
            childFields = Collections.unmodifiableCollection(childMeta.fieldCollection());
        } else {

            Set<FieldMeta<?, ?>> parentFieldSet = new HashSet<>(parentMeta.fieldCollection());
            Set<FieldMeta<?, ?>> childFieldSet = new HashSet<>(childMeta.fieldCollection());

            childFields = new ArrayList<>();
            parentFields = new ArrayList<>();

            for (FieldMeta<?, ?> fieldMeta : fieldMetas) {
                if (childFieldSet.contains(fieldMeta)) {
                    childFields.add(fieldMeta);
                } else if (parentFieldSet.contains(fieldMeta)) {
                    parentFields.add(fieldMeta);
                } else {
                    throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "FieldMeta[%s] and ChildTableMeta[%s] not match."
                            , fieldMeta, childMeta);
                }
            }

        }

        List<SQLWrapper> sqlWrapperList = new ArrayList<>(2);
        sqlWrapperList.add(
                createInsertForParent(parentMeta, beanWrapper, parentFields, innerInsert, visible)
        );

        InsertContext context = createInsertContext(innerInsert, visible);
        DMLUtils.createInsertForSimple(childMeta, childFields, beanWrapper, context);
        sqlWrapperList.add(context.build());
        return sqlWrapperList;
    }


    /**
     * @return a modifiable list
     */
    private SQLWrapper createInsertForParent(TableMeta<?> tableMeta, BeanWrapper beanWrapper
            , @Nullable Collection<? extends FieldMeta<?, ?>> fieldMetas, InnerInsert innerInsert
            , Visible visible) {
        return createInsertForSimple(tableMeta, beanWrapper, fieldMetas, innerInsert, visible);
    }

    private SQLWrapper createInsertForSimple(TableMeta<?> tableMeta, BeanWrapper beanWrapper
            , @Nullable Collection<? extends FieldMeta<?, ?>> fieldMetas, InnerInsert innerInsert
            , final Visible visible) {
        Collection<? extends FieldMeta<?, ?>> targetFields = fieldMetas;
        if (targetFields == null) {
            // unmodifiableCollection for avoid generic error
            targetFields = tableMeta.fieldCollection();
        }
        InsertContext context = createInsertContext(innerInsert, visible);
        DMLUtils.createInsertForSimple(tableMeta, targetFields, beanWrapper, context);

        SQLWrapper sqlWrapper;
        GeneratorMeta generatorMeta = tableMeta.primaryKey().generator();

        if (generatorMeta != null
                && ClassUtils.isAssignable(PostMultiGenerator.class, generatorMeta.type())) {
            sqlWrapper = context.build(beanWrapper);
        } else {
            sqlWrapper = context.build();
        }
        return sqlWrapper;
    }

    private List<BatchSQLWrapper> standardBatchInsert(InnerStandardBatchInsert insert, final Visible visible) {
        CriteriaCounselor.assertInsert(insert);

        TableMeta<?> tableMeta = insert.tableMeta();
        List<SQLWrapper> sqlWrapperList;
        switch (tableMeta.mappingMode()) {
            case SIMPLE:
                sqlWrapperList = Collections.singletonList(
                        standardBatchInsertForSimple(insert, visible)
                );
                break;
            case PARENT:
                sqlWrapperList = Collections.singletonList(
                        standardBatchInsertForParent(insert, visible)
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
        InsertContext context = createInsertContext(insert, visible);
        DMLUtils.createBatchInsertForSimple(insert.tableMeta(), context);
        return context.build();
    }

    private SQLWrapper standardBatchInsertForParent(InnerStandardBatchInsert insert, final Visible visible) {
        return standardBatchInsertForSimple(insert, visible);
    }

    private List<SQLWrapper> standardBatchInsertForChild(InnerStandardBatchInsert insert, final Visible visible) {
        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) insert.tableMeta();
        TableMeta<?> parentMeta = childMeta.parentMeta();

        List<SQLWrapper> sqlWrapperList = new ArrayList<>(2);
        // 1. parent sql wrapper
        sqlWrapperList.add(
                standardBatchInsertForParent(insert, visible)
        );
        //2. child sql wrapper
        InsertContext context = createInsertContext(insert, visible);
        DMLUtils.createBatchInsertForSimple(insert.tableMeta(), context);
        sqlWrapperList.add(
                context.build()
        );
        return Collections.unmodifiableList(sqlWrapperList);
    }

    /*################################## blow update private method ##################################*/

    private SQLWrapper standardSingleUpdate(InnerStandardUpdate update, Visible visible) {
        UpdateContext context = createUpdateContext(update, visible);

        StringBuilder builder = context.sqlBuilder().append("UPDATE");
        tableOnlyModifier(context);
        // append table name and alias
        context.appendTable(update.tableMeta());
        if (tableAliasAfterAs()) {
            builder.append(" AS");
        }
        context.appendText(update.tableAlias());
        // set clause
        standardSingleUpdateSetClause(context, update.tableMeta(), update.tableAlias()
                , update.targetFieldList(), update.valueExpList());
        // where clause
        singleTableWhereClause(context, update.tableMeta(), update.tableAlias()
                , update.predicateList(), visible);

        return context.build();
    }

    private List<SQLWrapper> standardDomainUpdate(InnerStandardDomainUpdate update, Visible visible) {
        List<SQLWrapper> list;
        switch (update.tableMeta().mappingMode()) {
            case SIMPLE:
            case PARENT:
                list = Collections.singletonList(
                        standardSingleUpdate(update, visible)
                );
                break;
            case CHILD:
                list = standardDomainUpdateDispatcher(update, visible);
                break;
            default:
                throw new IllegalArgumentException(String.format("unknown MappingMode[%s]"
                        , update.tableMeta().mappingMode()));

        }
        return list;
    }


    private void standardSingleUpdateSetClause(UpdateContext context, TableMeta<?> tableMeta, String tableAlias
            , List<FieldMeta<?, ?>> fieldMetaList, List<Expression<?>> valueExpList) {

        Assert.notEmpty(fieldMetaList, "set clause must not empty");
        Assert.isTrue(fieldMetaList.size() == valueExpList.size(), "field list ifAnd value exp list size not match");

        final int size = fieldMetaList.size();
        FieldMeta<?, ?> fieldMeta;
        Expression<?> valueExp;

        StringBuilder builder = context.sqlBuilder()
                .append(" SET");
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

    private void setClauseFieldsManagedByArmy(UpdateContext context, TableMeta<?> tableMeta, String tableAlias) {
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

        final ZonedDateTime now = ZonedDateTime.now(context.dql().zoneId());

        if (updateTimeField.javaType() == LocalDateTime.class) {
            SQLS.param(now.toLocalDateTime(), updateTimeField.mappingType())
                    .appendSQL(context);
        } else if (updateTimeField.javaType() == ZonedDateTime.class) {
            SQLS.param(now, updateTimeField.mappingType())
                    .appendSQL(context);
        } else {
            throw new MetaException(ErrorCode.META_ERROR
                    , "createTime or updateTime only support LocalDateTime or ZonedDateTime");
        }
    }

    private void singleTableWhereClause(SQLContext context, TableMeta<?> tableMeta, String tableAlias
            , List<IPredicate> predicateList
            , Visible visible) {

        if (CollectionUtils.isEmpty(predicateList)) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "update or delete must have where clause.");
        }

        StringBuilder builder = context.sqlBuilder()
                .append(" WHERE");

        for (Iterator<IPredicate> iterator = predicateList.iterator(); iterator.hasNext(); ) {
            // predicate self-describe
            iterator.next().appendSQL(context);
            if (iterator.hasNext()) {
                builder.append(" AND");
            }
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

    private List<SQLWrapper> standardDomainUpdateDispatcher(InnerStandardDomainUpdate update, final Visible visible) {
        List<SQLWrapper> sqlWrapperList;
        switch (update.tableMeta().mappingMode()) {
            case SIMPLE:
            case PARENT:
                sqlWrapperList = Collections.singletonList(
                        standardSingleUpdate(update, visible)
                );
                break;
            case CHILD:
                sqlWrapperList = standardDomainUpdateChildDispatcher(update, visible);
                break;
            default:
                throw DialectUtils.createMappingModeUnknownException(update.tableMeta().mappingMode());
        }
        return sqlWrapperList;
    }

    private List<SQLWrapper> standardDomainUpdateChildDispatcher(InnerStandardDomainUpdate update
            , final Visible visible) {

        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) update.tableMeta();
        ParentTableMeta<?> parentMeta = childMeta.parentMeta();

        final List<FieldMeta<?, ?>> targetFieldList = update.targetFieldList();
        final List<Expression<?>> valueList = update.valueExpList();

        Assert.notEmpty(targetFieldList, "set clause must not empty");
        Assert.isTrue(targetFieldList.size() == valueList.size(), "field list size and value list size not match");


        List<FieldMeta<?, ?>> parentFieldList = new ArrayList<>(), childFieldList = new ArrayList<>();
        List<Expression<?>> parentValueList = new ArrayList<>(), childValueList = new ArrayList<>();
        // separate target FieldMeta and value Expression.
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

        //  parent table update sql,maybe contains select
        final List<SQLWrapper> parentSqlList = standardDomainUpdateForParent(update, parentFieldList
                , parentValueList, childFieldList, visible);

        //   child table update sql
        final SQLWrapper childSqlWrapper = standardDomainUpdateForChild(update, childFieldList
                , childValueList, parentFieldList, visible);
        // merge childSqlWrapper and parentSqlList
        return DMLUtils.createDomainSQLWrapperList(childSqlWrapper, parentSqlList);
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
        standardSingleUpdateSetClause(context, update.tableMeta(), update.tableAlias()
                , parentFieldList, parentValueList);
        // merge sql fragment 'id = ?' and update.predicateList()
        List<IPredicate> mergedPredicateList = DMLUtils.mergeDomainUpdatePredicateList(
                update.predicateList(), context.tableMeta().primaryKey(), update.primaryKeyValue());

        // where clause with mergedPredicateList
        singleTableWhereClause(context, context.tableMeta(), update.tableAlias()
                , mergedPredicateList, visible);

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

        StandardChildDomainUpdateContext context = createChildDomainUpdateContext(update, parentFieldList, visible);
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
        standardSingleUpdateSetClause(context, update.tableMeta(), update.tableAlias()
                , childFieldList, childValueList);

        // merge sql fragment 'id = ?' and update.predicateList()
        List<IPredicate> mergedPredicateList = DMLUtils.mergeDomainUpdatePredicateList(
                update.predicateList(), context.tableMeta().primaryKey(), update.primaryKeyValue());

        // where clause with mergedPredicateList
        singleTableWhereClause(context, context.tableMeta(), context.tableAlias()
                , mergedPredicateList, visible);

        return context.build();
    }

    /*################################## blow delete private method ##################################*/

    private SQLWrapper standardSingleDelete(InnerStandardDelete delete, final Visible visible) {
        DeleteContext context = createDeleteContext(delete, visible);
        StringBuilder builder = context.sqlBuilder().append("DELETE FROM");
        tableOnlyModifier(context);
        // append table name
        context.appendTable(delete.tableMeta());

        if (this.singleDeleteHasTableAlias()) {
            if (this.tableAliasAfterAs()) {
                builder.append(" AS");
            }
            context.appendText(delete.tableAlias());
        }
        // where clause
        singleTableWhereClause(context, delete.tableMeta(), delete.tableAlias()
                , delete.predicateList(), visible);
        return context.build();
    }


    private List<SQLWrapper> standardDomainDeleteDispatcher(InnerStandardDomainDelete delete, final Visible visible) {
        List<SQLWrapper> sqlWrapperList;
        switch (delete.tableMeta().mappingMode()) {
            case PARENT:
            case SIMPLE:
                sqlWrapperList = Collections.singletonList(
                        standardSingleDelete(delete, visible)
                );
                break;
            case CHILD:
                final SQLWrapper childSql = standardDomainDeleteForChild(delete, visible);
                final List<SQLWrapper> parentSqlList = standardDomainDeleteForParent(delete, visible);
                if (parentSqlList.size() != 2) {
                    throw DialectUtils.createArmyCriteriaException();
                }
                sqlWrapperList = DMLUtils.createDomainSQLWrapperList(childSql, parentSqlList);
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
        singleTableWhereClause(context, context.tableMeta(), context.tableAlias()
                , mergedPredicateList, visible);
    }


}
