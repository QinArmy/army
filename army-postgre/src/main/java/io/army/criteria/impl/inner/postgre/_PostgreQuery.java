package io.army.criteria.impl.inner.postgre;

import io.army.criteria.impl.SQLs;
import io.army.criteria.impl.inner._DialectStatement;
import io.army.criteria.impl.inner._Query;
import io.army.criteria.impl.inner._Statement;

import javax.annotation.Nullable;

import java.util.List;

public interface _PostgreQuery extends _Query,
        _DialectStatement,
        _Statement._WithClauseSpec,
        _Query._DistinctOnClauseSpec,
        _Query._WindowClauseSpec,
        _Statement._SQL2008LimitClauseSpec {

    @Nullable
    SQLs.Modifier groupByModifier();

    List<_LockBlock> lockBlockList();


}
