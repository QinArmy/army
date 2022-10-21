package io.army.criteria;

import io.army.criteria.impl.SQLs;
import io.army.meta.ChildTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.SimpleTableMeta;
import io.army.meta.SingleTableMeta;

public interface Delete extends NarrowDmlStatement, DmlStatement.DmlDelete {


    @Deprecated
    interface _DeleteSpec extends DmlStatement._DmlDeleteSpec<Delete> {

    }

    /**
     * <p>
     * This interface representing FROM clause for single-table DELETE syntax.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>/
     *
     * @param <DT> next clause java type
     * @since 1.0
     */
    interface _SingleDeleteFromClause<DT> {

        DT from(SingleTableMeta<?> table, SQLs.WordAs wordAs, String alias);

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
