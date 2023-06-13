package io.army.sync;

import io.army.session.DataAccessException;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface MultiResult extends AutoCloseable {

    Type hasMore();

    long nextUpdate() throws DataAccessException;

    <R> List<R> nextQuery(Class<R> resultClass) throws DataAccessException;

    <R> List<R> nextQuery(Class<R> resultClass, Supplier<List<R>> listConstructor) throws DataAccessException;

    List<Map<String, Object>> nextQueryAsMap() throws DataAccessException;

    List<Map<String, Object>> nextQueryAsMap(Supplier<Map<String, Object>> mapConstructor)
            throws DataAccessException;

    List<Map<String, Object>> nextQueryAsMap(Supplier<Map<String, Object>> mapConstructor,
                                             Supplier<List<Map<String, Object>>> listConstructor)
            throws DataAccessException;


    @Override
    void close() throws DataAccessException;

    enum Type {

        NONE,
        UPDATE,
        QUERY
    }


}
