package io.army.dialect.mysql;

import io.army.criteria.*;
import io.army.criteria.impl._JoinType;
import io.army.criteria.impl._MySQLConsultant;
import io.army.criteria.impl._Pair;
import io.army.criteria.impl.inner.*;
import io.army.criteria.impl.inner.mysql.*;
import io.army.criteria.mysql.MySQLWords;
import io.army.dialect.*;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;
import io.army.modelgen._MetaBridge;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.*;

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

    static MySQLDialect create(_DialectEnv environment, Dialect dialect) {
        if (dialect.database != Database.MySQL) {
            throw new IllegalArgumentException();
        }
        return new MySQLDialect(environment, dialect);
    }

    private static final String SPACE_HINT_START = " /*+";

    private static final String SPACE_HINT_END = " */";

    private static final String SPACE_PARTITION_START = " PARTITION ( ";

    private static final String SPACE_WITH_ROLLUP = " WITH ROLLUP";

    private static final String SPACE_INTO = " INTO";


    private MySQLDialect(_DialectEnv environment, Dialect dialect) {
        super(environment, dialect);

    }

    @Override
    protected void assertDialectInsert(Insert insert) {
        super.assertDialectInsert(insert);
    }

    @Override
    protected void assertDialectUpdate(Update update) {
        _MySQLConsultant.assertUpdate(update);
    }

    @Override
    protected void assertDialectDelete(Delete delete) {
        _MySQLConsultant.assertDelete(delete);
    }

    @Override
    protected void assertDialectRowSet(RowSet rowSet) {
        _MySQLConsultant.assertRowSet(rowSet);
    }

    @Override
    protected void dialectSimpleQuery(final _Query query, final _SimpleQueryContext context) {
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
        sqlBuilder.append(_Constant.SPACE_FROM);
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
        this.standardLimitClause(stmt.offset(), stmt.rowCount(), context);

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

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/update.html">Single-table syntax</a>
     */
    @Override
    protected void dialectSingleUpdate(final _SingleUpdate update, final _SingleUpdateContext context) {
        final _MySQLSingleUpdate stmt = (_MySQLSingleUpdate) update;
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
        final SingleTableMeta<?> singleTable;
        if (table instanceof ChildTableMeta) {
            singleTable = ((ChildTableMeta<?>) table).parentMeta();
        } else {
            singleTable = (SingleTableMeta<?>) table;
        }
        final String safeTableAlias = context.safeTableAlias();
        sqlBuilder.append(_Constant.SPACE);
        this.safeObjectName(table, sqlBuilder);

        //6. partition
        this.partitionClause(stmt.partitionList(), sqlBuilder);
        //7. table alias
        sqlBuilder.append(_Constant.SPACE_AS_SPACE).append(safeTableAlias);

        //8. index hint
        this.indexHintClause(stmt.indexHintList(), sqlBuilder);
        //9. set clause
        this.singleTableSetClause(stmt, context);
        //10. where clause
        this.dmlWhereClause(stmt.predicateList(), context);
        //10.1 discriminator
        if (singleTable instanceof ParentTableMeta) {
            this.discriminator(table, safeTableAlias, context);
        }
        //10.2 append condition update fields
        context.appendConditionFields();

        //10.3 append visible
        if (singleTable.containField(_MetaBridge.VISIBLE)) {
            this.visiblePredicate(singleTable, safeTableAlias, context, false);
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
    }

    @Override
    protected void dialectMultiUpdate(final _MultiUpdate update, final _MultiUpdateContext context) {
        if (context.dialect() != this) {
            throw illegalDialect();
        }
        final _MySQLMultiUpdate stmt = (_MySQLMultiUpdate) update;

        //1. WITH clause
        this.mySqlWithClauseAndSpace(stmt.isRecursive(), stmt.cteList(), context);

        final StringBuilder sqlBuilder = context.sqlBuilder();
        //2. UPDATE key word
        sqlBuilder.append(_Constant.UPDATE);

        //3. hint comment block
        this.hintClause(stmt.hintList(), sqlBuilder, context);
        //4. modifier
        this.updateModifiers(stmt.modifierList(), sqlBuilder);
        //5. table_references (and partition ,index hint)
        this.mysqlTableReferences(stmt.tableBlockList(), context, false);
        //6. set clause
        this.multiTableSetClause(stmt, context);
        //7. where clause
        this.dmlWhereClause(stmt.predicateList(), context);
        //7.2 append condition update fields
        context.appendConditionFields();
        //7.3 append visible
        this.multiTableVisible(stmt.tableBlockList(), context, false);

    }


    @Override
    protected void dialectSingleDelete(final _SingleDelete delete, final _SingleDeleteContext context) {
        if (context.dialect() != this) {
            throw illegalDialect();
        }
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

        final SingleTableMeta<?> table = stmt.table();

        final String safeTableAlias;
        if (this.asOf80) {
            safeTableAlias = context.safeTableAlias();
        } else {
            safeTableAlias = null;
        }

        //5. FROM clause
        sqlBuilder.append(_Constant.SPACE_FROM_SPACE);
        this.safeObjectName(table, sqlBuilder);

        if (safeTableAlias != null) {
            sqlBuilder.append(_Constant.SPACE_AS_SPACE)
                    .append(safeTableAlias);
        }

        //6. partition clause
        this.partitionClause(stmt.partitionList(), sqlBuilder);

        //7. where clause
        this.dmlWhereClause(stmt.predicateList(), context);

        //7.1 append discriminator
        if (table instanceof ParentTableMeta) {
            this.discriminator(table, safeTableAlias, context);
        }
        //7.2 append visible
        if (table.containField(_MetaBridge.VISIBLE)) {
            this.visiblePredicate(table, safeTableAlias, context, false);
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

    }

    @Override
    protected void dialectMultiDelete(final _MultiDelete delete, final _MultiDeleteContext context) {
        if (context.dialect() != this) {
            throw illegalDialect();
        }
        final _MySQLMultiDelete stmt = (_MySQLMultiDelete) delete;

        //1. WITH clause
        this.mySqlWithClauseAndSpace(stmt.isRecursive(), stmt.cteList(), context);

        final StringBuilder sqlBuilder = context.sqlBuilder();
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
        final Map<String, ParentTableMeta<?>> aliasToLonelyParent;
        aliasToLonelyParent = this.tableAliasList(stmt.deleteTableList(), context);

        if (usingSyntax) {
            sqlBuilder.append(_Constant.SPACE_USING);
        } else {
            sqlBuilder.append(_Constant.SPACE_FROM);
        }
        //6. table_references (and partition ,index hint)
        this.mysqlTableReferences(stmt.tableBlockList(), context, false);
        //7. where clause
        this.dmlWhereClause(stmt.predicateList(), context);

        //7.1 append lonely parent discriminator
        for (Map.Entry<String, ParentTableMeta<?>> e : aliasToLonelyParent.entrySet()) {
            this.discriminator(e.getValue(), context.safeTableAlias(e.getKey()), context);
        }
        //7.2 append visible
        this.multiTableVisible(stmt.tableBlockList(), context, false);

    }

    private void hintClause(List<Hint> hintList, final StringBuilder sqlBuilder, final _SqlContext context) {
        if (hintList.size() == 0) {
            return;
        }
        sqlBuilder.append(SPACE_HINT_START);
        for (Hint hint : hintList) {
            _MySQLConsultant.assertHint(hint);
            ((_SelfDescribed) hint).appendSql(context);
        }
        sqlBuilder.append(SPACE_HINT_END);
    }

    private void updateModifiers(List<MySQLWords> modifierList, StringBuilder builder) {
        for (MySQLWords modifier : modifierList) {
            switch (modifier) {
                case LOW_PRIORITY:
                case IGNORE:
                    builder.append(modifier.words);
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

    private void selectModifiers(List<? extends SQLWords> modifierList, StringBuilder builder) {
        for (SQLWords modifier : modifierList) {
            switch ((MySQLWords) modifier) {
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


    private void mysqlTableReferences(final List<_TableBlock> blockList, final _MultiTableContext context
            , final boolean nested) {
        final int blockSize = blockList.size();
        if (blockSize == 0) {
            throw _Exceptions.tableBlockListIsEmpty(nested);
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
                sqlBuilder.append(_Constant.SPACE);//space prior to  table name
                table = (TableMeta<?>) tableItem;
                this.safeObjectName(table, sqlBuilder);
                if (block instanceof _MySQLTableBlock) {
                    this.partitionClause(((_MySQLTableBlock) block).partitionList(), sqlBuilder);
                }
                sqlBuilder.append(_Constant.SPACE_AS_SPACE)
                        .append(context.safeTableAlias(table, block.alias()));
                if (block instanceof _MySQLTableBlock) {
                    this.indexHintClause(((_MySQLTableBlock) block).indexHintList(), sqlBuilder);
                }
            } else if (tableItem instanceof SubQuery) {
                if (tableItem instanceof _LateralSubQuery) {
                    if (asOf80) {
                        throw _Exceptions.dontSupportLateralItem(tableItem, alias, this.dialect);
                    }
                    this.lateralSubQuery(joinType, (SubQuery) tableItem, context);
                } else {
                    this.subQueryStmt((SubQuery) tableItem, context);
                }
                sqlBuilder.append(_Constant.SPACE_AS_SPACE);
                this.identifier(alias, sqlBuilder);
            } else if (tableItem instanceof NestedItems) {
                _MySQLConsultant.assertNestedItems((NestedItems) tableItem);
                if (_StringUtils.hasText(alias)) {
                    throw _Exceptions.nestedItemsAliasHasText(alias);
                }
                this.mysqlTableReferences(((_NestedItems) tableItem).tableBlockList(), context, true);
            } else if (!asOf80) {
                throw _Exceptions.dontSupportTableItem(tableItem, alias);
            } else if (tableItem instanceof Cte) {
                _MySQLConsultant.assertMySQLCte((Cte) tableItem);
                sqlBuilder.append(_Constant.SPACE);
                this.identifier(((Cte) tableItem).name(), sqlBuilder);
                if (_StringUtils.hasText(alias)) {
                    sqlBuilder.append(_Constant.SPACE_AS_SPACE);
                    this.identifier(alias, sqlBuilder);
                } else if (!"".equals(alias)) {
                    throw _Exceptions.tableItemAliasNoText(tableItem);
                }
            } else if (tableItem instanceof Values) {
                this.valuesStmt((Values) tableItem, context);
                sqlBuilder.append(_Constant.SPACE_AS_SPACE);
                this.identifier(alias, sqlBuilder);
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


    /**
     * @return a unmodified map
     * @see #dialectMultiDelete(_MultiDelete, _MultiDeleteContext)
     */
    private Map<String, ParentTableMeta<?>> tableAliasList(final List<_Pair<String, TableMeta<?>>> deleteTablePairList
            , final _MultiDeleteContext context) {

        final int aliasSize = deleteTablePairList.size();
        assert aliasSize > 0;
        TableItem tableItem;
        String tableAlias;
        _Pair<String, TableMeta<?>> pair;
        //io.army.dialect.TableContext no bug,below correctly run.
        final Set<String> aliasSet = new HashSet<>((int) (aliasSize / 0.75F));
        final Map<String, String> childToParent = new HashMap<>((int) (aliasSize / 0.75F));
        final StringBuilder sqlBuilder = context.sqlBuilder();
        for (int i = 0; i < aliasSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            pair = deleteTablePairList.get(i);
            tableAlias = pair.first;
            tableItem = context.tableItemOf(tableAlias);
            if (tableItem != pair.second) {
                throw _Exceptions.unknownTableAlias(tableAlias);
            }
            sqlBuilder.append(_Constant.SPACE)
                    .append(context.safeTableAlias((TableMeta<?>) tableItem, tableAlias));

            if (!aliasSet.add(tableAlias)) {
                throw duplicationDelete(tableAlias);
            }

            if (!(tableItem instanceof ChildTableMeta)) {
                continue;
            }

            if (childToParent.putIfAbsent(tableAlias, context.parentAlias(tableAlias)) != null) {
                //aliasSet no bug,never here
                throw new IllegalStateException();
            }

        }//for

        //firstly, check parent of child
        for (Map.Entry<String, String> e : childToParent.entrySet()) {
            if (!aliasSet.contains(e.getValue())) {
                tableAlias = e.getKey();
                ChildTableMeta<?> child = (ChildTableMeta<?>) context.tableItemOf(tableAlias);
                throw _Exceptions.deleteChildButNoParent(child, tableAlias);
            }
        }

        //secondly, remove the parent that exists child.
        aliasSet.removeAll(childToParent.values());
        //finally, create aliasToParent
        Map<String, ParentTableMeta<?>> aliasToLonelyParent = null;
        for (String alias : aliasSet) {
            tableItem = context.tableItemOf(alias);
            if (!(tableItem instanceof ParentTableMeta)) {
                continue;
            }
            if (aliasToLonelyParent == null) {
                aliasToLonelyParent = new HashMap<>();
            }
            aliasToLonelyParent.put(alias, (ParentTableMeta<?>) tableItem);

        }
        if (aliasToLonelyParent == null) {
            aliasToLonelyParent = Collections.emptyMap();
        } else {
            aliasToLonelyParent = _CollectionUtils.unmodifiableMap(aliasToLonelyParent);
        }
        return aliasToLonelyParent;
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
            this.identifier(partitionList.get(i), sqlBuilder);
        }
        sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
    }

    private void indexHintClause(List<? extends _IndexHint> indexHintList, final StringBuilder sqlBuilder) {
        if (indexHintList.size() == 0) {
            return;
        }
        SQLWords purpose;
        List<String> indexNameList;
        int indexSize, hintIndex = 0;
        for (_IndexHint indexHint : indexHintList) {
            if (hintIndex > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
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
                this.identifier(indexNameList.get(i), sqlBuilder);
            }
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            hintIndex++;
        }


    }


    private void mySqlWithClauseAndSpace(final boolean recursive, final List<Cte> cteList, final _SqlContext context) {
        if (cteList.size() == 0) {
            return;
        }
        if (!this.asOf80) {
            throw _Exceptions.dontSupportWithClause(this.dialect);
        }
        this.withSubQueryAndSpace(recursive, cteList, context, _MySQLConsultant::assertMySQLCte);

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
            _MySQLConsultant.assertWindow(window);
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
                        this.identifier(ofList.get(i), sqlBuilder);
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
            this.identifier(intoList.get(i), sqlBuilder);
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

    private static CriteriaException duplicationDelete(String alias) {
        String m = String.format("Duplication delete table %s .", alias);
        return new CriteriaException(m);
    }


}
