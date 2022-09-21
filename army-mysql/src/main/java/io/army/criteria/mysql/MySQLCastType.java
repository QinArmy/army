package io.army.criteria.mysql;

import io.army.criteria.SQLWords;

/**
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_cast">CAST(expr AS type [ARRAY])</a>
 */
public enum MySQLCastType implements SQLWords {


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
    SIGNED_INTEGER,
    TIME,

    UNSIGNED_INTEGER,
    YEAR,

    Point,
    MultiPoint,
    LineString,
    MultiLineString,

    Polygon,
    MultiPolygon,
    GeometryCollection;


    @Override
    public final String render() {
        final String words;
        switch (this) {
            case SIGNED_INTEGER:
                words = " SIGNED INTEGER";
                break;
            case UNSIGNED_INTEGER:
                words = " UNSIGNED INTEGER";
                break;
            default:
                words = this.name();

        }
        return words;
    }

    @Override
    public final String toString() {
        return String.format("%s.%s", MySQLCastType.class, this.name());
    }


}