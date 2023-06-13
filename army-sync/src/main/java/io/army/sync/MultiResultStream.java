package io.army.sync;

import io.army.session.DataAccessException;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface MultiResultStream extends AutoCloseable {


    MultiResult.Type hasMore();

    long nextUpdate() throws DataAccessException;

    <R> Stream<R> nextQueryStream(Class<R> resultClass) throws DataAccessException;

    <R> Stream<R> nextQueryStream(Class<R> resultClass, Supplier<List<R>> listConstructor) throws DataAccessException;

    Stream<Map<String, Object>> nextQueryMapStream() throws DataAccessException;

    Stream<Map<String, Object>> nextQueryMapStream(Supplier<Map<String, Object>> mapConstructor)
            throws DataAccessException;

    Stream<Map<String, Object>> nextQueryMapStream(Supplier<Map<String, Object>> mapConstructor,
                                                   Supplier<List<Map<String, Object>>> listConstructor)
            throws DataAccessException;


    @Override
    void close() throws DataAccessException;


}
