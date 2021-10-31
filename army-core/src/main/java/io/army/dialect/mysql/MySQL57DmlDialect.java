package io.army.dialect.mysql;

import io.army.criteria.SQLContext;
import io.army.dialect.AbstractDmlDialect;

class MySQL57DmlDialect extends AbstractDmlDialect {

    MySQL57DmlDialect(MySQL57Dialect sql) {
        super(sql);
    }

    /*################################## blow DML method ##################################*/


    /*################################## blow AbstractDML method ##################################*/


    @Override
    protected final void tableOnlyModifier(SQLContext context) {
        // do nothing .
    }


}
