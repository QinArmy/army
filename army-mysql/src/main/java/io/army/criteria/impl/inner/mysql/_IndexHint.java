package io.army.criteria.impl.inner.mysql;

import io.army.criteria.SQLModifier;
import io.army.lang.Nullable;

import java.util.List;

public interface _IndexHint {

    SQLModifier command();

    @Nullable
    SQLModifier purpose();

    List<String> indexNameList();


}
