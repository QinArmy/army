package io.army.criteria.impl.inner.mysql;

import io.army.criteria.impl.inner._DialectStatement;
import io.army.criteria.impl.inner._Query;

public interface _MySQLQuery extends _Query, _DialectStatement {

    boolean groupByWithRollUp();


}
