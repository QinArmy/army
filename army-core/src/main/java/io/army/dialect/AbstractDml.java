package io.army.dialect;

import io.army.beans.ObjectWrapper;
import io.army.beans.ReadWrapper;
import io.army.boot.DomainValuesGenerator;
import io.army.criteria.*;
import io.army.criteria.impl._CriteriaCounselor;
import io.army.criteria.impl.inner.*;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.session.FactoryMode;
import io.army.session.GenericRmSessionFactory;
import io.army.sharding.RouteMode;
import io.army.sharding._RouteUtils;
import io.army.stmt.ParamValue;
import io.army.stmt.Stmt;
import io.army.stmt.Stmts;
import io.army.util.ArrayUtils;
import io.army.util.Assert;
import io.army.util._Exceptions;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;


/**
 * <p>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 */
public abstract class AbstractDml extends AbstractDMLAndDQL implements DmlDialect {

    protected static final char[] UPDATE = new char[]{'U', 'P', 'D', 'A', 'T', 'E'};

    protected static final char[] INSERT_INTO = new char[]{'I', 'S', 'E', 'R', 'T', 'E', ' ', 'I', 'N', 'T', 'O'};

    private static final Collection<String> FORBID_SET_FIELD = ArrayUtils.asUnmodifiableList(
            _MetaBridge.UPDATE_TIME, _MetaBridge.VERSION);


    protected AbstractDml(Dialect dialect) {
        super(dialect);
    }

    /*################################## blow DML batchInsert method ##################################*/

    /**
     * {@inheritDoc}
     */
    @Override
    public final Stmt valueInsert(final Insert insert, final Visible visible) {
        insert.prepared();
        return handleStandardValueInsert((_ValuesInsert) insert, visible);
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
        throw new UnsupportedOperationException();
    }


    /*################################## blow update method ##################################*/

    @Override
    public final Stmt update(final Update update, final Visible visible) {
        update.prepared();
        final _Update updateStmt = (_Update) update;
        _DmlUtils.assertUpdateSetAndWhereClause(updateStmt);
        final Stmt stmt;
        if (updateStmt instanceof _DialectStatement) {
            // assert implementation class is legal
            assertDialectUpdate(updateStmt);
            stmt = dialectUpdate(updateStmt, visible);
        } else if (update instanceof _SingleUpdate) {
            // assert implementation is standard implementation.
            _CriteriaCounselor.assertStandardUpdate(update);
            if (updateStmt instanceof _BatchUpdate) {
                stmt = handleStandardBatchUpdate((_BatchUpdate) update, visible);
            } else {
                stmt = handleStandardUpdate((_SingleUpdate) update, visible);
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
    public final Stmt delete(final Delete delete, final Visible visible) {
        delete.prepared();
        final Stmt stmt;
        if (delete instanceof _DialectStatement) {
            assertDialectDelete((_Delete) delete);
            stmt = dialectDelete((_Delete) delete, visible);
        } else if (delete instanceof _SingleDelete) {
            _CriteriaCounselor.assertStandardDelete(delete);
            if (delete instanceof _BatchDelete) {
                throw new UnsupportedOperationException();
            } else {
                stmt = this.handleStandardDelete((_SingleDelete) delete, visible);
            }
        } else {
            throw _Exceptions.unknownStatement(delete, this.dialect.sessionFactory());
        }
        return stmt;
    }

    @Override
    public Stmt returningDelete(Delete delete, Visible visible) {
        throw new UnsupportedOperationException();
    }

    /*################################## blow protected template method ##################################*/

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

    protected void assertDialectDelete(_Delete delete) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special delete."
                , database())
        );
    }

    protected Stmt dialectDelete(_Delete delete, Visible visible) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special delete."
                , database())
        );
    }


    protected final Map<Byte, List<ReadWrapper>> batchSingleDmlRoute(final _BatchSingleDml dml) {
        //1. firstly find table index from param expression or literal expression.
        final byte tableIndex;
        tableIndex = singleDmlTableRoute(dml);

        final TableMeta<?> table = dml.table();
        final RouteMode routeMode = table.routeMode();
        final boolean checkDatabaseRoute = this.sharding
                && (routeMode == RouteMode.DATABASE || routeMode == RouteMode.SHARDING);

        FieldMeta<?, ?> tableRouteField = null, databaseRouteField = null;
        for (_Predicate predicate : dml.predicateList()) {
            tableRouteField = predicate.tableRouteField(table);
            if (tableRouteField != null) {
                break;
            }
        }
        if (tableRouteField != null && !tableRouteField.tableRoute()) {
            String m = String.format("%s parse table route error.", _Predicate.class.getName());
            throw new IllegalStateException(m);
        }
        return Collections.emptyMap();
    }


    /**
     * <p>
     * This method get table index for single dml.
     * </p>
     *
     * @return <ul>
     * <li>{@link Byte#MIN_VALUE} route all</li>
     * <li>non-negative : table index</li>
     * <li>-1 not found table index</li>
     * </ul>
     * @throws CriteriaException when return negative and not equal {@link Byte#MIN_VALUE}
     * @see #handleStandardUpdate(_SingleUpdate, Visible)
     * @see #handleStandardDelete(_SingleDelete, Visible)
     */
    protected final byte singleDmlTableRoute(final _SingleDml dml) {
        final TableMeta<?> table = dml.table();
        if (table.immutable()) {
            throw _Exceptions.immutableTable(table);
        }
        final List<_Predicate> predicateList = dml.predicateList();
        final GenericRmSessionFactory factory = this.dialect.sessionFactory();
        Assert.databaseRoute(dml, dml.databaseIndex(), factory);
        if (factory.factoryMode() == FactoryMode.NO_SHARDING || table.routeMode() == RouteMode.NONE) {
            return 0;
        }
        byte tableIndex;
        tableIndex = _RouteUtils.tableRouteFromRouteField(table, predicateList, factory);
        final byte tableRoute = dml.tableIndex();
        if (tableIndex < 0) {
            tableIndex = tableRoute;
        } else if (tableRoute >= 0) {
            throw _Exceptions.tableIndexAmbiguity(dml, dml.tableIndex(), tableIndex);
        }

        if (tableIndex < 0) {
            if (tableRoute == -1) {
                if (!(dml instanceof _BatchDml)) {
                    throw _Exceptions.noTableRoute(dml, factory);
                }
            } else if (tableRoute != Byte.MIN_VALUE) {
                throw _Exceptions.tableIndexParseError(dml, table, tableIndex);
            }
        } else if (tableIndex >= table.tableCount()) {
            throw _Exceptions.tableIndexParseError(dml, table, tableIndex);
        }
        return tableIndex;
    }


    /*################################## blow protected method ##################################*/

    /*################################## blow private batchInsert method ##################################*/

    /**
     * @see #handleStandardValueInsert(_ValuesInsert, Visible)
     */
    private Stmt standardValueInsert(final _ValueInsertContext context) {
        _DmlUtils.appendStandardValueInsert(false, context);
        final _InsertBlock childBlock = context.childBlock();
        if (childBlock != null) {
            _DmlUtils.appendStandardValueInsert(true, context);
        }
        return context.build();
    }


    /**
     * @see #valueInsert(Insert, Visible)
     */
    private Stmt handleStandardValueInsert(final _ValuesInsert insert, final Visible visible) {
        // assert implementation class is legal
        _CriteriaCounselor.standardInsert(insert);
        final TableMeta<?> table = insert.table();
        final Stmt stmt;
        if (this.sharding) {
            final Map<Byte, List<ObjectWrapper>> domainMap;
            // sharding table and create domain property values.
            domainMap = _RouteUtils.insertSharding(this.dialect.sessionFactory(), insert);
            _ValueInsertContext context;
            final List<Stmt> stmtList = new ArrayList<>(domainMap.size());
            for (Map.Entry<Byte, List<ObjectWrapper>> e : domainMap.entrySet()) {
                if (table instanceof ChildTableMeta) {
                    context = ValueInsertContext.child(insert, e.getKey(), e.getValue(), this.dialect, visible);
                } else {
                    context = ValueInsertContext.single(insert, e.getKey(), e.getValue(), this.dialect, visible);
                }
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
            final _ValueInsertContext context;
            if (table instanceof ChildTableMeta) {
                context = ValueInsertContext.child(insert, (byte) 0, domainList, this.dialect, visible);
            } else {
                context = ValueInsertContext.single(insert, (byte) 0, domainList, this.dialect, visible);
            }
            stmt = standardValueInsert(context);
        }
        return stmt;
    }

    /**
     * @see #delete(Delete, Visible)
     */
    private Stmt handleStandardDelete(final _SingleDelete delete, final Visible visible) {
        final GenericRmSessionFactory factory = this.dialect.sessionFactory();
        final byte tableIndex;
        tableIndex = singleDmlTableRoute(delete);

        final Stmt stmt;
        if (tableIndex >= 0) {
            stmt = standardDeleteStmt(delete, tableIndex, visible);
        } else {
            stmt = standardDeleteWithAllRoute(delete, visible);
        }
        return stmt;
    }

    /**
     * @see #handleStandardDelete(_SingleDelete, Visible)
     */
    private Stmt standardDeleteStmt(final _SingleDelete delete, final byte tableIndex, final Visible visible) {
        final Stmt stmt;
        if (delete.table() instanceof ChildTableMeta) {
            final UpdateContext context;
            context = UpdateContext.child(update, tableIndex, this.dialect, visible);
            final _SetBlock childSetClause = context.childSetClause();
            assert childSetClause != null;
            if (childSetClause.targetParts().size() == 0) {
                stmt = standardSingleTableUpdate(context);
            } else {
                stmt = standardChildUpdate(context);
            }
        } else {
            final UpdateContext context;
            context = UpdateContext.single(update, tableIndex, this.dialect, visible);
            stmt = standardSingleTableUpdate(context);
        }
        return stmt;
    }

    /**
     * @see #handleStandardDelete(_SingleDelete, Visible)
     */
    private Stmt standardDeleteWithAllRoute(final _SingleDelete delete, final Visible visible) {
        final TableMeta<?> table = delete.table();
        final int tableCount = table.tableCount();
        final List<Stmt> stmtList = new ArrayList<>(tableCount);
        for (int i = 0; i < tableCount; i++) {
            stmtList.add(standardDeleteStmt(delete, (byte) i, visible));
        }
        return Stmts.group(stmtList);
    }


    /*################################## blow update private method ##################################*/


    /**
     * @see #handleStandardUpdate(_SingleUpdate, Visible)
     */
    protected Stmt standardChildUpdate(_SingleUpdateContext context) {
        throw new UnsupportedOperationException();
    }


    protected final List<GenericField<?, ?>> setClause(final _SetBlock clause, final _UpdateContext context) {

        final List<? extends SetTargetPart> targetPartList = clause.targetParts();
        final List<? extends SetValuePart> valuePartList = clause.valueParts();
        final String tableAlias = clause.tableAlias(), safeTableAlias = clause.safeTableAlias();
        final int targetCount = targetPartList.size();

        final Dialect dialect = context.dialect();
        final boolean supportOnlyDefault = dialect.supportOnlyDefault();
        final StringBuilder sqlBuilder = context.sqlBuilder();

        sqlBuilder.append(SET_WORD);
        final boolean supportTableAlias = dialect.setClauseTableAlias();
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
            if (hasSelfJoin && !(field instanceof LogicalField)) {
                throw _Exceptions.selfJoinNoLogicField(field);
            }
            if (field instanceof LogicalField && !tableAlias.equals(((LogicalField<?, ?>) field).tableAlias())) {
                throw _Exceptions.unknownColumn((LogicalField<?, ?>) field);
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
        final TableMeta<?> table = clause.table();
        if (table instanceof SingleTableMeta) {
            this.appendArmyManageFieldsToSetClause((SingleTableMeta<?>) table, safeTableAlias, context);
        }
        return Collections.unmodifiableList(conditionFields);
    }

    /**
     * @see #setClause(_SetBlock, _UpdateContext)
     */
    private void appendRowTarget(final _SetBlock clause, final Row<?> row
            , List<GenericField<?, ?>> conditionFields, final _UpdateContext context) {
        final StringBuilder sqlBuilder = context.sqlBuilder();
        final Dialect dialect = context.dialect();
        final boolean supportOnlyDefault = dialect.supportOnlyDefault();

        final TableMeta<?> table = clause.table();
        final String tableAlias = clause.tableAlias(), safeTableAlias = clause.safeTableAlias();
        final boolean hasSelfJoin = clause.hasSelfJoint(), supportTableAlias = dialect.setClauseTableAlias();
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
            if (hasSelfJoin && !(field instanceof LogicalField)) {
                throw _Exceptions.selfJoinNoLogicField(field);
            }
            if (field instanceof LogicalField && !tableAlias.equals(((LogicalField<?, ?>) field).tableAlias())) {
                throw _Exceptions.unknownColumn((LogicalField<?, ?>) field);
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
     * @see #setClause(_SetBlock, _UpdateContext)
     */
    private void appendArmyManageFieldsToSetClause(final SingleTableMeta<?> table, final String safeTableAlias
            , final _UpdateContext context) {

        final StringBuilder sqlBuilder = context.sqlBuilder();
        final Dialect dialect = context.dialect();
        final FieldMeta<?, ?> updateTime = table.getField(_MetaBridge.UPDATE_TIME);

        sqlBuilder.append(COMMA)
                .append(Constant.SPACE)
                .append(safeTableAlias)
                .append(Constant.POINT)
                .append(dialect.quoteIfNeed(updateTime.columnName()))
                .append(EQUAL);

        final Class<?> javaType = updateTime.javaType();
        if (javaType == LocalDateTime.class) {
            context.appendParam(ParamValue.build(updateTime, LocalDateTime.now()));
        } else if (javaType == OffsetDateTime.class) {
            final ZoneOffset zoneOffset = dialect.sessionFactory().zoneOffset();
            context.appendParam(ParamValue.build(updateTime, OffsetDateTime.now(zoneOffset)));
        } else if (javaType == ZonedDateTime.class) {
            final ZoneOffset zoneOffset = dialect.sessionFactory().zoneOffset();
            context.appendParam(ParamValue.build(updateTime, ZonedDateTime.now(zoneOffset)));
        } else {
            String m = String.format("%s don't support java type[%s]", updateTime, javaType);
            throw new MetaException(m);
        }

        if (table.containField(_MetaBridge.VERSION)) {
            final FieldMeta<?, ?> version = table.getField(_MetaBridge.VERSION);
            final String versionColumnName = dialect.quoteIfNeed(version.columnName());
            sqlBuilder.append(COMMA)
                    .append(Constant.SPACE)
                    .append(safeTableAlias)
                    .append(Constant.POINT)
                    .append(versionColumnName)
                    .append(EQUAL)
                    .append(Constant.SPACE)
                    .append(safeTableAlias)
                    .append(Constant.POINT)
                    .append(versionColumnName)
                    .append(" + 1");

        }

    }


    /**
     * @see #update(Update, Visible)
     */
    private Stmt handleStandardUpdate(final _SingleUpdate update, final Visible visible) {
        if (update instanceof _BatchDml) {
            throw new IllegalArgumentException("update type error.");
        }
        final byte tableIndex;
        tableIndex = singleDmlTableRoute(update);

        final Stmt stmt;
        if (tableIndex >= 0) {
            stmt = standardUpdateStmt(update, tableIndex, visible);
        } else if (tableIndex == Byte.MIN_VALUE) {
            final TableMeta<?> table = update.table();
            final int tableCount = table.tableCount();
            final List<Stmt> stmtList = new ArrayList<>(tableCount);
            for (int i = 0; i < tableCount; i++) {
                stmtList.add(standardUpdateStmt(update, (byte) i, visible));
            }
            stmt = Stmts.group(stmtList);
        } else {
            throw _Exceptions.databaseRouteError(update, this.dialect.sessionFactory());
        }
        return stmt;
    }

    /**
     * <p>
     * This method parse {@link _SingleUpdate} instance to {@link Stmt}.
     * </p>
     *
     * @see #handleStandardUpdate(_SingleUpdate, Visible)
     */
    private Stmt standardUpdateStmt(final _SingleUpdate update, final byte tableIndex, final Visible visible) {
        final Stmt stmt;
        if (update.table() instanceof ChildTableMeta) {
            final UpdateContext context;
            context = UpdateContext.child(update, tableIndex, this.dialect, visible);
            final _SetBlock childSetClause = context.childSetClause();
            assert childSetClause != null;
            if (childSetClause.targetParts().size() == 0) {
                stmt = standardSingleTableUpdate(context);
            } else {
                stmt = standardChildUpdate(context);
            }
        } else {
            final UpdateContext context;
            context = UpdateContext.single(update, tableIndex, this.dialect, visible);
            stmt = standardSingleTableUpdate(context);
        }
        return stmt;

    }

    /**
     * @see #handleStandardUpdate(_SingleUpdate, Visible)
     * @see #standardChildUpdate(_SingleUpdateContext)
     */
    private Stmt standardSingleTableUpdate(final UpdateContext context) {
        final _SetBlock childSetClause = context.childSetClause();
        if (childSetClause != null && childSetClause.targetParts().size() > 0) {
            throw new IllegalArgumentException("context error");
        }
        final Dialect dialect = this.dialect;

        final SingleTableMeta<?> table = context.table();
        final StringBuilder sqlBuilder = context.sqlBuilder;
        // 1. UPDATE clause
        sqlBuilder.append(UPDATE);
        sqlBuilder.append(Constant.SPACE);
        if (context.tableIndex == 0) {
            sqlBuilder.append(dialect.safeTableName(table.tableName()));
        } else {
            sqlBuilder.append(table.tableName())
                    .append(context.tableSuffix());
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

        //3.1 append discriminator predicate
        if (childSetClause == null) {
            if (table instanceof ParentTableMeta) {
                this.discriminator(table, context.safeTableAlias, context);
            }
        } else {
            final TableMeta<?> childTable = childSetClause.table();
            if (!(childTable instanceof ChildTableMeta)) {
                throw new IllegalArgumentException("context error");
            }
            this.discriminator(childTable, childSetClause.safeTableAlias(), context);
        }

        //3.2 append visible
        if (table.containField(_MetaBridge.VISIBLE)) {
            this.visiblePredicate(table, context.safeTableAlias, context);
        }
        //3.3 append condition update fields
        if (conditionFields.size() > 0) {
            this.conditionUpdate(context.safeTableAlias, conditionFields, context);
        }
        return context.build();
    }

    /**
     * @see #update(Update, Visible)
     */
    private Stmt handleStandardBatchUpdate(final _BatchUpdate update, final Visible visible) {

        //1. firstly find table index from param expression or literal expression.
        final byte tableIndex;
        tableIndex = singleDmlTableRoute(update);
        final TableMeta<?> table = update.table();

        //2. secondly find table route field from name param expression.
        final FieldMeta<?, ?> routeField;
        routeField = _RouteUtils.batchDmlTableRouteField(table, update.predicateList());

        if (tableIndex != -1 && routeField != null) {
            // found tow table route.
            throw _Exceptions.valueRouteAndNamedRouteConflict(update, tableIndex, routeField);
        }
        if (tableIndex == -1 && routeField == null) {
            // not found any table route.
            throw _Exceptions.noTableRoute(update, this.dialect.sessionFactory());
        }

        if (tableIndex == -1) {

        } else if (tableIndex >= 0) {

        } else if (tableIndex == Byte.MIN_VALUE) {

        } else {
            // here bug
            throw new IllegalStateException(String.format("table route[%s] error.", tableIndex));
        }
        return null;
    }


    private Stmt standardBatchUpdate(final _BatchUpdate update, final Visible visible) {
        // create batch update wrapper
        return _DmlUtils.createBatchSQLWrapper(
                update.wrapperList()
                , handleStandardUpdate(update, visible)
        );
    }




    /*################################## blow delete private method ##################################*/


}
