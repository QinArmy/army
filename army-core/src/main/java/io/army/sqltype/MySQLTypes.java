package io.army.sqltype;

import io.army.criteria.SQLWords;
import io.army.dialect.Database;

public enum MySQLTypes implements SqlType, SQLWords {

    NULL,
    BOOLEAN,

    TINYINT,
    TINYINT_UNSIGNED,
    SMALLINT,
    SMALLINT_UNSIGNED,

    INT,
    INT_UNSIGNED,
    MEDIUMINT,
    MEDIUMINT_UNSIGNED,

    BIGINT,
    BIGINT_UNSIGNED,
    DECIMAL,
    DECIMAL_UNSIGNED,

    FLOAT,
    DOUBLE,

    TIME,
    DATE,
    YEAR,
    DATETIME,

    CHAR,
    NCHAR,
    VARCHAR,
    TINYTEXT,
    TEXT,
    MEDIUMTEXT,
    LONGTEXT,

    BINARY,
    VARBINARY,
    TINYBLOB,
    BLOB,
    MEDIUMBLOB,
    LONGBLOB,

    BIT,
    ENUM,
    SET,
    JSON,

    POINT,
    LINESTRING,
    POLYGON,
    MULTIPOINT,
    MULTILINESTRING,
    MULTIPOLYGON,
    GEOMETRY,
    GEOMETRYCOLLECTION;


    @Override
    public final Database database() {
        return Database.MySQL;
    }

    @Override
    public final boolean supportNoPrecision() {
        final boolean match;
        switch (this) {
            case VARCHAR:
            case VARBINARY:
                match = false;
                break;
            default:
                match = true;
        }
        return match;
    }

    @Override
    public final boolean supportPrecision() {
        final boolean match;
        switch (this) {
            case VARCHAR:
            case VARBINARY:
            case DECIMAL:
            case DECIMAL_UNSIGNED:
            case TIME:
            case DATETIME:
            case TEXT:
            case BLOB:
                match = true;
                break;
            default:
                match = false;
        }
        return match;
    }

    @Override
    public final boolean supportPrecisionScale() {
        final boolean match;
        switch (this) {
            case DECIMAL:
            case DECIMAL_UNSIGNED:
                match = true;
                break;
            default:
                match = false;
        }
        return match;
    }

    @Override
    public final String render() {
        final String words;
        switch (this) {
            case TINYINT_UNSIGNED:
                words = "TINYINT UNSIGNED";
                break;
            case SMALLINT_UNSIGNED:
                words = "SMALLINT UNSIGNED";
                break;
            case MEDIUMINT_UNSIGNED:
                words = "MEDIUMINT UNSIGNED";
                break;
            case INT_UNSIGNED:
                words = "INT UNSIGNED";
                break;
            case DECIMAL_UNSIGNED:
                words = "DECIMAL UNSIGNED";
                break;
            case BIGINT_UNSIGNED:
                words = "BIGINT UNSIGNED";
                break;
            default:
                words = this.name();
        }
        return words;
    }


    @Override
    public final String toString() {
        return String.format("%s.%s", MySQLTypes.class.getName(), this.name());
    }


}