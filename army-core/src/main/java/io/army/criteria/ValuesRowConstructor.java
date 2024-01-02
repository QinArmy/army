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

import java.util.function.Function;

/**
 * <p>
 * This interface representing ROW constructor of VALUES statement.
 * * @since 0.6.0
 */
public interface ValuesRowConstructor {

    ValuesRowConstructor column(Expression exp);

    ValuesRowConstructor column(Expression exp1, Expression exp2);

    ValuesRowConstructor column(Expression exp1, Expression exp2, Expression exp3);

    ValuesRowConstructor column(Expression exp1, Expression exp2, Expression exp3, Expression exp4);

    ValuesRowConstructor column(Function<Object, Expression> valueOperator, Object value);

    ValuesRowConstructor column(Function<Object, Expression> valueOperator, Object value1, Object value2);

    ValuesRowConstructor column(Function<Object, Expression> valueOperator, Object value1, Object value2, Object value3);

    ValuesRowConstructor column(Function<Object, Expression> valueOperator, Object value1, Object value2, Object value3, Object value4);


    ValuesRowConstructor row();

}
