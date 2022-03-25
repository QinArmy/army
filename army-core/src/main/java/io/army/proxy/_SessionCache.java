package io.army.proxy;

import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.meta.UniqueFieldMeta;

import java.util.List;

public interface _SessionCache {

    @Nullable
    <T extends IDomain> T get(TableMeta<T> table, Object id);

    @Nullable
    <T extends IDomain> T get(TableMeta<T> table, UniqueFieldMeta<? super T> field, Object fieldValue);

    <T extends IDomain> T putIfAbsent(TableMeta<T> table, T domain);


    List<_CacheBlock> getChangedList();

    void clearChangedOnRollback();


    void clearOnSessionCLose();

}
