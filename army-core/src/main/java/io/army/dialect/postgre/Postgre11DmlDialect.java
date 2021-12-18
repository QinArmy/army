package io.army.dialect.postgre;

import io.army.criteria.Delete;
import io.army.criteria.Update;
import io.army.criteria.Visible;
import io.army.dialect.AbstractDm;
import io.army.stmt.Stmt;

class Postgre11DmlDialect extends AbstractDm {

    Postgre11DmlDialect(Postgre11Dialect dialect) {
        super(dialect);
    }


    @Override
    public Stmt returningUpdate(Update update, Visible visible) {
        return null;
    }

    @Override
    public Stmt returningDelete(Delete delete, Visible visible) {
        return null;
    }
}
