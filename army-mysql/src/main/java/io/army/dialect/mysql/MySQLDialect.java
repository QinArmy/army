package io.army.dialect.mysql;

import io.army.criteria.*;
import io.army.criteria.impl._JoinType;
import io.army.criteria.impl._MySQLCounselor;
import io.army.criteria.impl.inner.*;
import io.army.criteria.impl.inner.mysql.*;
import io.army.criteria.mysql.MySQLWords;
import io.army.dialect.*;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.SimpleTableMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;
import io.army.modelgen._MetaBridge;
import io.army.stmt.SimpleStmt;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.List;

/**
 * <p>
 * This class is the implementation of {@link _Dialect} for  MySQL dialect criteria api.
 * </p>
 * <p>
 * Below is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 *
 * @since 1.0
 */
final class MySQLDialect extends MySQL {

    private static final String SPACE_HINT_START = " /*+";

    private static final String SPACE_HINT_END = " */";

    private static final String SPACE_PARTITION_START = " PARTITION ( ";

    private static final String SPACE_WITH_ROLLUP = " WITH ROLLUP";

    private static final String SPACE_INTO = " INTO";

    private final boolean asOf80;

    MySQLDialect(_DialectEnvironment environment, Dialect dialect) {
        super(environment, dialect);
        this.asOf80 = this.dialect().version() >= Dialect.MySQL80.version();
    }

    @Override
    protected void assertDialectInsert(Insert insert) {
        super.assertDialectInsert(insert);
    }

    @Override
    protected void assertDialectUpdate(Update update) {
        _MySQLCounselor.assertUpdate(update);
    }

    @Override
    protected void assertDialectDelete(Delete delete) {
        _MySQLCounselor.assertDelete(delete);
    }

    @Override
    protected void assertDialectRowSet(RowSet rowSet) {
        _MySQLCounselor.assertRowSet(rowSet);
    }

    @Override
    protected void dialectSimpleQuery(final _Query query, final _StmtContext context) {
        if (context.dialect() != this) {
            throw illegalDialect();
        }
        final _MySQL80Query stmt = (_MySQL80Query) query;
        final StringBuilder sqlBuilder = context.sqlBuilder();

        //1. WITH clause
        this.mySqlWithClauseAndSpace(stmt.isRecursive(), stmt.cteList(), context);
        //2. SELECT key word
        sqlBuilder.append(_Constant.SELECT);
        //3. hint comment block
        this.hintClause(stmt.hintList(), sqlBuilder, context);
        //4. modifier
        this.selectModifiers(stmt.modifierList(), sqlBuilder);

        //5. select item list clause
        this.selectListClause(stmt.selectItemList(), context);
        final List<_TableBlock> tableBlockList;
        tableBlockList = stmt.tableBlockList();
        //6. FROM clause
        this.mysqlTableReferences(tableBlockList, context, false);
        //7. WHERE clause
        this.queryWhereClause(tableBlockList, stmt.predicateList(), context);
        //8. GROUP clause
        final List<? extends SortItem> groupByList;
        groupByList = stmt.groupByList();
        if (groupByList.size() > 0) {
            this.groupByClause(groupByList, context);
            if (stmt.groupByWithRollUp()) {
                //8.1 WITH ROLLUP clause
                sqlBuilder.append(SPACE_WITH_ROLLUP);
            }
            //8.2 HAVING clause
            this.havingClause(stmt.havingList(), context);
        }


        //9. WINDOW clause
        this.windowClause(stmt.windowList(), context);
        //10. ORDER BY clause
        final List<? extends SortItem> orderByList;
        orderByList = stmt.orderByList();
        if (orderByList.size() > 0) {
            this.orderByClause(orderByList, context);
            if (stmt.orderByWithRollup()) {
                if (!this.asOf80) {
                    throw dontSupportOrderByWithRollup();
                }
                //10.1 WITH ROLLUP clause
                sqlBuilder.append(SPACE_WITH_ROLLUP);
            }
        }

        //11. LIMIT clause
        this.limitClause(stmt.offset(), stmt.rowCount(), context);

        final List<String> intoList;
        intoList = stmt.intoVarList();
        final int intoSize = intoList.size();

        if (!this.asOf80 && intoSize > 0) {
            //12. prior to MySQL 8.0 into clause
            this.intoClause(intoList, sqlBuilder);
        }

        final SQLWords lockMode;
        lockMode = stmt.lockMode();
        if (lockMode != null) {
            //13. LOCK clause
            this.lockClause(lockMode, stmt.ofTableList(), stmt.lockOption(), sqlBuilder);
        }

        if (this.asOf80 && intoSize > 0) {
            //14. as of MySQL 8.0 into clause
            this.intoClause(intoList, sqlBuilder);
        }

    }

    @Override
    protected SimpleStmt dialectSingleUpdate(final _SingleUpdateContext context) {
        final _MySQLSingleUpdate stmt = (_MySQLSingleUpdate) context.statement();
        if (context.dialect() != this) {
            throw illegalDialect();
        }
        final StringBuilder sqlBuilder = context.sqlBuilder();
        //1. WITH clause
        this.mySqlWithClauseAndSpace(stmt.isRecursive(), stmt.cteList(), context);

        //2. UPDATE key word
        sqlBuilder.append(_Constant.UPDATE);

        //3. hint comment block
        this.hintClause(stmt.hintList(), sqlBuilder, context);
        //4. modifier
        this.updateModifiers(stmt.modifierList(), sqlBuilder);

        //5. table name
        final TableMeta<?> table = context.table();
        final SingleTableMeta<?> targetTable;
        final String safeTableAlias = context.safeTableAlias();
        if (table instanceof ChildTableMeta) {
            targetTable = ((ChildTableMeta<?>) table).parentMeta();
        } else {
            targetTable = (SingleTableMeta<?>) table;
        }
        sqlBuilder.append(_Constant.SPACE);
        this.safeObjectName(targetTable.tableName(), sqlBuilder);

        //6. partition
        this.partitionClause(stmt.partitionList(), sqlBuilder);
        //7. table alias

        sqlBuilder.append(_Constant.SPACE_AS_SPACE).append(safeTableAlias);

        //8. index hint
        this.indexHintClause(stmt.indexHintList(), sqlBuilder);
        //9. set clause
        final List<TableField<?>> conditionFields;
        conditionFields = this.singleTableSetClause(true, context);
        //10. where clause
        this.dmlWhereClause(context);
        //10.1 discriminator
        if (!(table instanceof SimpleTableMeta)) {
            this.discriminator(table, safeTableAlias, context);
        }
        //10.2 append condition update fields
        if (conditionFields.size() > 0) {
            this.conditionUpdate(conditionFields, context);
        }
        //10.3 append visible
        if (targetTable.containField(_MetaBridge.VISIBLE)) {
            this.visiblePredicate(targetTable, safeTableAlias, context);
        }

        //11. order by clause
        this.orderByClause(stmt.orderByList(), context);
        //12. limit clause
        final long rowCount;
        rowCount = stmt.rowCount();
        if (rowCount >= 0) {
            sqlBuilder.append(_Constant.SPACE_LIMIT_SPACE)
                    .append(rowCount);
        }
        return context.build();
    }

    @Override
    protected SimpleStmt dialectMultiUpdate(final _MultiUpdateContext context) {
        if (context.dialect() != this) {
            throw illegalDialect();
        }
        final _MySQLMultiUpdate stmt = (_MySQLMultiUpdate) context.statement();
        final StringBuilder sqlBuilder = context.sqlBuilder();

        //1. WITH clause
        this.mySqlWithClauseAndSpace(stmt.isRecursive(), stmt.cteList(), context);

        //2. UPDATE key word
        sqlBuilder.append(_Constant.UPDATE);

        //3. hint comment block
        this.hintClause(stmt.hintList(), sqlBuilder, context);
        //4. modifier
        this.updateModifiers(stmt.modifierList(), sqlBuilder);
        //5. table_references (and partition ,index hint)
        this.mysqlTableReferences(stmt.tableBlockList(), context, false);
        //6. set clause
        final List<TableField<?>> conditionFields;
        conditionFields = this.multiTableSetClause(context);
        //7. where clause
        this.dmlWhereClause(context);
        //7.2 append condition update fields
        if (conditionFields.size() > 0) {
            this.conditionUpdate(conditionFields, context);
        }
        //7.3 append visible
        this.multiDmlVisible(stmt.tableBlockList(), context);
        return context.build();
    }


    @Override
    protected SimpleStmt dialectSingleDelete(final _SingleDeleteContext context, final _SingleDelete delete) {
        if (context.dialect() != this) {
            throw illegalDialect();
        }
        assert context.childBlock() == null;

        final _MySQLSingleDelete stmt = (_MySQLSingleDelete) delete;
        final StringBuilder sqlBuilder = context.sqlBuilder();

        //1. WITH clause
        this.mySqlWithClauseAndSpace(stmt.isRecursive(), stmt.cteList(), context);

        //2. DELETE key word
        sqlBuilder.append(_Constant.DELETE);

        //3. hint comment block
        this.hintClause(stmt.hintList(), sqlBuilder, context);
        //4. modifier
        this.deleteModifiers(stmt.modifierList(), sqlBuilder);

        final SimpleTableMeta<?> table;
        table = stmt.table();
        final String safeTableAlias;
        safeTableAlias = context.safeTableAlias();

        //5. table name
        this.quoteIfNeed(table.tableName(), sqlBuilder);
        if (asOf80) {
            sqlBuilder.append(_Constant.SPACE_AS_SPACE)
                    .append(safeTableAlias);
        }

        //6. partition clause
        this.partitionClause(stmt.partitionList(), sqlBuilder);

        //7. where clause
        this.dmlWhereClause(context);
        if (table.containField(_MetaBridge.VISIBLE)) {
            //7.2 append visible
            this.visiblePredicate(table, asOf80 ? safeTableAlias : null, context);
        }
        //8. order by clause
        this.orderByClause(stmt.orderByList(), context);
        //9. limit clause
        final long rowCount;
        rowCount = stmt.rowCount();
        if (rowCount >= 0L) {
            sqlBuilder.append(_Constant.SPACE_LIMIT_SPACE)
                    .append(rowCount);
        }
        return context.build();
    }

    @Override
    protected SimpleStmt dialectMultiDelete(final _MultiDeleteContext context) {
        if (context.dialect() != this) {
            throw illegalDialect();
        }
        final _MySQLMultiDelete stmt = (_MySQLMultiDelete) context.statement();
        final StringBuilder sqlBuilder = context.sqlBuilder();

        //1. WITH clause
        this.mySqlWithClauseAndSpace(stmt.isRecursive(), stmt.cteList(), context);

        //2. DELETE key word
        sqlBuilder.append(_Constant.DELETE);

        //3. hint comment block
        this.hintClause(stmt.hintList(), sqlBuilder, context);
        //4. modifier
        this.deleteModifiers(stmt.modifierList(), sqlBuilder);
        //5. delete table clause
        final boolean usingSyntax = stmt.usingSyntax();
        if (usingSyntax) {
            sqlBuilder.append(_Constant.SPACE_FROM);
        }
        final List<String> tableAliasList;
        tableAliasList = stmt.tableAliasList();
        final int aliasSize = tableAliasList.size();
        for (int i = 0; i < aliasSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            this.quoteIfNeed(tableAliasList.get(i), sqlBuilder);
        }

        if (usingSyntax) {
            sqlBuilder.append(_Constant.SPACE_USING);
        } else {
            sqlBuilder.append(_Constant.SPACE_FROM);
        }
        //6. table_references (and partition ,index hint)
        this.mysqlTableReferences(stmt.tableBlockList(), context, false);
        //7. where clause
        this.dmlWhereClause(context);
        //7.1 append visible
        this.multiDmlVisible(stmt.tableBlockList(), context);
        return context.build();
    }

    private void hintClause(List<Hint> hintList, final StringBuilder sqlBuilder, final _SqlContext context) {
        if (hintList.size() == 0) {
            return;
        }
        sqlBuilder.append(SPACE_HINT_START);
        for (Hint hint : hintList) {
            _MySQLCounselor.assertHint(hint);
            ((_SelfDescribed) hint).appendSql(context);
        }
        sqlBuilder.append(SPACE_HINT_END);
    }

    private void updateModifiers(List<MySQLWords> modifierList, StringBuilder builder) {
        for (MySQLWords modifier : modifierList) {
            switch (modifier) {
                case LOW_PRIORITY:
                case IGNORE:
                    builder.append(modifier.render());
                    break;
                default:
                    throw new CriteriaException(String.format("%s UPDATE don't support %s", this.dialect, modifier));

            }
        }
    }

    private void deleteModifiers(List<MySQLWords> modifierList, StringBuilder builder) {
        for (MySQLWords modifier : modifierList) {
            switch (modifier) {
                case LOW_PRIORITY:
                case QUICK:
                case IGNORE:
                    builder.append(modifier.render());
                    break;
                default:
                    throw new CriteriaException(String.format("%s DELETE don't support %s", this.dialect, modifier));

            }
        }
    }

    private void selectModifiers(List<MySQLWords> modifierList, StringBuilder builder) {
        for (MySQLWords modifier : modifierList) {
            switch (modifier) {
                case ALL:
                case DISTINCT:
                case DISTINCTROW:
                case HIGH_PRIORITY:
                case STRAIGHT_JOIN:
                case SQL_SMALL_RESULT:
                case SQL_BIG_RESULT:
                case SQL_BUFFER_RESULT:
                case SQL_NO_CACHE:
                case SQL_CALC_FOUND_ROWS:
                    builder.append(modifier.render());
                    break;
                default:
                    throw new CriteriaException(String.format("%s SELECT don't support %s", this.dialect, modifier));

            }
        }
    }


    private void mysqlTableReferences(final List<_TableBlock> blockList, final _StmtContext context
            , final boolean nested) {
        final int blockSize = blockList.size();
        if (blockSize == 0) {
            throw new CriteriaException("No table_references");
        }

        final StringBuilder sqlBuilder = context.sqlBuilder();

        if (nested) {
            sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
        }

        final boolean asOf80 = this.asOf80;

        _TableBlock block;
        TableItem tableItem;
        TableMeta<?> table;
        String alias;
        _JoinType joinType;
        List<_Predicate> predicateList;
        for (int i = 0; i < blockSize; i++) {
            block = blockList.get(i);
            joinType = block.jointType();
            if (i > 0) {
                if (joinType == _JoinType.NONE) {
                    throw _Exceptions.dontSupportJoinType(joinType, this.dialect);
                }
                sqlBuilder.append(joinType.keyWords);
            } else if (joinType != _JoinType.NONE) {
                throw _Exceptions.unexpectedEnum(joinType);
            }
            tableItem = block.tableItem();
            alias = block.alias();

            if (tableItem instanceof TableMeta) {
                sqlBuilder.append(_Constant.SPACE);
                table = (TableMeta<?>) tableItem;
                this.safeObjectName(table.tableName(), sqlBuilder);
                if (block instanceof _MySQLTableBlock) {
                    this.partitionClause(((_MySQLTableBlock) block).partitionList(), sqlBuilder);
                }
                sqlBuilder.append(_Constant.SPACE_AS_SPACE);
                this.quoteIfNeed(alias, sqlBuilder);
                if (block instanceof _MySQLTableBlock) {
                    this.indexHintClause(((_MySQLTableBlock) block).indexHintList(), sqlBuilder);
                }
            } else if (tableItem instanceof SubQuery) {
                if (tableItem instanceof _LateralSubQuery) {
                    if (asOf80) {
                        throw _Exceptions.dontSupportLateralItem(tableItem, alias, this.dialect);
                    }
                    this.lateralSubQuery((SubQuery) tableItem, context);
                } else {
                    this.subQueryStmt((SubQuery) tableItem, context);
                }
                sqlBuilder.append(_Constant.SPACE_AS_SPACE);
                this.quoteIfNeed(alias, sqlBuilder);
            } else if (tableItem instanceof NestedItems) {
                if (_StringUtils.hasText(alias)) {
                    throw _Exceptions.nestedItemsAliasHasText(alias);
                }
                this.mysqlTableReferences(((_NestedItems) tableItem).tableBlockList(), context, true);
            } else if (!asOf80) {
                throw _Exceptions.dontSupportTableItem(tableItem, alias);
            } else if (tableItem instanceof Cte) {
                _MySQLCounselor.assertMySQLCte((Cte) tableItem);
                sqlBuilder.append(_Constant.SPACE);
                this.quoteIfNeed(((Cte) tableItem).name(), sqlBuilder);
                if (_StringUtils.hasText(alias)) {
                    sqlBuilder.append(_Constant.SPACE_AS_SPACE);
                    this.quoteIfNeed(alias, sqlBuilder);
                } else if (!"".equals(alias)) {
                    throw _Exceptions.tableItemAliasNoText(tableItem);
                }
            } else if (tableItem instanceof Values) {
                this.valuesStmt((Values) tableItem, context);
                sqlBuilder.append(_Constant.SPACE_AS_SPACE);
                this.quoteIfNeed(alias, sqlBuilder);
            } else {
                throw _Exceptions.dontSupportTableItem(tableItem, alias);
            }

            switch (joinType) {
                case LEFT_JOIN:
                case JOIN:
                case RIGHT_JOIN:
                case FULL_JOIN:
                case STRAIGHT_JOIN: {
                    predicateList = block.predicateList();
                    if (!nested || predicateList.size() > 0) {
                        this.onClause(predicateList, context);
                    }
                }
                break;
                case NONE:
                case CROSS_JOIN: {
                    if (block.predicateList().size() > 0) {
                        throw _Exceptions.joinTypeNoOnClause(joinType);
                    }
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(joinType);
            }


        }//for

        if (nested) {
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }

    }

    private void partitionClause(final List<String> partitionList, final StringBuilder sqlBuilder) {
        final int partitionSize = partitionList.size();
        if (partitionSize == 0) {
            return;
        }
        sqlBuilder.append(SPACE_PARTITION_START);
        for (int i = 0; i < partitionSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);
            }
            this.quoteIfNeed(partitionList.get(i), sqlBuilder);
        }
        sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
    }

    private void indexHintClause(List<? extends _IndexHint> indexHintList, final StringBuilder sqlBuilder) {
        if (indexHintList.size() == 0) {
            return;
        }
        SQLWords purpose;
        List<String> indexNameList;
        int indexSize;
        for (_IndexHint indexHint : indexHintList) {
            sqlBuilder.append(indexHint.command().render());
            purpose = indexHint.purpose();
            if (purpose != null) {
                sqlBuilder.append(purpose.render());
            }
            indexNameList = indexHint.indexNameList();
            indexSize = indexNameList.size();
            assert indexSize > 0;
            sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            for (int i = 0; i < indexSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);
                }
                this.quoteIfNeed(indexNameList.get(i), sqlBuilder);
            }
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }

    }


    private void mySqlWithClauseAndSpace(final boolean recursive, final List<Cte> cteList, final _SqlContext context) {
        if (cteList.size() == 0) {
            return;
        }
        if (!this.asOf80) {
            throw _Exceptions.dontSupportWithClause(this.dialect);
        }
        this.withSubQueryAndSpace(recursive, cteList, context, _MySQLCounselor::assertMySQLCte);

    }

    private void windowClause(final List<Window> windowList, final _SqlContext context) {
        final int windowSize = windowList.size();
        if (windowSize == 0) {
            return;
        }
        if (!this.asOf80) {
            throw new CriteriaException(String.format("%s don't support WINDOW clause.", this.dialect));
        }
        final StringBuilder sqlBuilder = context.sqlBuilder()
                .append(_Constant.SPACE_WINDOW);
        Window window;
        for (int i = 0; i < windowSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            window = windowList.get(i);
            _MySQLCounselor.assertWindow(window);
            ((_SelfDescribed) window).appendSql(context);
        }

    }

    private void lockClause(final SQLWords lockMode, final List<String> ofList
            , final @Nullable SQLWords lockOption, final StringBuilder sqlBuilder) {
        final String lockModeText;
        lockModeText = lockMode.render();
        if (!this.asOf80 && lockModeText.equals(_Constant.SPACE_FOR_SHARE)) {
            throw dontSupportLockWord(lockMode);
        }
        sqlBuilder.append(lockModeText); //append lock mode

        switch (lockModeText) {
            case _Constant.SPACE_FOR_SHARE:
            case _Constant.SPACE_FOR_UPDATE: {
                final int ofSize = ofList.size();
                if (ofSize > 0) {
                    if (!this.asOf80) {
                        throw dontSupportOfTableList();
                    }
                    for (int i = 0; i < ofSize; i++) {
                        if (i > 0) {
                            sqlBuilder.append(_Constant.SPACE_COMMA);
                        }
                        this.quoteIfNeed(ofList.get(i), sqlBuilder);
                    }
                }
                if (lockOption != null) {
                    if (!this.asOf80) {
                        throw dontSupportLockWord(lockOption);
                    }
                    sqlBuilder.append(lockOption.render());
                }
            }
            break;
            case _Constant.SPACE_LOCK_IN_SHARE_MODE:
                break;
            default: {
                String m = String.format("unknown lock mode[%s]", lockModeText);
                throw new CriteriaException(m);
            }
        }

    }


    private void intoClause(final List<String> intoList, final StringBuilder sqlBuilder) {
        final int intoSize = intoList.size();
        sqlBuilder.append(SPACE_INTO);
        for (int i = 0; i < intoSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            sqlBuilder.append(_Constant.AT_CHAR);
            this.quoteIfNeed(intoList.get(i), sqlBuilder);
        }

    }

    private CriteriaException dontSupportOrderByWithRollup() {
        return new CriteriaException(String.format("%s don't support%s in ORDER BY clause"
                , this.dialect, SPACE_WITH_ROLLUP));
    }

    private CriteriaException dontSupportLockWord(SQLWords lockOption) {
        return new CriteriaException(String.format("%s don't support%s clause"
                , this.dialect, lockOption.render()));
    }

    private CriteriaException dontSupportOfTableList() {
        return new CriteriaException(String.format("%s don't support OF clause in lock clause"
                , this.dialect));
    }


}
