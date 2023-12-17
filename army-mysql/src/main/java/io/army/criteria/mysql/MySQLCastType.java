package io.army.criteria.mysql;

import io.army.util._StringUtils;

/**
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_cast">CAST(expr AS type [ARRAY])</a>
 */
public enum MySQLCastType {


    BINARY,
    CHAR,
    DATE,
    DATETIME,

    DECIMAL,
    DOUBLE,
    FLOAT,
    JSON,

    NCHAR,
    REAL,
    SIGNED,
    TIME,

    UNSIGNED,
    YEAR,

    Point,
    MultiPoint,
    LineString,
    MultiLineString,

    Polygon,
    MultiPolygon,
    GeometryCollection;


    public final String typeName() {
        return this.name();
    }


    @Override
    public final String toString() {
        return _StringUtils.enumToString(this);
    }


}
