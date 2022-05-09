package io.army.criteria;

import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;

public interface Delete extends NarrowDmlStatement {


    interface _DeleteSpec {

        Delete asDelete();
    }

    interface _SingleDeleteClause<DT> {

        DT deleteFrom(SingleTableMeta<?> table, String alias);
    }


    interface StandardDeleteClause<DR> {

        DR deleteFrom(TableMeta<?> table, String tableAlias);
    }


    interface StandardDeleteSpec<C> extends Delete.StandardDeleteClause<Delete.StandardWhereSpec<C>> {

    }


    interface StandardWhereSpec<C> extends _WhereClause<C, _DeleteSpec, StandardWhereAndSpec<C>> {

    }

    interface StandardWhereAndSpec<C> extends _WhereAndClause<C, StandardWhereAndSpec<C>>
            , _DeleteSpec {

    }




    /*################################## blow batch delete ##################################*/

    interface StandardBatchDeleteSpec<C> extends Delete.StandardDeleteClause<Delete.StandardBatchWhereSpec<C>> {

    }


    interface StandardBatchWhereSpec<C>
            extends _WhereClause<C, _BatchParamClause<C, _DeleteSpec>, StandardBatchWhereAndSpec<C>> {

    }

    interface StandardBatchWhereAndSpec<C> extends _WhereAndClause<C, StandardBatchWhereAndSpec<C>>
            , _BatchParamClause<C, _DeleteSpec> {

    }


}
