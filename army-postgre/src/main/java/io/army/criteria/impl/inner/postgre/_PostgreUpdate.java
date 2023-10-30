package io.army.criteria.impl.inner.postgre;

import io.army.criteria.SQLWords;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl.inner._DialectStatement;
import io.army.criteria.impl.inner._JoinableUpdate;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.criteria.impl.inner._Statement;

import javax.annotation.Nullable;

public interface _PostgreUpdate extends _SingleUpdate, _DialectStatement, _Statement._WithClauseSpec,
        _JoinableUpdate, _Statement._ReturningListSpec, _Statement._WithDmlSpec {

    @Nullable
    SQLWords modifier();

    @Nullable
    SQLs.SymbolAsterisk asterisk();


}
