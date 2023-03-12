package io.army.criteria.dialect;

import io.army.criteria.DialectStatement;
import io.army.criteria.DmlStatement;
import io.army.criteria.SimpleDqlStatement;
import io.army.criteria.Statement;

public interface ReturningDelete extends SimpleDqlStatement, DialectStatement, DmlStatement, Statement.DmlStatementSpec {


}
