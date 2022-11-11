package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.TypeInfer;
import io.army.dialect._SqlContext;
import io.army.mapping.LongType;
import io.army.mapping.MappingType;
import io.army.meta.TypeMeta;


/**
 * <p>
 * This class is standard sql function utils.
 * </p>
 *
 * @since 1.0
 */
public abstract class SQLFunctions extends Functions {

    /**
     * private constructor
     */
    private SQLFunctions() {
    }



    /*-------------------below Aggregate Function-------------------*/

    public static Expression countStar() {
        return CountStartFunction.INSTANCE;
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LongType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static Expression count(Expression expr) {
        return FunctionUtils.oneArgFunc("COUNT", expr, LongType.INSTANCE);
    }


    /**
     * standard count(*) expression
     *
     * @see #countStar()
     * @since 1.0
     */
    private static final class CountStartFunction extends OperationExpression<TypeInfer> {

        private static final CountStartFunction INSTANCE = new CountStartFunction();

        private CountStartFunction() {
            super(SQLs::_identity);
        }

        @Override
        public CountStartFunction bracket() {
            //return this,don't create new instance
            return this;
        }

        @Override
        public TypeMeta typeMeta() {
            return LongType.INSTANCE;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            context.sqlBuilder().append(" COUNT(*)");
        }


    }//CountStartFunction


}
