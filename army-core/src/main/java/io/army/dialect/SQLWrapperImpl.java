package io.army.dialect;

import io.army.util.Assert;

import java.util.Collections;
import java.util.List;

final class SQLWrapperImpl implements SQLWrapper {


    private final String sql;

    private final List<ParamWrapper> paramList;

    SQLWrapperImpl(String sql, List<ParamWrapper> paramList) {
        Assert.hasText(sql, "sql required");
        Assert.notNull(paramList, "paramList required");

        this.sql = sql;
        this.paramList = Collections.unmodifiableList(paramList);
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
    public String toString() {
        final String sql = this.sql;
        StringBuilder builder = new StringBuilder("\noriginal sql:\n")
                .append(sql)
                .append("\n")
                .append("sql with param(s):\n");

        final int len = sql.length();
        final int size = this.paramList.size();
        final List<ParamWrapper> paramList = this.paramList;
        int start = 0, index = 0;

        for (int i; (i = sql.indexOf("?", start)) >= 0; start = i + 1, index++) {
            Assert.state(index < size, "sql and paramList not match.");

            builder.append(sql, start, i)
                    .append("{")
                    .append(index + 1)
                    .append(":")
                    .append(paramList.get(index).value())
                    .append("}")
            ;
        }

        Assert.state(index == size, "sql and paramList not match.");

        if (start < len) {
            builder.append(sql, start, len);
        }
        return builder.toString();
    }

}
