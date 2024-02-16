/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.dialect.mysql;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.*;
import io.army.criteria.impl.inner.*;
import io.army.criteria.impl.inner.mysql.*;
import io.army.criteria.mysql.MySQLLoadData;
import io.army.criteria.mysql.MySQLReplace;
import io.army.dialect.*;
import io.army.env.EscapeMode;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.session.SessionSpec;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * <p>This class is the implementation of {@link DialectParser} for  MySQL dialect criteria api.
 * <p>Below is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 *
 * @since 0.6.0
 */
final class MySQLDialectParser extends MySQLParser {

    static MySQLDialectParser create(DialectEnv environment, @Nullable MySQLDialect dialect) {
        assert dialect != null;
        return new MySQLDialectParser(environment, dialect);
    }

    private static final String SPACE_HINT_START = " /*+";

    private static final String SPACE_HINT_END = " */";

    private static final String SPACE_PARTITION_START = " PARTITION( ";

    private static final String SPACE_WITH_ROLLUP = " WITH ROLLUP";

    private static final String SPACE_INTO = " INTO";

    private static final String SPACE_ON_DUPLICATE_KEY_UPDATE = " ON DUPLICATE KEY UPDATE";


    private MySQLDialectParser(DialectEnv environment, MySQLDialect dialect) {
        super(environment, dialect);

    }

    @Override
    protected void assertInsert(final InsertStatement insert) {
        if (insert instanceof MySQLReplace) {
            _MySQLConsultant.assertReplace((MySQLReplace) insert);
        } else {
            _MySQLConsultant.assertInsert(insert);
        }
    }

    @Override
    protected void assertUpdate(UpdateStatement update) {
        _MySQLConsultant.assertUpdate(update);
    }

    @Override
    protected void assertDelete(DeleteStatement delete) {
        _MySQLConsultant.assertDelete(delete);
    }

    @Override
    protected void assertRowSet(final RowSet rowSet) {
        if (rowSet instanceof Query) {
            _MySQLConsultant.assertQuery((Query) rowSet);
        } else if (rowSet instanceof ValuesQuery) {
            if (!this.asOf80) {
                throw _Exceptions.unknownStatement(rowSet, this.dialect);
            }
            _MySQLConsultant.assertValues((ValuesQuery) rowSet);
        } else {
            throw _Exceptions.unknownStatement(rowSet, this.dialect);
        }

    }


    @Override
    protected void parseValuesInsert(final _ValueSyntaxInsertContext context, final _Insert._ValuesSyntaxInsert insert) {
        assert context.parser() == this;

        final _MySQLInsert stmt = (_MySQLInsert) insert;

        //1. append insert common part
        this.appendInsertCommonPart(context, stmt);
        //2. column list
        context.appendFieldList();
        //3. values clause
        context.appendValueList();

        //4. on duplicate key update clause
        if (!(insert instanceof MySQLReplace)) {
            appendMySqlConflictClause(context, stmt);
        }
    }


    @Override
    protected void parseAssignmentInsert(final _AssignmentInsertContext context, final _Insert._AssignmentInsert insert) {
        assert context.parser() == this;
        final _MySQLInsert stmt = (_MySQLInsert) insert;
        //1. append insert common part
        this.appendInsertCommonPart(context, stmt);
        //2. append assignment clause
        context.appendAssignmentClause();
        //3. on duplicate key update clause
        if (!(insert instanceof MySQLReplace)) {
            appendMySqlConflictClause(context, stmt);
        }

    }


    @Override
    protected void parseQueryInsert(final _QueryInsertContext context, final _Insert._QueryInsert insert) {
        assert context.parser() == this;
        final _MySQLInsert stmt = (_MySQLInsert) insert;
        //1. append insert common part
        this.appendInsertCommonPart(context, stmt);
        //2. column list
        context.appendFieldList();
        //3. sub query
        context.appendSubQuery();

        //4. on duplicate key update clause
        if (!(insert instanceof MySQLReplace)) {
            appendMySqlConflictClause(context, stmt);
        }

    }


    @Override
    protected void parseSimpleQuery(final _Query query, final _SimpleQueryContext context) {
        assert context.parser() == this;
        final _MySQLQuery stmt = (_MySQLQuery) query;

        //1. WITH clause
        parseWithClause(stmt, context);
        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder();
        if (sqlBuilder.length() > 0) {
            sqlBuilder.append(_Constant.SPACE);
        }
        //2. SELECT key word
        sqlBuilder.append(_Constant.SELECT);
        //3. hint comment block
        this.hintClause(stmt.hintList(), sqlBuilder, context);
        //4. modifier
        this.selectModifiers(stmt.modifierList(), sqlBuilder);

        //5. select item list clause
        this.selectionListClause(context);
        final List<_TabularBlock> tableBlockList;
        tableBlockList = stmt.tableBlockList();

        if (tableBlockList.size() > 0) {
            //6. FROM clause
            sqlBuilder.append(_Constant.SPACE_FROM);
            this.mysqlTableReferences(tableBlockList, context, false);
        }
        //7. WHERE clause
        this.queryWhereClause(tableBlockList, stmt.wherePredicateList(), context);
        //8. GROUP clause
        final List<? extends GroupByItem> groupByList;
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
        windowClause(stmt.windowList(), context, _MySQLConsultant::assertWindow);

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
        this.standardLimitClause(stmt.offsetExp(), stmt.rowCountExp(), context);

        final List<String> intoList;
        intoList = stmt.intoVarList();
        final int intoSize = intoList.size();

        if (!this.asOf80 && intoSize > 0) {
            //12. prior to MySQL 8.0 into clause
            this.intoClause(intoList, sqlBuilder);
        }

        final _Query._LockBlock lockBlock;
        lockBlock = stmt.lockBlock();
        if (lockBlock != null) {
            //13. LOCK clause
            this.lockClause(lockBlock, sqlBuilder);
        }

        if (this.asOf80 && intoSize > 0) {
            //14. as of MySQL 8.0 into clause
            this.intoClause(intoList, sqlBuilder);
        }

    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/values.html">VALUES Statement</a>
     */
    @Override
    protected void parseSimpleValues(final _ValuesQuery values, final _ValuesContext context) {
        final StringBuilder sqlBuilder;
        if ((sqlBuilder = context.sqlBuilder()).length() > 0) {
            sqlBuilder.append(_Constant.SPACE);
        }
        //1. VALUES keyword
        sqlBuilder.append(_Constant.VALUES);
        //2. row_constructor_list
        valuesClauseOfValues(context, _Constant.SPACE_ROW, values.rowList());
        //3. ORDER BY clause
        orderByClause(values.orderByList(), context);
        //4. LIMIT clause
        standardLimitClause(values.offsetExp(), values.rowCountExp(), context);
    }

    @Override
    protected void parseClauseAfterRightParen(final _ParensRowSet rowSet, final _ParenRowSetContext context) {
        orderByClause(rowSet.orderByList(), context);
        standardLimitClause(rowSet.offsetExp(), rowSet.rowCountExp(), context);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/update.html">Single-table syntax</a>
     */
    @Override
    protected void parseSingleUpdate(final _SingleUpdate update, final _SingleUpdateContext context) {

        final _MySQLSingleUpdate stmt = (_MySQLSingleUpdate) update;
        //1. WITH clause
        parseWithClause(stmt, context);

        final StringBuilder sqlBuilder;
        if ((sqlBuilder = context.sqlBuilder()).length() > 0) {
            sqlBuilder.append(_Constant.SPACE);
        }
        //2. UPDATE key word
        sqlBuilder.append(_Constant.UPDATE);

        //3. hint comment block
        this.hintClause(stmt.hintList(), sqlBuilder, context);
        //4. modifier
        this.updateModifiers(stmt.modifierList(), sqlBuilder);

        //5. table name
        final TableMeta<?> domainTable;
        domainTable = context.targetTable();
        final SingleTableMeta<?> updateTable;
        if (domainTable instanceof ChildTableMeta) {
            updateTable = ((ChildTableMeta<?>) domainTable).parentMeta();
        } else {
            updateTable = (SingleTableMeta<?>) domainTable;
        }

        this.safeObjectName(domainTable, sqlBuilder.append(_Constant.SPACE));
        //6. partition
        this.partitionClause(stmt.partitionList(), sqlBuilder);

        final String safeTableAlias;
        safeTableAlias = context.safeTargetTableAlias();
        //7. table alias
        sqlBuilder.append(_Constant.SPACE_AS_SPACE).append(safeTableAlias);

        //8. index hint
        this.indexHintClause(stmt.indexHintList(), sqlBuilder);
        //9. set clause
        this.singleTableSetClause(stmt.itemPairList(), context);
        //10. where clause
        this.dmlWhereClause(stmt.wherePredicateList(), context);
        //10.1 discriminator
        if (updateTable instanceof ParentTableMeta) {
            this.discriminator(domainTable, safeTableAlias, context);
        }
        //10.2 append condition update fields
        context.appendConditionFields();

        //10.3 append visible
        if (updateTable.containField(_MetaBridge.VISIBLE)) {
            this.visiblePredicate(updateTable, safeTableAlias, context, false);
        }
        //11. order by clause
        this.orderByClause(stmt.orderByList(), context);
        //12. limit clause
        this.standardLimitClause(null, stmt.rowCountExp(), context);

    }

    @Override
    protected void parseMultiUpdate(final _MultiUpdate update, final _MultiUpdateContext context) {
        assert context.parser() == this;
        final _MySQLMultiUpdate stmt = (_MySQLMultiUpdate) update;
        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder();
        //1. WITH clause
        parseWithClause(stmt, context);
        if (sqlBuilder.length() > 0) {
            sqlBuilder.append(_Constant.SPACE);
        }
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
        this.dmlWhereClause(stmt.wherePredicateList(), context);
        //7.2 append condition update fields
        context.appendConditionFields();
        //7.3 append visible
        this.multiTableVisible(stmt.tableBlockList(), context, false);

    }


    @Override
    protected void parseSingleDelete(final _SingleDelete delete, final _SingleDeleteContext context) {
        assert context.parser() == this;
        final _MySQLSingleDelete stmt = (_MySQLSingleDelete) delete;

        //1. WITH clause
        parseWithClause(stmt, context);
        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder();
        if (sqlBuilder.length() > 0) {
            sqlBuilder.append(_Constant.SPACE);
        }
        //2. DELETE key word
        sqlBuilder.append(_Constant.DELETE);

        //3. hint comment block
        this.hintClause(stmt.hintList(), sqlBuilder, context);
        //4. modifier
        this.deleteModifiers(stmt.modifierList(), sqlBuilder);

        final SingleTableMeta<?> deleteTable;
        deleteTable = (SingleTableMeta<?>) stmt.table();

        //5. FROM clause
        sqlBuilder.append(_Constant.SPACE_FROM_SPACE);
        this.safeObjectName(deleteTable, sqlBuilder);

        final String safeTableAlias;
        if (this.asOf80) {
            safeTableAlias = context.safeTargetTableAlias();
        } else {
            safeTableAlias = null;
        }
        if (safeTableAlias != null) {
            sqlBuilder.append(_Constant.SPACE_AS_SPACE)
                    .append(safeTableAlias);
        }

        //6. partition clause
        this.partitionClause(stmt.partitionList(), sqlBuilder);

        //7. where clause
        this.dmlWhereClause(stmt.wherePredicateList(), context);

        //7.1 append discriminator
        if (deleteTable instanceof ParentTableMeta) {
            this.discriminator(deleteTable, safeTableAlias, context);
        }
        //7.2 append visible
        if (deleteTable.containField(_MetaBridge.VISIBLE)) {
            this.visiblePredicate(deleteTable, safeTableAlias, context, false);
        }
        //8. order by clause
        this.orderByClause(stmt.orderByList(), context);
        //9. limit clause
        this.standardLimitClause(null, stmt.rowCountExp(), context);

    }

    @Override
    protected void parseMultiDelete(final _MultiDelete delete, final _MultiDeleteContext context) {
        assert context.parser() == this;
        final _MySQLMultiDelete stmt = (_MySQLMultiDelete) delete;
        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder();
        //1. WITH clause
        parseWithClause(stmt, context);
        if (sqlBuilder.length() > 0) {
            sqlBuilder.append(_Constant.SPACE);
        }
        //2. DELETE key word
        sqlBuilder.append(_Constant.DELETE);

        //3. hint comment block
        this.hintClause(stmt.hintList(), sqlBuilder, context);
        //4. modifier
        this.deleteModifiers(stmt.modifierList(), sqlBuilder);
        //5. delete table clause
        final boolean usingSyntax;
        usingSyntax = stmt.isUsingSyntax();
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
        this.dmlWhereClause(stmt.wherePredicateList(), context);

        //7.1 append lonely parent discriminator
        for (Map.Entry<String, ParentTableMeta<?>> e : aliasToLonelyParent.entrySet()) {
            this.discriminator(e.getValue(), context.safeTableAlias(e.getKey()), context);
        }
        //7.2 append visible
        this.multiTableVisible(stmt.tableBlockList(), context, false);

    }

    @Override
    protected _StmtContext handleDialectDml(final @Nullable _SqlContext outerContext, final DmlStatement statement,
                                            final SessionSpec sessionSpec) {
        final _StmtContext context;
        if (statement instanceof MySQLLoadData) {
            _MySQLConsultant.assertMySQLLoad((MySQLLoadData) statement);
            context = this.handleLoadData(outerContext, (_MySQLLoadData) statement, sessionSpec);
        } else {
            throw _Exceptions.unknownStatement(statement, this.dialect);
        }
        return context;
    }

    @Override
    protected _StmtContext handleDialectDql(final @Nullable _SqlContext outerContext, final DqlStatement statement,
                                            final SessionSpec sessionSpec) {
        return super.handleDialectDql(outerContext, statement, sessionSpec);
    }

    /*-----------------------below private method-----------------------*/

    /**
     * @see #appendInsertCommonPart(_InsertContext, _MySQLInsert)
     * @see #parseSingleUpdate(_SingleUpdate, _SingleUpdateContext)
     * @see #parseMultiUpdate(_MultiUpdate, _MultiUpdateContext)
     * @see #parseSingleDelete(_SingleDelete, _SingleDeleteContext)
     * @see #parseMultiDelete(_MultiDelete, _MultiDeleteContext)
     * @see #parseSimpleQuery(_Query, _SimpleQueryContext)
     */
    private void hintClause(List<Hint> hintList, final StringBuilder sqlBuilder, final _SqlContext context) {
        if (hintList.size() == 0) {
            return;
        }
        if (this.dialect.compareWith(MySQLDialect.MySQL57) < 0) {
            throw _Exceptions.dontSupportHint(this.dialect);
        }
        sqlBuilder.append(SPACE_HINT_START);
        for (Hint hint : hintList) {
            _MySQLConsultant.assertHint(hint);
            ((_SelfDescribed) hint).appendSql(sqlBuilder, context);
        }
        sqlBuilder.append(SPACE_HINT_END);
    }


    private void insertModifiers(StringBuilder sqlBuilder, _MySQLInsert stmt) {
        for (MySQLs.Modifier modifier : stmt.modifierList()) {
            if (modifier == MySQLs.LOW_PRIORITY
                    || modifier == MySQLs.HIGH_PRIORITY
                    || modifier == MySQLs.IGNORE) {
                sqlBuilder.append(modifier.spaceRender());
            } else if (modifier == MySQLs.DELAYED) {
                if (stmt instanceof _MySQLInsert._MySQLQueryInsert) {
                    String m = String.format("%s QUERY INSERT don't support %s", this.dialect, modifier);
                    throw new CriteriaException(m);
                }
                sqlBuilder.append(modifier.spaceRender());
            } else {
                throw new CriteriaException(String.format("%s INSERT don't support %s", this.dialect, modifier));
            }
        }

    }

    /**
     * @see #appendInsertCommonPart(_InsertContext, _MySQLInsert)
     */
    private void replaceModifiers(List<MySQLs.Modifier> modifierList, StringBuilder sqlBuilder) {
        final int modifierSize = modifierList.size();
        if (modifierSize == 0) {
            return;
        }

        assert modifierSize == 1;
        final MySQLs.Modifier modifier = modifierList.get(0);
        if (modifier == MySQLs.LOW_PRIORITY
                || modifier == MySQLs.DELAYED) {
            sqlBuilder.append(modifier.spaceRender());
        } else {
            throw new CriteriaException(String.format("%s REPLACE don't support %s", this.dialect, modifier));
        }
    }

    /**
     * @see #parseSingleUpdate(_SingleUpdate, _SingleUpdateContext)
     * @see #parseMultiDelete(_MultiDelete, _MultiDeleteContext)
     */
    private void updateModifiers(List<MySQLs.Modifier> modifierList, StringBuilder builder) {
        for (MySQLs.Modifier modifier : modifierList) {
            if (modifier == MySQLs.LOW_PRIORITY
                    || modifier == MySQLs.IGNORE) {
                builder.append(modifier.spaceRender());
                continue;
            }
            throw new CriteriaException(String.format("%s UPDATE don't support %s", this.dialect, modifier));
        }
    }


    /**
     * @see #parseSingleDelete(_SingleDelete, _SingleDeleteContext)
     * @see #parseMultiDelete(_MultiDelete, _MultiDeleteContext)
     */
    private void deleteModifiers(List<MySQLs.Modifier> modifierList, StringBuilder builder) {
        for (MySQLs.Modifier modifier : modifierList) {
            if (modifier == MySQLs.LOW_PRIORITY
                    || modifier == MySQLs.QUICK
                    || modifier == MySQLs.IGNORE) {
                builder.append(modifier.spaceRender());
                continue;
            }
            throw new CriteriaException(String.format("%s DELETE don't support %s", this.dialect, modifier));
        }
    }

    private void selectModifiers(List<? extends SQLWords> modifierList, StringBuilder builder) {
        MySQLs.Modifier modifier;
        for (SQLWords words : modifierList) {
            modifier = (MySQLs.Modifier) words;
            if (modifier == MySQLs.ALL
                    || modifier == MySQLs.DISTINCT
                    || modifier == MySQLs.DISTINCTROW
                    || modifier == MySQLs.HIGH_PRIORITY
                    || modifier == MySQLs.STRAIGHT_JOIN
                    || modifier == MySQLs.SQL_SMALL_RESULT
                    || modifier == MySQLs.SQL_BIG_RESULT
                    || modifier == MySQLs.SQL_BUFFER_RESULT
                    || modifier == MySQLs.SQL_NO_CACHE
                    || modifier == MySQLs.SQL_CALC_FOUND_ROWS) {
                builder.append(modifier.spaceRender());
            } else {
                throw new CriteriaException(String.format("%s SELECT don't support %s", this.dialect, modifier));
            }
        }
    }

    /**
     * @see #parseLoadData(_MySQLLoadData, _OtherDmlContext)
     */
    private void loadDataModifier(final List<MySQLs.Modifier> modifierList, final StringBuilder sqlBuilder) {
        for (MySQLs.Modifier modifier : modifierList) {
            if (modifier == MySQLs.LOW_PRIORITY
                    || modifier == MySQLs.CONCURRENT
                    || modifier == MySQLs.LOCAL) {
                sqlBuilder.append(modifier.spaceRender());
            } else {
                throw new CriteriaException(String.format("%s LOAD DATA don't support %s", this.dialect, modifier));
            }
        }

    }


    /**
     * @see #parseValuesInsert(_ValueSyntaxInsertContext, _Insert._ValuesSyntaxInsert)
     * @see #parseAssignmentInsert(_AssignmentInsertContext, _Insert._AssignmentInsert)
     * @see #parseQueryInsert(_QueryInsertContext, _Insert._QueryInsert)
     */
    private void appendInsertCommonPart(final _InsertContext context, final _MySQLInsert stmt) {
        final StringBuilder sqlBuilder;
        if ((sqlBuilder = context.sqlBuilder()).length() > 0) {
            sqlBuilder.append(_Constant.SPACE);
        }
        //1. INSERT/REPLACE keywords
        if (stmt instanceof MySQLReplace) {
            sqlBuilder.append("REPLACE");
        } else {
            sqlBuilder.append(_Constant.INSERT);
        }
        //2. hint clause
        this.hintClause(stmt.hintList(), sqlBuilder, context);
        //3. modifier list
        if (stmt instanceof MySQLReplace) {
            this.replaceModifiers(stmt.modifierList(), sqlBuilder);
        } else {
            this.insertModifiers(sqlBuilder, stmt);
        }

        //4. INTO keywords
        sqlBuilder.append(_Constant.SPACE_INTO_SPACE);

        //5. table name
        this.safeObjectName(context.insertTable(), sqlBuilder);
        //6. partition clause
        this.partitionClause(stmt.partitionList(), sqlBuilder);

    }


    /**
     * @see #parseMultiUpdate(_MultiUpdate, _MultiUpdateContext)
     * @see #parseMultiDelete(_MultiDelete, _MultiDeleteContext)
     * @see #parseSimpleQuery(_Query, _SimpleQueryContext)
     */
    private void mysqlTableReferences(final List<_TabularBlock> blockList, final _MultiTableStmtContext context
            , final boolean nested) {
        final int blockSize = blockList.size();
        assert blockSize > 0;
        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder();

        if (nested) {
            sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
        }
        final boolean asOf80 = this.asOf80;

        _TabularBlock block;
        TabularItem tabularItem;
        TableMeta<?> table;
        String alias, cteName;
        _JoinType joinType;
        List<_Predicate> predicateList;
        SQLWords modifier;
        for (int i = 0; i < blockSize; i++) {
            block = blockList.get(i);
            joinType = block.jointType();
            if (i > 0) {
                assert joinType != _JoinType.NONE;
                sqlBuilder.append(joinType.spaceRender());
            } else {
                assert joinType == _JoinType.NONE;
            }
            tabularItem = block.tableItem();
            alias = block.alias();
            if (!(tabularItem instanceof _NestedItems) && !_StringUtils.hasText(alias)) {
                throw _Exceptions.tabularAliasIsEmpty();
            }

            if (tabularItem instanceof TableMeta) {
                table = (TableMeta<?>) tabularItem;
                sqlBuilder.append(_Constant.SPACE);
                this.safeObjectName(table, sqlBuilder);
                if (block instanceof _MySQLTableBlock) {
                    this.partitionClause(((_MySQLTableBlock) block).partitionList(), sqlBuilder);
                }
                sqlBuilder.append(_Constant.SPACE_AS_SPACE)
                        .append(context.safeTableAlias(table, alias));
                if (block instanceof _MySQLTableBlock) {
                    this.indexHintClause(((_MySQLTableBlock) block).indexHintList(), sqlBuilder);
                }
            } else if (tabularItem instanceof DerivedTable) {
                if (block instanceof _ModifierTabularBlock
                        && (modifier = ((_ModifierTabularBlock) block).modifier()) != null) {
                    assert modifier == SQLs.LATERAL;
                    sqlBuilder.append(modifier.spaceRender());
                }
                if (tabularItem instanceof SubQuery) {
                    this.handleSubQuery((SubQuery) tabularItem, context);
                } else if (tabularItem instanceof SubValues) {
                    this.handleSubValues((SubValues) tabularItem, context);
                } else {
                    // function
                    ((_SelfDescribed) tabularItem).appendSql(sqlBuilder, context);
                }
                sqlBuilder.append(_Constant.SPACE_AS_SPACE);
                this.identifier(alias, sqlBuilder);

                if (block instanceof _AliasDerivedBlock) {
                    this.derivedColumnAliasClause((_AliasDerivedBlock) block, context);
                }
            } else if (tabularItem instanceof _NestedItems) {
                _MySQLConsultant.assertNestedItems((_NestedItems) tabularItem);
                if (_StringUtils.hasText(alias)) {
                    throw _Exceptions.nestedItemsAliasHasText(alias);
                }
                this.mysqlTableReferences(((_NestedItems) tabularItem).tableBlockList(), context, true);
            } else if (!asOf80) {
                throw _Exceptions.dontSupportTableItem(tabularItem, alias, this.dialect);
            } else if (tabularItem instanceof _Cte) {
                sqlBuilder.append(_Constant.SPACE);

                cteName = ((_Cte) tabularItem).name();
                this.identifier(cteName, sqlBuilder);

                if (!cteName.equals(alias)) {
                    sqlBuilder.append(_Constant.SPACE_AS_SPACE);
                    this.identifier(alias, sqlBuilder);
                }
            } else {
                throw _Exceptions.dontSupportTableItem(tabularItem, alias, this.dialect);
            }

            switch (joinType) {
                case LEFT_JOIN:
                case JOIN:
                case RIGHT_JOIN:
                case FULL_JOIN:
                case STRAIGHT_JOIN: {
                    predicateList = block.onClauseList();
                    if (predicateList.size() > 0) {
                        this.onClause(predicateList, context);
                    } else {
                        assert nested;
                    }
                }
                break;
                case NONE:
                case CROSS_JOIN:
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
     * @see #parseMultiDelete(_MultiDelete, _MultiDeleteContext)
     */
    private Map<String, ParentTableMeta<?>> tableAliasList(final List<_Pair<String, TableMeta<?>>> deleteTablePairList
            , final _MultiDeleteContext context) {

        final int aliasSize = deleteTablePairList.size();
        assert aliasSize > 0;
        TabularItem tableItem;
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
            tableItem = context.tabularItemOf(tableAlias);
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
                ChildTableMeta<?> child = (ChildTableMeta<?>) context.tabularItemOf(tableAlias);
                throw _Exceptions.deleteChildButNoParent(child, tableAlias);
            }
        }

        //secondly, remove the parent that exists child.
        aliasSet.removeAll(childToParent.values());
        //finally, create aliasToParent
        Map<String, ParentTableMeta<?>> aliasToLonelyParent = null;
        for (String alias : aliasSet) {
            tableItem = context.tabularItemOf(alias);
            if (!(tableItem instanceof ParentTableMeta)) {
                continue;
            }
            if (aliasToLonelyParent == null) {
                aliasToLonelyParent = _Collections.hashMap();
            }
            aliasToLonelyParent.put(alias, (ParentTableMeta<?>) tableItem);

        }
        if (aliasToLonelyParent == null) {
            aliasToLonelyParent = Collections.emptyMap();
        } else {
            aliasToLonelyParent = _Collections.unmodifiableMap(aliasToLonelyParent);
        }
        return aliasToLonelyParent;
    }

    /**
     * @see #parseValuesInsert(_ValueSyntaxInsertContext, _Insert._ValuesSyntaxInsert)
     * @see #parseAssignmentInsert(_AssignmentInsertContext, _Insert._AssignmentInsert)
     * @see #parseQueryInsert(_QueryInsertContext, _Insert._QueryInsert)
     * @see #parseSingleUpdate(_SingleUpdate, _SingleUpdateContext)
     * @see #parseMultiUpdate(_MultiUpdate, _MultiUpdateContext)
     * @see #parseSingleDelete(_SingleDelete, _SingleDeleteContext)
     * @see #parseMultiDelete(_MultiDelete, _MultiDeleteContext)
     * @see #parseSimpleQuery(_Query, _SimpleQueryContext)
     */
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

    /**
     * @see #parseSingleUpdate(_SingleUpdate, _SingleUpdateContext)
     * @see #parseMultiUpdate(_MultiUpdate, _MultiUpdateContext)
     * @see #parseMultiDelete(_MultiDelete, _MultiDeleteContext)
     * @see #parseSimpleQuery(_Query, _SimpleQueryContext)
     */
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
            sqlBuilder.append(indexHint.command().spaceRender());
            purpose = indexHint.purpose();
            if (purpose != null) {
                if (purpose != SQLs.JOIN && purpose != SQLs.ORDER_BY && purpose != SQLs.GROUP_BY) {
                    throw new CriteriaException(String.format("Illegal key words %s", purpose));
                }
                sqlBuilder.append(" FOR")
                        .append(purpose.spaceRender());
            }
            indexNameList = indexHint.indexNameList();
            indexSize = indexNameList.size();
            assert indexSize > 0;
            sqlBuilder.append(_Constant.SPACE_LEFT_PAREN)
                    .append(_Constant.SPACE);
            for (int i = 0; i < indexSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);
                }
                identifier(indexNameList.get(i), sqlBuilder);
            }
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            hintIndex++;
        }


    }


    private void lockClause(final _Query._LockBlock block, final StringBuilder sqlBuilder) {
        final String lockModeText;
        lockModeText = block.lockStrength().spaceRender();
        if (!this.asOf80 && lockModeText.equals(_Constant.SPACE_FOR_SHARE)) {
            throw dontSupportLockWord(block.lockStrength());
        }
        final List<String> ofList;
        ofList = block.lockTableAliasList();
        final SQLWords lockOption;
        lockOption = block.lockWaitOption();
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
                    sqlBuilder.append(_Constant.SPACE)
                            .append(lockOption.spaceRender());
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


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/load-data.html">LOAD DATA Statement</a>
     */
    private _OtherDmlContext handleLoadData(@Nullable final _SqlContext outerContext, final _MySQLLoadData loadData,
                                            final SessionSpec sessionSpec) {
        final _OtherDmlContext context;
        if (loadData instanceof _MySQLLoadData._ChildLoadData) {
            final _MySQLLoadData parentLoad;
            parentLoad = ((_MySQLLoadData._ChildLoadData) loadData).parentLoadData();

            final _OtherDmlContext parentContext;
            parentContext = this.createOtherDmlContext(outerContext, parentLoad.table()::isThisField, sessionSpec);
            this.parseLoadData(parentLoad, parentContext);

            context = this.createOtherDmlContext(outerContext, loadData.table()::isThisField, parentContext);
        } else {
            context = this.createOtherDmlContext(outerContext, loadData.table()::isThisField, sessionSpec);
        }
        this.parseLoadData(loadData, context);
        return context;
    }

    /**
     * @see #handleLoadData(_SqlContext, _MySQLLoadData, SessionSpec)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/load-data.html">LOAD DATA Statement</a>
     */
    private void parseLoadData(final _MySQLLoadData loadData, final _OtherDmlContext context) {
        final TableMeta<?> insertTable;
        insertTable = loadData.table();

        //1. LOAD DATA keywords
        final StringBuilder sqlBuilder;
        if ((sqlBuilder = context.sqlBuilder()).length() > 0) {
            sqlBuilder.append(_Constant.SPACE);
        }
        sqlBuilder.append("LOAD DATA");
        //2. modifiers
        loadDataModifier(loadData.modifierList(), sqlBuilder);

        //3. INFILE clause
        loadDataInfileClause(loadData.fileName(), sqlBuilder);

        //4. REPLACE / IGNORE
        final SQLWords strategyOption;
        strategyOption = loadData.strategyOption();
        if (strategyOption != null) {
            sqlBuilder.append(strategyOption.spaceRender());
        }
        //5. INTO TABLE clause
        sqlBuilder.append(" INTO TABLE ");
        this.safeObjectName(insertTable, sqlBuilder);

        //6. PARTITION clause
        this.partitionClause(loadData.partitionList(), sqlBuilder);
        //7. CHARACTER SET
        final String charset;
        if ((charset = loadData.charset()) != null) {
            sqlBuilder.append(" CHARACTER SET ");
            identifier(charset, sqlBuilder);
        }

        //8. FIELDS | COLUMNS clause
        final Boolean fieldKeyword;
        if ((fieldKeyword = loadData.fieldsKeyWord()) != null) {
            this.loadDataFieldsColumnsClause(loadData, fieldKeyword, sqlBuilder);
        }

        //9. LINES clause
        if (loadData.linesClause()) {
            this.loadDataLinesClause(loadData, sqlBuilder);
        }

        //10. IGNORE clause
        final Long ignoreRows;
        if ((ignoreRows = loadData.ignoreRows()) != null) {
            sqlBuilder.append(" IGNORE ")
                    .append(ignoreRows);
            final SQLWords word = loadData.ignoreRowWord();
            if (word != SQLs.LINES && word != SQLs.ROWS) {
                String m = String.format("MySQL LOAD DATA statement IGNORE number ROWS|LINES clause don't support %s key word.", word);
                throw new CriteriaException(m);
            }
            sqlBuilder.append(word.spaceRender());
        }

        //11. col_name_or_user_var
        this.loadDataColumnOrVarListClause(loadData.columnOrUserVarList(), context);

        //12. SET column pair clause
        this.loadDataSetColumnPairClause(loadData.columItemPairList(), context);

        if (context.hasParam()) {
            throw new CriteriaException("MySQL LOAD DATA statement don't parameter placeholder.");
        }

    }

    /**
     * @see #parseLoadData(_MySQLLoadData, _OtherDmlContext)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/load-data.html">LOAD DATA Statement</a>
     */
    private void loadDataInfileClause(final Path path, final StringBuilder sqlBuilder) {
        if (Files.notExists(path)) {
            String m = String.format("%s don't exists,couldn't execute MySQL LOAD DATA.", path.toAbsolutePath());
            throw new CriteriaException(m);
        }
        if (!Files.isReadable(path)) {
            String m = String.format("%s isn't readable,couldn't execute MySQL LOAD DATA.", path.toAbsolutePath());
            throw new CriteriaException(m);
        }
        sqlBuilder.append(" INFILE ");

        MySQLLiterals.mysqlEscapes(EscapeMode.BACK_SLASH, path.toAbsolutePath().toString(), true, sqlBuilder);
    }

    /**
     * @see #parseLoadData(_MySQLLoadData, _OtherDmlContext)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/load-data.html">LOAD DATA Statement</a>
     */
    private void loadDataFieldsColumnsClause(final _MySQLLoadData loadData, final boolean fieldKeywords,
                                             final StringBuilder sqlBuilder) {
        //1. FIELDS / COLUMNS keywords
        if (fieldKeywords) {
            sqlBuilder.append(" FIELDS");
        } else {
            sqlBuilder.append(" COLUMNS");
        }
        //2. TERMINATED BY
        final String terminatedString;
        if ((terminatedString = loadData.columnTerminatedBy()) != null) {
            sqlBuilder.append(" TERMINATED BY ");
            MySQLLiterals.mysqlEscapes(EscapeMode.BACK_SLASH, terminatedString, true, sqlBuilder);
        }
        //3. ENCLOSED BY
        final String enclosedChar;
        if ((enclosedChar = loadData.columnEnclosedBy()) != null) {
            if (loadData.columnOptionallyEnclosed()) {
                sqlBuilder.append(" OPTIONALLY");
            }
            sqlBuilder.append(" ENCLOSED BY ");
            MySQLLiterals.mysqlEscapes(EscapeMode.BACK_SLASH, enclosedChar, true, sqlBuilder);
        }
        //4. ESCAPED BY
        final String escapedChar;
        if ((escapedChar = loadData.columnEscapedBy()) != null) {
            sqlBuilder.append(" ESCAPED BY ");
            MySQLLiterals.mysqlEscapes(EscapeMode.BACK_SLASH, escapedChar, true, sqlBuilder);
        }

        if (terminatedString == null && enclosedChar == null && escapedChar == null) {
            throw _Exceptions.castCriteriaApi();
        }

    }

    /**
     * @see #parseLoadData(_MySQLLoadData, _OtherDmlContext)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/load-data.html">LOAD DATA Statement</a>
     */
    private void loadDataLinesClause(final _MySQLLoadData loadData, final StringBuilder sqlBuilder) {
        //1. LINES keywords
        sqlBuilder.append(" LINES");
        final String startingString, terminatedString;
        //2. STARTING BY clause
        if ((startingString = loadData.linesStartingBy()) != null) {
            sqlBuilder.append(" STARTING BY ");
            MySQLLiterals.mysqlEscapes(EscapeMode.BACK_SLASH, startingString, true, sqlBuilder);
        }
        //3. TERMINATED BY clause
        if ((terminatedString = loadData.linesTerminatedBy()) != null) {
            sqlBuilder.append(" TERMINATED BY ");
            MySQLLiterals.mysqlEscapes(EscapeMode.BACK_SLASH, terminatedString, true, sqlBuilder);
        }

        if (startingString == null && terminatedString == null) {
            throw _Exceptions.castCriteriaApi();
        }
    }

    /**
     * @see #parseLoadData(_MySQLLoadData, _OtherDmlContext)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/load-data.html">LOAD DATA Statement</a>
     */
    private void loadDataColumnOrVarListClause(final List<_Expression> columnOrVarList,
                                               final _OtherDmlContext context) {
        final int columnOrVarSize;
        if ((columnOrVarSize = columnOrVarList.size()) > 0) {
            final StringBuilder sqlBuilder = context.sqlBuilder();
            sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            for (int i = 0; i < columnOrVarSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                columnOrVarList.get(i).appendSql(sqlBuilder, context);
            }
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }
    }

    /**
     * @see #parseLoadData(_MySQLLoadData, _OtherDmlContext)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/load-data.html">LOAD DATA Statement</a>
     */
    private void loadDataSetColumnPairClause(final List<_Pair<FieldMeta<?>, _Expression>> columnPairList,
                                             final _OtherDmlContext context) {

        final int columnPairSize;
        if ((columnPairSize = columnPairList.size()) > 0) {
            final StringBuilder sqlBuilder = context.sqlBuilder();

            sqlBuilder.append(_Constant.SPACE_SET);

            _Pair<FieldMeta<?>, _Expression> pair;
            for (int i = 0; i < columnPairSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                pair = columnPairList.get(i);

                context.appendField(pair.first);
                sqlBuilder.append(_Constant.SPACE_EQUAL);
                pair.second.appendSql(sqlBuilder, context);

            }

        }

    }

    /**
     * @see #parseValuesInsert(_ValueSyntaxInsertContext, _Insert._ValuesSyntaxInsert)
     * @see #parseAssignmentInsert(_AssignmentInsertContext, _Insert._AssignmentInsert)
     * @see #parseQueryInsert(_QueryInsertContext, _Insert._QueryInsert)
     */
    private void appendMySqlConflictClause(final _InsertContext context, final _MySQLInsert stmt) {
        final List<_ItemPair> itemPairList;
        itemPairList = stmt.updateSetClauseList();
        if (itemPairList.size() == 0) {
            return;
        }

        if (context.rowAlias() != null) {
            if (!this.asOf80) {
                String m = String.format("%s don't support row alias clause", this.dialect);
                throw new CriteriaException(m);
            }
            final String safeRowAlias;
            safeRowAlias = context.safeRowAlias();
            assert safeRowAlias != null;
            context.sqlBuilder()
                    .append(_Constant.SPACE_AS_SPACE)
                    .append(safeRowAlias);
        }

        appendInsertConflictSetClause(context, SPACE_ON_DUPLICATE_KEY_UPDATE, itemPairList);

    }

    private CriteriaException dontSupportOrderByWithRollup() {
        return new CriteriaException(String.format("%s don't support%s in ORDER BY clause"
                , this.dialect, SPACE_WITH_ROLLUP));
    }

    private CriteriaException dontSupportLockWord(SQLWords lockOption) {
        return new CriteriaException(String.format("%s don't support %s clause"
                , this.dialect, lockOption.spaceRender()));
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
