package io.army.dialect;

import io.army.ErrorCode;
import io.army.ShardingMode;
import io.army.beans.DomainWrapper;
import io.army.beans.ReadonlyWrapper;
import io.army.boot.DomainValuesGenerator;
import io.army.criteria.*;
import io.army.criteria.impl.CriteriaCounselor;
import io.army.criteria.impl.inner.*;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.wrapper.ChildSQLWrapper;
import io.army.wrapper.SQLWrapper;
import io.army.wrapper.SimpleSQLWrapper;

import java.util.*;


/**
 * <p>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 */
public abstract class AbstractDML extends AbstractDMLAndDQL implements DML {


    protected AbstractDML(InnerDialect dialect) {
        super(dialect);
    }

    /*################################## blow DML batchInsert method ##################################*/

    /**
     *{@inheritDoc}
     */
    @Override
    public final List<SQLWrapper> valueInsert(Insert insert, @Nullable Set<Integer> domainIndexSet
            , final Visible visible) {
        DialectUtils.assertShardingMode(this.dialect,domainIndexSet);
        Assert.isTrue(insert.prepared(), "Insert don't invoke asInsert() method.");

        List<SQLWrapper> list;
        if (insert instanceof InnerStandardInsert) {
            InnerStandardInsert standardInsert = (InnerStandardInsert) insert;
            // assert implementation class is legal
            CriteriaCounselor.assertStandardInsert(standardInsert);
            if (standardInsert instanceof InnerStandardBatchInsert) {
                list = Collections.singletonList(
                        standardBatchInsert((InnerStandardBatchInsert) standardInsert, visible)
                );
            } else {
                list = standardInsert(standardInsert, domainIndexSet, visible);
            }
        } else if (insert instanceof InnerSpecialValueInsert) {
            InnerSpecialValueInsert generalInsert = (InnerSpecialValueInsert) insert;
            // assert implementation class is legal
            assertSpecialGeneralInsert(generalInsert);
            if (generalInsert instanceof InnerSpecialBatchInsert) {
                list = specialBatchInsert((InnerSpecialBatchInsert) generalInsert, visible);
            } else {
                list = specialGeneralInsert(generalInsert, visible);
            }
        } else {
            throw new IllegalArgumentException(String.format("Insert[%s] type unknown.", insert.getClass().getName()));
        }
        return list;
    }

    @Override
    public final SQLWrapper returningInsert(Insert insert, final Visible visible) {
        Assert.isTrue(insert.prepared(), "Insert don't invoke asInsert() method.");

        SQLWrapper sqlWrapper;
        if (insert instanceof InnerSpecialValueInsert) {

        } else {
            throw new IllegalArgumentException(String.format("Insert[%s] not supported by returningInsert.", insert));
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final SQLWrapper subQueryInsert(Insert insert, final Visible visible) {
        Assert.isTrue(insert.prepared(), "Insert don't invoke asInsert() method.");

        SQLWrapper sqlWrapper;
        if (insert instanceof InnerStandardSubQueryInsert) {
            InnerStandardSubQueryInsert subQueryInsert = (InnerStandardSubQueryInsert) insert;
            // assert implementation class is legal
            CriteriaCounselor.assertStandardSubQueryInsert(subQueryInsert);
            // parse sql
            sqlWrapper = standardSubQueryInsert(subQueryInsert, visible);
        } else if (insert instanceof InnerSpecialSubQueryInsert) {
            InnerSpecialSubQueryInsert subQueryInsert = (InnerSpecialSubQueryInsert) insert;
            // assert implementation class is legal
            assertSpecialSubQueryInsert(subQueryInsert);
            sqlWrapper = specialSubQueryInsert(subQueryInsert, visible);
        } else {
            throw new IllegalArgumentException(String.format("Insert[%s] type unknown.", insert.getClass().getName()));
        }
        return sqlWrapper;
    }


    /*################################## blow update method ##################################*/

    @Override
    public final SQLWrapper update(Update update, final Visible visible) {
        Assert.isTrue(update.prepared(), "Update don't invoke asUpdate() method.");

        SQLWrapper sqlWrapper;
        if (update instanceof InnerStandardUpdate) {
            InnerStandardUpdate standardUpdate = (InnerStandardUpdate) update;
            // assert implementation class is legal
            CriteriaCounselor.assertStandardUpdate(standardUpdate);
            DMLUtils.assertUpdateSetAndWhereClause(standardUpdate);
            if (update instanceof InnerStandardBatchUpdate) {
                sqlWrapper = standardBatchUpdate((InnerStandardBatchUpdate) update, visible);
            }else {
                sqlWrapper = standardGenericUpdate(standardUpdate, visible);
            }

        } else if (update instanceof InnerSpecialUpdate) {
            InnerSpecialUpdate specialUpdate = (InnerSpecialUpdate) update;
            // assert implementation class is legal
            assertSpecialUpdate(specialUpdate);
            DMLUtils.assertUpdateSetAndWhereClause(specialUpdate);
            sqlWrapper = specialUpdate(specialUpdate, visible);
        } else {
            throw new IllegalArgumentException(String.format("Update[%s] not supported by simpleUpdate.", update));
        }
        return sqlWrapper;
    }

    @Override
    public final SQLWrapper delete(Delete delete, final Visible visible) {
        Assert.isTrue(delete.prepared(), "Delete don't invoke asDelete() method.");

        SQLWrapper sqlWrapper;
        if (delete instanceof InnerStandardDelete) {
            InnerStandardDelete standardDelete = (InnerStandardDelete) delete;
            CriteriaCounselor.assertStandardDelete(standardDelete);
            if (standardDelete instanceof InnerStandardBatchDelete) {
                sqlWrapper = standardBatchDelete((InnerStandardBatchDelete) delete, visible);
            }else {
                sqlWrapper = standardGenericDelete(standardDelete, visible);
            }
        } else if (delete instanceof InnerSpecialDelete) {
            InnerSpecialDelete specialDelete = (InnerSpecialDelete) delete;
            assertSpecialDelete(specialDelete);
            if (specialDelete instanceof InnerSpecialBatchDelete) {
                throw new IllegalArgumentException(String.format("Delete[%s] not supported by simpleDelete.", delete));
            }
            sqlWrapper = specialDelete(specialDelete, visible);
        } else {
            throw new IllegalArgumentException(String.format("Delete[%s] not supported by simpleDelete.", delete));
        }
        return sqlWrapper;
    }

    /*################################## blow protected template method ##################################*/


    /*################################## blow multiInsert template method ##################################*/

    protected void assertSpecialGeneralInsert(InnerSpecialValueInsert insert) {
        throw new UnsupportedOperationException(String.format("dialect [%s] not support special general multiInsert."
                , database())
        );
    }

    protected void assertSpecialSubQueryInsert(InnerSpecialSubQueryInsert insert) {
        throw new UnsupportedOperationException(String.format("dialect [%s] not support special sub query multiInsert."
                , database())
        );
    }

    protected List<SQLWrapper> specialGeneralInsert(InnerSpecialValueInsert insert, Visible visible) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special general multiInsert."
                , database())
        );
    }

    protected List<SQLWrapper> specialBatchInsert(InnerSpecialBatchInsert insert, Visible visible) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special batch multiInsert."
                , database())
        );
    }

    protected SQLWrapper specialSubQueryInsert(InnerSpecialSubQueryInsert insert, Visible visible) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special sub query multiInsert."
                , database())
        );
    }



    /*################################## blow update template method ##################################*/


    protected void assertSpecialUpdate(InnerSpecialUpdate update) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special domain update."
                , database())
        );
    }

    protected SQLWrapper specialUpdate(InnerSpecialUpdate update, Visible visible) {
        throw new UnsupportedOperationException(String.format("dialect [%s] not support special update."
                , database())
        );
    }

    /*################################## blow delete template method ##################################*/

    protected void assertSpecialDelete(InnerSpecialDelete delete) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special delete."
                , database())
        );
    }

    protected SQLWrapper specialDelete(InnerSpecialDelete delete, Visible visible) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special delete."
                , database())
        );
    }

    /*################################## blow protected method ##################################*/

    /*################################## blow private batchInsert method ##################################*/

    /**
     * @return a modifiable list
     */
    private List<SQLWrapper> standardInsert(InnerStandardInsert insert, @Nullable Set<Integer> domainIndexSet
            , final Visible visible) {

        final TableMeta<?> tableMeta = insert.tableMeta();
        // 1. merge target fields.
        Set<FieldMeta<?, ?>> fieldMetaSet = DMLUtils.mergeInsertFields(tableMeta, this.dialect, insert.fieldList());

        final List<DomainWrapper> domainWrapperList = insert.wrapperList();
        final List<SQLWrapper> sqlWrapperList = new ArrayList<>(domainWrapperList.size());
        final DomainValuesGenerator generator = this.dialect.sessionFactory().domainValuesGenerator();
        final boolean migrationData = insert.migrationData();
        if (domainIndexSet == null) {
            for (DomainWrapper domainWrapper : domainWrapperList) {
                // 2. create domain required property value .
                generator.createValues(domainWrapper, migrationData);
                sqlWrapperList.add(
                        // 3. create sql of domain
                        insertDomain(domainWrapper, fieldMetaSet, insert, visible)
                );
            }
        } else {
            for (Integer domainIndex : domainIndexSet) {
                DomainWrapper domainWrapper = domainWrapperList.get(domainIndex);
                // 2. create domain required property value .
                generator.createValues(domainWrapper, migrationData);
                sqlWrapperList.add(
                        // 3. create sql of domain
                        insertDomain(domainWrapper, fieldMetaSet, insert, visible)
                );
            }
        }
        return sqlWrapperList;
    }

    private SQLWrapper standardSubQueryInsert(InnerStandardSubQueryInsert insert, final Visible visible) {
        SQLWrapper sqlWrapper;
        if (insert instanceof InnerStandardChildSubQueryInsert) {
            sqlWrapper = standardChildQueryInsert((InnerStandardChildSubQueryInsert) insert, visible);
        } else {
            SubQueryInsertContext context = SubQueryInsertContext.build(insert, this.dialect, visible);
            parseStandardSimpleSubQueryInsert(context, insert.tableMeta(), insert.fieldList(), insert.subQuery());
            sqlWrapper = context.build();
        }
        return sqlWrapper;
    }

    private ChildSQLWrapper standardChildQueryInsert(InnerStandardChildSubQueryInsert insert, final Visible visible) {
        final ChildTableMeta<?> childMeta = insert.tableMeta();
        final ParentTableMeta<?> parentMeta = childMeta.parentMeta();

        // firstly ,parse parent insert sql
        SubQueryInsertContext parentContext = SubQueryInsertContext.buildParent(insert, this.dialect, visible);
        parseStandardSimpleSubQueryInsert(parentContext, parentMeta, insert.parentFieldList(), insert.parentSubQuery());

        // secondly ,parse child insert sql
        SubQueryInsertContext childContext = SubQueryInsertContext.buildChild(insert, this.dialect, visible);
        parseStandardSimpleSubQueryInsert(childContext, childMeta, insert.fieldList(), insert.subQuery());

        return ChildSQLWrapper.build(parentContext.build(), childContext.build());
    }

    private void parseStandardSimpleSubQueryInsert(SubQueryInsertContext context
            , TableMeta<?> physicalTable, List<FieldMeta<?, ?>> fieldMetaList, SubQuery subQuery) {

        DMLUtils.assertSubQueryInsert(fieldMetaList, subQuery);

        StringBuilder builder = context.sqlBuilder().append("INSERT INTO");
        context.appendTable(physicalTable, null);
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
        subQuery.appendSQL(context);
    }


    private SQLWrapper insertDomain(ReadonlyWrapper domainWrapper, Collection<FieldMeta<?, ?>> fieldMetas
            , InnerStandardInsert insert, final Visible visible) {

        SQLWrapper sqlWrapper;
        switch (insert.tableMeta().mappingMode()) {
            case PARENT:// when PARENT,discriminatorValue is 0 .
            case SIMPLE:
                sqlWrapper = createInsertForSimple(domainWrapper, fieldMetas, insert, visible);
                break;
            case CHILD:
                sqlWrapper = createInsertForChild(domainWrapper, fieldMetas, insert, visible);
                break;
            default:
                throw DialectUtils.createMappingModeUnknownException(insert.tableMeta().mappingMode());

        }
        return sqlWrapper;
    }

    /**
     * @param mergedFields merged by {@link DMLUtils#mergeInsertFields(TableMeta, Dialect, Collection)}
     */
    private ChildSQLWrapper createInsertForChild(ReadonlyWrapper beanWrapper, Collection<FieldMeta<?, ?>> mergedFields
            , InnerStandardInsert insert, final Visible visible) {

        Assert.notEmpty(mergedFields, "mergedFields must not empty.");

        final ChildTableMeta<?> childMeta = (ChildTableMeta<?>) insert.tableMeta();
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
        StandardValueInsertContext parentContext = StandardValueInsertContext.buildParent(insert, beanWrapper
                , this.dialect, visible);
        DMLUtils.createValueInsertForSimple(parentMeta, childMeta,parentFields, beanWrapper, parentContext);

        //3. create child sql.
        StandardValueInsertContext childContext = StandardValueInsertContext.buildChild(insert, beanWrapper
                , this.dialect, visible);
        DMLUtils.createValueInsertForSimple(childMeta, childMeta,childFields, beanWrapper, childContext);

        return ChildSQLWrapper.build(parentContext.build(), childContext.build());
    }


    /**
     * @param mergedFields merged by {@link DMLUtils#mergeInsertFields(TableMeta, Dialect, Collection)}
     */
    private SimpleSQLWrapper createInsertForSimple(ReadonlyWrapper beanWrapper
            , Collection<FieldMeta<?, ?>> mergedFields, InnerStandardInsert insert
            , final Visible visible) {

        StandardValueInsertContext context = StandardValueInsertContext.build(insert, beanWrapper
                , this.dialect, visible);
        TableMeta<?> tableMeta = insert.tableMeta();
        DMLUtils.createValueInsertForSimple(tableMeta,tableMeta, mergedFields, beanWrapper, context);

        return context.build();
    }


    private SQLWrapper standardBatchInsert(InnerStandardBatchInsert insert, final Visible visible) {
        if (this.dialect.sessionFactory().shardingMode() != ShardingMode.NO_SHARDING) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "Batch insert only support NO_SHARDING mode.");
        }
        SQLWrapper sqlWrapper;
        switch (insert.tableMeta().mappingMode()) {
            case SIMPLE:
            case PARENT:
                sqlWrapper = standardBatchInsertForSimple(insert, visible);
                break;
            case CHILD:
                sqlWrapper = standardBatchInsertForChild(insert, visible);
                break;
            default:
                throw DialectUtils.createMappingModeUnknownException(insert.tableMeta().mappingMode());

        }
        return sqlWrapper;
    }

    private SQLWrapper standardBatchInsertForSimple(InnerStandardBatchInsert insert
            , final Visible visible) {
        TableMeta<?> tableMeta = insert.tableMeta();
        // 1.merge fields
        Set<FieldMeta<?, ?>> fieldMetaSet = DMLUtils.mergeInsertFields(tableMeta, this.dialect, insert.fieldList());

        StandardValueInsertContext context = StandardValueInsertContext.build(insert, null, this.dialect, visible);
        // 2. parse single table insert sql
        DMLUtils.createStandardBatchInsertForSimple(tableMeta, tableMeta,fieldMetaSet, context);
        // 3. create batch sql wrapper
        return DMLUtils.createBatchInsertWrapper(insert, context.build(), this.dialect.sessionFactory());
    }


    private ChildSQLWrapper standardBatchInsertForChild(InnerStandardBatchInsert insert, final Visible visible) {
        final ChildTableMeta<?> childMeta = (ChildTableMeta<?>) insert.tableMeta();
        final ParentTableMeta<?> parentMeta = childMeta.parentMeta();

        // 1. merge fields
        Set<FieldMeta<?, ?>> mergeFieldSet = DMLUtils.mergeInsertFields(childMeta, this.dialect, insert.fieldList());

        final Set<FieldMeta<?, ?>> parentFieldSet = new HashSet<>(), childFieldSet = new HashSet<>();
        // 2.  divide target fields
        DialectUtils.divideFields(childMeta, mergeFieldSet, parentFieldSet, childFieldSet);

        // 3. parent sql wrapper
        final StandardValueInsertContext parentContext = StandardValueInsertContext.buildParent(
                insert, null, this.dialect, visible);
        DMLUtils.createStandardBatchInsertForSimple(parentMeta,childMeta, parentFieldSet, parentContext);

        //4. child sql wrapper
        final StandardValueInsertContext childContext = StandardValueInsertContext.buildChild(
                insert, null, this.dialect, visible);
        DMLUtils.createStandardBatchInsertForSimple(childMeta,childMeta, childFieldSet, childContext);

        return ChildSQLWrapper.build(parentContext.build(), childContext.build());
    }

    /*################################## blow update private method ##################################*/

    private SQLWrapper standardGenericUpdate(InnerStandardUpdate update, final Visible visible) {

        SQLWrapper sqlWrapper;
        switch (update.tableMeta().mappingMode()) {
            case SIMPLE:
                sqlWrapper = standardSimpleUpdate(update, visible);
                break;
            case PARENT:
                sqlWrapper = standardParentUpdate(update, visible);
                break;
            case CHILD:
                sqlWrapper = standardChildUpdate(update, visible);
                break;
            default:
                throw DialectUtils.createMappingModeUnknownException(update.tableMeta().mappingMode());
        }
        return sqlWrapper;
    }

    private SQLWrapper standardChildUpdate(InnerStandardUpdate update, final Visible visible) {
        List<FieldMeta<?, ?>> targetFieldList = update.targetFieldList();
        List<Expression<?>> valueExpList = update.valueExpList();

        final List<FieldMeta<?, ?>> parentFieldList = new ArrayList<>(), childFieldList = new ArrayList<>();
        final List<Expression<?>> parentExpList = new ArrayList<>(), childExpList = new ArrayList<>();

        final ChildTableMeta<?> childMeta = (ChildTableMeta<?>) update.tableMeta();
        final ParentTableMeta<?> parentMeta = childMeta.parentMeta();
        // 1. divide target fields and value expressions with parentMeta and childMeta.
        final int size = targetFieldList.size();
        for (int i = 0; i < size; i++) {
            FieldMeta<?, ?> fieldMeta = targetFieldList.get(i);
            if (fieldMeta.tableMeta() == parentMeta) {
                parentFieldList.add(fieldMeta);
                parentExpList.add(valueExpList.get(i));
            } else if (fieldMeta.tableMeta() == childMeta) {
                childFieldList.add(fieldMeta);
                childExpList.add(valueExpList.get(i));
            } else {
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "FieldMeta[%s] error for ChildTableMeta[%s]"
                        , fieldMeta, childMeta);
            }
        }

        //2. parse parent update sql
        StandardUpdateContext parentContext = StandardUpdateContext.buildParent(update, this.dialect, visible);
        // 2-1. create parent predicate
        List<IPredicate> parentPredicateList = DMLUtils.extractParentPredicatesForUpdate(
                childMeta, childFieldList, update.predicateList());
        parseStandardUpdate(parentContext, parentMeta, update.tableAlias()
                , parentFieldList, parentExpList, parentPredicateList);

        SQLWrapper sqlWrapper;
        if (childFieldList.isEmpty()) {
            sqlWrapper = parentContext.build();
        } else {
            //3 parse child update sql (optional)
            StandardUpdateContext childContext = StandardUpdateContext.buildChild(update, this.dialect, visible);
            parseStandardUpdate(childContext, childMeta, update.tableAlias()
                    , childFieldList, childExpList, update.predicateList());

            sqlWrapper = ChildSQLWrapper.build(parentContext.build(), childContext.build());
        }
        return sqlWrapper;
    }


    private void parseStandardUpdate(StandardUpdateContext context, TableMeta<?> tableMeta, String tableAlias
            , List<FieldMeta<?, ?>> fieldList, List<Expression<?>> valueExpList, List<IPredicate> predicateList) {

        context.sqlBuilder().append("UPDATE");
        tableOnlyModifier(context);
        // append table name and alias
        context.appendTable(tableMeta,tableAlias);
        // set clause
        DMLUtils.standardSimpleUpdateSetClause(context, tableMeta, tableAlias
                , fieldList, valueExpList);
        // where clause
        simpleTableWhereClause(context, tableMeta, tableAlias
                , predicateList);

    }

    private SQLWrapper standardBatchUpdate(InnerStandardBatchUpdate update, final Visible visible) {
        // create batch update wrapper
        return DMLUtils.createBatchSQLWrapper(
                update.wrapperList()
                , standardGenericUpdate(update, visible)
        );
    }

    private SimpleSQLWrapper standardSimpleUpdate(InnerStandardUpdate update, final Visible visible) {
        StandardUpdateContext context = StandardUpdateContext.build(update, this.dialect, visible);

        parseStandardUpdate(context, update.tableMeta(), update.tableAlias()
                , update.targetFieldList(), update.valueExpList(), update.predicateList());

        return context.build();
    }

    private SimpleSQLWrapper standardParentUpdate(InnerStandardUpdate update, final Visible visible) {
        StandardUpdateContext context = StandardUpdateContext.build(update, this.dialect, visible);
        final ParentTableMeta<?> parentMeta = (ParentTableMeta<?>) update.tableMeta();
        // create parent predicate
        List<IPredicate> parentPredicateList = DMLUtils.createParentPredicates(parentMeta, update.predicateList());

        parseStandardUpdate(context, parentMeta, update.tableAlias()
                , update.targetFieldList(), update.valueExpList(), parentPredicateList);

        return context.build();
    }

    private void simpleTableWhereClause(TableContextSQLContext context, TableMeta<?> tableMeta, String tableAlias
            , List<IPredicate> predicateList) {

        final boolean needAppendVisible = DialectUtils.needAppendVisible(tableMeta);
        final boolean hasPredicate = !predicateList.isEmpty();
        if (hasPredicate || needAppendVisible) {
            context.sqlBuilder()
                    .append(" WHERE");
        }
        if (hasPredicate) {
            DialectUtils.appendPredicateList(predicateList, context);
        }
        if (needAppendVisible) {
            appendVisiblePredicate(tableMeta, tableAlias, context, hasPredicate);
        }
    }


    /*################################## blow delete private method ##################################*/

    private SQLWrapper standardBatchDelete(InnerStandardBatchDelete delete, final Visible visible) {
        return DMLUtils.createBatchSQLWrapper(
                delete.wrapperList()
                , standardGenericDelete(delete, visible)
        );
    }

    private SQLWrapper standardGenericDelete(InnerStandardDelete delete, final Visible visible) {
        SQLWrapper sqlWrapper;
        switch (delete.tableMeta().mappingMode()) {
            case SIMPLE:
                sqlWrapper = standardSimpleDelete(delete, visible);
                break;
            case PARENT:
                sqlWrapper = standardParentDelete(delete, visible);
                break;
            case CHILD:
                sqlWrapper = standardChildDelete(delete, visible);
                break;
            default:
                throw DialectUtils.createMappingModeUnknownException(delete.tableMeta().mappingMode());
        }
        return sqlWrapper;
    }


    private SQLWrapper standardSimpleDelete(InnerStandardDelete delete, final Visible visible) {
        StandardDeleteContext context = StandardDeleteContext.build(delete, this.dialect, visible);
        parseStandardDelete(delete.tableMeta(), delete.tableAlias(), delete.predicateList(), context);
        return context.build();
    }

    private SQLWrapper standardParentDelete(InnerStandardDelete delete, final Visible visible) {
        StandardDeleteContext context = StandardDeleteContext.build(delete, this.dialect, visible);
        final ParentTableMeta<?> parentMeta = (ParentTableMeta<?>) delete.tableMeta();
        // create parent predicate list
        List<IPredicate> parentPredicateList = DMLUtils.createParentPredicates(parentMeta, delete.predicateList());
        parseStandardDelete(parentMeta, delete.tableAlias(), parentPredicateList, context);
        return context.build();
    }

    private SQLWrapper standardChildDelete(InnerStandardDelete delete, final Visible visible) {
        final ChildTableMeta<?> childMeta = (ChildTableMeta<?>) delete.tableMeta();
        final ParentTableMeta<?> parentMeta = childMeta.parentMeta();
        // 1. extract parent predicate list
        List<IPredicate> parentPredicateList = DMLUtils.extractParentPredicateForDelete(childMeta
                , delete.predicateList());

        //2. create parent delete sql
        StandardDeleteContext parentContext = StandardDeleteContext.buildParent(delete, this.dialect, visible);
        parseStandardDelete(parentMeta, delete.tableAlias(), parentPredicateList, parentContext);

        //3. create child delete sql
        StandardDeleteContext childContext = StandardDeleteContext.buildChild(delete, this.dialect, visible);
        parseStandardDelete(childMeta, delete.tableAlias(), delete.predicateList(), childContext);

        return ChildSQLWrapper.build(parentContext.build(), childContext.build());
    }

    private void parseStandardDelete(TableMeta<?> tableMeta, String tableAlias, List<IPredicate> predicateList
            , StandardDeleteContext context) {

        StringBuilder builder = context.sqlBuilder().append("DELETE FROM");
        tableOnlyModifier(context);
        // append table name
        context.appendTable(tableMeta,tableAlias);
        // where clause
        simpleTableWhereClause(context, tableMeta, tableAlias, predicateList);
    }


}
