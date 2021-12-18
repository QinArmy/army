package io.army.dialect;


import io.army.lang.Nullable;

public interface _SingleUpdateContext extends _UpdateContext, _SetClause {


    @Nullable
    _SetClause childSetClause();


}
