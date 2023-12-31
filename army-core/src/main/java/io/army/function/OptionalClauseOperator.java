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

import io.army.criteria.Expression;
import io.army.criteria.SQLWords;

@FunctionalInterface
public interface OptionalClauseOperator<T1 extends SQLWords, T2, R> {

    R apply(Expression left, Expression right, T1 t1, T2 t2);


}
