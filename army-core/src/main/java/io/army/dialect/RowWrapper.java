package io.army.dialect;

import io.army.bean.ReadWrapper;
import io.army.meta.FieldMeta;

interface RowWrapper {

    void set(FieldMeta<?> field, Object value);

    boolean isNull(FieldMeta<?> field);


    ReadWrapper readonlyWrapper();


}
