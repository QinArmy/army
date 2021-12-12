package io.army.criteria.impl.inner;

import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;

public interface _Insert extends _Statement {

    TableMeta<?> tableMeta();

    /**
     * @return a unmodifiable list , maybe empty.
     */
    List<FieldMeta<?, ?>> fieldList();

}
