package io.army.criteria.mysql;

import io.army.criteria.SQLWords;

public enum MySQLCharset implements SQLWords {

    armscii8(" armscii8"),
    ascii(" ascii"),
    big5(" big5"),
    binary(" binary"),

    cp1250(" cp1250"),
    cp1251(" cp1251"),
    cp1256(" cp1256"),
    cp1257(" cp1257"),

    cp850(" cp850"),
    cp852(" cp852"),
    cp866(" cp866"),
    cp932(" cp932"),

    dec8(" dec8"),
    eucjpms(" eucjpms"),
    euckr(" euckr"),
    gb18030(" gb18030"),

    gb2312(" gb2312"),
    gbk(" gbk"),
    geostd8(" geostd8"),
    greek(" greek"),

    hebrew(" hebrew"),
    hp8(" hp8"),
    keybcs2(" keybcs2"),
    koi8r(" koi8r"),

    koi8u(" koi8u"),
    latin1(" latin1"),
    latin2(" latin2"),
    latin5(" latin5"),

    latin7(" latin7"),
    macce(" macce"),
    macroman(" macroman"),
    sjis(" sjis"),

    swe7(" swe7"),
    tis620(" tis620"),
    ucs2(" ucs2"),
    ujis(" ujis"),

    utf16(" utf16"),
    utf16le(" utf16le"),
    utf32(" utf32"),
    utf8mb3(" utf8mb3"),

    utf8mb4(" utf8mb4");

    private final String words;

    MySQLCharset(String words) {
        this.words = words;
    }

    @Override
    public final String render() {
        return this.words;
    }


    @Override
    public final String toString() {
        return String.format(" %s.%s", MySQLCharset.class.getName(), this.name());
    }

}
