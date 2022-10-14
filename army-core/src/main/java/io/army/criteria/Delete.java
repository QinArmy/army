package io.army.criteria;

import io.army.meta.ChildTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.SimpleTableMeta;

public interface Delete extends NarrowDmlStatement, DmlStatement.DmlDelete {


    @Deprecated
    interface _DeleteSpec extends DmlStatement._DmlDeleteSpec<Delete> {

    }

    interface _DeleteSimpleClause<DT> {

        DT deleteFrom(SimpleTableMeta<?> table, String alias);

    }

    interface _DeleteParentClause<DT> {

        DT deleteFrom(ParentTableMeta<?> table, String alias);

    }

    interface _DeleteChildClause<DT> {

        DT deleteFrom(ChildTableMeta<?> table, String alias);

    }




    /*################################## blow batch delete ##################################*/


}
