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

package io.army.criteria.dialect;

import io.army.criteria.DefiniteExpression;
import io.army.criteria.Expression;
import io.army.criteria.SimpleExpression;

import java.util.function.BiFunction;

public interface VarExpression extends DefiniteExpression {

    /**
     * @return session variable name
     */
    String name();


    SimpleExpression increment();

    SimpleExpression assignment(Expression value);

    SimpleExpression plusEqual(Expression value);

    SimpleExpression minusEqual(Expression value);

    SimpleExpression timesEqual(Expression value);

    SimpleExpression divideEqual(Expression value);

    SimpleExpression modeEqual(Expression value);


    /*-------------------below -------------------*/

    <T> SimpleExpression assignment(BiFunction<SimpleExpression, T, Expression> funcRef, T value);

    <T> SimpleExpression plusEqual(BiFunction<SimpleExpression, T, Expression> funcRef, T value);

    <T> SimpleExpression minusEqual(BiFunction<SimpleExpression, T, Expression> funcRef, T value);

    <T> SimpleExpression timesEqual(BiFunction<SimpleExpression, T, Expression> funcRef, T value);

    <T> SimpleExpression divideEqual(BiFunction<SimpleExpression, T, Expression> funcRef, T value);

    <T> SimpleExpression modeEqual(BiFunction<SimpleExpression, T, Expression> funcRef, T value);


}
