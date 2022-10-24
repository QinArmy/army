package io.army.criteria.postgre;

import io.army.criteria.CteBuilderSpec;

public interface PostgreCteBuilder extends CteBuilderSpec {

   PostgreInsert._DynamicSubInsertSpec singleInsert(String name);

   PostgreUpdate._DynamicCteUpdateSpec singleUpdate(String name);

   PostgreDelete._DynamicCteDeleteSpec singleDelete(String name);

   PostgreQuery._DynamicCteQuerySpec query(String name);

   PostgreValues._DynamicCteValuesSpec cteValues(String name);


}
