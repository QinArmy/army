package io.army.dialect.mysql;

import io.army.beans.ObjectWrapper;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._ValuesInsert;
import io.army.dialect._ValueInsertContext;
import io.army.stmt.Stmt;

import java.util.List;

class MySQL57Dml extends MysqlDml {

    MySQL57Dml(MySQL57Dialect dialect) {
        super(dialect);
    }



    /*################################## blow DML method ##################################*/


    /*################################## blow AbstractDML method ##################################*/


    @Override
    protected final _ValueInsertContext createValueInsertContext(_ValuesInsert insert, final byte tableIndex
            , List<ObjectWrapper> domainList, Visible visible) {
        return super.createValueInsertContext(insert, tableIndex, domainList, visible);
    }

    @Override
    protected final Stmt standardValueInsert(_ValueInsertContext ctx) {
        return super.standardValueInsert(ctx);
    }


}
