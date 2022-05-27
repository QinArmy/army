package io.army.dialect;

import io.army.ArmyException;
import io.army.criteria.*;
import io.army.criteria.impl._SQLCounselor;
import io.army.criteria.impl.inner.*;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.schema._FieldResult;
import io.army.schema._SchemaResult;
import io.army.schema._TableResult;
import io.army.session.Database;
import io.army.stmt.ParamValue;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmt;
import io.army.stmt.Stmts;
import io.army.util.ArrayUtils;
import io.army.util._Exceptions;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.function.Consumer;


/**
 * <p>
 * This class is base class of all implementation of {@link _Dialect}.
 * </p>
 * <p>
 * Below is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 *
 * @since 1.0
 */
public abstract class _AbstractDialect implements ArmyDialect {


    private static final Collection<String> FORBID_SET_FIELD = ArrayUtils.asUnmodifiableList(
            _MetaBridge.UPDATE_TIME, _MetaBridge.VERSION);

    public final _DialectEnvironment environment;

    protected final Set<String> keyWordSet;

    protected final char identifierQuote;

    protected final boolean identifierCaseSensitivity;

    protected final Dialect dialect;

    protected _AbstractDialect(_DialectEnvironment environment, Dialect dialect) {
        this.environment = environment;
        this.dialect = dialect;
        this.identifierQuote = identifierQuote();
        this.identifierCaseSensitivity = this.identifierCaseSensitivity();
        this.keyWordSet = Collections.unmodifiableSet(createKeyWordSet(environment.serverMeta()));
    }

    /*################################## blow DML batchInsert method ##################################*/


    /**
     * {@inheritDoc}
     */
    @Override
    public final Stmt insert(final Insert insert, final Visible visible) {
        insert.prepared();
        final Stmt stmt;
        if (insert instanceof StandardStatement) {
            _SQLCounselor.assertStandardInsert(insert);
            stmt = handleStandardValueInsert((_ValuesInsert) insert, visible);
        } else {
            assertDialectInsert(insert);
            stmt = handleDialectInsert(insert, visible);
        }
        return stmt;
    }


    /*################################## blow update method ##################################*/

    @Override
    public final Stmt update(final Update update, final Visible visible) {
        update.prepared();
        _DmlUtils.assertUpdateSetAndWhereClause((_Update) update);
        final _UpdateContext context;
        if (update instanceof StandardStatement) {
            // assert implementation is standard implementation.
            _SQLCounselor.assertStandardUpdate(update);
            final StandardUpdateContext singleContext;
            singleContext = StandardUpdateContext.create((_SingleUpdate) update, this, visible);
            final _SetBlock childBlock = singleContext.childBlock();
            if (childBlock == null || childBlock.leftItemList().size() == 0) {
                standardSingleTableUpdate(singleContext);
            } else {
                standardChildUpdate(singleContext);
            }
            context = singleContext;
        } else if (update instanceof _SingleUpdate) {
            // assert implementation class is legal
            assertDialectUpdate(update);
            final SingleUpdateContext context;
            context = SingleUpdateContext.create((_SingleUpdate) update, this, visible);
            dialectSingleUpdate(context);
        } else if (update instanceof _MultiUpdate) {
            assertDialectUpdate(update);
            final _MultiUpdateContext context;
            context = MultiUpdateContext.create((_MultiUpdate) update, this, visible);
            dialectMultiUpdate(context);
        } else {
            throw _Exceptions.unknownStatement(update, this.dialect);
        }
        final Stmt stmt;
        if (update instanceof _BatchDml) {
            stmt = Stmts.batchDml(simpleStmt, ((_BatchDml) update).paramList());
        } else {
            stmt = simpleStmt;
        }
        return stmt;
    }


    @Override
    public final Stmt delete(final Delete delete, final Visible visible) {
        delete.prepared();
        final SimpleStmt singleStmt;
        if (delete instanceof StandardStatement) {
            _SQLCounselor.assertStandardDelete(delete);
            singleStmt = this.handleStandardDelete((_SingleDelete) delete, visible);
        } else if (delete instanceof _SingleDelete) {
            final _SingleDeleteContext context;
            context = StandardDeleteContext.create((_SingleDelete) delete, this, visible);
            singleStmt = this.dialectSingleDelete(context, (_SingleDelete) delete);
        } else {
            throw _Exceptions.unknownStatement(delete, this);
        }
        if (delete instanceof _BatchDml) {
            stmt = Stmts.batchDml(simpleStmt, ((_BatchDml) delete).paramList());
        } else {
            stmt = simpleStmt;
        }
        return stmt;
    }

    @Override
    public final SimpleStmt select(final Select select, final Visible visible) {
        //1. assert prepared
        select.prepared();

        //2. assert select implementation class.
        if (select instanceof StandardQuery) {
            _SQLCounselor.assertStandardQuery(select);
        } else {
            this.assertDialectRowSet(select);
        }

        //3. parse select
        final _SelectContext context;
        if (select instanceof _UnionRowSet) {
            context = UnionSelectContext.create(select, this, visible);
            this.standardUnionQuery((_UnionRowSet) select, context);
        } else {
            context = SimpleSelectContext.create(select, this, visible);
            if (select instanceof StandardQuery) {
                this.standardSimpleQuery((_StandardQuery) select, context);
            } else {
                this.dialectSimpleQuery(context, (_Query) select);
            }
        }
        return context.build();
    }

    @Override
    public final void rowSet(final RowSet rowSet, final _SqlContext original) {
        //3. parse RowSet
        if (rowSet instanceof Select) {
            this.selectStmt((Select) rowSet, original);
        } else if (rowSet instanceof SubQuery) {
            this.subQueryStmt((SubQuery) rowSet, original);
        } else if (rowSet instanceof Values) {
            this.valuesStmt((Values) rowSet, original);
        } else {
            throw _Exceptions.unknownStatement(rowSet, this.dialect);
        }

    }


    @Override
    public final List<String> schemaDdl(final _SchemaResult schemaResult) {
        final DdlDialect ddlDialect;
        ddlDialect = createDdlDialect();

        final List<String> ddlList = new ArrayList<>();
        final List<TableMeta<?>> dropTableList = schemaResult.dropTableList();
        if (dropTableList.size() > 0) {
            ddlDialect.dropTable(dropTableList, ddlList);
        }
        for (TableMeta<?> table : schemaResult.newTableList()) {
            ddlDialect.createTable(table, ddlList);
        }

        List<FieldMeta<?>> newFieldList;
        List<_FieldResult> fieldResultList;
        List<String> indexList;
        for (_TableResult tableResult : schemaResult.changeTableList()) {
            TableMeta<?> table = tableResult.table();
            if (tableResult.comment()) {
                ddlDialect.modifyTableComment(table, ddlList);
            }
            newFieldList = tableResult.newFieldList();
            if (newFieldList.size() > 0) {
                ddlDialect.addColumn(newFieldList, ddlList);
            }
            fieldResultList = tableResult.changeFieldList();
            if (fieldResultList.size() > 0) {
                ddlDialect.modifyColumn(fieldResultList, ddlList);
            }
            indexList = tableResult.newIndexList();
            if (indexList.size() > 0) {
                ddlDialect.createIndex(table, indexList, ddlList);
            }
            indexList = tableResult.changeIndexList();
            if (indexList.size() > 0) {
                ddlDialect.dropIndex(table, indexList, ddlList);
                ddlDialect.createIndex(table, indexList, ddlList);
            }
        }

        final List<String> errorList;
        errorList = ddlDialect.errorMsgList();
        if (errorList.size() > 0) {
            final StringBuilder builder = new StringBuilder(errorList.size() * 10)
                    .append("create ddl occur error:");
            for (String msg : errorList) {
                builder.append('\n')
                        .append(msg);
            }
            throw new ArmyException(builder.toString());
        }
        return Collections.unmodifiableList(ddlList);
    }

    @Override
    public final String quoteIfNeed(final String identifier) {
        final String safeIdentifier;
        if (!this.identifierCaseSensitivity || this.keyWordSet.contains(identifier)) {
            safeIdentifier = this.identifierQuote + identifier + this.identifierQuote;
        } else {
            safeIdentifier = identifier;
        }
        return safeIdentifier;
    }

    @Override
    public final StringBuilder quoteIfNeed(final String identifier, final StringBuilder builder) {
        if (!this.identifierCaseSensitivity || this.keyWordSet.contains(identifier)) {
            builder.append(this.identifierQuote)
                    .append(identifier)
                    .append(this.identifierQuote);
        } else {
            builder.append(identifier);
        }
        return builder;
    }

    @Override
    public final Dialect dialectMode() {
        return this.dialect;
    }


    @Override
    public final Database database() {
        return this.environment.serverMeta().database();
    }

    @Override
    public final boolean isMockEnv() {
        return this.environment instanceof _MockDialects;
    }

    @Override
    public final FieldValueGenerator getFieldValueGenerator() {
        return this.environment.fieldValuesGenerator();
    }

    /**
     * @see #setClause(boolean, _SetBlock, _UpdateContext)
     */
    @Override
    public final void appendArmyManageFieldsToSetClause(final SingleTableMeta<?> table, final String safeTableAlias
            , final _SqlContext context) {

        final StringBuilder sqlBuilder = context.sqlBuilder();
        final _Dialect dialect = context.dialect();
        final FieldMeta<?> updateTime = table.getField(_MetaBridge.UPDATE_TIME);
        final Class<?> javaType = updateTime.javaType();
        final Temporal updateTimeValue;
        if (javaType == LocalDateTime.class) {
            updateTimeValue = LocalDateTime.now();
        } else if (javaType == OffsetDateTime.class) {
            updateTimeValue = OffsetDateTime.now(this.environment.zoneOffset());
        } else if (javaType == ZonedDateTime.class) {
            updateTimeValue = ZonedDateTime.now(this.environment.zoneOffset());
        } else {
            String m = String.format("%s don't support java type[%s]", updateTime, javaType);
            throw new MetaException(m);
        }
        final boolean supportTableAlias = dialect.setClauseTableAlias();
        sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);
        if (supportTableAlias) {
            sqlBuilder.append(safeTableAlias)
                    .append(_Constant.POINT);
        }
        dialect.safeObjectName(updateTime.columnName(), sqlBuilder)
                .append(_Constant.SPACE_EQUAL);

        context.appendParam(ParamValue.build(updateTime.mappingType(), updateTimeValue));

        if (table.containField(_MetaBridge.VERSION)) {
            sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);
            if (supportTableAlias) {
                sqlBuilder.append(safeTableAlias)
                        .append(_Constant.POINT);
            }
            final FieldMeta<?> version = table.getField(_MetaBridge.VERSION);
            final String versionColumnName = dialect.quoteIfNeed(version.columnName());
            sqlBuilder.append(versionColumnName)
                    .append(_Constant.SPACE_EQUAL_SPACE)
                    .append(safeTableAlias)
                    .append(_Constant.POINT)
                    .append(versionColumnName)
                    .append(" + 1");

        }

    }

    @Override
    public final String toString() {
        return String.format("[%s dialect:%s,hash:%s]"
                , this.getClass().getName(), this.dialectMode(), System.identityHashCode(this));
    }

    /*################################## blow protected template method ##################################*/

    /*################################## blow multiInsert template method ##################################*/


    protected abstract Set<String> createKeyWordSet(ServerMeta meta);


    protected abstract boolean supportTableOnly();

    protected abstract char identifierQuote();

    protected abstract boolean identifierCaseSensitivity();

    protected abstract DdlDialect createDdlDialect();


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

    protected void assertDialectRowSet(RowSet query) {
        String m = String.format("%s don't support this dialect select[%s]", this, query.getClass().getName());
        throw new CriteriaException(m);
    }




    /*################################## blow protected method ##################################*/

    /*################################## blow private batchInsert method ##################################*/







    /*################################## blow update private method ##################################*/

    protected Stmt handleDialectInsert(Insert insert, Visible visible) {
        throw new UnsupportedOperationException();
    }

    protected void dialectSingleUpdate(_SingleUpdateContext context) {
        throw new UnsupportedOperationException();
    }

    protected SimpleStmt dialectMultiUpdate(_MultiUpdateContext context) {
        throw new UnsupportedOperationException();
    }

    protected SimpleStmt dialectMultiDelete(_MultiDeleteContext context) {
        throw new UnsupportedOperationException();
    }

    protected SimpleStmt dialectSingleDelete(_SingleDeleteContext context, _SingleDelete stmt) {
        throw new UnsupportedOperationException();
    }

    protected void dialectSimpleQuery(_Query query, _StmtContext context) {
        throw new UnsupportedOperationException();
    }


    /**
     * @see #handleStandardUpdate(_SingleUpdate, Visible)
     */
    protected SimpleStmt standardChildUpdate(_DomainUpdateContext context) {
        throw new UnsupportedOperationException();
    }

    protected SimpleStmt standardChildDelete(_SingleDeleteContext context) {
        throw new UnsupportedOperationException();
    }


    protected void handleDialectTableBlock(_TableBlock block, _SqlContext context) {

    }

    protected void handleDialectTableItem(TableItem tableItem, _SqlContext context) {

    }

    /**
     * @see #selectStmt(Select, _SqlContext)
     */
    protected final void standardUnionQuery(final _UnionRowSet query, final _SqlContext context) {

        query.appendSql(context);

        final List<? extends SortItem> orderByList = query.orderByList();
        if (orderByList.size() > 0) {
            this.orderByClause(orderByList, context);
        }
        this.limitClause(query.offset(), query.rowCount(), context);
    }

    protected void standardLockClause(LockMode lockMode, _SqlContext context) {
        throw new UnsupportedOperationException();
    }


    protected final void lateralSubQuery(SubQuery subQuery, _SqlContext outerContext) {
        //TODO
    }

    protected final void onClause(final List<_Predicate> predicateList, final _SqlContext context) {
        final int size = predicateList.size();
        if (size == 0) {
            throw new CriteriaException("ON clause must not empty");
        }
        final StringBuilder sqlBuilder = context.sqlBuilder();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_AND);
            }
            predicateList.get(i).appendSql(context);
        }

    }


    protected final void withSubQueryAndSpace(final boolean recursive, final List<Cte> cteList
            , final _SqlContext context, final Consumer<Cte> assetConsumer) {
        final int cteSize = cteList.size();
        if (cteSize == 0) {
            return;
        }
        final StringBuilder sqlBuilder = context.sqlBuilder();
        sqlBuilder.append(_Constant.WITH);
        if (recursive) {
            sqlBuilder.append(_Constant.SPACE_RECURSIVE);
        }
        Cte cte;
        List<String> columnAliasList;
        SubQuery subQuery;
        for (int i = 0; i < cteSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            cte = cteList.get(i);
            assetConsumer.accept(cte);
            columnAliasList = cte.columnNameList();
            subQuery = (SubQuery) cte.subStatement();

            sqlBuilder.append(_Constant.SPACE);
            this.quoteIfNeed(cte.name(), sqlBuilder);// cte name

            if (columnAliasList.size() > 0) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
                int aliasCount = 0;
                for (String columnAlias : columnAliasList) {
                    if (aliasCount > 0) {
                        sqlBuilder.append(_Constant.SPACE_COMMA);
                    }
                    this.quoteIfNeed(columnAlias, sqlBuilder);
                    aliasCount++;
                }
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }
            sqlBuilder.append(_Constant.SPACE_AS);
            this.rowSet(subQuery, context);

        }

        sqlBuilder.append(_Constant.SPACE);

    }

    protected final void selectListClause(final List<? extends SelectItem> selectItemList, final _SqlContext context) {
        final StringBuilder builder = context.sqlBuilder();
        final int size = selectItemList.size();
        SelectItem selectItem;
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                builder.append(_Constant.SPACE_COMMA);
            }
            selectItem = selectItemList.get(i);
            if (selectItem instanceof Selection) {
                ((_Selection) selectItem).appendSelection(context);
            } else if (selectItem instanceof SelectionGroup) {
                ((_SelfDescribed) selectItem).appendSql(context);
            } else {
                throw _Exceptions.unknownSelectItem(selectItem);
            }

        }

    }

    protected final void standardFromClause(final List<? extends _TableBlock> tableBlockList, final _SqlContext context) {
        final int size = tableBlockList.size();
        if (size == 0) {
            throw _Exceptions.noFromClause();
        }

        final StringBuilder builder = context.sqlBuilder()
                .append(_Constant.SPACE_FROM);
        final _Dialect dialect = context.dialect();

        final boolean supportTableOnly = this.supportTableOnly();
        for (int i = 0; i < size; i++) {
            final _TableBlock block = tableBlockList.get(i);
            if (i > 0) {
                builder.append(block.jointType().render());
            }
            final TableItem tableItem = block.tableItem();
            if (tableItem instanceof TableMeta) {
                if (supportTableOnly) {
                    builder.append(_Constant.SPACE_ONLY);
                }
                builder.append(_Constant.SPACE);

                dialect.quoteIfNeed(((TableMeta<?>) tableItem).tableName(), builder)
                        .append(_Constant.SPACE_AS_SPACE);
                dialect.quoteIfNeed(block.alias(), builder);
            } else if (tableItem instanceof SubQuery) {
                this.subQueryStmt((SubQuery) tableItem, context);
                builder.append(_Constant.SPACE_AS_SPACE);
                dialect.quoteIfNeed(block.alias(), builder);
            } else {
                this.handleDialectTableItem(tableItem, context);
            }
            if (block instanceof _DialectTableBlock) {
                this.handleDialectTableBlock(block, context);
            }
            if (i == 0) {
                continue;
            }
            final List<_Predicate> onPredicates = block.predicateList();
            final int onSize = onPredicates.size();
            if (onSize > 0) {
                builder.append(_Constant.SPACE_ON);
            }
            for (int j = 0; j < onSize; j++) {
                if (j > 0) {
                    builder.append(_Constant.SPACE_AND);
                }
                onPredicates.get(j).appendSql(context);
            }

        }// for


    }

    protected final void queryWhereClause(final List<_TableBlock> blockList
            , final List<_Predicate> predicateList, final _SqlContext context) {
        final int predicateSize = predicateList.size();
        final Visible visible = context.visible();
        if (predicateSize == 0 && visible == Visible.BOTH) {
            return;
        }
        //1. append where key word
        final StringBuilder builder = context.sqlBuilder();

        if (predicateSize > 0) {
            builder.append(_Constant.SPACE_WHERE);
        }
        //2. append where predicates
        for (int i = 0; i < predicateSize; i++) {
            if (i > 0) {
                builder.append(_Constant.SPACE_AND);
            }
            predicateList.get(i).appendSql(context);
        }

        if (visible == Visible.BOTH) {
            return;
        }
        final Boolean visibleValue = visible.visible;
        assert visibleValue != null;

        final _Dialect dialect = context.dialect();

        final int blockSize = blockList.size();

        TableItem tableItem;
        FieldMeta<?> visibleField;
        _TableBlock block;
        //3. append visible
        for (int i = 0, outputCount = 0; i < blockSize; i++) {
            block = blockList.get(i);
            tableItem = block.tableItem();
            if (!(tableItem instanceof SingleTableMeta)
                    || !((SingleTableMeta<?>) tableItem).containField(_MetaBridge.VISIBLE)) {
                continue;
            }

            if (outputCount > 0 || predicateSize > 0) {
                builder.append(_Constant.SPACE_AND);
            } else {
                builder.append(_Constant.SPACE_WHERE);
            }

            visibleField = ((SingleTableMeta<?>) tableItem).getField(_MetaBridge.VISIBLE);

            builder.append(_Constant.SPACE);

            dialect.quoteIfNeed(block.alias(), builder)
                    .append(_Constant.POINT);

            dialect.quoteIfNeed(visibleField.columnName(), builder)
                    .append(_Constant.SPACE_EQUAL_SPACE)
                    .append(dialect.literal(visibleField, visibleValue));

            outputCount++;

        }// for

    }


    protected final void appendChildJoinParent(final _Block childBlock, final _Block parentBlock) {
        final String safeChildTableAlias = childBlock.safeTableAlias();
        final String safeParentTableAlias = parentBlock.safeTableAlias();
        final StringBuilder builder = parentBlock.sqlBuilder();
        final _Dialect dialect = parentBlock.dialect();

        // 1. child table name
        builder.append(_Constant.SPACE);
        dialect.quoteIfNeed(childBlock.table().tableName(), builder)
                .append(_Constant.SPACE_AS_SPACE)
                .append(safeChildTableAlias);

        //2. join clause
        builder.append(_Constant.SPACE_JOIN_SPACE);
        // append parent table name
        dialect.quoteIfNeed(parentBlock.table().tableName(), builder)
                .append(_Constant.SPACE_AS_SPACE)
                .append(safeParentTableAlias);

        //2.1 on clause
        builder.append(_Constant.SPACE_ON_SPACE)
                .append(safeChildTableAlias)
                .append(_Constant.POINT)
                .append(_MetaBridge.ID)
                .append(_Constant.SPACE_EQUAL_SPACE)
                .append(safeParentTableAlias)
                .append(_Constant.POINT)
                .append(_MetaBridge.ID);
    }

    protected final void groupByClause(final List<? extends SortItem> groupByList, final _SqlContext context) {
        final int size = groupByList.size();
        if (size == 0) {
            return;
        }
        final StringBuilder builder = context.sqlBuilder()
                .append(_Constant.SPACE_GROUP_BY);
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                builder.append(_Constant.SPACE_COMMA);
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
                .append(_Constant.SPACE_HAVING);
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                builder.append(_Constant.SPACE_AND);
            }
            havingList.get(i).appendSql(context);
        }

    }

    protected final void orderByClause(final List<? extends SortItem> orderByList, final _SqlContext context) {
        final int size = orderByList.size();
        if (size == 0) {
            return;
        }
        final StringBuilder builder = context.sqlBuilder()
                .append(_Constant.SPACE_ORDER_BY);

        for (int i = 0; i < size; i++) {
            if (i > 0) {
                builder.append(_Constant.SPACE_COMMA);
            }
            ((_SelfDescribed) orderByList.get(i)).appendSql(context);
        }

    }

    protected final void limitClause(final long offset, final long rowCount, final _SqlContext context) {
        if (offset >= 0 && rowCount >= 0) {
            context.sqlBuilder().append(_Constant.SPACE_LIMIT_SPACE)
                    .append(offset)
                    .append(_Constant.SPACE_COMMA_SPACE)
                    .append(rowCount);
        } else if (rowCount >= 0) {
            context.sqlBuilder().append(_Constant.SPACE_LIMIT_SPACE)
                    .append(rowCount);
        }
    }


    protected final void discriminator(TableMeta<?> table, String safeTableAlias, _SqlContext context) {
        final FieldMeta<?> field;
        if (table instanceof ChildTableMeta) {
            field = ((ChildTableMeta<?>) table).discriminator();
        } else if (table instanceof ParentTableMeta) {
            field = ((ParentTableMeta<?>) table).discriminator();
        } else {
            throw new IllegalArgumentException("table error");
        }
        final _Dialect dialect = context.dialect();
        final StringBuilder builder;
        builder = context.sqlBuilder()
                .append(_Constant.SPACE_AND_SPACE)
                .append(safeTableAlias)
                .append(_Constant.POINT);

        dialect.quoteIfNeed(field.columnName(), builder)
                .append(_Constant.SPACE_EQUAL_SPACE)
                .append(table.discriminatorValue());
    }


    protected final void visiblePredicate(SingleTableMeta<?> table, final @Nullable String safeTableAlias
            , final _SqlContext context) {

        final FieldMeta<?> field = table.getField(_MetaBridge.VISIBLE);
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
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE_AND_SPACE);

            if (safeTableAlias != null) {
                sqlBuilder.append(safeTableAlias)
                        .append(_Constant.POINT);
            }


            dialect.quoteIfNeed(field.columnName(), sqlBuilder)
                    .append(_Constant.SPACE_EQUAL_SPACE)
                    .append(dialect.literal(field, visibleValue));
        }

    }


    protected final void conditionUpdate(final List<TableField> conditionFields, final _SetClause clause) {
        final _SqlContext context = clause.context();
        final StringBuilder sqlBuilder = context.sqlBuilder();
        final _Dialect dialect = context.dialect();
        final boolean supportOnlyDefault, supportTableAlias;
        supportOnlyDefault = dialect.supportOnlyDefault();
        supportTableAlias = clause.supportTableAlias();

        String columnName, safeTableAlias;
        for (TableField field : conditionFields) {
            safeTableAlias = clause.validateField(field);
            columnName = field.columnName();
            sqlBuilder.append(_Constant.SPACE_AND_SPACE);

            if (supportTableAlias) {
                sqlBuilder.append(safeTableAlias)
                        .append(_Constant.POINT);
            }
            dialect.safeObjectName(columnName, sqlBuilder);

            switch (field.updateMode()) {
                case ONLY_NULL:
                    sqlBuilder.append(_Constant.SPACE_IS_NULL);
                    break;
                case ONLY_DEFAULT: {
                    if (!supportOnlyDefault) {
                        throw _Exceptions.dontSupportOnlyDefault(dialect);
                    }
                    sqlBuilder.append(_Constant.SPACE_EQUAL_SPACE)
                            .append(dialect.defaultFuncName())
                            .append(_Constant.SPACE_LEFT_PAREN);

                    if (supportTableAlias) {
                        sqlBuilder.append(safeTableAlias)
                                .append(_Constant.POINT);
                    }
                    dialect.safeObjectName(columnName, sqlBuilder)
                            .append(_Constant.SPACE_RIGHT_PAREN);
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(field.updateMode());
            }

        }
    }


    @Deprecated
    protected final void conditionUpdate(String safeTableAlias, List<TableField> conditionFields
            , _StmtContext context) {

        final StringBuilder sqlBuilder = context.sqlBuilder();
        final _Dialect dialect = context.dialect();
        final boolean supportOnlyDefault = dialect.supportOnlyDefault();
        String columnName;
        for (TableField field : conditionFields) {
            columnName = field.columnName();
            sqlBuilder
                    .append(_Constant.SPACE_AND_SPACE)
                    .append(_Constant.SPACE)
                    .append(safeTableAlias)
                    .append(_Constant.POINT);

            dialect.safeObjectName(columnName, sqlBuilder);

            switch (field.updateMode()) {
                case ONLY_NULL:
                    sqlBuilder.append(_Constant.SPACE_IS_NULL);
                    break;
                case ONLY_DEFAULT: {
                    if (!supportOnlyDefault) {
                        throw _Exceptions.dontSupportOnlyDefault(dialect);
                    }
                    sqlBuilder.append(_Constant.SPACE_EQUAL_SPACE)
                            .append(dialect.defaultFuncName())
                            .append(_Constant.SPACE_LEFT_PAREN)
                            .append(safeTableAlias)
                            .append(_Constant.POINT);

                    dialect.safeObjectName(columnName, sqlBuilder)
                            .append(_Constant.SPACE_RIGHT_PAREN);
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(field.updateMode());
            }

        }

    }


    protected final void dmlWhereClause(final List<_Predicate> predicateList, final _SqlContext context) {
        final int predicateCount = predicateList.size();
        if (predicateCount == 0) {
            throw _Exceptions.noWhereClause(context);
        }
        final StringBuilder sqlBuilder = context.sqlBuilder();
        sqlBuilder.append(_Constant.SPACE_WHERE);
        for (int i = 0; i < predicateCount; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_AND);
            }
            predicateList.get(i).appendSql(context);
        }

    }

    protected final void multiDmlVisible(final List<? extends _TableBlock> blockList, final _StmtContext context) {
        if (context.visible() == Visible.BOTH) {
            return;
        }
        TableItem tableItem;
        String safeTableAlias;
        SingleTableMeta<?> table;
        for (_TableBlock block : blockList) {
            tableItem = block.tableItem();
            if (!(tableItem instanceof SingleTableMeta)) {
                continue;
            }
            table = (SingleTableMeta<?>) tableItem;
            if (!table.containField(_MetaBridge.VISIBLE)) {
                continue;
            }
            safeTableAlias = context.safeTableAlias(table, block.alias());
            this.visiblePredicate(table, safeTableAlias, context);
        }
    }

    protected final List<TableField> singleTableSetClause(final boolean first, final _SetClause clause) {
        final _SqlContext context = clause.context();
        final StringBuilder sqlBuilder = context.sqlBuilder();
        final _Dialect dialect = context.dialect();
        final List<? extends SetLeftItem> leftItemList = clause.leftItemList();
        final List<? extends SetRightItem> rightItemList = clause.rightItemList();

        if (first) {
            sqlBuilder.append(_Constant.SPACE_SET_SPACE);
        } else {
            sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);
        }

        List<TableField> conditionFieldList = null;

        final int itemSize = leftItemList.size();
        SetLeftItem leftItem;
        SetRightItem rightItem;
        TableField field;
        _Expression expression;
        String tableSafeAlias;
        final boolean supportAlias, supportOnlyDefault, supportRow;
        supportAlias = clause.supportTableAlias();
        supportOnlyDefault = dialect.supportOnlyDefault();
        supportRow = clause.supportRow();
        for (int i = 0; i < itemSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);
            }
            leftItem = leftItemList.get(i);
            rightItem = rightItemList.get(i);
            if (leftItem instanceof Row) {
                if (!supportRow) {
                    throw _Exceptions.dontSupportRowLeftItem(dialect.dialectMode());
                }
                if (!(rightItem instanceof SubQuery)) {
                    throw _Exceptions.setTargetAndValuePartNotMatch(leftItem, rightItem);
                }
                this.appendRowItem(((Row) leftItem).fieldList(), clause, conditionFieldList);
                sqlBuilder.append(_Constant.SPACE_EQUAL_SPACE);
                dialect.rowSet((SubQuery) rightItem, context);
                continue;
            }
            field = (TableField) leftItem;
            if (!(rightItem instanceof _Expression)) {
                throw _Exceptions.setTargetAndValuePartNotMatch(leftItem, rightItem);
            }
            tableSafeAlias = clause.validateField(field);
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
                    if (conditionFieldList == null) {
                        conditionFieldList = new ArrayList<>();
                    }
                    conditionFieldList.add(field);
                }
                break;
                case ONLY_NULL: {
                    if (conditionFieldList == null) {
                        conditionFieldList = new ArrayList<>();
                    }
                    conditionFieldList.add(field);
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(field.updateMode());
            }
            expression = (_Expression) rightItem;
            if (!field.nullable() && expression.isNullableValue()) {
                throw _Exceptions.nonNullField(field.fieldMeta());
            }
            if (supportAlias) {
                sqlBuilder.append(tableSafeAlias)
                        .append(_Constant.POINT);
            }
            dialect.safeObjectName(field.columnName(), sqlBuilder)
                    .append(_Constant.SPACE_EQUAL);
            expression.appendSql(context);

        }

        if (clause instanceof _SingleSetClause) {
            final _SingleSetClause singleSetClause = (_SingleSetClause) clause;
            final TableMeta<?> table;
            table = singleSetClause.table();
            if (table instanceof ChildTableMeta) {
                this.appendArmyManageFieldsToSetClause(((ChildTableMeta<?>) table).parentMeta()
                        , singleSetClause.safeTableAlias(), context);
            } else {
                this.appendArmyManageFieldsToSetClause((SingleTableMeta<?>) table
                        , singleSetClause.safeTableAlias(), context);
            }
        }
        if (conditionFieldList == null) {
            conditionFieldList = Collections.emptyList();
        } else if (conditionFieldList.size() == 1) {
            conditionFieldList = Collections.singletonList(conditionFieldList.get(0));
        } else {
            conditionFieldList = Collections.unmodifiableList(conditionFieldList);
        }
        return conditionFieldList;
    }

    protected final List<TableField> multiTableSetClause(final _MultiUpdateContext context) {
        final StringBuilder sqlBuilder = context.sqlBuilder();
        final _Dialect dialect = context.dialect();
        final List<? extends SetLeftItem> leftItemList = context.leftItemList();
        final List<? extends SetRightItem> rightItemList = context.rightItemList();

        sqlBuilder.append(_Constant.SPACE_SET_SPACE);

        List<TableField> conditionFieldList = null;

        final int itemSize = leftItemList.size();
        SetLeftItem leftItem;
        SetRightItem rightItem;
        TableField field;
        _Expression expression;
        String tableSafeAlias;
        final boolean supportOnlyDefault, supportRow;
        supportOnlyDefault = dialect.supportOnlyDefault();
        supportRow = dialect.setClauseSupportRow();
        for (int i = 0; i < itemSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);
            }
            leftItem = leftItemList.get(i);
            rightItem = rightItemList.get(i);
            if (leftItem instanceof Row) {
                if (!supportRow) {
                    throw _Exceptions.dontSupportRowLeftItem(dialect.dialectMode());
                }
                if (!(rightItem instanceof SubQuery)) {
                    throw _Exceptions.setTargetAndValuePartNotMatch(leftItem, rightItem);
                }
                this.appendRowItem(((Row) leftItem).fieldList(), context, conditionFieldList);
                sqlBuilder.append(_Constant.SPACE_EQUAL_SPACE);
                dialect.subQuery((SubQuery) rightItem, context);
                continue;
            }
            field = (TableField) leftItem;
            if (!(rightItem instanceof _Expression)) {
                throw _Exceptions.setTargetAndValuePartNotMatch(leftItem, rightItem);
            }
            tableSafeAlias = context.validateField(field);
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
                    if (conditionFieldList == null) {
                        conditionFieldList = new ArrayList<>();
                    }
                    conditionFieldList.add(field);
                }
                break;
                case ONLY_NULL: {
                    if (conditionFieldList == null) {
                        conditionFieldList = new ArrayList<>();
                    }
                    conditionFieldList.add(field);
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(field.updateMode());
            }
            expression = (_Expression) rightItem;
            if (!field.nullable() && expression.isNullableValue()) {
                throw _Exceptions.nonNullField(field.fieldMeta());
            }
            sqlBuilder.append(tableSafeAlias)
                    .append(_Constant.POINT);
            dialect.safeObjectName(field.columnName(), sqlBuilder)
                    .append(_Constant.SPACE_EQUAL);
            expression.appendSql(context);
        }// for
        // update updateTime and version fields
        context.appendAfterSetClause();

        if (conditionFieldList == null) {
            conditionFieldList = Collections.emptyList();
        } else if (conditionFieldList.size() == 1) {
            conditionFieldList = Collections.singletonList(conditionFieldList.get(0));
        } else {
            conditionFieldList = Collections.unmodifiableList(conditionFieldList);
        }
        return conditionFieldList;
    }


    @Nullable
    private List<TableField> appendRowItem(final List<TableField> fieldList, final _SetClause clause
            , @Nullable List<TableField> conditionFieldList) {
        final _SqlContext context = clause.context();
        final int size = fieldList.size();
        final StringBuilder sqlBuilder = context.sqlBuilder();
        final _Dialect dialect = context.dialect();

        final boolean supportAlias, supportOnlyDefault;
        supportAlias = clause.supportTableAlias();
        supportOnlyDefault = dialect.supportOnlyDefault();

        String tableSafeAlias;
        TableField field;
        sqlBuilder.append(_Constant.SPACE_LEFT_PAREN)
                .append(_Constant.SPACE);
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);
            }
            field = fieldList.get(i);
            tableSafeAlias = clause.validateField(field);
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
                    if (conditionFieldList == null) {
                        conditionFieldList = new ArrayList<>();
                    }
                    conditionFieldList.add(field);
                }
                break;
                case ONLY_NULL: {
                    if (conditionFieldList == null) {
                        conditionFieldList = new ArrayList<>();
                    }
                    conditionFieldList.add(field);
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(field.updateMode());
            }
            if (supportAlias) {
                sqlBuilder.append(tableSafeAlias)
                        .append(_Constant.POINT);
            }
            dialect.safeObjectName(field.columnName(), sqlBuilder);

        }
        sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        return conditionFieldList;
    }


    protected final List<TableField> setClause(final boolean first, final _SetBlock clause, final _UpdateContext context) {

        final List<? extends SetLeftItem> targetPartList = clause.leftItemList();
        final List<? extends SetRightItem> valuePartList = clause.valueParts();
        final String tableAlias = clause.tableAlias(), safeTableAlias = clause.safeTableAlias();
        final int targetCount = targetPartList.size();

        final _Dialect dialect = context.dialect();
        final boolean supportOnlyDefault = dialect.supportOnlyDefault();
        final StringBuilder sqlBuilder = context.sqlBuilder();

        if (first) {
            sqlBuilder.append(_Constant.SPACE_SET);
        } else {
            sqlBuilder.append(_Constant.SPACE_COMMA);
        }
        final boolean supportTableAlias = dialect.setClauseTableAlias();
        final boolean hasSelfJoin = clause.hasSelfJoint();
        final List<TableField> conditionFields = new ArrayList<>();
        for (int i = 0; i < targetCount; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            final SetLeftItem targetPart = targetPartList.get(i);
            final SetRightItem valuePart = valuePartList.get(i);
            if (targetPart instanceof Row) {
                if (!(valuePart instanceof SubQuery)) {
                    throw _Exceptions.setTargetAndValuePartNotMatch(targetPart, valuePart);
                }
                this.appendRowTarget(clause, (Row) targetPart, conditionFields, context);
                sqlBuilder.append(_Constant.SPACE_EQUAL);
                this.subQueryStmt((SubQuery) targetPart, context);
                continue;
            }
            if (!(targetPart instanceof TableField)) {
                throw _Exceptions.unknownSetTargetPart(targetPart);
            } else if (!(valuePart instanceof _Expression)) {
                throw _Exceptions.setTargetAndValuePartNotMatch(targetPart, valuePart);
            }
            final TableField field = (TableField) targetPart;
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
                throw _Exceptions.selfJoinNonQualifiedField(field);
            }
            if (field instanceof QualifiedField && !tableAlias.equals(((QualifiedField<?>) field).tableAlias())) {
                throw _Exceptions.unknownColumn((QualifiedField<?>) field);
            }
            sqlBuilder.append(_Constant.SPACE);
            if (supportTableAlias) {
                sqlBuilder.append(safeTableAlias)
                        .append(_Constant.POINT);
            }
            dialect.safeObjectName(field.columnName(), sqlBuilder)
                    .append(_Constant.SPACE_EQUAL);

            if (!field.nullable() && ((_Expression) valuePart).isNullableValue()) {
                throw _Exceptions.nonNullField(field.fieldMeta());
            }
            ((_Expression) valuePart).appendSql(context);
        }
        final TableMeta<?> table = clause.table();
        if (table instanceof SingleTableMeta) {
            this.appendArmyManageFieldsToSetClause((SingleTableMeta<?>) table, safeTableAlias, context);
        }
        return Collections.unmodifiableList(conditionFields);
    }


    /**
     * @see #rowSet(RowSet, _SqlContext)
     */
    protected final void subQueryStmt(final SubQuery subQuery, final _SqlContext original) {
        //1. assert prepared
        subQuery.prepared();

        //2. assert sub query implementation class.
        if (subQuery instanceof StandardQuery) {
            _SQLCounselor.assertStandardQuery(subQuery);
        } else {
            this.assertDialectRowSet(subQuery);
        }
        final StringBuilder sqlBuilder = original.sqlBuilder();
        //3. parse sub query
        final boolean outerBrackets;
        outerBrackets = !(original instanceof _SubQueryContext) || original instanceof _SimpleQueryContext;

        if (outerBrackets) {
            sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);// append space left bracket before select key word
        }
        if (subQuery instanceof _UnionRowSet) {
            final _UnionQueryContext context;
            if (original instanceof _SubQueryContext && original instanceof _UnionQueryContext) {
                context = (_UnionQueryContext) original;
            } else {
                context = UnionSubQueryContext.create(original);
            }
            this.standardUnionQuery((_UnionRowSet) subQuery, context);
        } else {
            sqlBuilder.append(_Constant.SPACE); //append space before parse sub query
            final SimpleSubQueryContext context;
            context = SimpleSubQueryContext.create(subQuery, original);//create new simple sub query context
            if (subQuery instanceof StandardQuery) {
                this.standardSimpleQuery((_StandardQuery) subQuery, context);
            } else {
                this.dialectSimpleQuery((_Query) subQuery, context);
            }
        }

        if (outerBrackets) {
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);// append space left bracket after sub query end.
        }

    }


    /**
     * @see #rowSet(RowSet, _SqlContext)
     */
    protected final void valuesStmt(final Values values, final _SqlContext original) {
        //1. assert prepared
        values.prepared();

        //2. assert sub query implementation class.
        this.assertDialectRowSet(values);
        throw new UnsupportedOperationException();
    }

    /**
     * @see #rowSet(RowSet, _SqlContext)
     */
    private void selectStmt(final Select select, final _SqlContext original) {
        //1. assert prepared
        select.prepared();

        //2. assert sub query implementation class.
        if (select instanceof StandardQuery) {
            _SQLCounselor.assertStandardQuery(select);
        } else {
            this.assertDialectRowSet(select);
        }
        //3. assert context
        if (!(original instanceof PrimaryQueryContext)) {
            String m = String.format("Non-primary statement couldn't union %s", Select.class.getName());
            throw new CriteriaException(m);
        }
        //4. parse select
        if (select instanceof _UnionRowSet) {
            final _UnionQueryContext context;
            if (original instanceof _UnionQueryContext) {
                context = (_UnionQueryContext) original;
            } else {
                context = UnionSelectContext.create(select, (_SelectContext) original);
            }
            this.standardUnionQuery((_UnionRowSet) select, context);
        } else {
            final StringBuilder builder = original.sqlBuilder();
            if (builder.length() > 0) {
                builder.append(_Constant.SPACE); // append space before select key word
            }
            final SimpleSelectContext context;
            context = SimpleSelectContext.create(select, (_SelectContext) original);//create new simple select context
            if (select instanceof StandardQuery) {
                this.standardSimpleQuery((_StandardQuery) select, context);
            } else {
                this.dialectSimpleQuery((_Query) select, context);
            }
        }

    }


    /**
     * @see #setClause(boolean, _SetBlock, _UpdateContext)
     */
    private void appendRowTarget(final _SetBlock clause, final Row row
            , List<TableField> conditionFields, final _UpdateContext context) {
        final StringBuilder sqlBuilder = context.sqlBuilder();
        final _Dialect dialect = context.dialect();
        final boolean supportOnlyDefault = dialect.supportOnlyDefault();

        final TableMeta<?> table = clause.table();
        final String tableAlias = clause.tableAlias(), safeTableAlias = clause.safeTableAlias();
        final boolean hasSelfJoin = clause.hasSelfJoint(), supportTableAlias = dialect.setClauseTableAlias();
        sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
        int index = 0;
        for (TableField field : row.fieldList()) {
            if (index > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
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
                    throw _Exceptions.unknownColumn((QualifiedField<?>) field);
                } else {
                    throw _Exceptions.unknownColumn(tableAlias, field.fieldMeta());
                }
            }
            if (FORBID_SET_FIELD.contains(field.fieldName())) {
                throw _Exceptions.armyManageField(field.fieldMeta());
            }
            if (hasSelfJoin && !(field instanceof QualifiedField)) {
                throw _Exceptions.selfJoinNonQualifiedField(field);
            }
            if (field instanceof QualifiedField && !tableAlias.equals(((QualifiedField<?>) field).tableAlias())) {
                throw _Exceptions.unknownColumn((QualifiedField<?>) field);
            }
            sqlBuilder.append(_Constant.SPACE);
            if (supportTableAlias) {
                sqlBuilder.append(safeTableAlias)
                        .append(_Constant.POINT);
            }
            sqlBuilder.append(dialect.quoteIfNeed(field.columnName()));
            index++;
        }
        sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
    }


    private void standardSelectClause(List<? extends SQLWords> modifierList, _SqlContext context) {
        final StringBuilder builder = context.sqlBuilder()
                .append(_Constant.SELECT);
        switch (modifierList.size()) {
            case 0:
                //no-op
                break;
            case 1: {
                final SQLWords modifier = modifierList.get(0);
                if (!(modifier instanceof Distinct)) {
                    String m = String.format("Standard query api support only %s", Distinct.class.getName());
                    throw new CriteriaException(m);
                }
                builder.append(modifier.render());
            }
            break;
            default:
                String m = String.format("Standard query api support only %s", Distinct.class.getName());
                throw new CriteriaException(m);
        }

    }


    /**
     * @see #insert(Insert, Visible)
     */
    private Stmt handleStandardValueInsert(final _ValuesInsert insert, final Visible visible) {
        final StandardValueInsertContext context;
        context = StandardValueInsertContext.create(insert, this, visible);
        _DmlUtils.appendStandardValueInsert(context, this.environment.fieldValuesGenerator()); // append parent insert to parent context.
        context.onParentEnd(); // parent end event
        final _InsertBlock childBlock = context.childBlock();
        if (childBlock != null) {
            _DmlUtils.appendStandardValueInsert(context, null); // append child insert to child context.
        }
        return context.build();
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
     * @see #standardChildUpdate(_DomainUpdateContext)
     */
    private void standardSingleTableUpdate(final StandardUpdateContext context) {
        final _SetBlock childContext = context.childBlock();
        if (childContext != null && childContext.leftItemList().size() > 0) {
            throw new IllegalArgumentException("context error");
        }
        final _Dialect dialect = context.dialect;

        final SingleTableMeta<?> table = context.table;
        final StringBuilder sqlBuilder = context.sqlBuilder;
        // 1. UPDATE clause
        sqlBuilder.append(_Constant.UPDATE)
                .append(_Constant.SPACE);

        dialect.safeObjectName(table.tableName(), sqlBuilder);


        if (dialect.tableAliasAfterAs()) {
            sqlBuilder.append(_Constant.SPACE_AS_SPACE);
        }
        sqlBuilder.append(_Constant.SPACE)
                .append(context.safeTableAlias);

        final List<TableField> conditionFields;
        //2. set clause
        conditionFields = this.setClause(true, context, context);
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
        sqlBuilder.append(_Constant.DELETE_FROM_SPACE)
                .append(_Constant.SPACE);

        dialect.safeObjectName(table.tableName(), sqlBuilder);

        if (dialect.tableAliasAfterAs()) {
            sqlBuilder.append(_Constant.SPACE_AS_SPACE);
        }
        sqlBuilder.append(_Constant.SPACE)
                .append(context.safeTableAlias);

        //2. where clause
        this.dmlWhereClause(context);

        //2.1 append discriminator predicate
        if (table instanceof ParentTableMeta) {
            this.discriminator(table, context.safeTableAlias, context);
        }
        //2.2 append visible
        if (table.containField(_MetaBridge.VISIBLE)) {
            this.visiblePredicate(table, context.safeTableAlias, context);
        }
        return context.build();
    }


    private void standardSimpleQuery(_StandardQuery query, _SqlContext context) {
        //1. select clause
        this.standardSelectClause(query.modifierList(), context);
        //2. select list clause
        this.selectListClause(query.selectItemList(), context);
        //3. from clause
        final List<? extends _TableBlock> blockList = query.tableBlockList();
        this.standardFromClause(blockList, context);
        //4. where clause

        this.queryWhereClause(blockList, query.predicateList(), context);

        //5. groupBy clause
        final List<? extends SortItem> groupByList = query.groupByList();
        if (groupByList.size() > 0) {
            this.groupByClause(groupByList, context);
            this.havingClause(query.havingList(), context);
        }
        //6. orderBy clause
        this.orderByClause(query.orderByList(), context);

        //7. limit clause
        this.limitClause(query.offset(), query.rowCount(), context);

        //8. lock clause
        final LockMode lock = query.lockMode();
        if (lock != null) {
            this.standardLockClause(lock, context);
        }

    }



    /*################################## blow delete private method ##################################*/


    protected static IllegalArgumentException illegalDialect() {
        return new IllegalArgumentException("dialect instance error");
    }

}
