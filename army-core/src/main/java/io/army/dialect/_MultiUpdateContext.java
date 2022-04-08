package io.army.dialect;

import io.army.criteria.impl.inner._MultiUpdate;


public interface _MultiUpdateContext extends _UpdateContext, _SetClause {


    void appendAfterSetClause();

    _MultiUpdate statement();

}
