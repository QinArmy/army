package io.army.criteria;

import io.army.lang.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface NamedWindow {


    interface ExistingWindowClause<C, NR> {

        NR window(String existingWindowName);

        NR ifWindow(Supplier<String> supplier);

        NR ifWindow(Function<C, String> function);
    }


    interface PartitionClause<C, PR> {

        PR partitionBy(SortPart sortPart);

        PR partitionBy(SortPart sortPart1, SortPart sortPart2);

        PR partitionBy(List<SortPart> sortPartList);

        PR partitionBy(Function<C, List<SortPart>> function);

        PR partitionBy(Supplier<List<SortPart>> supplier);

        PR ifPartitionBy(@Nullable SortPart sortPart);

        PR ifPartitionBy(Supplier<List<SortPart>> supplier);

        PR ifPartitionBy(Function<C, List<SortPart>> function);

    }

    interface FrameUnitClause<C, FR> {

        FR rows();

        FR range();

        FR ifRows(Predicate<C> predicate);

        FR ifRange(Predicate<C> predicate);
    }

    interface FrameClause<C, ER> {

        ER currentRow();

        ER unboundedPreceding();

        ER unboundedFollowing();

        ER preceding(Expression<?> expression);

        ER following(Expression<?> expression);

        <E> ER preceding(Supplier<Expression<E>> supplier);

        <E> ER following(Function<C, Expression<E>> function);

    }

    interface FrameBetweenClause<BR> {

        BR between();

    }

    interface FrameBetweenAndClause<AR> {

        AR and();

    }


}
