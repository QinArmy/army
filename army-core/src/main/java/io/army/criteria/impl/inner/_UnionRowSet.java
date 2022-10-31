package io.army.criteria.impl.inner;

import io.army.criteria.RowSet;
import io.army.criteria.SQLWords;

public interface _UnionRowSet {

    RowSet leftRowSet();

    SQLWords unionType();

    RowSet rightRowSet();


}
