/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.criteria;

import io.army.function.TeNamedOperator;

import java.util.function.BiFunction;

/**
 * <p>
 * This interface is base interface of below:
 * <ul>
 *     <li>{@link TableField}</li>
 *     <li>{@link DerivedField}</li>
 *     <li>{@link DerivedField}</li>
 * </ul>
 *
 * @since 0.6.0
 */
public interface SqlField extends NamedExpression {

    String fieldName();


    CompoundPredicate equal(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundPredicate less(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundPredicate lessEqual(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundPredicate greater(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundPredicate greaterEqual(BiFunction<SqlField, String, Expression> namedOperator);

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
