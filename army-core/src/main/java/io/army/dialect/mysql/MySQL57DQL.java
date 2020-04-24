package io.army.dialect.mysql;

import io.army.criteria.LockMode;
import io.army.criteria.impl.inner.InnerSpecialComposeQuery;
import io.army.criteria.impl.inner.InnerSpecialSelect;
import io.army.criteria.impl.inner.InnerSpecialSubQuery;
import io.army.dialect.AbstractDQL;
import io.army.dialect.ClauseSQLContext;

class MySQL57DQL extends AbstractDQL {

    MySQL57DQL(MySQL57Dialect dialect) {
        super(dialect);
    }



    /*################################## blow AbstractDQL template method ##################################*/

    @Override
    protected void assertSpecialComposeSelect(InnerSpecialComposeQuery select) {

    }

    @Override
    protected void assertSpecialSubQuery(InnerSpecialSubQuery subQuery) {

    }

    @Override
    protected void assertSpecialComposeSubQuery(InnerSpecialComposeQuery composeQuery) {

    }

    @Override
    protected void assertSpecialSelect(InnerSpecialSelect select) {

    }

    @Override
    protected void specialPartSelect(InnerSpecialComposeQuery select, ClauseSQLContext context) {

    }

    @Override
    protected void specialSelect(InnerSpecialSelect specialSelect, ClauseSQLContext context) {

    }

    @Override
    protected void specialSubQuery(InnerSpecialSubQuery composeQuery, ClauseSQLContext context) {

    }

    @Override
    protected void limitClause(int offset, int rowCount, ClauseSQLContext context) {

    }

    @Override
    protected void lockClause(LockMode lockMode, ClauseSQLContext context) {

    }

    @Override
    protected boolean tableAliasAfterAs() {
        return false;
    }
}
