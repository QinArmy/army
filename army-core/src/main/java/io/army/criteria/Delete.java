package io.army.criteria;

import io.army.domain.IDomain;
import io.army.meta.TableMeta;

public interface Delete extends Dml, SQLDebug {


    interface DeleteSpec {

        Delete asDelete();
    }

    interface StandardDeleteSpec<C> {

        StandardWhereSpec<C> deleteFrom(TableMeta<? extends IDomain> table, String tableAlias);
    }


    interface StandardWhereSpec<C> extends Statement.WhereClause<C, Delete.DeleteSpec, Delete.StandardWhereAndSpec<C>> {

    }

    interface StandardWhereAndSpec<C> extends Statement.WhereAndClause<C, Delete.StandardWhereAndSpec<C>>
            , Delete.DeleteSpec {

    }





    /*################################## blow batch delete ##################################*/

    interface StandardBatchDeleteSpec<C> {

        StandardBatchWhereSpec<C> deleteFrom(TableMeta<? extends IDomain> table, String tableAlias);
    }

    interface StandardBatchParamSpec<C> extends Statement.BatchParamClause<C, Delete.DeleteSpec> {

    }


    interface StandardBatchWhereSpec<C>
            extends Statement.WhereClause<C, Delete.StandardBatchParamSpec<C>, Delete.StandardBatchWhereAndSpec<C>> {

    }

    interface StandardBatchWhereAndSpec<C> extends Statement.WhereAndClause<C, Delete.StandardBatchWhereAndSpec<C>>
            , StandardBatchParamSpec<C> {

    }


}
