package io.army.dialect;

import io.army.ErrorCode;
import io.army.beans.DomainWrapper;
import io.army.beans.ObjectWrapper;
import io.army.beans.ReadonlyWrapper;
import io.army.boot.DomainValuesGenerator;
import io.army.criteria.*;
import io.army.criteria.impl._CriteriaCounselor;
import io.army.criteria.impl.inner.*;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;
import io.army.session.FactoryMode;
import io.army.stmt.PairStmt;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmt;
import io.army.util.Assert;

import java.util.*;


/**
 * <p>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 */
public abstract class AbstractDmlDialect extends AbstractDMLAndDQL implements DmlDialect {


    protected AbstractDmlDialect(Dialect dialect) {
        super(dialect);
    }

    /*################################## blow DML batchInsert method ##################################*/

    /**
     * {@inheritDoc}
     */
    @Override
    public final Stmt valueInsert(final Insert insert, final Visible visible) {
        Assert.isTrue(insert.prepared(), "Insert don't invoke asInsert() method.");
        final _ValuesInsert valuesInsert = (_ValuesInsert) insert;
        if (insert instanceof _DialectStatement) {
            assertDialectInsert(valuesInsert);
        } else {
            // assert implementation class is legal
            _CriteriaCounselor.standardInsert(valuesInsert);
            final List<ObjectWrapper> domainList = valuesInsert.domainList();
            if (domainList.size() > 1 && this.sharding) {
                for (List<ObjectWrapper> group : shardingForInsert(domainList)) {
                    valueInsert(valuesInsert, group, visible);
                }
            } else {
                valueInsert(valuesInsert, domainList, visible);
            }
        }
        return null;
    }

    @Override
    public final Stmt returningInsert(Insert insert, final Visible visible) {
        Assert.isTrue(insert.prepared(), "Insert don't invoke asInsert() method.");

        Stmt stmt;
        if (insert instanceof _SpecialValueInsert) {

        } else {
            throw new IllegalArgumentException(String.format("Insert[%s] not supported by returningInsert.", insert));
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Stmt subQueryInsert(Insert insert, final Visible visible) {
        Assert.isTrue(insert.prepared(), "Insert don't invoke asInsert() method.");

        Stmt stmt;
        if (insert instanceof _StandardSubQueryInsert) {
            _StandardSubQueryInsert subQueryInsert = (_StandardSubQueryInsert) insert;
            // assert implementation class is legal
            _CriteriaCounselor.assertStandardSubQueryInsert(subQueryInsert);
            // parse sql
            stmt = standardSubQueryInsert(subQueryInsert, visible);
        } else if (insert instanceof _SpecialSubQueryInsert) {
            _SpecialSubQueryInsert subQueryInsert = (_SpecialSubQueryInsert) insert;
            // assert implementation class is legal
            assertSpecialSubQueryInsert(subQueryInsert);
            stmt = specialSubQueryInsert(subQueryInsert, visible);
        } else {
            throw new IllegalArgumentException(String.format("Insert[%s] type unknown.", insert.getClass().getName()));
        }
        return stmt;
    }


    /*################################## blow update method ##################################*/

    @Override
    public final Stmt update(Update update, final Visible visible) {
        Assert.isTrue(update.prepared(), "Update don't invoke asUpdate() method.");

        Stmt stmt;
        if (update instanceof _StandardUpdate) {
            _StandardUpdate standardUpdate = (_StandardUpdate) update;
            // assert implementation class is legal
            _CriteriaCounselor.assertStandardUpdate(standardUpdate);
            DmlUtils.assertUpdateSetAndWhereClause(standardUpdate);
            if (update instanceof _StandardBatchUpdate) {
                stmt = standardBatchUpdate((_StandardBatchUpdate) update, visible);
            } else {
                stmt = standardGenericUpdate(standardUpdate, visible);
            }

        } else if (update instanceof _SpecialUpdate) {
            _SpecialUpdate specialUpdate = (_SpecialUpdate) update;
            // assert implementation class is legal
            assertSpecialUpdate(specialUpdate);
            DmlUtils.assertUpdateSetAndWhereClause(specialUpdate);
            stmt = specialUpdate(specialUpdate, visible);
        } else {
            throw new IllegalArgumentException(String.format("Update[%s] not supported by simpleUpdate.", update));
        }
        return stmt;
    }

    @Override
    public final Stmt delete(Delete delete, final Visible visible) {
        Assert.isTrue(delete.prepared(), "Delete don't invoke asDelete() method.");

        Stmt stmt;
        if (delete instanceof _StandardDelete) {
            _StandardDelete standardDelete = (_StandardDelete) delete;
            _CriteriaCounselor.assertStandardDelete(standardDelete);
            if (standardDelete instanceof _StandardBatchDelete) {
                stmt = standardBatchDelete((_StandardBatchDelete) delete, visible);
            } else {
                stmt = standardGenericDelete(standardDelete, visible);
            }
        } else if (delete instanceof _SpecialDelete) {
            _SpecialDelete specialDelete = (_SpecialDelete) delete;
            assertSpecialDelete(specialDelete);
            if (specialDelete instanceof _SpecialBatchDelete) {
                throw new IllegalArgumentException(String.format("Delete[%s] not supported by simpleDelete.", delete));
            }
            stmt = specialDelete(specialDelete, visible);
        } else {
            throw new IllegalArgumentException(String.format("Delete[%s] not supported by simpleDelete.", delete));
        }
        return stmt;
    }

    /*################################## blow protected template method ##################################*/


    /*################################## blow multiInsert template method ##################################*/

    /**
     * @see #valueInsert(Insert, Visible)
     */
    protected void assertDialectInsert(_ValuesInsert insert) {
        throw new UnsupportedOperationException(String.format("dialect [%s] not support special general multiInsert."
                , database())
        );
    }


    protected Stmt dialectValueInsert(_ValuesInsert insert, Visible visible) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special general multiInsert."
                , database())
        );
    }

    protected List<Stmt> specialBatchInsert(_SpecialBatchInsert insert, Visible visible) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special batch multiInsert."
                , database())
        );
    }

    protected Stmt specialSubQueryInsert(_SpecialSubQueryInsert insert, Visible visible) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special sub query multiInsert."
                , database())
        );
    }



    /*################################## blow update template method ##################################*/


    protected void assertSpecialUpdate(_SpecialUpdate update) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special domain update."
                , database())
        );
    }

    protected Stmt specialUpdate(_SpecialUpdate update, Visible visible) {
        throw new UnsupportedOperationException(String.format("dialect [%s] not support special update."
                , database())
        );
    }

    /*################################## blow delete template method ##################################*/

    protected void assertSpecialDelete(_SpecialDelete delete) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special delete."
                , database())
        );
    }

    protected Stmt specialDelete(_SpecialDelete delete, Visible visible) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special delete."
                , database())
        );
    }

    /*################################## blow protected method ##################################*/

    /*################################## blow private batchInsert method ##################################*/

    private List<List<ObjectWrapper>> shardingForInsert(List<ObjectWrapper> domainList) {
        return Collections.singletonList(domainList);
    }

    /**
     * @return a modifiable list
     */
    private Stmt valueInsert(_ValuesInsert insert, List<ObjectWrapper> domainList, final Visible visible) {

        final TableMeta<?> tableMeta = insert.tableMeta();
        final List<FieldMeta<?, ?>> targetFieldList = insert.fieldSet();
        // 1. merge target fields.
        Collection<FieldMeta<?, ?>> fieldMetas;
        if (targetFieldList.isEmpty()) {

        } else {
            fieldMetas = DmlUtils.appendInsertFields(tableMeta, this.dialect, insert.fieldSet());
        }
        final List<ObjectWrapper> domainWrapperList = insert.domainList();
        final List<Stmt> stmtList = new ArrayList<>(domainWrapperList.size());
        final DomainValuesGenerator generator = this.dialect.sessionFactory().domainValuesGenerator();
        final boolean migrationData = insert.migrationData();
        if (domainIndexSet == null) {
            for (DomainWrapper domainWrapper : domainWrapperList) {
                // 2. create domain required property value .
                generator.createValues(domainWrapper, migrationData);
                stmtList.add(
                        // 3. create sql of domain
                        insertDomain(domainWrapper, fieldMetaSet, insert, visible)
                );
            }
        } else {
            for (Integer domainIndex : domainIndexSet) {
                DomainWrapper domainWrapper = domainWrapperList.get(domainIndex);
                // 2. create domain required property value .
                generator.createValues(domainWrapper, migrationData);
                stmtList.add(
                        // 3. create sql of domain
                        insertDomain(domainWrapper, fieldMetaSet, insert, visible)
                );
            }
        }
        return stmtList;
    }


    private Stmt createValueInsert(_ValueInsertContext context) {
        final SqlBuilder builder = context.sqlBuilder();
        builder.append(Keywords.INSERT_INTO)
                .append(' ');

        builder.append(Keywords.LEFT_BRACKET);
        int index = 0;
        for (FieldMeta<?, ?> fieldMeta : context.fieldMetas()) {
            if (index > 0) {
                builder.append(",");
            }
            context.appendField(fieldMeta);
            index++;
        }
        builder.append(Keywords.RIGHT_BRACKET)
                .append(Keywords.VALUES);
        index = 0;
        for (ObjectWrapper domain : shardingDomains) {
            if (index > 0) {
                builder.append(',');
            }
            builder.append('(');
            for (FieldMeta<?, ?> fieldMeta : context.fieldMetas()) {
                Object expValue = expMap.get(fieldMeta);

            }
            builder.append(')');
            index++;
        }
        return null;
    }


    private Stmt valueInsertForSingleTable(_ValuesInsert insert, List<ObjectWrapper> shardingDomains, final Visible visible) {
        TableMeta<?> tableMeta = insert.tableMeta();
        return null;
    }

    private Stmt standardSubQueryInsert(_StandardSubQueryInsert insert, final Visible visible) {
        Stmt stmt;
        if (insert instanceof _StandardChildSubQueryInsert) {
            stmt = standardChildQueryInsert((_StandardChildSubQueryInsert) insert, visible);
        } else {
            SubQueryInsertContext context = SubQueryInsertContext.build(insert, this.dialect, visible);
            parseStandardSimpleSubQueryInsert(context, insert.tableMeta(), insert.fieldSet(), insert.subQuery());
            stmt = context.build();
        }
        return stmt;
    }

    private PairStmt standardChildQueryInsert(_StandardChildSubQueryInsert insert, final Visible visible) {
        final ChildTableMeta<?> childMeta = insert.tableMeta();
        final ParentTableMeta<?> parentMeta = childMeta.parentMeta();

        // firstly ,parse parent insert sql
        SubQueryInsertContext parentContext = SubQueryInsertContext.buildParent(insert, this.dialect, visible);
        parseStandardSimpleSubQueryInsert(parentContext, parentMeta, insert.parentFieldList(), insert.parentSubQuery());

        // secondly ,parse child insert sql
        SubQueryInsertContext childContext = SubQueryInsertContext.buildChild(insert, this.dialect, visible);
        parseStandardSimpleSubQueryInsert(childContext, childMeta, insert.fieldSet(), insert.subQuery());

        return PairStmt.build(parentContext.build(), childContext.build());
    }

    private void parseStandardSimpleSubQueryInsert(SubQueryInsertContext context
            , TableMeta<?> physicalTable, List<FieldMeta<?, ?>> fieldMetaList, SubQuery subQuery) {

        DmlUtils.assertSubQueryInsert(fieldMetaList, subQuery);

        SqlBuilder builder = context.sqlBuilder().append("INSERT INTO");
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


    private Stmt insertDomain(ReadonlyWrapper domainWrapper, Collection<FieldMeta<?, ?>> fieldMetas
            , _StandardInsert insert, final Visible visible) {

        Stmt stmt;
        switch (insert.tableMeta().mappingMode()) {
            case PARENT:// when PARENT,discriminatorValue is 0 .
            case SIMPLE:
                stmt = createInsertForSimple(domainWrapper, fieldMetas, insert, visible);
                break;
            case CHILD:
                stmt = createInsertForChild(domainWrapper, fieldMetas, insert, visible);
                break;
            default:
                throw DialectUtils.createMappingModeUnknownException(insert.tableMeta().mappingMode());

        }
        return stmt;
    }

    /**
     * @param mergedFields merged by {@link DmlUtils#appendInsertFields(TableMeta, Dialect, Collection)}
     */
    private PairStmt createInsertForChild(ReadonlyWrapper beanWrapper, Collection<FieldMeta<?, ?>> mergedFields
            , _StandardInsert insert, final Visible visible) {

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
        DmlUtils.createValueInsertForSimple(parentMeta, childMeta, parentFields, beanWrapper, parentContext);

        //3. create child sql.
        StandardValueInsertContext childContext = StandardValueInsertContext.buildChild(insert, beanWrapper
                , this.dialect, visible);
        DmlUtils.createValueInsertForSimple(childMeta, childMeta, childFields, beanWrapper, childContext);

        return PairStmt.build(parentContext.build(), childContext.build());
    }


    /**
     * @param mergedFields merged by {@link DmlUtils#appendInsertFields(TableMeta, Dialect, Collection)}
     */
    private SimpleStmt createInsertForSimple(ReadonlyWrapper beanWrapper
            , Collection<FieldMeta<?, ?>> mergedFields, _StandardInsert insert
            , final Visible visible) {

        StandardValueInsertContext context = StandardValueInsertContext.build(insert, beanWrapper
                , this.dialect, visible);
        TableMeta<?> tableMeta = insert.tableMeta();
        DmlUtils.createValueInsertForSimple(tableMeta, tableMeta, mergedFields, beanWrapper, context);

        return context.build();
    }


    private Stmt standardBatchInsert(_StandardBatchInsert insert, final Visible visible) {
        if (this.dialect.sessionFactory().shardingMode() != FactoryMode.NO_SHARDING) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "Batch insert only support NO_SHARDING mode.");
        }
        Stmt stmt;
        switch (insert.tableMeta().mappingMode()) {
            case SIMPLE:
            case PARENT:
                stmt = standardBatchInsertForSimple(insert, visible);
                break;
            case CHILD:
                stmt = standardBatchInsertForChild(insert, visible);
                break;
            default:
                throw DialectUtils.createMappingModeUnknownException(insert.tableMeta().mappingMode());

        }
        return stmt;
    }

    private Stmt standardBatchInsertForSimple(_StandardBatchInsert insert
            , final Visible visible) {
        TableMeta<?> tableMeta = insert.tableMeta();
        // 1.merge fields
        Set<FieldMeta<?, ?>> fieldMetaSet = DmlUtils.appendInsertFields(tableMeta, this.dialect, insert.fieldSet());

        StandardValueInsertContext context = StandardValueInsertContext.build(insert, null, this.dialect, visible);
        // 2. parse single table insert sql
        DmlUtils.createStandardBatchInsertForSimple(tableMeta, tableMeta, fieldMetaSet, context);
        // 3. create batch sql wrapper
        return DmlUtils.createBatchInsertWrapper(insert, context.build(), this.dialect.sessionFactory());
    }


    private PairStmt standardBatchInsertForChild(_StandardBatchInsert insert, final Visible visible) {
        final ChildTableMeta<?> childMeta = (ChildTableMeta<?>) insert.tableMeta();
        final ParentTableMeta<?> parentMeta = childMeta.parentMeta();

        // 1. merge fields
        Set<FieldMeta<?, ?>> mergeFieldSet = DmlUtils.appendInsertFields(childMeta, this.dialect, insert.fieldSet());

        final Set<FieldMeta<?, ?>> parentFieldSet = new HashSet<>(), childFieldSet = new HashSet<>();
        // 2.  divide target fields
        DialectUtils.divideFields(childMeta, mergeFieldSet, parentFieldSet, childFieldSet);

        // 3. parent sql wrapper
        final StandardValueInsertContext parentContext = StandardValueInsertContext.buildParent(
                insert, null, this.dialect, visible);
        DmlUtils.createStandardBatchInsertForSimple(parentMeta, childMeta, parentFieldSet, parentContext);

        //4. child sql wrapper
        final StandardValueInsertContext childContext = StandardValueInsertContext.buildChild(
                insert, null, this.dialect, visible);
        DmlUtils.createStandardBatchInsertForSimple(childMeta, childMeta, childFieldSet, childContext);

        return PairStmt.build(parentContext.build(), childContext.build());
    }

    /*################################## blow update private method ##################################*/

    private Stmt standardGenericUpdate(_StandardUpdate update, final Visible visible) {

        Stmt stmt;
        switch (update.tableMeta().mappingMode()) {
            case SIMPLE:
                stmt = standardSimpleUpdate(update, visible);
                break;
            case PARENT:
                stmt = standardParentUpdate(update, visible);
                break;
            case CHILD:
                stmt = standardChildUpdate(update, visible);
                break;
            default:
                throw DialectUtils.createMappingModeUnknownException(update.tableMeta().mappingMode());
        }
        return stmt;
    }

    private Stmt standardChildUpdate(_StandardUpdate update, final Visible visible) {
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
        List<IPredicate> parentPredicateList = DmlUtils.extractParentPredicatesForUpdate(
                childMeta, childFieldList, update.predicateList());
        parseStandardUpdate(parentContext, parentMeta, update.tableAlias()
                , parentFieldList, parentExpList, parentPredicateList);

        Stmt stmt;
        if (childFieldList.isEmpty()) {
            stmt = parentContext.build();
        } else {
            //3 parse child update sql (optional)
            StandardUpdateContext childContext = StandardUpdateContext.buildChild(update, this.dialect, visible);
            parseStandardUpdate(childContext, childMeta, update.tableAlias()
                    , childFieldList, childExpList, update.predicateList());

            stmt = PairStmt.build(parentContext.build(), childContext.build());
        }
        return stmt;
    }


    private void parseStandardUpdate(StandardUpdateContext context, TableMeta<?> tableMeta, String tableAlias
            , List<FieldMeta<?, ?>> fieldList, List<Expression<?>> valueExpList, List<IPredicate> predicateList) {

        context.sqlBuilder().append("UPDATE");
        tableOnlyModifier(context);
        // append table name and alias
        context.appendTable(tableMeta, tableAlias);
        // set clause
        DmlUtils.standardSimpleUpdateSetClause(context, tableMeta, tableAlias
                , fieldList, valueExpList);
        // where clause
        simpleTableWhereClause(context, tableMeta, tableAlias
                , predicateList);

    }

    private Stmt standardBatchUpdate(_StandardBatchUpdate update, final Visible visible) {
        // create batch update wrapper
        return DmlUtils.createBatchSQLWrapper(
                update.wrapperList()
                , standardGenericUpdate(update, visible)
        );
    }

    private SimpleStmt standardSimpleUpdate(_StandardUpdate update, final Visible visible) {
        StandardUpdateContext context = StandardUpdateContext.build(update, this.dialect, visible);

        parseStandardUpdate(context, update.tableMeta(), update.tableAlias()
                , update.targetFieldList(), update.valueExpList(), update.predicateList());

        return context.build();
    }

    private SimpleStmt standardParentUpdate(_StandardUpdate update, final Visible visible) {
        StandardUpdateContext context = StandardUpdateContext.build(update, this.dialect, visible);
        final ParentTableMeta<?> parentMeta = (ParentTableMeta<?>) update.tableMeta();
        // create parent predicate
        List<IPredicate> parentPredicateList = DmlUtils.createParentPredicates(parentMeta, update.predicateList());

        parseStandardUpdate(context, parentMeta, update.tableAlias()
                , update.targetFieldList(), update.valueExpList(), parentPredicateList);

        return context.build();
    }

    private void simpleTableWhereClause(_TablesSqlContext context, TableMeta<?> tableMeta, String tableAlias
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

    private Stmt standardBatchDelete(_StandardBatchDelete delete, final Visible visible) {
        return DmlUtils.createBatchSQLWrapper(
                delete.wrapperList()
                , standardGenericDelete(delete, visible)
        );
    }

    private Stmt standardGenericDelete(_StandardDelete delete, final Visible visible) {
        Stmt stmt;
        switch (delete.tableMeta().mappingMode()) {
            case SIMPLE:
                stmt = standardSimpleDelete(delete, visible);
                break;
            case PARENT:
                stmt = standardParentDelete(delete, visible);
                break;
            case CHILD:
                stmt = standardChildDelete(delete, visible);
                break;
            default:
                throw DialectUtils.createMappingModeUnknownException(delete.tableMeta().mappingMode());
        }
        return stmt;
    }


    private Stmt standardSimpleDelete(_StandardDelete delete, final Visible visible) {
        StandardDeleteContext context = StandardDeleteContext.build(delete, this.dialect, visible);
        parseStandardDelete(delete.tableMeta(), delete.tableAlias(), delete.predicateList(), context);
        return context.build();
    }

    private Stmt standardParentDelete(_StandardDelete delete, final Visible visible) {
        StandardDeleteContext context = StandardDeleteContext.build(delete, this.dialect, visible);
        final ParentTableMeta<?> parentMeta = (ParentTableMeta<?>) delete.tableMeta();
        // create parent predicate list
        List<IPredicate> parentPredicateList = DmlUtils.createParentPredicates(parentMeta, delete.predicateList());
        parseStandardDelete(parentMeta, delete.tableAlias(), parentPredicateList, context);
        return context.build();
    }

    private Stmt standardChildDelete(_StandardDelete delete, final Visible visible) {
        final ChildTableMeta<?> childMeta = (ChildTableMeta<?>) delete.tableMeta();
        final ParentTableMeta<?> parentMeta = childMeta.parentMeta();
        // 1. extract parent predicate list
        List<IPredicate> parentPredicateList = DmlUtils.extractParentPredicateForDelete(childMeta
                , delete.predicateList());

        //2. create parent delete sql
        StandardDeleteContext parentContext = StandardDeleteContext.buildParent(delete, this.dialect, visible);
        parseStandardDelete(parentMeta, delete.tableAlias(), parentPredicateList, parentContext);

        //3. create child delete sql
        StandardDeleteContext childContext = StandardDeleteContext.buildChild(delete, this.dialect, visible);
        parseStandardDelete(childMeta, delete.tableAlias(), delete.predicateList(), childContext);

        return PairStmt.build(parentContext.build(), childContext.build());
    }

    private void parseStandardDelete(TableMeta<?> tableMeta, String tableAlias, List<IPredicate> predicateList
            , StandardDeleteContext context) {

        SqlBuilder builder = context.sqlBuilder().append("DELETE FROM");
        tableOnlyModifier(context);
        // append table name
        context.appendTable(tableMeta, tableAlias);
        // where clause
        simpleTableWhereClause(context, tableMeta, tableAlias, predicateList);
    }


}
