/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
