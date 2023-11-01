package io.army.schema;

import io.army.dialect._Constant;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.SchemaMeta;
import io.army.meta.ServerMeta;
import io.army.meta.TableMeta;
import io.army.sqltype.PostgreSqlType;
import io.army.sqltype.SQLType;
import io.army.util._Exceptions;

import java.util.Locale;

final class PostgreComparer extends ArmySchemaComparer {

    static PostgreComparer create(ServerMeta serverMeta) {
        return new PostgreComparer(serverMeta);
    }


    private PostgreComparer(ServerMeta serverMeta) {
        super(serverMeta);
    }


    @Override
    boolean compareSchema(SchemaInfo schemaInfo, SchemaMeta schemaMeta) {
        final String serverDatabase, serverSchema, catalog, schema;
        serverDatabase = schemaInfo.catalog();
        serverSchema = schemaInfo.schema();
        catalog = schemaMeta.catalog();
        schema = schemaMeta.schema();
        return !((catalog.isEmpty() || catalog.equals(serverDatabase)) && (schema.isEmpty() || schema.equals(serverSchema)));
    }

    @Override
    boolean compareSqlType(final _ColumnInfo columnInfo, final FieldMeta<?> field, final SQLType sqlType) {
        final String typeName;
        typeName = columnInfo.typeName().toLowerCase(Locale.ROOT);
        final boolean notMatch;
        switch ((PostgreSqlType) sqlType) {
            case BOOLEAN:
                switch (typeName) {
                    case "boolean":
                    case "bool":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case SMALLINT:
                switch (typeName) {
                    case "int2":
                    case "smallint":
                    case "smallserial":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case INTEGER:
            case NO_CAST_INTEGER:
                switch (typeName) {
                    case "int":
                    case "int4":
                    case "serial":
                    case "integer":
                    case "xid":  // https://www.postgresql.org/docs/current/datatype-oid.html
                    case "cid":  // https://www.postgresql.org/docs/current/datatype-oid.html
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case BIGINT:
                switch (typeName) {
                    case "int8":
                    case "bigint":
                    case "bigserial":
                    case "serial8":
                    case "xid8":  // https://www.postgresql.org/docs/current/datatype-oid.html  TODO what's tid ?
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case DECIMAL:
                switch (typeName) {
                    case "numeric":
                    case "decimal":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case FLOAT8:
                switch (typeName) {
                    case "float8":
                    case "double precision":
                    case "float":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case REAL:
                switch (typeName) {
                    case "float4":
                    case "real":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case TIME:
                switch (typeName) {
                    case "time":
                    case "time without time zone":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case TIMETZ:
                switch (typeName) {
                    case "timetz":
                    case "time with time zone":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case TIMESTAMP:
                switch (typeName) {
                    case "timestamp":
                    case "timestamp without time zone":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case TIMESTAMPTZ:
                switch (typeName) {
                    case "timestamptz":
                    case "timestamp with time zone":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case CHAR:
                switch (typeName) {
                    case "char":
                    case "character":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case VARCHAR:
                switch (typeName) {
                    case "varchar":
                    case "character varying":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case TEXT:
            case NO_CAST_TEXT:
                switch (typeName) {
                    case "text":
                    case "txid_snapshot":  // TODO txid_snapshot is text?
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case VARBIT:
                switch (typeName) {
                    case "bit varying":
                    case "varbit":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case DATE:
            case INTERVAL:

            case BIT:
            case BYTEA:
            case JSON:
            case JSONB:
            case JSONPATH:
            case XML:

            case UUID:
            case MONEY:

            case CIDR:
            case INET:
            case MACADDR:
            case MACADDR8:

            case POINT:
            case LINE:
            case PATH:
            case BOX:
            case LSEG:
            case CIRCLE:
            case POLYGON:

            case TSQUERY:
            case TSVECTOR:

            case INT4RANGE:
            case INT8RANGE:
            case NUMRANGE:
            case DATERANGE:
            case TSRANGE:
            case TSTZRANGE:

            case INT4MULTIRANGE:
            case INT8MULTIRANGE:
            case NUMMULTIRANGE:
            case DATEMULTIRANGE:
            case TSMULTIRANGE:
            case TSTZMULTIRANGE:

            case PG_SNAPSHOT:
            case PG_LSN:
            case ACLITEM:

                notMatch = !typeName.equals(sqlType.name().toLowerCase(Locale.ROOT));
                break;
            case USER_DEFINED: {
                final MappingType mappingType = field.mappingType();
                if (!(mappingType instanceof MappingType.SqlUserDefinedType)) {
                    throw _Exceptions.notUserDefinedType(mappingType, sqlType);
                }
                final String userTypeName;
                userTypeName = ((MappingType.SqlUserDefinedType) mappingType).sqlTypeName(this.serverMeta)
                        .toLowerCase(Locale.ROOT);
                notMatch = !typeName.equals(userTypeName);
            }
            break;
            case BOOLEAN_ARRAY:
                switch (typeName) {
                    case "boolean[]":
                    case "bool[]":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case SMALLINT_ARRAY:
                switch (typeName) {
                    case "int2[]":
                    case "smallint[]":
                    case "smallserial[]":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case INTEGER_ARRAY:
                switch (typeName) {
                    case "int[]":
                    case "int4[]":
                    case "integer[]":
                    case "serial[]":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case BIGINT_ARRAY:
                switch (typeName) {
                    case "int8[]":
                    case "bigint[]":
                    case "serial8[]":
                    case "bigserial[]":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case DECIMAL_ARRAY:
                switch (typeName) {
                    case "numeric[]":
                    case "decimal[]":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case FLOAT8_ARRAY:
                switch (typeName) {
                    case "float8[]":
                    case "float[]":
                    case "double precision[]":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case REAL_ARRAY:
                switch (typeName) {
                    case "float4[]":
                    case "real[]":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case CHAR_ARRAY:
                switch (typeName) {
                    case "char[]":
                    case "character[]":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case VARCHAR_ARRAY:
                switch (typeName) {
                    case "varchar[]":
                    case "character varying[]":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case TEXT_ARRAY:
                switch (typeName) {
                    case "text[]":
                    case "txid_snapshot[]":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case TIME_ARRAY:
                switch (typeName) {
                    case "time[]":
                    case "time without time zone[]":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case TIMETZ_ARRAY:
                switch (typeName) {
                    case "timetz[]":
                    case "time with time zone[]":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case TIMESTAMP_ARRAY:
                switch (typeName) {
                    case "timestamp[]":
                    case "timestamp without time zone[]":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case TIMESTAMPTZ_ARRAY:
                switch (typeName) {
                    case "timestamptz[]":
                    case "timestamp with time zone[]":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case VARBIT_ARRAY:
                switch (typeName) {
                    case "varbit[]":
                    case "bit varying[]":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case BIT_ARRAY:

            case UUID_ARRAY:
            case BYTEA_ARRAY:
            case MONEY_ARRAY:


            case DATE_ARRAY:
            case INTERVAL_ARRAY:

            case JSON_ARRAY:
            case JSONB_ARRAY:
            case JSONPATH_ARRAY:
            case XML_ARRAY:

            case LINE_ARRAY:
            case PATH_ARRAY:

            case CIDR_ARRAY:
            case INET_ARRAY:
            case MACADDR_ARRAY:
            case MACADDR8_ARRAY:

            case TSQUERY_ARRAY:
            case TSVECTOR_ARRAY:

            case INT4RANGE_ARRAY:
            case INT8RANGE_ARRAY:
            case NUMRANGE_ARRAY:
            case DATERANGE_ARRAY:
            case TSRANGE_ARRAY:
            case TSTZRANGE_ARRAY:

            case POINT_ARRAY:
            case BOX_ARRAY:
            case POLYGON_ARRAY:
            case LSEG_ARRAY:
            case CIRCLE_ARRAY:

            case PG_SNAPSHOT_ARRAY:
            case PG_LSN_ARRAY:
            case ACLITEM_ARRAY: {
                final String name = sqlType.name(), arrayTypeName;
                arrayTypeName = name.substring(0, name.lastIndexOf(_Constant.UNDERSCORE_ARRAY))
                        .toLowerCase(Locale.ROOT) + "[]";
                notMatch = !typeName.equals(arrayTypeName);
            }
            break;
            case USER_DEFINED_ARRAY: {
                final MappingType mappingType = field.mappingType();
                if (!(mappingType instanceof MappingType.SqlUserDefinedType)) {
                    throw _Exceptions.notUserDefinedType(mappingType, sqlType);
                }
                final String userTypeName;
                userTypeName = ((MappingType.SqlUserDefinedType) mappingType).sqlTypeName(this.serverMeta)
                        .toLowerCase(Locale.ROOT) + "[]";
                notMatch = !typeName.equals(userTypeName);
            }
            break;
            case REF_CURSOR:
            case UNKNOWN:
            default:
                throw _Exceptions.unexpectedEnum((Enum<?>) sqlType);
        }
        return notMatch;
    }

    @Override
    boolean compareDefault(_ColumnInfo columnInfo, FieldMeta<?> field, SQLType sqlType) {
        //currently, false
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
        //eg: china_region_pkey
        return table.tableName() + "_pkey";
    }


}
