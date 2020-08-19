package io.army.criteria;

import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Insert extends SQLStatement, SQLDebug {


    /*################################## blow interfaces  ##################################*/

    interface InsertSQLSpec {

    }

    interface InsertSpec extends InsertSQLSpec {

        Insert asInsert();
    }

    /*################################## blow multiInsert interfaces ##################################*/

    interface InsertOptionSpec<T extends IDomain> extends InsertIntoSpec<T> {

        InsertOptionSpec<T> dataMigration();
    }

    interface InsertIntoSpec<T extends IDomain> extends InsertSQLSpec {

        InsertValuesSpec<T> insertInto(Collection<FieldMeta<? super T, ?>> fieldMetaList);

        InsertValuesSpec<T> insertInto(Supplier<Collection<FieldMeta<? super T, ?>>> fieldMetaList);

        InsertValuesSpec<T> insertInto(TableMeta<T> tableMeta);
    }

    interface InsertValuesSpec<T extends IDomain> extends InsertSQLSpec {

        InsertSpec value(T domain);

        InsertSpec values(List<T> domainList);
    }



    /*################################## blow subQuery insert interfaces ##################################*/

    interface SubQueryTargetFieldSpec<T extends IDomain, C> extends InsertSQLSpec {

        SimpleTableRouteSpec<C> insertInto(List<FieldMeta<T, ?>> fieldMetaList);

        SimpleTableRouteSpec<C> insertInto(Function<C, List<FieldMeta<T, ?>>> function);
    }

    interface SimpleTableRouteSpec<C> extends SubQueryValueSpec<C> {

        SubQueryValueSpec<C> route(int databaseIndex, int tableIndex);

        SubQueryValueSpec<C> route(int tableIndex);
    }

    interface SubQueryValueSpec<C> extends InsertSQLSpec {

        InsertSpec subQuery(Function<C, SubQuery> function);
    }

    /*################################## blow child sub query insert interfaces ##################################*/

    interface ParentSubQueryTargetFieldSpec<T extends IDomain, C> extends InsertSQLSpec {

        ParentTableRouteSpec<T, C> parentFields(List<FieldMeta<T, ?>> fieldMetaList);

        ParentTableRouteSpec<T, C> parentFields(Function<C, List<FieldMeta<T, ?>>> function);

    }

    interface ParentTableRouteSpec<T extends IDomain, C> extends ParentSubQuerySpec<T, C> {

        ParentSubQuerySpec<T, C> route(int databaseIndex, int tableIndex);

        ParentSubQuerySpec<T, C> route(int tableIndex);
    }

    interface ChildSubQueryTargetFieldSpec<T extends IDomain, C> extends InsertSQLSpec {

        ChildSubQuerySpec<C> childFields(List<FieldMeta<T, ?>> fieldMetaList);

        ChildSubQuerySpec<C> childFields(Function<C, List<FieldMeta<T, ?>>> function);

    }

    interface ParentSubQuerySpec<T extends IDomain, C> extends InsertSQLSpec {

        ChildSubQueryTargetFieldSpec<T, C> parentSubQuery(Function<C, SubQuery> function);
    }

    interface ChildSubQuerySpec<C> extends InsertSQLSpec {

        InsertSpec childSubQuery(Function<C, SubQuery> function);
    }


}
