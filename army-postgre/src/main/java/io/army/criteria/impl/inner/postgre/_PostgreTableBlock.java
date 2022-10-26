package io.army.criteria.impl.inner.postgre;

import io.army.criteria.impl.inner._DialectTableBlock;
import io.army.criteria.impl.inner._Expression;
import io.army.lang.Nullable;

public interface _PostgreTableBlock extends _DialectTableBlock {

    @Nullable
    _Expression sampleMethod();

    @Nullable
    _Expression seed();


}
