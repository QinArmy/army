package io.army.criteria;

public interface ColumnSubQuery<E> extends SubQuery {

  interface ColumnSelectionSpec<E, C> extends QuerySQLSpec {

      FromSpec<ColumnSubQuery<E>, C> select(Distinct distinct, Selection selection);

      FromSpec<ColumnSubQuery<E>, C> select(Selection selection);

  }

}
