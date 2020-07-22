package io.army.criteria.impl.inner.mysql;


import io.army.criteria.SortPart;
import io.army.criteria.impl.inner.DeveloperForbid;
import io.army.criteria.impl.inner.InnerSingleUpdate;
import io.army.criteria.impl.inner.InnerSpecialUpdate;

import java.util.List;

@DeveloperForbid
public interface InnerMySQLSingleUpdate extends InnerSingleUpdate, InnerSpecialUpdate {

    /**
     * @return a unmodifiable list
     */
    List<SortPart> sortExpList();

    int rowCount();
}
