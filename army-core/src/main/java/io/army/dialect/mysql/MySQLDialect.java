package io.army.dialect.mysql;

import io.army.DialectMode;
import io.army.dialect.DialectEnvironment;
import io.army.dialect._AbstractDialect;
import io.army.mapping.MappingType;
import io.army.mapping.mysql.MySqlSetType;
import io.army.meta.ParamMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.SqlType;
import io.army.stmt.Stmt;
import io.army.util._Exceptions;

abstract class MySQLDialect extends _AbstractDialect {

    protected static final String NO_BACKSLASH_ESCAPES = "NO_BACKSLASH_ESCAPES";

    MySQLDialect(DialectEnvironment environment) {
        super(environment);
    }


    @Override
    public final String safeTableName(String tableName) {
        return this.quoteIfNeed(tableName);
    }

    @Override
    public final String safeColumnName(String columnName) {
        return this.quoteIfNeed(columnName);
    }

    @Override
    public void clearForDDL() {

    }


    @Override
    public final boolean supportInsertReturning() {
        // MySQL don't support insert returning.
        return false;
    }

    @Override
    public final boolean supportZone() {
        // MySQL don't support zone.
        return false;
    }

    @Override
    public final boolean supportOnlyDefault() {
        // MySQL support DEFAULT() function.
        return true;
    }


    @Override
    public boolean tableAliasAfterAs() {
        return false;
    }

    @Override
    public boolean singleDeleteHasTableAlias() {
        return false;
    }

    @Override
    public boolean hasRowKeywords() {
        return false;
    }


    @Override
    public String showSQL(Stmt stmt) {
        return null;
    }

    @Override
    public boolean supportSavePoint() {
        return false;
    }

    @Override
    public final boolean setClauseTableAlias() {
        // MySQL support table alias in set clause.
        return true;
    }

    @Override
    public DialectMode mode() {
        return null;
    }

    @Override
    public String defaultFuncName() {
        return null;
    }

    @Override
    public boolean multiTableUpdateChild() {
        return false;
    }


    @Override
    protected final String quoteIdentifier(String identifier) {
        return '`' + identifier + '`';
    }

    @Override
    public final String literal(ParamMeta paramMeta, Object nonNull) {
        final SqlType sqlType;
        final MappingType mappingType;
        if (paramMeta instanceof MappingType) {
            mappingType = (MappingType) paramMeta;
        } else {
            mappingType = paramMeta.mappingType();
        }
        sqlType = mappingType.sqlType(this.environment.serverMeta());
        final String literal;
        switch ((MySqlType) sqlType) {
            case INT:
                literal = MySQLLiterals.integer(sqlType, nonNull);
                break;
            case BIGINT:
                literal = MySQLLiterals.bigInt(sqlType, nonNull);
                break;
            case DECIMAL:
                literal = MySQLLiterals.decimal(sqlType, nonNull);
                break;
            case BOOLEAN:
                literal = MySQLLiterals.booleanLiteral(sqlType, nonNull);
                break;
            case DATETIME:
                literal = MySQLLiterals.datetime(sqlType, paramMeta, nonNull);
                break;
            case DATE:
                literal = MySQLLiterals.date(sqlType, nonNull);
                break;
            case TIME:
                literal = MySQLLiterals.time(sqlType, paramMeta, nonNull);
                break;
            case YEAR:
                literal = MySQLLiterals.year(sqlType, nonNull);
                break;
            case CHAR:
            case VARCHAR:
            case TINYTEXT:
            case TEXT:
            case MEDIUMTEXT:
            case LONGTEXT: {
                final boolean hexEscapes;
                hexEscapes = this.environment.serverMeta().sessionVar(NO_BACKSLASH_ESCAPES) != null;
                literal = MySQLLiterals.text(sqlType, hexEscapes, nonNull);
            }
            break;
            case JSON: {
                final String json;
                json = this.environment.jsonCodec().encode(nonNull);
                final boolean hexEscapes;
                hexEscapes = this.environment.serverMeta().sessionVar(NO_BACKSLASH_ESCAPES) != null;
                literal = MySQLLiterals.text(sqlType, hexEscapes, json);
            }
            break;
            case BINARY:
            case VARBINARY:
            case TINYBLOB:
            case BLOB:
            case MEDIUMBLOB:
            case LONGBLOB:
                literal = MySQLLiterals.binary(sqlType, nonNull);
                break;
            case ENUM:
                literal = MySQLLiterals.enumLiteral(sqlType, nonNull);
                break;
            case BIT:
                literal = MySQLLiterals.bit(sqlType, nonNull);
                break;
            case DOUBLE:
                literal = MySQLLiterals.doubleLiteral(sqlType, nonNull);
                break;
            case FLOAT:
                literal = MySQLLiterals.floatLiteral(sqlType, nonNull);
                break;
            case TINYINT:
                literal = MySQLLiterals.tinyInt(sqlType, nonNull);
                break;
            case SMALLINT:
                literal = MySQLLiterals.smallInt(sqlType, nonNull);
                break;
            case MEDIUMINT:
                literal = MySQLLiterals.mediumInt(sqlType, nonNull);
                break;
            case BIGINT_UNSIGNED:
                literal = MySQLLiterals.unsignedLBigInt(sqlType, nonNull);
                break;
            case DECIMAL_UNSIGNED:
                literal = MySQLLiterals.unsignedDecimal(sqlType, nonNull);
                break;
            case INT_UNSIGNED:
                literal = MySQLLiterals.unsignedInt(sqlType, nonNull);
                break;
            case MEDIUMINT_UNSIGNED:
                literal = MySQLLiterals.unsignedMediumInt(sqlType, nonNull);
                break;
            case SMALLINT_UNSIGNED:
                literal = MySQLLiterals.unsignedSmallInt(sqlType, nonNull);
                break;
            case TINYINT_UNSIGNED:
                literal = MySQLLiterals.unsignedTinyInt(sqlType, nonNull);
                break;
            case SET: {
                try {
                    literal = ((MySqlSetType) mappingType).literal(nonNull);
                } catch (RuntimeException e) {
                    throw _Exceptions.errorLiteralType(sqlType, nonNull);
                }
            }
            break;
            case POINT:
            case LINESTRING:
            case POLYGON:
            case MULTIPOINT:
            case MULTIPOLYGON:
            case MULTILINESTRING:
            case GEOMETRYCOLLECTION:
                throw _Exceptions.errorLiteralType(sqlType, nonNull);
            default:
                throw _Exceptions.unexpectedEnum((MySqlType) sqlType);
        }
        return literal;
    }


    /*################################## blow private method ##################################*/

//    private static void re(){
//        switch ((MySqlType) sqlType) {
//            case INT:
//            case BOOLEAN:
//            case DATE:
//            case BLOB:
//            case TINYINT:
//            case SMALLINT:
//            case BIGINT:
//            case DECIMAL:
//            case FLOAT:
//            case DOUBLE:
//            case CHAR:
//            case VARCHAR:
//            case VARBINARY:
//            case BINARY:
//            case TIMESTAMP:
//            case BIT:
//            case TIME:
//            case ENUM:
//            case LONGTEXT:
//            case DATETIME:
//            case YEAR:
//            case JSON:
//            case MEDIUMTEXT:
//            case TEXT:
//            case TINYTEXT:
//            case SET:
//            case DECIMAL_UNSIGNED:
//
//            case INT_UNSIGNED:
//            case MEDIUMINT_UNSIGNED:
//            case MEDIUMINT:
//            case SMALLINT_UNSIGNED:
//            case TINYINT_UNSIGNED:
//            case POINT:
//            case POLYGON:
//            case LONGBLOB:
//            case TINYBLOB:
//            case LINESTRING:
//            case MEDIUMBLOB:
//            case MULTIPOINT:
//            case MULTIPOLYGON:
//            case BIGINT_UNSIGNED:
//            case MULTILINESTRING:
//            case GEOMETRYCOLLECTION:
//                break;
//            default:
//                throw _Exceptions.unexpectedEnum((MySqlType) sqlType);
//        }
//    }


}
