package io.army.criteria.impl.inner.mysql;

import io.army.criteria.Cte;

import java.util.List;

public interface _MySQLWithClause {

    boolean isRecursive();

    List<Cte> cteList();

}
