package io.army.sharding;


import io.army.meta.FieldMeta;

public interface DatabaseRoute extends Route {

    boolean containsDatabase(int databaseIndex);

    @Deprecated
    byte database(FieldMeta<?, ?> fieldMeta, Object fieldValue);

    byte database(Object fieldValue);


}
