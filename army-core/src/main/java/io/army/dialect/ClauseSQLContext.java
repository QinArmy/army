package io.army.dialect;

import io.army.beans.BeanWrapper;
import io.army.criteria.SQLContext;

public interface ClauseSQLContext extends SQLContext {

    void currentClause(Clause clause);

    default SQLWrapper build() {
        throw new UnsupportedOperationException();
    }

    default BeanSQLWrapper build(BeanWrapper beanWrapper) {
        throw new UnsupportedOperationException();
    }
}
