package io.army.dialect;

import io.army.ErrorCode;
import io.army.ShardingMode;
import io.army.beans.DomainWrapper;
import io.army.beans.ReadonlyWrapper;
import io.army.boot.FieldValuesGenerator;
import io.army.criteria.*;
import io.army.criteria.impl.CriteriaCounselor;
import io.army.criteria.impl.SQLS;
import io.army.criteria.impl.inner.*;
import io.army.domain.IDomain;
import io.army.meta.*;
import io.army.util.Assert;
import io.army.wrapper.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;

//@SuppressWarnings("unused")
public abstract class AbstractDML extends AbstractDMLAndDQL implements DML {

    public AbstractDML(InnerDialect dialect) {
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

        List<SimpleBatchSQLWrapper> list;
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
    public final List<SimpleSQLWrapper> update(Update update, final Visible visible) {
        Assert.isTrue(update.prepared(), "Update don't invoke asUpdate() method.");

        List<SimpleSQLWrapper> list;
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
    public final List<SimpleSQLWrapper> delete(Delete delete, final Visible visible) {
        Assert.isTrue(delete.prepared(), "Delete don't invoke asDelete() method.");

        List<SimpleSQLWrapper> list;
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


    protected abstract boolean singleDeleteHasTableAlias();

    /*################################## blow multiInsert template method ##################################*/

    protected void assertSpecialGeneralInsert(InnerSpecialGeneralInsert insert) {
        throw new UnsupportedOperationException(String.format("dialect [%s] not support special general multiInsert."
                , sqlDialect())
        );
    }

    protected void assertSpecialBatchInsert(InnerSpecialBatchInsert insert) {
        throw new UnsupportedOperationException(String.format("dialect [%s] not support special abstract multiInsert."
                , sqlDialect())
        );
    }

    protected void assertSpecialSubQueryInsert(InnerSpecialSubQueryInsert insert) {
        throw new UnsupportedOperationException(String.format("dialect [%s] not support special sub query multiInsert."
                , sqlDialect())
        );
    }

    protected List<SQLWrapper> specialGeneralInsert(InnerSpecialGeneralInsert insert, Visible visible) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special general multiInsert."
                , sqlDialect())
        );
    }

    protected List<SimpleBatchSQLWrapper> specialBatchInsert(InnerSpecialBatchInsert insert, Visible visible) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special batch multiInsert."
                , sqlDialect())
        );
    }

    protected List<SQLWrapper> specialSubQueryInsert(InnerSpecialSubQueryInsert insert, Visible visible) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special sub query multiInsert."
                , sqlDialect())
        );
    }

    protected InsertContext createBeanInsertContext(InnerInsert insert, ReadonlyWrapper readonlyWrapper
            , final Visible visible) {
        InsertContext context;
        if (insert instanceof InnerStandardInsert) {
            context = AbstractStandardInsertContext.buildGeneric(this.dialect, visible, readonlyWrapper
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

    protected List<SimpleSQLWrapper> specialDomainUpdate(InnerSpecialDomainUpdate domainUpdate, Visible visible) {
        throw new UnsupportedOperationException(String.format("dialect [%s] not support special domain update."
                , sqlDialect())
        );
    }

    protected List<SimpleSQLWrapper> specialUpdate(InnerSpecialUpdate update, Visible visible) {
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

    protected List<SimpleSQLWrapper> specialDomainDeleteDispatcher(InnerSpecialDomainDelete delete, Visible visible) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special domain delete."
                , sqlDialect())
        );
    }

    protected List<SimpleSQLWrapper> specialDelete(InnerSpecialDelete delete, Visible visible) {
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

        final FieldValuesGenerator valuesGenerator = this.dialect.sessionFactory().fieldValuesGenerator();
        DomainWrapper domainWrapper;

        for (IDomain domain : domainList) {
            // 2. create required values.
            domainWrapper = valuesGenerator.createValues(tableMeta, domain);
            sqlWrapperList.add(
                    // 3. create sql of domain
                    insertDomain(tableMeta, domainWrapper, fieldMetaSet, insert, visible)
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


    private SQLWrapper insertDomain(TableMeta<?> tableMeta, DomainWrapper domainWrapper
            , Collection<FieldMeta<?, ?>> fieldMetas
            , InnerStandardInsert innerInsert, final Visible visible) {

        SQLWrapper sqlWrapper;
        switch (tableMeta.mappingMode()) {
            case PARENT:
            case SIMPLE:
                sqlWrapper = createInsertForSimple(tableMeta, domainWrapper, fieldMetas, innerInsert, visible);
                break;
            case CHILD:
                sqlWrapper = createInsertForChild((ChildTableMeta<?>) tableMeta
                        , domainWrapper, fieldMetas, innerInsert, visible);
                break;
            default:
                throw DialectUtils.createMappingModeUnknownException(tableMeta.mappingMode());

        }
        return sqlWrapper;
    }

    /**
     * @param mergedFields merged by {@link DMLUtils#mergeInsertFields(TableMeta, Dialect, Collection)}
     */
    private ChildSQLWrapper createInsertForChild(final ChildTableMeta<?> childMeta
            , DomainWrapper beanWrapper, Collection<FieldMeta<?, ?>> mergedFields
            , InnerStandardInsert insert, final Visible visible) {

        Assert.notEmpty(mergedFields, "mergedFields must not empty.");

        final ParentTableMeta<?> parentMeta = childMeta.parentMeta();
        final Set<FieldMeta<?, ?>> parentFields = new HashSet<>(), childFields = new HashSet<>();
        // 1.divide fields
        DialectUtils.divideFields(childMeta, mergedFields, parentFields, childFields);
        if (parentFields.isEmpty() || childFields.isEmpty()) {
            throw new ArmyCriteriaException(ErrorCode.CRITERIA_ERROR
                    , "multiInsert sql error,ChildMeta[%s] parent fields[%s] or child fields[%s]  is empty."
                    , childMeta, parentFields, childFields);
        }

        //2.  create parent sql.
        StandardInsertContext parentContext = StandardInsertContext.build(this.dialect, visible
                , beanWrapper, parentMeta);
        DMLUtils.createStandardInsertForSimple(parentMeta, childMeta, parentFields, beanWrapper, parentContext);

        //3. create child sql.
        StandardInsertContext childContext = StandardInsertContext.build(this.dialect, visible
                , beanWrapper, childMeta);
        DMLUtils.createStandardInsertForSimple(childMeta, childMeta, childFields, beanWrapper, childContext);

        return ChildSQLWrapper.build(parentContext.build(), childContext.build());
    }

    /**
     * @param mergedFields merged by {@link DMLUtils#mergeInsertFields(TableMeta, Dialect, Collection)}
     */
    private SimpleSQLWrapper createInsertForSimple(TableMeta<?> tableMeta, DomainWrapper beanWrapper
            , Collection<FieldMeta<?, ?>> mergedFields, InnerStandardInsert insert
            , final Visible visible) {

        StandardInsertContext context = StandardInsertContext.build(this.dialect, visible, beanWrapper, tableMeta);

        DMLUtils.createStandardInsertForSimple(tableMeta, tableMeta, mergedFields, beanWrapper, context);

        return context.build();
    }


    private List<SimpleBatchSQLWrapper> standardBatchInsert(InnerStandardBatchInsert insert, final Visible visible) {

        TableMeta<?> tableMeta = insert.tableMeta();
        List<SimpleSQLWrapper> sqlWrapperList;
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
                , this.dialect.sessionFactory().fieldValuesGenerator()
        );
    }


    private SimpleSQLWrapper standardBatchInsertForSimple(InnerStandardBatchInsert insert, final Visible visible) {
        TableMeta<?> tableMeta = insert.tableMeta();
        // 1.merge fields
        Set<FieldMeta<?, ?>> fieldMetaSet = DMLUtils.mergeInsertFields(tableMeta, this.dialect, insert.fieldList());

        InsertContext context = createBatchInsertContext(insert, visible);
        // single table multiInsert sql
        DMLUtils.createBatchInsertForSimple(tableMeta, fieldMetaSet, context);
        return context.build();
    }

    private List<SimpleSQLWrapper> standardBatchInsertForChild(InnerStandardBatchInsert insert, final Visible visible) {
        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) insert.tableMeta();
        ParentTableMeta<?> parentMeta = childMeta.parentMeta();

        Set<FieldMeta<?, ?>> parentFieldSet = new HashSet<>(), childFieldSet = new HashSet<>();
        // 1.  separate target fields
        DialectUtils.divideFields(childMeta, insert.fieldList(), parentFieldSet, childFieldSet);
        // 2. merge fields
        parentFieldSet = DMLUtils.mergeInsertFields(parentMeta, this.dialect, parentFieldSet);
        childFieldSet = DMLUtils.mergeInsertFields(childMeta, this.dialect, childFieldSet);
        // separate fields end.

        List<SimpleSQLWrapper> sqlWrapperList = new ArrayList<>(2);
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

    private SimpleSQLWrapper standardSimpleUpdate(InnerUpdate update, TableMeta<?> tableMeta, String tableAlias
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

    private List<SimpleSQLWrapper> standardDomainUpdate(InnerStandardDomainUpdate update, Visible visible) {
        List<SimpleSQLWrapper> list;
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

    private void setClauseFieldsManagedByArmy(TableContextSQLContext context, TableMeta<?> tableMeta, String tableAlias) {
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
            SQLS.param(now.toLocalDateTime(), updateTimeField.mappingMeta())
                    .appendSQL(context);
        } else if (updateTimeField.javaType() == ZonedDateTime.class) {
            if (!this.dialect.supportZoneId()) {
                throw new MetaException(ErrorCode.META_ERROR
                        , "dialec[%s]t not supported zone.", this.dialect.sqlDialect());
            }
            SQLS.param(now, updateTimeField.mappingMeta())
                    .appendSQL(context);
        } else {
            throw new MetaException(ErrorCode.META_ERROR
                    , "createTime or updateTime only support LocalDateTime or ZonedDateTime,please check.");
        }
    }

    private void simpleTableWhereClause(TableContextSQLContext context, TableMeta<?> tableMeta, String tableAlias
            , List<IPredicate> predicateList) {

        final boolean needAppendVisible = DialectUtils.needAppendVisible(tableMeta);
        if (!predicateList.isEmpty() || needAppendVisible) {
            context.sqlBuilder()
                    .append(" WHERE");
        }

        if (!predicateList.isEmpty()) {
            DialectUtils.appendPredicateList(predicateList, context);
        }
        if (needAppendVisible) {
            appendVisiblePredicate(tableMeta, tableAlias, context);
        }
    }


    private List<SimpleSQLWrapper> standardDomainUpdateChildDispatcher(InnerStandardDomainUpdate update
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
        final List<SimpleSQLWrapper> parentSqlList = standardDomainUpdateForParent(update, parentFieldList
                , parentValueList, childFieldList, visible);

        //3.  child table update sql
        final SimpleSQLWrapper childSqlWrapper = standardDomainUpdateForChild(update, childFieldList
                , childValueList, parentFieldList, visible);
        // merge childSqlWrapper and parentSqlList
        return DMLUtils.mergeDomainSQLWrappers(childSqlWrapper, parentSqlList);
    }

    private List<SimpleSQLWrapper> standardDomainUpdateForParent(InnerStandardDomainUpdate update
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
                update.predicateList(), context.tableMeta().id(), update.primaryKeyValue());

        // where clause with mergedPredicateList
        simpleTableWhereClause(context, context.tableMeta(), update.tableAlias()
                , mergedPredicateList);

        SimpleSQLWrapper parentSQLWrapper = context.build();

        List<SimpleSQLWrapper> sqlWrapperList;
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

    private SimpleSQLWrapper standardDomainUpdateForChild(InnerStandardDomainUpdate update
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
                update.predicateList(), context.tableMeta().id(), update.primaryKeyValue());

        // where clause with mergedPredicateList
        simpleTableWhereClause(context, context.tableMeta(), context.tableAlias()
                , mergedPredicateList);

        return context.build();
    }

    /*################################## blow delete private method ##################################*/

    private SimpleSQLWrapper standardSingleDelete(InnerDelete delete, TableMeta<?> tableMeta, String tableAlias
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


    private List<SimpleSQLWrapper> standardDomainDeleteDispatcher(InnerStandardDomainDelete delete, final Visible visible) {
        List<SimpleSQLWrapper> sqlWrapperList;
        switch (delete.tableMeta().mappingMode()) {
            case PARENT:
            case SIMPLE:
                sqlWrapperList = Collections.singletonList(
                        standardSingleDelete(delete, delete.tableMeta(), delete.tableAlias(), visible)
                );
                break;
            case CHILD:
                final SimpleSQLWrapper childSql = standardDomainDeleteForChild(delete, visible);
                final List<SimpleSQLWrapper> parentSqlList = standardDomainDeleteForParent(delete, visible);
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


    private SimpleSQLWrapper standardDomainDeleteForChild(InnerStandardDomainDelete delete, final Visible visible) {
        ChildDomainDeleteContext context = createChildDomainDeleteContext(delete, visible);
        doStandardDomainDelete(context, delete, visible);
        return context.build();
    }

    private List<SimpleSQLWrapper> standardDomainDeleteForParent(InnerStandardDomainDelete delete, final Visible visible) {
        List<SimpleSQLWrapper> parentSqlList = new ArrayList<>(2);
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
                delete.predicateList(), context.tableMeta().id(), delete.primaryKeyValue());

        // where clause with mergedPredicateList
        simpleTableWhereClause(context, context.tableMeta(), context.tableAlias()
                , mergedPredicateList);
    }


}
