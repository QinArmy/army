package io.army.dialect;

import io.army.ErrorCode;
import io.army.beans.ObjectWrapper;
import io.army.boot.DomainValuesGenerator;
import io.army.criteria.*;
import io.army.criteria.impl._CriteriaCounselor;
import io.army.criteria.impl.inner.*;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.session.FactoryMode;
import io.army.session.GenericRmSessionFactory;
import io.army.sharding._RouteUtils;
import io.army.stmt.PairStmt;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmt;
import io.army.stmt.Stmts;
import io.army.util.ArrayUtils;
import io.army.util.Assert;
import io.army.util._Exceptions;

import java.util.*;


/**
 * <p>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 */
public abstract class AbstractDmlDialect extends AbstractDMLAndDQL implements DmlDialect {

    private static final Collection<String> FORBID_SET_FIELD = ArrayUtils.asUnmodifiableList(
            _MetaBridge.UPDATE_TIME, _MetaBridge.VERSION);


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
    public final Stmt update(final Update update, final Visible visible) {
        update.prepared();
        final _Update updateStmt = (_Update) update;
        DmlUtils.assertUpdateSetAndWhereClause(updateStmt);
        final Stmt stmt;
        if (updateStmt instanceof _DialectStatement) {
            // assert implementation class is legal
            assertDialectUpdate(updateStmt);
            stmt = dialectUpdate(updateStmt, visible);
        } else if (update instanceof _SingleUpdate) {
            _CriteriaCounselor.assertStandardUpdate(updateStmt);
            if (updateStmt instanceof _BatchUpdate) {
                stmt = standardBatchUpdate((_BatchUpdate) update, visible);
            } else {
                stmt = handleStandardUpdate((_SingleUpdate) updateStmt, visible);
            }
        } else {
            throw _Exceptions.unknownStatement(update, this.dialect.sessionFactory());
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


    protected void assertDialectUpdate(_Update update) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special domain update."
                , database())
        );
    }

    protected Stmt dialectUpdate(_Update update, Visible visible) {
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


    protected Stmt standardChildUpdateContext(final _SingleUpdate update, byte tableIndex, final Visible visible) {
        throw new UnsupportedOperationException();
    }


    protected final List<GenericField<?, ?>> setClause(final _SetClause clause
            , final _UpdateContext context) {

        final List<? extends SetTargetPart> targetPartList = clause.targetParts();
        final List<? extends SetValuePart> valuePartList = clause.valueParts();
        final String tableAlias = clause.tableAlias();
        final char[] safeTableAlias = clause.safeTableAlias();
        final int targetCount = targetPartList.size();

        final Dialect dialect = context.dialect();
        final boolean supportOnlyDefault = dialect.supportOnlyDefault();
        final StringBuilder sqlBuilder = context.sqlBuilder();

        sqlBuilder.append(SET_WORD);
        final boolean supportTableAlias = context.setClauseTableAlias();
        final boolean hasSelfJoin = clause.hasSelfJoint();
        final List<GenericField<?, ?>> conditionFields = new ArrayList<>();
        for (int i = 0; i < targetCount; i++) {
            if (i > 0) {
                sqlBuilder.append(COMMA);
            }
            final SetTargetPart targetPart = targetPartList.get(i);
            final SetValuePart valuePart = valuePartList.get(i);
            if (targetPart instanceof Row) {
                if (!(valuePart instanceof RowSubQuery)) {
                    throw _Exceptions.setTargetAndValuePartNotMatch(targetPart, valuePart);
                }
                this.appendRowTarget(clause, (Row<?>) targetPart, conditionFields, context);
                sqlBuilder.append(EQUAL);
                dialect.subQuery((SubQuery) targetPart, context);
                continue;
            } else if (!(targetPart instanceof GenericField)) {
                throw _Exceptions.unknownSetTargetPart(targetPart);
            } else if (!(valuePart instanceof _Expression)) {
                throw _Exceptions.setTargetAndValuePartNotMatch(targetPart, valuePart);
            }
            final GenericField<?, ?> field = (GenericField<?, ?>) targetPart;
            switch (field.updateMode()) {
                case UPDATABLE:
                    // no-op
                    break;
                case IMMUTABLE:
                    throw _Exceptions.immutableField(field.fieldMeta());
                case ONLY_DEFAULT: {
                    if (!supportOnlyDefault) {
                        throw _Exceptions.dontSupportOnlyDefault(dialect);
                    }
                    conditionFields.add(field);
                }
                break;
                case ONLY_NULL:
                    conditionFields.add(field);
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(field.updateMode());
            }
            if (FORBID_SET_FIELD.contains(field.fieldName())) {
                throw _Exceptions.armyManageField(field.fieldMeta());
            }
            if (hasSelfJoin) {
                if (!(field instanceof LogicalField)) {
                    throw _Exceptions.selfJoinNoLogicField(field);
                }
                if (!tableAlias.equals(((LogicalField<?, ?>) field).tableAlias())) {
                    throw _Exceptions.unknownColumn((LogicalField<?, ?>) field);
                }
            }
            if (supportTableAlias) {
                sqlBuilder.append(Constant.SPACE)
                        .append(safeTableAlias)
                        .append(Constant.POINT);
            }
            sqlBuilder.append(dialect.quoteIfNeed(field.columnName()))
                    .append(EQUAL);

            if (!field.nullable() && ((_Expression<?>) valuePart).nullableExp()) {
                throw _Exceptions.nonNullField(field.fieldMeta());
            }
            ((_Expression<?>) valuePart).appendSql(context);
        }
        return Collections.unmodifiableList(conditionFields);
    }

    /**
     * @see #setClause(_SetClause, _UpdateContext)
     */
    private void appendRowTarget(final _SetClause clause, final Row<?> row
            , List<GenericField<?, ?>> conditionFields, final _UpdateContext context) {
        final StringBuilder sqlBuilder = context.sqlBuilder();
        final Dialect dialect = context.dialect();
        final boolean supportOnlyDefault = dialect.supportOnlyDefault();

        final TableMeta<?> table = clause.table();
        final String tableAlias = clause.tableAlias();
        final char[] safeTableAlias = clause.safeTableAlias();
        final boolean hasSelfJoin = context.setClauseTableAlias(), supportTableAlias = context.setClauseTableAlias();
        sqlBuilder.append(LEFT_BRACKET);
        int index = 0;
        for (GenericField<?, ?> field : row.columnList()) {
            if (index > 0) {
                sqlBuilder.append(COMMA);
            }
            switch (field.updateMode()) {
                case UPDATABLE:
                    // no-op
                    break;
                case IMMUTABLE:
                    throw _Exceptions.immutableField(field.fieldMeta());
                case ONLY_DEFAULT: {
                    if (!supportOnlyDefault) {
                        throw _Exceptions.dontSupportOnlyDefault(dialect);
                    }
                    conditionFields.add(field);
                }
                break;
                case ONLY_NULL: {
                    conditionFields.add(field);
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(field.updateMode());
            }
            if (field.tableMeta() != table) {
                if (field instanceof LogicalField) {
                    throw _Exceptions.unknownColumn((LogicalField<?, ?>) field);
                } else {
                    throw _Exceptions.unknownColumn(tableAlias, field.fieldMeta());
                }
            }
            if (FORBID_SET_FIELD.contains(field.fieldName())) {
                throw _Exceptions.armyManageField(field.fieldMeta());
            }
            if (hasSelfJoin) {
                if (!(field instanceof LogicalField)) {
                    throw _Exceptions.selfJoinNoLogicField(field);
                }
                if (!tableAlias.equals(((LogicalField<?, ?>) field).tableAlias())) {
                    throw _Exceptions.unknownColumn((LogicalField<?, ?>) field);
                }
            }
            sqlBuilder.append(Constant.SPACE);
            if (supportTableAlias) {
                sqlBuilder.append(safeTableAlias)
                        .append(Constant.POINT);
            }
            sqlBuilder.append(dialect.quoteIfNeed(field.columnName()));
            index++;
        }
        sqlBuilder.append(RIGHT_BRACKET);
    }


    /**
     * @see #update(Update, Visible)
     */
    private Stmt handleStandardUpdate(final _SingleUpdate update, final Visible visible) {
        final GenericRmSessionFactory factory = this.dialect.sessionFactory();
        Assert.databaseRoute(update, update.databaseIndex(), factory);

        final TableMeta<?> table = update.table();
        final List<_Predicate> predicateList = update.predicateList();

        byte tableIndex;
        if (factory.factoryMode() == FactoryMode.NO_SHARDING || table.tableCount() == 1) {
            tableIndex = 0;
        } else {
            tableIndex = _RouteUtils.tableRouteFromRouteField(table, predicateList, factory);
        }
        final byte tableRoute = update.tableIndex();
        if (tableIndex < 0) {
            tableIndex = tableRoute;
        } else if (tableRoute >= 0) {
            throw _Exceptions.tableIndexAmbiguity(update, update.tableIndex(), tableIndex);
        }

        final Stmt stmt;
        if (tableIndex < 0) {
            if (tableRoute == Byte.MIN_VALUE) {
                stmt = standardUpdateWithAllRoute(update, visible);
            } else {
                throw _Exceptions.noTableRoute(update, factory);
            }
        } else if (tableIndex >= table.tableCount()) {
            throw _Exceptions.tableIndexParseError(update, table, tableIndex);
        } else {
            if (table instanceof ChildTableMeta) {
                stmt = standardChildUpdateContext(update, tableIndex, visible);
            } else {
                stmt = standardSingleTableUpdate(update, tableIndex, visible);
            }
        }
        return stmt;
    }

    /**
     * @see #handleStandardUpdate(_SingleUpdate, Visible)
     * @see #standardChildUpdateContext(_SingleUpdate, byte, Visible)
     */
    private Stmt standardSingleTableUpdate(final _SingleUpdate update, final byte tableIndex, final Visible visible) {
        final Dialect dialect = this.dialect;
        final SingleUpdateContext context;
        context = SingleUpdateContext.create(update, tableIndex, dialect, visible);

        final SingleTableMeta<?> table = context.table();
        final StringBuilder sqlBuilder = context.sqlBuilder;
        // 1. UPDATE clause
        sqlBuilder.append(UPDATE);
        sqlBuilder.append(Constant.SPACE);
        if (context.tableIndex == 0) {
            sqlBuilder.append(dialect.quoteIfNeed(table.tableName()));
        } else {
            sqlBuilder.append(dialect.quoteIfNeed(table.tableName() + context.tableSuffix()));
        }
        if (dialect.tableAliasAfterAs()) {
            sqlBuilder.append(AS_WORD);
        }
        sqlBuilder.append(Constant.SPACE)
                .append(context.safeTableAlias);

        final List<GenericField<?, ?>> conditionFields;
        //2. set clause
        conditionFields = this.setClause(context, context);
        //3. where clause
        this.dmlWhereClause(context);
        //3.1 append condition update fields
        if (conditionFields.size() > 0) {
            this.conditionUpdate(context.safeTableAlias, conditionFields, context);
        }
        //3.2 append visible
        if (table.containField(_MetaBridge.VISIBLE)) {
            this.visiblePredicate(table, context.safeTableAlias, context);
        }
        return context.build();
    }

    /**
     * @see #handleStandardUpdate(_SingleUpdate, Visible)
     */
    private Stmt standardUpdateWithAllRoute(final _SingleUpdate update, final Visible visible) {
        final TableMeta<?> table = update.table();
        final int tableCount = table.tableCount();
        final List<Stmt> stmtList = new ArrayList<>(tableCount);
        for (int i = 0; i < tableCount; i++) {
            final Stmt stmt;
            if (table instanceof ChildTableMeta) {
                stmt = standardChildUpdateContext(update, (byte) i, visible);
            } else {
                stmt = standardSingleTableUpdate(update, (byte) i, visible);
            }
            stmtList.add(stmt);
        }
        return Stmts.group(stmtList);
    }


    private Stmt standardChildUpdate(_SingleUpdate update, final Visible visible) {
        List<FieldMeta<?, ?>> targetFieldList = update.fieldList();
        List<_Expression<?>> valueExpList = update.valueExpList();

        final List<FieldMeta<?, ?>> parentFieldList = new ArrayList<>(), childFieldList = new ArrayList<>();
        final List<_Expression<?>> parentExpList = new ArrayList<>(), childExpList = new ArrayList<>();

        final ChildTableMeta<?> childMeta = (ChildTableMeta<?>) update.table();
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

    private Stmt standardBatchUpdate(_BatchUpdate update, final Visible visible) {
        // create batch update wrapper
        return DmlUtils.createBatchSQLWrapper(
                update.wrapperList()
                , handleStandardUpdate(update, visible)
        );
    }

    private SimpleStmt standardSimpleUpdate(_SingleUpdate update, final Visible visible) {
        StandardUpdateContext context = StandardUpdateContext.build(update, this.dialect, visible);

        parseStandardUpdate(context, update.table(), update.tableAlias()
                , update.fieldList(), update.valueExpList(), update.predicateList());

        return context.build();
    }

    private SimpleStmt standardParentUpdate(_SingleUpdate update, final Visible visible) {
        StandardUpdateContext context = StandardUpdateContext.build(update, this.dialect, visible);
        final ParentTableMeta<?> parentMeta = (ParentTableMeta<?>) update.table();
        // create parent predicate
        List<_Predicate> parentPredicateList = DmlUtils.createParentPredicates(parentMeta, update.predicateList());

        parseStandardUpdate(context, parentMeta, update.tableAlias()
                , update.fieldList(), update.valueExpList(), parentPredicateList);

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

        final TableMeta<?> table = delete.table();
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
        parseStandardDelete(delete.table(), delete.tableAlias(), delete.predicateList(), context);
        return context.build();
    }

    private Stmt standardParentDelete(_StandardDelete delete, final Visible visible) {
        StandardDeleteContext context = StandardDeleteContext.build(delete, this.dialect, visible);
        final ParentTableMeta<?> parentMeta = (ParentTableMeta<?>) delete.table();
        // create parent predicate list
        List<_Predicate> parentPredicateList = DmlUtils.createParentPredicates(parentMeta, delete.predicateList());
        parseStandardDelete(parentMeta, delete.tableAlias(), parentPredicateList, context);
        return context.build();
    }

    private Stmt standardChildDelete(_StandardDelete delete, final Visible visible) {
        final ChildTableMeta<?> childMeta = (ChildTableMeta<?>) delete.table();
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
