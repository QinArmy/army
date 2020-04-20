package io.army.dialect.mysql;

import io.army.criteria.impl.inner.InnerSpecialInsert;
import io.army.dialect.AbstractDML;
import io.army.dialect.BatchSQLWrapper;
import io.army.dialect.SQLWrapper;

import java.util.List;

class MySQL57DML extends AbstractDML {

    MySQL57DML(MySQL57Dialect sql) {
        super(sql);
    }

    @Override
    protected List<SQLWrapper> specialInsert(InnerSpecialInsert insert) {
        return null;
    }

    @Override
    protected List<BatchSQLWrapper> specialBatchInsert(InnerSpecialInsert insert) {
        return null;
    }
}
