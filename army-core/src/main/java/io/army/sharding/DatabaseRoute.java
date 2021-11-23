package io.army.sharding;


import io.army.meta.FieldMeta;

public interface DatabaseRoute extends Route {

    boolean containsDatabase(int databaseIndex);

    byte database(FieldMeta<?, ?> fieldMeta, Object fieldValue);


}
