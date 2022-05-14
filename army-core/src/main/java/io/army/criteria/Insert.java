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
public interface Insert extends DmlStatement, DmlStatement.DmlInsert {



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

        /**
         * <p>
         * When invoking {@link InsertIntoClause#insertInto(TableMeta)} and appropriate field value is null,mode is valid.
         * </p>
         */
        OR nullHandle(NullHandleMode mode);
    }

    /**
     * @since 1.0
     */
    interface InsertIntoClause<C, T extends IDomain, IR> {

        IR insertInto(List<FieldMeta<? super T>> fields);

        IR insertInto(Function<C, List<FieldMeta<? super T>>> function);

        IR insertInto(Supplier<List<FieldMeta<? super T>>> supplier);

        IR insertInto(TableMeta<T> table);
    }

    /**
     * @since 1.0
     */
    interface CommonExpClause<C, T extends IDomain, SR> {

        SR set(FieldMeta<? super T> field, @Nullable Object paramOrExp);

        SR setExp(FieldMeta<? super T> field, Function<C, ? extends Expression> function);

        SR setExp(FieldMeta<? super T> field, Supplier<? extends Expression> supplier);

        SR setDefault(FieldMeta<? super T> field);

        SR setNull(FieldMeta<? super T> field);

    }

    /**
     * @since 1.0
     */
    interface ValueClause<C, T extends IDomain> {

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
    interface StandardValueInsertSpec<C, T extends IDomain>
            extends Insert.ValueInsertIntoSpec<C, T>, Insert.OptionClause<Insert.ValueInsertIntoSpec<C, T>> {

    }


    /**
     * @since 1.0
     */
    interface ValueInsertIntoSpec<C, T extends IDomain>
            extends Insert.InsertIntoClause<C, T, Insert.ValueSpec<C, T>> {

    }

    /**
     * @since 1.0
     */
    interface ValueSpec<C, T extends IDomain> extends Insert.CommonExpClause<C, T, ValueSpec<C, T>>
            , Insert.ValueClause<C, T> {

    }


}
