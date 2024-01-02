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
import io.army.criteria.TableField;
import io.army.criteria.UpdateStatement;
import io.army.criteria.impl.SQLs;
import io.army.meta.*;

/**
 * <p>
 * This interface representing standard update statement.
 * * @since 0.6.0
 */
public interface StandardUpdate extends StandardStatement {


    interface _WhereAndSpec<I extends Item> extends UpdateStatement._UpdateWhereAndClause<_WhereAndSpec<I>>,
            _DmlUpdateSpec<I> {

    }


    interface _StandardWhereClause<I extends Item> extends _WhereClause<_DmlUpdateSpec<I>, _WhereAndSpec<I>> {

    }


    interface _StandardSetClause<I extends Item, F extends TableField>
            extends UpdateStatement._StaticBatchSetClause<F, _WhereSpec<I, F>>,
            UpdateStatement._DynamicSetClause<UpdateStatement._BatchItemPairs<F>, _StandardWhereClause<I>> {

    }

    interface _WhereSpec<I extends Item, F extends TableField> extends _StandardSetClause<I, F>,
            _StandardWhereClause<I> {


    }

    interface _SingleUpdateClause<I extends Item> extends Item {

        <T> _StandardSetClause<I, FieldMeta<T>> update(SingleTableMeta<T> table, SQLs.WordAs as, String tableAlias);

        <P> _StandardSetClause<I, FieldMeta<P>> update(ComplexTableMeta<P, ?> table, SQLs.WordAs as, String tableAlias);


    }

    interface _WithSpec<I extends Item> extends _StandardDynamicWithClause<_SingleUpdateClause<I>>,
            _StandardStaticWithClause<_SingleUpdateClause<I>>,
            _SingleUpdateClause<I> {

    }

    interface _DomainUpdateClause<I extends Item> extends Item {

        _StandardSetClause<I, FieldMeta<?>> update(TableMeta<?> table, String tableAlias);

        <T> _StandardSetClause<I, FieldMeta<T>> update(SingleTableMeta<T> table, SQLs.WordAs as, String tableAlias);

        <T> _StandardSetClause<I, FieldMeta<? super T>> update(ChildTableMeta<T> table, SQLs.WordAs as, String tableAlias);

    }


}
