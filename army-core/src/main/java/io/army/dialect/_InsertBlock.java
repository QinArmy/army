package io.army.dialect;

import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;

public interface _InsertBlock {

    _SqlContext getContext();

    TableMeta<?> table();

    List<FieldMeta<?, ?>> fieldLis();


}
