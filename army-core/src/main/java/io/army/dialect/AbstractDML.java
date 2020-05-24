package io.army.dialect;

import io.army.ErrorCode;
import io.army.beans.DomainWrapper;
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
import io.army.wrapper.ChildSQLWrapper;
import io.army.wrapper.SQLWrapper;
import io.army.wrapper.SimpleSQLWrapper;

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
            if (standardInsert instanceof InnerStandardBatchInsert) {
                list = Collections.singletonList(
                        standardBatchInsert((InnerStandardBatchInsert) standardInsert, visible)
                );
            } else {
                list = standardInsert(standardInsert, visible);
            }
        } else if (insert instanceof InnerStandardSubQueryInsert) {
            InnerStandardSubQueryInsert subQueryInsert = (InnerStandardSubQueryInsert) insert;
            CriteriaCounselor.assertStandardSubQueryInsert(subQueryInsert);
            list = Collections.singletonList(
                    standardSubQueryInsert(subQueryInsert, visible)
            );

        } else if (insert instanceof InnerSpecialGeneralInsert) {
            InnerSpecialGeneralInsert generalInsert = (InnerSpecialGeneralInsert) insert;
            assertSpecialGeneralInsert(generalInsert);
            if (generalInsert instanceof InnerSpecialBatchInsert) {
                list = specialBatchInsert((InnerSpecialBatchInsert) generalInsert, visible);
            } else {
                list = specialGeneralInsert(generalInsert, visible);
            }
        } else if (insert instanceof InnerSpecialSubQueryInsert) {
            InnerSpecialSubQueryInsert subQueryInsert = (InnerSpecialSubQueryInsert) insert;
            assertSpecialSubQueryInsert(subQueryInsert);
            list = specialSubQueryInsert(subQueryInsert, visible);
        } else {
            throw new IllegalArgumentException(String.format("Insert[%s] type unknown.", insert.getClass().getName()));
        }
        return list;
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
    public final SQLWrapper delete(Delete delete, final Visible visible) {
        Assert.isTrue(delete.prepared(), "Delete don't invoke asDelete() method.");

        SQLWrapper sqlWrapper;
        if (delete instanceof InnerStandardDelete) {
            InnerStandardDelete standardDelete = (InnerStandardDelete) delete;
            CriteriaCounselor.assertStandardDelete(standardDelete);
            if (standardDelete instanceof InnerStandardBatchDelete) {
                sqlWrapper = standardBatchDelete((InnerStandardBatchDelete) standardDelete, visible);
            } else {
                sqlWrapper = standardGenericDelete(standardDelete, visible);
            }

        } else if (delete instanceof InnerSpecialDelete) {
            InnerSpecialDelete specialDelete = (InnerSpecialDelete) delete;
            assertSpecialDelete(specialDelete);
            sqlWrapper = specialDelete(specialDelete, visible);
        } else {
            throw new IllegalArgumentException(String.format("Delete[%s] type unknown.", delete));
        }
        return sqlWrapper;
    }

    /*################################## blow protected template method ##################################*/


    /*################################## blow multiInsert template method ##################################*/

    protected void assertSpecialGeneralInsert(InnerSpecialGeneralInsert insert) {
        throw new UnsupportedOperationException(String.format("dialect [%s] not support special general multiInsert."
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

    protected List<SQLWrapper> specialBatchInsert(InnerSpecialBatchInsert insert, Visible visible) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special batch multiInsert."
                , sqlDialect())
        );
    }

    protected List<SQLWrapper> specialSubQueryInsert(InnerSpecialSubQueryInsert insert, Visible visible) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special sub query multiInsert."
                , sqlDialect())
        );
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

    protected SQLWrapper specialDelete(InnerSpecialDelete delete, Visible visible) {
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

        final List<IDomain> domainList = insert.valueList();
        final List<SQLWrapper> sqlWrapperList = new ArrayList<>(domainList.size());

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

    private SimpleSQLWrapper standardSubQueryInsert(InnerStandardSubQueryInsert insert, final Visible visible) {


        final List<FieldMeta<?, ?>> fieldMetaList = insert.fieldList();
        DMLUtils.assertSubQueryInsert(fieldMetaList, insert.subQuery());

        final StandardSubInsertContext context = StandardSubInsertContext.build(insert, this.dialect, visible);
        StringBuilder builder = context.sqlBuilder().append("INSERT INTO");
        context.appendTable(insert.tableMeta());
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
        return context.build();
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
        StandardInsertContext parentContext = StandardInsertContext.buildParent(insert, this.dialect, visible);
        DMLUtils.createStandardInsertForSimple(parentMeta, parentFields, beanWrapper, parentContext);

        //3. create child sql.
        StandardInsertContext childContext = StandardInsertContext.buildChild(insert, this.dialect, visible);
        DMLUtils.createStandardInsertForSimple(childMeta, childFields, beanWrapper, childContext);

        return ChildSQLWrapper.build(parentContext.build(), childContext.build());
    }

    /**
     * @param mergedFields merged by {@link DMLUtils#mergeInsertFields(TableMeta, Dialect, Collection)}
     */
    private SimpleSQLWrapper createInsertForSimple(TableMeta<?> tableMeta, DomainWrapper beanWrapper
            , Collection<FieldMeta<?, ?>> mergedFields, InnerStandardInsert insert
            , final Visible visible) {

        StandardInsertContext context = StandardInsertContext.build(insert, this.dialect, visible);

        DMLUtils.createStandardInsertForSimple(tableMeta, mergedFields, beanWrapper, context);

        return context.build();
    }


    private SQLWrapper standardBatchInsert(InnerStandardBatchInsert insert, final Visible visible) {
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
                throw new IllegalArgumentException(
                        String.format("unknown MappingMode[%s]", insert.tableMeta().mappingMode()));

        }
        return DMLUtils.createBatchInsertWrapper(
                insert
                , sqlWrapper
                , this.dialect.sessionFactory()
        );
    }


    private SimpleSQLWrapper standardBatchInsertForSimple(InnerStandardBatchInsert insert, final Visible visible) {
        TableMeta<?> tableMeta = insert.tableMeta();
        // 1.merge fields
        Set<FieldMeta<?, ?>> fieldMetaSet = DMLUtils.mergeInsertFields(tableMeta, this.dialect, insert.fieldList());

        StandardInsertContext context = StandardInsertContext.build(insert, this.dialect, visible);
        // single table multiInsert sql
        DMLUtils.createStandardBatchInsertForSimple(tableMeta, fieldMetaSet, context);
        return context.build();
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
        final StandardInsertContext parentContext = StandardInsertContext.buildParent(insert, this.dialect, visible);
        DMLUtils.createStandardBatchInsertForSimple(parentMeta, parentFieldSet, parentContext);

        //4. child sql wrapper
        final StandardInsertContext childContext = StandardInsertContext.buildChild(insert, this.dialect, visible);
        DMLUtils.createStandardBatchInsertForSimple(childMeta, childFieldSet, childContext);

        return ChildSQLWrapper.build(parentContext.build(), childContext.build());
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

            sqlWrapper = ChildSQLWrapper.build(parentContext.build(), childContext.build());
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
        return DMLUtils.createBatchSQLWrapper(
                update.namedParams()
                , standardGenericUpdate(update, visible)
        );
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

    private SQLWrapper standardBatchDelete(InnerStandardBatchDelete delete, final Visible visible) {
        return DMLUtils.createBatchSQLWrapper(
                delete.namedParamList()
                , standardGenericDelete(delete, visible)
        );
    }

    private SQLWrapper standardGenericDelete(InnerStandardDelete delete, final Visible visible) {
        SQLWrapper sqlWrapper;
        switch (delete.tableMeta().mappingMode()) {
            case SIMPLE:
            case PARENT:
                sqlWrapper = standardSimpleDelete(delete, visible);
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

    private SQLWrapper standardChildDelete(InnerStandardDelete delete, final Visible visible) {
        final ChildTableMeta<?> childMeta = (ChildTableMeta<?>) delete.tableMeta();
        final ParentTableMeta<?> parentMeta = childMeta.parentMeta();
        // 1. extract parent predicate list
        List<IPredicate> parentPredicateList = DMLUtils.extractParentPredicateList(
                childMeta, new HashSet<>(childMeta.fieldCollection()), delete.predicateList());

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
        context.appendTable(tableMeta);

        if (this.singleDeleteHasTableAlias()) {
            if (this.tableAliasAfterAs()) {
                builder.append(" AS");
            }
            context.appendText(tableAlias);
        }
        // where clause
        simpleTableWhereClause(context, tableMeta, tableAlias, predicateList);
    }


}
