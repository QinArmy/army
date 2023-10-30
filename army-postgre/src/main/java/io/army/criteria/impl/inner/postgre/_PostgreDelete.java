package io.army.criteria.impl.inner.postgre;

import io.army.criteria.impl.SQLs;
import io.army.criteria.impl.inner._DialectStatement;
import io.army.criteria.impl.inner._JoinableDelete;
import io.army.criteria.impl.inner._SingleDelete;
import io.army.criteria.impl.inner._Statement;

import javax.annotation.Nullable;

public interface _PostgreDelete extends _SingleDelete,
        _JoinableDelete,
        _DialectStatement,
        _Statement._WithClauseSpec,
        _Statement._ReturningListSpec,
        _Statement._WithDmlSpec {

    @Nullable
    SQLs.WordOnly modifier();

    @Nullable
    SQLs.SymbolAsterisk symbolAsterisk();


}
