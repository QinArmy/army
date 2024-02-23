package io.army.criteria.postgre;


import io.army.criteria.SubQuery;
import io.army.criteria.dialect.DmlCommand;

import java.util.function.BooleanSupplier;

/**
 * <p>This interface representing Postgre DECLARE cursor statement
 *
 * @see <a href="https://www.postgresql.org/docs/current/sql-declare.html">DECLARE — define a cursor</a>
 * @see <a href="https://www.postgresql.org/docs/current/sql-fetch.html">FETCH — retrieve rows from a query using a cursor</a>
 */
public interface PostgreCursor extends PostgreStatement {


    interface _ForQueryClause {

        PostgreQuery.WithSpec<_AsCommandClause<DmlCommand>> forSpace();

        _AsCommandClause<DmlCommand> forSpace(SubQuery query);

    }


    interface _HoldSpec extends _ForQueryClause {

        _ForQueryClause withHold();

        _ForQueryClause withoutHold();

        _ForQueryClause ifWithHold(BooleanSupplier supplier);

        _ForQueryClause ifWithoutHold(BooleanSupplier supplier);

    }

    interface _CursorClause {

        _HoldSpec cursor();

    }


    interface _ScrollSpec extends _CursorClause {

        _CursorClause scroll();

        _CursorClause noScroll();

        _CursorClause ifScroll(BooleanSupplier supplier);

        _CursorClause ifNoScroll(BooleanSupplier supplier);

    }


    interface _InsensitiveSpec extends _ScrollSpec {

        _ScrollSpec insensitive();

        _ScrollSpec asensitive();

        _ScrollSpec ifInsensitive(BooleanSupplier supplier);

        _ScrollSpec ifAsensitive(BooleanSupplier supplier);

    }


    interface _BinarySpec extends _InsensitiveSpec {

        _InsensitiveSpec binary();

    }


    interface _PostgreDeclareClause {

        _BinarySpec declare(String name);

    }


    /*-------------------below CLOSE cursor command interfaces-------------------*/


}
