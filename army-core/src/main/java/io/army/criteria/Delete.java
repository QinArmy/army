package io.army.criteria;

import io.army.domain.IDomain;
import io.army.meta.TableMeta;

public interface Delete extends NarrowDmlStatement {


    interface DeleteSpec {

        Delete asDelete();
    }

    interface StandardDeleteClause<DR> {

        DR deleteFrom(TableMeta<? extends IDomain> table, String tableAlias);
    }


    interface StandardDeleteSpec<C> extends Delete.StandardDeleteClause<Delete.StandardWhereSpec<C>> {

    }


    interface StandardWhereSpec<C> extends Statement.WhereClause<C, Delete.DeleteSpec, Delete.StandardWhereAndSpec<C>> {

    }

    interface StandardWhereAndSpec<C> extends Statement.WhereAndClause<C, Delete.StandardWhereAndSpec<C>>
            , Delete.DeleteSpec {

    }




    /*################################## blow batch delete ##################################*/

    interface StandardBatchDeleteSpec<C> extends Delete.StandardDeleteClause<Delete.StandardBatchWhereSpec<C>> {

    }


    interface StandardBatchWhereSpec<C>
            extends Statement.WhereClause<C, Statement.BatchParamClause<C, Delete.DeleteSpec>, Delete.StandardBatchWhereAndSpec<C>> {

    }

    interface StandardBatchWhereAndSpec<C> extends Statement.WhereAndClause<C, Delete.StandardBatchWhereAndSpec<C>>
            , Statement.BatchParamClause<C, Delete.DeleteSpec> {

    }


}
