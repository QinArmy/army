package io.army.dialect.postgre;

import io.army.criteria.*;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl._PostgreConsultant;
import io.army.criteria.impl.inner.*;
import io.army.dialect.*;
import io.army.lang.Nullable;
import io.army.util._Exceptions;


final class PostgreDialectParser extends PostgreParser {

    static PostgreDialectParser create(DialectEnv environment, PostgreDialect dialect) {
        return new PostgreDialectParser(environment, dialect);
    }

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
    protected void parseValuesInsert(_ValueInsertContext context, _Insert._ValuesSyntaxInsert insert) {
        super.parseValuesInsert(context, insert);
    }

    @Override
    protected void parseQueryInsert(_QueryInsertContext context, _Insert._QueryInsert insert) {
        super.parseQueryInsert(context, insert);
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


    private static CriteriaException errorFetchOnlyOrWithTies(@Nullable SQLWords words) {
        String m = String.format("Postgre don't support modifier[%s] in FETCH clause.", words);
        return new CriteriaException(m);
    }


}
