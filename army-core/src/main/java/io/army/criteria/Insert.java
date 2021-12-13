package io.army.criteria;

import io.army.domain.IDomain;
import io.army.lang.Nullable;
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

    }

    /*################################## blow multiInsert interfaces ##################################*/

    interface InsertOptionSpec<T extends IDomain, C> extends InsertIntoSpec<T, C> {

        InsertIntoSpec<T, C> migration();
    }

    interface InsertIntoSpec<T extends IDomain, C> extends InsertSqlSpec {

        InsertValuesSpec<T, C> insertInto(Collection<FieldMeta<? super T, ?>> fields);

        InsertValuesSpec<T, C> insertInto(Supplier<Collection<FieldMeta<? super T, ?>>> supplier);

        InsertValuesSpec<T, C> insertInto(Function<C, Collection<FieldMeta<? super T, ?>>> function);

        InsertValuesSpec<T, C> insertInto(TableMeta<T> table);
    }

    interface InsertValuesSpec<T extends IDomain, C> extends InsertSqlSpec {

        <F> InsertValuesSpec<T, C> set(FieldMeta<? super T, F> fieldMeta, @Nullable F value);

        <F> InsertValuesSpec<T, C> set(FieldMeta<? super T, F> fieldMeta, Expression<F> value);

        <F> InsertValuesSpec<T, C> set(FieldMeta<? super T, F> fieldMeta, Function<C, Expression<F>> function);

        <F> InsertValuesSpec<T, C> setDefault(FieldMeta<? super T, F> fieldMeta);

        InsertSpec value(T domain);

        InsertSpec value(Function<C, T> function);

        InsertSpec values(List<T> domainList);

        InsertSpec values(Function<C, List<T>> function);

    }


    interface SubQueryInsertFieldSpec<T extends IDomain, C> extends InsertSqlSpec {

        SubQueryInsertSpec<T, C> insertInto(List<FieldMeta<T, ?>> fieldList);

        SubQueryInsertSpec<T, C> insertInto(Function<C, List<FieldMeta<T, ?>>> function);

        SubQueryInsertSpec<T, C> insertInto(Supplier<List<FieldMeta<T, ?>>> supplier);

    }

    interface SubQueryInsertSpec<T extends IDomain, C> extends InsertSqlSpec {

        InsertSpec values(Function<C, SubQuery> function);
    }


    interface SubQueryInsertParentFieldSpec<T extends IDomain, C> {

        SubQueryInsertParentSpec<T, C> insertInto(List<FieldMeta<? super T, ?>> fieldList);

        SubQueryInsertParentSpec<T, C> insertInto(Function<C, List<FieldMeta<? super T, ?>>> function);

        SubQueryInsertParentSpec<T, C> insertInto(Supplier<List<FieldMeta<? super T, ?>>> supplier);
    }

    interface SubQueryInsertParentSpec<T extends IDomain, C> extends InsertSqlSpec {

        SubQueryInsertFieldSpec<T, C> values(Function<C, SubQuery> function);

    }


}
