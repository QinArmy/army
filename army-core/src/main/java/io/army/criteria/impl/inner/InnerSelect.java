package io.army.criteria.impl.inner;

import io.army.criteria.LockMode;

@DeveloperForbid
public interface InnerSelect extends InnerQuery {

    LockMode lockMode();
}
