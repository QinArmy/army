package io.army.criteria;

import io.army.function.TeNamedOperator;

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
public interface SQLField extends NamedExpression {

    String fieldName();


    IPredicate equal(BiFunction<SQLField, String, Expression> namedOperator);

    IPredicate less(BiFunction<SQLField, String, Expression> namedOperator);

    IPredicate lessEqual(BiFunction<SQLField, String, Expression> namedOperator);

    IPredicate great(BiFunction<SQLField, String, Expression> namedOperator);

    IPredicate greatEqual(BiFunction<SQLField, String, Expression> namedOperator);

    IPredicate notEqual(BiFunction<SQLField, String, Expression> namedOperator);

    IPredicate like(BiFunction<SQLField, String, Expression> namedOperator);

    IPredicate notLike(BiFunction<SQLField, String, Expression> namedOperator);

    IPredicate in(TeNamedOperator<SQLField> namedOperator, int size);

    IPredicate notIn(TeNamedOperator<SQLField> namedOperator, int size);

    Expression mod(BiFunction<SQLField, String, Expression> namedOperator);

    Expression plus(BiFunction<SQLField, String, Expression> namedOperator);

    Expression minus(BiFunction<SQLField, String, Expression> namedOperator);

    Expression times(BiFunction<SQLField, String, Expression> namedOperator);

    Expression divide(BiFunction<SQLField, String, Expression> namedOperator);

    Expression bitwiseAnd(BiFunction<SQLField, String, Expression> namedOperator);

    Expression bitwiseOr(BiFunction<SQLField, String, Expression> namedOperator);

    Expression xor(BiFunction<SQLField, String, Expression> namedOperator);

    Expression rightShift(BiFunction<SQLField, String, Expression> namedOperator);

    Expression leftShift(BiFunction<SQLField, String, Expression> namedOperator);


}
