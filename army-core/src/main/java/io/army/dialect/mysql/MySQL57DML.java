package io.army.dialect.mysql;

import io.army.criteria.SQLContext;
import io.army.dialect.AbstractDML;

class MySQL57DML extends AbstractDML {

    MySQL57DML(MySQL57Dialect sql) {
        super(sql);
    }

    /*################################## blow DML method ##################################*/

    @Override
    public final boolean singleDeleteHasTableAlias() {
        return false;
    }

    /*################################## blow AbstractDML method ##################################*/

    @Override
    protected final boolean tableAliasAfterAs() {
        return true;
    }

    @Override
    protected final void tableOnlyModifier(SQLContext context) {
        // do nothing .
    }


}
