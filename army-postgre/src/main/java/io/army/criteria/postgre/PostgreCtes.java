package io.army.criteria.postgre;

import io.army.criteria.CteBuilderSpec;

public interface PostgreCtes extends CteBuilderSpec {

   PostgreInsert._DynamicCteParensSpec singleInsert(String name);

   PostgreUpdate._DynamicCteUpdateSpec singleUpdate(String name);

   PostgreDelete._DynamicCteDeleteSpec singleDelete(String name);

   PostgreQuery._DynamicCteQuerySpec subQuery(String name);

   PostgreValues._DynamicCteValuesSpec cteValues(String name);


}
