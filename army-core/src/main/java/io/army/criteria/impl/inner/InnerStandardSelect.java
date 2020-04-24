package io.army.criteria.impl.inner;

import io.army.criteria.LockMode;

@DeveloperForbid
public interface InnerStandardSelect extends InnerSelect, InnerQuery {

    LockMode lockMode();
}
