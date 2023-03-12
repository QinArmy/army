package io.army.criteria;

import io.army.criteria.dialect.SubDelete;
import io.army.criteria.dialect.SubInsert;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.impl.SQLs;
import io.army.dialect.Dialect;
import io.army.function.*;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.TableMeta;
import io.army.stmt.Stmt;

import java.util.List;
import java.util.function.*;

/**
 * <p>
 * This interface representing sql statement,this interface is base interface of below:
 * <ul>
 *     <li>{@link Select}</li>
 *     <li>{@link InsertStatement}</li>
 *     <li>{@link UpdateStatement}</li>
 *     <li>{@link DeleteStatement}</li>
 *     <li>{@link SubQuery}</li>
 *     <li>{@link Values}</li>
 * </ul>
 * </p>
 *
 * @since 1.0
 */
public interface Statement extends Item {

    /**
     * assert statement prepared
     */
    void prepared();

    boolean isPrepared();


    interface StatementMockSpec {

        /**
         * @param none nothing
         */
        String mockAsString(Dialect dialect, Visible visible, boolean none);

        Stmt mockAsStmt(Dialect dialect, Visible visible);

    }

    interface DmlStatementSpec {

    }

    interface JoinBuilder {

    }

    interface AscDesc {

    }

    interface NullsFirstLast {

    }


    interface _AsCteClause<I extends Item> extends Item {

        I asCte();
    }


    /**
     * <p>
     * This interface representing AS clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <AR> next clause java type
     * @since 1.0
     */
    interface _AsClause<AR> extends Item {

        AR as(String alias);
    }

    interface _StaticAsClaus<AR> extends Item {

        AR as();
    }

    interface _StaticBetweenClause<BR> {

        BR between();
    }

    interface _StaticAndClause<AR> extends Item {

        AR and();
    }


    /**
     * <p>
     * This interface representing bind params clause for batch update(delete).
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <BR> next clause java type
     * @since 1.0
     */
    interface _BatchParamClause<BR> {

        <P> BR paramList(List<P> paramList);

        <P> BR paramList(Supplier<List<P>> supplier);

        BR paramList(Function<String, ?> function, String keyName);
    }


    interface _LeftParenClause<LR> {

        LR leftParen();
    }

    /**
     * <p>
     * This interface representing RIGHT BRACKET clause in join expression.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <RR> next clause java type
     * @since 1.0
     */
    interface _RightParenClause<RR> extends Item {

        RR rightParen();

    }


    /**
     * <p>
     * This interface representing ON clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <OR> next clause java type
     * @since 1.0
     */
    interface _OnClause<OR> extends Item {

        OR on(IPredicate predicate);

        OR on(IPredicate predicate1, IPredicate predicate2);

        OR on(Function<Expression, IPredicate> operator, DataField operandField);

        OR on(Function<Expression, IPredicate> operator1, DataField operandField1
                , Function<Expression, IPredicate> operator2, DataField operandField2);

        OR on(Consumer<Consumer<IPredicate>> consumer);


    }


    /**
     * <p>
     * This interface representing FROM clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <FT> next clause java type
     * @param <FS> next clause java type
     * @since 1.0
     */
    interface _FromClause<FT, FS> {

        FT from(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

        <T extends DerivedTable> FS from(Supplier<T> supplier);

    }

    interface _FromModifierTabularClause<FT, FS> extends _FromClause<FT, FS> {

        <T extends DerivedTable> FS from(Query.DerivedModifier modifier, Supplier<T> supplier);
    }

    interface _FromModifierClause<FT, FS> extends _FromModifierTabularClause<FT, FS> {

        FT from(Query.TableModifier modifier, TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

    }

    interface _FromNestedClause<FN> {

        FN from();

    }

    /**
     * <p>
     * This interface representing dialect FROM clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <FC> same with the FS of {@link _FromClause}
     * @see _FromClause
     * @since 1.0
     */
    interface _FromCteClause<FC> {

        FC from(String cteName);

        FC from(String cteName, SQLs.WordAs wordAs, String alias);

    }

    interface _FromModifierCteClause<FC> extends _FromCteClause<FC> {

        FC from(Query.DerivedModifier modifier, String cteName);

        FC from(Query.DerivedModifier modifier, String cteName, SQLs.WordAs wordAs, String alias);
    }


    /**
     * <p>
     * This interface representing FROM clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <FT> next clause java type
     * @param <FS> next clause java type
     * @since 1.0
     */
    interface _UsingItemClause<FT, FS> {

        FT using(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

        <T extends DerivedTable> FS using(Supplier<T> supplier);

    }

    interface _UsingModifierTabularClause<FT, FS> extends _UsingItemClause<FT, FS> {

        <T extends DerivedTable> FS using(Query.DerivedModifier modifier, Supplier<T> supplier);
    }

    interface _UsingModifierClause<FT, FS> extends _UsingModifierTabularClause<FT, FS> {

        FT using(Query.TableModifier modifier, TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

    }

    interface _UsingNestedClause<FN> {

        FN using();

    }

    /**
     * <p>
     * This interface representing dialect FROM clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <FC> same with the FS of {@link _FromClause}
     * @see _FromClause
     * @since 1.0
     */
    interface _UsingCteClause<FC> {

        FC using(String cteName);

        FC using(String cteName, SQLs.WordAs wordAs, String alias);

    }

    interface _UsingModifierCteClause<FC> extends _UsingCteClause<FC> {

        FC using(Query.DerivedModifier modifier, String cteName);

        FC using(Query.DerivedModifier modifier, String cteName, SQLs.WordAs wordAs, String alias);
    }


    /**
     * <p>
     * This interface representing JOIN clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <JT> next clause java type,it's sub interface of {@link _OnClause}
     * @param <JS> next clause java type,it's sub interface of {@link _OnClause}
     * @see _CrossJoinClause
     * @since 1.0
     */
    interface _JoinClause<JT, JS> extends Item {

        JT leftJoin(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

        <T extends DerivedTable> JS leftJoin(Supplier<T> supplier);

        JT join(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

        <T extends DerivedTable> JS join(Supplier<T> supplier);

        JT rightJoin(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);


        <T extends DerivedTable> JS rightJoin(Supplier<T> supplier);

        JT fullJoin(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

        <T extends DerivedTable> JS fullJoin(Supplier<T> supplier);

    }


    interface _JoinModifierTabularClause<JT, JS> extends _JoinClause<JT, JS> {

        <T extends DerivedTable> JS leftJoin(Query.DerivedModifier modifier, Supplier<T> supplier);

        <T extends DerivedTable> JS join(Query.DerivedModifier modifier, Supplier<T> supplier);

        <T extends DerivedTable> JS rightJoin(Query.DerivedModifier modifier, Supplier<T> supplier);

        <T extends DerivedTable> JS fullJoin(Query.DerivedModifier modifier, Supplier<T> supplier);
    }

    interface _JoinModifierClause<JT, JS> extends _JoinModifierTabularClause<JT, JS> {

        JT leftJoin(Query.TableModifier modifier, TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

        JT join(Query.TableModifier modifier, TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

        JT rightJoin(Query.TableModifier modifier, TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

        JT fullJoin(Query.TableModifier modifier, TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

    }


    /**
     * <p>
     * This interface representing CROSS JOIN clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _CrossJoinClause<FT, FS> {

        FT crossJoin(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

        <T extends DerivedTable> FS crossJoin(Supplier<T> supplier);

    }

    interface _CrossJoinModifierTabularClause<FT, FS> extends _CrossJoinClause<FT, FS> {

        <T extends DerivedTable> FS crossJoin(Query.DerivedModifier modifier, Supplier<T> supplier);
    }

    interface _CrossJoinModifierClause<FT, FS> extends _CrossJoinModifierTabularClause<FT, FS> {

        FT crossJoin(Query.TableModifier modifier, TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);


    }

    interface _JoinNestedClause<JN> {

        JN leftJoin();

        JN join();

        JN rightJoin();

        JN fullJoin();

    }

    interface _StraightJoinNestedClause<JN> {

        JN straightJoin();
    }

    interface _CrossJoinNestedClause<FN> {

        FN crossJoin();
    }


    interface _DynamicJoinClause<B extends JoinBuilder, JD> {

        JD ifLeftJoin(Consumer<B> consumer);

        JD ifJoin(Consumer<B> consumer);

        JD ifRightJoin(Consumer<B> consumer);

        JD ifFullJoin(Consumer<B> consumer);
    }

    interface _DynamicCrossJoinClause<B extends JoinBuilder, JD> {

        JD ifCrossJoin(Consumer<B> consumer);
    }

    interface _DynamicStraightJoinClause<B extends JoinBuilder, JD> {

        JD ifStraightJoin(Consumer<B> consumer);
    }


    /**
     * <p>
     * This interface representing a left bracket clause after key word 'FROM' or key word 'JOIN'.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <LT> next clause java type
     * @param <LS> next clause java type
     * @since 1.0
     */
    interface _NestedLeftParenClause<LT, LS> {

        LT leftParen(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

        <T extends DerivedTable> LS leftParen(Supplier<T> supplier);

    }

    interface _NestedLeftParenModifierTabularClause<LT, LS> extends _NestedLeftParenClause<LT, LS> {

        <T extends DerivedTable> LS leftParen(Query.DerivedModifier modifier, Supplier<T> supplier);

    }

    interface _NestedLeftParenModifierClause<LT, LS> extends _NestedLeftParenModifierTabularClause<LT, LS> {

        LT leftParen(Query.TableModifier modifier, TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

    }


    interface _NestedDialectLeftParenClause<LP> {

        LP leftParen(TableMeta<?> table);

    }


    interface _MinWhereClause<WR, WA> {

        WR where(Consumer<Consumer<IPredicate>> consumer);

        WA where(IPredicate predicate);

        WA where(Supplier<IPredicate> supplier);

        WA whereIf(Supplier<IPredicate> supplier);

    }

    interface _MinQueryWhereClause<WR, WA> extends _MinWhereClause<WR, WA> {

        WR ifWhere(Consumer<Consumer<IPredicate>> consumer);
    }

    /**
     * <p>
     * This interface representing WHERE clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <WR> next clause java type
     * @param <WA> next clause java type
     * @since 1.0
     */
    interface _WhereClause<WR, WA> extends _MinWhereClause<WR, WA> {

        //below two argument method

        WA where(Function<Expression, IPredicate> expOperator, Expression operand);

        WA where(UnaryOperator<IPredicate> expOperator, IPredicate operand);

        <E extends RightOperand> WA where(Function<E, IPredicate> expOperator, Supplier<E> supplier);

        WA where(Function<BiFunction<DataField, String, Expression>, IPredicate> fieldOperator,
                 BiFunction<DataField, String, Expression> operator);

        //below three argument method

        WA where(ExpressionOperator<Expression, Expression, IPredicate> expOperator,
                 BiFunction<Expression, Expression, Expression> valueOperator, Expression expression);

        WA where(ExpressionOperator<Expression, Object, IPredicate> expOperator,
                 BiFunction<Expression, Object, Expression> valueOperator, Object value);

        <T> WA where(ExpressionOperator<Expression, T, IPredicate> expOperator,
                     BiFunction<Expression, T, Expression> valueOperator, Supplier<T> getter);

        WA where(BiFunction<TeNamedOperator<DataField>, Integer, IPredicate> expOperator,
                 TeNamedOperator<DataField> namedOperator, int size);

        //below four argument method


        WA where(ExpressionOperator<Expression, Object, IPredicate> expOperator,
                 BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        WA where(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);

        WA where(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator, String paramName, int size);

        //below five argument method

        WA where(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator,
                 Object firstValue, SQLs.WordAnd and, Object secondValue);

        <T> WA where(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator,
                     Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

        //below six argument method

        WA where(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator,
                 Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);


        //below two argument method

        <E extends RightOperand> WA whereIf(Function<E, IPredicate> expOperator, Supplier<E> supplier);

        //below three argument method
        <T> WA whereIf(ExpressionOperator<Expression, T, IPredicate> expOperator,
                       BiFunction<Expression, T, Expression> operator, Supplier<T> getter);

        WA whereIf(BiFunction<TeNamedOperator<DataField>, Integer, IPredicate> expOperator,
                   TeNamedOperator<DataField> namedOperator, Supplier<Integer> supplier);

        //below four argument method

        WA whereIf(ExpressionOperator<Expression, Object, IPredicate> expOperator,
                   BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

        WA whereIf(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator, String paramName,
                   Supplier<Integer> supplier);

        //below five argument method

        <T> WA whereIf(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator,
                       Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);


        //below six argument method

        //below seven argument method

        WA whereIf(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator,
                   Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);
    }

    /**
     * <p>
     * This interface representing WHERE clause in SELECT statement.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <WR> next clause java type
     * @param <WA> next clause java type
     * @since 1.0
     */
    interface _QueryWhereClause<WR, WA> extends _WhereClause<WR, WA>, _MinQueryWhereClause<WR, WA> {

    }


    interface _MinWhereAndClause<WA> {

        WA and(IPredicate predicate);

        WA and(Supplier<IPredicate> supplier);

        WA ifAnd(Supplier<IPredicate> supplier);

    }


    /**
     * <p>
     * This interface representing AND clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <WA> next clause java type
     * @since 1.0
     */
    interface _WhereAndClause<WA> extends _MinWhereAndClause<WA> {

        //tow argument method

        WA and(Function<Expression, IPredicate> expOperator, Expression operand);

        WA and(UnaryOperator<IPredicate> expOperator, IPredicate operand);

        <E extends RightOperand> WA and(Function<E, IPredicate> expOperator, Supplier<E> supplier);

        WA and(Function<BiFunction<DataField, String, Expression>, IPredicate> fieldOperator, BiFunction<DataField, String, Expression> namedOperator);

        //three argument method

        WA and(ExpressionOperator<Expression, Expression, IPredicate> expOperator,
               BiFunction<Expression, Expression, Expression> valueOperator, Expression expression);

        WA and(ExpressionOperator<Expression, Object, IPredicate> expOperator,
               BiFunction<Expression, Object, Expression> valueOperator, Object value);

        <T> WA and(ExpressionOperator<Expression, T, IPredicate> expOperator,
                   BiFunction<Expression, T, Expression> operator, Supplier<T> getter);

        WA and(BiFunction<TeNamedOperator<DataField>, Integer, IPredicate> expOperator,
               TeNamedOperator<DataField> namedOperator, int size);

        //four argument method

        WA and(ExpressionOperator<Expression, Object, IPredicate> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

        WA and(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);


        WA and(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator, String paramName, int size);

        //five argument method

        WA and(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator,
               Object firstValue, SQLs.WordAnd and, Object secondValue);

        <T> WA and(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator,
                   Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);


        //six argument method

        WA and(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator,
               Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

        <E extends RightOperand> WA ifAnd(Function<E, IPredicate> expOperator, Supplier<E> supplier);

        <T> WA ifAnd(ExpressionOperator<Expression, T, IPredicate> expOperator,
                     BiFunction<Expression, T, Expression> operator, Supplier<T> getter);

        WA ifAnd(BiFunction<TeNamedOperator<DataField>, Integer, IPredicate> expOperator,
                 TeNamedOperator<DataField> namedOperator, Supplier<Integer> supplier);

        WA ifAnd(ExpressionOperator<Expression, Object, IPredicate> expOperator,
                 BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

        <T> WA ifAnd(BetweenValueOperator<T> expOperator,
                     BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and,
                     Supplier<T> secondGetter);

        WA ifAnd(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator,
                 Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

        WA ifAnd(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator, String paramName,
                 Supplier<Integer> supplier);


    }


    interface _StaticOrderByCommaClause<OR> {


        OR comma(Expression exp, AscDesc ascDesc);

        OR comma(Expression exp1, AscDesc ascDesc1, Expression exp2);

        OR comma(Expression exp1, Expression exp2, AscDesc ascDesc2);

        OR comma(Expression exp1, AscDesc ascDesc1, Expression exp2, AscDesc ascDesc2);

    }

    interface _StaticOrderByNullsCommaClause<OR> extends _StaticOrderByCommaClause<OR> {

        OR comma(Expression exp, NullsFirstLast nullOption);

        OR comma(Expression exp, AscDesc ascDesc, NullsFirstLast nullOption);

    }

    /**
     * <p>
     * This interface representing ORDER BY clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _StaticOrderByClause<OR> extends Item {

        OR orderBy(Expression exp);

        OR orderBy(Expression exp, AscDesc ascDesc);

        OR orderBy(Expression exp1, Expression exp2);

        OR orderBy(Expression exp1, AscDesc ascDesc1, Expression exp2);

        OR orderBy(Expression exp1, Expression exp2, AscDesc ascDesc2);

        OR orderBy(Expression exp1, AscDesc ascDesc1, Expression exp2, AscDesc ascDesc2);

    }


    interface _DynamicOrderByClause<B extends SortItems, OR> {

        OR orderBy(Consumer<B> consumer);

        OR ifOrderBy(Consumer<B> consumer);
    }


    /**
     * <p>
     * This interface representing row count limit clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _RowCountLimitClause<LR> extends Item {

        LR limit(Expression rowCount);

        LR limit(BiFunction<MappingType, Number, Expression> operator, long rowCount);

        <N extends Number> LR limit(BiFunction<MappingType, Number, Expression> operator, Supplier<N> supplier);

        LR limit(BiFunction<MappingType, Number, Expression> operator, Function<String, ?> function, String keyName);

        <N extends Number> LR ifLimit(BiFunction<MappingType, Number, Expression> operator, Supplier<N> supplier);

        LR ifLimit(BiFunction<MappingType, Number, Expression> operator, Function<String, ?> function, String keyName);

        LR ifLimit(Supplier<Expression> supplier);

    }


    interface _DmlRowCountLimitClause<LR> extends _RowCountLimitClause<LR> {

        LR limit(BiFunction<MappingType, String, Expression> operator, String paramName);

        LR ifLimit(BiFunction<MappingType, String, Expression> operator, @Nullable String paramName);
    }

    interface _RowCountLimitAllClause<LR> extends _RowCountLimitClause<LR> {

        LR limitAll();

        LR ifLimitAll(BooleanSupplier supplier);

    }


    interface _QueryOffsetClause<R> {

        R offset(Expression start, Query.FetchRow row);


        /**
         * @param operator the method reference of below:
         *                 <ul>
         *                      <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                      <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                 </ul>
         * @param start    non-negative integer
         * @param row      {@link SQLs#ROW} or {@link SQLs#ROWS}
         */
        R offset(BiFunction<MappingType, Number, Expression> operator, long start, Query.FetchRow row);


        /**
         * @param operator the method reference of below:
         *                 <ul>
         *                      <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                      <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                 </ul>
         * @param supplier return non-negative integer
         * @param row      {@link SQLs#ROW} or {@link SQLs#ROWS}
         */
        <N extends Number> R offset(BiFunction<MappingType, Number, Expression> operator, Supplier<N> supplier
                , Query.FetchRow row);

        /**
         * @param operator the method reference of below:
         *                 <ul>
         *                      <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                      <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                 </ul>
         * @param function {@link Function#apply(Object)} return non-negative integer
         * @param keyName  keyName that is passed to function
         * @param row      {@link SQLs#ROW} or {@link SQLs#ROWS}
         */
        R offset(BiFunction<MappingType, Object, Expression> operator, Function<String, ?> function
                , String keyName, Query.FetchRow row);

        /**
         * @param operator the method reference of below:
         *                 <ul>
         *                      <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                      <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                 </ul>
         * @param start    non-negative integer
         * @param row      {@link SQLs#ROW} or {@link SQLs#ROWS}
         */
        R ifOffset(BiFunction<MappingType, Number, Expression> operator, @Nullable Number start, Query.FetchRow row);

        /**
         * @param operator the method reference of below:
         *                 <ul>
         *                      <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                      <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                 </ul>
         * @param supplier return non-negative integer
         * @param row      {@link SQLs#ROW} or {@link SQLs#ROWS}
         */
        <N extends Number> R ifOffset(BiFunction<MappingType, Number, Expression> operator, Supplier<N> supplier
                , Query.FetchRow row);

        /**
         * @param operator the method reference of below:
         *                 <ul>
         *                      <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                      <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                 </ul>
         * @param function {@link Function#apply(Object)} return non-negative integer
         * @param keyName  keyName that is passed to function
         * @param row      {@link SQLs#ROW} or {@link SQLs#ROWS}
         */
        R ifOffset(BiFunction<MappingType, Object, Expression> operator, Function<String, ?> function
                , String keyName, Query.FetchRow row);

    }

    interface _QueryFetchClause<R> {

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param count        non-negative
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        R fetch(Query.FetchFirstNext firstOrNext, Expression count, Query.FetchRow row
                , Query.FetchOnlyWithTies onlyWithTies);


        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                          <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                     </ul>
         * @param count        non-negative
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        R fetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , long count, Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies);

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                          <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                     </ul>
         * @param supplier     return non-negative integer
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        <N extends Number> R fetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , Supplier<N> supplier, Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies);

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                          <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                     </ul>
         * @param function     {@link Function#apply(Object)} return non-negative integer
         * @param keyName      keyName that is passed to function
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        R fetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Object, Expression> operator
                , Function<String, ?> function, String keyName, Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies);

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                          <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                     </ul>
         * @param count        non-negative
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        R ifFetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , @Nullable Number count, Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies);

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                          <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                     </ul>
         * @param supplier     return non-negative integer
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        <N extends Number> R ifFetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , Supplier<N> supplier, Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies);

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                          <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                     </ul>
         * @param function     {@link Function#apply(Object)} return non-negative integer
         * @param keyName      keyName that is passed to function
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        R ifFetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Object, Expression> operator
                , Function<String, ?> function, String keyName, Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies);
    }

    interface _LimitClause<LR> extends _RowCountLimitClause<LR> {

        LR limit(Expression offset, Expression rowCount);

        LR limit(BiFunction<MappingType, Number, Expression> operator, long offset, long rowCount);

        <N extends Number> LR limit(BiFunction<MappingType, Number, Expression> operator, Supplier<N> offsetSupplier, Supplier<N> rowCountSupplier);

        LR limit(BiFunction<MappingType, Object, Expression> operator, Function<String, ?> function, String offsetKey, String rowCountKey);

        LR limit(Consumer<BiConsumer<Expression, Expression>> consumer);


        <N extends Number> LR ifLimit(BiFunction<MappingType, Number, Expression> operator, Supplier<N> offsetSupplier, Supplier<N> rowCountSupplier);

        LR ifLimit(BiFunction<MappingType, Object, Expression> operator, Function<String, ?> function, String offsetKey, String rowCountKey);

        LR ifLimit(Consumer<BiConsumer<Expression, Expression>> consumer);

    }

    interface _FetchPercentClause<R> extends _QueryFetchClause<R> {

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param percent      the percentage of the total number of selected rows
         * @param wordPercent  {@link SQLs#PERCENT}
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        R fetch(Query.FetchFirstNext firstOrNext, Expression percent, SQLs.WordPercent wordPercent, Query.FetchRow row
                , Query.FetchOnlyWithTies onlyWithTies);


        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                          <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                     </ul>
         * @param percent      non-null,the percentage of the total number of selected rows
         * @param wordPercent  {@link SQLs#PERCENT}
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        R fetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , Number percent, SQLs.WordPercent wordPercent, Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies);

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                          <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                     </ul>
         * @param supplier     {@link  Supplier#get()} return non-null percent
         * @param wordPercent  {@link SQLs#PERCENT}
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        <N extends Number> R fetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , Supplier<N> supplier, SQLs.WordPercent wordPercent, Query.FetchRow row
                , Query.FetchOnlyWithTies onlyWithTies);

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                          <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                     </ul>
         * @param function     {@link Function#apply(Object)} return non-null percent
         * @param keyName      keyName that is passed to function
         * @param wordPercent  {@link SQLs#PERCENT}
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        R fetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Object, Expression> operator
                , Function<String, ?> function, String keyName, SQLs.WordPercent wordPercent
                , Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies);

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                          <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                     </ul>
         * @param percent      nullable,percent
         * @param wordPercent  {@link SQLs#PERCENT}
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        R ifFetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , @Nullable Number percent, SQLs.WordPercent wordPercent, Query.FetchRow row
                , Query.FetchOnlyWithTies onlyWithTies);

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                          <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                     </ul>
         * @param supplier     return nullable percent
         * @param wordPercent  {@link SQLs#PERCENT}
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        <N extends Number> R ifFetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , Supplier<N> supplier, SQLs.WordPercent wordPercent, Query.FetchRow row
                , Query.FetchOnlyWithTies onlyWithTies);

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                          <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                     </ul>
         * @param function     {@link Function#apply(Object)} return nullable percent
         * @param keyName      keyName that is passed to function
         * @param wordPercent  {@link SQLs#PERCENT}
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        R ifFetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Object, Expression> operator
                , Function<String, ?> function, String keyName, SQLs.WordPercent wordPercent
                , Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies);

    }


    interface _CommaStringDualSpec<PR> extends Statement._RightParenClause<PR> {

        Statement._RightParenClause<PR> comma(String string);

        _CommaStringDualSpec<PR> comma(String string1, String string2);
    }

    interface _CommaStringQuadraSpec<PR> extends Statement._RightParenClause<PR> {

        Statement._RightParenClause<PR> comma(String string);

        Statement._RightParenClause<PR> comma(String string1, String string2);

        Statement._RightParenClause<PR> comma(String string1, String string2, String string3);

        _CommaStringQuadraSpec<PR> comma(String string1, String string2, String string3, String string4);

    }

    interface _LeftParenStringDualClause<PR> extends Item {

        Statement._RightParenClause<PR> leftParen(String string);

        _CommaStringDualSpec<PR> leftParen(String string1, String string2);


    }

    interface _LeftParenStringDynamicClause<RR> extends Item {

        Statement._RightParenClause<RR> leftParen(Consumer<Consumer<String>> consumer);
    }

    interface _LeftParenStringDynamicOptionalClause<RR> extends Item {

        Statement._RightParenClause<RR> leftParenIf(Consumer<Consumer<String>> consumer);

    }


    interface _LeftParenStringDualSpec<RR>
            extends _LeftParenStringDualClause<RR>, _LeftParenStringDynamicClause<RR> {

    }

    interface _LeftParenStringDualOptionalSpec<RR> extends _LeftParenStringDualSpec<RR>
            , _LeftParenStringDynamicOptionalClause<RR> {

    }


    interface _LeftParenStringQuadraSpec<RR> extends _LeftParenStringDualSpec<RR> {

        _CommaStringQuadraSpec<RR> leftParen(String string1, String string2, String string3, String string4);

    }

    interface _LeftParenStringQuadraOptionalSpec<RR> extends _LeftParenStringQuadraSpec<RR>
            , _LeftParenStringDynamicOptionalClause<RR> {


    }

    interface _ParensStringClause<R> {

        R parens(String first, String... rest);

        R parens(Consumer<Consumer<String>> consumer);

        R ifParens(Consumer<Consumer<String>> consumer);
    }


    interface _ParensOnSpec<R> extends _ParensStringClause<_OnClause<R>>, _OnClause<R> {

    }

    interface _AsParensOnClause<R> extends _AsClause<_ParensOnSpec<R>> {

    }


    interface _DmlInsertClause<I extends Item> extends Item {

        I asInsert();

    }

    interface _DqlInsertClause<Q extends Item> {

        Q asReturningInsert();
    }


    interface _DmlUpdateSpec<I extends Item> {

        I asUpdate();
    }

    interface _DqlUpdateSpec<I extends Item> {

        I asReturningUpdate();
    }

    interface _DmlDeleteSpec<I extends Item> {

        I asDelete();
    }

    interface _DqlDeleteSpec<Q extends Item> {

        Q asReturningDelete();
    }

    interface _MultiStmtSpec extends Item {

        MultiStatement asMultiStmt();

    }

    interface _AsValuesClause<I extends Item> {

        I asValues();

    }


    /**
     * This is base interface of below:
     * <ul>
     *     <li>{@link UpdateStatement}</li>
     *     <li>{@link SubUpdate}</li>
     * </ul>
     *
     * @since 1.0
     */
    interface DmlUpdate {


    }

    /**
     * This is base interface of below:
     * <ul>
     *     <li>{@link DeleteStatement}</li>
     *     <li>{@link SubDelete}</li>
     * </ul>
     *
     * @since 1.0
     */
    interface DmlDelete {


    }

    interface _AsCommandClause<I extends Item> extends Item {

        I asCommand();
    }


    /**
     * This is base interface of below:
     * <ul>
     *     <li>{@link InsertStatement}</li>
     *     <li>{@link SubInsert}</li>
     * </ul>
     *
     * @since 1.0
     */
    interface DmlInsert extends Item {


    }

    interface DqlInsert extends Item {

    }


}
