package io.army.dialect;

import io.army.criteria.Select;
import io.army.criteria.SubQuery;
import io.army.criteria.Visible;
import io.army.criteria._SqlContext;
import io.army.stmt.SimpleStmt;

public interface DqlDialect extends SqlDialect {

    SimpleStmt select(Select select, Visible visible);

    void select(Select select, _SqlContext original);


    void subQuery(SubQuery subQuery, _SqlContext original);

}
