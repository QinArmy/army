package io.army.criteria.impl.inner.mysql;

import io.army.criteria.SortPart;
import io.army.criteria.impl.inner.*;

import java.util.List;

@DeveloperForbid
public interface InnerMySQLSingleDelete extends InnerSQL, InnerDelete {

    /**
     * @return a unmodifiable list
     */
    List<SortPart> sortExpList();

    int rowCount();
}
