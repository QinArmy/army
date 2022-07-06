package io.army.criteria.mysql;

import io.army.criteria.*;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This interface representing MySQL Values statement,this interface is base interface of below:
 *     <ul>
 *         <li>MySQL {@link  io.army.criteria.Values}</li>
 *         <li>MySQL {@link io.army.criteria.SubValues}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface MySQLDqlValues extends DialectStatement, RowSet.DqlValues {


    interface _UnionSpec<C, U extends DqlValues>
            extends Query._UnionClause<C, _UnionOrderBySpec<C, U>>, Values._ValuesSpec<U> {

    }

    interface _UnionLimitSpec<C, U extends DqlValues> extends Statement._RowCountLimitClause<C, _UnionSpec<C, U>>
            , _UnionSpec<C, U> {

    }

    interface _UnionOrderBySpec<C, U extends DqlValues> extends Statement._OrderByClause<C, _UnionLimitSpec<C, U>>
            , _UnionLimitSpec<C, U> {

    }


    interface _LimitSpec<C, U extends DqlValues>
            extends Statement._RowCountLimitClause<C, _UnionSpec<C, U>>, _UnionSpec<C, U> {
    }


    interface _OrderBySpec<C, U extends DqlValues>
            extends Statement._OrderByClause<C, _LimitSpec<C, U>>, _LimitSpec<C, U> {

    }

    interface _ValueRowCommaSpec<C, U extends DqlValues>
            extends Statement._RightParenClause<_StaticValueRowSpec<C, U>> {

        _ValueRowCommaSpec<C, U> comma(Object value);

        _ValueRowCommaSpec<C, U> commaLiteral(Object value);

        _ValueRowCommaSpec<C, U> commaExp(Supplier<? extends Expression> supplier);

        _ValueRowCommaSpec<C, U> commaExp(Function<C, ? extends Expression> function);

    }

    interface _StaticValueRowClause<C, U extends DqlValues> {

        _ValueRowCommaSpec<C, U> row(Object value);

        _ValueRowCommaSpec<C, U> rowLiteral(Object value);

        _ValueRowCommaSpec<C, U> rowExp(Supplier<? extends Expression> supplier);

        _ValueRowCommaSpec<C, U> rowExp(Function<C, ? extends Expression> function);

    }


    interface _StaticValueRowSpec<C, U extends DqlValues>
            extends _StaticValueRowClause<C, U>, _OrderBySpec<C, U> {

    }


    interface _ValuesStmtValuesClause<C, U extends DqlValues> {

        _StaticValueRowClause<C, U> values();

        _OrderBySpec<C, U> values(Consumer<RowConstructor> consumer);

        _OrderBySpec<C, U> values(BiConsumer<C, RowConstructor> consumer);

    }


}
