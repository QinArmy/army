package io.army.criteria.impl;


import io.army.criteria.Expression;
import io.army.criteria.Statement;
import io.army.criteria.dialect.Window;
import io.army.criteria.postgre.PostgreWindow;
import io.army.criteria.standard.SQLFunction;
import io.army.mapping.DoubleType;
import io.army.mapping.LongType;
import io.army.mapping.MappingType;

import java.util.function.Consumer;


/**
 * <p>
 * Package class,This class hold window function method.
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/current/functions-window.html">Window Functions</a>
 * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html">Aggregate Functions</a>
 * @since 1.0
 */
abstract class PostgreWindowFunctions extends PostgreDocumentFunctions {

    PostgreWindowFunctions() {
    }


    public interface _OverSpec extends Window._OverWindowClause<PostgreWindow._PartitionBySpec> {


    }

    public interface _AggregateWindowFunc extends _OverSpec, SQLFunction.AggregateFunction, Expression {

        _OverSpec filter(Consumer<Statement._SimpleWhereClause> consumer);

        _OverSpec ifFilter(Consumer<Statement._SimpleWhereClause> consumer);


    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LongType#INSTANCE}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">row_number () → bigint<br/>
     * Returns the number of the current row within its partition, counting from 1.
     * </a>
     */
    public static _OverSpec rowNumber() {
        return PostgreFunctionUtils.zeroArgWindowFunc("ROW_NUMBER", LongType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LongType#INSTANCE}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">rank () → bigint<br/>
     * Returns the rank of the current row, with gaps; that is, the row_number of the first row in its peer group.
     * </a>
     */
    public static _OverSpec rank() {
        return PostgreFunctionUtils.zeroArgWindowFunc("RANK", LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LongType#INSTANCE}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">dense_rank () → bigint<br/>
     * Returns the rank of the current row, without gaps; this function effectively counts peer groups.
     * </a>
     */
    public static _OverSpec denseRank() {
        return PostgreFunctionUtils.zeroArgWindowFunc("DENSE_RANK", LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType#INSTANCE}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">percent_rank () → double precision<br/>
     * Returns the relative rank of the current row, that is (rank - 1) / (total partition rows - 1). The value thus ranges from 0 to 1 inclusive.
     * </a>
     */
    public static _OverSpec percentRank() {
        return PostgreFunctionUtils.zeroArgWindowFunc("PERCENT_RANK", DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType#INSTANCE}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">cume_dist () → double precision<br/>
     * Returns the cumulative distribution, that is (number of partition rows preceding or peers with current row) / (total partition rows). The value thus ranges from 1/N to 1.
     * </a>
     */
    public static _OverSpec cumeDist() {
        return PostgreFunctionUtils.zeroArgWindowFunc("CUME_DIST", DoubleType.INSTANCE);
    }


}
