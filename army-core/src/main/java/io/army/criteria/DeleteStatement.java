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

import io.army.criteria.impl.SQLs;
import io.army.meta.ChildTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.SingleTableMeta;

/**
 * <p>
 * This interface is base interface of below:
 * <ul>
 *     <li>{@link Delete}</li>
 *     <li>{@link BatchDelete}</li>
 *     <li>{@link io.army.criteria.dialect.ReturningDelete}</li>
 *     <li>{@link io.army.criteria.dialect.BatchReturningDelete}</li>
 * </ul>
 *
 * @since 0.6.0
 */
public interface DeleteStatement extends NarrowDmlStatement {


    @Deprecated
    interface _DeleteSpec extends DmlStatement._DmlDeleteSpec<DeleteStatement> {

    }

    /**
     * <p>
     * This interface representing FROM clause for single-table DELETE syntax.
     *
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * /
     *
     * @param <DT> next clause java type
     * @since 0.6.0
     */
    interface _SingleDeleteFromClause<DT> {

        DT from(SingleTableMeta<?> table, SQLs.WordAs wordAs, String alias);

    }

    interface _SingleDeleteClause<DT> extends Item {

        DT deleteFrom(SingleTableMeta<?> table, SQLs.WordAs wordAs, String alias);

    }

    interface _DeleteParentClause<DT> {

        DT deleteFrom(ParentTableMeta<?> table, String alias);

    }

    interface _DeleteChildClause<DT> {

        DT deleteFrom(ChildTableMeta<?> table, String alias);

    }




    /*################################## blow batch delete ##################################*/


}
