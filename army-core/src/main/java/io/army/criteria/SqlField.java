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
public interface SqlField extends NamedExpression {

    String fieldName();


    CompoundPredicate equalSpace(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundPredicate lessSpace(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundPredicate lessEqualSpace(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundPredicate greatSpace(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundPredicate greatEqualSpace(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundPredicate notEqualSpace(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundPredicate likeSpace(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundPredicate notLikeSpace(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundPredicate inSpace(TeNamedOperator<SqlField> namedOperator, int size);

    CompoundPredicate notInSpace(TeNamedOperator<SqlField> namedOperator, int size);

    CompoundExpression modSpace(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundExpression plusSpace(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundExpression minusSpace(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundExpression timesSpace(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundExpression divideSpace(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundExpression bitwiseAndSpace(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundExpression bitwiseOrSpace(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundExpression xorSpace(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundExpression rightShiftSpace(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundExpression leftShiftSpace(BiFunction<SqlField, String, Expression> namedOperator);


}
