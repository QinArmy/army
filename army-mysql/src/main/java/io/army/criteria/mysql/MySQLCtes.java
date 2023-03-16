package io.army.criteria.mysql;

import io.army.criteria.CteBuilderSpec;

public interface MySQLCtes extends CteBuilderSpec {


    MySQLQuery._DynamicCteParensSpec subQuery(String name);


}
