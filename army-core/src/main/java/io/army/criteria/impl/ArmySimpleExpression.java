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

package io.army.criteria.impl;

import io.army.criteria.standard.SQLs;

/**
 * <p>
 * Package interface. This interface is base interface of below:
 *     <ul>
 *         <li>{@link OperationExpression.OperationSimpleExpression}</li>
 *         <li>{@link OperationExpression.BracketsExpression}</li>
 *         <li>{@link ArmyParamExpression}</li>
 *         <li>{@link ArmyLiteralExpression}</li>
 *         <li>{@link SQLs#NULL}</li>
 *         <li>{@link SQLs#TRUE}</li>
 *         <li>{@link SQLs#FALSE}</li>
 *         <li>{@link  OperationPredicate.OperationSimplePredicate}</li>
 *     </ul>
 ** @since 0.6.0
 */
interface ArmySimpleExpression extends ArmyExpression, ArmySimpleSQLExpression {


}
