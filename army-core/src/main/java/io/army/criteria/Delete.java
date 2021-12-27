package io.army.criteria;

import io.army.domain.IDomain;
import io.army.meta.TableMeta;

public interface Delete extends Dml, SQLDebug {


    @Deprecated
    interface DeleteSpec {

        Delete asDelete();
    }

    interface DomainDeleteSpec<C> {

        DeleteWhereSpec<C> deleteFrom(TableMeta<? extends IDomain> table, String tableAlias);
    }




    /*################################## blow batch delete ##################################*/

    interface BatchDomainDeleteSpec<C> {

        BatchDeleteWhereSpec<C> deleteFrom(TableMeta<? extends IDomain> table, String tableAlias);
    }







}
