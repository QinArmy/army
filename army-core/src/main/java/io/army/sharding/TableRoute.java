package io.army.sharding;


import io.army.meta.FieldMeta;

public interface TableRoute extends Route {

    boolean containTable(int tableIndex);

    @Deprecated
    byte table(FieldMeta<?> fieldMeta, Object fieldValue);

    byte table(Object fieldValue);

}
