package io.army.criteria.impl.inner;

import io.army.criteria.RowSet;
import io.army.criteria.impl._UnionType;

public interface _UnionRowSet extends _RowSet {

    RowSet leftRowSet();

    _UnionType unionType();

    RowSet rightRowSet();


}
