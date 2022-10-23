package io.army.criteria.postgre;

import io.army.criteria.CteBuilderSpec;

public interface PostgreCteBuilder extends CteBuilderSpec {

    PostgreInsert._DynamicSubInsert singleInsert(String name);



}
