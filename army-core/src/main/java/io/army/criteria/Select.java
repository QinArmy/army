package io.army.criteria;

import io.army.dialect.Dialect;
import io.army.stmt.SimpleStmt;

public interface Select extends Query {


    @Override
    SimpleStmt mockAsStmt(Dialect dialect, Visible visible);


}
