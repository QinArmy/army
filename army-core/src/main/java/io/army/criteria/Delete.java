package io.army.criteria;

import io.army.meta.SingleTableMeta;

public interface Delete extends NarrowDmlStatement, DmlStatement.DmlDelete {


    @Deprecated
    interface _DeleteSpec extends DmlStatement._DmlDeleteSpec<Delete> {

    }

    interface _SingleDeleteClause<DT> {

        DT deleteFrom(SingleTableMeta<?> table, String alias);
    }




    /*################################## blow batch delete ##################################*/


}
