package io.army.criteria;

import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface DialectStatement extends Statement {


    interface WithCteClause<C, WE> {

        WE with(String cteName, Supplier<SubQuery> supplier);

        WE with(String cteName, Function<C, SubQuery> function);

        WE with(Supplier<List<Cte>> supplier);

        WE with(Function<C, List<Cte>> function);

        WE withRecursive(String cteName, Supplier<SubQuery> supplier);

        WE withRecursive(String cteName, Function<C, SubQuery> function);

        WE withRecursive(Supplier<List<Cte>> supplier);

        WE withRecursive(Function<C, List<Cte>> function);

    }


    interface DialectJoinClause<C, JT, JS> extends Statement.JoinClause<C, JT, JS> {

        JT straightJoin(TableMeta<?> table, String tableAlias);

        <T extends TableItem> JS straightJoin(Function<C, T> function, String alias);

        <T extends TableItem> JS straightJoin(Supplier<T> supplier, String alias);

        JT ifStraightJoin(Predicate<C> predicate, TableMeta<?> table, String alias);

        <T extends TableItem> JS ifStraightJoin(Function<C, T> function, String alias);

        <T extends TableItem> JS ifStraightJoin(Supplier<T> supplier, String alias);

    }


}
