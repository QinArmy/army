package io.army.dialect.postgre;

import io.army.criteria.LockMode;
import io.army.dialect.AbstractDQL;
import io.army.dialect.DialectUtils;
import io.army.dialect.SQLBuilder;
import io.army.dialect.TableContextSQLContext;

class Postgre11DQL extends AbstractDQL {

    Postgre11DQL(Postgre11Dialect dialect) {
        super(dialect);
    }

    @Override
    protected TableContextSQLContext createSpecialSelectContext(TableContextSQLContext original) {
        return null;
    }

    @Override
    protected TableContextSQLContext createSpecialSubQueryContext(TableContextSQLContext original) {
        return null;
    }

    @Override
    protected void limitClause(int offset, int rowCount, TableContextSQLContext context) {
        SQLBuilder builder = context.sqlBuilder();
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
    protected void lockClause(LockMode lockMode, TableContextSQLContext context) {
        SQLBuilder builder = context.sqlBuilder();
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
