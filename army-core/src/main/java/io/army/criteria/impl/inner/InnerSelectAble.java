package io.army.criteria.impl.inner;

import io.army.criteria.LockMode;

@DeveloperForbid
public interface InnerSelectAble extends InnerQueryAble {

    LockMode lockMode();
}
