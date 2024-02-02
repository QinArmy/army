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

import javax.annotation.Nullable;

public interface Clause extends Item {


    interface _VariadicCommaClause {

        _VariadicCommaClause comma(@Nullable Object exp);

    }

    interface _VariadicSpaceClause {

        _VariadicCommaClause space(@Nullable Object exp);
    }

    interface _VariadicExprCommaClause {

        _VariadicExprCommaClause comma(Expression exp);


        _VariadicExprCommaClause comma(Expression exp1, Expression exp2);

        _VariadicExprCommaClause comma(Expression exp1, Expression exp2, Expression exp3);

        _VariadicExprCommaClause comma(Expression exp1, Expression exp2, Expression exp3, Expression exp4);

    }

    interface _VariadicExprSpaceClause {

        _VariadicExprCommaClause space(Expression exp);

        _VariadicExprCommaClause space(Expression exp1, Expression exp2);

        _VariadicExprCommaClause space(Expression exp1, Expression exp2, Expression exp3);

        _VariadicExprCommaClause space(Expression exp1, Expression exp2, Expression exp3, Expression exp4);

    }

    interface _VariadicConsumer {

        _VariadicConsumer accept(@Nullable Object exp);

    }


    interface _PairVariadicCommaClause {

        _PairVariadicCommaClause comma(String keyName, @Nullable Object value);

        _PairVariadicCommaClause comma(Expression key, @Nullable Object value);

    }

    interface _PairVariadicSpaceClause {

        _PairVariadicCommaClause space(String keyName, @Nullable Object value);

        _PairVariadicCommaClause space(Expression key, @Nullable Object value);

    }

    interface _PairVariadicConsumerClause {

        _PairVariadicConsumerClause accept(String keyName, @Nullable Object value);

        _PairVariadicConsumerClause accept(Expression key, @Nullable Object value);

    }


    interface _StaticStringDualCommaClause extends Item {

        Item comma(String str1);

        _StaticStringDualCommaClause comma(String str1, String str2);

    }

    interface _StaticStringQuadraCommaClause {

        Item comma(String str1);

        Item comma(String str1, String str2);

        Item comma(String str1, String str2, String str3);

        _StaticStringQuadraCommaClause comma(String str1, String str2, String str3, String str4);

    }

    interface _StaticStringSpaceClause {

        Item space(String str1);

        _StaticStringDualCommaClause space(String str1, String str2);

        Item space(String str1, String str2, String str3);

        _StaticStringQuadraCommaClause space(String str1, String str2, String str3, String str4);


    }


}
