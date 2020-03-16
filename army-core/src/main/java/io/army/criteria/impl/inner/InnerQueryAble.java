package io.army.criteria.impl.inner;

import io.army.criteria.LockMode;

public interface InnerQueryAble extends InnerBasicQueryAble {

    LockMode lockMode();
}
