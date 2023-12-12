package io.army.criteria;

import java.util.function.Function;

/**
 * <p>
 * This interface representing ROW constructor of VALUES statement.
 * * @since 1.0
 */
public interface ValuesRowConstructor {

    ValuesRowConstructor column(Expression exp);

    ValuesRowConstructor column(Expression exp1, Expression exp2);

    ValuesRowConstructor column(Expression exp1, Expression exp2, Expression exp3);

    ValuesRowConstructor column(Expression exp1, Expression exp2, Expression exp3, Expression exp4);

    ValuesRowConstructor column(Function<Object, Expression> valueOperator, Object value);

    ValuesRowConstructor column(Function<Object, Expression> valueOperator, Object value1, Object value2);

    ValuesRowConstructor column(Function<Object, Expression> valueOperator, Object value1, Object value2, Object value3);

    ValuesRowConstructor column(Function<Object, Expression> valueOperator, Object value1, Object value2, Object value3, Object value4);


    ValuesRowConstructor row();

}
