package io.army.dialect;

import io.army.beans.DomainWrapper;
import io.army.criteria.SQLContext;
import io.army.criteria.Visible;
import io.army.wrapper.DomainSQLWrapper;
import io.army.wrapper.ParamWrapper;
import io.army.wrapper.SimpleSQLWrapper;

import java.util.List;

public interface ClauseSQLContext extends SQLContext {

    void currentClause(Clause clause);

    Visible visible();

    Dialect dialect();

    List<ParamWrapper> paramList();

    SimpleSQLWrapper build();

    DomainSQLWrapper build(DomainWrapper domainWrapper);
}
