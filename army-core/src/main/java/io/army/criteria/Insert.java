package io.army.criteria;

import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;
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

        OR migration(boolean migration);

        OR nullHandle(NullHandleMode mode);
    }


    interface _IntoClause<C, T extends IDomain, NR> {

        NR into(TableMeta<T> table);

        _ColumnClause<T, NR> into(FieldMeta<? extends T> field);

        NR into(Consumer<Consumer<FieldMeta<? super T>>> consumer);

        NR into(BiConsumer<C, Consumer<FieldMeta<? super T>>> consumer);

    }


    interface _ColumnClause<T extends IDomain, IR> extends _RightParenClause<IR> {

        _ColumnClause<T, IR> comma(FieldMeta<? super T> field);

    }

    interface _ColumnListClause<C, T extends IDomain, IR> {

        _RightParenClause<IR> leftParen(Consumer<Consumer<FieldMeta<? super T>>> consumer);

        _RightParenClause<IR> leftParen(BiConsumer<C, Consumer<FieldMeta<? super T>>> consumer);

        _ColumnClause<T, IR> leftParen(FieldMeta<? super T> field);

    }


    /**
     * @since 1.0
     */
    interface _CommonExpClause<C, T extends IDomain, CR> {

        CR common(FieldMeta<? super T> field, @Nullable Object value);

        CR commonLiteral(FieldMeta<? super T> field, @Nullable Object value);

        CR commonExp(FieldMeta<? super T> field, Function<C, ? extends Expression> function);

        CR commonExp(FieldMeta<? super T> field, Supplier<? extends Expression> supplier);

        CR commonDefault(FieldMeta<? super T> field);

        CR commonNull(FieldMeta<? super T> field);

        CR ifCommon(FieldMeta<? super T> field, Supplier<?> supplier);

        CR ifCommon(FieldMeta<? super T> field, Function<C, ?> function);

        CR ifCommon(FieldMeta<? super T> field, Function<String, ?> function, String keyName);

        CR ifCommonLiteral(FieldMeta<? super T> field, Supplier<?> supplier);

        CR ifCommonLiteral(FieldMeta<? super T> field, Function<C, ?> function);

        CR ifCommonLiteral(FieldMeta<? super T> field, Function<String, ?> function, String keyName);

    }

    /**
     * @since 1.0
     */
    interface _ValueClause<C, T extends IDomain, VR> {

        VR value(T domain);

        VR value(Function<C, T> function);

        VR value(Supplier<T> supplier);

        VR value(Function<String, Object> function, String keyName);

        VR values(List<T> domainList);

        VR values(Function<C, List<T>> function);

        VR values(Supplier<List<T>> supplier);

        VR values(Function<String, Object> function, String keyName);
    }


    interface _StandardLiteralOptionSpec<C>
            extends _PreferLiteralClause<_StandardOptionSpec<C>>, _StandardOptionSpec<C> {

    }

    /**
     * @since 1.0
     */
    interface _StandardOptionSpec<C>
            extends _StandardInsertIntoClause<C>, _OptionClause<_StandardInsertIntoClause<C>> {

    }


    /**
     * @since 1.0
     */
    interface _StandardInsertIntoClause<C> {

        <T extends IDomain> _StandardColumnsSpec<C, T> insertInto(TableMeta<T> table);

    }

    interface _StandardColumnsSpec<C, T extends IDomain>
            extends _ColumnListClause<C, T, _StandardValueSpec<C, T>>, _StandardValueSpec<C, T> {

    }

    /**
     * @since 1.0
     */
    interface _StandardValueSpec<C, T extends IDomain> extends _CommonExpClause<C, T, _StandardValueSpec<C, T>>
            , _ValueClause<C, T, _InsertSpec> {


    }


    interface _SubQueryClause<C, SR> {

        _RightParenClause<SR> leftParen(Supplier<? extends SubQuery> supplier);

        _RightParenClause<SR> leftParen(Function<C, ? extends SubQuery> function);
    }

    interface _StandardSubQueryInsertClause<C> {

        <T extends IDomain> _ColumnListClause<C, T, _StandardSubQuerySpec<C>> insertInto(SingleTableMeta<T> table);

        <T extends IDomain> _ColumnListClause<C, T, _StandardParentSubQuerySpec<C, T>> insertInto(ChildTableMeta<T> table);

    }

    interface _StandardSubQuerySpec<C> extends _SubQueryClause<C, Insert._InsertSpec> {


    }

    interface _StandardParentSubQuerySpec<C, T extends IDomain>
            extends _SubQueryClause<C, _ColumnListClause<C, T, _StandardSubQuerySpec<C>>> {

    }


}
