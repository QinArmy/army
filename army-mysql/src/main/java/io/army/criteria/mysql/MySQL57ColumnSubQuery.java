package io.army.criteria.mysql;

import io.army.criteria.ColumnSubQuery;
import io.army.criteria.Distinct;
import io.army.criteria.Selection;

public interface MySQL57ColumnSubQuery<E> extends ColumnSubQuery<E>, MySQL57SubQuery {

    interface MySQLColumnSelectionSpec<E, C> extends QuerySQLSpec {

        MySQLFromSpec<MySQL57ColumnSubQuery<E>, C> select(Distinct distinct, Selection selection);

        MySQLFromSpec<MySQL57ColumnSubQuery<E>, C> select(Selection selection);

    }

}
