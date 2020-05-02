package io.army.dialect;

import io.army.beans.BeanWrapper;
import io.army.criteria.SQLContext;
import io.army.criteria.Visible;
import io.army.wrapper.DomainSQLWrapper;
import io.army.wrapper.SQLWrapper;

public interface ClauseSQLContext extends SQLContext {

    void currentClause(Clause clause);

    Visible visible();

    default SQLWrapper build() {
        throw new UnsupportedOperationException();
    }

    default DomainSQLWrapper build(BeanWrapper beanWrapper) {
        throw new UnsupportedOperationException();
    }
}
