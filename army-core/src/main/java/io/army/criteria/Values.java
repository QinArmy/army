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

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * This interface representing VALUES statement
 *
 * @since 0.6.0
 */
public interface Values extends DqlStatement, DialectStatement, ValuesQuery {


    interface _StaticValueRowCommaDualSpec<RR> extends Statement._RightParenClause<RR> {

        Statement._RightParenClause<RR> comma(Expression exp);

        _StaticValueRowCommaDualSpec<RR> comma(Expression exp1, Expression exp2);


        Statement._RightParenClause<RR> comma(Function<Object, Expression> valueOperator, Object value);

        _StaticValueRowCommaDualSpec<RR> comma(Function<Object, Expression> valueOperator, Object value1, Object value2);

    }

    interface _StaticValueRowCommaQuadraSpec<RR> extends Statement._RightParenClause<RR> {

        Statement._RightParenClause<RR> comma(Expression exp);

        Statement._RightParenClause<RR> comma(Expression exp1, Expression exp2);

        Statement._RightParenClause<RR> comma(Expression exp1, Expression exp2, Expression exp3);

        _StaticValueRowCommaQuadraSpec<RR> comma(Expression exp1, Expression exp2, Expression exp3, Expression exp4);

        Statement._RightParenClause<RR> comma(Function<Object, Expression> valueOperator, Object value);

        Statement._RightParenClause<RR> comma(Function<Object, Expression> valueOperator, Object value1, Object value2);

        Statement._RightParenClause<RR> comma(Function<Object, Expression> valueOperator, Object value1, Object value2, Object value3);

        _StaticValueRowCommaQuadraSpec<RR> comma(Function<Object, Expression> valueOperator, Object value1, Object value2, Object value3, Object value4);

    }


    interface _StaticValueLeftParenClause<RR> {

        Statement._RightParenClause<RR> leftParen(Expression exp);

        _StaticValueRowCommaDualSpec<RR> leftParen(Expression exp1, Expression exp2);

        Statement._RightParenClause<RR> leftParen(Expression exp1, Expression exp2, Expression exp3);

        _StaticValueRowCommaQuadraSpec<RR> leftParen(Expression exp1, Expression exp2, Expression exp3, Expression exp4);

        Statement._RightParenClause<RR> leftParen(Function<Object, Expression> valueOperator, Object value);

        _StaticValueRowCommaDualSpec<RR> leftParen(Function<Object, Expression> valueOperator, Object value1, Object value2);

        Statement._RightParenClause<RR> leftParen(Function<Object, Expression> valueOperator, Object value1, Object value2, Object value3);

        _StaticValueRowCommaQuadraSpec<RR> leftParen(Function<Object, Expression> valueOperator, Object value1, Object value2, Object value3, Object value4);

    }


    interface _DynamicValuesClause<VR> {

        VR values(Consumer<ValuesRowConstructor> consumer);

    }

    interface _StaticValuesClause<VR> extends Item {

        VR values();
    }


}
