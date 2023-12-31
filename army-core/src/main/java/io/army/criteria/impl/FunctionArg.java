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


/**
 * <p>
 * Package interface . This interface representing legal sql function argument.
 * This interface is base interface of below:
 * <ul>
 *     <li>{@link SingleFunctionArg}</li>
 * </ul>
 *
 * @since 0.6.0
 */
interface FunctionArg extends ArmySQLExpression {

    /**
     * <p>
     * This interface representing legal sql function argument.
     * This interface is base interface of below:
     *     <ul>
     *         <li>{@link OperationExpression}</li>
     *         <li>{@link SQLs#_ASTERISK_EXP}</li>
     *         <li>{@link ArmyRowExpression}</li>
     *     </ul>
     *
     *
     * @since 0.6.0
     */
    interface SingleFunctionArg extends FunctionArg {

    }

}
