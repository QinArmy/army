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

package io.army.criteria.standard;

import io.army.criteria.Item;
import io.army.criteria.impl.SQLs;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;

public interface StandardDelete extends StandardStatement {

    interface _DeleteFromClause<R> extends Item {

        R deleteFrom(SingleTableMeta<?> table, SQLs.WordAs as, String tableAlias);

    }



    interface _WhereAndSpec<I extends Item> extends _WhereAndClause<_WhereAndSpec<I>>,
            _DmlDeleteSpec<I> {

    }

    interface _WhereSpec<I extends Item>
            extends _WhereClause<_DmlDeleteSpec<I>, _WhereAndSpec<I>> {

    }


    interface _StandardDeleteClause<I extends Item> extends _DeleteFromClause<_WhereSpec<I>> {

    }


    interface _DomainDeleteClause<I extends Item> extends Item {

        _WhereSpec<I> deleteFrom(TableMeta<?> table, SQLs.WordAs as, String tableAlias);

    }




    interface _WithSpec<I extends Item> extends _StandardDynamicWithClause<_StandardDeleteClause<I>>,
            _StandardStaticWithClause<_StandardDeleteClause<I>>,
            _StandardDeleteClause<I> {

    }



}
