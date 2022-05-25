package io.army.criteria.impl.inner.mysql;

import io.army.criteria.impl.inner._DialectStatement;
import io.army.criteria.impl.inner._Query;
import io.army.criteria.mysql.MySQLQuery;

import java.util.List;

public interface _MySQLQuery extends _Query, _DialectStatement, MySQLQuery {


    boolean groupByWithRollUp();

    List<String> intoVarList();


}
