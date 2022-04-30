package io.army.criteria.impl.inner.mysql;

import io.army.criteria.Cte;
import io.army.criteria.SQLWords;
import io.army.criteria.Window;
import io.army.criteria.mysql.MySQL80Query;

import java.util.List;

public interface _MySQL80Query extends _MySQLQuery, MySQL80Query {

    boolean isRecursive();

    List<Cte> cteList();

    List<Window> windowList();

    boolean orderByWithRollup();

    SQLWords lockMode();

    List<String> ofTableList();

    SQLWords lockOption();

}
