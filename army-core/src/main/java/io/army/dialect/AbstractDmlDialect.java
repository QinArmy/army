package io.army.dialect;

import io.army.ErrorCode;
import io.army.beans.ObjectWrapper;
import io.army.boot.DomainValuesGenerator;
import io.army.criteria.*;
import io.army.criteria.impl._CriteriaCounselor;
import io.army.criteria.impl.inner.*;
import io.army.meta.*;
import io.army.stmt.PairStmt;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmt;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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
        insert.prepared();
        final Stmt stmt;
        if (insert instanceof _DialectStatement) {
            stmt = handleDialectValueInsert((_ValuesInsert) insert);
        } else {
            stmt = handleStandardValueInsert((_ValuesInsert) insert, visible);
        }
        return stmt;
    }


    @Override
    public final Stmt returningInsert(Insert insert, final Visible visible) {
        insert.prepared();

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
    public final Stmt subQueryInsert(Insert insert, Visible visible) {
        insert.prepared();

        Stmt stmt;
        if (insert instanceof _SubQueryInsert) {
            if (insert instanceof _DialectStatement) {
                stmt = handleDialectSubQueryInsert((_SubQueryInsert) insert);
            } else {
                stmt = handleStandardSubQueryInsert((_SubQueryInsert) insert);
            }
        } else {
            throw _Exceptions.unknownStatement(insert, this.dialect.sessionFactory());
        }
        return stmt;
    }


    /*################################## blow update method ##################################*/

    @Override
    public final Stmt update(Update update, final Visible visible) {
        update.prepared();

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
    public Stmt returningUpdate(Update update, Visible visible) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final Stmt delete(Delete delete, final Visible visible) {
        delete.prepared();

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

    @Override
    public Stmt returningDelete(Delete delete, Visible visible) {
        throw new UnsupportedOperationException();
    }

    /*################################## blow protected template method ##################################*/

    /**
     * @see #valueInsert(Insert, Visible)
     */
    protected Stmt handleDialectValueInsert(final _ValuesInsert insert) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see #subQueryInsert(Insert, Visible)
     */
    protected Stmt handleDialectSubQueryInsert(final _SubQueryInsert insert) {
        throw new UnsupportedOperationException();
    }

    /*################################## blow multiInsert template method ##################################*/




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

    /**
     * @see #createValueInsertContext(_ValuesInsert, byte, List, Visible)
     */
    protected Stmt standardValueInsert(final _ValueInsertContext ctx) {
        final StandardValueInsertContext context = (StandardValueInsertContext) ctx;
        final StandardValueInsertContext parentContext = context.parentContext;
        if (parentContext != null) {
            DmlUtils.appendStandardValueInsert(parentContext);
        }
        DmlUtils.appendStandardValueInsert(context);
        return context.build();
    }

    /**
     * @see #standardValueInsert(_ValueInsertContext)
     */
    protected _ValueInsertContext createValueInsertContext(_ValuesInsert insert, final byte tableIndex
            , List<ObjectWrapper> domainList, Visible visible) {
        return StandardValueInsertContext.create(insert, tableIndex, domainList, this.dialect, visible);
    }


    /*################################## blow protected method ##################################*/

    /*################################## blow private batchInsert method ##################################*/


    /**
     * @see #subQueryInsert(Insert, Visible)
     */
    private Stmt handleStandardSubQueryInsert(final _SubQueryInsert insert) {
        return null;
    }


    /**
     * @see #valueInsert(Insert, Visible)
     */
    private Stmt handleStandardValueInsert(final _ValuesInsert insert, final Visible visible) {
        // assert implementation class is legal
        _CriteriaCounselor.standardInsert(insert);
        final Stmt stmt;
        if (this.sharding) {
            final Map<Byte, List<ObjectWrapper>> domainMap;
            // sharding table and create domain property values.
            domainMap = DmlUtils.insertSharding(this.dialect.sessionFactory(), insert);
            _ValueInsertContext context;
            final List<Stmt> stmtList = new ArrayList<>(domainMap.size());
            for (Map.Entry<Byte, List<ObjectWrapper>> e : domainMap.entrySet()) {
                context = createValueInsertContext(insert, e.getKey(), e.getValue(), visible);
                stmtList.add(standardValueInsert(context));
            }
            stmt = Stmts.group(stmtList);
        } else {
            final DomainValuesGenerator generator = this.dialect.sessionFactory().domainValuesGenerator();
            final boolean migration = insert.migrationData();
            final List<ObjectWrapper> domainList = insert.domainList();
            for (ObjectWrapper domain : domainList) {
                generator.createValues(domain, migration);
            }
            _ValueInsertContext context = createValueInsertContext(insert, (byte) 0, domainList, visible);
            stmt = standardValueInsert(context);
        }
        return stmt;
    }


    private Stmt standardSubQueryInsert(_StandardSubQueryInsert insert, final Visible visible) {
        Stmt stmt;
        if (insert instanceof _StandardChildSubQueryInsert) {
            stmt = standardChildQueryInsert((_StandardChildSubQueryInsert) insert, visible);
        } else {
            SubQueryInsertContext context = SubQueryInsertContext.build(insert, this.dialect, visible);
            parseStandardSimpleSubQueryInsert(context, insert.table(), insert.fieldList(), insert.subQuery());
            stmt = context.build();
        }
        return stmt;
    }

    private PairStmt standardChildQueryInsert(_StandardChildSubQueryInsert insert, final Visible visible) {
        final ChildTableMeta<?> childMeta = insert.table();
        final ParentTableMeta<?> parentMeta = childMeta.parentMeta();

        // firstly ,parse parent insert sql
        SubQueryInsertContext parentContext = SubQueryInsertContext.buildParent(insert, this.dialect, visible);
        parseStandardSimpleSubQueryInsert(parentContext, parentMeta, insert.parentFieldList(), insert.parentSubQuery());

        // secondly ,parse child insert sql
        SubQueryInsertContext childContext = SubQueryInsertContext.buildChild(insert, this.dialect, visible);
        parseStandardSimpleSubQueryInsert(childContext, childMeta, insert.fieldList(), insert.subQuery());

        return PairStmt.build(parentContext.build(), childContext.build());
    }

    private void parseStandardSimpleSubQueryInsert(SubQueryInsertContext context
            , TableMeta<?> physicalTable, List<FieldMeta<?, ?>> fieldMetaList, SubQuery subQuery) {

        DmlUtils.assertSubQueryInsert(fieldMetaList, subQuery);

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
        subQuery.appendSql(context);
    }

    /*################################## blow update private method ##################################*/

    private Stmt standardChildUpdate(_StandardUpdate update, final Visible visible) {
        List<FieldMeta<?, ?>> targetFieldList = update.targetFieldList();
        List<_Expression<?>> valueExpList = update.valueExpList();

        final List<FieldMeta<?, ?>> parentFieldList = new ArrayList<>(), childFieldList = new ArrayList<>();
        final List<_Expression<?>> parentExpList = new ArrayList<>(), childExpList = new ArrayList<>();

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
        List<_Predicate> parentPredicateList = DmlUtils.extractParentPredicatesForUpdate(
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

    private Stmt standardGenericUpdate(_StandardUpdate update, final Visible visible) {
        final TableMeta<?> table = update.tableMeta();
        final Stmt stmt;
        if (table instanceof SimpleTableMeta) {
            stmt = standardSimpleUpdate(update, visible);
        } else if (table instanceof ParentTableMeta) {
            stmt = standardParentUpdate(update, visible);
        } else if (table instanceof ChildTableMeta) {
            stmt = standardChildUpdate(update, visible);
        } else {
            throw _Exceptions.unknownTableType(table);
        }
        return stmt;
    }


    private void parseStandardUpdate(StandardUpdateContext context, TableMeta<?> tableMeta, String tableAlias
            , List<FieldMeta<?, ?>> fieldList, List<_Expression<?>> valueExpList, List<_Predicate> predicateList) {

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
        List<_Predicate> parentPredicateList = DmlUtils.createParentPredicates(parentMeta, update.predicateList());

        parseStandardUpdate(context, parentMeta, update.tableAlias()
                , update.targetFieldList(), update.valueExpList(), parentPredicateList);

        return context.build();
    }

    private void simpleTableWhereClause(_TablesSqlContext context, TableMeta<?> tableMeta, String tableAlias
            , List<_Predicate> predicateList) {

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

        final TableMeta<?> table = delete.tableMeta();
        final Stmt stmt;
        if (table instanceof SimpleTableMeta) {
            stmt = standardSimpleDelete(delete, visible);
        } else if (table instanceof ParentTableMeta) {
            stmt = standardParentDelete(delete, visible);
        } else if (table instanceof ChildTableMeta) {
            stmt = standardChildDelete(delete, visible);
        } else {
            throw _Exceptions.unknownTableType(table);
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
        List<_Predicate> parentPredicateList = DmlUtils.createParentPredicates(parentMeta, delete.predicateList());
        parseStandardDelete(parentMeta, delete.tableAlias(), parentPredicateList, context);
        return context.build();
    }

    private Stmt standardChildDelete(_StandardDelete delete, final Visible visible) {
        final ChildTableMeta<?> childMeta = (ChildTableMeta<?>) delete.tableMeta();
        final ParentTableMeta<?> parentMeta = childMeta.parentMeta();
        // 1. extract parent predicate list
        List<_Predicate> parentPredicateList = DmlUtils.extractParentPredicateForDelete(childMeta
                , delete.predicateList());

        //2. create parent delete sql
        StandardDeleteContext parentContext = StandardDeleteContext.buildParent(delete, this.dialect, visible);
        parseStandardDelete(parentMeta, delete.tableAlias(), parentPredicateList, parentContext);

        //3. create child delete sql
        StandardDeleteContext childContext = StandardDeleteContext.buildChild(delete, this.dialect, visible);
        parseStandardDelete(childMeta, delete.tableAlias(), delete.predicateList(), childContext);

        return PairStmt.build(parentContext.build(), childContext.build());
    }

    private void parseStandardDelete(TableMeta<?> tableMeta, String tableAlias, List<_Predicate> predicateList
            , StandardDeleteContext context) {

        StringBuilder builder = context.sqlBuilder().append("DELETE FROM");
        tableOnlyModifier(context);
        // append table name
        context.appendTable(tableMeta, tableAlias);
        // where clause
        simpleTableWhereClause(context, tableMeta, tableAlias, predicateList);
    }


}
