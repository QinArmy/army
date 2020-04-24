package io.army.dialect;

import io.army.ErrorCode;
import io.army.beans.BeanWrapper;
import io.army.boot.FieldValuesGenerator;
import io.army.criteria.*;
import io.army.criteria.impl.CriteriaCounselor;
import io.army.criteria.impl.SQLS;
import io.army.criteria.impl.inner.*;
import io.army.domain.IDomain;
import io.army.generator.PostMultiGenerator;
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

    protected InsertContext createInsertContext(InnerInsert insert, final Visible visible) {
        return new StandardInsertContext(this.dialect, visible, insert);
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

        TableMeta<?> tableMeta = insert.tableMeta();
        // 1. get target fields.
        Collection<FieldMeta<?, ?>> fieldMetas = insert.fieldList();
        if (CollectionUtils.isEmpty(fieldMetas)) {
            fieldMetas = Collections.unmodifiableCollection(tableMeta.fieldCollection());
        }

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
                    insertDomain(tableMeta, beanWrapper, fieldMetas, insert, visible)
            );
        }
        return sqlWrapperList;
    }

    private List<SQLWrapper> standardSubQueryInsert(InnerStandardSubQueryInsert insert, final Visible visible) {
        CriteriaCounselor.assertStandardSubQueryInsert(insert);

        TableMeta<?> tableMeta = insert.tableMeta();
        List<FieldMeta<?, ?>> fieldMetaList = insert.fieldList();
        int subQuerySelectionCount = DMLUtils.selectionCount(insert.subQuery());

        if (subQuerySelectionCount != fieldMetaList.size()) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                    , "selection size[%s] of SubQuery and targetFieldList size[%s] not match."
                    , subQuerySelectionCount, fieldMetaList.size());
        }

        DMLContext context = createInsertContext(insert, visible);
        StringBuilder builder = context.sqlBuilder().append("INSERT INTO ");
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
            , Collection<FieldMeta<?, ?>> fieldMetas
            , InnerInsert innerInsert, final Visible visible) {

        List<SQLWrapper> sqlWrapperList;
        switch (tableMeta.mappingMode()) {
            case SIMPLE:
                sqlWrapperList = Collections.singletonList(
                        createInsertForSimple(tableMeta, entityWrapper, fieldMetas, innerInsert, visible)
                );
                break;
            case PARENT:
                sqlWrapperList = Collections.singletonList(
                        createInsertForParent(tableMeta, entityWrapper, fieldMetas, innerInsert, visible)
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


    private List<SQLWrapper> createInsertForChild(ChildTableMeta<?> childMeta
            , BeanWrapper beanWrapper, Collection<FieldMeta<?, ?>> fieldMetas
            , InnerInsert insert, final Visible visible) {

        TableMeta<?> parentMeta = childMeta.parentMeta();
        Collection<FieldMeta<?, ?>> childFields, parentFields;

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

        List<SQLWrapper> sqlWrapperList = new ArrayList<>(2);
        // firstly, add parent sql.
        sqlWrapperList.add(
                createInsertForParent(parentMeta, beanWrapper, parentFields, insert, visible)
        );

        InsertContext context = createInsertContext(insert, visible);
        DMLUtils.createInsertForSimple(childMeta, childFields, beanWrapper, context);
        // secondly,add child sql.
        sqlWrapperList.add(context.build());
        return sqlWrapperList;
    }


    /**
     * @return a modifiable list
     */
    private SQLWrapper createInsertForParent(TableMeta<?> tableMeta, BeanWrapper beanWrapper
            , Collection<FieldMeta<?, ?>> fieldMetas, InnerInsert innerInsert
            , Visible visible) {
        return createInsertForSimple(tableMeta, beanWrapper, fieldMetas, innerInsert, visible);
    }

    private SQLWrapper createInsertForSimple(TableMeta<?> tableMeta, BeanWrapper beanWrapper
            , Collection<FieldMeta<?, ?>> fieldMetas, InnerInsert innerInsert
            , final Visible visible) {

        InsertContext context = createInsertContext(innerInsert, visible);
        DMLUtils.createInsertForSimple(tableMeta, fieldMetas, beanWrapper, context);

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
        CriteriaCounselor.assertStandardBatchInsert(insert);

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

    private SQLWrapper standardSimpleUpdate(InnerUpdate update, TableMeta<?> tableMeta, String tableAlias
            , final Visible visible) {
        UpdateContext context = createUpdateContext(update, visible);

        StringBuilder builder = context.sqlBuilder().append("UPDATE");
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
                , update.predicateList(), visible);

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

    private void simpleTableWhereClause(ClauseSQLContext context, TableMeta<?> tableMeta, String tableAlias
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
        standardSimpleUpdateSetClause(context, update.tableMeta(), update.tableAlias()
                , parentFieldList, parentValueList);
        // merge sql fragment 'id = ?' and update.predicateList()
        List<IPredicate> mergedPredicateList = DMLUtils.mergeDomainUpdatePredicateList(
                update.predicateList(), context.tableMeta().primaryKey(), update.primaryKeyValue());

        // where clause with mergedPredicateList
        simpleTableWhereClause(context, context.tableMeta(), update.tableAlias()
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
                , mergedPredicateList, visible);

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
        simpleTableWhereClause(context, tableMeta, tableAlias, delete.predicateList(), visible);
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
        simpleTableWhereClause(context, context.tableMeta(), context.tableAlias()
                , mergedPredicateList, visible);
    }


}
