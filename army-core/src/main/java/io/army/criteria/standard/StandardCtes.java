package io.army.criteria.standard;

import io.army.criteria.CteBuilderSpec;

public interface StandardCtes extends CteBuilderSpec {

    StandardQuery._DynamicCteParensSpec subQuery(String name);

}
