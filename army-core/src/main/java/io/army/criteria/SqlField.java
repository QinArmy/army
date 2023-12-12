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
*
 * @since 1.0
 */
public interface SqlField extends NamedExpression {

    String fieldName();


    CompoundPredicate equal(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundPredicate less(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundPredicate lessEqual(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundPredicate great(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundPredicate greatEqual(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundPredicate notEqual(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundPredicate like(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundPredicate notLike(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundPredicate in(TeNamedOperator<SqlField> namedOperator, int size);

    CompoundPredicate notIn(TeNamedOperator<SqlField> namedOperator, int size);

    CompoundExpression mod(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundExpression plus(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundExpression minus(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundExpression times(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundExpression divide(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundExpression bitwiseAnd(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundExpression bitwiseOr(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundExpression xor(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundExpression rightShift(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundExpression leftShift(BiFunction<SqlField, String, Expression> namedOperator);


}
