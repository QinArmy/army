package io.army.criteria.impl.inner;

import io.army.criteria.LockMode;
import io.army.criteria.Select;

@DeveloperForbid
public interface InnerSelect extends InnerQuery, Select {

    LockMode lockMode();
}
