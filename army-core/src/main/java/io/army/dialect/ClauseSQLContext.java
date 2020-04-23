package io.army.dialect;

import io.army.beans.BeanWrapper;
import io.army.criteria.SQLContext;
import io.army.criteria.Visible;

public interface ClauseSQLContext extends SQLContext {

    void currentClause(Clause clause);

    Visible visible();

    default SQLWrapper build() {
        throw new UnsupportedOperationException();
    }

    default BeanSQLWrapper build(BeanWrapper beanWrapper) {
        throw new UnsupportedOperationException();
    }
}
