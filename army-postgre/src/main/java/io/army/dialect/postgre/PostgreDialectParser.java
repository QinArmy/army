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

package io.army.dialect.postgre;

import io.army.criteria.*;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl._JoinType;
import io.army.criteria.impl._PostgreConsultant;
import io.army.criteria.impl.inner.*;
import io.army.criteria.impl.inner.postgre.*;
import io.army.dialect.*;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;
import io.army.modelgen._MetaBridge;
import io.army.session.SessionSpec;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.util.List;


/**
 * <p>This class is the implementation of {@link DialectParser} for  PostgreSQL dialect criteria api.
 * <p>Below is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 *
 * @since 0.6.0
 */
final class PostgreDialectParser extends PostgreParser {


    static PostgreDialectParser create(DialectEnv environment, PostgreDialect dialect) {
        return new PostgreDialectParser(environment, dialect);
    }

    private static final String SPACE_ON_CONFLICT = " ON CONFLICT";

    private static final String SPACE_ON_CONSTRAINT = " ON CONSTRAINT";

    private PostgreDialectParser(DialectEnv environment, PostgreDialect dialect) {
        super(environment, dialect);
    }


    @Override
    public String sqlElement(final SQLElement element) {
        _PostgreConsultant.assertSqlElement(element);
        if (!(element instanceof _TableNameElement)) {
            throw _Exceptions.castCriteriaApi();
        }
        //TODO
        return this.safeObjectName(((_TableNameElement) element).tableMeta());
    }

    @Override
    protected void assertInsert(InsertStatement insert) {
        _PostgreConsultant.assertInsert(insert);
    }

    @Override
    protected void assertUpdate(UpdateStatement update) {
        _PostgreConsultant.assertUpdate(update);
    }

    @Override
    protected void assertDelete(DeleteStatement delete) {
        _PostgreConsultant.assertDelete(delete);
    }

    @Override
    protected void assertRowSet(final RowSet query) {
        _PostgreConsultant.assertRowSet(query);
    }


    @Override
    protected void parseClauseAfterRightParen(final _ParensRowSet rowSet, final _ParenRowSetContext context) {
        this.orderByClause(rowSet.orderByList(), context);
        postgreLimitClause((_Statement._SQL2008LimitClauseSpec) rowSet, context);
    }


    @Override
    protected void postgreWithClause(final _Statement._WithClauseSpec spec, final _SqlContext context) {
        final List<_Cte> cteList = spec.cteList();
        final int cteSize = cteList.size();
        if (cteSize == 0) {
            return;
        }
        final StringBuilder sqlBuilder;
        if ((sqlBuilder = context.sqlBuilder()).length() > 0) {
            sqlBuilder.append(_Constant.SPACE);
        }
        sqlBuilder.append(_Constant.WITH);
        if (spec.isRecursive()) {
            sqlBuilder.append(_Constant.SPACE_RECURSIVE);
        }
        sqlBuilder.append(_Constant.SPACE);

        _PostgreCte cte;
        SubStatement subStatement;
        List<String> columnAliasList;

        for (int i = 0, columnSize; i < cteSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);
            }
            cte = (_PostgreCte) cteList.get(i);

            this.identifier(cte.name(), sqlBuilder);

            columnAliasList = cte.columnAliasList();
            columnSize = columnAliasList.size();
            if (columnSize > 0) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN)
                        .append(_Constant.SPACE);
                for (int columnIndex = 0; columnIndex < columnSize; columnIndex++) {
                    if (columnIndex > 0) {
                        sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);
                    }
                    this.identifier(columnAliasList.get(columnIndex), sqlBuilder);
                }
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }
            sqlBuilder.append(_Constant.SPACE_AS)
                    .append(_Constant.SPACE_LEFT_PAREN);
            subStatement = cte.subStatement();
            subStatement.prepared();
            if (subStatement instanceof SubQuery) {
                this.handleQuery((SubQuery) subStatement, context);
            } else if (subStatement instanceof _Insert) {
                _PostgreConsultant.assertSubInsert(subStatement);
                this.handleInsertStmtFromWithClause(context, (_Insert) subStatement);
            } else if (subStatement instanceof _Update) {
                _PostgreConsultant.assertSubUpdate(subStatement);
                parseSingleUpdate((_SingleUpdate) subStatement, createJoinableUpdateContextForCte(context, (_SingleUpdate) subStatement));
            } else if (subStatement instanceof _Delete) {
                _PostgreConsultant.assertSubDelete(subStatement);
                parseSingleDelete((_SingleDelete) subStatement, createJoinableDeleteContextForCte(context, (_SingleDelete) subStatement));
            } else if (subStatement instanceof SubValues) {
                handleValuesQuery((ValuesQuery) subStatement, context);
            } else {
                throw _Exceptions.unexpectedStatement(subStatement);
            }
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);


        }

    }

    @Override
    protected void parseValuesInsert(final _ValueSyntaxInsertContext context, final _Insert._ValuesSyntaxInsert insert) {

        this.parsePostgreInsert(context, (_PostgreInsert) insert);

    }


    @Override
    protected void parseQueryInsert(final _QueryInsertContext context, final _Insert._QueryInsert insert) {

        this.parsePostgreInsert(context, (_PostgreInsert) insert);

    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-select.html">SELECT Statement</a>
     */
    @Override
    protected void parseSimpleQuery(final _Query query, final _SimpleQueryContext context) {
        final _PostgreQuery stmt = (_PostgreQuery) query;
        // 1. WITH clause
        this.postgreWithClause(stmt, context);

        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder();
        if (sqlBuilder.length() > 0) {
            sqlBuilder.append(_Constant.SPACE);
        }
        // 2. SELECT key word
        sqlBuilder.append(_Constant.SELECT);
        // 3. modifiers
        this.selectModifierClause(stmt.modifierList(), context, _PostgreConsultant::queryModifier);
        // 4. DISTINCT ON expression clause
        distinctOnExpressionsClause(stmt, context);
        // 5. selection list clause
        this.selectionListClause(context);

        // 6. FROM clause
        final List<_TabularBlock> tableBlockList;
        tableBlockList = stmt.tableBlockList();
        if (tableBlockList.size() > 0) {
            sqlBuilder.append(_Constant.SPACE_FROM);
            this.postgreFromItemsClause(stmt.tableBlockList(), context, false);
        }

        // 7. WHERE clause
        this.queryWhereClause(tableBlockList, stmt.wherePredicateList(), context);

        // 8. GROUP BY and having clause
        final List<? extends GroupByItem> groupByItemList;
        final int groupItemSize;
        if ((groupItemSize = (groupByItemList = stmt.groupByList()).size()) > 0) {
            sqlBuilder.append(_Constant.SPACE_GROUP_BY);
            final SQLs.Modifier modifier = stmt.groupByModifier();
            if (modifier != null) {
                sqlBuilder.append(modifier.spaceRender());
            }
            for (int i = 0; i < groupItemSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                ((_SelfDescribed) groupByItemList.get(i)).appendSql(sqlBuilder, context);
            }
            this.havingClause(stmt.havingList(), context);
        }

        // 9. WINDOW clause
        final List<_Window> windowList;
        windowList = stmt.windowList();
        if (windowList.size() > 0) {
            this.windowClause(windowList, context, _PostgreConsultant::assertWindow);
        }

        // 10. ORDER BY clause
        this.orderByClause(stmt.orderByList(), context);
        // 11. LIMIT OFFSET FETCH clause
        postgreLimitClause(stmt, context);
        // 12 LOCK clause
        postgreLockClause(stmt, context);

    }


    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-values.html">VALUES Statement</a>
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
        valuesClauseOfValues(context, null, values.rowList());
        //3. ORDER BY clause
        orderByClause(values.orderByList(), context);

        //4. LIMIT clause
        postgreLimitClause((_PostgreValues) values, context);

    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-update.html">UPDATE Statement</a>
     */
    @Override
    protected void parseSingleUpdate(final _SingleUpdate update, final _SingleUpdateContext context) {
        final _PostgreUpdate stmt = (_PostgreUpdate) update;

        // 1. WITH clause
        this.postgreWithClause(stmt, context);

        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder();
        if (sqlBuilder.length() > 0) {
            sqlBuilder.append(_Constant.SPACE);
        }
        // 2. UPDATE key word
        sqlBuilder.append(_Constant.UPDATE);
        // 3. ONLY modifier
        final SQLWords onlyModifier;
        onlyModifier = stmt.modifier();
        if (onlyModifier != null) {
            assert onlyModifier == SQLs.ONLY;
            sqlBuilder.append(onlyModifier.spaceRender());
        }
        // 4. table name
        sqlBuilder.append(_Constant.SPACE);
        final TableMeta<?> updateTable = context.targetTable();
        assert updateTable == stmt.table();
        this.safeObjectName(updateTable, sqlBuilder);

        // 5. table alias
        final String safeTableAlias;
        safeTableAlias = context.safeTargetTableAlias();
        sqlBuilder.append(_Constant.SPACE_AS_SPACE)
                .append(safeTableAlias);
        // 6. SET clause
        this.singleTableSetClause(stmt.itemPairList(), context);

        // 7. FROM clause
        final List<_TabularBlock> tableBlockList;
        tableBlockList = stmt.tableBlockList();
        final boolean existsFromClause;
        existsFromClause = tableBlockList.size() > 0;
        if (existsFromClause) {
            sqlBuilder.append(_Constant.SPACE_FROM);
            this.postgreFromItemsClause(stmt.tableBlockList(), (_MultiTableStmtContext) context, false);
        }

        // 8. WHERE clause
        this.dmlWhereClause(stmt.wherePredicateList(), context);
        context.appendConditionFields();

        //TODO discriminator
        if (existsFromClause) {
            this.multiTableVisible(tableBlockList, (_MultiTableStmtContext) context, false);
        } else if (updateTable instanceof SingleTableMeta) {
            this.visiblePredicate((SingleTableMeta<?>) updateTable, safeTableAlias, context, false);
        }

        // 9. RETURNING clause
        if (stmt instanceof _ReturningDml) {
            returningClause(context, (_ReturningDml) stmt);
        }

    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-delete.html">DELETE Statement</a>
     */
    @Override
    protected void parseSingleDelete(final _SingleDelete delete, final _SingleDeleteContext context) {
        final _PostgreDelete stmt = (_PostgreDelete) delete;

        // 1. WITH clause
        this.postgreWithClause(stmt, context);

        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder();
        if (sqlBuilder.length() > 0) {
            sqlBuilder.append(_Constant.SPACE);
        }
        // 2. DELETE key word
        sqlBuilder.append(_Constant.DELETE_FROM);
        // 3. ONLY modifier
        final SQLs.WordOnly onlyModifier;
        onlyModifier = stmt.modifier();
        if (onlyModifier != null) {
            assert onlyModifier == SQLs.ONLY;
            sqlBuilder.append(onlyModifier.spaceRender());
        }
        // 4. table name
        final TableMeta<?> deleteTable = context.targetTable();
        assert deleteTable == stmt.table();
        sqlBuilder.append(_Constant.SPACE);
        this.safeObjectName(deleteTable, sqlBuilder);

        // 5. symbol star
        final SQLs.SymbolAsterisk symbolStar;
        if ((symbolStar = stmt.symbolAsterisk()) != null) {
            assert symbolStar == SQLs.ASTERISK;
            sqlBuilder.append(_Constant.SPACE)
                    .append(_Constant.ASTERISK);
        }

        // 6. table alias
        final String safeTableAlias;
        safeTableAlias = context.safeTargetTableAlias();
        sqlBuilder.append(_Constant.SPACE_AS_SPACE)
                .append(safeTableAlias);

        // 7. USING clause
        final List<_TabularBlock> tableBlockList;
        tableBlockList = stmt.tableBlockList();
        final boolean existsFromClause;
        existsFromClause = tableBlockList.size() > 0;
        if (existsFromClause) {
            sqlBuilder.append(_Constant.SPACE_USING);
            this.postgreFromItemsClause(stmt.tableBlockList(), (_MultiTableStmtContext) context, false);
        }

        // 8. WHERE clause
        this.dmlWhereClause(stmt.wherePredicateList(), context);

        //TODO discriminator
        if (existsFromClause) {
            this.multiTableVisible(tableBlockList, (_MultiTableStmtContext) context, false);
        } else if (deleteTable instanceof SingleTableMeta) {
            this.visiblePredicate((SingleTableMeta<?>) deleteTable, safeTableAlias, context, false);
        }

        // 9. RETURNING clause
        if (stmt instanceof _ReturningDml) {
            returningClause(context, (_ReturningDml) stmt);
        }
    }

    @Override
    protected _PrimaryContext handleDialectDml(@Nullable _SqlContext outerContext, DmlStatement statement,
                                               SessionSpec sessionSpec) {
        return super.handleDialectDml(outerContext, statement, sessionSpec);
    }

    @Override
    protected _PrimaryContext handleDialectDql(@Nullable _SqlContext outerContext, DqlStatement statement,
                                               SessionSpec sessionSpec) {
        return super.handleDialectDql(outerContext, statement, sessionSpec);
    }


    /**
     * @see #parseSimpleQuery(_Query, _SimpleQueryContext)
     * @see #parseSingleUpdate(_SingleUpdate, _SingleUpdateContext)
     * @see #parseSingleDelete(_SingleDelete, _SingleDeleteContext)
     */
    private void postgreFromItemsClause(final List<_TabularBlock> blockList, final _MultiTableStmtContext context,
                                        final boolean nested) {
        final int blockSize = blockList.size();
        assert blockSize > 0;
        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder();

        _TabularBlock block;
        TabularItem tabularItem;
        TableMeta<?> table;
        String alias;
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

            if (tabularItem instanceof TableMeta) {
                table = (TableMeta<?>) tabularItem;

                if (block instanceof _ModifierTabularBlock
                        && (modifier = ((_ModifierTabularBlock) block).modifier()) != null) {
                    assert modifier == SQLs.ONLY;
                    sqlBuilder.append(modifier.spaceRender());
                }
                sqlBuilder.append(_Constant.SPACE);
                this.safeObjectName(table, sqlBuilder);

                sqlBuilder.append(_Constant.SPACE_AS_SPACE)
                        .append(context.safeTableAlias(table, alias));
                if (block instanceof _PostgreTableBlock) {
                    this.postgreTableSampleClause((_PostgreTableBlock) block, sqlBuilder, context);
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
                _PostgreConsultant.assertNestedItems((_NestedItems) tabularItem);
                if (_StringUtils.hasText(alias)) {
                    throw _Exceptions.nestedItemsAliasHasText(alias);
                }
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
                this.postgreFromItemsClause(((_NestedItems) tabularItem).tableBlockList(), context, true);
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            } else if (tabularItem instanceof _Cte) {
                _PostgreConsultant.assertPostgreCte((_Cte) tabularItem);
                sqlBuilder.append(_Constant.SPACE);
                this.identifier(((_Cte) tabularItem).name(), sqlBuilder);
                if (_StringUtils.hasText(alias)) {
                    sqlBuilder.append(_Constant.SPACE_AS_SPACE);
                    this.identifier(alias, sqlBuilder);
                } else if (!"".equals(alias)) {
                    throw _Exceptions.tableItemAliasNoText(tabularItem);
                }
            } else if (tabularItem instanceof UndoneFunction) {
                final _DoneFuncBlock funcBlock = (_DoneFuncBlock) block;
                if ((modifier = funcBlock.modifier()) != null) {
                    assert modifier == SQLs.LATERAL;
                    sqlBuilder.append(modifier.spaceRender());
                }
                // undone function
                ((_SelfDescribed) tabularItem).appendSql(sqlBuilder, context);

                sqlBuilder.append(_Constant.SPACE_AS_SPACE);
                this.identifier(alias, sqlBuilder);

                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
                final List<_FunctionField> fieldList = funcBlock.fieldList();
                final int fieldSize = fieldList.size();
                for (int fieldIndex = 0; fieldIndex < fieldSize; fieldIndex++) {
                    if (fieldIndex > 0) {
                        sqlBuilder.append(_Constant.SPACE_COMMA);
                    }
                    fieldList.get(fieldIndex).appendSql(sqlBuilder, context);
                }
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
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
                    if (!nested) {
                        this.onClause(predicateList, context);
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


    }


    /**
     * @see #postgreFromItemsClause(List, _MultiTableStmtContext, boolean)
     * @see <a href="https://www.postgresql.org/docs/current/sql-select.html">SELECT syntax</a>
     */
    private void postgreTableSampleClause(final _PostgreTableBlock block, final StringBuilder sqlBuilder, final _SqlContext context) {
        _Expression expression;
        expression = block.sampleMethod();
        if (expression != null) {
            sqlBuilder.append(" TABLESAMPLE");
            expression.appendSql(sqlBuilder, context);
        }
        expression = block.seed();
        if (expression != null) {
            sqlBuilder.append(" REPEATABLE")
                    .append(_Constant.LEFT_PAREN);
            expression.appendSql(sqlBuilder, context);
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }

    }


    /**
     * @see #parseValuesInsert(_ValueSyntaxInsertContext, _Insert._ValuesSyntaxInsert)
     * @see #parseQueryInsert(_QueryInsertContext, _Insert._QueryInsert)
     * @see <a href="https://www.postgresql.org/docs/current/sql-insert.html">Postgre INSERT syntax</a>
     */
    private void parsePostgreInsert(final _InsertContext context, final _PostgreInsert stmt) {
        final TableMeta<?> insertTable;
        insertTable = context.insertTable();
        if (stmt instanceof _Insert._OneStmtChildInsert) {
            ((_Insert._OneStmtChildInsert) stmt).validParentDomain();
        }

        postgreWithClause(stmt, context);

        final StringBuilder sqlBuilder;
        if ((sqlBuilder = context.sqlBuilder()).length() > 0) {
            sqlBuilder.append(_Constant.SPACE);
        }
        // 1. INSERT INTO key words
        sqlBuilder.append(_Constant.INSERT)
                .append(_Constant.SPACE_INTO_SPACE);

        // 2. table name
        assert insertTable == stmt.table();
        this.safeObjectName(insertTable, sqlBuilder);

        // 3. table alias
        final String safeTableAlias;
        if ((safeTableAlias = context.safeTableAlias()) != null) {
            sqlBuilder.append(_Constant.SPACE_AS_SPACE)
                    .append(safeTableAlias);
        }

        // 4. append column list
        ((_InsertContext._ColumnListSpec) context).appendFieldList();

        // 5. OVERRIDING { SYSTEM | USER } VALUE clause
        final SQLWords overridingModifier;
        if ((overridingModifier = stmt.overridingValueWords()) != null) {
            sqlBuilder.append(overridingModifier.spaceRender());
        }
        // due to army manage createTime(updateTime) field,so army don't support DEFAULT VALUES

        // 6. VALUES/QUERY clause
        if (context instanceof _ValueSyntaxInsertContext) {
            ((_ValueSyntaxInsertContext) context).appendValueList();
        } else {
            ((_QueryInsertContext) context).appendSubQuery();
        }

        // 7. ON CONFLICT clause
        final _PostgreInsert._ConflictActionClauseResult conflictClause;
        if ((conflictClause = stmt.getConflictActionResult()) != null) {
            this.insertOnConflictClause(context, conflictClause);
        }

        // 8. RETURNING clause
        if (stmt instanceof _ReturningDml) {
            returningClause(context, (_ReturningDml) stmt);
        } else if (context instanceof _ValueSyntaxInsertContext) {
            ((_InsertContext._ReturningIdSpec) context).appendReturnIdIfNeed();
        }

    }


    /**
     * @see #parsePostgreInsert(_InsertContext, _PostgreInsert)
     * @see <a href="https://www.postgresql.org/docs/current/sql-insert.html">Postgre INSERT syntax</a>
     */
    private void insertOnConflictClause(final _InsertContext context,
                                        final _PostgreInsert._ConflictActionClauseResult clause) {
        final StringBuilder sqlBuilder;
        // 1. ON CONFLICT key words
        sqlBuilder = context.sqlBuilder()
                .append(SPACE_ON_CONFLICT);


        final List<_ConflictTargetItem> targetItemList;
        targetItemList = clause.conflictTargetItemList();

        final String constraintName;
        constraintName = clause.constraintName();

        final int targetItemSize;
        targetItemSize = targetItemList.size();
        if (constraintName != null && targetItemSize > 0) {
            throw _Exceptions.castCriteriaApi();
        }

        // 2. below conflict_target clause
        if (constraintName != null) {
            sqlBuilder.append(SPACE_ON_CONSTRAINT)
                    .append(_Constant.SPACE);

            this.identifier(constraintName, sqlBuilder);
        } else if (targetItemSize > 0) {
            sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            for (int i = 0; i < targetItemSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                targetItemList.get(i).appendSql(sqlBuilder, context);
            }
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

            final List<_Predicate> indexPredicateList;
            indexPredicateList = clause.indexPredicateList();
            if (indexPredicateList.size() > 0) {
                this.dmlWhereClause(indexPredicateList, context);
            }
        } else if (clause.indexPredicateList().size() > 0) {
            throw _Exceptions.castCriteriaApi();
        }

        //3. below conflict_action clause
        if (clause.isDoNothing()) {
            if (clause.updateSetClauseList().size() > 0 || clause.updateSetPredicateList().size() > 0) {
                throw _Exceptions.castCriteriaApi();
            }
            sqlBuilder.append(" DO NOTHING");
        } else if (clause.updateSetClauseList().size() == 0
                || (constraintName == null && targetItemSize == 0)) { // For ON CONFLICT DO UPDATE, a conflict_target must be provided.
            throw _Exceptions.castCriteriaApi();
        } else {
            context.outputFieldTableAlias(true);
            this.insertDoUpdateSetClause(context, clause);
            context.outputFieldTableAlias(false);
        }

    }

    /**
     * @see #insertOnConflictClause(_InsertContext, _PostgreInsert._ConflictActionClauseResult)
     * @see <a href="https://www.postgresql.org/docs/current/sql-insert.html">Postgre INSERT syntax</a>
     */
    private void insertDoUpdateSetClause(final _InsertContext context,
                                         final _PostgreInsert._ConflictActionClauseResult clause) {


        final List<_ItemPair> updateItemList;
        updateItemList = clause.updateSetClauseList();
        final int updateItemSize;
        updateItemSize = updateItemList.size();
        assert updateItemSize > 0;

        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder();
        sqlBuilder.append(" DO UPDATE SET");

        for (int i = 0; i < updateItemSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            updateItemList.get(i).appendItemPair(sqlBuilder, context);
        }
        final TableMeta<?> insertTable = context.insertTable();

        final List<_Predicate> updatePredicateList;
        updatePredicateList = clause.updateSetPredicateList();
        final boolean visibleIsFirstPredicate;
        if (updatePredicateList.size() > 0) {
            this.dmlWhereClause(updatePredicateList, context);
            context.appendConditionPredicate(false);
            visibleIsFirstPredicate = false;
        } else if (context.hasConditionPredicate()) {
            sqlBuilder.append(_Constant.SPACE_WHERE);
            context.appendConditionPredicate(true);
            visibleIsFirstPredicate = false;
        } else {
            visibleIsFirstPredicate = true;
        }

        if (context.visible() != Visible.BOTH && insertTable.containComplexField(_MetaBridge.VISIBLE)) {
            if (visibleIsFirstPredicate) {
                sqlBuilder.append(_Constant.SPACE_WHERE);
            }
            if (insertTable instanceof SingleTableMeta) {
                this.visiblePredicate((SingleTableMeta<?>) insertTable, context.safeTableAliasOrSafeTableName(), context, visibleIsFirstPredicate);
            } else {
                this.parentVisiblePredicate(context, visibleIsFirstPredicate);
            }

        }

    }


    /**
     * @see #parseSimpleQuery(_Query, _SimpleQueryContext)
     */
    private void postgreLockClause(final _PostgreQuery stmt, final _SimpleQueryContext context) {
        final List<_Query._LockBlock> blockList = stmt.lockBlockList();
        if (blockList.size() == 0) {
            return;
        }

        final List<? extends SQLWords> modifierList = stmt.modifierList();
        if (modifierList.contains(Postgres.DISTINCT)) {
            String m = String.format("%s Currently, lock clause cannot be specified with DISTINCT", this.dialect);
            throw new CriteriaException(m);
        }

        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder();
        List<String> tableAliasList;
        int tableAliasSize;
        SQLWords waitOption;
        for (_Query._LockBlock block : blockList) {

            sqlBuilder.append(block.lockStrength().spaceRender());

            tableAliasList = block.lockTableAliasList();
            tableAliasSize = tableAliasList.size();

            if (tableAliasSize > 0) {
                sqlBuilder.append(_Constant.SPACE_OF_SPACE);
                for (int i = 0; i < tableAliasSize; i++) {
                    if (i > 0) {
                        sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);
                    }
                    sqlBuilder.append(context.safeTableAlias(tableAliasList.get(i)));
                }

            }// if(tableAliasSize >0)

            waitOption = block.lockWaitOption();
            if (waitOption != null) {
                sqlBuilder.append(waitOption.spaceRender());
            }


        }

    }

    /*-------------------below static method -------------------*/

    /**
     * @see #parsePostgreInsert(_InsertContext, _PostgreInsert)
     * @see #parseSingleUpdate(_SingleUpdate, _SingleUpdateContext)
     * @see #parseSingleDelete(_SingleDelete, _SingleDeleteContext)
     */
    private static void returningClause(final _SqlContext context, final _ReturningDml stmt) {
        final List<? extends _SelectItem> selectionList;
        selectionList = stmt.returningList();
        final int selectionSize;
        selectionSize = selectionList.size();

        if (selectionSize == 0) {
            return;
        }

        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder()
                .append(_Constant.SPACE_RETURNING);

        if (context instanceof _InsertContext
                && ((_InsertContext) context).tableAlias() != null) {
            ((_InsertContext) context).outputFieldTableAlias(true);
        }

        for (int i = 0; i < selectionSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            selectionList.get(i).appendSelectItem(sqlBuilder, context);
        }


    }

    /**
     * @see #parseSimpleQuery(_Query, _SimpleQueryContext)
     */
    private static void distinctOnExpressionsClause(final _PostgreQuery stmt, final _SimpleQueryContext context) {
        final List<_Expression> expList = stmt.distinctOnExpressions();
        final int distinctOnExpSize = expList.size();
        if (distinctOnExpSize == 0) {
            return;
        }
        final List<? extends SQLWords> modifierList = stmt.modifierList();
        if (modifierList.size() != 1 || modifierList.get(0) != Postgres.DISTINCT) {
            throw _Exceptions.castCriteriaApi();
        }

        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder()
                .append(_Constant.SPACE_ON)
                .append(_Constant.LEFT_PAREN);
        for (int i = 0; i < distinctOnExpSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            expList.get(i).appendSql(sqlBuilder, context);
        }
        sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

    }

    /**
     * @see #parseClauseAfterRightParen(_ParensRowSet, _ParenRowSetContext)
     * @see #parseSimpleQuery(_Query, _SimpleQueryContext)
     * @see #parseSimpleValues(_ValuesQuery, _ValuesContext)
     * @see #parseSimpleValues(_ValuesQuery, _ValuesContext)
     */
    private static void postgreLimitClause(final _Statement._SQL2008LimitClauseSpec stmt, final _SqlContext context) {
        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder();

        final _Expression rowCountExp, offsetExp;
        rowCountExp = stmt.rowCountExp();
        offsetExp = stmt.offsetExp();

        final SQLWords fetchFirstNext;
        fetchFirstNext = stmt.fetchFirstOrNext();

        // LIMIT clause
        if (fetchFirstNext == null && rowCountExp != null) {
            sqlBuilder.append(_Constant.SPACE_LIMIT);
            rowCountExp.appendSql(sqlBuilder, context);
        }


        // OFFSET clause
        if (offsetExp != null) {
            sqlBuilder.append(_Constant.SPACE_OFFSET);
            offsetExp.appendSql(sqlBuilder, context);
            final SQLWords offsetRow;
            if ((offsetRow = stmt.offsetRowModifier()) != null) {
                if (offsetRow != SQLs.ROW && offsetRow != SQLs.ROWS) {
                    throw _Exceptions.castCriteriaApi();
                }
                sqlBuilder.append(offsetRow.spaceRender());
            }
        }

        // FETCH clause
        if (fetchFirstNext != null) {
            if (rowCountExp == null) {
                throw _Exceptions.castCriteriaApi();
            }

            final SQLWords fetchRowRows, fetchOnlyWithTies;
            fetchRowRows = stmt.fetchRowModifier();
            fetchOnlyWithTies = stmt.fetchOnlyOrWithTies();

            if (fetchRowRows != SQLs.ROW && fetchRowRows != SQLs.ROWS) {
                throw _Exceptions.castCriteriaApi();
            } else if (fetchOnlyWithTies == null || stmt.fetchPercentModifier() != null) {
                throw _Exceptions.castCriteriaApi();
            } else if (fetchOnlyWithTies != SQLs.ONLY && fetchOnlyWithTies != SQLs.WITH_TIES) {
                throw errorFetchOnlyOrWithTies(fetchOnlyWithTies);
            }

            sqlBuilder.append(_Constant.SPACE_FETCH)
                    .append(fetchFirstNext.spaceRender());

            rowCountExp.appendSql(sqlBuilder, context);

            sqlBuilder.append(fetchRowRows.spaceRender())
                    .append(fetchOnlyWithTies.spaceRender());

        }


    }


    private static CriteriaException errorFetchOnlyOrWithTies(@Nullable SQLWords words) {
        String m = String.format("Postgre don't support modifier[%s] in FETCH clause.", words);
        return new CriteriaException(m);
    }


}
