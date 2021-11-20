package io.army.dialect;

import io.army.criteria.Select;
import io.army.criteria.SqlContext;
import io.army.criteria.SubQuery;
import io.army.criteria.Visible;
import io.army.stmt.SimpleStmt;

public interface DqlDialect extends SqlDialect {

    SimpleStmt select(Select select, Visible visible);

    void select(Select select, SqlContext original);


    void subQuery(SubQuery subQuery, SqlContext original);

}
