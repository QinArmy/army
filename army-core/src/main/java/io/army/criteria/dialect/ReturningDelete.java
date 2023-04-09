package io.army.criteria.dialect;

import io.army.criteria.DeleteStatement;
import io.army.criteria.DialectStatement;
import io.army.criteria.SimpleDqlStatement;

public interface ReturningDelete extends DeleteStatement,
        SimpleDqlStatement,
        DialectStatement {


}
