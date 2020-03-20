package io.army.criteria.impl;

import io.army.criteria.impl.inner.InnerObjectDeleteAble;

class ObjectDeleteAbleImpl<C> extends AbstractContextualDelete<C> implements InnerObjectDeleteAble {


    ObjectDeleteAbleImpl(C criteria) {
        super(criteria);
    }

    @Override
    int tableWrapperCount() {
        return 0;
    }
}
