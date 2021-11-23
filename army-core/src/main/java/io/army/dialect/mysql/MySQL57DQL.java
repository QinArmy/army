package io.army.dialect.mysql;

import io.army.ErrorCode;
import io.army.criteria.CriteriaException;
import io.army.criteria.LockMode;
import io.army.dialect.AbstractDQL;
import io.army.dialect.Keywords;
import io.army.dialect.SqlBuilder;
import io.army.dialect._TablesSqlContext;
import io.army.mapping.MappingType;
import io.army.mapping._MappingFactory;
import io.army.stmt.ParamValue;

class MySQL57DQL extends AbstractDQL {

    MySQL57DQL(MySQL57Dialect dialect) {
        super(dialect);
    }



    /*################################## blow AbstractDQL template method ##################################*/


    @Override
    protected _TablesSqlContext createSpecialSelectContext(_TablesSqlContext original) {
        return null;
    }

    @Override
    protected _TablesSqlContext createSpecialSubQueryContext(_TablesSqlContext original) {
        return null;
    }

    @Override
    protected final void limitClause(int offset, int rowCount, _TablesSqlContext context) {

        if (offset > -1 || rowCount > -1) {
            SqlBuilder builder = context.sqlBuilder();
            builder.append(" ")
                    .append(Keywords.LIMIT);
            final MappingType integerType = _MappingFactory.getMapping(Integer.class);
            if (offset > -1) {
                builder.append(" ?,");
                context.appendParam(ParamValue.build(integerType, offset));
                if (rowCount < 0) {
                    throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                            , "offset[%s] > -1 and rowCount[%s] < 0 is supported by MySQL.", offset, rowCount);
                }
            }
            builder.append(" ?");
            context.appendParam(ParamValue.build(integerType, rowCount));
        }
    }

    @Override
    protected void lockClause(LockMode lockMode, _TablesSqlContext context) {
        switch (lockMode) {
            case PESSIMISTIC_READ:
                context.sqlBuilder().append(" LOCK IN SHARE MODE");
                break;
            case PESSIMISTIC_WRITE:
                context.sqlBuilder().append(" FOR UPDATE");
                break;
            default:
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                        , "LockMode[%s] is supported by MySQL 5.7", lockMode);
        }
    }

}
