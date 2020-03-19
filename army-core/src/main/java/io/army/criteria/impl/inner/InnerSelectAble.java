package io.army.criteria.impl.inner;

import io.army.criteria.LockMode;

@DeveloperForbid
public interface InnerSelectAble extends InnerQuery {

    LockMode lockMode();
}
