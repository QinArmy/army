package io.army.proxy;


import javax.annotation.Nullable;

import io.army.meta.TableMeta;
import io.army.meta.UniqueFieldMeta;

import java.util.List;

public interface _SessionCache {

    @Nullable
    <T> T get(TableMeta<T> table, Object id);

    @Nullable
    <T> T get(TableMeta<T> table, UniqueFieldMeta<? super T> field, Object fieldValue);

    <T> T putIfAbsent(TableMeta<T> table, T domain);


    List<_CacheBlock> getChangedList();

    void clearChangedOnRollback();


    void clearOnSessionCLose();

}
