package io.army.dialect;

import io.army.criteria.*;

import java.util.List;

public interface DQL extends SQL {

    default List<SQLWrapper> select(Select select, Visible visible) {
        throw new UnsupportedOperationException();
    }

    default void select(Select select, SQLContext context) {
        throw new UnsupportedOperationException();
    }

    default void partQuery(QueryAfterSet select, SQLContext context) {
        throw new UnsupportedOperationException();
    }

    default void subQuery(SubQuery subQuery, SQLContext context) {
        throw new UnsupportedOperationException();
    }
}
