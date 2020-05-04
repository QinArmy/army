package io.army.dialect.mysql;

import io.army.ErrorCode;
import io.army.criteria.CriteriaException;
import io.army.criteria.LockMode;
import io.army.criteria.impl.inner.InnerSpecialComposeQuery;
import io.army.criteria.impl.inner.InnerSpecialSelect;
import io.army.criteria.impl.inner.InnerSpecialSubQuery;
import io.army.dialect.AbstractDQL;
import io.army.dialect.ClauseSQLContext;
import io.army.dialect.Keywords;
import io.army.meta.mapping.MappingFactory;
import io.army.meta.mapping.MappingMeta;
import io.army.wrapper.ParamWrapper;

class MySQL57DQL extends AbstractDQL {

    MySQL57DQL(MySQL57Dialect dialect) {
        super(dialect);
    }



    /*################################## blow AbstractDQL template method ##################################*/

    @Override
    protected void assertSpecialComposeSelect(InnerSpecialComposeQuery select) {

    }

    @Override
    protected void assertSpecialSubQuery(InnerSpecialSubQuery subQuery) {

    }

    @Override
    protected void assertSpecialComposeSubQuery(InnerSpecialComposeQuery composeQuery) {

    }

    @Override
    protected void assertSpecialSelect(InnerSpecialSelect select) {

    }

    @Override
    protected void specialPartSelect(InnerSpecialComposeQuery select, ClauseSQLContext context) {

    }

    @Override
    protected void specialSelect(InnerSpecialSelect specialSelect, ClauseSQLContext context) {

    }

    @Override
    protected void specialSubQuery(InnerSpecialSubQuery composeQuery, ClauseSQLContext context) {

    }

    @Override
    protected ClauseSQLContext createSpecialSelectContext(ClauseSQLContext original) {
        return null;
    }

    @Override
    protected ClauseSQLContext createSpecialSubQueryContext(ClauseSQLContext original) {
        return null;
    }

    @Override
    protected final void limitClause(int offset, int rowCount, ClauseSQLContext context) {

        if (offset > -1 || rowCount > -1) {
            StringBuilder builder = context.sqlBuilder();
            builder.append(" ")
                    .append(Keywords.LIMIT);
            final MappingMeta integerType = MappingFactory.getDefaultMapping(Integer.class);
            if (offset > -1) {
                builder.append(" ?,");
                context.appendParam(ParamWrapper.build(integerType, offset));
                if (rowCount < 0) {
                    throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                            , "offset[%s] > -1 and rowCount[%s] < 0 is supported by MySQL.", offset, rowCount);
                }
            }
            builder.append(" ?");
            context.appendParam(ParamWrapper.build(integerType, rowCount));
        }
    }

    @Override
    protected void lockClause(LockMode lockMode, ClauseSQLContext context) {
        switch (lockMode) {
            case READ:
                context.sqlBuilder().append(" LOCK IN SHARE MODE");
                break;
            case WRITE:
                context.sqlBuilder().append(" FOR UPDATE");
                break;
            default:
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                        , "LockMode[%s] is supported by MySQL 5.7", lockMode);
        }
    }

    @Override
    protected final boolean tableAliasAfterAs() {
        return true;
    }
}
