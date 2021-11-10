package io.army.dialect.mysql;


import io.army.criteria.Delete;
import io.army.criteria.Update;
import io.army.criteria.Visible;
import io.army.dialect.*;
import io.army.session.GenericRmSessionFactory;
import io.army.stmt.Stmt;

import java.util.Set;

/**
 * this class is a  {@link Dialect} implementation then abstract base class of all MySQL 5.7 Dialect
 */
class MySQL57Dialect extends AbstractDialect {


    MySQL57Dialect(GenericRmSessionFactory sessionFactory) {
        super(sessionFactory);

    }

    /*################################## blow interfaces method ##################################*/

    @Override
    public Stmt returningUpdate(Update update, Visible visible) {
        return null;
    }

    @Override
    public Stmt returningDelete(Delete delete, Visible visible) {
        return null;
    }

    @Override
    public Database database() {
        return Database.MySQL;
    }

    @Override
    public final boolean supportSavePoint() {
        // always true
        return true;
    }

    @Override
    public final boolean supportZone() {
        return false;
    }

    @Override
    public final boolean tableAliasAfterAs() {
        return true;
    }

    @Override
    public final boolean singleDeleteHasTableAlias() {
        return false;
    }

    @Override
    public final boolean hasRowKeywords() {
        return true;
    }

    /*####################################### below AbstractDialect template  method #################################*/

    @Override
    protected final String doQuote(String identifier) {
        return "`" + identifier + "`";
    }

    @Override
    protected Set<String> createKeywordsSet() {
        return MySQLDialectUtils.create57KeywordsSet();
    }

    @Override
    protected DDL createDDL() {
        return new MySQL57DDL(this);
    }

    @Override
    protected DmlDialect createDML() {
        return new MySQL57DmlDialect(this);
    }

    @Override
    protected DQL createDQL() {
        return new MySQL57DQL(this);
    }

    @Override
    protected TCL createTCL() {
        return new MySQL57TCL(this);
    }
}
