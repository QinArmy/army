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
import io.army.meta.mapping.MappingFactory;
import io.army.util.Assert;
import io.army.util.ClassUtils;
import io.army.util.CollectionUtils;
import io.army.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public abstract class AbstractDML implements DML {

    private final Dialect dialect;
    private Collection<FieldMeta<?, ?>> childFields;

    public AbstractDML(Dialect dialect) {
        this.dialect = dialect;
    }

    /*################################## blow SQL interface method ##################################*/

    @Override
    public final String quoteIfNeed(String identifier) {
        return dialect.quoteIfNeed(identifier);
    }

    @Override
    public final boolean isKeyWord(String identifier) {
        return dialect.isKeyWord(identifier);
    }

    @Override
    public final ZoneId zoneId() {
        return dialect.zoneId();
    }

    @Override
    public final SessionFactory sessionFactory() {
        return dialect.sessionFactory();
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
                insertDomain(tableMeta, beanWrapper, tableMeta.fieldCollection(), null)
        );

    }


    @Override
    public final List<SQLWrapper> insert(Insert insert) {
        List<SQLWrapper> list;
        if (insert instanceof InnerStandardInsert) {
            list = standardInsert((InnerStandardInsert) insert);
        } else if (insert instanceof InnerStandardSubQueryInsert) {
            list = standardSubQueryInsert((InnerStandardSubQueryInsert) insert);
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
            list = standardBatchInsert((InnerStandardBatchInsert) insert);
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

        } else if (update instanceof InnerStandardSingleUpdate) {

            CriteriaCounselor.assertStandardUpdate((InnerStandardSingleUpdate) update);
            list = Collections.singletonList(
                    standardSingleUpdate((InnerStandardSingleUpdate) update, visible)
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
    public List<SQLWrapper> delete(Delete.DeleteAble singleDeleteAble, Visible visible) {
       /* InnerDelete deleteAble = (InnerDelete) singleDeleteAble;
        TableMeta<?> tableMeta = deleteAble.tableMeta();
        List<SQLWrapper> list;
        if (tableMeta.parentMeta() == null) {
            list = createDeleteForSimple(deleteAble, visible);
        } else {
            list = createDeleteForChild(deleteAble, visible);
        }*/
        return Collections.emptyList();
    }

    /*################################## blow package batchInsert template method ##################################*/

    protected InsertContext createInsertContext(InnerInsert insert) {
        InsertContext context;
        if (insert == null) {
            context = new StandardInsertContext(this, this.dialect);
        } else {
            context = new StandardInsertContext(this, this.dialect, insert);
        }
        return context;
    }


    protected UpdateContext createUpdateContext(InnerUpdate update) {
        return new StandardUpdateContext(this, this.dialect, (InnerStandardSingleUpdate) update);
    }

    protected ChildDomainUpdateContext createChildDomainUpdateContext(InnerDomainUpdate update
            , List<FieldMeta<?, ?>> parentFieldList) {
        Collection<FieldMeta<?, ?>> parentFields;
        if (parentFieldList.size() < 3) {
            parentFields = parentFieldList;
        } else {
            parentFields = new HashSet<>(parentFieldList);
        }
        return new StandardChildDomainUpdateContext(this, this.dialect, (InnerStandardDomainUpdate) update, parentFields);
    }

    protected ParentDomainUpdateContext createParentDomainUpdateContext(InnerDomainUpdate update
            , List<FieldMeta<?, ?>> childFieldList) {
        Collection<FieldMeta<?, ?>> parentFields;
        if (childFieldList.size() < 3) {
            parentFields = childFieldList;
        } else {
            parentFields = new HashSet<>(childFieldList);
        }
        return new ParentDomainUpdateContextImpl(this, this.dialect, (InnerStandardDomainUpdate) update, parentFields);
    }

    protected abstract List<SQLWrapper> specialInsert(InnerSpecialInsert insert);

    protected abstract List<BatchSQLWrapper> specialBatchInsert(InnerSpecialInsert insert);

    protected abstract List<SQLWrapper> specialUpdate(InnerSpecialUpdate update, Visible visible);

    protected void standardSingleUpdateModifier(UpdateContext context) {
        context.sqlBuilder()
                .append(" ");
    }

    protected String subQueryParentAlias(String parentTableName) {
        Random random = new Random();
        return "_" + parentTableName + random.nextInt(4) + "_";
    }

    protected abstract boolean tableAliasAfterAs();



    /*################################## blow protected method ##################################*/

    /*################################## blow private batchInsert method ##################################*/

    /**
     * @return a modifiable list
     */
    private List<SQLWrapper> standardInsert(InnerStandardInsert insert) {
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
                    insertDomain(tableMeta, beanWrapper, fieldMetas, insert)
            );
        }
        return sqlWrapperList;
    }

    private List<SQLWrapper> standardSubQueryInsert(InnerStandardSubQueryInsert insert) {
        CriteriaCounselor.assertInsert(insert);

        TableMeta<?> tableMeta = insert.tableMeta();
        List<FieldMeta<?, ?>> fieldMetaList = insert.fieldList();
        int subQuerySelectionCount = DMLUtils.selectionCount(insert.subQuery());

        if (subQuerySelectionCount != fieldMetaList.size()) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                    , "selection size[%s] of SubQuery and targetFieldList size[%s] not match."
                    , subQuerySelectionCount, fieldMetaList.size());
        }

        InsertContext context = createInsertContext(insert);
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
        return Collections.singletonList(DMLUtils.createSQLWrapper(context));
    }


    private List<SQLWrapper> insertDomain(TableMeta<?> tableMeta, BeanWrapper entityWrapper
            , @Nullable Collection<? extends FieldMeta<?, ?>> fieldMetas
            , @Nullable InnerInsert innerInsert) {

        List<SQLWrapper> sqlWrapperList;
        switch (tableMeta.mappingMode()) {
            case SIMPLE:
                sqlWrapperList = Collections.singletonList(
                        createInsertForSimple(tableMeta, entityWrapper, fieldMetas, innerInsert)
                );
                break;
            case CHILD:
                sqlWrapperList = createInsertForChild((ChildTableMeta<?>) tableMeta
                        , entityWrapper, fieldMetas, innerInsert);
                break;
            case PARENT:
                sqlWrapperList = Collections.singletonList(
                        createInsertForParent(tableMeta, entityWrapper, fieldMetas, innerInsert)
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
            , @Nullable InnerInsert innerInsert) {

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
                createInsertForParent(parentMeta, beanWrapper, parentFields, innerInsert)
        );

        InsertContext context = createInsertContext(innerInsert);
        DMLUtils.createInsertForSimple(childMeta, childFields, beanWrapper, context);
        sqlWrapperList.add(DMLUtils.createSQLWrapper(context));

        return sqlWrapperList;
    }


    /**
     * @return a modifiable list
     */
    private SQLWrapper createInsertForParent(TableMeta<?> tableMeta, BeanWrapper beanWrapper
            , @Nullable Collection<? extends FieldMeta<?, ?>> fieldMetas, @Nullable InnerInsert innerInsert) {
        return createInsertForSimple(tableMeta, beanWrapper, fieldMetas, innerInsert);
    }

    private SQLWrapper createInsertForSimple(TableMeta<?> tableMeta, BeanWrapper beanWrapper
            , @Nullable Collection<? extends FieldMeta<?, ?>> fieldMetas, @Nullable InnerInsert innerInsert) {
        Collection<? extends FieldMeta<?, ?>> targetFields = fieldMetas;
        if (targetFields == null) {
            // unmodifiableCollection for avoid generic error
            targetFields = tableMeta.fieldCollection();
        }
        InsertContext context = createInsertContext(innerInsert);
        DMLUtils.createInsertForSimple(tableMeta, targetFields, beanWrapper, context);

        SQLWrapper sqlWrapper;
        GeneratorMeta generatorMeta = tableMeta.primaryKey().generator();

        if (generatorMeta != null
                && ClassUtils.isAssignable(PostMultiGenerator.class, generatorMeta.type())) {
            sqlWrapper = DMLUtils.createSQLWrapper(context, beanWrapper);
        } else {
            sqlWrapper = DMLUtils.createSQLWrapper(context);
        }
        return sqlWrapper;
    }

    private List<BatchSQLWrapper> standardBatchInsert(InnerStandardBatchInsert insert) {
        CriteriaCounselor.assertInsert(insert);

        TableMeta<?> tableMeta = insert.tableMeta();
        List<SQLWrapper> sqlWrapperList;
        switch (tableMeta.mappingMode()) {
            case SIMPLE:
                sqlWrapperList = Collections.singletonList(
                        standardBatchInsertForSimple(insert)
                );
                break;
            case PARENT:
                sqlWrapperList = Collections.singletonList(
                        standardBatchInsertForParent(insert)
                );
                break;
            case CHILD:
                sqlWrapperList = standardBatchInsertForChild(insert);
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


    private SQLWrapper standardBatchInsertForSimple(InnerStandardBatchInsert insert) {
        InsertContext context = createInsertContext(insert);
        DMLUtils.createBatchInsertForSimple(insert.tableMeta(), context);
        return DMLUtils.createSQLWrapper(context);
    }

    private SQLWrapper standardBatchInsertForParent(InnerStandardBatchInsert insert) {
        return standardBatchInsertForSimple(insert);
    }

    private List<SQLWrapper> standardBatchInsertForChild(InnerStandardBatchInsert insert) {
        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) insert.tableMeta();
        TableMeta<?> parentMeta = childMeta.parentMeta();

        List<SQLWrapper> sqlWrapperList = new ArrayList<>(2);
        // 1. parent sql wrapper
        sqlWrapperList.add(
                standardBatchInsertForParent(insert)
        );
        //2. child sql wrapper
        InsertContext context = createInsertContext(insert);
        DMLUtils.createBatchInsertForSimple(insert.tableMeta(), context);
        sqlWrapperList.add(
                DMLUtils.createSQLWrapper(context)
        );
        return Collections.unmodifiableList(sqlWrapperList);
    }

    /*################################## blow update private method ##################################*/

    private SQLWrapper standardSingleUpdate(InnerStandardSingleUpdate update, Visible visible) {
        UpdateContext context = createUpdateContext(update);

        StringBuilder builder = context.sqlBuilder().append("UPDATE");
        standardSingleUpdateModifier(context);
        // append table name and alias
        context.appendTable(update.tableMata());
        if (tableAliasAfterAs()) {
            builder.append(" AS");
        }
        context.appendText(update.tableAlias());
        // set clause
        standardSingleUpdateSetClause(context, update.tableMata(), update.tableAlias()
                , update.targetFieldList(), update.valueExpList());
        // where clause
        singleTableWhereClause(context, update.tableMata(), update.tableAlias()
                , update.predicateList(), visible);

        return context.build();
    }

    private List<SQLWrapper> standardDomainUpdate(InnerStandardDomainUpdate update, Visible visible) {
        List<SQLWrapper> list;
        switch (update.tableMata().mappingMode()) {
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
                        , update.tableMata().mappingMode()));

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
                visibleConstantPredicateForSimple(context, tableMeta, tableAlias, visible);
                break;
            case CHILD:
                visibleSubQueryPredicateForChild(context, (ChildTableMeta<?>) tableMeta, tableAlias, visible);
                break;
            default:
                throw new IllegalArgumentException(String.format("unknown MappingMode[%s].", tableMeta.mappingMode()));
        }
    }

    private void visibleConstantPredicateForSimple(SQLContext context
            , TableMeta<?> tableMeta, String tableAlias, Visible visible) {

        switch (visible) {
            case ONLY_VISIBLE:
                visibleConstantPredicate(context, Boolean.TRUE, tableMeta, tableAlias);
                break;
            case ONLY_NON_VISIBLE:
                visibleConstantPredicate(context, Boolean.FALSE, tableMeta, tableAlias);
                break;
            case BOTH:
                break;
            default:
                throw new IllegalArgumentException(String.format("unknown Visible[%s]", visible));
        }
    }

    private void visibleSubQueryPredicateForChild(SQLContext context
            , ChildTableMeta<?> childMeta, String childAlias, Visible visible) {
        if (visible == Visible.BOTH) {
            return;
        }

        ParentTableMeta<?> parentMeta = childMeta.parentMeta();

        final String parentAlias = subQueryParentAlias(parentMeta.tableName());
        // append exists SubQuery
        StringBuilder builder = context.sqlBuilder()
                .append(" AND EXISTS ( SELECT");

        context.appendField(parentAlias, parentMeta.primaryKey());
        // from clause
        builder.append(" FROM");
        context.appendParentTableOf(childMeta);

        if (tableAliasAfterAs()) {
            builder.append(" AS");
        }
        context.appendText(parentAlias);
        // where clause
        builder.append(" WHERE");
        context.appendField(parentAlias, parentMeta.primaryKey());
        builder.append(" =");
        context.appendField(childAlias, childMeta.primaryKey());

        // visible predicate
        switch (visible) {
            case ONLY_VISIBLE:
                visibleConstantPredicate(context, Boolean.TRUE, parentMeta, parentAlias);
                break;
            case ONLY_NON_VISIBLE:
                visibleConstantPredicate(context, Boolean.FALSE, parentMeta, parentAlias);
                break;
            default:
                throw new IllegalArgumentException(String.format("unknown Visible[%s]", visible));

        }
        builder.append(" )");
    }

    private void visibleConstantPredicate(SQLContext context, Boolean visible
            , TableMeta<?> tableMeta, String tableAlias) {

        final FieldMeta<?, ?> visibleField = tableMeta.getField(TableMeta.VISIBLE);

        StringBuilder builder = context.sqlBuilder()
                .append(" AND");
        context.appendField(tableAlias, visibleField);
        builder.append(" =");
        SQLS.constant(visible, visibleField.mappingType())
                .appendSQL(context);

    }

    private List<SQLWrapper> standardDomainUpdateDispatcher(InnerStandardDomainUpdate update, Visible visible) {
        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) update.tableMata();
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

        // firstly,  child table update sql
        final SQLWrapper childSqlWrapper = standardDomainUpdateForChild(update, childFieldList
                , childValueList, parentFieldList, visible);
        // secondly, parent table update sql,maybe contains select
        final List<SQLWrapper> parentSqlList = standardDomainUpdateForParent(update, parentFieldList
                , parentValueList, childFieldList, visible);
        // merge childSqlWrapper and parentSqlList
        return DMLUtils.createDomainUpdateSQLWrapperList(childSqlWrapper, parentSqlList);
    }

    private List<SQLWrapper> standardDomainUpdateForParent(InnerStandardDomainUpdate update
            , List<FieldMeta<?, ?>> parentFieldList, List<Expression<?>> parentValueList,
                                                           List<FieldMeta<?, ?>> childFieldList, Visible visible) {

        ParentDomainUpdateContext context = createParentDomainUpdateContext(update, childFieldList);

        StringBuilder builder = context.sqlBuilder().append("UPDATE");
        standardSingleUpdateModifier(context);
        // append table name and alias
        context.appendTable(update.tableMata());
        if (tableAliasAfterAs()) {
            builder.append(" AS");
        }
        context.appendText(update.tableAlias());
        // set clause
        standardSingleUpdateSetClause(context, update.tableMata(), update.tableAlias()
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
                    DMLUtils.createQueryChildBeanSQLWrapper(update, childFieldList, this.dialect)
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
            , List<FieldMeta<?, ?>> parentFieldList, Visible visible) {

        ChildDomainUpdateContext context = createChildDomainUpdateContext(update, parentFieldList);
        // update clause
        StringBuilder builder = context.sqlBuilder().append("UPDATE");
        // eg: oracle need add 'ONLY' prefix.
        standardSingleUpdateModifier(context);
        // append table name and alias
        context.appendTable(update.tableMata());
        if (tableAliasAfterAs()) {
            builder.append(" AS");
        }
        context.appendText(update.tableAlias());
        // set clause
        standardSingleUpdateSetClause(context, update.tableMata(), update.tableAlias()
                , childFieldList, childValueList);

        // merge sql fragment 'id = ?' and update.predicateList()
        List<IPredicate> mergedPredicateList = DMLUtils.mergeDomainUpdatePredicateList(
                update.predicateList(), context.tableMeta().primaryKey(), update.primaryKeyValue());

        // where clause with mergedPredicateList
        singleTableWhereClause(context, context.tableMeta(), context.tableAlias()
                , mergedPredicateList, visible);

        return context.build();
    }


    /**
     * @return a unmodifiable list
     */
    private List<SQLWrapper> createDeleteForSimple(InnerDelete deleteAble, Visible visible) {

       /* final SQLContext context = new DefaultSQLContext(this, SQLStatement.DELETE);
        TableMeta<?> tableMeta = deleteAble.tableMeta();

        final String tableName = this.sql.quoteIfNeed(tableMeta.tableName());
        // 1. singleDelete clause
        appendDeleteClause(context, tableName, deleteAble);
        // 2. where clause
        appendDeleteWhereClause(context, deleteAble, visible);
        return Collections.singletonList(
                SQLWrapper.build(context.sqlBuilder().toString(), context.paramList())
        );*/
        return Collections.emptyList();
    }

    private void appendDeleteClause(SQLContext context, String tableName, InnerDelete deleteAble) {
        context.sqlBuilder()
                .append("DELETE FROM ")
                .append(tableName);

    }

    private void appendDeleteWhereClause(SQLContext context, InnerDelete deleteAble, Visible visible) {

        /*List<IPredicate> predicateList = deleteAble.predicateList();
        Assert.notEmpty(predicateList, "no where clause forbidden by army");

        StringBuilder builder = context.sqlBuilder()
                .appendText(" WHERE");

        for (Iterator<IPredicate> iterator = predicateList.iterator(); iterator.hasNext(); ) {
            iterator.next().appendSQL(context);
            if (iterator.hasNext()) {
                builder.appendText(" AND");
            }
        }
        Boolean visibleValue = visible.getValue();
        if (visibleValue != null) {
            FieldMeta<?, ?> visibleField = deleteAble.tableMeta().getField(TableMeta.VISIBLE);
            String textValue = visibleField.mappingType().nonNullTextValue(visibleValue);
            builder.appendText(" AND ")
                    .appendText(this.quoteIfNeed(visibleField.fieldName()))
                    .appendText(" = ")
                    .appendText(DialectUtils.quoteIfNeed(visibleField.mappingType(), textValue))
            ;
        }*/

    }

    /**
     * @return a unmodifiable list
     */
    private List<SQLWrapper> createObjectUpdate(InnerObjectUpdate updateAble, Visible visible) {
        TableMeta<?> childMeta = null, parentMeta = childMeta.parentMeta();
        Assert.notNull(parentMeta, () -> String.format("Table[%s] not child mode", childMeta.tableName()));

        ObjectUpdateContextImpl context = new ObjectUpdateContextImpl(this, this.dialect, childMeta, null);
        // 1. singleUpdate clause
        appendObjectUpdateClause(context);
        // 2. set clause
        //standardSingleUpdateSetClause(context, updateAble.targetFieldList(), updateAble.valueExpList());
        // 3. where clause
        // singleTableWhereClause(context, updateAble.predicateList());
        //4. appendText child visible
        // appendVisiblePredicate(parentMeta, context, context.safeParentAlias(), visible);

        return Collections.singletonList(
                SQLWrapper.build(context.builder.toString(), context.paramList)
        );
    }

    private void appendObjectUpdateClause(ObjectUpdateContextImpl context) {
        TableMeta<?> childMeta = context.tableMeta(), parentMeta = childMeta.parentMeta();
        Assert.notNull(parentMeta, () -> String.format("Table[%s] not child mode", childMeta.tableName()));

        StringBuilder builder = context.sqlBuilder()
                .append("UPDATE ")
                .append(this.dialect.quoteIfNeed(childMeta.tableName()));

        if (StringUtils.hasText(context.safeAlias())) {
            builder.append(" AS ")
                    .append(context.safeAlias());
        }
        builder.append(" JOIN ")
                .append(this.dialect.quoteIfNeed(parentMeta.tableName()))
                .append(" AS ")
                .append(context.safeParentAlias())
                .append(" ON ")
                .append(context.safeAlias())
                .append(".")
                .append(TableMeta.ID)
                .append(" = ")
                .append(context.safeParentAlias())
                .append(".")
                .append(TableMeta.ID)
        ;
    }


    private void appendOrderByClause(SQLContext context, List<Expression<?>> orderExpList, List<Boolean> ascExpList) {
        if (CollectionUtils.isEmpty(orderExpList)) {
            return;
        }
        Assert.isTrue(orderExpList.size() == ascExpList.size(), "orderExpList size havingAnd ascExpList size not match.");

        StringBuilder builder = context.sqlBuilder()
                .append(" ORDER BY");

        Expression<?> orderExp;
        Boolean ascExp;
        final int size = orderExpList.size();
        for (int i = 0; i < size; i++) {
            orderExp = orderExpList.get(i);
            orderExp.appendSQL(context);
            ascExp = ascExpList.get(i);

            if (Boolean.TRUE.equals(ascExp)) {
                builder.append(" ASC");
            } else if (Boolean.FALSE.equals(ascExp)) {
                builder.append(" DESC");
            }
            if (i < size - 1) {
                builder.append(",");
            }
        }
    }

    private void appendLimitClause(SQLContext context, int rowCount) {
        if (rowCount > -1) {
            context.sqlBuilder().append(" LIMIT ?");
            context.appendParam(
                    ParamWrapper.build(MappingFactory.getDefaultMapping(Integer.class), rowCount)
            );
        }
    }


    private void assertFieldAndValueExpressionMatch(FieldMeta<?, ?> fieldMeta, Expression<?> valueExp) {
        if (!fieldMeta.nullable()
                && valueExp instanceof ParamExpression
                && ((ParamExpression<?>) valueExp).value() == null) {
            throw new IllegalArgumentException(String.format("domain[%s] mapping prop[%s] non-null"
                    , fieldMeta.tableMeta().javaType().getName()
                    , fieldMeta.propertyName()));
        }
    }


}
