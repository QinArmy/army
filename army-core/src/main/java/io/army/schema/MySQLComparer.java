package io.army.schema;

import io.army.dialect.Database;
import io.army.meta.FieldMeta;
import io.army.meta.SchemaMeta;
import io.army.meta.ServerMeta;
import io.army.meta.TableMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;

import java.util.Locale;

final class MySQLComparer extends ArmySchemaComparer {

    static MySQLComparer create(ServerMeta serverMeta) {
        return new MySQLComparer(serverMeta);
    }

    private MySQLComparer(ServerMeta serverMeta) {
        super(serverMeta);
        if (serverMeta.serverDatabase() != Database.MySQL) {
            throw new IllegalArgumentException("serverMeta error.");
        }
    }

    @Override
    boolean compareSchema(SchemaInfo schemaInfo, SchemaMeta schemaMeta) {
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
    boolean compareSqlType(final ColumnInfo columnInfo, final FieldMeta<?> field, final DataType dataType) {
        final String typeName;
        typeName = columnInfo.typeName().toUpperCase(Locale.ROOT);

        if (!(dataType instanceof MySQLType)) {
            return true;
        }

        final boolean match;
        switch ((MySQLType) dataType) {
            case BOOLEAN: {
                switch (typeName) {
                    case "BOOLEAN":
                    case "TINYINT":
                        match = true;
                        break;
                    default:
                        match = false;
                }
            }
            break;
            case INT: {
                switch (typeName) {
                    case "INT":
                    case "INTEGER":
                        match = true;
                        break;
                    default:
                        match = false;
                }
            }
            break;
            case INT_UNSIGNED: {
                switch (typeName) {
                    case "INT UNSIGNED":
                    case "INTEGER UNSIGNED":
                        match = true;
                        break;
                    default:
                        match = false;
                }
            }
            break;
            case GEOMETRYCOLLECTION: {
                switch (typeName) {
                    case "GEOMCOLLECTION":
                    case "GEOMETRYCOLLECTION":
                        match = true;
                        break;
                    default:
                        match = false;
                }
            }
            break;
            default:
                match = typeName.equals(dataType.typeName());
        }
        return !match;
    }

    @Override
    boolean compareDefault(ColumnInfo columnInfo, FieldMeta<?> field, DataType sqlType) {
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
    String primaryKeyName(TableMeta<?> table) {
        return "PRIMARY";
    }


}
