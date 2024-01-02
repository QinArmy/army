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
 * This interface The statement that can present in UNION clause,this interface is base interface of below:
 * <ul>
 *     <li>{@link Query}</li>
 *     <li>{@link ValuesQuery}</li>
 * </ul>
 *
 * @since 0.6.0
 */
public interface RowSet extends Statement {

    @Deprecated
    interface _RowSetSpec<R extends Item> {

        R asQuery();

    }

    interface _StaticUnionClause<R> {

        R union();

        R unionAll();

        R unionDistinct();
    }

    interface _DynamicUnionParensClause<T extends Item, R extends Item> {

        R unionParens(Consumer<T> consumer);

        R ifUnionParens(Consumer<T> consumer);

    }

    interface _StaticIntersectClause<R> {

        R intersect();

        R intersectAll();

        R intersectDistinct();
    }


    interface _StaticExceptClause<SP> {

        SP except();

        SP exceptAll();

        SP exceptDistinct();
    }


    interface _StaticMinusClause<SP> {

        SP minus();

        SP minusAll();

        SP minusDistinct();
    }


    interface _DynamicParensRowSetClause<T extends Item, R extends Item> {

        R parens(Function<T, R> function);

    }


}
