package io.army.criteria.impl.inner.postgre;

import io.army.criteria.SQLWords;
import io.army.criteria.impl.inner._DialectStatement;
import io.army.criteria.impl.inner._SingleDelete;
import io.army.lang.Nullable;

public interface _PostgreDelete extends _SingleDelete, _DialectStatement {

    @Nullable
    SQLWords modifier();


}
