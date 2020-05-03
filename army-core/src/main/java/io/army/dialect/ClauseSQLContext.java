package io.army.dialect;

import io.army.beans.DomainWrapper;
import io.army.criteria.SQLContext;
import io.army.criteria.Visible;
import io.army.wrapper.DomainSQLWrapper;
import io.army.wrapper.SimpleSQLWrapper;

public interface ClauseSQLContext extends SQLContext {

    void currentClause(Clause clause);

    Visible visible();

    Dialect dialect();

    default SimpleSQLWrapper build() {
        throw new UnsupportedOperationException();
    }

    default DomainSQLWrapper build(DomainWrapper beanWrapper) {
        throw new UnsupportedOperationException();
    }
}
