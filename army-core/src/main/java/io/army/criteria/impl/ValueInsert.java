package io.army.criteria.impl;

import io.army.criteria.Insert;
import io.army.criteria.impl.inner._ValuesInsert;
import io.army.domain.IDomain;

abstract class ValueInsert<T extends IDomain, C> implements Insert
        , Insert.InsertSpec, Insert.InsertIntoSpec<T, C>, Insert.InsertValuesSpec<T, C>, Insert.InsertOptionSpec<T, C>
        , _ValuesInsert {


}
