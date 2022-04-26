package io.army.criteria;

import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public interface DialectStatement extends Statement {


    /**
     * <p>
     * This interface representing WITH clause(Common Table Expressions).
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C>  criteria object java type
     * @param <WE> next clause java type
     */
    interface WithCteClause<C, WE> {

        WE with(String cteName, Supplier<? extends SubQuery> supplier);

        WE with(String cteName, Function<C, ? extends SubQuery> function);

        WE with(Supplier<List<Cte>> supplier);

        WE with(Function<C, List<Cte>> function);

        WE withRecursive(String cteName, Supplier<? extends SubQuery> supplier);

        WE withRecursive(String cteName, Function<C, ? extends SubQuery> function);

        WE withRecursive(Supplier<List<Cte>> supplier);

        WE withRecursive(Function<C, List<Cte>> function);

    }

    /**
     * <p>
     * This interface representing dialect from clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C>  criteria object java type
     * @param <FT> next clause java type
     * @param <FS> next clause java type
     * @param <FP> next clause java type
     * @param <FB> next clause java type,it's sub interface of {@link LeftBracketClause}.
     * @since 1.0
     */
    interface DialectFromClause<C, FT, FS, FP, FB> extends Statement.FromClause<C, FT, FS, FB> {

        FP from(TableMeta<?> table);
    }


    /**
     * <p>
     * This interface representing dialect join clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C>  criteria object java type
     * @param <JT> next clause java type
     * @param <JS> next clause java type
     * @param <JP> next clause java type
     * @param <JC> next clause java type
     * @param <JD> next clause java type
     * @param <JE> next clause java type
     * @param <JF> next clause java type
     * @since 1.0
     */
    interface DialectJoinClause<C, JT, JS, JP, JC, JD, JE, JF> extends Statement.JoinClause<C, JT, JS, JC, JD, JE> {

        JT straightJoin(TableMeta<?> table, String tableAlias);

        <T extends TableItem> JS straightJoin(Function<C, T> function, String alias);

        <T extends TableItem> JS straightJoin(Supplier<T> supplier, String alias);

        JE straightJoin();

        JP leftJoin(TableMeta<?> table);

        JP join(TableMeta<?> table);

        JP rightJoin(TableMeta<?> table);

        JP straightJoin(TableMeta<?> table);

        JP fullJoin(TableMeta<?> table);

        JF crossJoin(TableMeta<?> table);

    }

    /**
     * <p>
     * This interface representing dialect left bracket clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C>  criteria object java type
     * @param <JT> next clause java type
     * @param <JS> next clause java type
     * @param <JP> next clause java type
     * @since 1.0
     */
    interface DialectLeftBracketClause<C, JT, JS, JP> extends LeftBracketClause<C, JT, JS> {

        @Override
        DialectLeftBracketClause<C, JT, JS, JP> leftBracket();

        JP leftBracket(TableMeta<?> table);
    }

    /**
     * <p>
     * This interface representing dialect union clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C>  criteria object java type
     * @param <UR> next clause java type
     * @param <SP> next clause java type
     * @since 1.0
     */
    interface DialectUnionClause<C, UR, SP> extends Query.QueryUnionClause<C, UR, SP> {

    }


}
