package io.army.criteria.impl.inner.postgre;

import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._TableBlock;
import io.army.lang.Nullable;

public interface _PostgreTableBlock extends _TableBlock {

    @Nullable
    _Expression sampleMethod();

    @Nullable
    _Expression seed();


}
