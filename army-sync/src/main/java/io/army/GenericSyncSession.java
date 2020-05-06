package io.army;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.Pair;
import io.army.util.Triple;

import java.util.List;

public interface GenericSyncSession extends GenericSession {

    SessionFactory sessionFactory();

    <T extends IDomain> void save(T domain);

    @Nullable
    <T extends IDomain> T get(TableMeta<T> tableMeta, Object id);

    @Nullable
    <T extends IDomain> T get(TableMeta<T> tableMeta, Object id, Visible visible);

    @Nullable
    <T extends IDomain> T getByUnique(TableMeta<T> tableMeta, List<String> propNameList, List<Object> valueList);

    @Nullable
    <T extends IDomain> T getByUnique(TableMeta<T> tableMeta, List<String> propNameList
            , List<Object> valueList, Visible visible);

    @Nullable
    <T> T selectOne(Select select, Class<T> resultClass);

    @Nullable
    <T> T selectOne(Select select, Class<T> resultClass, Visible visible);

    <T> List<T> select(Select select, Class<T> resultClass);

    <T> List<T> select(Select select, Class<T> resultClass, Visible visible);

    @Nullable
    <F, S> Pair<F, S> selectOnePair(Select select);

    @Nullable
    <F, S> Pair<F, S> selectOnePair(Select select, Visible visible);

    <F, S> List<Pair<F, S>> selectPair(Select select);

    <F, S> List<Pair<F, S>> selectPair(Select select, Visible visible);

    @Nullable
    <F, S, T> Triple<F, S, T> selectOneTriple(Select select);

    @Nullable
    <F, S, T> Triple<F, S, T> selectOneTriple(Select select, Visible visible);

    <F, S, T> List<Triple<F, S, T>> selectTriple(Select select);

    <F, S, T> List<Triple<F, S, T>> selectTriple(Select select, Visible visible);


    /**
     * @param update will start singleUpdate dml instance.
     * @return a unmodifiable list, at most two element.
     */
    List<Integer> update(Update update);

    List<Integer> update(Update update, Visible visible);

    void insert(Insert insert);

    void insert(Insert insert, Visible visible);

    void delete(Delete delete);

    void delete(Delete delete, Visible visible);

}
