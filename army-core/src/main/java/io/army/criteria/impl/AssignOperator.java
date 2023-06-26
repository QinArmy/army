package io.army.criteria.impl;

import io.army.criteria.SQLField;
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


    final void appendOperator(final SQLField field, final StringBuilder sqlBuilder, final _SqlContext context) {
        switch (context.database()) {
            case MySQL:
            case PostgreSQL:
                this.simpleOperator(field, sqlBuilder, context);
                break;
            default:
                throw _Exceptions.unexpectedEnum(context.database());
        }

    }


    private void simpleOperator(final SQLField field, final StringBuilder sqlBuilder, final _SqlContext context) {
        sqlBuilder.append(_Constant.SPACE_EQUAL);
        ((_SelfDescribed) field).appendSql(sqlBuilder, context);
        switch (this) {
            case PLUS_EQUAL:
                sqlBuilder.append(DualExpOperator.PLUS.spaceOperator);
                break;
            case MINUS_EQUAL:
                sqlBuilder.append(DualExpOperator.MINUS.spaceOperator);
                break;
            default:
                throw _Exceptions.unexpectedEnum(this);
        }
    }


}
