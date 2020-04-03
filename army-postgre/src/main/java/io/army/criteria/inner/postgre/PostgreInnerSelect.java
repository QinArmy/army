package io.army.criteria.inner.postgre;

import io.army.criteria.SQLModifier;
import io.army.criteria.impl.inner.DeveloperForbid;
import io.army.criteria.postgre.PostgreWithQuery;
import io.army.lang.Nullable;

import java.util.List;

@DeveloperForbid
public interface PostgreInnerSelect extends PostgreInnerQuery {

    @Nullable
    SQLModifier recursive();

    /**
     * @return a unmodifiable list
     */
    List<PostgreWithQuery> withQueryList();


}
