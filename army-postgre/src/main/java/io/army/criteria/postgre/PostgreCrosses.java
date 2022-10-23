package io.army.criteria.postgre;

import io.army.criteria.Query;
import io.army.criteria.Statement;
import io.army.criteria.TabularItem;
import io.army.criteria.impl.SQLs;
import io.army.meta.TableMeta;

import java.util.function.Supplier;

public interface PostgreCrosses extends Statement.JoinBuilder {

    PostgreQuery._DynamicTableSampleJoinSpec tabular(TableMeta<?> table, SQLs.WordAs wordAs, String alias);


    <T extends TabularItem> Statement._AsClause<PostgreQuery._DynamicJoinSpec> tabular(Supplier<T> supplier);

    <T extends TabularItem> Statement._AsClause<PostgreQuery._DynamicJoinSpec> tabular(Query.TabularModifier modifier
            , Supplier<T> supplier);

    PostgreQuery._DynamicJoinSpec tabular(String cteName);

    PostgreQuery._DynamicJoinSpec tabular(String cteName, SQLs.WordAs wordAs, String alias);

    //TODO add dialect function tabular

}
