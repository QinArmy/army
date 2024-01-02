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

/**
 * <p>
 * This interface is base interface of below:
 * <ul>
 *     <li>{@link Expression}</li>
 *     <li>{@link RowExpression}</li>
 * </ul>
 *
 * @since 0.6.0
 */
public interface SQLExpression extends RowElement, RightOperand {


    /**
     * <p>
     * <strong>= ANY</strong> operator
     *
     */
    CompoundPredicate equalAny(SubQuery subQuery);

    /**
     * <p>
     * <strong>= SOME</strong> operator
     *
     */
    CompoundPredicate equalSome(SubQuery subQuery);

    CompoundPredicate equalAll(SubQuery subQuery);

    CompoundPredicate notEqualAny(SubQuery subQuery);

    CompoundPredicate notEqualSome(SubQuery subQuery);

    CompoundPredicate notEqualAll(SubQuery subQuery);

    CompoundPredicate lessAny(SubQuery subQuery);

    CompoundPredicate lessSome(SubQuery subQuery);

    CompoundPredicate lessAll(SubQuery subQuery);

    CompoundPredicate lessEqualAny(SubQuery subQuery);

    CompoundPredicate lessEqualSome(SubQuery subQuery);

    CompoundPredicate lessEqualAll(SubQuery subQuery);

    CompoundPredicate greaterAny(SubQuery subQuery);

    CompoundPredicate greaterSome(SubQuery subQuery);

    CompoundPredicate greaterAll(SubQuery subQuery);

    CompoundPredicate greaterEqualAny(SubQuery subQuery);

    CompoundPredicate greaterEqualSome(SubQuery subQuery);

    CompoundPredicate greaterEqualAll(SubQuery subQuery);


    CompoundPredicate in(SQLColumnSet row);

    CompoundPredicate notIn(SQLColumnSet row);


}
