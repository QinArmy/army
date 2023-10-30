package io.army.criteria.impl.inner.mysql;

import io.army.criteria.impl.inner._DialectStatement;
import io.army.criteria.impl.inner._Query;
import io.army.criteria.impl.inner._Statement;

import javax.annotation.Nullable;

import java.util.List;

public interface _MySQLQuery extends _Query,
        _DialectStatement,
        _Statement._WithClauseSpec,
        _Query._WindowClauseSpec,
        _Statement._LimitClauseSpec {

    boolean groupByWithRollUp();

    boolean orderByWithRollup();

    @Nullable
    _LockBlock lockBlock();

    List<String> intoVarList();




}
