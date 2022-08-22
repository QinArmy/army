package io.army.sqltype;

import io.army.dialect.Database;

public enum MySQLTypes implements SqlType {

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
    public final String toString() {
        return String.format("%s.%s", MySQLTypes.class.getName(), this.name());
    }


}
