package io.army.criteria.mysql;

import io.army.criteria.Statement;
import io.army.criteria.TableItemGroup;

/**
 * <p>
 * This interface representing MySQL nested join.
 * </p>
 *
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/nested-join-optimization.html">Nested Join Optimization</a>
 * @since 1.0
 */
public interface NestedJoin extends TableItemGroup {


    interface UpdatePartitionSpec<C> extends MySQLQuery.PartitionClause<C, Statement.AsClause<UpdateIndexHintSpec<C>>> {

    }

    interface UpdateJoinSpec<C>
            extends MySQLQuery.MySQLJoinClause<C, UpdateIndexHintSpec<C>, UpdateOnSpec<C>, UpdatePartitionSpec<C>>
            , TableItemGroup.TableItemGroupSpec {

    }


    interface UpdateOnSpec<C> extends Statement.OnClause<C, UpdateJoinSpec<C>>, UpdateJoinSpec<C> {

    }

    interface UpdateIndexHintSpec<C> extends MySQLQuery.IndexHintClause<C, UpdateIndexSpec<C>, UpdateOnSpec<C>> {

    }

    interface UpdateIndexSpec<C> extends MySQLQuery.IndexJoinClause<C, UpdateJoinSpec<C>> {

    }


}
