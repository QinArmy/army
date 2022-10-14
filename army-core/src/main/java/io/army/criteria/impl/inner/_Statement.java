package io.army.criteria.impl.inner;

import io.army.criteria.SelectItem;
import io.army.criteria.SortItem;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.List;

public interface _Statement {

    void clear();

    interface _PredicateListSpec {

        List<_Predicate> predicateList();
    }

    interface _OrderByListSpec {

        List<? extends SortItem> orderByList();
    }

    interface _ReturningListSpec {

        List<? extends SelectItem> returningList();
    }

    interface _RowCountSpec {

        @Nullable
        _Expression rowCount();

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


}
