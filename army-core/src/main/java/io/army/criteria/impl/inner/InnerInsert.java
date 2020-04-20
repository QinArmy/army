package io.army.criteria.impl.inner;

import io.army.criteria.Insert;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.Collection;
import java.util.List;

@DeveloperForbid
public interface InnerInsert {

    TableMeta<?> tableMeta();

    /**
     * @return a unmodifiable list , maybe empty.
     * @see Insert.InsertIntoAble#insertInto(TableMeta)
     * @see Insert.InsertIntoAble#insertInto(Collection)
     */
    List<FieldMeta<?, ?>> fieldList();

    void clear();

}
