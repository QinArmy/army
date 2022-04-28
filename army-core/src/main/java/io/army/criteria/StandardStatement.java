package io.army.criteria;

import io.army.lang.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This interface representing statement with standard syntax.
 * </p>
 *
 * @see StandardQuery
 * @since 1.0
 */
public interface StandardStatement extends Statement {


    interface SelectClauseForStandard<C, SR> extends Query.SelectClause<C, SR> {

        SR select(@Nullable Distinct modifier, SelectItem selectItem);

        SR select(@Nullable Distinct modifier, SelectItem selectItem1, SelectItem selectItem2);

        <S extends SelectItem> SR select(@Nullable Distinct modifier, Function<C, List<S>> function);

        <S extends SelectItem> SR select(@Nullable Distinct modifier, Supplier<List<S>> supplier);

        SR select(@Nullable Distinct modifier, Consumer<List<SelectItem>> consumer);

    }

}
