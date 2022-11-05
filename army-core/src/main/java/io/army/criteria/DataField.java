package io.army.criteria;

import io.army.function.TeExpression;

import java.util.function.BiFunction;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link TableField}</li>
 *         <li>{@link DerivedField}</li>
 *         <li>{@link DerivedField}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface DataField extends NamedExpression {

    String fieldName();


    IPredicate equal(BiFunction<DataField, String, Expression> namedOperator);

    IPredicate less(BiFunction<DataField, String, Expression> namedOperator);

    IPredicate lessEqual(BiFunction<DataField, String, Expression> namedOperator);

    IPredicate great(BiFunction<DataField, String, Expression> namedOperator);

    IPredicate greatEqual(BiFunction<DataField, String, Expression> namedOperator);

    IPredicate notEqual(BiFunction<DataField, String, Expression> namedOperator);

    IPredicate like(BiFunction<DataField, String, Expression> namedOperator);

    IPredicate notLike(BiFunction<DataField, String, Expression> namedOperator);

    IPredicate in(TeExpression<DataField, String, Integer> namedOperator, int size);

    IPredicate notIn(TeExpression<DataField, String, Integer> namedOperator, int size);

    Expression mod(BiFunction<DataField, String, Expression> namedOperator);

    Expression plus(BiFunction<DataField, String, Expression> namedOperator);

    Expression minus(BiFunction<DataField, String, Expression> namedOperator);

    Expression times(BiFunction<DataField, String, Expression> namedOperator);

    Expression divide(BiFunction<DataField, String, Expression> namedOperator);

    Expression bitwiseAnd(BiFunction<DataField, String, Expression> namedOperator);

    Expression bitwiseOr(BiFunction<DataField, String, Expression> namedOperator);

    Expression xor(BiFunction<DataField, String, Expression> namedOperator);

    Expression rightShift(BiFunction<DataField, String, Expression> namedOperator);

    Expression leftShift(BiFunction<DataField, String, Expression> namedOperator);


}
