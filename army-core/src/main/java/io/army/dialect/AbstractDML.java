package io.army.dialect;

import io.army.ErrorCode;
import io.army.ShardingMode;
import io.army.beans.DomainWrapper;
import io.army.beans.ReadonlyWrapper;
import io.army.boot.FieldValuesGenerator;
import io.army.criteria.*;
import io.army.criteria.impl.CriteriaCounselor;
import io.army.criteria.impl.inner.*;
import io.army.domain.IDomain;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.wrapper.*;

import java.util.*;

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

        List<BatchSimpleSQLWrapper> list;
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
    public final SQLWrapper update(Update update, final Visible visible) {
        Assert.isTrue(update.prepared(), "Update don't invoke asUpdate() method.");

        SQLWrapper sqlWrapper;
        if (update instanceof InnerStandardUpdate) {
            InnerStandardUpdate standardUpdate = (InnerStandardUpdate) update;
            CriteriaCounselor.assertStandardUpdate(standardUpdate);
            DMLUtils.assertUpdateSetAndWhereClause(standardUpdate);
            if (update instanceof InnerStandardBatchUpdate) {
                sqlWrapper = standardBatchUpdate((InnerStandardBatchUpdate) standardUpdate, visible);
            } else {
                sqlWrapper = standardGenericUpdate(standardUpdate, visible);
            }
        } else if (update instanceof InnerSpecialUpdate) {
            InnerSpecialUpdate specialUpdate = (InnerSpecialUpdate) update;
            assertSpecialUpdate(specialUpdate);
            DMLUtils.assertUpdateSetAndWhereClause(specialUpdate);
            sqlWrapper = specialUpdate(specialUpdate, visible);
        } else {
            throw new IllegalArgumentException(String.format("Update[%s] type unknown.", update));
        }
        return sqlWrapper;
    }

    @Override
    public final List<SimpleSQLWrapper> delete(Delete delete, final Visible visible) {
        Assert.isTrue(delete.prepared(), "Delete don't invoke asDelete() method.");

        List<SimpleSQLWrapper> list;
        if (delete instanceof InnerStandardDelete) {
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

    protected List<BatchSimpleSQLWrapper> specialBatchInsert(InnerSpecialBatchInsert insert, Visible visible) {
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


    protected void assertSpecialUpdate(InnerSpecialUpdate update) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special domain update."
                , sqlDialect())
        );
    }

    protected SQLWrapper specialUpdate(InnerSpecialUpdate update, Visible visible) {
        throw new UnsupportedOperationException(String.format("dialect [%s] not support special update."
                , sqlDialect())
        );
    }

    /*################################## blow delete template method ##################################*/

    protected void assertSpecialDelete(InnerSpecialDelete delete) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special delete."
                , sqlDialect())
        );
    }

    protected List<SimpleSQLWrapper> specialDelete(InnerSpecialDelete delete, Visible visible) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special delete."
                , sqlDialect())
        );
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


    private List<BatchSimpleSQLWrapper> standardBatchInsert(InnerStandardBatchInsert insert, final Visible visible) {

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

    private SQLWrapper standardGenericUpdate(InnerStandardUpdate update, final Visible visible) {

        SQLWrapper sqlWrapper;
        switch (update.tableMeta().mappingMode()) {
            case SIMPLE:
            case PARENT:
                sqlWrapper = standardSimpleUpdate(update, visible);
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

        // 2. extract parent predicate lists
        final List<IPredicate> parentPredicates = DMLUtils.extractParentPredicateList(childMeta, childFieldList
                , update.predicateList());
        //3. parse parent update sql
        StandardUpdateContext parentContext = StandardUpdateContext.buildParent(update, this.dialect, visible);
        parseStandardUpdate(parentContext, parentMeta, update.tableAlias()
                , parentFieldList, parentExpList, parentPredicates);

        SQLWrapper sqlWrapper;
        if (childFieldList.isEmpty()) {
            sqlWrapper = parentContext.build();
        } else {
            //4. parse child update sql ,optional
            StandardUpdateContext childContext = StandardUpdateContext.buildChild(update, this.dialect, visible);
            parseStandardUpdate(childContext, childMeta, update.tableAlias()
                    , childFieldList, childExpList, update.predicateList());

            sqlWrapper = ChildUpdateSQLWrapper.build(
                    (SimpleUpdateSQLWrapper) parentContext.build()
                    , (SimpleUpdateSQLWrapper) childContext.build());
        }
        return sqlWrapper;
    }


    private void parseStandardUpdate(StandardUpdateContext context, TableMeta<?> tableMeta, String tableAlias
            , List<FieldMeta<?, ?>> fieldList, List<Expression<?>> valueExpList, List<IPredicate> predicateList) {

        StringBuilder builder = context.sqlBuilder().append("UPDATE");
        tableOnlyModifier(context);
        // append table name and alias
        context.appendTable(tableMeta);
        if (tableAliasAfterAs()) {
            builder.append(" AS");
        }
        context.appendText(tableAlias);
        // set clause
        DMLUtils.standardSimpleUpdateSetClause(context, tableMeta, tableAlias
                , fieldList, valueExpList);
        // where clause
        simpleTableWhereClause(context, tableMeta, tableAlias
                , predicateList);

    }

    private SQLWrapper standardBatchUpdate(InnerStandardBatchUpdate update, final Visible visible) {
        return null;
    }

    private SimpleSQLWrapper standardSimpleUpdate(InnerStandardUpdate update, final Visible visible) {
        StandardUpdateContext context = StandardUpdateContext.build(update, this.dialect, visible);

        parseStandardUpdate(context, update.tableMeta(), update.tableAlias()
                , update.targetFieldList(), update.valueExpList(), update.predicateList());

        return context.build();
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




}
