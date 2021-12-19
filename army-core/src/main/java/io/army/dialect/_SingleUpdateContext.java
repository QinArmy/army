package io.army.dialect;


import io.army.lang.Nullable;
import io.army.meta.SingleTableMeta;

public interface _SingleUpdateContext extends _UpdateContext, _SetBlock {


    @Override
    SingleTableMeta<?> table();

    @Nullable
    _SetBlock childSetClause();


}
