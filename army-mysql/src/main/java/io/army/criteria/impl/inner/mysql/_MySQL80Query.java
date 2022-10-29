package io.army.criteria.impl.inner.mysql;

import io.army.criteria.SQLWords;
import io.army.criteria.Window;
import io.army.criteria.impl.inner._Cte;
import io.army.lang.Nullable;

import java.util.List;

public interface _MySQL80Query extends _MySQLQuery, MySQL80Query {

    boolean isRecursive();

    List<_Cte> cteList();

    List<Window> windowList();

    boolean orderByWithRollup();

    @Nullable
    SQLWords lockMode();

    List<String> ofTableList();

    @Nullable
    SQLWords lockOption();

}
