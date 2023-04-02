package io.army.dialect.postgre;

import io.army.criteria.*;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl._PostgreConsultant;
import io.army.criteria.impl.inner.*;
import io.army.criteria.impl.inner.postgre._ConflictTargetItem;
import io.army.criteria.impl.inner.postgre._PostgreInsert;
import io.army.dialect.*;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

import java.util.List;


/**
 * <p>
 * This class is the implementation of {@link DialectParser} for  PostgreSQL dialect criteria api.
 * </p>
 * <p>
 * Below is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 *
 * @since 1.0
 */
final class PostgreDialectParser extends PostgreParser {

    private int updateItemSize;

    static PostgreDialectParser create(DialectEnv environment, PostgreDialect dialect) {
        return new PostgreDialectParser(environment, dialect);
    }

    private static final String SPACE_ON_CONFLICT = " ON CONFLICT";

    private static final String SPACE_ON_CONSTRAINT = " ON CONSTRAINT";

    private PostgreDialectParser(DialectEnv environment, PostgreDialect dialect) {
        super(environment, dialect);
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
        final StringBuilder sqlBuilder = context.sqlBuilder();

        final _Expression rowCountExp, offsetExp;
        rowCountExp = rowSet.rowCountExp();
        offsetExp = rowSet.offsetExp();

        final _Statement._SQL2008LimitClauseSpec clause = (_Statement._SQL2008LimitClauseSpec) rowSet;
        final SQLWords fetchFirstNext;
        fetchFirstNext = clause.fetchFirstOrNext();

        // LIMIT clause
        if (fetchFirstNext == null && rowCountExp != null) {
            sqlBuilder.append(_Constant.SPACE_LIMIT);
            rowCountExp.appendSql(context);
        }


        // OFFSET clause
        if (offsetExp != null) {
            sqlBuilder.append(_Constant.SPACE_OFFSET);
            offsetExp.appendSql(context);
            final SQLWords offsetRow;
            if ((offsetRow = clause.offsetRowModifier()) != null) {
                if (offsetRow != SQLs.ROW && offsetRow != SQLs.ROWS) {
                    throw _Exceptions.castCriteriaApi();
                }
                sqlBuilder.append(offsetRow.render());
            }
        }

        // FETCH clause
        if (fetchFirstNext != null) {
            if (rowCountExp == null) {
                throw _Exceptions.castCriteriaApi();
            }

            final SQLWords fetchRowRows, fetchOnlyWithTies;
            fetchRowRows = clause.fetchRowModifier();
            fetchOnlyWithTies = clause.fetchOnlyOrWithTies();

            if (fetchRowRows != SQLs.ROW && fetchRowRows != SQLs.ROWS) {
                throw _Exceptions.castCriteriaApi();
            } else if (fetchOnlyWithTies == null || clause.fetchPercentModifier() != null) {
                throw _Exceptions.castCriteriaApi();
            } else if (fetchOnlyWithTies != SQLs.ONLY && fetchOnlyWithTies != SQLs.WITH_TIES) {
                throw errorFetchOnlyOrWithTies(fetchOnlyWithTies);
            }

            sqlBuilder.append(_Constant.SPACE_FETCH)
                    .append(fetchFirstNext.render());

            rowCountExp.appendSql(context);

            sqlBuilder.append(fetchRowRows.render())
                    .append(fetchOnlyWithTies.render());

        }


    }


    @Override
    protected void parseWithClause(final _Statement._WithClauseSpec spec, final _SqlContext context) {
        super.parseWithClause(spec, context);
    }

    @Override
    protected void parseValuesInsert(final _ValueInsertContext context, final _Insert._ValuesSyntaxInsert insert) {

        this.parsePostgreInsert(context, (_PostgreInsert) insert);

    }


    @Override
    protected void parseQueryInsert(_QueryInsertContext context, _Insert._QueryInsert insert) {

        this.parsePostgreInsert(context, (_PostgreInsert) insert);

    }

    @Override
    protected void parseSimpleQuery(_Query query, _SimpleQueryContext context) {
        super.parseSimpleQuery(query, context);
    }

    @Override
    protected void parseSimpleValues(_ValuesQuery values, _ValuesContext context) {
        super.parseSimpleValues(values, context);
    }

    @Override
    protected void parseSingleUpdate(_SingleUpdate update, _SingleUpdateContext context) {
        super.parseSingleUpdate(update, context);
    }

    @Override
    protected void parseSingleDelete(_SingleDelete delete, _SingleDeleteContext context) {
        super.parseSingleDelete(delete, context);
    }

    @Override
    protected _PrimaryContext handleDialectDml(@Nullable _SqlContext outerContext, DmlStatement statement,
                                               Visible visible) {
        return super.handleDialectDml(outerContext, statement, visible);
    }

    @Override
    protected _PrimaryContext handleDialectDql(@Nullable _SqlContext outerContext, DqlStatement statement,
                                               Visible visible) {
        return super.handleDialectDql(outerContext, statement, visible);
    }


    /**
     * @see #parseValuesInsert(_ValueInsertContext, _Insert._ValuesSyntaxInsert)
     * @see #parseQueryInsert(_QueryInsertContext, _Insert._QueryInsert)
     */
    private void parsePostgreInsert(final _InsertContext context, final _PostgreInsert stmt) {
        final StringBuilder sqlBuilder;
        if ((sqlBuilder = context.sqlBuilder()).length() > 0) {
            sqlBuilder.append(_Constant.SPACE);
        }
        // 1. INSERT INTO key words
        sqlBuilder.append(_Constant.INSERT)
                .append(_Constant.SPACE_INTO_SPACE);

        // 2. table name
        final TableMeta<?> insertTable;
        insertTable = context.insertTable();
        assert insertTable == stmt.insertTable();

        this.safeObjectName(insertTable, sqlBuilder);

        // 3. row alias
        final String safeRowAlias;
        if ((safeRowAlias = context.safeRowAlias()) != null) {
            sqlBuilder.append(_Constant.SPACE_AS_SPACE)
                    .append(safeRowAlias);
        }

        // 4. append column list
        ((_InsertContext._ColumnListSpec) context).appendFieldList();

        // 5. OVERRIDING { SYSTEM | USER } VALUE clause
        final SQLWords overridingModifier;
        if ((overridingModifier = stmt.overridingValueWords()) != null) {
            context.sqlBuilder().append(overridingModifier.render());
        }
        // due to army manage createTime(updateTime) field,so army don't support DEFAULT VALUES

        // 6. VALUES/QUERY clause
        if (context instanceof _ValueInsertContext) {
            ((_ValueInsertContext) context).appendValueList();
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
            this.returningClause(context, stmt);
        } else if (context instanceof _ValueInsertContext) {
            ((_ValueInsertContext) context).appendReturnIdIfNeed();
        }
    }

    /**
     * @see #parsePostgreInsert(_InsertContext, _PostgreInsert)
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
                targetItemList.get(i).appendSql(context);
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
        final List<_ItemPair> updateItemList;
        updateItemList = clause.updateSetClauseList();
        final int updateItemSize;
        updateItemSize = updateItemList.size();
        if (clause.isDoNothing()) {
            if (updateItemSize > 0 || clause.updateSetPredicateList().size() > 0) {
                throw _Exceptions.castCriteriaApi();
            }
            sqlBuilder.append(" DO NOTHING");
        } else if (updateItemSize == 0) {
            throw _Exceptions.castCriteriaApi();
        } else {
            sqlBuilder.append(" DO UPDATE SET");
            for (int i = 0; i < updateItemSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                updateItemList.get(i).appendItemPair(context);
            }

            final List<_Predicate> updatePredicateList;
            updatePredicateList = clause.updateSetPredicateList();
            if (updatePredicateList.size() > 0) {
                this.dmlWhereClause(updatePredicateList, context);
            }
        }

    }

    /**
     * @see #parsePostgreInsert(_InsertContext, _PostgreInsert)
     */
    private void returningClause(final _SqlContext context, final _Statement._ReturningListSpec stmt) {
        if (!(stmt instanceof _ReturningDml)) {
            return;
        }
        final List<? extends _Selection> selectionList;
        selectionList = stmt.returningList();
        final int selectionSize;
        selectionSize = selectionList.size();

        if (selectionSize == 0) {
            return;
        }

        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder()
                .append(_Constant.SPACE_RETURNING);
        for (int i = 0; i < selectionSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            selectionList.get(i).appendSelectItem(context);
        }


    }


    private static CriteriaException errorFetchOnlyOrWithTies(@Nullable SQLWords words) {
        String m = String.format("Postgre don't support modifier[%s] in FETCH clause.", words);
        return new CriteriaException(m);
    }


}
