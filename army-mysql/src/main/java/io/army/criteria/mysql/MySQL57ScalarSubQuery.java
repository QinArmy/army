package io.army.criteria.mysql;

import io.army.criteria.Distinct;
import io.army.criteria.ScalarSubQuery;
import io.army.criteria.Selection;

public interface MySQL57ScalarSubQuery<E> extends ScalarSubQuery<E>, MySQL57ColumnSubQuery<E>, MySQL57RowSubQuery {


    interface MySQLScalarSelectionSpec<E, C> extends QuerySQLSpec {

        MySQLFromSpec<MySQL57ScalarSubQuery<E>, C> select(Distinct distinct, Selection selection);

        MySQLFromSpec<MySQL57ScalarSubQuery<E>, C> select(Selection selection);

    }

}
