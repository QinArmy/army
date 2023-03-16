package io.army.criteria;

import java.util.function.Function;

/**
 * <p>
 * This interface representing ROW constructor of VALUES statement.
 * </p>
 *
 * @since 1.0
 */
public interface RowConstructor {

    RowConstructor column(Expression exp);

    RowConstructor column(Expression exp1, Expression exp2);

    RowConstructor column(Expression exp1, Expression exp2, Expression exp3);

    RowConstructor column(Expression exp1, Expression exp2, Expression exp3, Expression exp4);

    RowConstructor column(Function<Object, Expression> valueOperator, Object value);

    RowConstructor column(Function<Object, Expression> valueOperator, Object value1, Object value2);

    RowConstructor column(Function<Object, Expression> valueOperator, Object value1, Object value2, Object value3);

    RowConstructor column(Function<Object, Expression> valueOperator, Object value1, Object value2, Object value3, Object value4);


    RowConstructor row();

}
