package io.army.schema;

import io.army.dialect.Database;
import io.army.meta.FieldMeta;
import io.army.meta.SchemaMeta;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.SqlType;

final class MySQLComparer extends AbstractSchemaComparer {

    static MySQLComparer create(ServerMeta serverMeta) {
        return new MySQLComparer(serverMeta);
    }


    private MySQLComparer(ServerMeta serverMeta) {
        super(serverMeta);
        if (serverMeta.database() != Database.MySQL) {
            throw new IllegalArgumentException("serverMeta error.");
        }
    }

    @Override
    boolean compareSchema(_SchemaInfo schemaInfo, SchemaMeta schemaMeta) {
        String database, catalog, schema;
        database = schemaInfo.catalog();
        if (database == null) {
            database = schemaInfo.schema();
        }
        catalog = schemaMeta.catalog();
        schema = schemaMeta.schema();
        return (!catalog.isEmpty() && !catalog.equals(database))
                || (!schema.isEmpty() && !schema.equals(database));
    }

    @Override
    boolean compareSqlType(_ColumnInfo columnInfo, FieldMeta<?> field, SqlType sqlType) {
        final boolean match;
        switch ((MySQLTypes) sqlType) {
            case INT:
            case BIGINT:
            case DECIMAL:
            case DATETIME:
            case DATE:
            case TIME:
            case YEAR:
            case CHAR:
            case VARCHAR:
            case ENUM:
            case JSON:
            case SET:
            case TINYTEXT:
            case TEXT:
            case MEDIUMTEXT:
            case LONGTEXT:
            case BINARY:
            case VARBINARY:
            case TINYBLOB:
            case BLOB:
            case MEDIUMBLOB:
            case LONGBLOB:
            case BIT:
            case FLOAT:
            case DOUBLE:
            case TINYINT:
            case SMALLINT:
            case MEDIUMINT:
                match = sqlType.name().equals(columnInfo.typeName());
                break;
            case BOOLEAN: {
                final String typeName = columnInfo.typeName();
                if (sqlType.name().equals(typeName)) {
                    match = true;
                } else if (MySQLTypes.TINYINT.name().equals(typeName) || MySQLTypes.BIT.name().equals(typeName)) {
                    match = columnInfo.precision() == 1;
                } else {
                    match = false;
                }
            }
            break;
            case SMALLINT_UNSIGNED:
            case TINYINT_UNSIGNED:
            case MEDIUMINT_UNSIGNED:
            case INT_UNSIGNED:
            case BIGINT_UNSIGNED:
            case DECIMAL_UNSIGNED:
                match = sqlType.name().replace('_', ' ').equals(columnInfo.typeName());
                break;
            case POINT:
            case LINESTRING:
            case POLYGON:
            case MULTIPOINT:
            case MULTIPOLYGON:
            case MULTILINESTRING:
            case GEOMETRYCOLLECTION: {
                final String typeName;
                typeName = columnInfo.typeName();
                match = "GEOMETRY".equals(typeName) // JDBC
                        || sqlType.name().equals(typeName); //JDBD
            }
            break;
            default:
                match = true; // default match.
        }
        return !match;
    }

    @Override
    boolean compareDefault(_ColumnInfo columnInfo, FieldMeta<?> field, SqlType sqlType) {
//        switch ((MySqlType) sqlType) {
//            case INT:
//            case BIGINT:
//            case DECIMAL:
//            case BOOLEAN:
//            case DATETIME:
//            case DATE:
//            case TIME:
//            case YEAR:
//
//            case CHAR:
//            case VARCHAR:
//            case ENUM:
//            case JSON:
//            case SET:
//            case TINYTEXT:
//            case TEXT:
//            case MEDIUMTEXT:
//            case LONGTEXT:
//
//            case BINARY:
//            case VARBINARY:
//            case TINYBLOB:
//            case BLOB:
//            case MEDIUMBLOB:
//            case LONGBLOB:
//
//            case BIT:
//            case FLOAT:
//            case DOUBLE:
//
//            case TINYINT:
//            case TINYINT_UNSIGNED:
//            case SMALLINT:
//            case SMALLINT_UNSIGNED:
//            case MEDIUMINT:
//            case MEDIUMINT_UNSIGNED:
//            case INT_UNSIGNED:
//            case BIGINT_UNSIGNED:
//            case DECIMAL_UNSIGNED:
//
//            case POINT:
//            case LINESTRING:
//            case POLYGON:
//            case MULTIPOINT:
//            case MULTIPOLYGON:
//            case MULTILINESTRING:
//            case GEOMETRYCOLLECTION:
//                break;
//            default:
//        } //TODO
        return false;
    }

    @Override
    boolean supportColumnComment() {
        return true;
    }

    @Override
    boolean supportTableComment() {
        return true;
    }

    @Override
    String primaryKeyName() {
        return "PRIMARY";
    }


}
