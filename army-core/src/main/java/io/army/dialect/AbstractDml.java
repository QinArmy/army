package io.army.dialect;

import io.army.beans.ObjectWrapper;
import io.army.beans.ReadWrapper;
import io.army.boot.DomainValuesGenerator;
import io.army.criteria.*;
import io.army.criteria.impl._CriteriaCounselor;
import io.army.criteria.impl.inner.*;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.sharding.RouteMode;
import io.army.stmt.*;
import io.army.util.ArrayUtils;
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
public abstract class AbstractDml extends AbstractDmlAndDql implements DmlDialect {

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
    public final Stmt insert(final Insert insert, final Visible visible) {
        insert.prepared();
        return handleStandardValueInsert((_ValuesInsert) insert, visible);
    }


    /*################################## blow update method ##################################*/

    @Override
    public final Stmt update(final Update update, final Visible visible) {
        update.prepared();
        _DmlUtils.assertUpdateSetAndWhereClause((_Update) update);
        final Stmt stmt;
        if (update instanceof _DialectStatement) {
            // assert implementation class is legal
            assertDialectUpdate(update);
            if (update instanceof _BatchMultiUpdate) {
                stmt = this.handleDialectBatchMultiUpdate((_BatchMultiUpdate) update, visible);
            } else if (update instanceof _MultiUpdate) {
                stmt = this.handleDialectMultiUpdate((_MultiUpdate) update, visible);
            } else if (update instanceof _BatchSingleUpdate) {
                stmt = this.handleDialectBatchSingleUpdate((_BatchSingleUpdate) update, visible);
            } else if (update instanceof _SingleUpdate) {
                stmt = this.handleDialectSingleUpdate((_SingleUpdate) update, visible);
            } else {
                throw _Exceptions.unknownStatement(update, this.dialect.sessionFactory());
            }
        } else if (update instanceof _SingleUpdate) {
            // assert implementation is standard implementation.
            _CriteriaCounselor.assertStandardUpdate(update);
            final SimpleStmt singleStmt;
            singleStmt = handleStandardUpdate((_SingleUpdate) update, visible);
            if (update instanceof _BatchSingleUpdate) {
                stmt = _DmlUtils.createBatchStmt(singleStmt, ((_BatchSingleUpdate) update).wrapperList());
            } else {
                stmt = singleStmt;
            }
        } else {
            throw _Exceptions.unknownStatement(update, this.dialect.sessionFactory());
        }
        return stmt;
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
            final SimpleStmt simpleStmt;
            simpleStmt = this.handleStandardDelete((_SingleDelete) delete, visible);
            if (delete instanceof _BatchDelete) {
                stmt = _DmlUtils.createBatchStmt(simpleStmt, ((_BatchDelete) delete).wrapperList());
            } else {
                stmt = simpleStmt;
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


    protected void assertDialectUpdate(Update update) {
        throw new UnsupportedOperationException(String.format("dialect[%s] not support special domain update."
                , database())
        );
    }

    protected SimpleStmt dialectSingleUpdate(_SingleUpdate update, byte tableIndex, Visible visible) {
        throw new UnsupportedOperationException(String.format("dialect [%s] not support special update."
                , database())
        );
    }

    protected SimpleStmt dialectMultiUpdate(_MultiUpdate update, byte tableIndex, Visible visible) {
        throw new UnsupportedOperationException(String.format("dialect [%s] not support special update."
                , database())
        );
    }

    protected boolean dialectMultiUpdateFromSubQuery() {
        throw new UnsupportedOperationException();
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



    /*################################## blow protected method ##################################*/

    /*################################## blow private batchInsert method ##################################*/

    /**
     * @return {@link BatchStmt} or {@link GroupStmt} consist of {@link BatchStmt}
     * @see #update(Update, Visible)
     */
    private Stmt handleDialectBatchMultiUpdate(final _BatchMultiUpdate update, final Visible visible) {
        final Stmt stmt;
        if (this.sharding) {
            final Map<Byte, List<ReadWrapper>> wrapperMap;
            // sharding wrapperList
            wrapperMap = batchMultiDmlPrimaryRoute(update);
            final List<Stmt> stmtList = new ArrayList<>();
            for (Map.Entry<Byte, List<ReadWrapper>> e : wrapperMap.entrySet()) {
                final SimpleStmt temp;
                temp = this.dialectMultiUpdate(update, e.getKey(), visible);
                stmtList.add(_DmlUtils.createBatchStmt(temp, e.getValue()));
            }
            stmt = Stmts.group(stmtList);
        } else {
            final SimpleStmt temp;
            temp = this.dialectMultiUpdate(update, (byte) 0, visible);
            stmt = _DmlUtils.createBatchStmt(temp, update.wrapperList());
        }
        return stmt;
    }

    /**
     * @see #update(Update, Visible)
     */
    private Stmt handleDialectMultiUpdate(final _MultiUpdate update, final Visible visible) {
        if (update instanceof _BatchDml) {
            throw new IllegalArgumentException("update type error.");
        }
        final byte tableIndex;
        tableIndex = this.multiDmlPrimaryTaleRoute(update);

        final Stmt stmt;
        if (tableIndex >= 0) {
            stmt = this.dialectMultiUpdate(update, tableIndex, visible);
        } else if (tableIndex == Byte.MIN_VALUE) {
            final TableMeta<?> table = primaryTable(update.tableBlockList());
            final int tableCount = table.tableCount();
            final List<Stmt> stmtList = new ArrayList<>(tableCount);
            for (int i = 0; i < tableCount; i++) {
                stmtList.add(this.dialectMultiUpdate(update, (byte) i, visible));
            }
            stmt = Stmts.group(stmtList);
        } else {
            throw _Exceptions.databaseRouteError(update, this.dialect.sessionFactory());
        }
        return stmt;
    }


    /**
     * @return {@link BatchStmt} or {@link GroupStmt} consist of {@link BatchStmt}
     * @see #update(Update, Visible)
     */
    private Stmt handleDialectBatchSingleUpdate(final _BatchSingleUpdate update, final Visible visible) {
        final TableMeta<?> table = update.table();
        final RouteMode routeMode = table.routeMode();
        final Stmt stmt;
        if (this.sharding && (routeMode == RouteMode.TABLE || routeMode == RouteMode.SHARDING)) {
            final Map<Byte, List<ReadWrapper>> wrapperMap;
            // sharding wrapperList
            wrapperMap = batchSingleDmlRoute(update);

            final List<Stmt> stmtList = new ArrayList<>();
            for (Map.Entry<Byte, List<ReadWrapper>> e : wrapperMap.entrySet()) {
                final SimpleStmt temp;
                temp = this.dialectSingleUpdate(update, e.getKey(), visible);
                stmtList.add(_DmlUtils.createBatchStmt(temp, e.getValue()));
            }
            stmt = Stmts.group(stmtList);
        } else {
            if (routeMode == RouteMode.DATABASE) {
                //TODO check database index
                throw new UnsupportedOperationException();
            }
            final SimpleStmt temp;
            temp = this.dialectSingleUpdate(update, (byte) 0, visible);
            stmt = _DmlUtils.createBatchStmt(temp, update.wrapperList());
        }
        return stmt;
    }

    /**
     * @return {@link SimpleStmt} or {@link GroupStmt} consist of {@link SimpleStmt}
     * @see #update(Update, Visible)
     */
    private Stmt handleDialectSingleUpdate(final _SingleUpdate update, final Visible visible) {
        if (update instanceof _BatchDml) {
            throw new IllegalArgumentException("update type error.");
        }
        final byte tableIndex;
        tableIndex = this.singleDmlTableRoute(update);

        final Stmt stmt;
        if (tableIndex >= 0) {
            stmt = this.dialectSingleUpdate(update, tableIndex, visible);
        } else if (tableIndex == Byte.MIN_VALUE) {
            final TableMeta<?> table = update.table();
            final int tableCount = table.tableCount();
            final List<Stmt> stmtList = new ArrayList<>(tableCount);
            for (int i = 0; i < tableCount; i++) {
                stmtList.add(this.dialectSingleUpdate(update, (byte) i, visible));
            }
            stmt = Stmts.group(stmtList);
        } else {
            throw _Exceptions.databaseRouteError(update, this.dialect.sessionFactory());
        }
        return stmt;
    }


    /**
     * @see #insert(Insert, Visible)
     */
    private Stmt handleStandardValueInsert(final _ValuesInsert insert, final Visible visible) {
        final DomainValuesGenerator generator = this.dialect.sessionFactory().domainValuesGenerator();
        final boolean migration = insert.migrationData();
        final List<ObjectWrapper> domainList = insert.domainList();
        for (ObjectWrapper domain : domainList) {
            generator.createValues(domain, migration);
        }
        final _ValueInsertContext context;
        context = StandardValueInsertContext.create(insert, this.dialect, visible);
        _DmlUtils.appendStandardValueInsert(false, context); // append parent insert to parent context.
        final _InsertBlock childBlock = context.childBlock();
        if (childBlock != null) {
            _DmlUtils.appendStandardValueInsert(true, context); // append child insert to child context.
        }
        return context.build();
    }

    /**
     * @see #delete(Delete, Visible)
     */
    private SimpleStmt handleStandardDelete(final _SingleDelete delete, final Visible visible) {
        final _SingleDeleteContext context;
        context = StandardDeleteContext.create(delete, this.dialect, visible);
        final _Block childBlock = context.childBlock();
        final Stmt stmt;
        if (childBlock == null) {
        } else {
            final StandardUpdateContext context;
            context = StandardUpdateContext.single(update, tableIndex, this.dialect, visible);
            stmt = standardSingleTableUpdate(context);
        }
        return stmt;
    }


    /*################################## blow update private method ##################################*/


    /**
     * @see #handleStandardUpdate(_SingleUpdate, Visible)
     */
    protected SimpleStmt standardChildUpdate(_SingleUpdateContext context) {
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
            if (hasSelfJoin && !(field instanceof QualifiedField)) {
                throw _Exceptions.selfJoinNoLogicField(field);
            }
            if (field instanceof QualifiedField && !tableAlias.equals(((QualifiedField<?, ?>) field).tableAlias())) {
                throw _Exceptions.unknownColumn((QualifiedField<?, ?>) field);
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
                if (field instanceof QualifiedField) {
                    throw _Exceptions.unknownColumn((QualifiedField<?, ?>) field);
                } else {
                    throw _Exceptions.unknownColumn(tableAlias, field.fieldMeta());
                }
            }
            if (FORBID_SET_FIELD.contains(field.fieldName())) {
                throw _Exceptions.armyManageField(field.fieldMeta());
            }
            if (hasSelfJoin && !(field instanceof QualifiedField)) {
                throw _Exceptions.selfJoinNoLogicField(field);
            }
            if (field instanceof QualifiedField && !tableAlias.equals(((QualifiedField<?, ?>) field).tableAlias())) {
                throw _Exceptions.unknownColumn((QualifiedField<?, ?>) field);
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
    private SimpleStmt handleStandardUpdate(final _SingleUpdate update, final Visible visible) {
        final StandardUpdateContext context;
        context = StandardUpdateContext.create(update, this.dialect, visible);
        final _SetBlock childBlock = context.childBlock();
        final SimpleStmt stmt;
        if (childBlock == null || childBlock.targetParts().size() == 0) {
            stmt = standardSingleTableUpdate(context);
        } else {
            stmt = standardChildUpdate(context);
        }
        return stmt;
    }


    /**
     * @see #handleStandardUpdate(_SingleUpdate, Visible)
     * @see #standardChildUpdate(_SingleUpdateContext)
     */
    private SimpleStmt standardSingleTableUpdate(final StandardUpdateContext context) {
        final _SetBlock childSetClause = context.childBlock();
        if (childSetClause != null && childSetClause.targetParts().size() > 0) {
            throw new IllegalArgumentException("context error");
        }
        final Dialect dialect = this.dialect;

        final SingleTableMeta<?> table = context.table();
        final StringBuilder sqlBuilder = context.sqlBuilder;
        // 1. UPDATE clause
        sqlBuilder.append(UPDATE);
        sqlBuilder.append(Constant.SPACE);
        sqlBuilder.append(dialect.safeTableName(table.tableName()));
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



    /*################################## blow delete private method ##################################*/


}
