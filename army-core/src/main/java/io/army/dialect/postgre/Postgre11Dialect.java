package io.army.dialect.postgre;

import io.army.criteria.Delete;
import io.army.criteria.Update;
import io.army.criteria.Visible;
import io.army.dialect.*;
import io.army.session.GenericRmSessionFactory;
import io.army.stmt.Stmt;

import java.util.Set;

class Postgre11Dialect extends AbstractDialect {


    Postgre11Dialect(GenericRmSessionFactory sessionFactory) {
        super(sessionFactory);
    }

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
        return Database.PostgreSQL;
    }

    @Override
    public final boolean supportSavePoint() {
        //always true
        return true;
    }

    @Override
    protected Set<String> createKeywordsSet() {
        return Postgre11DialectUtils.create11KeywordsSet();
    }

    @Override
    protected final String doQuote(String identifier) {
        return "\"" + identifier + "\"";
    }

    @Override
    protected DdlDialect createDDL() {
        return new Postgre11DDL(this);
    }

    @Override
    protected DmlDialect createDML() {
        return new Postgre11DmlDialect(this);
    }

    @Override
    protected DqlDialect createDQL() {
        return new Postgre11DQL(this);
    }

    @Override
    protected TclDialect createTCL() {
        return new Postgre11TCL(this);
    }

    @Override
    public final boolean supportZone() {
        // always true
        return true;
    }


    @Override
    public final boolean tableAliasAfterAs() {
        // always true
        return true;
    }

    @Override
    public final boolean singleDeleteHasTableAlias() {
        // always true
        return true;
    }

    @Override
    public final boolean hasRowKeywords() {
        // always true
        return true;
    }
}
