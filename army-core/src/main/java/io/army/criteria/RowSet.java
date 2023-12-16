package io.army.criteria;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * This interface The statement that can present in UNION clause,this interface is base interface of below:
 * <ul>
 *     <li>{@link Query}</li>
 *     <li>{@link Values}</li>
 * </ul>
 *
 * @since 0.6.0
 */
public interface RowSet extends Statement {

    @Deprecated
    interface _RowSetSpec<R extends Item> {

        R asQuery();

    }

    interface _StaticUnionClause<R> {

        R union();

        R unionAll();

        R unionDistinct();
    }

    interface _DynamicUnionParensClause<T extends Item, R extends Item> {

        R unionParens(Consumer<T> consumer);

        R ifUnionParens(Consumer<T> consumer);

    }

    interface _StaticIntersectClause<R> {

        R intersect();

        R intersectAll();

        R intersectDistinct();
    }


    interface _StaticExceptClause<SP> {

        SP except();

        SP exceptAll();

        SP exceptDistinct();
    }


    interface _StaticMinusClause<SP> {

        SP minus();

        SP minusAll();

        SP minusDistinct();
    }


    interface _DynamicParensRowSetClause<T extends Item, R extends Item> {

        R parens(Function<T, R> function);

    }


}
