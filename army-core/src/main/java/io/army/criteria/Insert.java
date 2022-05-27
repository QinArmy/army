package io.army.criteria;

import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
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
    interface _InsertSpec {

        Insert asInsert();

    }

    /*################################## blow multiInsert interfaces ##################################*/

    interface _PreferLiteralOptionClause<OR> {
        _OptionClause<OR> preferLiteral(boolean prefer);
    }

    /**
     * @since 1.0
     */
    interface _OptionClause<OR> {

        OR migration();

        /**
         * <p>
         * When invoking {@link _InsertIntoClause#insertInto(TableMeta)} and appropriate field value is null,mode is valid.
         * </p>
         */
        OR nullHandle(NullHandleMode mode);
    }

    /**
     * @since 1.0
     */
    interface _InsertIntoClause<C, T extends IDomain, IR> {

        IR insertInto(Consumer<Consumer<FieldMeta<? super T>>> consumer);

        IR insertInto(BiConsumer<C, Consumer<FieldMeta<? super T>>> consumer);

        IR insertInto(TableMeta<T> table);
    }

    /**
     * @since 1.0
     */
    interface _CommonExpClause<C, T extends IDomain, SR> {

        SR set(FieldMeta<? super T> field, @Nullable Object paramOrExp);

        SR setExp(FieldMeta<? super T> field, Function<C, ? extends Expression> function);

        SR setExp(FieldMeta<? super T> field, Supplier<? extends Expression> supplier);

        SR setDefault(FieldMeta<? super T> field);

        SR setNull(FieldMeta<? super T> field);

        SR ifSetExp(FieldMeta<? super T> field, Function<C, ? extends Expression> function);

        SR ifSetExp(FieldMeta<? super T> field, Supplier<? extends Expression> supplier);

    }

    /**
     * @since 1.0
     */
    interface _ValueClause<C, T extends IDomain> {

        _InsertSpec value(T domain);

        _InsertSpec value(Function<C, T> function);

        _InsertSpec value(Supplier<T> supplier);

        _InsertSpec value(Function<String, Object> function, String keyName);

        _InsertSpec values(List<T> domainList);

        _InsertSpec values(Function<C, List<T>> function);

        _InsertSpec values(Supplier<List<T>> supplier);

        _InsertSpec values(Function<String, Object> function, String keyName);
    }


    interface _StandardOptimizingOptionSpec<C, T extends IDomain>
            extends _PreferLiteralOptionClause<_ValueInsertIntoSpec<C, T>>
            , _StandardValueInsertSpec<C, T> {

    }

    /**
     * @since 1.0
     */
    interface _StandardValueInsertSpec<C, T extends IDomain>
            extends _ValueInsertIntoSpec<C, T>, _OptionClause<_ValueInsertIntoSpec<C, T>> {

    }


    /**
     * @since 1.0
     */
    interface _ValueInsertIntoSpec<C, T extends IDomain>
            extends _InsertIntoClause<C, T, _ValueSpec<C, T>> {

    }

    /**
     * @since 1.0
     */
    interface _ValueSpec<C, T extends IDomain> extends _CommonExpClause<C, T, _ValueSpec<C, T>>
            , _ValueClause<C, T> {

    }


}
