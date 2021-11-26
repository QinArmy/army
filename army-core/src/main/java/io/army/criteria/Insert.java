package io.army.criteria;

import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Insert extends Statement, SQLDebug {


    /*################################## blow interfaces  ##################################*/

    interface InsertSqlSpec {

    }

    interface InsertSpec extends InsertSqlSpec {

        Insert asInsert();

        Insert asInsert(Visible visible);

    }

    /*################################## blow multiInsert interfaces ##################################*/

    interface InsertOptionSpec<T extends IDomain, C> extends InsertIntoSpec<T, C> {

        InsertOptionSpec<T, C> migration();
    }

    interface InsertIntoSpec<T extends IDomain, C> extends InsertSqlSpec {

        InsertSetSpec<T, C> insertInto(Collection<FieldMeta<? super T, ?>> fieldMetaList);

        InsertSetSpec<T, C> insertInto(Supplier<Collection<FieldMeta<? super T, ?>>> fieldMetaList);

        InsertSetSpec<T, C> insertInto(TableMeta<T> tableMeta);
    }

    interface InsertValuesSpec<T extends IDomain> extends InsertSqlSpec {

        InsertSpec value(T domain);

        InsertSpec values(List<T> domainList);
    }

    interface InsertSetSpec<T extends IDomain, C> extends InsertValuesSpec<T> {

        <F> InsertSetSpec<T, C> set(FieldMeta<? super T, F> fieldMeta, F value);

        <F> InsertSetSpec<T, C> set(FieldMeta<? super T, F> fieldMeta, Expression<F> value);

        <F> InsertSetSpec<T, C> set(FieldMeta<? super T, F> fieldMeta, Function<C, Expression<F>> function);

        <F> InsertSetSpec<T, C> setDefault(FieldMeta<? super T, F> fieldMeta);
    }


}
