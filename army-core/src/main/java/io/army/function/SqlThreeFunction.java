package io.army.function;


import io.army.criteria.Expression;
import io.army.criteria.Item;
import io.army.criteria.TypeInfer;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl._AliasExpression;
import io.army.criteria.impl._ItemExpression;

import java.util.function.Function;

/**
 * <p>
 * This interface representing the sql function method that take three argument method
 * ,eg:{@code  MySQLs#lag(Expression, Expression, Expression,Function, Function)}
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
public interface SqlThreeFunction<E extends Item, I extends Item, R extends Item> {

    R apply(Expression exp1, Expression ex2, Expression exp3, Function<_ItemExpression<I>, E> expFunc,
            Function<TypeInfer, I> endFunc);


}