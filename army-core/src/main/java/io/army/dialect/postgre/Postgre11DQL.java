package io.army.dialect.postgre;

import io.army.criteria.LockMode;
import io.army.dialect.AbstractDQL;
import io.army.dialect.DialectUtils;
import io.army.dialect._TablesSqlContext;

class Postgre11DQL extends AbstractDQL {

    Postgre11DQL(Postgre11Dialect dialect) {
        super(dialect);
    }

    @Override
    protected _TablesSqlContext createSpecialSelectContext(_TablesSqlContext original) {
        return null;
    }

    @Override
    protected _TablesSqlContext createSpecialSubQueryContext(_TablesSqlContext original) {
        return null;
    }

    @Override
    protected void limitClause(int offset, int rowCount, _TablesSqlContext context) {
        StringBuilder builder = context.sqlBuilder();
        if (rowCount > -1) {
            builder.append(" LIMIT ")
                    .append(rowCount);
        }
        if (offset > -1) {
            builder.append(" OFFSET ")
                    .append(offset);
        }
    }

    @Override
    protected void lockClause(LockMode lockMode, _TablesSqlContext context) {
        StringBuilder builder = context.sqlBuilder();
        switch (lockMode) {
            case NONE:
                //no-op
                break;
            case PESSIMISTIC_READ:
                builder.append(" FOR SHARE");
                break;
            case PESSIMISTIC_WRITE:
                builder.append(" FOR UPDATE");
                break;
            default:
                throw DialectUtils.createUnknownLockModeException(lockMode, database());
        }
    }
}
