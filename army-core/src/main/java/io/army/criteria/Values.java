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

import io.army.criteria.standard.SQLs;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * <p>
 * This interface representing VALUES statement
 *
 * @since 0.6.0
 */
public interface Values extends DqlStatement, DialectStatement, ValuesQuery {


    interface _ValueStaticColumnDualCommaClause extends Item {


        /**
         * @param exp nullable, one of following :
         *            <ul>
         *              <li>null</li>
         *              <li>{@link Expression} instance,but {@link SQLs#DEFAULT}</li>
         *              <li>literal</li>
         *            </ul>
         * @throws CriteriaException throw when exp error.
         */
        Item comma(@Nullable Object exp);

        /**
         * @param exp1 nullable, one of following :
         *             <ul>
         *               <li>null</li>
         *              <li>{@link Expression} instance,but {@link SQLs#DEFAULT}</li>
         *               <li>literal</li>
         *             </ul>
         * @param exp2 nullable, one of following :
         *             <ul>
         *               <li>null</li>
         *              <li>{@link Expression} instance,but {@link SQLs#DEFAULT}</li>
         *               <li>literal</li>
         *             </ul>
         * @throws CriteriaException throw when exp error.
         */
        _ValueStaticColumnDualCommaClause comma(@Nullable Object exp1, @Nullable Object exp2);

    }


    interface _ValueStaticColumnQuadraCommaClause extends _ValueStaticColumnDualCommaClause {


        /**
         * @param exp1 nullable, one of following :
         *             <ul>
         *               <li>null</li>
         *              <li>{@link Expression} instance,but {@link SQLs#DEFAULT}</li>
         *               <li>literal</li>
         *             </ul>
         * @param exp2 nullable, one of following :
         *             <ul>
         *               <li>null</li>
         *              <li>{@link Expression} instance,but {@link SQLs#DEFAULT}</li>
         *               <li>literal</li>
         *             </ul>
         * @param exp3 nullable, one of following :
         *             <ul>
         *               <li>null</li>
         *              <li>{@link Expression} instance,but {@link SQLs#DEFAULT}</li>
         *               <li>literal</li>
         *             </ul>
         * @throws CriteriaException throw when exp error.
         */
        Item comma(@Nullable Object exp1, @Nullable Object exp2, @Nullable Object exp3);

        /**
         * @param exp1 nullable, one of following :
         *             <ul>
         *               <li>null</li>
         *              <li>{@link Expression} instance,but {@link SQLs#DEFAULT}</li>
         *               <li>literal</li>
         *             </ul>
         * @param exp2 nullable, one of following :
         *             <ul>
         *               <li>null</li>
         *              <li>{@link Expression} instance,but {@link SQLs#DEFAULT}</li>
         *               <li>literal</li>
         *             </ul>
         * @param exp3 nullable, one of following :
         *             <ul>
         *               <li>null</li>
         *              <li>{@link Expression} instance,but {@link SQLs#DEFAULT}</li>
         *               <li>literal</li>
         *             </ul>
         * @param exp4 nullable, one of following :
         *             <ul>
         *               <li>null</li>
         *              <li>{@link Expression} instance,but {@link SQLs#DEFAULT}</li>
         *               <li>literal</li>
         *             </ul>
         * @throws CriteriaException throw when exp error.
         */
        _ValueStaticColumnQuadraCommaClause comma(@Nullable Object exp1, Object exp2, @Nullable Object exp3, @Nullable Object exp4);

    }


    interface _ValueStaticColumnOctupleCommaClause extends _ValueStaticColumnQuadraCommaClause {

        _ValueStaticColumnOctupleCommaClause comma(@Nullable Object exp1, Object exp2, @Nullable Object exp3,
                                                   @Nullable Object exp4, @Nullable Object exp5);

        _ValueStaticColumnOctupleCommaClause comma(@Nullable Object exp1, Object exp2, @Nullable Object exp3,
                                                   @Nullable Object exp4, @Nullable Object exp5, @Nullable Object exp6);

        _ValueStaticColumnOctupleCommaClause comma(@Nullable Object exp1, Object exp2, @Nullable Object exp3,
                                                   @Nullable Object exp4, @Nullable Object exp5, @Nullable Object exp6,
                                                   @Nullable Object exp7);

        _ValueStaticColumnOctupleCommaClause comma(@Nullable Object exp1, Object exp2, @Nullable Object exp3,
                                                   @Nullable Object exp4, @Nullable Object exp5, @Nullable Object exp6,
                                                   @Nullable Object exp7, @Nullable Object exp8);


    }


    interface _ValueStaticColumnSpaceClause {


        /**
         * @param exp nullable, one of following :
         *            <ul>
         *              <li>null</li>
         *              <li>{@link Expression} instance,but {@link SQLs#DEFAULT}</li>
         *              <li>literal</li>
         *            </ul>
         * @throws CriteriaException throw when exp error.
         */
        Item space(@Nullable Object exp);

        /**
         * @param exp1 nullable, one of following :
         *             <ul>
         *               <li>null</li>
         *              <li>{@link Expression} instance,but {@link SQLs#DEFAULT}</li>
         *               <li>literal</li>
         *             </ul>
         * @param exp2 nullable, one of following :
         *             <ul>
         *               <li>null</li>
         *              <li>{@link Expression} instance,but {@link SQLs#DEFAULT}</li>
         *               <li>literal</li>
         *             </ul>
         * @throws CriteriaException throw when exp error.
         */
        _ValueStaticColumnDualCommaClause space(@Nullable Object exp1, @Nullable Object exp2);

        /**
         * @param exp1 nullable, one of following :
         *             <ul>
         *               <li>null</li>
         *              <li>{@link Expression} instance,but {@link SQLs#DEFAULT}</li>
         *               <li>literal</li>
         *             </ul>
         * @param exp2 nullable, one of following :
         *             <ul>
         *               <li>null</li>
         *              <li>{@link Expression} instance,but {@link SQLs#DEFAULT}</li>
         *               <li>literal</li>
         *             </ul>
         * @param exp3 nullable, one of following :
         *             <ul>
         *               <li>null</li>
         *              <li>{@link Expression} instance,but {@link SQLs#DEFAULT}</li>
         *               <li>literal</li>
         *             </ul>
         * @throws CriteriaException throw when exp error.
         */
        Item space(@Nullable Object exp1, @Nullable Object exp2, @Nullable Object exp3);

        /**
         * @param exp1 nullable, one of following :
         *             <ul>
         *               <li>null</li>
         *              <li>{@link Expression} instance,but {@link SQLs#DEFAULT}</li>
         *               <li>literal</li>
         *             </ul>
         * @param exp2 nullable, one of following :
         *             <ul>
         *               <li>null</li>
         *              <li>{@link Expression} instance,but {@link SQLs#DEFAULT}</li>
         *               <li>literal</li>
         *             </ul>
         * @param exp3 nullable, one of following :
         *             <ul>
         *               <li>null</li>
         *              <li>{@link Expression} instance,but {@link SQLs#DEFAULT}</li>
         *               <li>literal</li>
         *             </ul>
         * @param exp4 nullable, one of following :
         *             <ul>
         *               <li>null</li>
         *              <li>{@link Expression} instance,but {@link SQLs#DEFAULT}</li>
         *               <li>literal</li>
         *             </ul>
         * @throws CriteriaException throw when exp error.
         */
        _ValueStaticColumnQuadraCommaClause space(@Nullable Object exp1, Object exp2, @Nullable Object exp3, @Nullable Object exp4);


        _ValueStaticColumnOctupleCommaClause space(@Nullable Object exp1, Object exp2, @Nullable Object exp3,
                                                   @Nullable Object exp4, @Nullable Object exp5);

        _ValueStaticColumnOctupleCommaClause space(@Nullable Object exp1, Object exp2, @Nullable Object exp3,
                                                   @Nullable Object exp4, @Nullable Object exp5, @Nullable Object exp6);

        _ValueStaticColumnOctupleCommaClause space(@Nullable Object exp1, Object exp2, @Nullable Object exp3,
                                                   @Nullable Object exp4, @Nullable Object exp5, @Nullable Object exp6,
                                                   @Nullable Object exp7);

        _ValueStaticColumnOctupleCommaClause space(@Nullable Object exp1, Object exp2, @Nullable Object exp3,
                                                   @Nullable Object exp4, @Nullable Object exp5, @Nullable Object exp6,
                                                   @Nullable Object exp7, @Nullable Object exp8);

    }


    interface _ValuesDynamicColumnClause {


        /**
         * @param exp nullable, one of following :
         *            <ul>
         *              <li>null</li>
         *              <li>{@link Expression} instance,but {@link SQLs#DEFAULT}</li>
         *              <li>literal</li>
         *            </ul>
         * @throws CriteriaException throw when exp error.
         */
        _ValuesDynamicColumnClause column(@Nullable Object exp);

        /**
         * @param exp1 nullable, one of following :
         *             <ul>
         *               <li>null</li>
         *              <li>{@link Expression} instance,but {@link SQLs#DEFAULT}</li>
         *               <li>literal</li>
         *             </ul>
         * @param exp2 nullable, one of following :
         *             <ul>
         *               <li>null</li>
         *              <li>{@link Expression} instance,but {@link SQLs#DEFAULT}</li>
         *               <li>literal</li>
         *             </ul>
         * @throws CriteriaException throw when exp error.
         */
        _ValuesDynamicColumnClause column(@Nullable Object exp1, @Nullable Object exp2);

        /**
         * @param exp1 nullable, one of following :
         *             <ul>
         *               <li>null</li>
         *              <li>{@link Expression} instance,but {@link SQLs#DEFAULT}</li>
         *               <li>literal</li>
         *             </ul>
         * @param exp2 nullable, one of following :
         *             <ul>
         *               <li>null</li>
         *              <li>{@link Expression} instance,but {@link SQLs#DEFAULT}</li>
         *               <li>literal</li>
         *             </ul>
         * @param exp3 nullable, one of following :
         *             <ul>
         *               <li>null</li>
         *              <li>{@link Expression} instance,but {@link SQLs#DEFAULT}</li>
         *               <li>literal</li>
         *             </ul>
         * @throws CriteriaException throw when exp error.
         */
        _ValuesDynamicColumnClause column(@Nullable Object exp1, @Nullable Object exp2, @Nullable Object exp3);

        /**
         * @param exp1 nullable, one of following :
         *             <ul>
         *               <li>null</li>
         *              <li>{@link Expression} instance,but {@link SQLs#DEFAULT}</li>
         *               <li>literal</li>
         *             </ul>
         * @param exp2 nullable, one of following :
         *             <ul>
         *               <li>null</li>
         *              <li>{@link Expression} instance,but {@link SQLs#DEFAULT}</li>
         *               <li>literal</li>
         *             </ul>
         * @param exp3 nullable, one of following :
         *             <ul>
         *               <li>null</li>
         *              <li>{@link Expression} instance,but {@link SQLs#DEFAULT}</li>
         *               <li>literal</li>
         *             </ul>
         * @param exp4 nullable, one of following :
         *             <ul>
         *               <li>null</li>
         *              <li>{@link Expression} instance,but {@link SQLs#DEFAULT}</li>
         *               <li>literal</li>
         *             </ul>
         * @throws CriteriaException throw when exp error.
         */
        _ValuesDynamicColumnClause column(@Nullable Object exp1, Object exp2, @Nullable Object exp3, @Nullable Object exp4);

        _ValuesDynamicColumnClause column(@Nullable Object exp1, Object exp2, @Nullable Object exp3,
                                          @Nullable Object exp4, @Nullable Object exp5);

        _ValuesDynamicColumnClause column(@Nullable Object exp1, Object exp2, @Nullable Object exp3,
                                          @Nullable Object exp4, @Nullable Object exp5, @Nullable Object exp6);

        _ValuesDynamicColumnClause column(@Nullable Object exp1, Object exp2, @Nullable Object exp3,
                                          @Nullable Object exp4, @Nullable Object exp5, @Nullable Object exp6,
                                          @Nullable Object exp7);

        _ValuesDynamicColumnClause column(@Nullable Object exp1, Object exp2, @Nullable Object exp3,
                                          @Nullable Object exp4, @Nullable Object exp5, @Nullable Object exp6,
                                          @Nullable Object exp7, @Nullable Object exp8);

    }


    interface _ValuesRowParensClause<R> {

        R parens(Consumer<_ValueStaticColumnSpaceClause> consumer);

        R parens(SQLs.SymbolSpace space, Consumer<_ValuesDynamicColumnClause> consumer);

    }


    interface _ValuesRowClause<R> {

        R row(Consumer<_ValueStaticColumnSpaceClause> consumer);

        R row(SQLs.SymbolSpace space, Consumer<_ValuesDynamicColumnClause> consumer);

    }


    interface _DynamicValuesRowParensClause<R> {

        R values(Consumer<ValuesParens> consumer);

    }


    interface _DynamicValuesRowClause<VR> {

        VR values(Consumer<ValuesRows> consumer);

    }

    interface _StaticValuesClause<VR> extends Item {

        VR values();
    }


}
