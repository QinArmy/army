package io.army.criteria.impl;

import io.army.criteria.impl.inner.InnerObjectDeleteAble;

class ObjectDeleteAbleImpl<C> extends DeleteAbleImpl<C> implements InnerObjectDeleteAble {


    ObjectDeleteAbleImpl(C criteria) {
        super(criteria);
    }
}
