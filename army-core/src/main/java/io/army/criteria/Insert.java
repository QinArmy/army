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

        InsertValuesSpec<T, C> insertInto(Function<C, Collection<FieldMeta<? super T, ?>>> function);

        InsertValuesSpec<T, C> insertInto(Supplier<Collection<FieldMeta<? super T, ?>>> supplier);

        InsertValuesSpec<T, C> insertInto(TableMeta<T> table);
    }

    interface InsertValuesSpec<T extends IDomain, C> extends InsertSqlSpec {

        InsertValuesSpec<T, C> set(FieldMeta<? super T, ?> field, @Nullable Object value);

        InsertValuesSpec<T, C> set(FieldMeta<? super T, ?> field, Expression<?> value);

        <F> InsertValuesSpec<T, C> set(FieldMeta<? super T, ?> field, Function<C, Expression<F>> function);

        <F> InsertValuesSpec<T, C> set(FieldMeta<? super T, ?> field, Supplier<Expression<F>> supplier);

        InsertValuesSpec<T, C> setDefault(FieldMeta<? super T, ?> field);

        InsertValuesSpec<T, C> setNull(FieldMeta<? super T, ?> field);

        InsertSpec value(T domain);

        InsertSpec value(Function<C, T> function);

        InsertSpec value(Supplier<T> supplier);

        InsertSpec values(List<T> domainList);

        InsertSpec values(Function<C, List<T>> function);

        InsertSpec values(Supplier<List<T>> supplier);

    }


}
