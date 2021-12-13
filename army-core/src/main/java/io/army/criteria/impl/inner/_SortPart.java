package io.army.criteria.impl.inner;

import io.army.criteria.SortPart;
import io.army.dialect._SqlContext;

public interface _SortPart extends SortPart {

    void appendSortPart(_SqlContext context);
}
