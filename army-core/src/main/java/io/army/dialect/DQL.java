package io.army.dialect;

import io.army.criteria.QueryAfterSet;
import io.army.criteria.SQLContext;
import io.army.criteria.Select;
import io.army.criteria.SubQuery;

import java.util.List;

public interface DQL extends SQL {

    default List<SQLWrapper> select(Select select) {
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
