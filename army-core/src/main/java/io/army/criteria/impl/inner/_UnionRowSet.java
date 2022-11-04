package io.army.criteria.impl.inner;

import io.army.criteria.RowSet;
import io.army.criteria.SQLWords;

public interface _UnionRowSet extends _RowSet {

    RowSet leftRowSet();

    SQLWords unionType();

    RowSet rightRowSet();


}
