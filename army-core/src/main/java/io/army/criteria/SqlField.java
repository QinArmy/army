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


    CompoundPredicate spaceEqual(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundPredicate spaceLess(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundPredicate spaceLessEqual(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundPredicate spaceGreater(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundPredicate spaceGreaterEqual(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundPredicate spaceNotEqual(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundPredicate spaceLike(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundPredicate spaceNotLike(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundPredicate spaceIn(TeNamedOperator<SqlField> namedOperator, int size);

    CompoundPredicate spaceNotIn(TeNamedOperator<SqlField> namedOperator, int size);

    CompoundExpression spaceMod(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundExpression spacePlus(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundExpression spaceMinus(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundExpression spaceTimes(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundExpression spaceDivide(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundExpression spaceBitwiseAnd(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundExpression spaceBitwiseOr(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundExpression spaceXor(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundExpression spaceRightShift(BiFunction<SqlField, String, Expression> namedOperator);

    CompoundExpression spaceLeftShift(BiFunction<SqlField, String, Expression> namedOperator);


}
