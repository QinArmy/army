package io.army.criteria.impl.inner;

import io.army.criteria.SortItem;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.List;

public interface _Statement {

    void clear();

    interface _WherePredicateListSpec {

        List<_Predicate> wherePredicateList();
    }

    interface _OrderByListSpec {

        List<? extends SortItem> orderByList();
    }

    interface _ReturningListSpec {

        /**
         * @throws UnsupportedOperationException throw when this isn't instance of {@link _ReturningDml}
         */
        List<? extends _Selection> returningList();
    }

    interface _RowCountSpec {

        @Nullable
        _Expression rowCount();

    }

    interface _LimitClauseSpec extends _RowCountSpec {

        @Nullable
        _Expression offset();

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
        List<_TabularBock> tableBlockList();

    }

    interface _ChildStatement {

    }


}
