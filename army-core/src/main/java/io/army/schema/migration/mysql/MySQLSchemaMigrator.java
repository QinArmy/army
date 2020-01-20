package io.army.schema.migration.mysql;

import io.army.ErrorCode;
import io.army.schema.SchemaInfoException;
import io.army.schema.migration.SchemaMigrator;

public interface MySQLSchemaMigrator extends SchemaMigrator {

    static SchemaMigrator newInstance(String version){
        String[] versionArray = version.split(".");
        SchemaMigrator migrator ;
        if(versionArray[0].equals("8")){
            migrator = new MySQL80SchemaMigrator();
        }else {
            throw new SchemaInfoException(ErrorCode.NONE,"not support MySQL version[%s].",version);
        }
        return migrator;
    }

}
