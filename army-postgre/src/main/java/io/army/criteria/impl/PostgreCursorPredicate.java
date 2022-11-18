package io.army.criteria.impl;

import io.army.criteria.TypeInfer;
import io.army.dialect._SqlContext;
import io.army.util._StringUtils;

final class PostgreCursorPredicate extends OperationPredicate<TypeInfer> {


    private final String cursorName;

    PostgreCursorPredicate(String cursorName) {
        super(SQLs._IDENTITY);
        this.cursorName = cursorName;
    }

    @Override
    public void appendSql(final _SqlContext context) {
        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder()
                .append(" CURRENT OF ");
        context.parser().identifier(this.cursorName, sqlBuilder);
    }

    @Override
    public int hashCode() {
        return this.cursorName.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean match;
        if (obj == this) {
            match = true;
        } else if (obj instanceof PostgreCursorPredicate) {
            match = ((PostgreCursorPredicate) obj).cursorName.equals(this.cursorName);
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public String toString() {
        return _StringUtils.builder()
                .append(" CURRENT OF ")
                .append(this.cursorName)
                .toString();
    }


}
