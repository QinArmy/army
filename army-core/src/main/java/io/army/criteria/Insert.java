package io.army.criteria;

import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @since 1.0
 */
public interface Insert extends Statement {


    /*################################## blow interfaces  ##################################*/

    /**
     * @since 1.0
     */
    interface InsertSpec {

        Insert asInsert();

    }

    /*################################## blow multiInsert interfaces ##################################*/

    /**
     * @since 1.0
     */
    interface OptionClause<OR> {

        OR migration();
    }

    /**
     * @since 1.0
     */
    interface InsertIntoClause<T extends IDomain, C, IR> {

        IR insertInto(List<FieldMeta<? super T>> fields);

        IR insertInto(Function<C, List<FieldMeta<? super T>>> function);

        IR insertInto(Supplier<List<FieldMeta<? super T>>> supplier);

        IR insertInto(TableMeta<T> table);
    }

    /**
     * @since 1.0
     */
    interface CommonExpClause<T extends IDomain, C, SR> {

        SR set(FieldMeta<? super T> field, @Nullable Object paramOrExp);

        SR set(FieldMeta<? super T> field, Function<C, Object> paramOrExp);

        SR set(FieldMeta<? super T> field, Supplier<Object> paramOrExp);

        SR setDefault(FieldMeta<? super T> field);

        SR setNull(FieldMeta<? super T> field);

    }

    /**
     * @since 1.0
     */
    interface ValueClause<T extends IDomain, C> {

        InsertSpec value(T domain);

        InsertSpec value(Function<C, T> function);

        InsertSpec value(Supplier<T> supplier);

        InsertSpec value(Function<String, Object> function, String keyName);

        InsertSpec values(List<T> domainList);

        InsertSpec values(Function<C, List<T>> function);

        InsertSpec values(Supplier<List<T>> supplier);

        InsertSpec values(Function<String, Object> function, String keyName);
    }


    /**
     * @since 1.0
     */
    interface StandardValueInsertSpec<T extends IDomain, C>
            extends Insert.ValueInsertIntoSpec<T, C>, Insert.OptionClause<Insert.ValueInsertIntoSpec<T, C>> {

    }


    /**
     * @since 1.0
     */
    interface ValueInsertIntoSpec<T extends IDomain, C>
            extends Insert.InsertIntoClause<T, C, Insert.ValueSpec<T, C>> {

    }

    /**
     * @since 1.0
     */
    interface ValueSpec<T extends IDomain, C> extends Insert.CommonExpClause<T, C, ValueSpec<T, C>>
            , Insert.ValueClause<T, C> {

    }


}
