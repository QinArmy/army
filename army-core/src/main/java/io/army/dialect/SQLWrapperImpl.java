package io.army.dialect;

import io.army.lang.Nullable;
import io.army.util.Assert;
import io.army.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

final class SQLWrapperImpl implements SQLWrapper {


    private final String sql;

    private final List<ParamWrapper> paramList;

    private final boolean hasVersion;

    SQLWrapperImpl(String sql, List<ParamWrapper> paramList) {
        this(sql, paramList, false);
    }

    SQLWrapperImpl(String sql, List<ParamWrapper> paramList, boolean hasVersion) {
        Assert.hasText(sql, "dml required");
        Assert.notNull(paramList, "paramList required");

        this.sql = sql;
        this.paramList = Collections.unmodifiableList(paramList);
        this.hasVersion = hasVersion;
    }

    @Override
    public String sql() {
        return sql;
    }

    @Override
    public List<ParamWrapper> paramList() {
        return paramList;
    }

    @Override
    public String toString(Dialect dialect) {
        return toString();
    }

    @Override
    public String toString() {
        return replacePlaceHolder(this.sql);
    }

    /*################################## blow private method ##################################*/

    private boolean isInsertSql(String afterTrimSql) {
        return afterTrimSql.startsWith("INSERT INTO ")
                || afterTrimSql.startsWith("insert into ");
    }


    private void plusIndexPrefix(final String tablePart, StringBuilder builder) {
        int index;
        index = tablePart.indexOf("(");
        Assert.isTrue(index > 0, () -> String.format("insert dml error,%s", tablePart));
        // insert into (
        builder.append(tablePart, 0, index);

        final int len = tablePart.length();
        for (int i = index + 1; i < len; i++) {
            if (!Character.isWhitespace(tablePart.charAt(i))) {
                builder.append(tablePart, index, i)
                        .append("1:");
                index = i;
                break;
            }
        }

        // start from index
        int start = index;
        for (int num = 2; (index = tablePart.indexOf(",", start)) >= 0; start = index + 1, num++) {

            builder.append(tablePart, start, index + 1)
                    .append(num)
                    .append(":")
            ;
        }
        if (start < len) {
            builder.append(tablePart, start, len);
        }
    }


    private static final Logger LOG = LoggerFactory.getLogger(SQLWrapperImpl.class);


    private String replacePlaceHolder(String sql) {
        StringBuilder builder = new StringBuilder();
        builder.append("original dml:\n")
                .append(sql)
        .append("\ndml with param(s):\n");

        final int len = sql.length();
        final int size = this.paramList.size();
        final List<ParamWrapper> paramList = this.paramList;
        int start = 0, index = 0;
        ParamWrapper paramWrapper;
        for (int i; (i = sql.indexOf("?", start)) >= 0; start = i + 1, index++) {
            Assert.state(index < size, "dml then paramList not match.");

            builder.append(sql, start, i)
                    .append("{")
                    .append(index + 1)
                    .append(":");
            paramWrapper = paramList.get(index);
            if (paramWrapper.value() == null) {
                builder.append("NULL");
            } else {
                builder.append(paramWrapper.mappingType().nonNullTextValue(paramWrapper.value()));
            }

            builder.append("}")
            ;
        }

        Assert.state(index == size, () -> String.format(
                "dml then paramList not match.dml:\n%s\nparamList size:%s", sql, paramList.size()));

        if (start < len) {
            builder.append(sql, start, len);
        }
        return builder.toString();
    }


    public int indexNotWhitespace(String text, int fromIndex) {
        return doIndexWhitespace(text, fromIndex, false);
    }

    public int indexWhitespace(String text, int fromIndex) {
        return doIndexWhitespace(text, fromIndex, true);
    }

    private int doIndexWhitespace(String partSQL, int fromIndex, boolean match) {
        int len = partSQL.length(), index = -1;
        if (fromIndex > len || fromIndex < 0) {
            throw createSqlError(partSQL);
        }
        for (int i = fromIndex; i < len; i++) {
            if (Character.isWhitespace(partSQL.charAt(i)) == match) {
                index = i;
                break;
            }
        }

        ClauseParser parser = SQLWrapperImpl::leftRoundBracketParse;
        return index;
    }


    @FunctionalInterface
    private interface ClauseParser {

        ClauseParser LEFT_ROUND_BRACKET = SQLWrapperImpl::leftRoundBracketParse;

        ClauseParser SINGLE_QUOTE = SQLWrapperImpl::singleQuoteParse;

        ClauseParser COMMA = SQLWrapperImpl::commaParse;

        ClauseParser QUESTION = SQLWrapperImpl::questionParse;

        ClauseParser NUMBER = SQLWrapperImpl::numberParse;

        ClauseParser FUNCTION = SQLWrapperImpl::functionParse;

        ClauseParser RIGHT_ROUND_BRACKET = SQLWrapperImpl::rightRoundBracket;

        ClauseParser SEMICOLON = SQLWrapperImpl::singleQuoteParse;

        @Nullable
        ClauseParser parser(String sql, int fromIndex, StringBuilder builder);

    }

    @Nullable
    private static ClauseParser singleQuoteParse(String sql, int fromIndex, StringBuilder builder) {
        return null;
    }

    @Nullable
    private static ClauseParser commaParse(String sql, int fromIndex, StringBuilder builder) {
        return null;
    }

    @Nullable
    private static ClauseParser questionParse(String sql, int fromIndex, StringBuilder builder) {
        return null;
    }

    @Nullable
    private static ClauseParser numberParse(String sql, int fromIndex, StringBuilder builder) {
        return null;
    }

    @Nullable
    private static ClauseParser functionParse(String sql, int fromIndex, StringBuilder builder) {
        return null;
    }

    @Nullable
    private static ClauseParser rightRoundBracket(String sql, int fromIndex, StringBuilder builder) {
        return null;
    }

    @Nullable
    private static ClauseParser semicolonParse(String sql, int fromIndex, StringBuilder builder) {
        return null;
    }


    private static ClauseParser leftRoundBracketParse(String sql, int fromIndex, StringBuilder builder) {
        int index = sql.indexOf("(");
        if (index < 0) {
            throw createSqlError(sql);
        }
        switch (nextNotWhitespace(sql, fromIndex)) {
            case '?':
                return null;
            case '\'':
            case ',':
                throw createSqlError(sql);
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '9':
                break;
            default:
                // hear , function


        }
        int indexOfComma = indexOfNextComma(sql, index + 1);
        return null;
    }

    private static int indexOfNextComma(String sql, int fromIndex) {
        if (fromIndex < 0 || fromIndex >= sql.length()) {
            throw createSqlError(sql);
        }
        int len = sql.length(), index = -1;
        char ch;
        for (int i = fromIndex; i < len; i++) {
            ch = sql.charAt(i);
            if (ch == ',') {
                index = i;
                break;
            }

        }
        return index;
    }


    private static char nextNotWhitespace(String sql, int fromIndex) {
        final int len = sql.length();
        if (fromIndex < 0 || fromIndex >= len) {
            throw createSqlError(sql);
        }
        char ch = '\00';
        for (int i = fromIndex; i < len; i++) {
            ch = sql.charAt(i);
            if (Character.isWhitespace(ch)) {
                continue;
            }
            break;
        }
        return ch;
    }


    private static IllegalArgumentException createSqlError(String partSQL) {
        return new IllegalArgumentException(String.format("value part of insert dml,%s", partSQL));
    }

}
