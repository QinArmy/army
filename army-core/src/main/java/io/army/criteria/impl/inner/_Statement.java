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

package io.army.criteria.impl.inner;

import io.army.criteria.SQLWords;
import io.army.criteria.SortItem;
import io.army.criteria.Statement;
import io.army.meta.TableMeta;

import javax.annotation.Nullable;
import java.util.List;

public interface _Statement extends Statement {

    void close();

    interface _WherePredicateListSpec {

        List<_Predicate> wherePredicateList();
    }

    interface _OrderByListSpec {

        List<? extends SortItem> orderByList();
    }

    /**
     * This interface representing dialect WITH clause support update/delete, for example postgre.
     */
    interface _WithDmlSpec {

    }

    interface _ReturningListSpec {

        /**
         * @throws UnsupportedOperationException throw when this isn't instance of {@link _ReturningDml}
         */
        List<? extends _SelectItem> returningList();
    }

    interface _RowCountSpec {

        @Nullable
        _Expression rowCountExp();

    }

    interface _LimitClauseSpec extends _RowCountSpec {

        @Nullable
        _Expression offsetExp();

    }

    /**
     * SQL:2008 introduced a different syntax
     */
    interface _SQL2008LimitClauseSpec extends _LimitClauseSpec {

        @Nullable
        SQLWords offsetRowModifier();

        @Nullable
        SQLWords fetchFirstOrNext();


        /**
         * @return row count or percent {@link io.army.criteria.Expression }
         */
        @Override
        _Expression rowCountExp();

        @Nullable
        SQLWords fetchPercentModifier();

        @Nullable
        SQLWords fetchRowModifier();

        @Nullable
        SQLWords fetchOnlyOrWithTies();


    }

    interface _ItemPairList {

        /**
         * @return a unmodifiable list,non-empty
         */
        List<_ItemPair> itemPairList();

    }

    interface _TableMetaSpec {

        TableMeta<?> table();

    }

    interface _WithClauseSpec {

        boolean isRecursive();

        List<_Cte> cteList();

    }


    interface _JoinableStatement extends _Statement {

        /**
         * @return a unmodifiable list
         */
        List<_TabularBlock> tableBlockList();

    }

    interface _ChildStatement {

        TableMeta<?> table();

        _Statement parentStmt();

    }


}
