package io.army.dialect;

import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;

public interface _InsertBlock {

    default _SqlContext getContext() {
        throw new UnsupportedOperationException();
    }

    TableMeta<?> table();

    List<FieldMeta<?, ?>> fieldLis();


}
