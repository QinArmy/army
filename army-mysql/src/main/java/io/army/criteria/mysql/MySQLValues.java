package io.army.criteria.mysql;

import io.army.criteria.DialectStatement;
import io.army.criteria.Expression;
import io.army.criteria.Statement;
import io.army.criteria.Values;
import io.army.lang.Nullable;

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
public interface MySQLValues extends Values, DialectStatement {


    interface _LimitSpec<C> extends Statement._RowCountLimitClause<C, Values._ValuesSpec>, Values._ValuesSpec {

    }


    interface _OrderBySpec<C> extends Statement._OrderByClause<C, _LimitSpec<C>>, _LimitSpec<C> {

    }

    interface _ValueRowCommaSpec<C, CR> extends Statement._RightParenClause<CR> {

        _ValueRowCommaSpec<C, CR> comma(@Nullable Object value);

        _ValueRowCommaSpec<C, CR> commaLiteral(@Nullable Object value);

        _ValueRowCommaSpec<C, CR> commaExp(Supplier<? extends Expression> supplier);

        _ValueRowCommaSpec<C, CR> commaExp(Function<C, ? extends Expression> function);

    }

    interface _ValueRowClause<C, CR> {

        _ValueRowCommaSpec<C, CR> row(@Nullable Object value);

        _ValueRowCommaSpec<C, CR> rowLiteral(@Nullable Object value);

        _ValueRowCommaSpec<C, CR> rowExp(Supplier<? extends Expression> supplier);

        _ValueRowCommaSpec<C, CR> rowExp(Function<C, ? extends Expression> function);

    }


    interface _ValueRowSpec<C> extends _ValueRowClause<C, _ValueRowSpec<C>>, _OrderBySpec<C> {

    }

    interface _ValuesStmtValues<C> {

        _ValueRowClause<C, _ValueRowSpec<C>> values();

    }


}
