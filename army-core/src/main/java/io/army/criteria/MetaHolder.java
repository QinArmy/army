package io.army.criteria;

import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import org.springframework.lang.NonNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * created  on 2019-01-30.
 */
public abstract class MetaHolder {

    private static final ConcurrentMap<Class<? extends IDomain>, TableMeta<?>> TABLE_META_HOLDER
            = new ConcurrentHashMap<>();

    @NonNull
    public static <T extends IDomain> TableMeta<T> getTable(@NonNull Class<T> entityClass) {
        @SuppressWarnings("unchecked")
        TableMeta<T> table = (TableMeta<T>) TABLE_META_HOLDER.get(entityClass);
        Assert.assertNotNull(table, "");
        return table;
    }


}
