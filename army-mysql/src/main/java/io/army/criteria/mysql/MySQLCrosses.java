package io.army.criteria.mysql;

import io.army.criteria.Query;
import io.army.criteria.Statement;
import io.army.criteria.TabularItem;
import io.army.criteria.impl.SQLs;
import io.army.meta.TableMeta;

import java.util.function.Supplier;

public interface MySQLCrosses extends Statement.JoinBuilder {

    void tabular(TableMeta<?> table, SQLs.WordAs wordAs, String alias);

    <T extends TabularItem> Statement._AsClause<T> tabular(Supplier<T> supplier);

    <T extends TabularItem> Statement._AsClause<T> tabular(Query.TabularModifier modifier, Supplier<T> supplier);


}
