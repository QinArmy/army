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

    RowConstructor add(Expression exp);

    RowConstructor add(Expression exp1, Expression exp2);

    RowConstructor add(Expression exp1, Expression exp2, Expression exp3);

    RowConstructor add(Expression exp1, Expression exp2, Expression exp3, Expression exp4);

    RowConstructor add(Function<Object, Expression> valueOperator, Object value);

    RowConstructor add(Function<Object, Expression> valueOperator, Object value1, Object value2);

    RowConstructor add(Function<Object, Expression> valueOperator, Object value1, Object value2, Object value3);

    RowConstructor add(Function<Object, Expression> valueOperator, Object value1, Object value2, Object value3, Object value4);


    RowConstructor row();

}
