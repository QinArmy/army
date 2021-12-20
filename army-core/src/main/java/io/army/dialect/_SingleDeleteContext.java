package io.army.dialect;

import io.army.lang.Nullable;
import io.army.meta.SingleTableMeta;

public interface _SingleDeleteContext extends _DeleteContext, _Block {

    SingleTableMeta<?> table();

    @Nullable
    _Block childBlock();

}
