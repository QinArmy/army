package io.army.criteria.impl.inner.mysql;


import io.army.criteria.SortPart;
import io.army.criteria.impl.inner.*;

import java.util.List;

@DeveloperForbid
public interface InnerMySQLSingleUpdate extends InnerSpecialUpdate, InnerUpdate, InnerSingleDML {

    /**
     * @return a unmodifiable list
     */
    List<SortPart> sortExpList();

    int rowCount();
}
