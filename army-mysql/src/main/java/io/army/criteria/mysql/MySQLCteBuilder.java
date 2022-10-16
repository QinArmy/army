package io.army.criteria.mysql;

import io.army.criteria.CteBuilderSpec;

public interface MySQLCteBuilder extends CteBuilderSpec {


    MySQLQuery._DynamicCteWithSpec query(String cteName);


}
