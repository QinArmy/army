package io.army.criteria.mysql;

import io.army.criteria.Hint;
import io.army.criteria.Insert;
import io.army.domain.IDomain;
import io.army.meta.ChildTableMeta;
import io.army.meta.SingleTableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/insert.html">INSERT Statement</a>
 */
public interface MySQLInsert extends Insert {


    interface _InsertClause<C, IR> {

        IR insert(Function<C, List<Hint>> hints, List<MySQLWords> modifiers);

        IR insert(Supplier<List<Hint>> hints, List<MySQLWords> modifiers);
    }

    interface _ValueIntoClause<C, IR> {

        <T extends IDomain> _ValuePartitionSpec<C, T, IR> into(SingleTableMeta<T> table);

        <T extends IDomain> _ValuePartitionSpec<C, T, IR> into(ChildTableMeta<T> table);

    }


    interface _ValuePartitionSpec<C, T extends IDomain, CR>
            extends MySQLQuery._PartitionClause<C, _ComplexColumnListClause<C, T, CR>>, _ComplexColumnListClause<C, T, CR> {

    }


}
