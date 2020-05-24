package io.army.dialect.mysql;

import io.army.criteria.SQLContext;
import io.army.dialect.AbstractDML;

class MySQL57DML extends AbstractDML {

    MySQL57DML(MySQL57Dialect sql) {
        super(sql);
    }

    /*################################## blow DML method ##################################*/


    /*################################## blow AbstractDML method ##################################*/


    @Override
    protected final void tableOnlyModifier(SQLContext context) {
        // do nothing .
    }


}
