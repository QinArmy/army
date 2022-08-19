package io.army.sqltype;

import io.army.dialect.Database;

public enum MySqlType implements SqlType {

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


}
