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
 * <p>
 * This interface representing INSERT statement.
 * </p>
 *
 * @since 1.0
 */
public interface Insert extends DmlStatement, DmlStatement.DmlInsert {



    /*################################## blow interfaces  ##################################*/

    /**
     * <p>
     * This interface representing the capacity ending INSERT statement.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _InsertSpec {

        Insert asInsert();

    }

    /*################################## blow multiInsert interfaces ##################################*/

    /**
     * <p>
     * This interface representing the option prefer output literal when output {@link IDomain} column,but not contain
     * comment expression
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _PreferLiteralClause<PO> {
        PO preferLiteral(boolean prefer);
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

        SR set(FieldMeta<? super T> field, @Nullable Object value);

        SR setLiteral(FieldMeta<? super T> field, @Nullable Object value);

        SR setExp(FieldMeta<? super T> field, Function<C, ? extends Expression> function);

        SR setExp(FieldMeta<? super T> field, Supplier<? extends Expression> supplier);

        SR setDefault(FieldMeta<? super T> field);

        SR setNull(FieldMeta<? super T> field);

        SR ifSet(FieldMeta<? super T> field, Supplier<?> supplier);

        SR ifSet(FieldMeta<? super T> field, Function<C, ?> function);

        SR ifSet(FieldMeta<? super T> field, Function<String, ?> function, String keyName);

        SR ifSetLiteral(FieldMeta<? super T> field, Supplier<?> supplier);

        SR ifSetLiteral(FieldMeta<? super T> field, Function<C, ?> function);

        SR ifSetLiteral(FieldMeta<? super T> field, Function<String, ?> function, String keyName);

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


    interface _StandardLiteralOptionSpec<C, T extends IDomain>
            extends _PreferLiteralClause<_StandardOptionSpec<C, T>>
            , _StandardOptionSpec<C, T> {

    }

    /**
     * @since 1.0
     */
    interface _StandardOptionSpec<C, T extends IDomain>
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
