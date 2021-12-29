package io.army.criteria;

import io.army.domain.IDomain;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Delete extends Dml, SQLDebug {


    @Deprecated
    interface DeleteSpec {

        Delete asDelete();
    }

    interface DomainDeleteSpec<C> {

        WhereSpec<C> deleteFrom(TableMeta<? extends IDomain> table, String tableAlias);
    }




    /*################################## blow batch delete ##################################*/

    interface BatchDomainDeleteSpec<C> {

        BatchWhereSpec<C> deleteFrom(TableMeta<? extends IDomain> table, String tableAlias);
    }


    interface WhereSpec<C> {

        DmlSpec<Delete> where(List<IPredicate> predicateList);

        DmlSpec<Delete> where(Function<C, List<IPredicate>> function);

        DmlSpec<Delete> where(Supplier<List<IPredicate>> supplier);

        WhereAndSpec<C, Delete> where(IPredicate predicate);
    }

    interface BatchWhereSpec<C> {

        BatchParamSpec<C, Delete> where(List<IPredicate> predicateList);

        BatchParamSpec<C, Delete> where(Function<C, List<IPredicate>> function);

        BatchParamSpec<C, Delete> where(Supplier<List<IPredicate>> supplier);

        BatchWhereAndSpec<C, Delete> where(IPredicate predicate);
    }


}
