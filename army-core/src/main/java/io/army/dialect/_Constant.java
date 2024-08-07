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

package io.army.dialect;

public interface _Constant {

    String TRUE = "TRUE";

    String FALSE = "FALSE";

    String SPACE_NULL = " NULL";

    String NULL = "NULL";

    String SPACE_DEFAULT = " DEFAULT";

    String SPACE_NOT_NULL = " NOT NULL";

    String FORBID_ALIAS = "_army_";

    String WITH = "WITH";

    String SPACE_RECURSIVE = " RECURSIVE";

    String SELECT = "SELECT";

    String SPACE_SELECT_SPACE = " SELECT ";

    String UPDATE = "UPDATE";

    String UPDATE_SPACE = "UPDATE ";

    String DELETE = "DELETE";

    String DELETE_SPACE = "DELETE ";

    String DELETE_FROM = "DELETE FROM";
    String DELETE_FROM_SPACE = "DELETE FROM ";

    String SPACE_FROM = " FROM";

    String SPACE_FROM_SPACE = " FROM ";

    String SPACE_LATERAL = " LATERAL";

    String SPACE_USING = " USING";

    String SPACE_JOIN_SPACE = " JOIN ";

    String LEFT_JOIN = "LEFT JOIN";

    String RIGHT_JOIN = "RIGHT JOIN";

    String FULL_JOIN = "FULL JOIN";

    String SPACE_ON_SPACE = " ON ";

    String SPACE_ON = " ON";

    String SPACE_WHERE = " WHERE";

    String SPACE_AND = " AND";

    String SPACE_AND_SPACE = " AND ";

    String SPACE_NOT = " NOT";

    String SPACE_EXISTS = " EXISTS";

    String SPACE_OR = " OR";

    String SPACE_GROUP_BY = " GROUP BY";

    String SPACE_HAVING = " HAVING";

    String SPACE_ORDER_BY = " ORDER BY";

    String SPACE_WINDOW = " WINDOW";

    String SPACE_EQUAL = " =";

    String SPACE_EQUAL_SPACE = " = ";

    String SPACE_AS = " AS";

    String SPACE_AS_SPACE = " AS ";

    String INSERT = "INSERT";

    String SPACE_INTO_SPACE = " INTO ";

    String INSERT_INTO_SPACE = "INSERT INTO ";

    String VALUES = "VALUES";

    String SPACE_VALUES = " VALUES";

    String SPACE_BETWEEN = " BETWEEN";

    String DEFAULT = "DEFAULT";

    String SPACE_SET = " SET";

    String SPACE_SET_SPACE = " SET ";


    String SPACE_LIMIT = " LIMIT";

    String SPACE_LIMIT_SPACE = " LIMIT ";

    String SPACE_FETCH = " FETCH";


    String SPACE_OFFSET = " OFFSET";

    String SPACE_OFFSET_SPACE = " OFFSET ";

    String SPACE_ROW = " ROW";

    String SPACE_ROWS = " ROWS";

    String SPACE_OVER = " OVER";

    String SPACE_INTERVAL = " INTERVAL";

    String SPACE_PARTITION_BY = " PARTITION BY";

    String SPACE_IS_NULL = " IS NULL";

    String SPACE_RETURNING = " RETURNING";

    String SPACE_ONLY = " ONLY";

    String SPACE_COMMA = " ,";

    String SPACE_SEPARATOR = " SEPARATOR";

    String SPACE_CHARACTER_SET_SPACE = " CHARACTER SET ";

    String SPACE_COLLATE_SPACE = " COLLATE ";


    String UNDERSCORE_ARRAY = "_ARRAY";

    String SPACE_SEMICOLON = " ;";

    String SPACE_SEMICOLON_TWO_LINE = " ;\n\n";

    String SPACE_COMMA_SPACE = " , ";

    String SPACE_SEMICOLON_SPACE = " ; ";

    String SPACE_LEFT_PAREN = " (";

    String SPACE_RIGHT_PAREN = " )";

    String SPACE_LEFT_BRACE = " {";

    String SPACE_RIGHT_BRACE = " }";

    String SPACE_RIGHT_SQUARE_BRACKET = " ]";

    String PARENS = "()";

    String SPACE_FOR_UPDATE = " FOR UPDATE";

    String SPACE_FOR_SHARE = " FOR SHARE";

    String SPACE_OF_SPACE = " OF ";

    String SPACE_LOCK_IN_SHARE_MODE = " LOCK IN SHARE MODE";

    String SPACE_ASC = " ASC";

    String SPACE_DESC = " DESC";

    String SPACE_QUOTE = " '";

    String SPACE_AT = " @";

    String SPACE_ZERO = " 0";

    String DOUBLE_COLON = "::";

    char LEFT_PAREN = '(';

    char RIGHT_PAREN = ')';

    char LEFT_SQUARE_BRACKET = '[';

    char RIGHT_SQUARE_BRACKET = ']';

    char LEFT_BRACE = '{';

    char RIGHT_BRACE = '}';

    char SPACE = ' ';

    char COMMA = ',';

    char PERIOD = '.';

    char NUL_CHAR = '\0';
    char BACK_SLASH = '\\';

    char QUOTE = '\'';

    char DOUBLE_QUOTE = '"';

    char AT = '@';

    char ASTERISK = '*';

    char SLASH = '/';

    char EQUAL = '=';

    char SEMICOLON = ';';


}
