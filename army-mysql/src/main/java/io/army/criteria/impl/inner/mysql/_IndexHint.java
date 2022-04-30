package io.army.criteria.impl.inner.mysql;

import io.army.criteria.SQLWords;
import io.army.lang.Nullable;

import java.util.List;

public interface _IndexHint {

    SQLWords command();

    @Nullable
    SQLWords purpose();

    List<String> indexNameList();


}
