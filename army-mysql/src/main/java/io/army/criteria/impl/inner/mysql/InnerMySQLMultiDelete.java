package io.army.criteria.impl.inner.mysql;

import io.army.criteria.impl.inner.DeveloperForbid;
import io.army.criteria.impl.inner.InnerDelete;
import io.army.criteria.impl.inner.InnerMultiTableSQL;

import java.util.List;

@DeveloperForbid
public interface InnerMySQLMultiDelete extends InnerDelete, InnerMultiTableSQL {

    List<SimpleTableWrapper> targetTableList();
}
