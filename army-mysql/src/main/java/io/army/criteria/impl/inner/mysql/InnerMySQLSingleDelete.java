package io.army.criteria.impl.inner.mysql;

import io.army.criteria.SortPart;
import io.army.criteria.impl.inner.DeveloperForbid;
import io.army.criteria.impl.inner.InnerDelete;
import io.army.criteria.impl.inner.InnerSingleTableSQL;

import java.util.List;

@DeveloperForbid
public interface InnerMySQLSingleDelete extends InnerDelete, InnerSingleTableSQL {

    /**
     * @return a unmodifiable list
     */
    List<SortPart> sortExpList();

    int rowCount();
}
