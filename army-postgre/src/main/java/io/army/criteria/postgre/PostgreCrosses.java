package io.army.criteria.postgre;

import io.army.criteria.Statement;

public interface PostgreCrosses extends Statement.JoinBuilder,
        Statement._DynamicTabularModifierClause<
                PostgreStatement._DynamicTableSampleJoinSpec,
                Statement._AsClause<PostgreStatement._DynamicJoinSpec>>,
        PostgreStatement._PostgreTabularSpaceUndoneFuncClause<PostgreStatement._DynamicJoinSpec>,
        Statement._DynamicTabularCteClause<PostgreStatement._DynamicJoinSpec>,
        PostgreStatement._PostgreDynamicNestedClause<PostgreStatement._DynamicJoinSpec> {

}
