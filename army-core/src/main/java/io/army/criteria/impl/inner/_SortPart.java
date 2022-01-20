package io.army.criteria.impl.inner;

import io.army.criteria.SortPart;
import io.army.dialect._SqlContext;

@Deprecated
public interface _SortPart extends SortPart {


    default void appendSortPart(_SqlContext context) {
        throw new UnsupportedOperationException();
    }
}
