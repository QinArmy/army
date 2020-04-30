package io.army;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.util.Pair;
import io.army.util.Triple;

import java.util.List;

public interface GenericSyncSession extends GenericSession {


    void save(IDomain entity);

    <T extends IDomain> T get(TableMeta<T> tableMeta, Object id);

    <T extends IDomain> T get(TableMeta<T> tableMeta, Object id, Visible visible);

    <T extends IDomain> T getByUnique(TableMeta<T> tableMeta, List<String> propNameList, List<Object> valueList);

    <T extends IDomain> T getByUnique(TableMeta<T> tableMeta, List<String> propNameList
            , List<Object> valueList, Visible visible);

    <T> T selectOne(Select select, Class<T> resultClass);

    <T> T selectOne(Select select, Class<T> resultClass, Visible visible);

    <T> List<T> select(Select select, Class<T> resultClass);

    <T> List<T> select(Select select, Class<T> resultClass, Visible visible);

    <F, S> List<Pair<F, S>> selectPair(Select select, Class<F> firstClass, Class<S> secondClass);

    <F, S> List<Pair<F, S>> selectPair(Select select, Class<F> firstClass, Class<S> secondClass, Visible visible);

    <F, S, T> List<Triple<F, S, T>> selectTriple(Select select, Class<F> firstClass, Class<S> secondClass
            , Class<T> thirdClass);

    <F, S, T> List<Triple<F, S, T>> selectTriple(Select select, Class<F> firstClass, Class<S> secondClass
            , Class<T> thirdClass, Visible visible);

    /**
     * @param update will execute singleUpdate dml instance.
     * @return a unmodifiable list, at most two element.
     */
    List<Integer> update(Update update);

    List<Integer> update(Update update, Visible visible);

    void insert(Insert insert);

    void insert(Insert insert, Visible visible);

    void delete(Delete delete);

    void delete(Delete delete, Visible visible);

}
