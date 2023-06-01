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


    CompoundPredicate equal(BiFunction<SQLField, String, Expression> namedOperator);

    CompoundPredicate less(BiFunction<SQLField, String, Expression> namedOperator);

    CompoundPredicate lessEqual(BiFunction<SQLField, String, Expression> namedOperator);

    CompoundPredicate great(BiFunction<SQLField, String, Expression> namedOperator);

    CompoundPredicate greatEqual(BiFunction<SQLField, String, Expression> namedOperator);

    CompoundPredicate notEqual(BiFunction<SQLField, String, Expression> namedOperator);

    CompoundPredicate like(BiFunction<SQLField, String, Expression> namedOperator);

    CompoundPredicate notLike(BiFunction<SQLField, String, Expression> namedOperator);

    CompoundPredicate in(TeNamedOperator<SQLField> namedOperator, int size);

    CompoundPredicate notIn(TeNamedOperator<SQLField> namedOperator, int size);

    CompoundExpression mod(BiFunction<SQLField, String, Expression> namedOperator);

    CompoundExpression plus(BiFunction<SQLField, String, Expression> namedOperator);

    CompoundExpression minus(BiFunction<SQLField, String, Expression> namedOperator);

    CompoundExpression times(BiFunction<SQLField, String, Expression> namedOperator);

    CompoundExpression divide(BiFunction<SQLField, String, Expression> namedOperator);

    CompoundExpression bitwiseAnd(BiFunction<SQLField, String, Expression> namedOperator);

    CompoundExpression bitwiseOr(BiFunction<SQLField, String, Expression> namedOperator);

    CompoundExpression xor(BiFunction<SQLField, String, Expression> namedOperator);

    CompoundExpression rightShift(BiFunction<SQLField, String, Expression> namedOperator);

    CompoundExpression leftShift(BiFunction<SQLField, String, Expression> namedOperator);


}
