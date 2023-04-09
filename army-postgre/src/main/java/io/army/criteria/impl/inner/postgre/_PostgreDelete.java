package io.army.criteria.impl.inner.postgre;

import io.army.criteria.impl.SQLs;
import io.army.criteria.impl._JoinableDelete;
import io.army.criteria.impl.inner._DialectStatement;
import io.army.criteria.impl.inner._SingleDelete;
import io.army.criteria.impl.inner._Statement;
import io.army.lang.Nullable;

public interface _PostgreDelete extends _SingleDelete,
        _JoinableDelete,
        _DialectStatement,
        _Statement._WithClauseSpec,
        _Statement._ReturningListSpec {

    @Nullable
    SQLs.WordOnly modifier();

    @Nullable
    SQLs.SymbolStar symbolStar();


}
