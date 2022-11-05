package io.army.criteria.impl.inner.postgre;

import io.army.criteria.SQLWords;
import io.army.criteria.impl.inner._DialectStatement;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.criteria.impl.inner._Update;
import io.army.lang.Nullable;

public interface _PostgreUpdate extends _SingleUpdate, _DialectStatement, _Update._SupportCteDmlSpec {

    @Nullable
    SQLWords modifier();

}
