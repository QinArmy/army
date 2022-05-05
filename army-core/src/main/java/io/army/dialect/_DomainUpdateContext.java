package io.army.dialect;


import io.army.lang.Nullable;
import io.army.meta.SingleTableMeta;

public interface _DomainUpdateContext extends _UpdateContext, _SetBlock {


    @Override
    SingleTableMeta<?> table();


    @Nullable
    _SetBlock childBlock();


}