package io.army.env;

import io.army.util._StringUtils;

public enum EscapeMode {

    DEFAULT,

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-literals.html#character-escape-sequences">MySQL String Literals</a>
     * @see <a href="https://www.postgresql.org/docs/current/sql-syntax-lexical.html#SQL-SYNTAX-STRINGS-ESCAPE">Postgre String Constants With C-Style Escapes</a>
     */
    BACK_SLASH,

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-syntax-lexical.html#SQL-SYNTAX-STRINGS-UESCAPE">Postgre String Constants With Unicode Escapes</a>
     */
    UNICODE;

    @Override
    public final String toString() {
        return _StringUtils.enumToString(this);
    }


}
