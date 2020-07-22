package io.army.criteria.impl.inner.mysql;

import io.army.criteria.impl.inner.DeveloperForbid;
import io.army.criteria.impl.inner.InnerDelete;
import io.army.criteria.impl.inner.InnerMultiDML;
import io.army.criteria.impl.inner.InnerSpecialDelete;

import java.util.List;

@DeveloperForbid
public interface InnerMySQLMultiDelete extends InnerMultiDML, InnerSpecialDelete {

    List<SimpleTableWrapper> targetTableList();
}
