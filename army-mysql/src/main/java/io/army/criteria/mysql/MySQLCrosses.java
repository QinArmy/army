package io.army.criteria.mysql;

import io.army.criteria.Query;
import io.army.criteria.Statement;
import io.army.criteria.TabularItem;
import io.army.criteria.impl.SQLs;
import io.army.meta.TableMeta;

import java.util.function.Supplier;

public interface MySQLCrosses extends Statement.JoinBuilder {

    MySQLQuery._DynamicIndexHintJoinClause tabular(TableMeta<?> table, SQLs.WordAs wordAs, String alias);

    MySQLQuery._DynamicPartitionJoinClause tabular(TableMeta<?> table);

    <T extends TabularItem> Statement._AsClause<MySQLQuery._DynamicJoinSpec> tabular(Supplier<T> supplier);

    <T extends TabularItem> Statement._AsClause<MySQLQuery._DynamicJoinSpec> tabular(Query.DerivedModifier modifier
            , Supplier<T> supplier);

    MySQLQuery._DynamicJoinSpec tabular(String cteName);

    MySQLQuery._DynamicJoinSpec tabular(String cteName, SQLs.WordAs wordAs, String alias);


}
