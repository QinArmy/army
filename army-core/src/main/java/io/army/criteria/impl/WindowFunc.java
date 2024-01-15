package io.army.criteria.impl;

import io.army.criteria.SimpleExpression;
import io.army.criteria.dialect.Window;
import io.army.criteria.standard.SQLFunction;
import io.army.mapping.DoubleType;
import io.army.mapping.LongType;
import io.army.mapping.MappingType;

public abstract class WindowFunc {

    private WindowFunc() {
        throw new UnsupportedOperationException();
    }

    public interface _OverSpec extends Window._OverWindowClause<Window._StandardPartitionBySpec> {


    }

    public interface _AggregateWindowFunc extends _OverSpec, SQLFunction.AggregateFunction, SimpleExpression {

    }

    /**
     * <p>The {@link MappingType} of function return type:  {@link  DoubleType}.
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_cume-dist">CUME_DIST() over_clause</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">cume_dist () → double precision<br/>
     * Returns the cumulative distribution, that is (number of partition rows preceding or peers with current row) / (total partition rows). The value thus ranges from 1/N to 1.
     * </a>
     */
    public static _OverSpec cumeDist() {
        return WindowFunctions.zeroArgWindowFunc("CUME_DIST", DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  LongType}.
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_row-number">ROW_NUMBER() over_clause</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">row_number () → bigint<br/>
     * Returns the number of the current row within its partition, counting from 1.
     * </a>
     */
    public static _OverSpec rowNumber() {
        return WindowFunctions.zeroArgWindowFunc("ROW_NUMBER", LongType.INSTANCE);
    }


}
