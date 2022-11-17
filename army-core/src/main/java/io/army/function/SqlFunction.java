package io.army.function;

import io.army.criteria.Expression;
import io.army.criteria.Item;
import io.army.criteria.SpacePredicate;
import io.army.criteria.TypeInfer;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl._AliasExpression;
import io.army.criteria.impl._ItemExpression;
import io.army.criteria.impl._ParenExpression;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * <p>
 * This interface representing the sql function method that take zero argument method,eg: {@link SQLs#cases(Function, Function)}
 * </p>
 *
 * @param <E> below type:
 *            <ul>
 *                 <li>{@link Expression}</li>
 *                 <li>{@link _AliasExpression}</li>
 *                 <li>{@link _ParenExpression}</li>
 *                 <li>{@link SpacePredicate}</li>
 *            </ul>
 * @param <I> the type of below method returning :
 *            <ul>
 *                 <li>{@link _AliasExpression#as(String)}</li>
 *                 <li>{@link _ParenExpression#rightParen()}</li>
 *                 <li>{@link SpacePredicate#space()}</li>
 *            </ul>
 * @param <R> the interface type of sql function method returning,eg: {@link SQLs#cases(Function, Function)} returning type.
 * @since 1.0
 */
@FunctionalInterface
public interface SqlFunction<E extends Expression, I extends Item, R extends Item>
        extends BiFunction<Function<_ItemExpression<I>, E>, Function<TypeInfer, I>, R> {


}
