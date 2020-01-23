package io.army.schema.migration;

import io.army.criteria.MetaException;
import io.army.dialect.DataBase;
import io.army.dialect.Dialect;
import io.army.dialect.TableDDL;
import io.army.meta.TableMeta;
import io.army.schema.SchemaInfoException;
import io.army.schema.migration.mysql.MySQLSchemaMigrator;
import io.army.schema.migration.mysql.MySQLTableDDL;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;
import java.util.Map;

class Meta2SchemaImpl implements Meta2Schema {

    @Override
    public Map<TableMeta<?>, List<String>> migrate(Collection<TableMeta<?>> tableMetas, Connection connection
            , Dialect dialect) throws SchemaInfoException, MetaException {
        List<Migration> migrationList;

        migrationList =  createSchemaMigrator(dialect.database())
                .migrate(tableMetas,connection,dialect);



        return null;
    }

    private TableDDL createTableDDL(DataBase dataBase){
        TableDDL tableDDL;
        switch (dataBase) {
            case MySQL:
            case MySQL57:
            case MySQL80:
                tableDDL = MySQLTableDDL.newInstance(dataBase);
                break;
            case SQL_Server:
            case OceanBase:
            case Postgre:
            case Oracle:
            case Db2:
            default:
                throw new IllegalArgumentException(String.format("unsupported database %s", dataBase));
        }
        return tableDDL;
    }


    private SchemaMigrator createSchemaMigrator(DataBase dataBase) {
        SchemaMigrator migrator;
        switch (dataBase) {
            case MySQL:
            case MySQL57:
            case MySQL80:
                migrator = MySQLSchemaMigrator.newInstance(dataBase);
                break;
            case SQL_Server:
            case OceanBase:
            case Postgre:
            case Oracle:
            case Db2:
            default:
                throw new IllegalArgumentException(String.format("unsupported database %s", dataBase));
        }
        return migrator;
    }


}
