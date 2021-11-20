package io.army.criteria.impl.inner.mysql;


import io.army.criteria.SortPart;
import io.army.criteria.impl.inner.DeveloperForbid;
import io.army.criteria.impl.inner._SingleDml;
import io.army.criteria.impl.inner._SpecialUpdate;
import io.army.criteria.impl.inner._Update;

import java.util.List;

@DeveloperForbid
public interface InnerMySQLSingleUpdate extends _SpecialUpdate, _Update, _SingleDml {

    /**
     * @return a unmodifiable list
     */
    List<SortPart> sortExpList();

    int rowCount();
}
