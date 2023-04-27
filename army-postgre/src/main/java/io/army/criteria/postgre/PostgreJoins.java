package io.army.criteria.postgre;

import io.army.criteria.Statement;

public interface PostgreJoins extends Statement.JoinBuilder,
        Statement._DynamicTabularModifierClause<
                PostgreStatement._DynamicTableSampleOnSpec,
                Statement._AsParensOnClause<PostgreStatement._DynamicJoinSpec>>,
        PostgreStatement._PostgreTabularSpaceUndoneFuncClause<Statement._OnClause<PostgreStatement._DynamicJoinSpec>>,
        Statement._DynamicTabularCteClause<Statement._OnClause<PostgreStatement._DynamicJoinSpec>>,
        PostgreStatement._PostgreDynamicNestedClause<Statement._OnClause<PostgreStatement._DynamicJoinSpec>> {


}
