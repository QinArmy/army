package io.army.criteria.impl.inner;

import io.army.criteria.NullHandleMode;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.Map;

public interface _Insert extends _Statement {

    TableMeta<?> table();

    /**
     * @return a unmodifiable list , maybe empty.
     */
    List<FieldMeta<?>> fieldList();

    List<FieldMeta<?>> childFieldList();

    Map<FieldMeta<?>, Boolean> fieldMap();


    interface _CommonExpInsert extends _Insert {

        boolean isMigration();

        NullHandleMode nullHandle();

        Map<FieldMeta<?>, _Expression> commonExpMap();

    }


}
