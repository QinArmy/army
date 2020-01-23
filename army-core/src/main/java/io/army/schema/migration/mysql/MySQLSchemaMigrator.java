package io.army.schema.migration.mysql;

import io.army.dialect.DataBase;
import io.army.schema.migration.SchemaMigrator;

public interface MySQLSchemaMigrator extends SchemaMigrator {

    static MySQLSchemaMigrator newInstance(DataBase dataBase) {
        MySQLSchemaMigrator schemaMigrator ;
        switch (dataBase) {
            case MySQL:
            case MySQL57:
                schemaMigrator = new MySQL57SchemaMigrator();
                break;
            case MySQL80:
                schemaMigrator = new MySQL80SchemaMigrator();
                break;
            default:
                throw new IllegalArgumentException(String.format("unsupported %s", dataBase));
        }
        return schemaMigrator;
    }

}
