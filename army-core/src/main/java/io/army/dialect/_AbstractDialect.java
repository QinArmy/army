package io.army.dialect;

import io.army.beans.ObjectWrapper;
import io.army.criteria.*;
import io.army.criteria.impl._CriteriaCounselor;
import io.army.criteria.impl.inner.*;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmt;
import io.army.util.ArrayUtils;
import io.army.util._Exceptions;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.*;


/**
 * <p>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 */
public abstract class _AbstractDialect implements _Dialect {

    protected static final char[] UPDATE = new char[]{'U', 'P', 'D', 'A', 'T', 'E'};


    private static final Collection<String> FORBID_SET_FIELD = ArrayUtils.asUnmodifiableList(
            _MetaBridge.UPDATE_TIME, _MetaBridge.VERSION);

    protected final DialectEnvironment environment;

    protected final Set<String> keyWordSet;

    protected _AbstractDialect(DialectEnvironment environment) {
        this.environment = environment;
        this.keyWordSet = Collections.unmodifiableSet(createKeyWordSet());
    }

    /*################################## blow DML batchInsert method ##################################*/


    /**
     * {@inheritDoc}
     */
    @Override
    public final Stmt insert(final Insert insert, final Visible visible) {
        insert.prepared();
        final Stmt stmt;
        if (insert instanceof _DialectStatement) {
            assertDialectInsert(insert);
            stmt = handleDialectInsert(insert, visible);
        } else {
            _CriteriaCounselor.assertStandardInsert(insert);
            stmt = handleStandardValueInsert((_ValuesInsert) insert, visible);
        }
        return stmt;
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
            final SimpleStmt singleStmt;
            singleStmt = handleDialectUpdate(update, visible);
            if (update instanceof _BatchDml) {
                stmt = _DmlUtils.createBatchStmt(singleStmt, ((_BatchDml) update).wrapperList());
            } else {
                stmt = singleStmt;
            }
        } else if (update instanceof _SingleUpdate) {
            // assert implementation is standard implementation.
            _CriteriaCounselor.assertStandardUpdate(update);
            final SimpleStmt singleStmt;
            singleStmt = handleStandardUpdate((_SingleUpdate) update, visible);
            if (update instanceof _BatchDml) {
                stmt = _DmlUtils.createBatchStmt(singleStmt, ((_BatchDml) update).wrapperList());
            } else {
                stmt = singleStmt;
            }
        } else {
            throw _Exceptions.unknownStatement(update, this);
        }
        return stmt;
    }


    @Override
    public final Stmt delete(final Delete delete, final Visible visible) {
        delete.prepared();
        final Stmt stmt;
        if (delete instanceof _DialectStatement) {
            assertDialectDelete(delete);
            final SimpleStmt singleStmt;
            singleStmt = handleDialectDelete(delete, visible);
            if (delete instanceof _BatchDml) {
                stmt = _DmlUtils.createBatchStmt(singleStmt, ((_BatchDml) delete).wrapperList());
            } else {
                stmt = singleStmt;
            }
        } else if (delete instanceof _SingleDelete) {
            _CriteriaCounselor.assertStandardDelete(delete);
            final SimpleStmt simpleStmt;
            simpleStmt = this.handleStandardDelete((_SingleDelete) delete, visible);
            if (delete instanceof _BatchSingleDelete) {
                stmt = _DmlUtils.createBatchStmt(simpleStmt, ((_BatchSingleDelete) delete).wrapperList());
            } else {
                stmt = simpleStmt;
            }
        } else {
            throw _Exceptions.unknownStatement(delete, this);
        }
        return stmt;
    }

    @Override
    public final SimpleStmt select(final Select select, final Visible visible) {
        select.prepared();
        final SimpleStmt stmt;
        if (select instanceof StandardQuery) {
            _CriteriaCounselor.assertStandardSelect(select);
            stmt = this.handleStandardSelect(select, visible);
        } else {
            this.assertDialectSelect(select);
            stmt = this.handleDialectSelect(select, visible);
        }
        return stmt;
    }

    @Override
    public final void select(final Select select, final _SqlContext original) {

    }

    @Override
    public final void subQuery(SubQuery subQuery, _SqlContext original) {

    }

    @Override
    public final boolean isKeyWord(final String identifier) {
        return this.keyWordSet.contains(identifier);
    }


    @Override
    public final String quoteIfNeed(final String identifier) {
        return this.keyWordSet.contains(identifier) ? this.quoteIdentifier(identifier) : identifier;
    }


    @Override
    public final Database database() {
        return this.environment.serverMeta().database();
    }


    /*################################## blow protected template method ##################################*/

    /*################################## blow multiInsert template method ##################################*/


    protected abstract Set<String> createKeyWordSet();

    protected abstract String quoteIdentifier(String identifier);

    protected abstract boolean supportTableOnly();


    /*################################## blow update template method ##################################*/

    protected void assertDialectInsert(Insert insert) {
        String m = String.format("%s don't support this dialect insert[%s]", this, insert.getClass().getName());
        throw new CriteriaException(m);
    }

    protected void assertDialectUpdate(Update update) {
        String m = String.format("%s don't support this dialect update[%s]", this, update.getClass().getName());
        throw new CriteriaException(m);
    }


    /*################################## blow delete template method ##################################*/

    protected void assertDialectDelete(Delete delete) {
        String m = String.format("%s don't support this dialect delete[%s]", this, delete.getClass().getName());
        throw new CriteriaException(m);
    }

    protected void assertDialectSelect(Select select) {
        String m = String.format("%s don't support this dialect select[%s]", this, select.getClass().getName());
        throw new CriteriaException(m);
    }




    /*################################## blow protected method ##################################*/

    /*################################## blow private batchInsert method ##################################*/







    /*################################## blow update private method ##################################*/

    protected Stmt handleDialectInsert(Insert insert, Visible visible) {
        throw new UnsupportedOperationException();
    }

    protected SimpleStmt handleDialectUpdate(Update update, Visible visible) {
        throw new UnsupportedOperationException();
    }

    protected SimpleStmt handleDialectDelete(Delete update, Visible visible) {
        throw new UnsupportedOperationException();
    }

    protected SimpleStmt handleDialectSelect(Select select, Visible visible) {
        throw new UnsupportedOperationException();
    }


    /**
     * @see #handleStandardUpdate(_SingleUpdate, Visible)
     */
    protected SimpleStmt standardChildUpdate(_SingleUpdateContext context) {
        throw new UnsupportedOperationException();
    }

    protected SimpleStmt standardChildDelete(_SingleDeleteContext context) {
        throw new UnsupportedOperationException();
    }


    protected void handleDialectTableBlock(_TableBlock block, _SqlContext context) {

    }

    protected void handleDialectTablePart(TablePart tablePart, _SqlContext context) {

    }

    protected void standardLockClause(LockMode lockMode, _SqlContext context) {

    }

    protected final void fromClause(final List<? extends _TableBlock> tableBlockList, final _SqlContext context) {
        final int size = tableBlockList.size();
        if (size == 0) {
            throw _Exceptions.noFromClause();
        }

        final StringBuilder builder = context.sqlBuilder()
                .append(Constant.SPACE_FROM);
        final _Dialect dialect = context.dialect();

        final boolean supportTableOnly = this.supportTableOnly();
        for (int i = 0, index; i < size; i++) {
            final _TableBlock block = tableBlockList.get(i);
            if (i > 0) {
                builder.append(Constant.SPACE)
                        .append(block.jointType().render());
            }
            final TablePart tablePart = block.table();
            if (tablePart instanceof TableMeta) {
                if (supportTableOnly) {
                    builder.append(Constant.SPACE_ONLY);
                }
                builder.append(Constant.SPACE)
                        .append(dialect.quoteIfNeed(((TableMeta<?>) tablePart).tableName()));
            } else if (tablePart instanceof SubQuery) {
                this.subQuery((SubQuery) tablePart, context);
            } else {
                this.handleDialectTablePart(tablePart, context);
            }
            if (block instanceof _DialectTableBlock) {
                this.handleDialectTableBlock(block, context);
            }
            if (i == 0) {
                continue;
            }
            index = 0;// reset to 0
            for (_Predicate predicate : block.predicates()) {
                if (index > 0) {
                    builder.append(Constant.SPACE_AND);
                }
                predicate.appendSql(context);
                index++;
            }

        }// for


    }

    protected final void queryWhereClause(final List<_Predicate> predicateList, final _SqlContext context) {
        final int size = predicateList.size();
        if (size == 0) {
            return;
        }
        final StringBuilder builder = context.sqlBuilder()
                .append(Constant.SPACE_WHERE);
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                builder.append(Constant.SPACE_AND);
            }
            predicateList.get(i).appendSql(context);
        }
    }


    protected final void groupByClause(final List<SortPart> groupByList, final _SqlContext context) {
        final int size = groupByList.size();
        if (size == 0) {
            throw new IllegalArgumentException("groupByList is empty");
        }
        final StringBuilder builder = context.sqlBuilder()
                .append(Constant.SPACE_GROUP_BY);
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                builder.append(Constant.SPACE_COMMA);
            }
            ((_SelfDescribed) groupByList.get(i)).appendSql(context);
        }

    }

    protected final void havingClause(final List<_Predicate> havingList, final _SqlContext context) {
        final int size = havingList.size();
        if (size == 0) {
            return;
        }
        final StringBuilder builder = context.sqlBuilder()
                .append(Constant.SPACE_HAVING);
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                builder.append(Constant.SPACE_AND);
            }
            havingList.get(i).appendSql(context);
        }

    }

    protected final void orderByClause(final List<SortPart> orderByList, final _SqlContext context) {
        final int size = orderByList.size();
        if (size == 0) {
            return;
        }
        final StringBuilder builder = context.sqlBuilder()
                .append(Constant.SPACE_ORDER_BY);

        for (int i = 0; i < size; i++) {
            if (i > 0) {
                builder.append(Constant.SPACE_COMMA);
            }
            ((_SelfDescribed) orderByList.get(i)).appendSql(context);
        }

    }


    protected final void discriminator(TableMeta<?> table, String safeTableAlias, _StmtContext context) {
        final FieldMeta<?, ?> field;
        if (table instanceof ChildTableMeta) {
            field = ((ChildTableMeta<?>) table).discriminator();
        } else if (table instanceof ParentTableMeta) {
            field = ((ParentTableMeta<?>) table).discriminator();
        } else {
            throw new IllegalArgumentException("table error");
        }
        final _Dialect dialect = context.dialect();
        context.sqlBuilder()
                .append(Constant.SPACE_AND)
                .append(Constant.SPACE)
                .append(safeTableAlias)
                .append(Constant.POINT)
                .append(dialect.quoteIfNeed(field.columnName()))
                .append(Constant.SPACE_EQUAL)
                .append(Constant.SPACE)
                .append(table.discriminatorValue());
    }

    protected final void visiblePredicate(SingleTableMeta<?> table, final String safeTableAlias
            , final _StmtContext context) {

        final FieldMeta<?, ?> field = table.getField(_MetaBridge.VISIBLE);
        final Boolean visibleValue;
        switch (context.visible()) {
            case ONLY_VISIBLE:
                visibleValue = Boolean.TRUE;
                break;
            case ONLY_NON_VISIBLE:
                visibleValue = Boolean.FALSE;
                break;
            case BOTH:
                visibleValue = null;
                break;
            default:
                throw _Exceptions.unexpectedEnum(context.visible());
        }
        if (visibleValue != null) {
            final _Dialect dialect = context.dialect();
            final StringBuilder sqlBuilder = context.sqlBuilder();

            sqlBuilder.append(Constant.SPACE_AND)
                    .append(Constant.SPACE)
                    .append(safeTableAlias)
                    .append(Constant.POINT)
                    .append(dialect.quoteIfNeed(field.columnName()))
                    .append(Constant.SPACE_EQUAL)
                    .append(Constant.SPACE)
                    .append(dialect.literal(field.mappingType(), visibleValue));
        }

    }


    protected final void conditionUpdate(String safeTableAlias, List<GenericField<?, ?>> conditionFields
            , _StmtContext context) {

        final StringBuilder sqlBuilder = context.sqlBuilder();
        final _Dialect dialect = context.dialect();
        final boolean supportOnlyDefault = dialect.supportOnlyDefault();
        for (GenericField<?, ?> field : conditionFields) {
            final char[] safeColumnAlias = dialect.safeColumnName(field.columnName()).toCharArray();
            sqlBuilder
                    .append(Constant.SPACE_AND)
                    .append(Constant.SPACE)
                    .append(safeTableAlias)
                    .append(Constant.POINT)
                    .append(safeColumnAlias);

            switch (field.updateMode()) {
                case ONLY_NULL:
                    sqlBuilder.append(Constant.SPACE_IS_NULL);
                    break;
                case ONLY_DEFAULT: {
                    if (!supportOnlyDefault) {
                        throw _Exceptions.dontSupportOnlyDefault(dialect);
                    }
                    sqlBuilder.append(Constant.SPACE_EQUAL)
                            .append(Constant.SPACE)
                            .append(dialect.defaultFuncName())
                            .append(Constant.SPACE_LEFT_BRACKET)
                            .append(safeTableAlias)
                            .append(Constant.POINT)
                            .append(safeColumnAlias)
                            .append(Constant.SPACE_RIGHT_BRACKET);
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(field.updateMode());
            }

        }

    }


    protected final void dmlWhereClause(_DmlContext context) {
        final List<_Predicate> predicateList = context.predicates();
        final int predicateCount = predicateList.size();
        if (predicateCount == 0) {
            throw _Exceptions.noWhereClause(context);
        }
        final StringBuilder sqlBuilder = context.sqlBuilder();
        sqlBuilder.append(Constant.SPACE_WHERE);
        for (int i = 0; i < predicateCount; i++) {
            if (i > 0) {
                sqlBuilder.append(Constant.SPACE_AND);
            }
            predicateList.get(i).appendSql(context);
        }

    }


    protected final List<GenericField<?, ?>> setClause(final _SetBlock clause, final _UpdateContext context) {

        final List<? extends SetTargetPart> targetPartList = clause.targetParts();
        final List<? extends SetValuePart> valuePartList = clause.valueParts();
        final String tableAlias = clause.tableAlias(), safeTableAlias = clause.safeTableAlias();
        final int targetCount = targetPartList.size();

        final _Dialect dialect = context.dialect();
        final boolean supportOnlyDefault = dialect.supportOnlyDefault();
        final StringBuilder sqlBuilder = context.sqlBuilder();

        sqlBuilder.append(Constant.SPACE_SET);
        final boolean supportTableAlias = dialect.setClauseTableAlias();
        final boolean hasSelfJoin = clause.hasSelfJoint();
        final List<GenericField<?, ?>> conditionFields = new ArrayList<>();
        for (int i = 0; i < targetCount; i++) {
            if (i > 0) {
                sqlBuilder.append(Constant.SPACE_COMMA);
            }
            final SetTargetPart targetPart = targetPartList.get(i);
            final SetValuePart valuePart = valuePartList.get(i);
            if (targetPart instanceof Row) {
                if (!(valuePart instanceof RowSubQuery)) {
                    throw _Exceptions.setTargetAndValuePartNotMatch(targetPart, valuePart);
                }
                this.appendRowTarget(clause, (Row<?>) targetPart, conditionFields, context);
                sqlBuilder.append(Constant.SPACE_EQUAL);
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
            sqlBuilder.append(Constant.SPACE);
            if (supportTableAlias) {
                sqlBuilder.append(safeTableAlias)
                        .append(Constant.POINT);
            }
            sqlBuilder.append(dialect.safeColumnName(field.columnName()))
                    .append(Constant.SPACE_EQUAL);

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
        final _Dialect dialect = context.dialect();
        final boolean supportOnlyDefault = dialect.supportOnlyDefault();

        final TableMeta<?> table = clause.table();
        final String tableAlias = clause.tableAlias(), safeTableAlias = clause.safeTableAlias();
        final boolean hasSelfJoin = clause.hasSelfJoint(), supportTableAlias = dialect.setClauseTableAlias();
        sqlBuilder.append(Constant.SPACE_LEFT_BRACKET);
        int index = 0;
        for (GenericField<?, ?> field : row.columnList()) {
            if (index > 0) {
                sqlBuilder.append(Constant.SPACE_COMMA);
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
        sqlBuilder.append(Constant.SPACE_RIGHT_BRACKET);
    }


    /**
     * @see #setClause(_SetBlock, _UpdateContext)
     */
    private void appendArmyManageFieldsToSetClause(final SingleTableMeta<?> table, final String safeTableAlias
            , final _UpdateContext context) {

        final StringBuilder sqlBuilder = context.sqlBuilder();
        final _Dialect dialect = context.dialect();
        final FieldMeta<?, ?> updateTime = table.getField(_MetaBridge.UPDATE_TIME);
        final boolean supportTableAlias = dialect.setClauseTableAlias();
        sqlBuilder.append(Constant.SPACE_COMMA)
                .append(Constant.SPACE);

        if (supportTableAlias) {
            sqlBuilder.append(safeTableAlias)
                    .append(Constant.POINT);
        }
        sqlBuilder.append(dialect.safeColumnName(updateTime.columnName()))
                .append(Constant.SPACE_EQUAL);

        final Class<?> javaType = updateTime.javaType();
        sqlBuilder.append(Constant.SPACE);
        if (javaType == LocalDateTime.class) {
            sqlBuilder.append(dialect.literal(updateTime, LocalDateTime.now()));
        } else if (javaType == OffsetDateTime.class) {
            sqlBuilder.append(dialect.literal(updateTime, OffsetDateTime.now(this.environment.zoneOffset())));
        } else if (javaType == ZonedDateTime.class) {
            sqlBuilder.append(dialect.literal(updateTime, ZonedDateTime.now(this.environment.zoneOffset())));
        } else {
            String m = String.format("%s don't support java type[%s]", updateTime, javaType);
            throw new MetaException(m);
        }

        if (table.containField(_MetaBridge.VERSION)) {
            final FieldMeta<?, ?> version = table.getField(_MetaBridge.VERSION);
            final String versionColumnName = dialect.quoteIfNeed(version.columnName());
            sqlBuilder.append(Constant.SPACE_COMMA)
                    .append(Constant.SPACE);

            if (supportTableAlias) {
                sqlBuilder.append(safeTableAlias)
                        .append(Constant.POINT);
            }

            sqlBuilder.append(versionColumnName)
                    .append(Constant.SPACE_EQUAL)
                    .append(Constant.SPACE)
                    .append(safeTableAlias)
                    .append(Constant.POINT)
                    .append(versionColumnName)
                    .append(" + 1");

        }

    }


    private void standardSelectClause(List<SQLModifier> modifierList, _SqlContext context) {
        final StringBuilder builder = context.sqlBuilder()
                .append(Constant.SELECT);
        switch (modifierList.size()) {
            case 0:
                //no-op
                break;
            case 1: {
                final SQLModifier modifier = modifierList.get(0);
                if (!(modifier instanceof Distinct)) {
                    String m = String.format("Standard query api support only %s", Distinct.class.getName());
                    throw new CriteriaException(m);
                }
                builder.append(Constant.SPACE)
                        .append(modifier.render());
            }
            break;
            default:
                String m = String.format("Standard query api support only %s", Distinct.class.getName());
                throw new CriteriaException(m);
        }

    }

    private void selectListClause(final List<SelectPart> selectPartList, final _SqlContext context) {
        final StringBuilder builder = context.sqlBuilder();
        final int size = selectPartList.size();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                builder.append(Constant.SPACE)
                        .append(Constant.COMMA);
            }
            ((_SelfDescribed) selectPartList.get(i)).appendSql(context);
        }

    }


    /**
     * @see #insert(Insert, Visible)
     */
    private Stmt handleStandardValueInsert(final _ValuesInsert insert, final Visible visible) {
        final StandardValueInsertContext context;
        context = StandardValueInsertContext.create(insert, this, visible);

        final FieldValuesGenerator generator = this.environment.fieldValuesGenerator();
        final TableMeta<?> table = context.table;
        final boolean migration = insert.migrationData();
        for (ObjectWrapper wrapper : insert.domainList()) {
            generator.generate(table, wrapper, migration);
        }

        _DmlUtils.appendStandardValueInsert(false, context); // append parent insert to parent context.
        context.onParentEnd(); // parent end event

        final _InsertBlock childBlock = context.childBlock();
        if (childBlock != null) {
            _DmlUtils.appendStandardValueInsert(true, context); // append child insert to child context.
        }
        return context.build();
    }


    private SimpleStmt handleStandardSelect(final Select select, final Visible visible) {
        final _SelectContext context;
        if (select instanceof _UnionQuery) {
            context = UnionSelectContext.create(select, null, this, visible);
            ((_UnionQuery) select).appendSql(context);
        } else {
            context = SimpleSelectContext.create(select, this, visible);
            this.standardQuery((_StandardQuery) select, context);
        }
        return context.build();
    }


    /**
     * @see #update(Update, Visible)
     */
    private SimpleStmt handleStandardUpdate(final _SingleUpdate update, final Visible visible) {
        final StandardUpdateContext context;
        context = StandardUpdateContext.create(update, this, visible);
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
     * @see #delete(Delete, Visible)
     */
    private SimpleStmt handleStandardDelete(final _SingleDelete delete, final Visible visible) {
        final StandardDeleteContext context;
        context = StandardDeleteContext.create(delete, this, visible);
        final _Block childBlock = context.childBlock();
        final SimpleStmt stmt;
        if (childBlock == null) {
            stmt = standardSingleTableDelete(context);
        } else {
            stmt = standardChildDelete(context);
        }
        return stmt;
    }


    /**
     * @see #handleStandardUpdate(_SingleUpdate, Visible)
     * @see #standardChildUpdate(_SingleUpdateContext)
     */
    private SimpleStmt standardSingleTableUpdate(final StandardUpdateContext context) {
        final _SetBlock childContext = context.childBlock();
        if (childContext != null && childContext.targetParts().size() > 0) {
            throw new IllegalArgumentException("context error");
        }
        final _Dialect dialect = context.dialect;

        final SingleTableMeta<?> table = context.table;
        final StringBuilder sqlBuilder = context.sqlBuilder;
        // 1. UPDATE clause
        sqlBuilder.append(Constant.UPDATE)
                .append(Constant.SPACE)
                .append(dialect.safeTableName(table.tableName()));

        if (dialect.tableAliasAfterAs()) {
            sqlBuilder.append(Constant.SPACE)
                    .append(Constant.AS);
        }
        sqlBuilder.append(Constant.SPACE)
                .append(context.safeTableAlias);

        final List<GenericField<?, ?>> conditionFields;
        //2. set clause
        conditionFields = this.setClause(context, context);
        //3. where clause
        this.dmlWhereClause(context);

        //3.1 append discriminator predicate
        if (childContext == null) {
            if (table instanceof ParentTableMeta) {
                this.discriminator(table, context.safeTableAlias, context);
            }
        } else {
            this.discriminator(childContext.table(), context.safeTableAlias, context);
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
     * @see #handleStandardDelete(_SingleDelete, Visible)
     */
    private SimpleStmt standardSingleTableDelete(final StandardDeleteContext context) {
        if (context.childBlock != null) {
            throw new IllegalArgumentException();
        }
        final _Dialect dialect = context.dialect;
        final SingleTableMeta<?> table = context.table;
        final StringBuilder sqlBuilder = context.sqlBuilder;

        // 1. DELETE clause
        sqlBuilder.append(Constant.DELETE_FROM)
                .append(Constant.SPACE)
                .append(dialect.safeTableName(table.tableName()));

        if (dialect.tableAliasAfterAs()) {
            sqlBuilder.append(Constant.SPACE)
                    .append(Constant.AS);
        }
        sqlBuilder.append(Constant.SPACE)
                .append(context.safeTableAlias);

        //2. where clause
        this.dmlWhereClause(context);

        //2.1 append discriminator predicate
        if (table instanceof ParentTableMeta) {
            this.discriminator(table, context.safeTableAlias, context);
        }
        return context.build();
    }


    private void standardQuery(_StandardQuery query, _SqlContext context) {
        this.standardSelectClause(query.modifierList(), context);
        this.selectListClause(query.selectPartList(), context);
        this.fromClause(query.tableBlockList(), context);
        this.queryWhereClause(query.predicateList(), context);

        final List<SortPart> groupByList = query.groupPartList();
        if (groupByList.size() > 0) {
            this.groupByClause(groupByList, context);
            this.havingClause(query.havingList(), context);
        }
        this.orderByClause(query.orderByList(), context);

        final StringBuilder builder = context.sqlBuilder();
        final long offset, rowCount;
        offset = query.offset();
        rowCount = query.rowCount();
        if (offset >= 0 && rowCount >= 0) {
            builder.append(Constant.SPACE)
                    .append(Constant.LIMIT)
                    .append(Constant.SPACE)
                    .append(offset)
                    .append(Constant.SPACE)
                    .append(rowCount);
        } else if (rowCount >= 0) {
            builder.append(Constant.SPACE)
                    .append(Constant.LIMIT)
                    .append(Constant.SPACE)
                    .append(rowCount);
        }

        final LockMode lock = query.lockMode();
        if (lock != null) {
            this.standardLockClause(lock, context);
        }

    }



    /*################################## blow delete private method ##################################*/


}
