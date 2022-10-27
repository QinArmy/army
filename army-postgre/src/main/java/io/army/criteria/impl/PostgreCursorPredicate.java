package io.army.criteria.impl;

import io.army.dialect._SqlContext;

final class PostgreCursorPredicate extends OperationPredicate {


    private final String cursorName;

    PostgreCursorPredicate(String cursorName) {
        this.cursorName = cursorName;
    }

    @Override
    public void appendSql(final _SqlContext context) {
        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder()
                .append(" CURRENT OF ");
        context.parser().identifier(this.cursorName, sqlBuilder);
    }


}
