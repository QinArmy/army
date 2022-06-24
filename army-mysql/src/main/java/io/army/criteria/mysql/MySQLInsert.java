package io.army.criteria.mysql;

import io.army.criteria.Hint;
import io.army.criteria.Insert;
import io.army.domain.IDomain;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/insert.html">INSERT Statement</a>
 */
public interface MySQLInsert extends Insert {


    interface _MySQLInsertClause<C, T extends IDomain, IR> {

        _IntoClause<C, T, IR> insert(Function<C, List<Hint>> hints, List<MySQLWords> modifiers);

        _IntoClause<C, T, IR> insert(Supplier<List<Hint>> hints, List<MySQLWords> modifiers);

    }


}
