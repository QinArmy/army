package io.army.criteria.mysql;

import io.army.criteria.Statement;
import io.army.meta.TableMeta;

public interface MySQLCrosses extends Statement.JoinBuilder,
        Statement._DynamicTabularDerivedModifierClause<
                MySQLQuery._DynamicIndexHintJoinClause,
                Statement._AsClause<MySQLQuery._DynamicJoinSpec>>,
        Statement._DynamicTabularCteClause<MySQLQuery._DynamicJoinSpec>,
        MySQLStatement._MySQLDynamicNestedClause<MySQLQuery._DynamicJoinSpec> {


    MySQLQuery._DynamicPartitionJoinClause tabular(TableMeta<?> table);


}
