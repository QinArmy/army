package io.army.criteria.impl.inner;

import java.util.List;

@DeveloperForbid
public interface InnerSQL {

    List<TableWrapper> tableWrapperList();

    void clear();
}
