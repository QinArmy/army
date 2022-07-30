package io.army.criteria.impl;

import io.army.criteria.DataField;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.dialect.Dialect;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.util._Exceptions;

enum AssignOperator {

    PLUS_EQUAL(" +="),
    MINUS_EQUAL(" -=");

    private final String signText;

    AssignOperator(String signText) {
        this.signText = signText;
    }


    final void appendOperator(final Dialect dialect, final DataField field, final _SqlContext context) {
        switch (dialect.database()) {
            case MySQL:
            case PostgreSQL:
                this.simpleOperator(field, context);
                break;
            default:
                throw _Exceptions.unexpectedEnum(dialect.database());
        }

    }


    private void simpleOperator(final DataField field, final _SqlContext context) {
        final StringBuilder sqlBuilder = context.sqlBuilder()
                .append(_Constant.SPACE_EQUAL);
        ((_SelfDescribed) field).appendSql(context);
        switch (this) {
            case PLUS_EQUAL:
                sqlBuilder.append(DualOperator.PLUS.signText);
                break;
            case MINUS_EQUAL:
                sqlBuilder.append(DualOperator.MINUS.signText);
                break;
            default:
                throw _Exceptions.unexpectedEnum(this);
        }
    }


}
