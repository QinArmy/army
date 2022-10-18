package io.army.criteria.impl.inner.mysql;

import io.army.criteria.SQLWords;
import io.army.criteria.impl.inner._DialectStatement;
import io.army.criteria.impl.inner._Query;
import io.army.criteria.impl.inner._Statement;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.List;

public interface _MySQLQuery extends _Query, _DialectStatement, _Statement._WithClauseSpec, _Statement._LimitClauseSpec {

    boolean groupByWithRollUp();

    boolean orderByWithRollup();

    List<TableMeta<?>> lockOfTableList();

    @Nullable
    SQLWords lockMode();

    @Nullable
    SQLWords lockWaitOption();

    List<String> intoVarList();


}
