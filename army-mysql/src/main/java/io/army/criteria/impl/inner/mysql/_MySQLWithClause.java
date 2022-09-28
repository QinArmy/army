package io.army.criteria.impl.inner.mysql;

import io.army.criteria.impl.inner._Cte;

import java.util.List;

public interface _MySQLWithClause {

    boolean isRecursive();

    List<_Cte> cteList();

}
