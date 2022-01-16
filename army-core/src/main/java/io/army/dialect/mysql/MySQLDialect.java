package io.army.dialect.mysql;

import io.army.DialectMode;
import io.army.dialect.DialectEnvironment;
import io.army.dialect._AbstractDialect;
import io.army.meta.ParamMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.SqlType;
import io.army.stmt.Stmt;
import io.army.util._Exceptions;

import java.util.Set;

abstract class MySQLDialect extends _AbstractDialect {

    MySQLDialect(DialectEnvironment environment) {
        super(environment);
    }


    @Override
    public void clearForDDL() {

    }


    @Override
    public boolean supportZone() {
        return false;
    }

    @Override
    public boolean supportOnlyDefault() {
        return false;
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
    public boolean setClauseTableAlias() {
        return false;
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
    protected Set<String> createKeyWordSet() {
        return null;
    }

    @Override
    protected String quoteIdentifier(String identifier) {
        return null;
    }

    @Override
    public final String literal(ParamMeta paramMeta, Object nonNull) {
        final SqlType sqlType;
        sqlType = paramMeta.mappingType().sqlType(this.environment.serverMeta());
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
            case TINYINT:
                literal = MySQLLiterals.tinyInt(sqlType, nonNull);
                break;
            case SMALLINT:
                literal = MySQLLiterals.smallInt(sqlType, nonNull);
                break;
            case FLOAT:
                literal = MySQLLiterals.floatLiteral(sqlType, nonNull);
                break;
            case DOUBLE:
                literal = MySQLLiterals.doubleLiteral(sqlType, nonNull);
                break;
            case CHAR:
            case VARCHAR:
            case VARBINARY:
            case BINARY:
            case TIMESTAMP:
            case BIT:
            case ENUM:
            case LONGTEXT:
            case BLOB:
            case JSON:
            case MEDIUMTEXT:
            case TEXT:
            case TINYTEXT:
            case SET:
            case DECIMAL_UNSIGNED:
            case INT_UNSIGNED:
            case MEDIUMINT_UNSIGNED:
            case MEDIUMINT:
            case SMALLINT_UNSIGNED:
            case TINYINT_UNSIGNED:
            case POINT:
            case POLYGON:
            case LONGBLOB:
            case TINYBLOB:
            case LINESTRING:
            case MEDIUMBLOB:
            case MULTIPOINT:
            case MULTIPOLYGON:
            case BIGINT_UNSIGNED:
            case MULTILINESTRING:
            case GEOMETRYCOLLECTION:
                break;
            default:
                throw _Exceptions.unexpectedEnum((MySqlType) sqlType);
        }
        return "";
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
