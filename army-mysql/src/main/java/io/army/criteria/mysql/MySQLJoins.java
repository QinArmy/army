package io.army.criteria.mysql;

import io.army.criteria.Statement;
import io.army.meta.TableMeta;

public interface MySQLJoins extends Statement.JoinBuilder,
        Statement._DynamicTabularDerivedModifierClause<
                MySQLQuery._DynamicIndexHintOnClause,
                Statement._AsClause<Statement._OnClause<MySQLQuery._DynamicJoinSpec>>>,
        Statement._DynamicTabularCteClause<Statement._OnClause<MySQLQuery._DynamicJoinSpec>>,
        MySQLStatement._MySQLDynamicNestedClause<Statement._OnClause<MySQLQuery._DynamicJoinSpec>> {

    MySQLQuery._DynamicPartitionOnClause tabular(TableMeta<?> table);

}
