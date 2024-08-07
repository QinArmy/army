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

package io.army.function;

import io.army.criteria.CompoundPredicate;
import io.army.criteria.Expression;
import io.army.criteria.SimpleExpression;
import io.army.lang.Nullable;

import java.util.function.BiFunction;

@FunctionalInterface
public interface DialectBooleanOperator<T> {


    CompoundPredicate apply(BiFunction<SimpleExpression, Expression, CompoundPredicate> operator, BiFunction<SimpleExpression, T, Expression> funcRef, @Nullable T value);

}
