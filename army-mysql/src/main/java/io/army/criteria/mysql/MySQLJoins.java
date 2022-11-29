package io.army.criteria.mysql;

import io.army.criteria.Query;
import io.army.criteria.Statement;
import io.army.criteria.TabularItem;
import io.army.criteria.impl.SQLs;
import io.army.meta.TableMeta;

import java.util.function.Supplier;

public interface MySQLJoins extends Statement.JoinBuilder {

    MySQLQuery._DynamicIndexHintOnClause tabular(TableMeta<?> table, SQLs.WordAs wordAs, String alias);

    MySQLQuery._DynamicPartitionOnClause tabular(TableMeta<?> table);

    <T extends TabularItem> Statement._AsClause<Statement._OnClause<MySQLQuery._DynamicJoinSpec>> tabular(Supplier<T> supplier);

    <T extends TabularItem> Statement._AsClause<Statement._OnClause<MySQLQuery._DynamicJoinSpec>> tabular(Query.DerivedModifier modifier
            , Supplier<T> supplier);

    Statement._OnClause<MySQLQuery._DynamicJoinSpec> tabular(String cteName);

    Statement._OnClause<MySQLQuery._DynamicJoinSpec> tabular(String cteName, SQLs.WordAs wordAs, String alias);

}
