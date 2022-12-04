package io.army.criteria.postgre;

import io.army.criteria.DerivedTable;
import io.army.criteria.Query;
import io.army.criteria.Statement;
import io.army.criteria.impl.SQLs;
import io.army.meta.TableMeta;

import java.util.function.Supplier;

public interface PostgreJoins extends Statement.JoinBuilder {

    PostgreStatement._DynamicTableSampleOnSpec tabular(TableMeta<?> table, SQLs.WordAs wordAs, String alias);

    PostgreStatement._DynamicTableSampleOnSpec tabular(Query.TableModifier modifier, TableMeta<?> table,
                                                       SQLs.WordAs wordAs, String alias);

    <T extends DerivedTable> Statement._AsParensOnClause<PostgreStatement._DynamicJoinSpec> tabular(Supplier<T> supplier);

    <T extends DerivedTable> Statement._AsParensOnClause<PostgreStatement._DynamicJoinSpec> tabular(
            Query.DerivedModifier modifier, Supplier<T> supplier);

    Statement._OnClause<PostgreStatement._DynamicJoinSpec> tabular(String cteName);

    Statement._OnClause<PostgreStatement._DynamicJoinSpec> tabular(String cteName, SQLs.WordAs wordAs, String alias);


}
