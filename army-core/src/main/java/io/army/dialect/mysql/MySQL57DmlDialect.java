package io.army.dialect.mysql;

import io.army.criteria.Delete;
import io.army.criteria.Update;
import io.army.criteria.Visible;
import io.army.criteria._SqlContext;
import io.army.dialect.AbstractDmlDialect;
import io.army.stmt.Stmt;

class MySQL57DmlDialect extends AbstractDmlDialect {

    MySQL57DmlDialect(MySQL57Dialect sql) {
        super(sql);
    }

    /*################################## blow DML method ##################################*/


    /*################################## blow AbstractDML method ##################################*/


    @Override
    public Stmt returningUpdate(Update update, Visible visible) {
        return null;
    }

    @Override
    public Stmt returningDelete(Delete delete, Visible visible) {
        return null;
    }

    @Override
    protected final void tableOnlyModifier(_SqlContext context) {
        // do nothing .
    }


}
